using SnapMemo.src.logic;
using SnapMemo.src.model;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.Storage.Pickers;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace SnapMemo.src.ui
{
    class MemoBlock : Button
    {
        public Memo Memo
        {
            get; private set;
        }
        public bool Selected { get; set; }

        public MemoBlock(Memo memo)
        {
            // TODO presentation part
            this.Margin = new Thickness(10, 10, 10, 10);
            this.Background = new SolidColorBrush(Colors.Gray);

            this.Memo = memo;
            this.Content = memo.ToString();
            this.Click += ClickToModify;
        }

        public void ClickToModify(object sender, RoutedEventArgs e)
        {
            Frame frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MemoModifyPage), Memo);
        }

        public void ClickToSelect(object sender, RoutedEventArgs e)
        {
            var gray = new SolidColorBrush(Colors.Gray);
            var blue = new SolidColorBrush(Colors.CornflowerBlue);

            if (Selected)
            {
                Selected = false;
                Background = gray;
            }
            else
            {
                Selected = true;
                Background = blue;
            }
        }
    }

    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MemoListPage : Page
    {
        private static bool debugWithoutNet = false;

        public MemoListPage()
        {
            this.InitializeComponent();
            LoadMemos();

            // set buttons in title
            MainPage.Instance.CameraButton.Visibility = Visibility.Visible;
            MainPage.Instance.PlusButton.Visibility = Visibility.Visible;
            MainPage.Instance.DeleteButton.Visibility = Visibility.Collapsed;

            MainPage.Instance.CameraButton.Click += OnSnap;
            MainPage.Instance.PlusButton.Click += OnAdd;
            MainPage.Instance.DeleteButton.Click += OnDelete;

            // set title
            MainPage.Instance.Title = "Memos";
        }

        private async void LoadMemos()
        {
            // from Local DB
            // List<Memo> memos = DBHelper.GetAllMemo();

            // from server-end
            ICollection<Memo> memos = await NetHelper.GetAllMemos(Preference.GetUserID());
            foreach (var memo in memos)
            {
                var memoBlock = new MemoBlock(memo);
                memoBlock.Holding += OnChoose;
                memoBlock.RightTapped += OnChoose;
                memoList.Children.Add(memoBlock);
            }
        }

        private void OnAdd(object sender, RoutedEventArgs e)
        {
            Frame frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MemoModifyPage));
        }

        private void OnChoose(object sender, RoutedEventArgs e)
        {
            var gray = new SolidColorBrush(Colors.Gray);
            var blue = new SolidColorBrush(Colors.CornflowerBlue);
            var memos = memoList.Children.ToList();
            foreach (var one in memos)
            {
                MemoBlock memoBlock = one as MemoBlock;

                memoBlock.Background = gray;
                memoBlock.Selected = false;
                memoBlock.Click -= memoBlock.ClickToModify;
                memoBlock.Click += memoBlock.ClickToSelect;
                memoBlock.Holding -= OnChoose;
                memoBlock.RightTapped -= OnChoose;
            }

            // change the state of the first selected one
            var firstMemo = sender as MemoBlock;
            firstMemo.Background = blue;
            firstMemo.Selected = true;

            // change view of cancel Button
            var cancelBtn = MainPage.Instance.CancelButton;
            cancelBtn.Visibility = Visibility.Visible;
            cancelBtn.Click += OnCancelChoose;

            // hide add and camera, show delete
            MainPage.Instance.PlusButton.Visibility = Visibility.Collapsed;
            MainPage.Instance.CameraButton.Visibility = Visibility.Collapsed;
            MainPage.Instance.DeleteButton.Visibility = Visibility.Visible;
        }

        private void OnCancelChoose(object sender, RoutedEventArgs e)
        {
            // change states of memoBlocks
            var gray = new SolidColorBrush(Colors.Gray);
            var memos = memoList.Children.ToList();
            foreach (var one in memos)
            {
                MemoBlock memoBlock = one as MemoBlock;

                memoBlock.Background = gray;
                memoBlock.Selected = false;
                memoBlock.Click += memoBlock.ClickToModify;
                memoBlock.Click -= memoBlock.ClickToSelect;
                memoBlock.Holding += OnChoose;
                memoBlock.RightTapped += OnChoose;
            }

            // hide cancel Button
            var cancelBtn = MainPage.Instance.CancelButton;
            cancelBtn.Visibility = Visibility.Collapsed;
            cancelBtn.Click -= OnCancelChoose;

            // show add and camera, hide delete
            MainPage.Instance.PlusButton.Visibility = Visibility.Visible;
            MainPage.Instance.CameraButton.Visibility = Visibility.Visible;
            MainPage.Instance.DeleteButton.Visibility = Visibility.Collapsed; 
        }

        private async void OnDelete(object sender, RoutedEventArgs e)
        {
            var memos = memoList.Children.ToList();
            memoList.Children.Clear();
            foreach (var one in memos)
            {
                MemoBlock memoBlock = one as MemoBlock;
                if (!memoBlock.Selected)
                {
                    memoList.Children.Add(memoBlock);
                }
                else
                {
                    if (!debugWithoutNet)
                    {
                        await NetHelper.DeleteMemo(memoBlock.Memo.MemoID);
                    }
                    DBHelper.DeleteMemo(memoBlock.Memo);
                    NotificationHelper.RemoveToastFromSchedule(memoBlock.Memo);
                }
            }

            OnCancelChoose(null, e);
        }

        private async void OnSnap(object sender, RoutedEventArgs e)
        {
            FileOpenPicker fileOpenPicker = new FileOpenPicker();
            fileOpenPicker.SuggestedStartLocation = PickerLocationId.PicturesLibrary;
            fileOpenPicker.FileTypeFilter.Add(".jpg");
            fileOpenPicker.FileTypeFilter.Add(".png");
            fileOpenPicker.ViewMode = PickerViewMode.Thumbnail;

            var inputFile = await fileOpenPicker.PickSingleFileAsync();

            if (inputFile == null)
            {
                // The user cancelled the picking operation
                return;
            }
            else
            {
                Frame frame = Window.Current.Content as Frame;
                frame.Navigate(typeof(PictureChoosePage), await inputFile.OpenAsync(FileAccessMode.Read));
            }
        }
    }
}

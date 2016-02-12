using SnapMemo.src.logic;
using SnapMemo.src.model;
using SnapMemo.src.ui;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage.Pickers;
using Windows.UI;
using Windows.UI.Notifications;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace SnapMemo
{
    class MemoBlock : Button
    {
        public Memo Memo
        {
            get;  private set;
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
            Frame root = Window.Current.Content as Frame;
            root.Navigate(typeof(MemoModifyPage), Memo);
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
    public sealed partial class MainPage : Page
    {
        private bool isSelectMode = false;

        public MainPage()
        {
            this.InitializeComponent();
            LoadMemos();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            if(e.Parameter == null)
            {
                return;
            }

            var pType = e.Parameter.GetType();
        }

        private void LoadMemos()
        {
            List<Memo> memos = DBHelper.GetAllMemo();
            foreach(var memo in memos)
            {
                memoList.Children.Add(new MemoBlock(memo));
            }
        }

        private void OnAdd(object sender, RoutedEventArgs e)
        {
            Frame root = Window.Current.Content as Frame;
            root.Navigate(typeof(MemoModifyPage));
        }

        private void OnChoose(object sender, RoutedEventArgs e)
        {
            var gray = new SolidColorBrush(Colors.Gray);
            var memos = memoList.Children.ToList();
            foreach(var one in memos)
            {
                MemoBlock memoBlock = one as MemoBlock;
                if (isSelectMode)
                {
                    memoBlock.Background = gray;
                    memoBlock.Selected = false;
                    memoBlock.Click += memoBlock.ClickToModify;
                    memoBlock.Click -= memoBlock.ClickToSelect;
                }
                else
                {
                    memoBlock.Click -= memoBlock.ClickToModify;
                    memoBlock.Click += memoBlock.ClickToSelect;
                }
            }
            isSelectMode = !isSelectMode;
        }

        private void OnDelete(object sender, RoutedEventArgs e)
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
                    DBHelper.DeleteMemo(memoBlock.Memo);
                    NotificationHelper.RemoveFromSchedule(memoBlock.Memo);
                }
            }
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
                Frame root = Window.Current.Content as Frame;
                root.Navigate(typeof(PictureChoosePage), inputFile);
            }
        }
    }
}

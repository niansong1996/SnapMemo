﻿using SnapMemo.src.logic;
using SnapMemo.src.model;
using SnapMemo.src.model.Operation;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.Storage.Pickers;
using Windows.UI;
using Windows.UI.Popups;
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
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MemoListPage : Page
    {
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
            IList<Memo> memos = new List<Memo>();

            try
            {
                // from server-end
                var temp = await NetHelper.GetAllMemos(Preference.GetUserID());
                memos = MemoSort.SortByTime(temp);
            }
            catch(Exception e)
            {
                Debug.WriteLine(e.Message);
                // from local DB
                var temp = DBHelper.GetAllMemo();
                memos = MemoSort.SortByTime(temp);
            }

            foreach (var memo in memos)
            {
                var memoBlock = new MemoView(memo);
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
            var memos = memoList.Children.ToList();
            foreach (var one in memos)
            {
                MemoView memoBlock = one as MemoView;

                memoBlock.Selected = false;
                memoBlock.Tapped -= memoBlock.ClickToModify;
                memoBlock.Tapped += memoBlock.ClickToSelect;
                memoBlock.Holding -= OnChoose;
                memoBlock.RightTapped -= OnChoose;
            }

            // change the state of the first selected one
            var firstMemo = sender as MemoView;
            firstMemo.ClickToSelect(firstMemo, e);

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
            var memos = memoList.Children.ToList();
            foreach (var one in memos)
            {
                MemoView memoBlock = one as MemoView;

                if (memoBlock.Selected)
                {
                    memoBlock.ClickToSelect(memoBlock, e);
                }
                memoBlock.Tapped += memoBlock.ClickToModify;
                memoBlock.Tapped -= memoBlock.ClickToSelect;
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

        private void OnDelete(object sender, RoutedEventArgs e)
        {
            var memos = memoList.Children.ToList();
            memoList.Children.Clear();
            foreach (var one in memos)
            {
                MemoView memoBlock = one as MemoView;
                if (!memoBlock.Selected)
                {
                    memoList.Children.Add(memoBlock);
                }
                else
                {
                    DBHelper.DeleteMemo(memoBlock.Memo);
                    NotificationHelper.RemoveToastFromSchedule(memoBlock.Memo);
                    NotificationHelper.RemoveTileNotifications();
                    UnsyncQueue.Instance.Enqueue(new DeleteMemoOperation(memoBlock.Memo.MemoID));
                }
            }

            OnCancelChoose(null, e);
        }

        private async void OnSnap(object sender, RoutedEventArgs e)
        {
            try
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
                    Windows.Storage.AccessCache.StorageApplicationPermissions.FutureAccessList.Add(inputFile);
                    Frame frame = Window.Current.Content as Frame;
                    frame.Navigate(typeof(PictureChoosePage), await inputFile.OpenAsync(FileAccessMode.Read));
                }
            }
            catch (Exception exception)
            {
                Debug.WriteLine(exception.Message);

                var msgDialog = new MessageDialog(exception.Message) { Title = "Unkown Error" };
                msgDialog.Commands.Add(new Windows.UI.Popups.UICommand("OK", uiCommand => {
                    Frame frame = Window.Current.Content as Frame;
                    frame.Navigate(typeof(MainPage));
                }));
                msgDialog.Commands.Add(new Windows.UI.Popups.UICommand("Cancel", uiCommand => { }));
                await msgDialog.ShowAsync();

                return;
            }
        }
    }
}

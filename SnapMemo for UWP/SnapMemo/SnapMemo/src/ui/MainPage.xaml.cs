using SnapMemo.src.logic;
using SnapMemo.src.model;
using SnapMemo.src.ui;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Data.Json;
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
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public static MainPage Instance {
            get; private set;
        }

        public string Title
        {
            get { return titleTB.Text; }
            set { titleTB.Text = value; }
        }

        public Button PlusButton
        {
            get { return plusButton; }
            private set { }
        }

        public Button CameraButton
        {
            get { return cameraButton; }
            private set { }
        }

        public Button DeleteButton
        {
            get { return deleteButton; }
            private set { }
        }

        public Button CancelButton
        {
            get { return cancelButton; }
            private set { }
        }

        public Button HamburgerButton
        {
            get { return hamburgerButton; }
            private set { }
        }

        public Frame ContentFrame
        {
            get; private set;
        }

        public MainPage()
        {
            this.InitializeComponent();
            Instance = this;
            ContentFrame = myFrame;
            myFrame.Navigate(typeof(MemoListPage));
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

        private void hamburgerButton_Click(object sender, RoutedEventArgs e)
        {
            mySplitView.IsPaneOpen = !mySplitView.IsPaneOpen;
        }

        private void pagesListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (memoListBoxItem.IsSelected)
            {
                myFrame.Navigate(typeof(MemoListPage));
                mySplitView.IsPaneOpen = false;
            }
            else if (accountBoxItem.IsSelected)
            {
                if(Preference.GetUserID() == Preference.DefaultID)
                {
                    myFrame.Navigate(typeof(LogInPage));
                }
                else
                {
                    myFrame.Navigate(typeof(AccountPage));
                }
                
                mySplitView.IsPaneOpen = false;
            }
        }

        private void plusTB_Tapped(object sender, TappedRoutedEventArgs e)
        {

        }
    }
}

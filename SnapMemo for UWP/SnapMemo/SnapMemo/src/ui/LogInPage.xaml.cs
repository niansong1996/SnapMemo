﻿using SnapMemo.src.logic;
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
    public sealed partial class LogInPage : Page
    {
        public LogInPage()
        {
            this.InitializeComponent();

            // set plus button and camera button in title
            MainPage.Instance.CameraButton.Visibility = Visibility.Collapsed;
            MainPage.Instance.PlusButton.Visibility = Visibility.Collapsed;
            MainPage.Instance.DeleteButton.Visibility = Visibility.Collapsed;

            // set title
            MainPage.Instance.Title = "Account";
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            StorageFolder appInstalledFolder = Windows.ApplicationModel.Package.Current.InstalledLocation;
            StorageFolder assets = await appInstalledFolder.GetFolderAsync("Assets");
            var file = await assets.GetFileAsync("SnapMemo_square.png");
            imgView.Source = await PictureConvert.FromStream(await file.OpenAsync(FileAccessMode.Read));
        }

        private async void OnSignIn(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("send Login : " + nameTB.Text + passwordTB.Password);

            try
            {
                string userID = JsonString.DeQuotes(
                await NetHelper.Login(nameTB.Text, passwordTB.Password));

                // debug
                Debug.WriteLine("Log in successfully");
                Debug.WriteLine("userID: " + userID);

                Preference.SetUserID(userID);
                Preference.SetUserName(nameTB.Text);

                var frame = MainPage.Instance.ContentFrame;
                frame.Navigate(typeof(AccountPage));
            }
            catch (COMException)
            {
                warnTB.Visibility = Visibility.Visible;
                warnTB.Text = "网络异常";
            }
            catch(NullReferenceException)
            {
                warnTB.Visibility = Visibility.Visible;
                warnTB.Text = "无效的用户名或密码";
            }
        }

        private void OnSignUp(object sender, RoutedEventArgs e)
        {
            MainPage.Instance.ContentFrame.Navigate(typeof(SignUpPage));
        }
    }
}

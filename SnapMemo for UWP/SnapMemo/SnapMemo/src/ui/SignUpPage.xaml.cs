using SnapMemo.src.logic;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
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
    public sealed partial class SignUpPage : Page
    {
        public SignUpPage()
        {
            this.InitializeComponent();
        }

        private async void OnSignUp(object sender, RoutedEventArgs e)
        {
            if(passwordTB.Password != confirmTB.Password)
            {
                Debug.WriteLine("password differs!");
                warnTB.Text = "password differs!";
                return;
            }

            Debug.WriteLine("send Sign up : " + nameTB.Text + passwordTB.Password);

            string userID = JsonString.DeQuotes(
                await NetHelper.SignUp(nameTB.Text, passwordTB.Password));

            Debug.WriteLine("Sign up successfully");
            Debug.WriteLine("userID: " + userID);

            Preference.SetUserID(userID);

            var frame = MainPage.Instance.ContentFrame;
            frame.Navigate(typeof(AccountPage));
        }

        private void OnLogin(object sender, RoutedEventArgs e)
        {
            MainPage.Instance.ContentFrame.Navigate(typeof(LogInPage));
        }
    }
}

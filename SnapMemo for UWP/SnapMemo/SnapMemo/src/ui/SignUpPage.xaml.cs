using SnapMemo.src.logic;
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
            if(passwordTB.Password.Length < 4)
            {
                Debug.WriteLine("密码长度过短，至少4位");
                warnTB.Text = "密码长度过短，至少4位";
                return;
            }

            if(passwordTB.Password != confirmTB.Password)
            {
                Debug.WriteLine("两次输入密码不一致!");
                warnTB.Text = "两次输入密码不一致!";
                return;
            }

            try
            {
                Debug.WriteLine("send Sign up : " + nameTB.Text + passwordTB.Password);

                string userID = JsonString.DeQuotes(
                    await NetHelper.SignUp(nameTB.Text, passwordTB.Password));

                Debug.WriteLine("Sign up successfully");
                Debug.WriteLine("userID: " + userID);

                Preference.SetUserID(userID);
                Preference.SetUserName(nameTB.Text);

                var frame = MainPage.Instance.ContentFrame;
                frame.Navigate(typeof(AccountPage));
            }
            catch (COMException)
            {
                Debug.WriteLine("网络错误");
                warnTB.Text = "网络错误";
            }
        }

        private void OnLogin(object sender, RoutedEventArgs e)
        {
            MainPage.Instance.ContentFrame.Navigate(typeof(LogInPage));
        }
    }
}

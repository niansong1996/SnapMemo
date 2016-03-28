using SnapMemo.src.logic;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Graphics.Imaging;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace SnapMemo.src.ui
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class AccountPage : Page
    {
        private async Task WriteToFile(IRandomAccessStream memStream)
        {
            var myPictures = await StorageLibrary.GetLibraryAsync(KnownLibraryId.Pictures);
            var wFile = await myPictures.Folders[0]
                .CreateFileAsync("testYao.jpg", CreationCollisionOption.ReplaceExisting);

            var wStream = await wFile.OpenAsync(FileAccessMode.ReadWrite);
            using (var outputStream = wStream.GetOutputStreamAt(0))
            {
                memStream.Seek(0);
                await RandomAccessStream.CopyAsync(memStream, outputStream);
            }
            wStream.Dispose();
        }

        private async void loadProfilePicture()
        {
            // I don't know why the stream api is so confusing...
            using(var picStream = await NetHelper.GetProfilePicture(userIDTB.Text))
            {
                await WriteToFile(picStream);

                BitmapImage bitmap = new BitmapImage();
                bitmap.SetSource(picStream);

                imageView.Source = bitmap;
            }
        }

        public AccountPage()
        {
            this.InitializeComponent();

            userIDTB.Text = Preference.GetUserID();
            userNameTB.Text = Preference.GetUserName();
            signatureTB.Text = Preference.GetSignature();

            loadProfilePicture();
        }

        private void OnLogout(object sender, RoutedEventArgs e)
        {
            Preference.SetUserName(Preference.DefaultName);
            Preference.SetUserID(Preference.DefaultID);
            Preference.SetSignature(Preference.DefaultSignature);

            var frame = MainPage.Instance.ContentFrame;
            frame.Navigate(typeof(LogInPage));
        }

        private void OnEdit(object sender, RoutedEventArgs e)
        {
            // TODO
            Debug.WriteLine("click edit");
        }

        private void OnTextChanged(object sender, TextChangedEventArgs e)
        {
            Debug.WriteLine("text change");
            editButton.IsEnabled = true;
        }

        private void OnEnabledChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            Debug.WriteLine("enable change");
            if (editButton.IsEnabled)
            {
                var green = new SolidColorBrush(Colors.LightGreen);
                editButton.Background = green;
            }
            else
            {
                var gray = new SolidColorBrush(Colors.Gray);
                editButton.Background = gray;
            }
        }
    }
}

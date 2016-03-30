using SnapMemo.src.logic;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
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
        private static readonly string cacheFileName = "SnapMemoProfile.txt";

        private async Task WriteToFile(IRandomAccessStream memStream)
        {
            var myPictures = await StorageLibrary.GetLibraryAsync(KnownLibraryId.Pictures);
            var wFile = await myPictures.Folders[0]
                .CreateFileAsync(cacheFileName, CreationCollisionOption.ReplaceExisting);

            var wStream = await wFile.OpenAsync(FileAccessMode.ReadWrite);
            using (var outputStream = wStream.GetOutputStreamAt(0))
            {
                memStream.Seek(0);
                await RandomAccessStream.CopyAsync(memStream, outputStream);
            }
            wStream.Dispose();
        }

        private async Task<IRandomAccessStream> ReadFromFile()
        {
            var myPictures = await StorageLibrary.GetLibraryAsync(KnownLibraryId.Pictures);
            var rFile = await myPictures.Folders[0].GetFileAsync(cacheFileName);

            var stream = await rFile.OpenAsync(FileAccessMode.Read);
            return stream;
        }

        private async void loadProfilePicture()
        {
            try
            {
                using (var picStream = await NetHelper.GetProfilePicture(userIDTB.Text))
                {
                    await this.WriteToFile(picStream);
                    imageView.Source = await PictureConvert.FromStream(picStream);
                }
            }
            catch (COMException)
            {
                try
                {
                    using (var picStream = await ReadFromFile())
                    {
                        imageView.Source = await PictureConvert.FromStream(picStream);
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine(e.Message);
                    StorageFolder appInstalledFolder = Windows.ApplicationModel.Package.Current.InstalledLocation;
                    StorageFolder assets = await appInstalledFolder.GetFolderAsync("Assets");
                    var file = await assets.GetFileAsync("Square150x150Logo.scale-200.png");
                    imageView.Source = await PictureConvert.FromStream(await file.OpenAsync(FileAccessMode.Read));
                }
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
                var greenRGB = new Color();
                greenRGB.R = 0x44;
                greenRGB.G = 0xc6;
                greenRGB.B = 0x90;
                greenRGB.A = 255;
                var green = new SolidColorBrush(greenRGB);
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

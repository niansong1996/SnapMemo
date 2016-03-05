using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Graphics.Imaging;
using Windows.Storage.Streams;
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
    public sealed partial class TestPage : Page
    {
        public TestPage()
        {
            this.InitializeComponent();
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            Debug.WriteLine("navigate finish");

            IRandomAccessStream stream = null;

            try
            {
                stream = e.Parameter as IRandomAccessStream;
            }
            catch (Exception)
            {
                Debug.WriteLine("wrong type convert");
            }


            // Create the decoder from the stream
            var decoder = await BitmapDecoder.CreateAsync(stream);

            // Get the SoftwareBitmap representation of the file
            SoftwareBitmap picture = await decoder.GetSoftwareBitmapAsync();

            SoftwareBitmap bitmapBGRA8 = SoftwareBitmap.Convert(picture, BitmapPixelFormat.Bgra8, BitmapAlphaMode.Premultiplied);

            var source = new SoftwareBitmapSource();
            await source.SetBitmapAsync(bitmapBGRA8);

            imgView.Source = source;

            Debug.WriteLine("already finish");
        }
    }
}

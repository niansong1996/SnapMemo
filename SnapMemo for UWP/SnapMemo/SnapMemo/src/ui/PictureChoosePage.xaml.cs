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
    public sealed partial class PictureChoosePage : Page
    {
        // the picture handling with
        BitmapDecoder decoder;

        public PictureChoosePage()
        {
            this.InitializeComponent();
        }

        private async void capture()
        {
            var memStream = new InMemoryRandomAccessStream();
            var encoder = await BitmapEncoder.CreateForTranscodingAsync(memStream, decoder);

            BitmapBounds bounds = new BitmapBounds();
            bounds.X = 0;
            bounds.Y = 0;
            bounds.Height = 34;
            bounds.Width = 77;
            encoder.BitmapTransform.Bounds = bounds;

            await encoder.FlushAsync();

            await WriteToFile(memStream);
            memStream.Dispose();
        }

        private async Task WriteToFile(IRandomAccessStream memStream)
        {
            var myPictures = await StorageLibrary.GetLibraryAsync(KnownLibraryId.Pictures);
            var wFile = await myPictures.Folders[0]
                .CreateFileAsync("testYao.jpg", CreationCollisionOption.ReplaceExisting);

            var wStream = await wFile.OpenAsync(FileAccessMode.ReadWrite);
            using(var outputStream = wStream.GetOutputStreamAt(0))
            {
                memStream.Seek(0);
                await RandomAccessStream.CopyAsync(memStream, outputStream);
            }
            wStream.Dispose();
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            var inputFile = e.Parameter as StorageFile;

            SoftwareBitmap picture;
            using (IRandomAccessStream stream = await inputFile.OpenAsync(FileAccessMode.Read))
            {
                // Create the decoder from the stream
                decoder = await BitmapDecoder.CreateAsync(stream);

                // Get the SoftwareBitmap representation of the file
                picture = await decoder.GetSoftwareBitmapAsync();
            }

            SoftwareBitmap bitmapBGRA8 = SoftwareBitmap.Convert(picture, BitmapPixelFormat.Bgra8, BitmapAlphaMode.Premultiplied);

            var source = new SoftwareBitmapSource();
            await source.SetBitmapAsync(bitmapBGRA8);

            imgView.Source = source;
        }

        private void OnOK(object sender, RoutedEventArgs e)
        {
            capture();

            // TODO send to server
            //Frame root = Window.Current.Content as Frame;
            //root.Navigate(typeof(MemoModifyPage));
        }

        private void OnCancel(object sender, RoutedEventArgs e)
        {
            Frame root = Window.Current.Content as Frame;
            root.Navigate(typeof(MainPage));
        }

        enum EventMode
        {
            MOVE,
            DRAW,
            NONE
        }
        private EventMode mode;
        private Point initTappedPos;
        private Point initRectPos;

        private void BorderOnTapped(object sender, TappedRoutedEventArgs e)
        {
            var x = e.GetPosition(border).X;
            var y = e.GetPosition(border).Y;
            Debug.WriteLine("border detect tap:{0}, {1}", x, y);

            var thickness = border.Margin;
            Debug.WriteLine("origin margin: " + thickness.ToString());

            thickness.Left += x;
            thickness.Top += y;
            border.Margin = thickness;

            Debug.WriteLine("final margin: " + border.Margin.ToString());
        }

        private void OnImagePressed(object sender, PointerRoutedEventArgs e)
        {
            var currentPos = e.GetCurrentPoint(containerGrid).Position;

            initTappedPos = currentPos;
            mode = EventMode.DRAW;

            border.Width = 0;
            border.Height = 0;
            border.Margin = new Thickness(currentPos.X, currentPos.Y, 0, 0);
        }

        private void OnMoved(object sender, PointerRoutedEventArgs e)
        {
            var currentPos = e.GetCurrentPoint(containerGrid).Position;

            if (mode == EventMode.DRAW)
            {
                // the position of the left-top of the rectangle
                var startPoint = new Point(
                    Math.Min(currentPos.X, initTappedPos.X), 
                    Math.Min(currentPos.Y, initTappedPos.Y));

                border.Margin = new Thickness(startPoint.X, startPoint.Y, 0, 0);
                border.Width = Math.Abs(currentPos.X - initTappedPos.X);
                border.Height = Math.Abs(currentPos.Y - initTappedPos.Y);
            }
            else if(mode == EventMode.MOVE)
            {
                border.Margin = new Thickness(
                    initRectPos.X + (currentPos.X - initTappedPos.X),
                    initRectPos.Y + (currentPos.Y - initTappedPos.Y),
                    0, 0);
            }
            else
            {
                // nothing
            }
        }

        private void OnReleased(object sender, PointerRoutedEventArgs e)
        {
            mode = EventMode.NONE;
            initTappedPos = new Point(0, 0);
        }

        private static readonly double sensitiveRadius = 16;

        private void OnRectPressed(object sender, PointerRoutedEventArgs e)
        {
            // the side value relative to the container
            double leftSide = border.Margin.Left;
            double rightSide = leftSide + border.Width;
            double topSide = border.Margin.Top;
            double bottomSide = topSide + border.Height;

            var currentPos = e.GetCurrentPoint(containerGrid).Position;

            // whether the current tapped position is close to the sides
            bool closeLeft = currentPos.X - leftSide < sensitiveRadius;
            bool closeRight = rightSide - currentPos.X < sensitiveRadius;
            bool closeTop = currentPos.Y - topSide < sensitiveRadius;
            bool closeBottom = bottomSide - currentPos.Y < sensitiveRadius;

            if (closeLeft && closeTop)
            {
                mode = EventMode.DRAW;
                initTappedPos = new Point(rightSide, bottomSide);
            }
            else if (closeLeft && closeBottom)
            {
                mode = EventMode.DRAW;
                initTappedPos = new Point(rightSide, topSide);
            }
            else if (closeRight && closeTop)
            {
                mode = EventMode.DRAW;
                initTappedPos = new Point(leftSide, bottomSide);
            }
            else if (closeRight && closeBottom)
            {
                mode = EventMode.DRAW;
                initTappedPos = new Point(leftSide, topSide);
            }
            else // not in 4 corner, move the rect without resizing
            {
                mode = EventMode.MOVE;
                initTappedPos = currentPos;
                initRectPos = new Point(leftSide, topSide);
            }
        }
    }
}

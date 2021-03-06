﻿using SnapMemo.src.logic;
using SnapMemo.src.model;
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
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;
using Windows.UI.Xaml.Shapes;
using Windows.Web.Http;
using Windows.Web.Http.Filters;

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

        private bool recognizing = false;
        private double backgroundOpacity = 0.32;

        public PictureChoosePage()
        {
            this.InitializeComponent();
        }

        private async Task<IRandomAccessStream> Capture()
        {
            var memStream = new InMemoryRandomAccessStream();
            var encoder = await BitmapEncoder.CreateForTranscodingAsync(memStream, decoder);

            var imgViewW = imgView.RenderSize.Width;
            var imgViewH = imgView.RenderSize.Height;

            var deviationW = (containerGrid.RenderSize.Width - imgViewW) / 2;
            var deviationH = (containerGrid.RenderSize.Height - imgViewH) / 2;

            BitmapBounds bounds = new BitmapBounds();
            bounds.X = (uint)((border.Margin.Left - deviationW) / imgViewW * decoder.PixelWidth);
            bounds.Y = (uint)((border.Margin.Top - deviationH) / imgViewH * decoder.PixelHeight);
            bounds.Width = (uint)(border.Width / imgViewW * decoder.PixelWidth);
            bounds.Height = (uint)(border.Height / imgViewH * decoder.PixelHeight);

            Debug.WriteLine("left:{0}, top:{1}, width:{2}, height:{3}", border.Margin.Left, border.Margin.Top, border.Width, border.Height);
            Debug.WriteLine("cwidth:{0}, cheight:{1}", imgViewW, imgViewH);
            Debug.WriteLine("pixelWidth:{0}, pixelHeight:{1}", decoder.PixelWidth, decoder.PixelHeight);
            Debug.WriteLine("x:{0}, y:{1}, w:{2}, h:{3}", bounds.X, bounds.Y, bounds.Width, bounds.Height);

            encoder.BitmapTransform.Bounds = bounds;

            // flush content in bounds into memStream
            await encoder.FlushAsync();

            return memStream;
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

            IRandomAccessStream stream = null;

            try
            {
                stream = e.Parameter as IRandomAccessStream;
            }
            catch (Exception)
            {
                Debug.WriteLine("wrong type convert");
            }

            decoder = await BitmapDecoder.CreateAsync(stream);
            imgView.Source = await PictureConvert.FromStream(stream);
        }

        private async Task handleNetException(Frame frame)
        {
            var msgDialog = new MessageDialog("网络错误，确定返回主界面") { Title = "网络错误" };
            msgDialog.Commands.Add(new Windows.UI.Popups.UICommand("确定", uiCommand => {
                frame.Navigate(typeof(MainPage));
            }));
            msgDialog.Commands.Add(new Windows.UI.Popups.UICommand("取消", uiCommand => { }));
            await msgDialog.ShowAsync();
        }

        private async void OnOK(object sender, RoutedEventArgs e)
        {
            if (recognizing)
            {
                return;
            }
            else
            {
                recognizing = true;
            }

            var memStream = await Capture();

            Frame root = Window.Current.Content as Frame;

            RecognizingPopup popUp = new RecognizingPopup();
            try
            {
                // animation
                outmostGridView.Opacity = backgroundOpacity;
                popUp.Show();

                var memo = await NetHelper.ResolveImage(memStream);

                // finish animation
                popUp.Dispose();

                root.Navigate(typeof(MemoModifyPage), memo);
            }
            catch (COMException)
            {
                popUp.Dispose();
                await handleNetException(root);
            }
        }

        private void OnCancel(object sender, RoutedEventArgs e)
        {
            Frame frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainPage));
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

        private void imgView_ManipulationStarted(object sender, ManipulationStartedRoutedEventArgs e)
        {
            var currentPos = calPointRelativeToContainerGrid(e.Position, sender);

            initTappedPos = currentPos;
            mode = EventMode.DRAW;

            border.Width = 0;
            border.Height = 0;
            border.Margin = new Thickness(currentPos.X, currentPos.Y, 0, 0);
        }

        private Point calPointRelativeToContainerGrid(Point origin, object sender)
        {
            var currentPos = origin;
            var imgViewW = imgView.RenderSize.Width;
            var imgViewH = imgView.RenderSize.Height;

            var deviationW = (containerGrid.RenderSize.Width - imgViewW) / 2;
            var deviationH = (containerGrid.RenderSize.Height - imgViewH) / 2;

            if (sender.GetType() == typeof(Rectangle))
            {
                currentPos.X += border.Margin.Left;
                currentPos.Y += border.Margin.Top;
            }
            else
            {
                if (deviationW > 0)
                {
                    currentPos.X += deviationW;
                }

                if (deviationH > 0)
                {
                    currentPos.Y += deviationH;
                }
            }

            return currentPos;
        }

        private void manipulationDelta(object sender, ManipulationDeltaRoutedEventArgs e)
        {
            var imgViewW = imgView.RenderSize.Width;
            var imgViewH = imgView.RenderSize.Height;

            var deviationW = (containerGrid.RenderSize.Width - imgViewW) / 2;
            var deviationH = (containerGrid.RenderSize.Height - imgViewH) / 2;

            var currentPos = calPointRelativeToContainerGrid(e.Position, sender);

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
            else if (mode == EventMode.MOVE)
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

        private void manipulationCompleted(object sender, ManipulationCompletedRoutedEventArgs e)
        {
            Debug.WriteLine("complete manipulating");

            mode = EventMode.NONE;
            initTappedPos = new Point(0, 0);
        }

        private void border_ManipulationStarted(object sender, ManipulationStartedRoutedEventArgs e)
        {
            var imgViewW = imgView.RenderSize.Width;
            var imgViewH = imgView.RenderSize.Height;

            var deviationW = (containerGrid.RenderSize.Width - imgViewW) / 2;
            var deviationH = (containerGrid.RenderSize.Height - imgViewH) / 2;

            // the side value relative to the container
            double leftSide = border.Margin.Left;
            double rightSide = leftSide + border.Width;
            double topSide = border.Margin.Top;
            double bottomSide = topSide + border.Height;

            var currentPos = calPointRelativeToContainerGrid(e.Position, sender);

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

        private async Task focus(object sender, Point position)
        {
            var memStream = new InMemoryRandomAccessStream();
            var encoder = await BitmapEncoder.CreateForTranscodingAsync(memStream, decoder);

            // flush content in bounds into memStream
            await encoder.FlushAsync();

            Frame root = Window.Current.Content as Frame;

            RecognizingPopup popUp = new RecognizingPopup();
            try
            {
                var realX = (position.X / imgView.RenderSize.Width) * decoder.PixelWidth;
                var realY = (position.Y / imgView.RenderSize.Height) * decoder.PixelHeight;

                Debug.WriteLine("holding: " + realX + ", " + realY);

                // animation
                outmostGridView.Opacity = backgroundOpacity;
                popUp.Show();
                var memo = await NetHelper.ResolveImage(memStream, realX, realY);

                // finish animation
                popUp.Dispose();

                root.Navigate(typeof(MemoModifyPage), memo);
            }
            catch (COMException ex)
            {
                popUp.Dispose();
                await handleNetException(root);
            }
        }

        private async void imgView_Holding(object sender, HoldingRoutedEventArgs e)
        {
            if (recognizing)
            {
                return;
            }
            else
            {
                recognizing = true;
            }

            var position = e.GetPosition(imgView);

            await focus(sender, position);
        }

        private async void imgView_RightTapped(object sender, RightTappedRoutedEventArgs e)
        {
            if (recognizing)
            {
                return;
            }
            else
            {
                recognizing = true;
            }

            var position = e.GetPosition(imgView);

            await focus(sender, position);
        }
    }
}

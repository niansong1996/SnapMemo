using System;
using System.Collections.Generic;
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

// The User Control item template is documented at http://go.microsoft.com/fwlink/?LinkId=234236

namespace SnapMemo.src.ui
{
    public sealed partial class RecognizingPopup : UserControl
    {
        private Popup popUp;

        public RecognizingPopup()
        {
            this.InitializeComponent();

            popUp = new Popup();
            this.Width = Window.Current.Bounds.Width;
            this.Height = Window.Current.Bounds.Height;
            popUp.Child = this;

            this.loaded();
        }

        public void Show()
        {
            popUp.IsOpen = true;
        }

        public void Dispose()
        {
            popUp.IsOpen = false;
            this.unLoaded();
        }

        private void loaded()
        {
            Window.Current.SizeChanged += currentSizeChanged;
        }

        private void unLoaded()
        {
            Window.Current.SizeChanged -= currentSizeChanged;
        }

        private void currentSizeChanged(object sender, Windows.UI.Core.WindowSizeChangedEventArgs e)
        {
            this.Width = e.Size.Width;
            this.Height = e.Size.Height;
        }

        public RecognizingPopup(string title) : this()
        {
            title_TB.Text = title;
        }


    }
}

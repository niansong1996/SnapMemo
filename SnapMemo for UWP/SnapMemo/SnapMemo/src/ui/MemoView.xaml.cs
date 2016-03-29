using SnapMemo.src.model;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI;
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
    public sealed partial class MemoView : UserControl
    {
        public Memo Memo
        {
            get; private set;
        }
        public bool Selected { get; set; }

        public MemoView(Memo memo)
        {
            this.InitializeComponent();
            this.Memo = memo;
            this.Tapped += ClickToModify;

            // presentation
            this.titleTB.Text = memo.Title;
            this.contentTB.Text = memo.Content;

            this.timeTB.Text = Time2String.Time2Str(memo.Time);
        }

        public void ClickToModify(object sender, RoutedEventArgs e)
        {
            Frame frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MemoModifyPage), Memo);
        }

        public void ClickToSelect(object sender, RoutedEventArgs e)
        {
            var white = new SolidColorBrush(Colors.White);
            var blue = new SolidColorBrush(Colors.Blue);

            if (Selected)
            {
                Selected = false;
                border.BorderBrush = white;
            }
            else
            {
                Selected = true;
                border.BorderBrush = blue;
            }
        }
    }
}

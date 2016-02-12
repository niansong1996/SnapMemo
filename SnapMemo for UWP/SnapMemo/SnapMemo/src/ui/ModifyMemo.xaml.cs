using SnapMemo.src.logic;
using SnapMemo.src.model;
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
    public sealed partial class MemoModifyPage : Page
    {
        private enum OperateType
        {
            MODIFY,
            ADD
        }

        private Memo modifyingMemo;
        private OperateType type;

        public MemoModifyPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            type = e.Parameter == null ? OperateType.ADD : OperateType.MODIFY;
            modifyingMemo = e.Parameter == null ? new Memo(DateTime.Now) : e.Parameter as Memo;
            if(type == OperateType.MODIFY)
            {
                titleTB.Text = modifyingMemo.Title;
                contentTB.Text = modifyingMemo.Content;
                timeDP.Date = modifyingMemo.Time;
                timeTP.Time = modifyingMemo.Time.TimeOfDay;
            }
        }

        private void onSave(object sender, RoutedEventArgs e)
        {
            modifyingMemo.Title = titleTB.Text;
            var ds = timeDP.Date;
            var ts = timeTP.Time;
            modifyingMemo.Time = new DateTime(ds.Year, ds.Month, ds.Day, ts.Hours, ts.Minutes, ts.Seconds);
            modifyingMemo.Content = contentTB.Text;

            Frame root = Window.Current.Content as Frame;
            if(type == OperateType.ADD)
            {
                DBHelper.AddMemo(modifyingMemo);
                NotificationHelper.AddToastToSchedule(modifyingMemo);
                NotificationHelper.AddTileNotification();

                root.Navigate(typeof(MainPage));
            }
            else if(type == OperateType.MODIFY)
            {
                DBHelper.UpdateMemo(modifyingMemo);
                NotificationHelper.RemoveToastFromSchedule(modifyingMemo);
                NotificationHelper.AddToastToSchedule(modifyingMemo);

                root.Navigate(typeof(MainPage));
            }
        }

        private void onCancel(object sender, RoutedEventArgs e)
        {
            Frame root = Window.Current.Content as Frame;
            root.Navigate(typeof(MainPage));
        }
    }
}

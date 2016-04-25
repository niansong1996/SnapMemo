using SnapMemo.src.logic;
using SnapMemo.src.model;
using SnapMemo.src.model.Operation;
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
    public sealed partial class MemoModifyPage : Page
    {
        private enum OperateType
        {
            MODIFY,
            SNAP,
            ADD
        }

        private Memo modifyingMemo;
        private OperateType type;

        private static readonly bool debugWithoutNet = false;

        public MemoModifyPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            if(e.Parameter == null)
            {
                type = OperateType.ADD;
                modifyingMemo = new Memo(DateTime.Now);
                timeDP.Date = DateTime.Now.AddMinutes(5);
                timeTP.Time = DateTime.Now.AddMinutes(5).TimeOfDay;
            }
            else
            {
                modifyingMemo = e.Parameter as Memo;

                // fill the blanks
                titleTB.Text = modifyingMemo.Title;
                contentTB.Text = modifyingMemo.Content;
                timeDP.Date = modifyingMemo.Time;
                timeTP.Time = modifyingMemo.Time.TimeOfDay;

                // if no memoID, that is from snap
                type = modifyingMemo.MemoID == null ? OperateType.SNAP : OperateType.MODIFY;
            }
        }

        private async void onSave(object sender, RoutedEventArgs e)
        {
            modifyingMemo.Title = titleTB.Text;
            var ds = timeDP.Date;
            var ts = timeTP.Time;
            modifyingMemo.Time = new DateTime(ds.Year, ds.Month, ds.Day, ts.Hours, ts.Minutes, ts.Seconds, DateTimeKind.Local).ToUniversalTime();
            modifyingMemo.Content = contentTB.Text;

            Frame rootFrame = Window.Current.Content as Frame;
            if(type == OperateType.ADD || type == OperateType.SNAP)
            {
                // sync in server-end
                try
                {
                    var memoID = await NetHelper.AddMemo(Preference.GetUserID(), modifyingMemo);
                    modifyingMemo.MemoID = memoID;
                }
                catch (COMException)
                {
                    UnsyncQueue.Instance.Enqueue(new AddMemoOperation(Preference.GetUserID(), modifyingMemo));
                    modifyingMemo.MemoID = MemoIDGenerator.Generate(Preference.GetUserID());
                }
                
                // save in local DB
                DBHelper.AddMemo(modifyingMemo);

                // add notification
                NotificationHelper.AddToastToSchedule(modifyingMemo);

                rootFrame.Navigate(typeof(MainPage));
            }
            else if(type == OperateType.MODIFY)
            {
                // sync in server-end
                try
                {
                    await NetHelper.ModifyMemo(modifyingMemo);
                }
                catch (COMException)
                {
                    UnsyncQueue.Instance.Enqueue(new ModifyMemoOperation(modifyingMemo));
                }

                // save in local DB
                DBHelper.UpdateMemo(modifyingMemo);

                // add notification
                NotificationHelper.RemoveToastFromSchedule(modifyingMemo);
                NotificationHelper.AddToastToSchedule(modifyingMemo);

                rootFrame.Navigate(typeof(MainPage));
            }
        }

        private void onCancel(object sender, RoutedEventArgs e)
        {
            var rootFrame = Window.Current.Content as Frame;
            rootFrame.Navigate(typeof(MainPage));
        }

        private void backButton_Click(object sender, RoutedEventArgs e)
        {
            var rootFrame = Window.Current.Content as Frame;
            rootFrame.Navigate(typeof(MainPage));
        }
    }
}

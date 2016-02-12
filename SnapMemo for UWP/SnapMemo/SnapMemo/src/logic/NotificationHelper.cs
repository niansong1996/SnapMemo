using SnapMemo.src.model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.UI.Notifications;

namespace SnapMemo.src.logic
{
    class NotificationHelper
    {
        private static readonly string toastIdPrefix = "SnapM";

        public static void AddToSchedule(Memo memo)
        {
            // prepare display xml
            var toastXml = ToastNotificationManager.GetTemplateContent
                (ToastTemplateType.ToastImageAndText02);
            var texts = toastXml.GetElementsByTagName("text");
            texts[0].AppendChild(toastXml.CreateTextNode(memo.Title));
            texts[1].AppendChild(toastXml.CreateTextNode(memo.Content));

            try
            {
                var toast = new ScheduledToastNotification(toastXml, memo.Time);
                toast.Id = toastIdPrefix + memo.Id;
                ToastNotificationManager.CreateToastNotifier().AddToSchedule(toast);
            }
            catch (ArgumentException)
            {
                Debug.WriteLine("catch exception");
            }
        }

        public static void RemoveFromSchedule(Memo memo)
        {
            var toastNotifier = ToastNotificationManager.CreateToastNotifier();
            var toasts = toastNotifier.GetScheduledToastNotifications();

            foreach(var one in toasts)
            {
                if(one.Id == toastIdPrefix + memo.Id)
                {
                    toastNotifier.RemoveFromSchedule(one);
                    break;
                }
            }
        }
    }
}

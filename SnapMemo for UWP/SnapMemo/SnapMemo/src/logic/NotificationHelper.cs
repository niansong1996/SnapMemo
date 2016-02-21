using SnapMemo.src.model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using Windows.Storage;
using Windows.UI.Notifications;

namespace SnapMemo.src.logic
{
    class NotificationHelper
    {
        private static readonly string toastIdPrefix = "SnapM";
        private static readonly string tileXmlFileName = @"MyTile.xml";

        public static void AddToastToSchedule(Memo memo)
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
                toast.Id = toastIdPrefix + memo.LocalID;
                ToastNotificationManager.CreateToastNotifier().AddToSchedule(toast);
            }
            catch (ArgumentException)
            {
                Debug.WriteLine("catch exception");
            }
        }

        public static void RemoveToastFromSchedule(Memo memo)
        {
            var toastNotifier = ToastNotificationManager.CreateToastNotifier();
            var toasts = toastNotifier.GetScheduledToastNotifications();

            foreach(var one in toasts)
            {
                if(one.Id == toastIdPrefix + memo.LocalID)
                {
                    toastNotifier.RemoveFromSchedule(one);
                    break;
                }
            }
        }

        public async static void AddTileNotification()
        {
            var tileXml = new Windows.Data.Xml.Dom.XmlDocument();                        
            
            // load xml
            var installLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
            var assetsFolder = await installLocation.GetFolderAsync("Assets");
            var tileXmlFile = await assetsFolder.GetFileAsync(tileXmlFileName);
            var xmlText = await FileIO.ReadTextAsync(tileXmlFile);

            // set title and caption
            tileXml.LoadXml(string.Format(xmlText, "title", "caption"));

            //var tileTextAttributes = tileXml.GetElementsByTagName("text");
            //tileTextAttributes[0].AppendChild(tileXml.CreateTextNode("This notification will expire at "));
            var tileNotification = new TileNotification(tileXml);

            // set expiration time
            var dueTime = DateTime.Now;
            dueTime.AddMinutes(20);
            tileNotification.ExpirationTime = dueTime;

            // set enable
            var tileUpdater = TileUpdateManager.CreateTileUpdaterForApplication();
            tileUpdater.EnableNotificationQueueForWide310x150(true);

            tileUpdater.Update(tileNotification);
        }
    }
}

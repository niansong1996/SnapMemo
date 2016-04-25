using SnapMemo.src.model;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Xml.Dom;
using Windows.Storage;
using Windows.UI.Notifications;

namespace SnapMemo.src.logic
{
    public class NotificationHelper
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

        public async static void AddTileNotification(Memo memo)
        {   
            // load xml template
            var installLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
            var assetsFolder = await installLocation.GetFolderAsync("Assets");
            var tileXmlFile = await assetsFolder.GetFileAsync(tileXmlFileName);
            var xmlText = await FileIO.ReadTextAsync(tileXmlFile);

            // set title and caption
            var tileXml = new XmlDocument();
            tileXml.LoadXml(string.Format(xmlText, memo.Title, Time2String.Time2Str(memo.Time) + "\n" + memo.Content));

            var tileNotification = new TileNotification(tileXml);
            tileNotification.Tag = memo.MemoID.Substring(6, 10);

            // set expiration time
            tileNotification.ExpirationTime = memo.Time.AddMinutes(1);

            // set TileUpdater
            var tileUpdater = TileUpdateManager.CreateTileUpdaterForApplication();
            tileUpdater.Update(tileNotification);
        }

        public async static void RefreshTiles(IList<Memo> allMemos)
        {
            // load xml template
            var installLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
            var assetsFolder = await installLocation.GetFolderAsync("Assets");
            var tileXmlFile = await assetsFolder.GetFileAsync(tileXmlFileName);
            var xmlText = await FileIO.ReadTextAsync(tileXmlFile);

            // set TileUpdater
            var tileUpdater = TileUpdateManager.CreateTileUpdaterForApplication();
            tileUpdater.EnableNotificationQueue(true);

            // start time
            var startTime = DateTime.Now.AddSeconds(10);

            // temp list to add tiles, then reverse it according to the emgergency
            tileUpdater.Clear();
            var now = DateTime.Now;
            List<Memo> tempList = new List<Memo>();
            for(int i = 0, addCount = 0; i < allMemos.Count && addCount < 5; ++i)
            {
                Memo memo = allMemos[i];

                if (now.CompareTo(memo.Time) <= 0)
                {
                    ++addCount;
                    tempList.Add(memo);
                }
            }

            // reverse and add to schedule
            for (int i = 0; i < tempList.Count; ++i)
            {
                Memo memo = tempList[tempList.Count - 1 - i];

                // set title and caption
                var tileXml = new XmlDocument();
                tileXml.LoadXml(string.Format(xmlText, memo.Title, Time2String.Time2Str(memo.Time) + "\n" + memo.Content));

                var tileNotification = new ScheduledTileNotification(tileXml, startTime.AddSeconds(i * 10));
                tileNotification.Tag = memo.MemoID.Substring(6, 10);

                // set expiration time
                tileNotification.ExpirationTime = memo.Time.AddMinutes(1);

                tileUpdater.AddToSchedule(tileNotification);
            }
        }

        public static void RemoveTileNotifications()
        {
            var tileUpdater = TileUpdateManager.CreateTileUpdaterForApplication();
            tileUpdater.Clear();
        }
    }
}

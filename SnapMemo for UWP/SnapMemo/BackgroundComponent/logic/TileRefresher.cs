using SnapMemo.src.logic;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel.Background;

namespace BackgroundComponent
{
    public sealed class TileRefresher : IBackgroundTask
    {
        void IBackgroundTask.Run(IBackgroundTaskInstance taskInstance)
        {
            var deferral = taskInstance.GetDeferral();

            var allMemos = DBHelper.GetAllMemo();

            var memos = MemoSort.SortByTime(allMemos);

            NotificationHelper.RefreshNotification(memos);

            Debug.WriteLine("==============fire TileRefresher.");

            deferral.Complete();
        }
    }
}

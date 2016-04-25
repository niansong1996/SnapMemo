using SnapMemo.src.logic;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;

namespace SnapMemo.src.model.Operation
{
    public class AddMemoOperation : IUploadable
    {
        private string userID;
        private Memo memo;

        public AddMemoOperation(string userID, Memo memo)
        {
            this.userID = userID;
            this.memo = memo;
        }

        async Task IUploadable.Upload()
        {
            var memoID = await NetHelper.AddMemo(userID, memo);
            memo.MemoID = memoID;
            DBHelper.UpdateMemo(memo);
        }
    }
}

using SnapMemo.src.logic;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.model.Operation
{
    public class DeleteMemoOperation : IUploadable
    {
        string memoID;

        public DeleteMemoOperation(string memoID)
        {
            this.memoID = memoID;
        }

        public async Task Upload()
        {
            await NetHelper.DeleteMemo(memoID);
        }
    }
}

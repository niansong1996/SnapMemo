using SnapMemo.src.logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.model.Operation
{
    class ModifyMemoOperation : IUploadable
    {
        private Memo memo;

        public ModifyMemoOperation(Memo memo)
        {
            this.memo = memo;
        }

        public async Task Upload()
        {
            await NetHelper.ModifyMemo(memo);
        }
    }
}

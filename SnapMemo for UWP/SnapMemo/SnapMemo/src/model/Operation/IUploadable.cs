using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;

namespace SnapMemo.src.model.Operation
{
    interface IUploadable
    {
        Task Upload();
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.tool
{
    class MemoIDGenerator
    {
        public static string Generate(string userID)
        {
            DateTime current = DateTime.Now;
            return userID + current.Year + current.Month + current.Day + current.Hour + current.Minute + current.Second + current.Millisecond;
        }
    }
}

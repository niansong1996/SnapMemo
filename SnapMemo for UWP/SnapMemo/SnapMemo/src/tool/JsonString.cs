using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.tool
{
    class JsonString
    {
        public static string DeQuotes(string str)
        {
            return str.Substring(1, str.Length - 2);
        }
    }
}

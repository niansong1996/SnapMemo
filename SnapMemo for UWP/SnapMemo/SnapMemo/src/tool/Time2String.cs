using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.tool
{
    public class Time2String
    {
        public static string Time2Str(DateTime time)
        {
            var str = new StringBuilder();
            str.Append(time.Year);
            str.Append("-");
            str.Append(time.Month);
            str.Append("-");
            str.Append(time.Day);

            str.Append(" ");
            str.Append(time.Hour);
            str.Append(":");
            if(time.Minute < 10)
            {
                str.Append("0");
            }
            str.Append(time.Minute);

            return str.ToString();
        }
    }
}

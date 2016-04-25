using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.tool
{
    public class StringEncode
    {
        public static string FromUTF8(string origin)
        {
            Debug.WriteLine("========start trying encode");
            Debug.WriteLine(Encoding.Unicode.GetString(Encoding.UTF8.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF32.GetString(Encoding.UTF8.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF7.GetString(Encoding.UTF8.GetBytes(origin)));
            Debug.WriteLine(Encoding.BigEndianUnicode.GetString(Encoding.UTF8.GetBytes(origin)));
            Debug.WriteLine(Encoding.ASCII.GetString(Encoding.UTF8.GetBytes(origin)));

            Debug.WriteLine(Encoding.UTF8.GetString(Encoding.Unicode.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF32.GetString(Encoding.Unicode.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF7.GetString(Encoding.Unicode.GetBytes(origin)));
            Debug.WriteLine(Encoding.BigEndianUnicode.GetString(Encoding.Unicode.GetBytes(origin)));
            Debug.WriteLine(Encoding.ASCII.GetString(Encoding.Unicode.GetBytes(origin)));

            Debug.WriteLine(Encoding.Unicode.GetString(Encoding.UTF32.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF8.GetString(Encoding.UTF32.GetBytes(origin)));
            Debug.WriteLine(Encoding.UTF7.GetString(Encoding.UTF32.GetBytes(origin)));
            Debug.WriteLine(Encoding.BigEndianUnicode.GetString(Encoding.UTF32.GetBytes(origin)));
            Debug.WriteLine(Encoding.ASCII.GetString(Encoding.UTF32.GetBytes(origin)));

            return Encoding.Unicode.GetString(Encoding.UTF8.GetBytes(origin));
        }
    }
}

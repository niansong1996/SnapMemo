using SQLite.Net.Attributes;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Foundation.Collections;
using Windows.Storage;

namespace SnapMemo.src.model
{
    class Memo
    {
        private static IPropertySet localValues;
        private static uint idCount;

        [PrimaryKey]
        public uint Id { get; private set; }
        public DateTime Time { get; set; }
        public string Location { get; set; }
        public string Title { get; set; }
        public string Content { get; set; }

        static Memo()
        {
            localValues = ApplicationData.Current.LocalSettings.Values;

            try
            {
                idCount = (uint)localValues["idCount"];
            }
            catch (NullReferenceException e)
            {
                idCount = 0;
            }
            
            Debug.WriteLine("read idCount: " + idCount);
        }

        public Memo()
        {
            Id = idCount;

            ++idCount;
            localValues["idCount"] = idCount;
        }
        
        public Memo(DateTime time, string content = "", string location = "", string title = ""): this()
        {
            Time = time;
            Location = location;
            Title = title;
            Content = content;
        }

        public override string ToString()
        {
            return "Title: " + Title
                + "\nTime: " + Time
                + "\nContent: " + Content;
        }
    }
}

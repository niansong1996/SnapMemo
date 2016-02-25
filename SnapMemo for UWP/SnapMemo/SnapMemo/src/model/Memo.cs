using SQLite.Net.Attributes;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Foundation.Collections;
using Windows.Storage;

namespace SnapMemo.src.model
{
    class Memo
    {
        private static IPropertySet localValues;
        private static uint idCount;

        [PrimaryKey]
        public uint LocalID { get; private set; }
        public DateTime Time { get; set; }
        public string Location { get; set; }
        public string Title { get; set; }
        public string Content { get; set; }
        public string MemoID { get; set; }

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
            LocalID = idCount;

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

        public Memo(JsonObject jsonObject): this()
        {
            Title = jsonObject["topic"].ToString();
            Content = jsonObject["content"].ToString();

            // time format : 2016-02-07 15:32
            var dateTimeStr = jsonObject["time"].ToString();
            var dateTimeArr = dateTimeStr.Split(' ');

            var dateStr = dateTimeArr[0];
            var dateArr = dateStr.Split('-');

            var timeStr = dateTimeArr[1];
            var timeArr = timeStr.Split(':');

            Time = new DateTime(int.Parse(dateArr[0]), int.Parse(dateArr[1]), int.Parse(dateArr[2]),
                int.Parse(timeArr[0]), int.Parse(timeArr[1]), 0); // neglect the seconds
        }

        public override string ToString()
        {
            return "Title: " + Title
                + "\nTime: " + Time
                + "\nContent: " + Content;
        }

        public JsonObject ToJsonObject()
        {
            JsonObject obj = new JsonObject();
            obj["topic"] = JsonValue.CreateStringValue(Title);
            obj["content"] = JsonValue.CreateStringValue(Content);

            // time format : 2016-02-07 15:32
            var stringBuilder = new StringBuilder();
            stringBuilder.AppendFormat("{0}-{1}-{2} {3}:{4}",
                Time.Year, Time.Month, Time.Day, Time.Hour, Time.Minute);
            obj["time"] = JsonValue.CreateStringValue(stringBuilder.ToString());

            return obj;
        }
    }
}

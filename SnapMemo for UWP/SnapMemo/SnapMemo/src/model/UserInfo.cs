using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Foundation.Collections;
using Windows.Storage;

namespace SnapMemo.src.model
{
    struct UserInfo
    {
        private static IPropertySet localValues;

        public string UserID { get; set; }
        public string UserName { get; set; }
        public string EducationInfo { get; set; }
        public string Signature { get; set; }

        static UserInfo()
        {
            localValues = ApplicationData.Current.LocalSettings.Values;
        }

        public UserInfo(JsonObject returnJson)
        {
            UserID = JsonString.DeQuotes(localValues["userID"].ToString());
            UserName = JsonString.DeQuotes(returnJson["userName"].ToString());
            EducationInfo = JsonString.DeQuotes(returnJson["educationInfo"].ToString());
            Signature = JsonString.DeQuotes(returnJson["signature"].ToString());
        }
    }
}

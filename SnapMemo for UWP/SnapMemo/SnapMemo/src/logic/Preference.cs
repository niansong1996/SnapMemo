using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.UI.Xaml;

namespace SnapMemo.src.logic
{
    static class Preference
    {
        private static IPropertySet localValues = ApplicationData.Current.LocalSettings.Values;

        public readonly static string DefaultID = "000000";
        public readonly static string DefaultName = "SnapMemo";
        public readonly static string DefaultSignature = "Life is short, you need python";

        public static string GetUserID()
        {
            var userID = localValues["userID"];
            return userID == null ? DefaultID : ((string)userID);
        }

        public static void SetUserID(string userID)
        {
            localValues["userID"] = userID;
        }

        public static string GetUserName()
        {
            var userName = localValues["userName"];
            return userName == null ? DefaultName : ((string)userName);
        }

        public static void SetUserName(string userName)
        {
            localValues["userName"] = userName;
        }

        public static string GetSignature()
        {
            var signature = localValues["signature"];
            return signature == null ? DefaultSignature : ((string)signature);
        }

        public static void SetSignature(string signature)
        {
            localValues["signature"] = signature;
        }
    }
}

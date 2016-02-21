using SnapMemo.src.model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Storage.Streams;
using Windows.Web.Http;

namespace SnapMemo.src.logic
{
    static class NetHelper
    {
        private static Uri uri;

        static NetHelper()
        {
            uri = new Uri("http://139.129.40.103:5678/SnapMemo");
        }


        /// <summary>
        /// package and send an http request in a specific format
        /// </summary>
        /// <param name="requestType">the header "Request-Type"</param>
        /// <param name="requestJson">the content</param>
        /// <returns>a result as json object</returns>
        private static async Task<JsonObject> sendInJson(string requestType, JsonObject requestJson)
        {
            var httpClient = new HttpClient();
            httpClient.DefaultRequestHeaders.Add("Accept-Charset", "utf-8");
            httpClient.DefaultRequestHeaders.Add("Request-Type", requestType);

            var request = new HttpRequestMessage(HttpMethod.Post, uri);
            request.Content = new HttpStringContent(requestJson.ToString());
            request.Content.Headers.ContentType =
                new Windows.Web.Http.Headers.HttpMediaTypeHeaderValue("application/json");

            var response = await httpClient.SendRequestAsync(request);
            httpClient.Dispose();

            if (response.IsSuccessStatusCode)
            {
                var returnJson = new JsonObject();
                JsonObject.TryParse(response.Content.ToString(), out returnJson);

                Debug.WriteLine("================got response==========");
                Debug.WriteLine(returnJson);

                return returnJson;
            }

            return null;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="memStream">the stream form of the picture</param>
        /// <returns>A memo generated from the picture</returns>
        public static async Task<Memo> ResolveImage(IRandomAccessStream memStream)
        {
            var httpClient = new HttpClient();
            httpClient.DefaultRequestHeaders.Add("Accept-Charset", "utf-8");
            httpClient.DefaultRequestHeaders.Add("Request-Type", "Resolve-Image");

            var request = new HttpRequestMessage(HttpMethod.Post, uri);
            request.Content = new HttpStreamContent(memStream);
            request.Content.Headers.ContentType =
                new Windows.Web.Http.Headers.HttpMediaTypeHeaderValue("application/octet-stream");

            var response = await httpClient.SendRequestAsync(request);
            if (response.IsSuccessStatusCode)
            {
                var rJson = new JsonObject();
                JsonObject.TryParse(response.Content.ToString(), out rJson);

                Debug.WriteLine("================got response==========");
                Debug.WriteLine(rJson);
            }
            httpClient.Dispose();

            return null;
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">userID, user can't see this, only developers care about this.</param>
        /// <returns>A list of the memos of the user in the server-end</returns>
        public static async Task<List<Memo>> GetAllMemos(string id)
        {
            var requestJson = new JsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(id);

            var returnJson = await sendInJson("Get-Memo-List", requestJson);

            return null;
        }

        
        /// <summary>
        /// 
        /// </summary>
        /// <param name="name">userName, a unique identifier of a user, user can see this.</param>
        /// <param name="password">password</param>
        /// <returns>userID, user can't see this, only developers care about this.</returns>
        public static async Task<string> Login(string name, string password)
        {
            var requestJson = new JsonObject();
            requestJson["userName"] = JsonValue.CreateStringValue(name);
            requestJson["password"] = JsonValue.CreateStringValue(password);

            var returnJson = await sendInJson("Sign-In", requestJson);

            return returnJson["userID"].ToString();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="userName">userName, a unique identifier of a user, user can see this.</param>
        /// <param name="password">password</param>
        /// <returns>userID, user can't see this, only developers care about this.</returns>
        public static async Task<string> SignUp(string userName, string password)
        {
            var requestJson = new JsonObject();
            requestJson["userName"] = JsonValue.CreateStringValue(userName);
            requestJson["password"] = JsonValue.CreateStringValue(password);

            var returnJson = await sendInJson("Sign-Up", requestJson);

            return returnJson["userID"].ToString();
        }

        public static async Task<UserInfo> GetUserInfo(string userID)
        {
            var requestJson = new JsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(userID);

            var returnJson = await sendInJson("Get-User-Info", requestJson);

            return new UserInfo()
            {
                UserID = userID,
                UserName = returnJson["userName"].ToString(),
                EducationInfo = returnJson["educationInfo"].ToString(),
                Signature = returnJson["signature"].ToString()
            };
        }
    }
}

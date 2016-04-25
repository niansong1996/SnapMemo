using SnapMemo.src.model;
using SnapMemo.src.tool;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Storage.Streams;
using Windows.Web.Http;

namespace SnapMemo.src.logic
{
    public static class NetHelper
    {
        private static Uri uri;

        static NetHelper()
        {
            //uri = new Uri("http://139.129.40.103:5678/SnapMemo");
            uri = new Uri("http://139.129.40.103:5678/SnapMemo/servlet/main");
        }

        private static async Task<JsonObject> makeJson(HttpResponseMessage response)
        {
            var contentBuffer = await response.Content.ReadAsBufferAsync();
            var bytes = WindowsRuntimeBufferExtensions.ToArray(contentBuffer);
            var content = Encoding.UTF8.GetString(bytes);
            Debug.WriteLine(content);

            var rJson = new JsonObject();
            JsonObject.TryParse(content, out rJson);

            return rJson;
        }

        private static async Task<HttpResponseMessage> sendInJson(string requestType, JsonObject requestJson)
        {
            var httpClient = new HttpClient();
            httpClient.DefaultRequestHeaders.Add("Accept-Charset", "utf-8");
            httpClient.DefaultRequestHeaders.Add("Request-Type", requestType);

            var request = new HttpRequestMessage(HttpMethod.Post, uri);
            request.Content = new HttpStringContent(requestJson.ToString());
            // Test
            Debug.WriteLine(request.Content.ToString());
            request.Content.Headers.ContentType =
                new Windows.Web.Http.Headers.HttpMediaTypeHeaderValue("application/json");

            var response = await httpClient.SendRequestAsync(request);
            Debug.WriteLine("================got response==========");
            Debug.WriteLine(response.ToString());

            httpClient.Dispose();

            return response;
        }

        /// <summary>
        /// package and send an http request in a specific format
        /// </summary>
        /// <param name="requestType">the header "Request-Type"</param>
        /// <param name="requestJson">the content</param>
        /// <returns>a result as json object</returns>
        private static async Task<JsonObject> sendAndGetInJson(string requestType, JsonObject requestJson)
        {
            var response = await sendInJson(requestType, requestJson);

            if (response.IsSuccessStatusCode)
            {
                var returnJson = await makeJson(response);

                Debug.WriteLine("==============converted to Json===========");
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

            Debug.WriteLine("================got response==========");
            Debug.WriteLine(response.ToString());

            if (response.IsSuccessStatusCode)
            {
                var rJson = await makeJson(response);
                Debug.WriteLine("==============converted to Json===========");
                Debug.WriteLine(rJson);

                return new Memo(rJson);
            }
            httpClient.Dispose();

            return null;
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="id">userID, user can't see this, only developers care about this.</param>
        /// <returns>A list of the memos of the user in the server-end</returns>
        public static async Task<ICollection<Memo>> GetAllMemos(string id)
        {
            var requestJson = new JsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(id);

            var returnJson = await sendAndGetInJson("Get-Memo-List", requestJson);
            var list = returnJson["memo"].GetArray();

            // add in result list
            var resultList = new LinkedList<Memo>();
            foreach(var one in list)
            {
                resultList.AddFirst(new Memo(one.GetObject()));
            }

            return resultList;
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

            var returnJson = await sendAndGetInJson("Sign-In", requestJson);

            return requestJson == null ? null : returnJson["userID"].ToString();
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

            var returnJson = await sendAndGetInJson("Sign-Up", requestJson);

            return requestJson == null ? null : returnJson["userID"].ToString();
        }

        public static async Task<UserInfo> GetUserInfo(string userID)
        {
            var requestJson = new JsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(userID);

            var returnJson = await sendAndGetInJson("Get-User-Info", requestJson);

            return new UserInfo(returnJson);
        }

        public static async Task<string> AddMemo(string userID, Memo memo)
        {
            var requestJson = memo.ToJsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(userID);

            var returnJson = await sendAndGetInJson("Save-Memo", requestJson);

            return JsonString.DeQuotes(returnJson["memoID"].ToString());
         }

        // TODO: return type
        public static async Task<bool> ModifyMemo(Memo memo)
        {
            var requestJson = memo.ToJsonObject();
            requestJson["memoID"] = JsonValue.CreateStringValue(memo.MemoID);

            var returnJson = await sendAndGetInJson("Modify-Memo", requestJson);

            return true;
        }

        public static async Task<bool> DeleteMemo(string memoID)
        {
            var requestJson = new JsonObject();
            requestJson["memoID"] = JsonValue.CreateStringValue(memoID);

            var returnJson = await sendAndGetInJson("Delete-Memo", requestJson);

            return true;
        }

        public static async Task<IRandomAccessStream> GetProfilePicture(string userID)
        {
            var requestJson = new JsonObject();
            requestJson["userID"] = JsonValue.CreateStringValue(userID);

            var response = await sendInJson("Get-Logo", requestJson);

            if (response.IsSuccessStatusCode)
            {
                var stream = new InMemoryRandomAccessStream();
                await response.Content.WriteToStreamAsync(stream);
                return stream;
            }

            return null;
        }
    }
}

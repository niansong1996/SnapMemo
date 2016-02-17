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

        public static async Task<List<Memo>> GetAllMemos(string id)
        {
            var httpClient = new HttpClient();
            httpClient.DefaultRequestHeaders.Add("Accept-Charset", "utf-8");
            httpClient.DefaultRequestHeaders.Add("Request-Type", "Get-Memo-List");

            var jsonObject = new JsonObject();
            jsonObject["userID"] = JsonValue.CreateStringValue(id);
            var request = new HttpRequestMessage(HttpMethod.Post, uri);
            request.Content = new HttpStringContent(jsonObject.ToString());
            request.Content.Headers.ContentType =
                new Windows.Web.Http.Headers.HttpMediaTypeHeaderValue("text/plain");

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

    }
}

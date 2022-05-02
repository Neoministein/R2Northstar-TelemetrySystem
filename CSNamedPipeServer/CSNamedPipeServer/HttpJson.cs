using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http.Json;
using Newtonsoft.Json;

namespace CSNamedPipeServer
{
    public static class UpDownData
    {
        static readonly HttpClient client = new HttpClient();

        // TODO: Use queue instead
        public static async Task<string> PostJsonHttpClient(string _uri, string _send)
        {
            var postResponse = await client.PostAsJsonAsync("http://echo.jsontest.com/key/value/one/two", "");
            postResponse.EnsureSuccessStatusCode();
            var responseMessage = await postResponse.Content.ReadAsStringAsync();
            return responseMessage;
        }

        public static async Task PutJsonHttpClient(string _uri, string _send)
        {
            var putResponse = await client.PutAsJsonAsync(_uri, _send);
            putResponse.EnsureSuccessStatusCode();
        }

        public static async Task<string> GetTaskAsync(string _uri)
        {
            var responseMessage = await client.GetStringAsync(_uri);
            return responseMessage;
        }
    }
}

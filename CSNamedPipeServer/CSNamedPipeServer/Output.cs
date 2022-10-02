using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http.Json;
using Newtonsoft.Json;

namespace CSNamedPipeServer
{
    // TODO: Header send token
    // "Bearer" vor dem token
    public static class Output
    {
        static readonly HttpClient client = new HttpClient();

        public static void Init()
        {
            client.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", GloVars.ArgUserKey);
        }

        // TODO: Use queue instead
        /// <summary>
        /// Post http sends json, recive json
        /// </summary>
        /// <param name="_uri">Url to send to</param>
        /// <param name="_sendJson">Json to send</param>
        /// <returns>Json answer</returns>
        public static async Task<string> PostJsonHttpClient(string _uri, string _sendJson)
        {
            string responseMessage = String.Empty;
            try
            {
                var content = new StringContent(_sendJson, Encoding.UTF8, "application/json");
                HttpResponseMessage postResponse = await client.PostAsync(_uri, content);
                postResponse.EnsureSuccessStatusCode();
                responseMessage = await postResponse.Content.ReadAsStringAsync();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            return responseMessage;
        }

        /// <summary>
        /// Put http sends json
        /// </summary>
        /// <param name="_uri">Url to send to</param>
        /// <param name="_sendJson">Json to send</param>
        /// <returns></returns>
        public static async Task PutJsonHttpClient(string _uri, string _sendJson)
        {
            try
            {
                var content = new StringContent(_sendJson, Encoding.UTF8, "application/json");
                var putResponse = await client.PutAsync(_uri, content);
                putResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        ///// <summary>
        ///// Get http
        ///// </summary>
        ///// <param name="_uri">Url to get from</param>
        ///// <returns>Json answer</returns>
        //public static async Task<string> GetTaskAsync(string _uri)
        //{
        //    var responseMessage = await client.GetFromJsonAsync<string>(_uri);
        //    return responseMessage;
        //}
    }
}

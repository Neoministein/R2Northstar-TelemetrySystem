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
        /// <summary>
        /// Post http sends json, recive json
        /// </summary>
        /// <param name="_uri">Url to send to</param>
        /// <param name="_send">Json to send</param>
        /// <returns>Json answer</returns>
        public static async Task<string> PostJsonHttpClient(string _uri, string _send)
        {
            string responseMessage = String.Empty;
            try
            {
                Console.WriteLine("Start match+ - Send:");
                Console.WriteLine("Url: " + _uri + " | String: " + _send);
                HttpResponseMessage postResponse = await client.PostAsJsonAsync(_uri, _send);
                postResponse.EnsureSuccessStatusCode();
                responseMessage = await postResponse.Content.ReadAsStringAsync();
                Console.WriteLine("Start match- - Recive:");
                Console.WriteLine("String: " + responseMessage);
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
        /// <param name="_send">Json to send</param>
        /// <returns></returns>
        public static async Task PutJsonHttpClient(string _uri, string _send)
        {
            try
            {
                var putResponse = await client.PutAsJsonAsync(_uri, _send);
                putResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }

        /// <summary>
        /// Get http
        /// </summary>
        /// <param name="_uri">Url to get from</param>
        /// <returns>Json answer</returns>
        public static async Task<string> GetTaskAsync(string _uri)
        {
            var responseMessage = await client.GetFromJsonAsync<string>(_uri);
            return responseMessage;
        }
    }
}

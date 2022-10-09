using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;

namespace CSNamedPipeServer
{
    public static class GloVars
    {
        public const LogMode ArgLogMode = LogMode.None;

        public static string ArgUrl;
        public static string ArgUserKey;
        private const int UserKeyLength = 47;

        public static IMessageProcessor GetProcessor()
        {
            return new RestProcessor();
        }

        public static void LoadSettings(string _userKey = "", string _url = "")
        {
            if (IsValidSettings || (!String.IsNullOrWhiteSpace(_userKey) && !String.IsNullOrWhiteSpace(_url)))
            {
                if (String.IsNullOrWhiteSpace(_userKey)) { /*ArgUserKey = ConfigurationManager.AppSettings.Get("userkey");*/ }
                else
                {
                    ArgUserKey = _userKey;
                    //ConfigurationManager.AppSettings.Set("userkey", _userKey);
                }
                if (ArgUserKey.Length != UserKeyLength)
                    throw new InvalidArgumentsException("User key has invalid length");

                if (String.IsNullOrWhiteSpace(_url)) { /*ArgUrl = ConfigurationManager.AppSettings.Get("url");*/ }
                else
                {
                    ArgUrl = _url;
                    if (ArgUrl[ArgUrl.Length - 1] != '/')
                        ArgUrl += '/';
                    //ConfigurationManager.AppSettings.Set("url", _url);
                }
                bool result = Uri.TryCreate(ArgUrl, UriKind.Absolute, out Uri uriResult)
                    && (uriResult.Scheme == Uri.UriSchemeHttp || uriResult.Scheme == Uri.UriSchemeHttps);
                if (!result)
                {
                    throw new InvalidArgumentsException("Invalid Url: " + ArgUrl);
                }
            }
            else
            {
                throw new InvalidArgumentsException("Settings are invalid or do not exsist.");
            }
        }

        private static bool IsValidSettings
        {
            get
            {
                return true;
                // TODO: Read from settings file
                //return Uri.TryCreate(ConfigurationManager.AppSettings.Get("url"), UriKind.Absolute, out Uri uriResult)
                //&& (uriResult.Scheme == Uri.UriSchemeHttp || uriResult.Scheme == Uri.UriSchemeHttps)
                //&& ConfigurationManager.AppSettings.Get("userkey").Length == userKeyLength;
            }
        }
    }
}

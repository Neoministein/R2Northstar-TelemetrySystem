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
        public static string argUrl;
        public static string argUserKey;
        private const int userKeyLength = 47;

        public static void LoadSettings(string _userKey = "", string _url = "")
        {
            if (IsValidSettings || (!String.IsNullOrWhiteSpace(_userKey) && !String.IsNullOrWhiteSpace(_url)))
            {
                if (String.IsNullOrWhiteSpace(_userKey)) { /*argUserKey = ConfigurationManager.AppSettings.Get("userkey");*/ }
                else
                {
                    argUserKey = _userKey;
                    //ConfigurationManager.AppSettings.Set("userkey", _userKey);
                }
                if (argUserKey.Length != userKeyLength)
                    throw new InvalidArgumentsException("User key has invalid length");

                if (String.IsNullOrWhiteSpace(_url)) { /*argUrl = ConfigurationManager.AppSettings.Get("url");*/ }
                else
                {
                    argUrl = _url;
                    if (argUrl[argUrl.Length - 1] != '/')
                        argUrl += '/';
                    //ConfigurationManager.AppSettings.Set("url", _url);
                }
                bool result = Uri.TryCreate(argUrl, UriKind.Absolute, out Uri uriResult)
                    && (uriResult.Scheme == Uri.UriSchemeHttp || uriResult.Scheme == Uri.UriSchemeHttps);
                if (!result)
                {
                    throw new InvalidArgumentsException("Invalid Url: " + argUrl);
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

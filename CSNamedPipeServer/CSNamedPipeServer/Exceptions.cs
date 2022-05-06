using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSNamedPipeServer
{
    /// <summary>
    /// Throw when getting data meant for a different server
    /// </summary>
    public class WrongAnswerException : Exception
    {
        public WrongAnswerException() { }

        public WrongAnswerException(string _message) : base(_message) { }

        public WrongAnswerException(string _message, Exception _inner) : base(_message, _inner) { }
    }
}

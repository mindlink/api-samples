using System.Web;

namespace FoundationApiDemo
{
    using System;
    using System.Net;
    using System.Threading;

    /// <summary>
    /// Demonstrates changing composing state.
    /// </summary>
    class Demo9 : Demo8
    {
        /// <summary>
        /// Runs the demo.
        /// </summary>
        /// <param name="baseUri">
        /// The FAPI base URL.
        /// </param>
        /// <param name="user">
        /// The FAPI username.
        /// </param>
        /// <param name="pass">
        /// The FAPI password.
        /// </param>
        /// <param name="agentId">
        /// The agent ID to use.
        /// </param>
        public static void RunDemo9(string baseUri, string user, string pass, string agentId)
        {
            var token = string.Empty;
            var lastEvent = 0L;

            do
            {
                if (string.IsNullOrEmpty(token))
                {
                    token = GetToken(baseUri, user, pass, agentId);
                }

                try
                {
                    var events = GetResponse(baseUri + "/Collaboration/v1/Events?last-event=" + lastEvent + "&types=message", token);

                    foreach (var evt in events)
                    {
                        lastEvent = Math.Max(lastEvent, (long)evt.EventId);
                        Console.WriteLine();
                        Console.WriteLine("Received message from channel: {0}", evt.ChannelId);
                        Console.WriteLine("Starting composing...");

                        GetResponse(baseUri + "/Collaboration/V1/Channels/" + HttpUtility.UrlEncode(evt.ChannelId.ToString()) + "/Me", token, new { IsComposing = true });

                        Thread.Sleep(3000);

                        Console.WriteLine("Stopping composing...");
                        GetResponse(baseUri + "/Collaboration/v1/Channels/" + HttpUtility.UrlEncode(evt.ChannelId.ToString()) + "/Me", token, new { IsComposing = false });
                    }
                }
                catch (WebException ex)
                {
                    if (((HttpWebResponse)ex.Response).StatusCode == HttpStatusCode.Unauthorized)
                    {
                        token = string.Empty;
                    }
                    else
                    {
                        throw;
                    }
                }
            }
            while (true);
        }
    }
}

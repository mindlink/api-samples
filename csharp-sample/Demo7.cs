using System.Web;

namespace FoundationApiDemo
{
    using System;
    using System.Net;

    /// <summary>
    /// Demonstrates streaming events.
    /// </summary>
    internal class Demo7 : Demo6
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
        public static void RunDemo7(string baseUri, string user, string pass, string agentId)
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
                        Console.WriteLine("Channel: {0}", evt.ChannelId);
                        Console.WriteLine(" Sender: {0}", evt.Sender);
                        Console.WriteLine("  Alias: {0}", evt.SenderAlias);
                        Console.WriteLine("Content: {0}", evt.Content);

                        if (!evt.Content.ToString().StartsWith("You said: "))
                        {
                            GetResponse(baseUri + "/Collaboration/v1/Channels/" + HttpUtility.UrlEncode(evt.ChannelId) + "/Messages", token, new { IsAlert = false, Text = "You said: " + evt.Content });
                        }
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

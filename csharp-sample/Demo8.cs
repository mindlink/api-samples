namespace FoundationApiDemo
{
    using System;

    /// <summary>
    /// Demonstrates sending and receiving message parts.
    /// </summary>
    internal class Demo8 : Demo7
    {
        /// <summary>
        /// Runs the demo.
        /// </summary>
        /// <param name="baseUrl">
        /// The base FAPI url.
        /// </param>
        /// <param name="password">
        /// The password.
        /// </param>
        /// <param name="provisionedUser">
        /// The provisioned user.
        /// </param>
        /// <param name="agentId">
        /// The agent id.
        /// </param>
        /// <param name="channelId">
        /// The channel id.
        /// </param>
        public static void RunDemo8(string baseUrl, string password, string provisionedUser, string agentId, string channelId)
        {
            var token = GetToken(baseUrl, provisionedUser, password, agentId);

            var plainTextMessagePart = new { __type = "PlainTextMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Text = "This is a test"};
            var hyperlinkMessagePart = new { __type = "HyperlinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Text = "Go here!", Url = "www.example.com" };
            var channelLinkMessagePart = new { __type = "ChannelLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", ChannelName = "This Group", ChannelId = channelId };
            var hashtagMessagePart = new { __type = "HashtagMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Hashtag = "#hashtag" };

            var messageParts = new dynamic[] { plainTextMessagePart, hyperlinkMessagePart, channelLinkMessagePart, hashtagMessagePart };

            GetResponse(baseUrl + "/Collaboration/v1/Channels/" + channelId + "/Messages", token, new { IsAlert = false, MessageParts = messageParts });

            Console.WriteLine("Successfully sent message in channel: {0}.", channelId);

            var events = GetResponse(baseUrl + "/Collaboration/v1/Events?last-event=" + 0L + "&types=message", token);

            foreach (var evt in events)
            {
                if (evt.Content.ToString().StartsWith("This is a test"))
                {
                    Console.WriteLine("Received message event with message parts: {0}", evt.MessageParts);
                }
            }
        }
    }
}

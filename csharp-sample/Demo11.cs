﻿namespace FoundationApiDemo
{
    using System;
    using System.Web;

    /// <summary>
    /// Demonstrates sending and receiving message parts with security of context.
    /// </summary>
    internal class Demo11 : Demo7
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
        public static void RunDemo11(string baseUrl, string password, string provisionedUser, string agentId, string channelId)
        {
            var messageIndex = 1;
            while (true)
            {
                var token = GetToken(baseUrl, provisionedUser, password, agentId);

                var plainTextMessagePart = new { __type = "PlainTextMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Text = "This is test message " + messageIndex };
                var hyperlinkMessagePart = new { __type = "HyperlinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Text = "Go here!", Url = "www.example.com" };
                var channelLinkMessagePart = new { __type = "ChannelLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", ChannelName = "This Group", ChannelId = channelId };
                var hashtagMessagePart = new { __type = "HashtagMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", Hashtag = "#hashtag" };
                var codeBlockMessagePart = new { __type = "CodeBlockMessagePart:http://schemas.fcg.im/foundation/v1/collaboration", CodeBlock = "Console.WriteLine(\"Hello World!\");" };
                messageIndex++;
                var messageParts = new dynamic[] { plainTextMessagePart, hyperlinkMessagePart, channelLinkMessagePart, hashtagMessagePart, codeBlockMessagePart };
                var metadata = new
                {
                    __type = "SecurityContext:http://schemas.fcg.im/foundation/v1/collaboration",
                    Id = "coi2",
                    DisplayText = "COI 2",
                    Description = "This is the COI 2",
                    ForegroundColor = "#000000",
                    BackgroundColor = "#FF7E77"
                };

                GetResponse(baseUrl + "/Collaboration/v1/Channels/" + HttpUtility.UrlEncode(channelId) + "/Messages", token, new { IsAlert = false, MessageParts = messageParts, SecurityContext = metadata });

                Console.WriteLine("Successfully sent message in channel: {0}.", channelId);

                System.Threading.Thread.Sleep(TimeSpan.FromMinutes(1));
            }
        }
    }
}

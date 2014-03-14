namespace FoundationApiDemo
{
    using System;

    /// <summary>
    /// Demonstrates how to use the collaboration service. Authenticate and then get channels (for example).
    /// </summary>
    internal class Demo6 : Demo5
    {
        /// <summary>
        /// Runs the demo.
        /// </summary>
        /// <param name="baseUrl">
        /// The base url.
        /// </param>
        /// <param name="user">
        /// The user with which to authenticate.
        /// </param>
        /// <param name="password">
        /// The password.
        /// </param>
        /// <param name="agentId">
        /// The agent ID.
        /// </param>
        public static void RunDemo6(string baseUrl, string user, string password, string agentId)
        {
            var token = GetToken(baseUrl, user, password, agentId);
            var channels = GetResponse(baseUrl + "/Collaboration/V1/Channels", token);

            foreach (var channel in channels)
            {
                Console.WriteLine("Channel {0}:", channel.Id);
                Console.WriteLine("\t     Can accept files: {0}", channel.CanAcceptFiles);
                Console.WriteLine("\t     Description: {0}", channel.Desciption);
                Console.WriteLine("\t     Display name: {0}", channel.DisplayName);
                Console.WriteLine("\t     Subject: {0}", channel.Subject);

                Console.WriteLine();
            }
        }
    }
}
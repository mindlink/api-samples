namespace FoundationApiDemo
{
    using System;

    /// <summary>
    /// Authorizes a user against an agent.
    /// </summary>
    internal class Demo5 : Demo4
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
        public static void RunDemo5(string baseUrl, string user, string password, string agentId)
        {
            Console.WriteLine("Got token for agent {0}: {1}", agentId, GetToken(baseUrl, user, password, agentId));
        }
    }
}
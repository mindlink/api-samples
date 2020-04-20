namespace FoundationApiDemo
{
    using System;

    /// <summary>
    /// Foundation API demo program.
    /// </summary>
    internal class Program
    {
        /// <summary>
        /// The base URL of the foundation API.
        /// </summary>
        private const string ApiUrl = "http://fapi.company.com";

        /// <summary>
        /// The username to use to connect to to the FAPI.
        /// </summary>
        private const string Username = "company\\masterUser";

        /// <summary>
        /// The password to provide when connecting to the FAPI.
        /// </summary>
        private const string Password = "password";

        /// <summary>
        /// The user to create using the provisioning service.
        /// </summary>
        private const string ProvisioningUser = "company\\provisionedUser";

        /// <summary>
        /// The ID of the user to create.
        /// </summary>
        private const string ProvisioningUserId = "user1";

        /// <summary>
        /// The ID of the agent to create.
        /// </summary>
        private const string AgentId = "agent1";

        /// <summary>
        /// The username of the agent to create.
        /// </summary>
        private const string AgentUsername = "sip:agent@company.com";

        /// <summary>
        /// The channel to provision the agent in.
        /// </summary>
        private const string ChannelId = "chat-room:guid";

        /// <summary>
        /// Program entry point.
        /// </summary>
        /// <param name="args">
        /// Command line arguments.
        /// </param>
        public static void Main(string[] args)
        {
            Demo1.RunDemo1(ApiUrl);
            Demo2.RunDemo2(ApiUrl, Username, Password);
            Demo3.RunDemo3(ApiUrl, Username, Password);
            Demo4.RunDemo4(ApiUrl, Username, Password, ProvisioningUserId, ProvisioningUser, AgentId, AgentUsername, ChannelId);
            Demo5.RunDemo5(ApiUrl, ProvisioningUser, Password, AgentId);
            Demo6.RunDemo6(ApiUrl, ProvisioningUser, Password, AgentId);
            Demo7.RunDemo7(ApiUrl, ProvisioningUser, Password, AgentId);
            Demo8.RunDemo8(ApiUrl, Password, ProvisioningUser, AgentId, ChannelId);
            Demo9.RunDemo9(ApiUrl, ProvisioningUser, Password, AgentId);

            Console.ReadLine();
        }
    }
}

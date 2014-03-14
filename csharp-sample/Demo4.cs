namespace FoundationApiDemo
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Net;

    using Newtonsoft.Json;

    /// <summary>
    /// Demonstrates how to create a user and an agent for that user.
    /// </summary>
    internal class Demo4 : Demo3
    {
        /// <summary>
        /// Runs the demo.
        /// </summary>
        /// <param name="baseUrl">
        /// The base FAPI url.
        /// </param>
        /// <param name="user">
        /// The user with which to authenticate.
        /// </param>
        /// <param name="password">
        /// The password.
        /// </param>
        /// <param name="provisionedUserId">
        /// The provisioned user id.
        /// </param>
        /// <param name="provisionedUser">
        /// The provisioned user.
        /// </param>
        /// <param name="agentId">
        /// The agent id.
        /// </param>
        /// <param name="agentUserName">
        /// The agent user name.
        /// </param>
        /// <param name="channelId">
        /// The channel id.
        /// </param>
        public static void RunDemo4(string baseUrl, string user, string password, string provisionedUserId, string provisionedUser, string agentId, string agentUserName, string channelId)
        {
            var token = GetToken(baseUrl, user, password);
            var response = PutUser(baseUrl + "/Provisioning/V1/Users/" + provisionedUserId, token, provisionedUserId, provisionedUser);

            if (response)
            {
                Console.WriteLine("Successfully added user {0} with ID: {1}", provisionedUser, provisionedUserId);
            }

            response = PutAgent(baseUrl + "/Provisioning/V1/Agents/" + agentId, token, provisionedUserId, agentId, agentUserName, channelId);

            if (response)
            {
                Console.WriteLine("Successfully added agent {0} with ID: {1}", agentUserName, agentId);
            }
        }

        /// <summary>
        /// Adds or updates the user with the specified user ID.
        /// </summary>
        /// <param name="url">
        /// The url of the users resource collection.
        /// </param>
        /// <param name="token">
        /// The token.
        /// </param>
        /// <param name="provisionedUserId">
        /// The provisioned user id.
        /// </param>
        /// <param name="provisionedUser">
        /// The provisioned user.
        /// </param>
        /// <returns>
        /// Whether the request was successful.
        /// </returns>
        protected static bool PutUser(string url, string token, string provisionedUserId, string provisionedUser)
        {
            var client = (HttpWebRequest)WebRequest.Create(url);
            client.Method = "PUT";
            client.ContentType = "application/json";
            client.Accept = "application/json";
            client.Headers.Add(HttpRequestHeader.Authorization, "FCF " + token);

            var requestStream = client.GetRequestStream();
            using (var writer = new StreamWriter(requestStream))
            {
                writer.Write(JsonConvert.SerializeObject(new { UserId = provisionedUserId, Username = provisionedUser }));
                writer.Close();
            }

            var response = client.GetResponse();

            return ((HttpWebResponse)response).StatusCode == HttpStatusCode.OK;
        }

        /// <summary>
        /// Adds or updates an agent for a user.
        /// </summary>
        /// <param name="url">
        /// The url or the agent resource collection.
        /// </param>
        /// <param name="token">
        /// The token.
        /// </param>
        /// <param name="provisionedUserId">
        /// The provisioned user ID to add the agent to.
        /// </param>
        /// <param name="agentId">
        /// The agent ID.
        /// </param>
        /// <param name="agentUsername">
        /// The agent username.
        /// </param>
        /// <param name="channelId">
        /// The channel ID to add to the agent.
        /// </param>
        /// <returns>
        /// Whether the request was successful.
        /// </returns>
        protected static bool PutAgent(string url, string token, string provisionedUserId, string agentId, string agentUsername, string channelId)
        {
            var client = (HttpWebRequest)WebRequest.Create(url);
            client.Method = "PUT";
            client.ContentType = "application/json";
            client.Accept = "application/json";
            client.Headers.Add(HttpRequestHeader.Authorization, "FCF " + token);

            var requestStream = client.GetRequestStream();
            using (var writer = new StreamWriter(requestStream))
            {
                writer.Write(JsonConvert.SerializeObject(
                    new
                        {
                            Id = agentId, 
                            UserName = agentUsername, 
                            Channels = new List<object> { new { Id = channelId, State = 0 } }, 
                            MetaData = new Dictionary<string, string>(), 
                            CanProvision = true,
                            State = 0,
                            Users = new List<string> { provisionedUserId }
                        }));
                writer.Close();
            }

            var response = client.GetResponse();

            return ((HttpWebResponse)response).StatusCode == HttpStatusCode.OK;
        }
    }
}
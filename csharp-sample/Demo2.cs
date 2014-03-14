namespace FoundationApiDemo
{
    using System;
    using System.IO;
    using System.Net;

    using Newtonsoft.Json;

    /// <summary>
    /// Authentication example.
    /// </summary>
    internal class Demo2 : Demo1
    {
        /// <summary>
        /// Runs this demo.
        /// </summary>
        /// <param name="baseUrl">
        /// The base FAPI url.
        /// </param>
        /// <param name="user">
        /// The user to authenticate with.
        /// </param>
        /// <param name="password">
        /// The password to authenticate with.
        /// </param>
        public static void RunDemo2(string baseUrl, string user, string password)
        {
            Console.WriteLine("Got token: {0}", GetToken(baseUrl, user, password));
        }

        /// <summary>
        /// Gets a token from the Foundation API.
        /// </summary>
        /// <param name="baseUrl">
        /// The base url for the API.
        /// </param>
        /// <param name="user">
        /// The username to use.
        /// </param>
        /// <param name="password">
        /// The password to use.
        /// </param>
        /// <param name="agent">
        /// The agent to authenticate as (optional for superusers).
        /// </param>
        /// <returns>
        /// The token retrieved from the FAPI.
        /// </returns>
        protected static string GetToken(string baseUrl, string user, string password, string agent = "")
        {
            var client = (HttpWebRequest)WebRequest.Create(baseUrl + "/Authentication/v1/Tokens");
            client.Method = "POST";
            client.ContentType = "application/json";
            client.Accept = "application/json";

            var requestStream = client.GetRequestStream();
            using (var writer = new StreamWriter(requestStream))
            {
                writer.Write(JsonConvert.SerializeObject(new { Username = user, Password = password, AgentId = agent }));
            }

            var response = client.GetResponse();
            var responseStream = response.GetResponseStream();
            string token;
            using (var reader = new StreamReader(responseStream))
            {
                token = JsonConvert.DeserializeObject<string>(reader.ReadToEnd());
            }

            return token;
        }
    }
}

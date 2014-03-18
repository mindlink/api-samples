namespace FoundationApiDemo
{
    using System;
    using System.IO;
    using System.Net;

    using Newtonsoft.Json;

    /// <summary>
    /// Demonstrates how to pass the token in a HTTP header.
    /// </summary>
    internal class Demo3 : Demo2
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
        public static void RunDemo3(string baseUrl, string user, string password)
        {
            var token = GetToken(baseUrl, user, password);
            var response = GetResponse(baseUrl + "/Provisioning/v1/Agents", token);

            foreach (var agent in response)
            {
                Console.WriteLine("Agent {0}:", agent.Id);
                Console.WriteLine("\t     Username: {0}", agent.UserName);
                Console.WriteLine("\tCan provision: {0}", agent.CanProvision);
                Console.WriteLine("\t        Users: {0}", string.Join(", ", agent.Users));
   
                Console.WriteLine();
            }
        }

        /// <summary>
        /// Makes an authenticated request to the FAPI and gets the response.
        /// </summary>
        /// <param name="url">
        /// The URL to request.
        /// </param>
        /// <param name="token">
        /// The token to pass.
        /// </param>
        /// <param name="body">
        /// The body of the request, if any.
        /// </param>
        /// <returns>
        /// The deserialized response.
        /// </returns>
        protected static dynamic GetResponse(string url, string token, object body = null)
        {
            var client = (HttpWebRequest)WebRequest.Create(url);
            client.Method = body == null ? "GET" : "POST";
            client.ContentType = "application/json";
            client.Accept = "application/json";
            client.Headers.Add(HttpRequestHeader.Authorization, "FCF " + token);

            if (body != null)
            {
                var requestStream = client.GetRequestStream();
                using (var writer = new StreamWriter(requestStream))
                {
                    writer.Write(JsonConvert.SerializeObject(body));
                }
            }

            var response = client.GetResponse();
            var responseStream = response.GetResponseStream();
            
            dynamic responseBody;
            using (var reader = new StreamReader(responseStream))
            {
                responseBody = JsonConvert.DeserializeObject<dynamic>(reader.ReadToEnd());
            }

            return responseBody;
        }
    }
}

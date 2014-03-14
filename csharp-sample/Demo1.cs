namespace FoundationApiDemo
{
    using System;
    using System.Net;

    /// <summary>
    /// Demonstrates how to perform a basic HTTP request.
    /// </summary>
    internal class Demo1
    {
        /// <summary>
        /// Runs this demo.
        /// </summary>
        /// <param name="baseUrl">
        /// The base FAPI url.
        /// </param>
        public static void RunDemo1(string baseUrl)
        {
            try
            {
                var client = WebRequest.Create(baseUrl + "/Provisioning/v1/Agents");
                client.GetResponse();
            }
            catch (WebException ex)
            {
                var response = (HttpWebResponse)ex.Response;
                Console.WriteLine("Error - status code {0} received ({1})", (int)response.StatusCode, response.StatusCode);
            }
        }
    }
}

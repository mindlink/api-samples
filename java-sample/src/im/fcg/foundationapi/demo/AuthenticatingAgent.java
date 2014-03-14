package im.fcg.foundationapi.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A basic agent which facilitates making authenticated requests to the
 * Foundation API. The agent internally handles authentication and maintenance
 * of the API's session token. Authentication is only performed when a page
 * is requested - if the given credentials are not valid, an error will only
 * occur when a request is made.
 */
public class AuthenticatingAgent {

    /** The method to call to authenticate. */
    private static final String AUTH_METHOD = "/Authentication/v1/Tokens";

    /** The current token provided by the API. */
    private String token;

    /** The base URL to use to connect to the API. */
    private final String baseUrl;
    /** The username to use when authenticating to the API. */
    private final String username;
    /** The password to use when authenticating to the API. */
    private final String password;
    /** The agent to request when authenticating to the API. */
    private final String agent;

    /**
     * Creates a new {@link AuthenticatingAgent}.
     *
     * @param baseUrl The base address for the Foundation API. The agent will
     * append method names automatically. For example, a base URL of
     * <code>http://api.company.com</code> will result in URLs constructed such
     * as <code>http://api.company.com/Authentication/v1/Tokens</code>.
     * @param username The username to give to the API when authenticating
     * @param password The username to give to the API when authenticating
     * @param agent The ID of the agent to use. May be an empty string if
     * authenticating as a super user.
     */
    public AuthenticatingAgent(final String baseUrl, final String username,
            final String password, final String agent) {
        super();
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.agent = agent;
    }

    /**
     * Sends a request to the Foundation API and returns the textual content
     * of the response. Will attempt to authenticate if the agent has not
     * previously done so, or if the request fails with a 401 authentication
     * error.
     *
     * @param address The address of the API method to call
     * (e.g. <code>/Authentication/v1/Tokens</code>) including any GET
     * parameters.
     * @param method The HTTP method to use (GET, POST, etc).
     * @param body The body to send with the request (or <code>null</code>
     * for no body).
     * @return The body of the API response
     * @throws IOException If the API cannot be reached, or authentication fails
     * @see #getResponse(String, String, String, boolean)
     */
    public String getResponse(final String address, final String method,
            final String body) throws IOException {
        return getResponse(address, method, body, true);
    }

    /**
     * Sends a request to the Foundation API and returns the textual content
     * of the response. Will optionally attempt to authenticate if the agent has
     * not previously done so, or if the request fails with a 401 authentication
     * error.
     *
     * @param address The address of the API method to call
     * (e.g. <code>/Authentication/v1/Tokens</code>) including any GET
     * parameters.
     * @param method The HTTP method to use (GET, POST, etc).
     * @param body The body to send with the request (or <code>null</code>
     * for no body).
     * @param authenticate Whether or not to attempt to authenticate
     * @return The body of the API response
     * @throws IOException If the API cannot be reached, or authentication fails
     */
    public String getResponse(final String address, final String method,
            final String body, final boolean authenticate) throws IOException {
        if (authenticate && token == null) {
            authenticate();
        }

        final URL url = new URL(baseUrl + address);
        final HttpURLConnection connection
                = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            connection.setRequestProperty("Authorization", "FCF " + token);
        }

        try {
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length",
                        String.valueOf(body.length()));
                OutputStreamWriter out
                        = new OutputStreamWriter(connection.getOutputStream());
                out.write(body);
                out.close();
            }

            final InputStreamReader in
                    = new InputStreamReader(connection.getInputStream());
            final BufferedReader reader = new BufferedReader(in);

            final StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        } catch (IOException ex) {
            if (authenticate && connection.getResponseCode() == 401) {
                authenticate();
                return getResponse(address, method, body);
            }

            throw ex;
        }
    }

    /**
     * Authenticates the agent by attempting to POST to the
     * <code>/Authentication/v1/Tokens</code> resource.
     *
     * @throws IOException If the authentication request cannot be completed,
     * or if the JSON payload/response can't be built/parsed.
     */
    private void authenticate() throws IOException {
        token = null;

        try {
            JSONObject object = new JSONObject();
            object.put("Username", username);
            object.put("Password", password);
            object.put("AgentId", agent);

            JSONTokener tokener = new JSONTokener(getResponse(AUTH_METHOD,
                    "POST", object.toString(), false));
            token = tokener.nextString(tokener.nextClean());
        } catch (JSONException ex) {
            throw new IOException("Unable to authenticate", ex);
        }
    }

    /**
     * Converts the given WCF Dictionary-style JSON array into a {@link Map}.
     *
     * @param object The object to be converted
     * @return A map containing all keys and values of the given JSON array
     * converted to {@link String}s. If a <code>null</code> object was passed,
     * an empty map will be returned.
     * @throws JSONException If an array element can't be parsed
     */
    protected Map<String, String> getMap(final JSONArray array)
            throws JSONException {
        final Map<String, String> res = new HashMap<String, String>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                res.put(object.getString("Key"), object.getString("Value"));
            }
        }

        return res;
    }
}

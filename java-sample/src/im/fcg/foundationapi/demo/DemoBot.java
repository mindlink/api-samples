package im.fcg.foundationapi.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import im.fcg.foundationapi.demo.provisioning.ProvisionedChannel;
import im.fcg.foundationapi.demo.provisioning.ProvisioningAgent;
import im.fcg.foundationapi.demo.streaming.StreamingCollaborationAgent;

/**
 * A simple interactive bot to demonstrate using the Foundation API. Interaction
 * with the API is performed through the {@link SimpleCollaborationAgent},
 * {@link StreamingCollaborationAgent} and {@link ProvisioningAgent} classes.
 * Authentication is handled by the {@link AuthenticatingAgent} superclass.
 */
public class DemoBot {

    /**
     * Main entry point.
     *
     * @param args Command line arguments
     * @throws IOException If the API can't be reached
     */
    public static void main(final String[] args) throws IOException {
        if (args.length < 3 || args.length > 4) {
            System.err.println("Usage: demobot <url> <user> <pass> [agent]");
            System.err.println();
            System.err.println("   <url> - base URL for foundation API");
            System.err.println("  <user> - username to use for authentication");
            System.err.println("  <pass> - password to use for authentication");
            System.err.println(" [agent] - agent ID to request");
            System.err.println();
            System.err.println("Password may be specified as '-' to read from "
                    + "standard input.");
            System.err.println();
            System.err.println("If agent is omitted a superuser account must be"
                    + "used, and some functions will");
            System.err.println("be unavailable.");
            System.err.println();
            return;
        }

        final InputStreamReader inputReader = new InputStreamReader(System.in);
        final BufferedReader bufferedReader = new BufferedReader(inputReader);

        final String url = args[0];
        final String user = args[1];
        final String agent = args.length > 3 ? args[3] : null;

        final String password;
        if (args[2].equals("-")) {
            System.out.print("Enter password: ");
            password = bufferedReader.readLine();
        } else {
            password = args[2];
        }

        final StreamingCollaborationAgent collabAgent
                = new StreamingCollaborationAgent(url, user, password, agent);
        final ProvisioningAgent provAgent
                = new ProvisioningAgent(url, user, password, agent);

        while (true) {
            System.out.println();
            System.out.println("Functions: channels, send, exit");
            System.out.print("Select option: ");

            final String option = bufferedReader.readLine().toLowerCase();

            if ("channels".equals(option)) {
                doChannels(collabAgent, provAgent, agent, bufferedReader);
            } else if ("send".equals(option)) {
                doSend(collabAgent, bufferedReader);
            } else if ("exit".equals(option)) {
                break;
            } else {
                System.out.println("Unrecognised option");
            }
        }
    }

    private static void doChannels(final StreamingCollaborationAgent cAgent,
            final ProvisioningAgent pAgent, final String agentId,
            final BufferedReader reader)
            throws IOException {
        System.out.println();
        System.out.println("Channel functions: list, search, add, remove");
        System.out.print("Select option: ");

        final String option = reader.readLine().toLowerCase();

        if ("list".equals(option)) {
            System.out.println();

            for (Channel channel : cAgent.getChannels()) {
                System.out.println(channel.getDisplayName());
                System.out.println("\t         ID: " + channel.getId());
                System.out.println("\t    Subject: " + channel.getSubject());
                System.out.println("\tDescription: " + channel.getDescription());
                System.out.println("\t     E-mail: " + channel.getEmailAddress());
                System.out.println();
            }
        } else if ("search".equals(option)) {
            System.out.print("Enter search terms: ");
            final String terms = reader.readLine();
            System.out.println();

            for (Map.Entry<String, String> entry : pAgent.findChannels(terms).entrySet()) {
                System.out.println(entry.getKey());
                System.out.println("\t" + entry.getValue());
                System.out.println();
            }
        } else if ("add".equals(option)) {
            System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();

            pAgent.addChannel(agentId, new ProvisionedChannel(channel));

            System.out.println("Added!");
        } else if ("remove".equals(option)) {
            System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();

            pAgent.deleteChannel(agentId, channel);

            System.out.println("Removed!");
        } else {
            System.out.println("Unrecognised option");
        }
    }

    private static void doSend(final SimpleCollaborationAgent agent,
            final BufferedReader reader) throws IOException {
        System.out.println();
        System.out.println("Send functions: message, alert, story");
        System.out.print("Select option: ");

        final String option = reader.readLine().toLowerCase();

        if ("message".equals(option)) {
            System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();
            System.out.print("Enter message: ");
            final String message = reader.readLine();

            agent.sendMessage(channel, message);
            System.out.println();
            System.out.println("Sent");
        } else if ("alert".equals(option)) {
            System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();
            System.out.print("Enter message: ");
            final String message = reader.readLine();

            agent.sendAlert(channel, message);
            System.out.println();
            System.out.println("Sent");
        } else if ("story".equals(option)) {
            System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();
            System.out.print("Enter subject: ");
            final String subject = reader.readLine();
            System.out.println("Enter message. Enter \"EOF\" on a new line to end.");
            final StringBuilder builder = new StringBuilder();
            String line = "";

            while (!"EOF".equals(line.trim())) {
                if (builder.length() > 0) {
                    builder.append("\r\n");
                }

                builder.append(line);
                line = reader.readLine();
            }

            agent.sendStory(channel, subject, builder.toString());
            System.out.println();
            System.out.println("Sent");
        }
    }

}


package com.mindlinksoft.foundationapi.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mindlinksoft.foundationapi.demo.management.ChannelPrivacy;
import com.mindlinksoft.foundationapi.demo.management.ManagedCategory;
import com.mindlinksoft.foundationapi.demo.management.ManagedChannel;
import com.mindlinksoft.foundationapi.demo.management.ManagementAgent;
import com.mindlinksoft.foundationapi.demo.provisioning.ProvisionedChannel;
import com.mindlinksoft.foundationapi.demo.provisioning.ProvisioningAgent;
import com.mindlinksoft.foundationapi.demo.streaming.Event;
import com.mindlinksoft.foundationapi.demo.streaming.EventListener;
import com.mindlinksoft.foundationapi.demo.streaming.EventType;
import com.mindlinksoft.foundationapi.demo.streaming.StreamingCollaborationAgent;

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
        final ManagementAgent managementAgent
                = new ManagementAgent(url, user, password, agent);

        while (true) {
            System.out.println();
            System.out.println("Functions: channels, send, history, events, manage, exit");
            System.out.print("Select option: ");

            final String option = bufferedReader.readLine().toLowerCase();

            if ("channels".equals(option)) {
                doChannels(collabAgent, provAgent, agent, bufferedReader);
            } else if ("send".equals(option)) {
                doSend(collabAgent, bufferedReader);
            } else if ("history".equals(option)) {
            	doHistory(collabAgent, bufferedReader);
            } else if ("events".equals(option)) {
                doEvents(collabAgent, bufferedReader);
            } else if ("manage".equals(option)) {
                doManage(managementAgent, bufferedReader);
            } else if ("exit".equals(option)) {
                break;
            } else {
                System.out.println("Unrecognised option");
            }
        }
    }

    private static void doManage(final ManagementAgent mAgent, final BufferedReader reader) throws IOException {
        System.out.println();

        while (true) {
            System.out.println("Management functions: categories, channels, members, exit");
            System.out.print("Select option: ");
            final String option = reader.readLine().toLowerCase();

            if ("categories".equals(option)) {
                System.out.println();

                for (final ManagedCategory category : mAgent.getCategories()) {
                    System.out.println(category.getName());
                    System.out.println("\t         ID: " + category.getId());
                    System.out.println();
                }
            } else if ("channels".equals(option)) {
                doManageChannels(mAgent, reader);
            } else if ("members".equals(option)) {
                doManageMembers(mAgent, reader);
            } else if ("exit".equals(option)) {
                break;
            }
        }
    }

    private static void doManageChannels(final ManagementAgent mAgent, final BufferedReader reader) throws IOException {
        System.out.println();

        while (true) {
            System.out.println("Channel management functions: get, create, delete, exit");
            System.out.print("Select option: ");
            final String option = reader.readLine().toLowerCase();

            if ("get".equals(option)) {
                System.out.println();

                for (final ManagedChannel channel : mAgent.getManagedChannels()) {
                    System.out.println(channel.getName());
                    System.out.println("\t         ID: " + channel.getId());
                    System.out.println("\t         Privacy: " + channel.getPrivacy());
                    System.out.println();
                }
            } else if ("create".equals(option)) {
                System.out.print("Enter channel name: ");
                final String name = reader.readLine();

                System.out.print("Enter category ID: ");
                final String categoryId = reader.readLine();

                System.out.print("Enter channel description: ");
                final String description = reader.readLine();

                System.out.print("Enter privacy (Open, Closed, Secret): ");
                final ChannelPrivacy privacy = toEnum(
                    ChannelPrivacy.class, reader.readLine(), ChannelPrivacy.Open);

                final LinkedList<String> members = new LinkedList<>();

                if (privacy != ChannelPrivacy.Open) {
                    System.out.println("Enter members (enter blank line when done):");
                    String next = reader.readLine();
                    
                    while (next != null && next.length() > 0) {
                        members.add(next);

                        next = reader.readLine();
                    }
                }

                final String channelId = mAgent.addManagedChannel(name, categoryId, description, privacy, members);

                System.out.println("Created channel with ID: " + channelId);
            } else if ("delete".equals(option)) {
                System.out.print("Enter channel ID: ");
                final String id = reader.readLine();

                mAgent.deleteManagedChannel(id);
                System.out.println("Deleted channel with ID: " + id);
            } else if ("exit".equals(option)) {
                break;
            }
        }
    }

    private static void doManageMembers(final ManagementAgent mAgent, final BufferedReader reader) throws IOException {
        System.out.println();

        while (true) {
            System.out.println("Channel member management functions: get, set, exit");
            System.out.print("Select option: ");
            final String option = reader.readLine().toLowerCase();

            if ("get".equals(option)) {
                System.out.print("Enter channel ID: ");
                final String id = reader.readLine();
                System.out.println();

                final Collection<String> members = mAgent.getChannelMembers(id);

                System.out.println("Members:");
                for (final String member : members) {
                    System.out.println(member);
                    System.out.println();
                }
            } else if ("set".equals(option)) {
                System.out.print("Enter channel ID: ");
                final String id = reader.readLine();

                final LinkedList<String> members = new LinkedList<>();
                System.out.println("Enter members (enter blank line when done):");
                String next = reader.readLine();
                
                while (next != null && next.length() > 0) {
                    members.add(next);

                    next = reader.readLine();
                }

                mAgent.setChannelMembers(id, members);

                System.out.println("Updated members of channel with ID: " + id);
            } else if ("exit".equals(option)) {
                break;
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

            for (final Channel channel : cAgent.getChannels()) {
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

            for (final Map.Entry<String, String> entry : pAgent.findChannels(terms).entrySet()) {
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
        System.out.println("Send functions: message, messageparts, alert, story");
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
        } else if ("messageparts".equals(option)) {
        	System.out.println();
        	System.out.println("(Note: This feature only applies to API v18.6 and above)");
        	System.out.print("Enter channel ID: ");
            final String channel = reader.readLine();

            agent.sendMessageParts(channel);
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
    
    private static void doHistory(final SimpleCollaborationAgent agent,
            final BufferedReader reader) throws IOException, NumberFormatException {
        System.out.println();
        System.out.print("Enter channel ID: ");
        final String channelId = reader.readLine();
        System.out.print("Number of messages: ");
        final String messageCountString = reader.readLine();

        System.out.print("Get messages before token (blank for latest messages): ");
        final String beforeToken = reader.readLine();
        
        final int messageCount = Integer.parseInt(messageCountString);

        final List<Message> historyMessages = agent.getChannelHistory(channelId, messageCount, beforeToken);
        
        System.out.println();
        System.out.println("Received Messages");
        System.out.println("------------------");
        
        for (final Message message : historyMessages) {
        	System.out.println(message);
        }
    }

    private static void doEvents(final StreamingCollaborationAgent agent,
            final BufferedReader reader) throws IOException {
        System.out.println();
        System.out.print("Enter channel ID: ");
        final String channelId = reader.readLine();
        System.out.println("Waiting for events... (Press Enter to stop)");

        class ChannelEventListener implements EventListener {
        	public void eventReceived(final StreamingCollaborationAgent agent, final Event event) {
        		System.out.println(event);
        	}
        }
        
        final EventListener channelEventListener = new ChannelEventListener();
        
        System.out.println();
        System.out.println("Received Events");
        System.out.println("---------------");
        
        agent.addEventListener(channelEventListener);
        agent.startStreaming(new String[] { channelId }, null, new EventType[] { EventType.MESSAGE });
        
        reader.readLine();
        
        agent.removeEventListener(channelEventListener);
        agent.stopStreaming();
    }

    private static <T extends Enum<?>> T toEnum(Class<T> enumeration, String search, T d) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return d;
    }
}


var baseUrl = 'https://api.company.com';

var bot;

var logMessage = function(message, cls, dontEncode) {
    var li = $('<li></li>');
    
    if (dontEncode) {
        li.html(message);
    } else {
        li.text(message);
    }
    
    if (cls) {
        li.addClass(cls);
    }

    $('#log-panel ul').append(li);

    // Scroll to bottom
    $(window).scrollTop($(document).height());
};

var listAsString = function(list, limit, fn) {
    if (list == null) {
        return 'null';
    }

    limit = Math.min(limit, list.length);
    var str = '';
    for (var i = 0; i < limit; i++) {
        str += (typeof fn == 'function' ? fn(list[i]) : list[i]);
        str += '<br/>';
    }

    if (limit != list.length) {
        str += ' ... and ' + (list.length - limit) + ' more';
    }

    return str;
}

var formatTime = function(timestamp) {
    return new Date(timestamp).toString();
}

var setupBot = function(sipAddress, username, password) {

    bot = new MindLink.FoundationApi.V1.Bot({
        baseUrl: baseUrl
    });

    $.extend(bot, {
        onLogMessage: function(message) {
            logMessage(message, 'debug');
        },
        onChannelHistory: function(channelId, messages) {
            logMessage('Chat history received for ' + channelId + '. Contains ' + messages.length + ' message(s).');
            // display the messages
            logMessage(
                listAsString(
                    messages,
                    messages.length,
                    function(message) {
                        var token = message.Token ? ('[token = ' + message.Token + '] ') : '';
                        var messageSenderAlias = message.SenderAlias ? ' (' + message.SenderAlias + ')' : '';
                        return token + formatTime(message.Timestamp) + ' ' + message.SenderId + messageSenderAlias + ' ' + message.Text + ' ' + JSON.stringify(message.MessageParts) + '\n'; 
                    }),
                '',
                true);

            if (console && console.log) {
                console.log('Chat history for ' + channelId + ':');
                console.log(messages);
            }
        },
        onSearchChatComplete: function(results) {
            if (results.length === 0) {
                logMessage('Search completed with empty results.');
                return;
            }
            
            var channelResults = results[0];

            logMessage('Chat history search results received for ' + channelResults.ChannelId + '. Contains ' + channelResults.Count + ' message(s).');
            // display the messages
            logMessage(
                listAsString(
                    channelResults.Messages,
                    channelResults.Messages.length,
                    function(message) {
                        var token = message.Token ? ('[token = ' + message.Token + '] ') : '';
                        var messageSenderAlias = message.SenderAlias ? ' (' + message.SenderAlias + ')' : '';
                        return token + formatTime(message.Timestamp) + ' ' + message.SenderId + messageSenderAlias + ' ' + message.Text + ' ' + JSON.stringify(message.MessageParts) + ' \n';
                    }),
                '',
                true);


            if (console && console.log) {
                console.log('Chat history for ' + channelResults.ChannelId + ':');
                console.log(channelResults.Messages);
            }
        },
        onChannelInfo: function(channelId, name, subject, description, displayName, emailAddress, canAcceptFiles, isReadOnly, maxMessageLength, maxStoryLength) {
            logMessage('Channel info received for ' + channelId + '. Name: \'' + name + '\' Subject: \'' + subject + '\', Description: \'' + description + '\', Display Name: \'' + displayName + '\', Email Address: \'' + emailAddress + '\', Files: \'' + canAcceptFiles + '\', Read Only: \'' + isReadOnly + '\', Max Message Length: \'' + maxMessageLength + '\', Max Story Length: \'' + maxStoryLength + '\'.');
        },
        onChannelMessage: function(channelId, senderId, alert, timestamp, token, message) {
            logMessage(senderId + ' sent a' + (alert ? 'n alert' : '') + ' message to \'' + channelId + '\' with token \'' + token + '\' at ' + formatTime(timestamp) + ': \'' + message + '\'.');
        },
        onChannelStory: function(channelId, senderId, alert, timestamp, token, subject, content) {
            logMessage(senderId + ' sent a' + (alert ? 'n alert' : '') + ' story with subject \'' + subject + '\' to ' + channelId + '\' with token \'' + token + '\' at ' + formatTime(timestamp) + '.');
        },
        onChannelUpload: function(channelId, fileName, fileSize) {
            logMessage('Uploaded file ' + fileName + ' of size ' + fileSize + ' to channel ' + channelId);
        },
        onChannelsList: function(channels, searchCriteria, limited) {
            logMessage('Chat rooms list received (' + channels.length + ' channels): ');
            logMessage(listAsString(channels, 8, function(channel) { return channel.DisplayName; }), '', true);
        },
        onError: function(errorCode, message) {
            logMessage('An error was received: ' + errorCode + ': ' + message, 'error');
        },
        onChannelState: function(channelId, subject, presence, presenceText) {
            logMessage('State info received for ' + channelId + '. Subject: \'' + subject + '\', presence: \'' + presence + '\', presenceText: ' + presenceText + '.');
        },
        onMetaData: function(metaData) {
            logMessage('Meta data received - ' + metaData.length + ' pairs.');
        },
        onAuthenticated: function(token) {
            logMessage('Authenticated with token: \'' + token + '\'.');
            //bot.streaming.start();
            $('form [disabled]').attr('disabled', false);
            $('form#streaming #stop-streaming').attr('disabled', true);
        },
        onToken: function(token, username, agent, expires) {
            logMessage('Got token information. Username: \'' + username + '\', agent: \'' + agent + '\', expires: \'' + expires + '\'.');
        },
        onProvisionedAgentsList: function (agents) {
            logMessage('Agents list received (' + agents.length + ' agents): ');
            logMessage(listAsString(agents, agents.length, function (agent) {
                return 'Id: ' + agent.Id + ' state: ' + agent.State; 
            }), '', true);               
        },
        onProvisionedAgent: function (provisioningMode, canProvision, managementMode, channels, id, metaData, state, userName, users) {
            logMessage('Agent returned. ID: ' + id + ', user name: ' + userName + ', provisioning mode: ' + provisioningMode + ', can provision: ' + canProvision + ', management mode: ' + managementMode + ', state: ' + state);
            logMessage(' - Channels:')
            logMessage(listAsString(channels, channels.length, function (channel) {
                return '    - Id: ' + channel.Id + ', state: ' + channel.State; 
            }), '', true);
            logMessage(' - Meta data:')
            logMessage(listAsString(metaData, metaData.length, function (data) {
                return '    - Key: ' + data.Key + ', value: ' + data.Value; 
            }), '', true);
            logMessage(' - Users:')
            logMessage(listAsString(users, users.length, function (user) {
                return '    - ' + user; 
            }), '', true);
        },
        onProvisionedAgentChannelsList: function (channels) {
            logMessage('Channels list received for agent (' + channels.length + ' channels): ');
            logMessage(listAsString(channels, channels.length, function (channel) {
                return 'Id: ' + channel.Id + ' state: ' + channel.State; 
            }), '', true);
        },
        onProvisionedAgentMetadataList: function (metadata) {
            logMessage('Metadata list received for agent (' + metadata.length + ' metadata): ');
            logMessage(listAsString(metadata, metadata.length, function (data) {
                return 'Key: ' + data.Key + ' value: ' + data.Value; 
            }), '', true);
        },
        onProvisionedAgentMetadataKeyValue: function (value) {
            logMessage('Metadata value : ' + value);
        },
        onUserList: function (users) {
            logMessage('user list received for provisioning service (' + users.length + ' users): ');
            logMessage(listAsString(users, users.length, function (user) {
                return 'UserId: ' + user.UserId;
            }), '', true);
        },
        onUser: function (userId, userName) {
            logMessage('User. ID: ' + userId + ', username: ' + userName);
        },
        onProvisionedChannelForAgent: function (status) {
            logMessage('Result from attempting to add channel on agent\'s provisioned list: ' + status);
        },
        onProvisionedMetadataForAgent: function (status) {
            logMessage('Result from attempting to add metadata for agent: ' + status);
        },
        onprovisionChangedSingleMetadataForAgent: function (status) {
            logMessage('Result from attempting to update value of an existing metadata key for agent: ' + status);
        },
        onprovisionChangedUser: function (status) {
            logMessage('Result from attempting to create/update a user: ' + status);
        },
        onDeleteAgentById: function (status) {
            logMessage('Result from attempting to delete an agent: ' + status);
        },
        onDeleteChannelByAgentId: function (status) {
            logMessage('Result from attempting to delete channel by agent id: ' + status);
        },
        onDeleteMatadataKeyByAgentId: function (status) {
            logMessage('Result from attempting to delete metadata key by agent id: ' + status);
        },
        onDeleteUserById: function (status) {
            logMessage('Result from attempting to delete user by id: ' + status);
        },
        onCreateAgent: function (status) {
            logMessage('Result from attempting to create an Agent: ' + status);
        },
        onThrottleList: function (throttles) {
            logMessage('throttle list received for provisioning service (' + throttles.length + ' users): ');
            logMessage(listAsString(throttles, throttles.length, function (throttle) {
                return 'ThrottleId: ' + throttle.Id;
            }), '', true);
        },
        onProvisionedThrottle: function (id, type, threshold, agents) {
            logMessage('Throttle returned. ID: ' + id + ', type: ' + type + ', threshold: ' + threshold);
            logMessage(' - Agents:')
            logMessage(listAsString(agents, agents.length, function (agent) {
                return '    - Id: ' + agent; 
            }), '', true);
        },
        onUpdateThrottle: function (status) {
            logMessage('Result from attempting to add/update a throttle: ' + status);
        },
        onDeleteThrottleById: function (status) {
            logMessage('Result from attempting to delete throttle by id: ' + status);
        },
        onFindChannelsComplete: function (channels) {
            logMessage('Chat rooms list received (' + channels.length + ' channels): ');
            logMessage(listAsString(channels, 8, function(channel) { return channel.Key + ' - ' + channel.Value; }), '', true);
        },
        onStreamingStarted: function() {
            $('form#streaming input[id=start-streaming]').attr('disabled', true);
            $('form#streaming input[id=stop-streaming]').attr('disabled', false);
        },
        onStreamingStopped: function() {
            $('form#streaming input[id=start-streaming]').attr('disabled', false);
            $('form#streaming input[id=stop-streaming]').attr('disabled', true);
        },
        onMessageReceived: function(eventId, time, channelId, token, sender, senderAlias, content, messageParts) {
            var aliasText = senderAlias ? ' with alias \'' + senderAlias + '\'' : '';
            logMessage('Message received to channel \'' + channelId + '\' with token \'' + token + '\' from \'' + sender + '\'' + aliasText + ', text: \'' + content + '\' and message parts: \'' + JSON.stringify(messageParts) + '\'.', 'streaming');
        },
        onChannelStateChanged: function(eventId, time, channelId, active) {
            logMessage('Channel state changed for channel \'' + channelId + '\': ' + (active ? 'active' : 'inactive'), 'streaming');
        },
        onMetaDataUpdated: function(eventId, time, key, value) {
            logMessage('Meta data key changed: \'' + key + '\' -> \'' + value + '\'.', 'streaming');
        },
        onManagementTestResultReceived: function (result) {
            logMessage('Management test result OK');
        },
    });
};

$(document).ready(function () {
    jQuery.support.cors = true;

    $('form#configure input[id=server-address]').bind('keyup change', function() {
        bot.setBaseUrl($('form#configure input[id=server-address]').val());
    });
    
    $('form#authenticate input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var username = $('form#authenticate input[id=authenticate-username]').val();
        var password = $('form#authenticate input[id=authenticate-password]').val();
        var agentId = $('form#authenticate input[id=authenticate-agent]').val();
        bot.authenticate(username, password, agentId);
    });

    $('form#get-token input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.getToken();
    });

    $('form#send-message input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#send-message input[id=message-channel-id]').val();
        var message = $('form#send-message textarea[id=message-content]').val();
        var alert = $('form#send-message input[id=message-alert]').is(':checked');
        bot.collaboration.sendChannelMessage(channelId, message, alert);
    });
    
    $('form#send-message-parts input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#send-message-parts input[id=message-channel-id]').val();
        var partsAsText = $('form#send-message-parts textarea[id=message-content]').val();
        var alert = $('form#send-message-parts input[id=message-alert]').is(':checked');
        bot.collaboration.sendChannelMessageAsParts(channelId, JSON.parse(partsAsText), alert);
    });
    
    $('form#send-message-parts input[id=message-part-plaintext]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-message-parts textarea[id=message-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"PlainTextMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Text":"This is a test"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-message-parts textarea[id=message-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-message-parts input[id=message-part-hyperlink]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-message-parts textarea[id=message-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"HyperlinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Text":"Go here!","Url":"www.example.com"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-message-parts textarea[id=message-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-message-parts input[id=message-part-channellink]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-message-parts textarea[id=message-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"ChannelLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","ChannelName":"Engineering","ChannelId":"chat-room:12345678-aabb-ccdd-eeff-012345678910"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-message-parts textarea[id=message-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-message-parts input[id=message-part-hashtag]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-message-parts textarea[id=message-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"HashtagMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Hashtag":"#API"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-message-parts textarea[id=message-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-message-parts input[id=message-part-clear]').click(function (ev) {
        ev.preventDefault();
        $('form#send-message-parts textarea[id=message-content]').val('[]');
    });

    $('form#send-story input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#send-story input[id=story-channel-id]').val();
        var subject = $('form#send-story input[id=story-subject]').val();
        var content = $('form#send-story textarea[id=story-content]').val();
        var alert = $('form#send-story input[id=story-alert]').is(':checked');
        bot.collaboration.sendChannelStory(channelId, subject, content, alert);
    });
    
    $('form#send-story-parts input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#send-story-parts input[id=story-channel-id]').val();
        var subject = $('form#send-story-parts input[id=story-subject]').val();
        var partsAsText = $('form#send-story-parts textarea[id=story-content]').val();
        var alert = $('form#send-story-parts input[id=story-alert]').is(':checked');
        bot.collaboration.sendChannelStoryAsParts(channelId, subject, JSON.parse(partsAsText), alert);
    });
    
    $('form#send-story-parts input[id=message-part-plaintext]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-story-parts textarea[id=story-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"PlainTextMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Text":"This is a test"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-story-parts textarea[id=story-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-story-parts input[id=message-part-hyperlink]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-story-parts textarea[id=story-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"HyperlinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Text":"Go here!","Url":"www.example.com"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-story-parts textarea[id=story-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-story-parts input[id=message-part-userlink]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-story-parts textarea[id=story-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"UserLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","DisplayName":"John Smith","UserId":"contact:john.smith@example.com"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-story-parts textarea[id=story-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-story-parts input[id=message-part-channellink]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-story-parts textarea[id=story-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"ChannelLinkMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","ChannelName":"Engineering","ChannelId":"chat-room:12345678-aabb-ccdd-eeff-012345678910"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-story-parts textarea[id=story-content]').val('[' + existingParts + newPart + ']');
    });
    
    $('form#send-story-parts input[id=message-part-hashtag]').click(function (ev) {
        ev.preventDefault();
        var existingParts = $('form#send-story-parts textarea[id=story-content]').val();
        if (existingParts.indexOf('[') == 0 && existingParts.indexOf(']', this.length - 1) !== -1) {
            existingParts = existingParts.substring(1, existingParts.length - 1);
        }
        var newPart = '{"__type":"HashtagMessagePart:http://schemas.fcg.im/foundation/v1/collaboration","Hashtag":"#API"}';
        if (existingParts.length !== 0) {
            newPart = ',' + newPart;
        }
        $('form#send-story-parts textarea[id=story-content]').val('[' + existingParts + newPart + ']');
    });

    $('form#send-story-parts input[id=message-part-clear]').click(function (ev) {
        ev.preventDefault();
        $('form#send-story-parts textarea[id=story-content]').val('[]');
    });

    
    $('form#upload-file input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#upload-file input[id=upload-file-channel-id]').val();
        var fileName = $('form#upload-file input[id=upload-file-file-name]').val();
        var contents = $('form#upload-file input[id=upload-file-file-contents]')[0].files[0];

        if (!contents) {
            logMessage('Cannot upload file as no file is selected.');
            return;
        }
        
        bot.collaboration.uploadFile(channelId, fileName, contents);
    });

    $('form#channels-list input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.collaboration.requestChannelsList();
    });

    $('form#channel-info input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#channel-info input[id=info-channel-id]').val();
        bot.collaboration.requestChannelInfo(channelId);
    });

    $('form#channel-history input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelId = $('form#channel-history input[id=history-channel-id]').val();
        var limit = $('form#channel-history select[id=history-limit]').val();
        var token = $('form#channel-history input[id=history-token]').val();

        bot.collaboration.requestChannelHistory(channelId, limit, token);
    });

    $('form#channel-state input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var id = $('form#channel-state input[id=state-channel-id]').val();
        bot.collaboration.requestChannelState(id);
    });

    $('form#request-metadata input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.collaboration.requestMetaData();
    });

    $('form#chat-search input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var channelid = $('form#chat-search input[id=search-channel-id]').val();
        var matchcase = $('form#chat-search input[id=search-matchcase]').is(':checked');
        var matchexact = $('form#chat-search input[id=search-matchexact]').is(':checked');
        var searchtext = $('form#chat-search input[id=search-text]').val();
        var limit = $('form#chat-search select[id=search-limit]').val();
        var anyDate = $('form#chat-search input[id=search-anyDate]').is(':checked');
        var onDate = $('form#chat-search input[id=search-onDate]').is(':checked');
        var onDateValue = $('form#chat-search input[id=search-onDateValue]').val();

        var channelIds = [channelid];

        if (!onDate) {
            onDateValue = null;
        }

        bot.collaboration.searchChatHistory(searchtext, matchcase, matchexact, false, null, null, 0, onDateValue, parseInt(limit), channelIds);
    });

    $('form#channel-agent-state input[id=start-composing]').click(function (ev) {
        ev.preventDefault();

        var channelid = $('form#channel-agent-state input[id=channel-agent-state-channel-id]').val();
        bot.collaboration.updateChannelAgentState(channelid, true);
    });

    $('form#channel-agent-state input[id=stop-composing]').click(function (ev) {
        ev.preventDefault();

        var channelid = $('form#channel-agent-state input[id=channel-agent-state-channel-id]').val();
        bot.collaboration.updateChannelAgentState(channelid, false);
    });

    $('form#request-provisioned-agents input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.provisioning.requestProvisionedAgents();
    });

    $('form#request-provisioned-agents-by-id input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#request-provisioned-agents-by-id input[id=agent-id]').val();
        bot.provisioning.requestProvisionedAgentById(agentId);
    });

    $('form#request-provisioned-agents-channels input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#request-provisioned-agents-channels input[id=channels-agent-id]').val();
        bot.provisioning.requestProvisionedAgentChannels(agentId);
    });

    $('form#request-provisioned-agent-metadata input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#request-provisioned-agent-metadata input[id=metadata-agent-id]').val();
        bot.provisioning.requestProvisionedAgentMetaData(agentId);
    });

    $('form#request-provisioned-agent-metadata-by-key input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#request-provisioned-agent-metadata-by-key input[id=metadata-key-agent-id]').val();
        var key = $('form#request-provisioned-agent-metadata-by-key input[id=metadata-key]').val();
        bot.provisioning.requestProvisionedAgentMetaDataByKey(agentId, key);
    });

    $('form#request-provisioned-users input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.provisioning.requestAllUsers();
    });

    $('form#request-provisioned-user-by-id input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var userId = $('form#request-provisioned-user-by-id input[id=user-id]').val();
        bot.provisioning.requestUsersById(userId);
    });

    $('form#provision-channel-for-agent input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-channel-for-agent input[id=provision-channel-agent-id]').val();
        var channelId = $('form#provision-channel-for-agent input[id=provision-channel-channel-id]').val();
        bot.provisioning.provisionChannelForAgent(agentId, channelId);
    });

    $('form#provision-metadata-for-agent input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-metadata-for-agent input[id=provision-metadata-agent-id]').val();
        var metdataData = $('form#provision-metadata-for-agent input[id=provision-metadata-metadataList]').val();
        bot.provisioning.provisionMetadataForAgent(agentId, metdataData);
    });

    $('form#provision-update-metadata-for-agent input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-update-metadata-for-agent input[id=provision-metadata-update-agent-id]').val();
        var metdataDataKey = $('form#provision-update-metadata-for-agent input[id=provision-metadata-update-key]').val();
        var value = $('form#provision-update-metadata-for-agent input[id=provision-metadata-update-value]').val();
        bot.provisioning.provisionChangeSingleMetadataForAgent(agentId, metdataDataKey, value);
    });

    $('form#provision-update-user input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var userId = $('form#provision-update-user input[id=provision-update-user-id]').val();
        var userName = $('form#provision-update-user input[id=provision-update-username]').val();
        bot.provisioning.provisionChangeUser(userId, userName);
    });

    $('form#provision-create-agent input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-create-agent input[id=provision-create-agent-id]').val();
        var userName = $('form#provision-create-agent input[id=provision-create-agent-username]').val();
        var channels = $('form#provision-create-agent input[id=provision-create-agent-channels]').val();
        var metaData = $('form#provision-create-agent input[id=provision-create-agent-metadata]').val();
        var users = $('form#provision-create-agent input[id=provision-create-agent-users]').val();
        var provisioningMode = $('form#provision-create-agent select[id=provision-create-agent-provisioningMode]').val();
        var managementMode = $('form#provision-create-agent select[id=provision-create-agent-managementMode]').val();
        bot.provisioning.createAgent(agentId, userName, channels, metaData, users, provisioningMode, managementMode);
    });

    $('form#provision-delete-agent input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-delete-agent input[id=provision-delete-agent-id]').val();
        bot.provisioning.deleteAgent(agentId);
    });

    $('form#provision-delete-channel-by-agentId input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-delete-channel-by-agentId input[id=provision-delete-channel-agent-id]').val();
        var channelId = $('form#provision-delete-channel-by-agentId input[id=provision-delete-channel-channel-id]').val();
        bot.provisioning.deleteChannelByAgentId(agentId, channelId);
    });

    $('form#provision-delete-metadata-by-agentId input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var agentId = $('form#provision-delete-metadata-by-agentId input[id=provision-delete-metadata-agent-id]').val();
        var metaDataKey = $('form#provision-delete-metadata-by-agentId input[id=provision-delete-metadata-metadataKey]').val();
        bot.provisioning.deleteMetadataKeyByAgentId(agentId, metaDataKey);
    });

    $('form#provision-delete-user-by-Id input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var userId = $('form#provision-delete-user-by-Id input[id=provision-delete-user-id]').val();
        bot.provisioning.deleteUserById(userId);
    });
    
    $('form#request-throttles input[type=submit]').click(function (ev) {
        ev.preventDefault();
        bot.provisioning.requestAllThrottles();
    });
    
    $('form#request-throttle-by-id input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var throttleId = $('form#request-throttle-by-id input[id=throttle-id]').val();
        bot.provisioning.requestThrottleById(throttleId);
    });
    
    $('form#update-throttle input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var throttleId = $('form#update-throttle input[id=update-throttle-id]').val();
        var throttleType = $('form#update-throttle select[id=update-throttle-type]').val();
        var throttleThreshold = $('form#update-throttle input[id=update-throttle-threshold]').val();
        var throttleAgents = $('form#update-throttle input[id=update-throttle-agents]').val();
        bot.provisioning.updateThrottle(throttleId, throttleType, throttleThreshold, throttleAgents);
    });

    $('form#delete-throttle-by-id input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var throttleId = $('form#delete-throttle-by-id input[id=delete-throttle-id]').val();
        bot.provisioning.deleteThrottleById(throttleId);
    });
    
    $('form#provision-find-channels input[type=submit]').click(function (ev) {
        ev.preventDefault();
        var searchTerm = $('form#provision-find-channels input[id=provision-find-channels-search-text]').val();
        bot.provisioning.findChannels(searchTerm);
    });

    $('form#streaming input[id=start-streaming]').click(function(ev) {
        ev.preventDefault();
        var eventTypes = $('form#streaming input[id=streaming-event-types]').val();
        var channels = $('form#streaming input[id=streaming-channels]').val();
        var regex = $('form#streaming input[id=streaming-regex]').val();
        bot.streaming.start(eventTypes, channels, regex);
    });

    $('form#streaming input[id=stop-streaming]').click(function(ev) {
        ev.preventDefault();
        bot.streaming.stop();
    });

    var privacies = {
        0: "Open",
        1: "Closed",
        2: "Secret"
    };

    $('form#manage-get-categories input[type=submit]').click(function (ev) {
        ev.preventDefault();
        
        bot.management.getChannelCategories(function (categories) {
            logMessage('Categories list received (' + categories.length + ' channels): ');
            logMessage(listAsString(categories, categories.length, function (channel) {
                return 'Id: ' + channel.Id + ' name: ' + channel.Name; 
            }), '', true);
        });
    });

    $('form#manage-get-channels input[type=submit]').click(function (ev) {
        ev.preventDefault();
        
        bot.management.getManagedChannels(function (channels) {
            logMessage('Managed channels list received (' + channels.length + ' channels): ');
            logMessage(listAsString(channels, channels.length, function (channel) {
                return 'Id: ' + channel.Id + ' name: ' + channel.Name + ' privacy: ' + privacies[channel.Privacy]; 
            }), '', true);
        });
    });

    $('form#manage-get-channel-members input[type=submit]').click(function (ev) {
        ev.preventDefault();

        var channel = $('form#manage-get-channel-members input[id=manage-get-channel-members-channel]').val();

        bot.management.getManagedChannelMembers(channel, function (members) {
            logMessage('Managed channel members list received (' + members.length + ' members) for channel ' + channel + ': ');
            logMessage(listAsString(members, members.length, function (member) {
                return 'Id: ' + member; 
            }), '', true);
        });
    });

    $('form#manage-set-channel-members input[type=submit]').click(function (ev) {
        ev.preventDefault();

        var channel = $('form#manage-set-channel-members input[id=manage-set-channel-members-channel]').val();
        var members = $.map($('form#manage-set-channel-members textarea[id=manage-set-channel-members-members]').val().split(/\n/).filter(function(v) {
            return !!v;
        }), $.trim);


        bot.management.setManagedChannelMembers(channel, members, function() {
            logMessage('Successfully set members for channel ' + channel);
        });
    });

    $('form#manage-delete-channel input[type=submit]').click(function (ev) {
        ev.preventDefault();

        var channel = $('form#manage-delete-channel input[id=manage-delete-channel-channel]').val();

        bot.management.deleteManagedChannel(channel, function() {
            logMessage('Successfully deleted channel ' + channel);
        });
    });

    $('form#manage-create-channel input[type=submit]').click(function (ev) {
        ev.preventDefault();

        var name = $('form#manage-create-channel input[id=manage-create-channel-name]').val();
        var categoryId = $('form#manage-create-channel input[id=manage-create-channel-category]').val();
        var description = $('form#manage-create-channel input[id=manage-create-channel-description]').val();
        var members = $.map($('form#manage-create-channel textarea[id=manage-create-channel-members]').val().split(/\n/).filter(function(v) {
            return !!v;
        }), $.trim);
        var privacy = $('form#manage-create-channel select[id=manage-create-channel-privacy]').val();

        bot.management.createManagedChannel(name, categoryId, description, privacy, members, function(channelId) {
            logMessage('Successfully created channel with name \'' + name + '\' as \'' + channelId + '\'');
        });
    });

    var selectTab = function (name) {
        $('.tab').css('display', 'none');
        $('#' + name).css('display', 'block');
        $('#menu a').removeClass('selected');
        $('#' + name + '-tab').addClass('selected');
    }

    $('#configuration-tab').click(function (ev) {
        selectTab('configuration');
    });
    
    $('#authentication-tab').click(function (ev) {
        selectTab('authentication');
    });

    $('#collaboration-tab').click(function (ev) {
        selectTab('collaboration');
    });

    $('#provisioning-tab').click(function (ev) {
        selectTab('provisioning');
    });

    $('#management-tab').click(function (ev) {
        selectTab('management');
    });

    selectTab('configuration');

    logMessage('If JS not hosted run this page in IE or as a Chrome extension for it to work.');
    logMessage('Test bot ready.');

    setupBot('', '', '');
});
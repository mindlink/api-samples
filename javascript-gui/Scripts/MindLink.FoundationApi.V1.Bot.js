
if (typeof MindLink == "undefined") MindLink = {};
if (typeof MindLink.FoundationApi == "undefined") MindLink.FoundationApi = {};
if (typeof MindLink.FoundationApi.V1 == "undefined") MindLink.FoundationApi.V1 = {};

MindLink.FoundationApi.V1.Bot = function(config) {

    var self = this;

    var baseUrl = config.baseUrl;
    
    // The XHR request for getting events
    var getEventsJqXhr = null;
    var streaming = false;

    // Namespaces
    self.collaboration = {};
    self.provisioning = {};
    self.streaming = {};
    self.management = {};

    // Event handlers
    self.onLogMessage = jQuery.noop; // message
    self.onChannelHistory = jQuery.noop; // channelId, messages
    self.onChannelInfo = jQuery.noop; // Id, name, subject, description, users
    self.onChannelMessage = jQuery.noop; // channelId, channelName, senderUserName, alert, timestamp, message
    self.onChannelsList = jQuery.noop; // channels
    self.onChannelsStory = jQuery.noop; // channelId, channelName, senderUserName, alert, timestamp, subject, content
    self.onChannelUpload = jQuery.noop; // channelId, fileName, fileSize
    self.onError = jQuery.noop; // errorCode, message
    self.onStateInfo = jQuery.noop; // channelId, subject, presence, presenceText
    self.onSearchChatComplete = jQuery.noop; // array of objects made up of channelId, count, maxMessageId, messages, minMessageId
    self.onMetaData = jQuery.noop; // metadata
    self.onProvisionedAgentsList = jQuery.noop; // array of Provisioned Agents
    self.onProvisionedAgent = jQuery.noop; // CanProvision , Channels , Id , MetaData , State , UserName
    self.onProvisionedAgentChannelsList = jQuery.noop; // list of agents provisioned channels
    self.onProvisionedAgentMetadataList = jQuery.noop; // list of key value pairs metadata for an agent
    self.onProvisionedAgentMetadataKeyValue = jQuery.noop;  // metadata key value
    self.onUserList = jQuery.noop; // return list of users that have been configured
    self.onThrottleList = jQuery.noop; // return list of throttles that have been configured
    self.onUpdateThrottle = jQuery.noop;
    self.onDeleteThrottleById = jQuery.noop;
    self.onProvisionedThrottle = jQuery.noop; // Id , Type, Threshold, Agents
    self.onUser = jQuery.noop; // returns UserId
    self.onProvisionedChannelForAgent = jQuery.noop;
    self.onProvisionedMetadataForAgent = jQuery.noop;
    self.onprovisionChangedSingleMetadataForAgent = jQuery.noop;
    self.onprovisionChangedUser = jQuery.noop;
    self.onCreateAgent = jQuery.noop;
    self.onDeleteAgentById = jQuery.noop;
    self.onDeleteChannelByAgentId = jQuery.noop;
    self.onDeleteMatadataKeyByAgentId = jQuery.noop;
    self.onDeleteUserById = jQuery.noop;
    self.onAuthenticated = jQuery.noop; // token
    self.onToken = jQuery.noop; // token, username, agent, expires
    self.onFindChannelsComplete = jQuery.noop; //Key ,value
    self.onStreamingStarted = jQuery.noop;
    self.onStreamingStopped = jQuery.noop;
    self.onMessageReceived = jQuery.noop; // event id, time, channel id, sender, content
    self.onChannelStateChanged = jQuery.noop; // event id, time, channel id, active
    self.onMetaDataUpdated = jQuery.noop; // event id, time, key, value
    self.onManagementTestResultReceived = jQuery.noop // result

    var log = function(message) {
        self.onLogMessage(message);
    };

    var handleError = function(errorCode, message, errorFn) {
        self.onError(errorCode, message);
        if (errorFn) errorFn(errorCode, message);
    };

    var sendRequest = function(method, actionType, data, successFn, errorFn, ajaxOptions) {
        var headers = self.token ? { 'Authorization': 'FCF ' + self.token} : {};
        return jQuery.ajax(jQuery.extend({
            url: baseUrl + '/' + method,
            type: actionType,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            dataType: 'json',
            cache: false,
            timeout: 20000,
            processData: false,
            headers: headers,
            error: function(xhr, status, error) {
                if (xhr.status === 200 && status === 'parsererror') {
                   successFn(null, 'success');                                                     
                } else {   
                    handleError(xhr.status, error, errorFn);
                }
            },
            success: function(result, statusText) {
                if (result && result.ExceptionDetail) {
                    handleError(0, result.Message, errorFn);
                } else {
                    successFn(result, statusText);
                }
            }
        }, ajaxOptions));
    };
    
    var stopGetEvents = function() {
        streaming = false;
        if (getEventsJqXhr) {
            getEventsJqXhr.abort();
            getEventsJqXhr = null;
        }
    };

    var getEvents = function(lastEventId, eventTypes, channels, regex) {
        var url = 'Collaboration/V1/Events?last-event=' + lastEventId;
        if (eventTypes) url += '&types=' + eventTypes;
        if (channels) url += '&channels=' + channels;
        if (regex) url += '&regex=' + regex;
        getEventsJqXhr = sendRequest(url, 'GET', '', function(result) {
            for (var i = 0; i < result.length; i++) {
                var ev = result[i];
                switch (ev.__type.split(':')[0]) {
                    case 'ChannelStateEvent':
                        self.onChannelStateChanged(ev.EventId, ev.Time, ev.ChannelId, ev.Active);
                        break;
                    case 'MessageEvent':
                        self.onMessageReceived(ev.EventId, ev.Time, ev.ChannelId, ev.Sender, ev.Content, ev.MessageParts);
                        break;
                    case 'MetaDataEvent':
                        self.onMetaDataUpdated(ev.EventId, ev.Time, ev.Key, ev.Value);
                        break;
                    default:
                        log('Unhandled event: ' + JSON.stringify(ev));
                }
                lastEventId = ev.EventId;
            }
            getEvents(lastEventId, eventTypes, channels, regex);
        }, function(errorCode, message) {
            if (streaming) {
                log('Error while listening for events: (' + errorCode + ') \'' + message + '\'. Event polling will begin again in five seconds...');
                setTimeout(function() { getEvents(lastEventId); }, 5000);
            } else {
                log('Streaming stopped.');
            }
        }, {
            timeout: 40000
        });
    };
    
    self.authenticate = function(username, password, agentId, callbackFn, errorFn) {
        log('Authenticating user \'' + username + '\'...');
        sendRequest('Authentication/V1/Tokens', 'POST', {
            Username: username,
            Password: password,
            AgentId: agentId
        }, function(result) {
            self.token = result;
            self.onAuthenticated(result);
            if (callbackFn) callbackFn(result);
        }, errorFn);
    };

    self.getToken = function(callbackFn, errorFn) {
        log('Getting token...');
        sendRequest('Authentication/V1/Tokens/' + self.token, 'GET', '', function(result) {
            var expires = new Date(result.ExpiresTimestamp);
            self.onToken(self.token, result.Username, result.AgentId, expires);
            if (callbackFn) callbackFn(self.token, result.Username, result.AgentId, expires);
        }, errorFn);
    };

    self.setBaseUrl = function(newBaseUrl) {
        baseUrl = newBaseUrl;
    },
    
    self.streaming.start = function(eventTypes, channels, regex) {
        log('Starting streaming...');
        stopGetEvents();
        streaming = true;
        getEvents(0, eventTypes, channels, regex);
        self.onStreamingStarted();
    };

    self.streaming.stop = function() {
        log('Stopping streaming...');
        stopGetEvents();
        self.onStreamingStopped();
    };

    self.collaboration.requestChannelHistory = function(channelId, limit, callbackFn, errorFn) {
        limit = limit || 50; // default
        log('Requesting channel history for \'' + channelId + '\'...');
        sendRequest('Collaboration/V1/Channels/' + channelId + '/Messages?take=' + limit, 'GET', '', function(result) {
            self.onChannelHistory(channelId, result);
            if (callbackFn) callbackFn(channelId, result);
        }, errorFn);
    };

    self.collaboration.requestChannelsList = function(callbackFn, errorFn) {
        log('Requesting channels list...');
        sendRequest('Collaboration/V1/Channels', 'GET', '', function(result) {
            self.onChannelsList(result);
            if (callbackFn) callbackFn(result);
        }, errorFn);
    };

    self.collaboration.requestChannelInfo = function(channelId, callbackFn, errorFn) {
        log('Requesting channel info for \'' + channelId + '\'...');
        sendRequest('Collaboration/V1/Channels/' + channelId, 'GET', '', function(result) {
            self.onChannelInfo(channelId, result.DisplayName, result.Subject, result.Description, result.DisplayName, result.EmailAddress, result.CanAcceptFiles, result.IsReadOnly, result.MaxMessageLength, result.MaxStoryLength);
            if (callbackFn) callbackFn(channelId, result.DisplayName, result.Subject, result.Description, result.DisplayName, result.EmailAddress, result.CanAcceptFiles, result.IsReadOnly);
        }, errorFn);
    };

    self.collaboration.requestChannelState = function(channelId, callbackFn, errorFn) {
        log('Requesting state for \'' + channelId + '\'...');
        sendRequest('Collaboration/V1/Channels/' + channelId + '/State', 'GET', '', function(result) {
            self.onChannelState(channelId, result.Subject, result.PresenceState, result.PresenceText);
            if (callbackFn) callbackFn(channelId, result.Subject, result.Presence, result.PresenceText);
        }, errorFn);
    };

    self.collaboration.sendChannelMessage = function(channelId, text, alert, callbackFn, errorFn) {
        log('Sending ' + (alert ? 'alert ' : '') + 'message to channel \'' + channelId + '\'...');
        var body = {
            IsAlert: alert,
            Text: text
        };
        sendRequest('Collaboration/V1/Channels/' + channelId + '/Messages', 'POST', body, function(result) {
            self.onChannelMessage(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Text);
            if (callbackFn) callbackFn(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Text);
        }, errorFn);
    };

    self.collaboration.sendChannelMessageAsParts = function(channelId, messageParts, alert, callbackFn, errorFn) {
        log('Sending ' + (alert ? 'alert ' : '') + 'message as parts to channel \'' + channelId + '\'...');
        var body = {
            IsAlert: alert,
            MessageParts: messageParts
        };

        sendRequest('Collaboration/V1/Channels/' + channelId + '/Messages', 'POST', body, function(result) {
            self.onChannelMessage(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Text);
            if (callbackFn) callbackFn(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Text);
        }, errorFn);
    };
    
    self.collaboration.sendChannelStory = function(channelId, subject, content, alert, callbackFn, errorFn) {
        log('Sending ' + (alert ? 'alert ' : '') + 'story (with subject \'' + subject + '\') to channel \'' + channelId + '\'...');
        var body = {
            IsAlert: alert,
            Subject: subject,
            Text: content
        };
        sendRequest('Collaboration/V1/Channels/' + channelId + '/Messages', 'POST', body, function(result) {
            self.onChannelStory(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Subject, result.Text);
            if (callbackFn) callbackFn(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Subject, result.Text);
        }, errorFn);
    };
    
    self.collaboration.sendChannelStoryAsParts = function(channelId, subject, messageParts, alert, callbackFn, errorFn) {
        log('Sending ' + (alert ? 'alert ' : '') + 'story (with subject \'' + subject + '\') to channel \'' + channelId + '\'...');
        var body = {
            IsAlert: alert,
            Subject: subject,
            MessageParts: messageParts
        };

        sendRequest('Collaboration/V1/Channels/' + channelId + '/Messages', 'POST', body, function(result) {
            self.onChannelStory(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Subject, result.Text);
            if (callbackFn) callbackFn(result.ChannelId, result.SenderId, result.IsAlert, result.Timestamp, result.Subject, result.Text);
        }, errorFn);
    };
    
    self.collaboration.uploadFile = function(channelId, fileName, content, errorFn) {
        log('Uploading file name with content size \'' + content.size + '\' to channel \'' + channelId + '\'');
        
        var xhr = new XMLHttpRequest();
        xhr.open('POST', baseUrl + '/Collaboration/V1/Channels/' + channelId + '/File/' + fileName, true);

        if (self.token) {
            xhr.setRequestHeader('Authorization', 'FCF ' + self.token);
        }
        
        xhr.onload = function(e) {
            if (!(xhr.status === 200 || xhr.status === 204)) {
                handleError(xhr.status, xhr.statusText, errorFn);
                return;
            }

            self.onChannelUpload(channelId, fileName, content.size);
        };

        xhr.send(content);
    };

    self.collaboration.searchChatHistory = function(searchTerm, matchCase, matchExact, matchAll, fromDate, toDate, daysBack, onDate, limit, channelIds, callbackFn, errorFn) {
        log('Searching term \'' + searchTerm + '\' (match case: ' + (matchCase ? 'yes' : 'no') + ', match exact: ' + (matchExact ? 'yes' : 'no') + ', match all: ' + (matchAll ? 'yes' : 'no') + ')...');
        sendRequest('Collaboration/V1/Channels/Search', 'POST', {
            ChannelIds: channelIds,
            DaysBack: daysBack,
            FromDate: fromDate,
            Limit: limit,
            MatchAll: matchAll,
            MatchCase: matchCase,
            MatchExact: matchExact,
            OnDate: onDate,
            SearchTerm: searchTerm,
            ToDate: toDate
        }, function(results) {
            self.onSearchChatComplete(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.collaboration.requestMetaData = function(callbackFn, errorFn) {
        log('Requesting user agent metadata...');
        sendRequest('Collaboration/V1/Metadata', 'GET', '', function(results) {
            self.onMetaData(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.provisioning.requestProvisionedAgents = function(callbackFn, errorFn) {
        log('Requesting all provisioned agents...');
        sendRequest('Provisioning/V1/Agents', 'GET', '', function(results) {
            self.onProvisionedAgentsList(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.provisioning.requestProvisionedAgentById = function(agentId, callbackFn, errorFn) {
        log('Requesting provisioned agents by ID...');
        sendRequest('Provisioning/V1/Agents/' + agentId, 'GET', '', function(result) {
            self.onProvisionedAgent(result.ProvisioningMode, result.CanProvision, result.Channels, result.Id, result.MetaData, result.State, result.UserName, result.Users);
            if (callbackFn) callbackFn(result.ProvisioningMode, result.CanProvision, result.Channels, result.Id, result.MetaData, result.State, result.UserName, result.Users);
        }, errorFn);
    };

    self.provisioning.requestProvisionedAgentChannels = function(agentId, callbackFn, errorFn) {
        log('Requesting provisioned agents channels...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/Channels', 'GET', '', function(results) {
            self.onProvisionedAgentChannelsList(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.provisioning.requestProvisionedAgentMetaData = function(agentId, callbackFn, errorFn) {
        log('Requesting provisioned agent\'s meta data...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/Metadata', 'GET', '', function(results) {
            self.onProvisionedAgentMetadataList(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.provisioning.requestProvisionedAgentMetaDataByKey = function(agentId, key, callbackFn, errorFn) {
        log('Requesting agent meta data by key...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/Metadata/' + key, 'GET', '', function(result) {
            self.onProvisionedAgentMetadataKeyValue(result);
            if (callbackFn) callbackFn(result);
        }, errorFn);
    };

    self.provisioning.requestAllUsers = function(callbackFn, errorFn) {
        log('Requesting provisioned users...');
        sendRequest('Provisioning/V1/Users', 'GET', '', function(results) {
            self.onUserList(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };
    
    self.provisioning.requestAllThrottles = function(callbackFn, errorFn) {
        log('Requesting provisioned throttles...');
        sendRequest('Provisioning/V1/Throttles', 'GET', '', function(results) {
            self.onThrottleList(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };
    
    self.provisioning.requestThrottleById = function(throttleId, callbackFn, errorFn) {
        log('Requesting provisioned throttle by ID...');
        sendRequest('Provisioning/V1/Throttles/' + throttleId, 'GET', '', function(result) {
            self.onProvisionedThrottle(result.Id, result.Type, result.Threshold, result.Agents);
            if (callbackFn) callbackFn(result.Id, result.Type, result.Threshold, result.Agents);
        }, errorFn);
    };
    
    self.provisioning.updateThrottle = function(throttleId, type, threshold, agents,  callbackFn, errorFn) {
        log('Creating/updating throttle...');
        var agents = agents.split(';');

        sendRequest('Provisioning/V1/Throttles/' + throttleId, 'PUT', {
            Id: throttleId,
            Type: type,
            Threshold: threshold,
            Agents: agents
        }, function(result, status) {
            self.onUpdateThrottle(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };
    
    self.provisioning.deleteThrottleById = function(throttleId, callbackFn, errorFn) {
        log('Deleting throttle by ID...');
        sendRequest('Provisioning/V1/Throttles/' + throttleId, 'DELETE', '', function(result, status) {
            self.onDeleteThrottleById(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.requestUsersById = function(userId, callbackFn, errorFn) {
        log('Requesting provisioned users by ID...');
        sendRequest('Provisioning/V1/Users/' + userId, 'GET', '', function(result) {
            self.onUser(result.UserId, result.Username);
            if (callbackFn) callbackFn(result.UserId, result.Username);
        }, errorFn);
    };

    self.provisioning.provisionChannelForAgent = function(agentId, channelId, callbackFn, errorFn) {
        log('Provisioning channel for agent...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/Channels/' + channelId, 'PUT', '', function(result, status) {
            self.onProvisionedChannelForAgent(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.provisionMetadataForAgent = function(agentId, metadata, callbackFn, errorFn) {
        log('Provisioning meta data for agent...');
        var meta = [];
        var rawMeta = metadata.split(";"); // key1:value1;key2:value2;.....
        for (i = 0; i < rawMeta.length; i++) {
            var keyValuePair = rawMeta[i].split(":");
            if (keyValuePair[0] && keyValuePair[1]) {
                meta[i] = { Key: keyValuePair[0], Value: keyValuePair[1] };
            }
        }
        sendRequest('Provisioning/V1/Agents/' + agentId + '/MetaData', 'PUT', meta, function(result, status) {
            self.onProvisionedMetadataForAgent(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.provisionChangeSingleMetadataForAgent = function(agentId, metadataKey, value, callbackFn, errorFn) {
        log('Updating value of existing meta data for agent...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/MetaData/' + metadataKey, 'PUT', value, function(result, status) {
            self.onprovisionChangedSingleMetadataForAgent(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.provisionChangeUser = function(userId, username, callbackFn, errorFn) {
        log('Adding/updating user...');
        sendRequest('Provisioning/V1/Users/' + userId, 'PUT', {
            UserId: userId,
            Username: username
        }, function(result, status) {
            self.onprovisionChangedUser(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.createAgent = function(agentId, userName, channels, metaData, users, provisioningMode, callbackFn, errorFn) {
        log('Creating new agent...');
        var meta = [];
        var rawMeta = metaData.split(';'); // key1:value1;key2:value2;.....
        for (i = 0; i < rawMeta.length; i++) {
            var keyValuePair = rawMeta[i].split(':');
            if (keyValuePair[0] && keyValuePair[1]) {
                meta[i] = { Key: keyValuePair[0], Value: keyValuePair[1] };
            }
        }
        var chnls = [];
        var rawChannels = channels.split(';'); // chnl1;chnl2;.....
        for (i = 0; i < rawChannels.length; i++) {
            if (rawChannels[i]) {
                chnls[i] = { Id: rawChannels[i], State: "0" };
            }
        }
        var usrs = users.split(';');
        sendRequest('Provisioning/V1/Agents/' + agentId, 'PUT', {
            Id: agentId,
            UserName: userName,
            Channels: chnls,
            MetaData: meta,
            Users: usrs,
            CanProvision: provisioningMode == '3' || provisioningMode == '2',
            ProvisioningMode: provisioningMode,
            State: '0'
        }, function(result, status) {
            self.onCreateAgent(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.deleteAgent = function(agentId, userId, username, callbackFn, errorFn) {
        log('Deleting agent by ID...');
        sendRequest('Provisioning/V1/Agents/' + agentId, 'DELETE', '', function(result, status) {
            self.onDeleteAgentById(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.deleteChannelByAgentId = function(agentId, channelId, callbackFn, errorFn) {
        log('Deleting channel by agent ID...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/Channels/' + channelId, 'DELETE', '', function(result, status) {
            self.onDeleteChannelByAgentId(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.deleteMetadataKeyByAgentId = function(agentId, metadataKey, callbackFn, errorFn) {
        log('Deleting channel by agent ID...');
        sendRequest('Provisioning/V1/Agents/' + agentId + '/MetaData/' + metadataKey, 'DELETE', '', function(result, status) {
            self.onDeleteMatadataKeyByAgentId(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.deleteUserById = function(userId, callbackFn, errorFn) {
        log('Deleting user by ID...');
        sendRequest('Provisioning/V1/Users/' + userId, 'DELETE', '', function(result, status) {
            self.onDeleteUserById(status);
            if (callbackFn) callbackFn(status);
        }, errorFn);
    };

    self.provisioning.findChannels = function(searchTerm, callbackFn, errorFn) {
        log('Find channels by criteria...');
        sendRequest('Provisioning/V1/Channels?query=' + searchTerm, 'Get', '', function(results) {
            self.onFindChannelsComplete(results);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    };

    self.management.test = function(callbackFn, errorFn) {
        log('Test management API...')
        sendRequest('Management/V1/Test', 'GET', '', function(result) {
            self.onManagementTestResultReceived(result);
            if (callbackFn) callbackFn(results);
        }, errorFn);
    }
};
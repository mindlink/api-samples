"""
MindLink API samples.

For more information, visit our developer wiki @https://wiki.mindlinksoft.com/tiki-index.php?page=MindLink+API and our engineering blog @https://engineering.mindlinksoft.com/

The samples are provided "as is". Do feel free to experiment or use them as you deem fit. Have fun!

ApiConnection.py

This module exposes methods to perform authentication and basic collaboration operations, like getting, sending messages and streaming messages event from channels.

For usage samples, check the sample_bot.py file.

"""

import requests
import threading

class ApiConnection:
    def __init__(self, host, username, password, agent):
        self.host = host
        self.username = username
        self.password = password
        self.agent = agent
        self.last_event = 0
        self.running = False
        self.token = ''


    def authenticate(self):
        request_url = '{}/Authentication/V1/Tokens'.format(self.host)

        response = requests.post(request_url, json = {
                'Username': self.username,
                'Password': self.password,
                'AgentId': self.agent
        }, headers = {
            'Accept' : 'application/json'
        })

        if response.status_code == 200:
            self.token = response.json()
            return self.token

        print ('Something went wrong while authenticating!', response.status_code, response.reason)


    def get_messages(self, channel_id, count):
        if self.token == '':
            self.authenticate()

        request_url = '{}/Collaboration/V1/Channels/{}/Messages'.format(self.host, channel_id)

        parameters = {
            'take': count,
        }

        response = requests.get(
            request_url,
            params = parameters,
            headers = {
                'Accept' : 'application/json',
                'Authorization': 'FCF {}'.format(self.token)}
            )

        if response.status_code == 200:
            return response.json()

        print ('Something went wrong while getting messages!', response.status_code, response.reason)


    def send_message(self, channel_id, content, is_alert):
        if self.token == '':
            self.authenticate()        

        request_url = '{}/Collaboration/V1/Channels/{}/Messages'.format(self.host, channel_id)

        response = requests.post(
            request_url,
            json = {
                'Text': content,
                'IsAlert': is_alert
            },
            headers = {
                'Accept' : 'application/json',
                'Authorization': 'FCF {}'.format(self.token)}
            )

        if response.status_code != 200:
            print ('Something went wrong while sending a message to a channel!', channel_id, response.status_code, response.reason)

    def send_message_parts(self, channel_id, messageParts, is_alert):
        if self.token == '':
            self.authenticate()        

        request_url = '{}/Collaboration/V1/Channels/{}/Messages'.format(self.host, channel_id)

        response = requests.post(
            request_url,
            json = {
                'MessageParts': messageParts,
                'IsAlert': is_alert
            },
            headers = {
                'Accept' : 'application/json',
                'Authorization': 'FCF {}'.format(self.token)}
            )

        if response.status_code != 200:
            print ('Something went wrong while sending a message-part message to a channel!', channel_id, response.status_code, response.reason)

    def update_channel_agent_state(self, channel_id, is_composing):
        if self.token == '':
            self.authenticate()        

        request_url = '{}/Collaboration/V1/Channels/{}/Me'.format(self.host, channel_id)

        response = requests.post(
            request_url,
            json = {
                'IsComposing': is_composing
            },
            headers = {
                'Accept' : 'application/json',
                'Authorization': 'FCF {}'.format(self.token)}
            )

        if response.status_code != 200:
            print ('Something went wrong while updating channel agent state!', channel_id, response.status_code, response.reason)


    def get_events(self, channel_id, callback):
        if self.token == '':
            self.authenticate()

        self.running = True

        while self.running:
            request_url = '{}/Collaboration/V1/Events'.format(self.host)

            parameters = {
                'last-event': self.last_event,
                'types': ['message'],
                'channels': [channel_id],
                'regex': '',
                'origins': 'remote'
            }

            response = requests.get(
                request_url,
                params = parameters,
                headers = {
                    'Accept' : 'application/json',
                    'Authorization': 'FCF {}'.format(self.token)
                    })

            if response.status_code != 200:
                print ('Something went wrong while getting events!', response.status_code, response.reason)
                continue

            for event in response.json():
                eventId = event['EventId']

                if eventId > self.last_event:
                    self.last_event = eventId                
                
                callback(event)

    def start_streaming(self, channel_id, callback):
        self.events_thread = threading.Thread(target=self.get_events, args = (channel_id, callback))
        self.events_thread.start()

    def stop_streaming(self):
        self.running = False

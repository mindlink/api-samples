"""
MindLink API sample.
This module exposes methods to perform authentication and basic collaboration operations, like getting, sending messages and streaming messages event from channels.

For more information, visit our developer wiki @https://wiki.mindlinksoft.com/tiki-index.php?page=MindLink+API and our engineering blog @https://engineering.mindlinksoft.com/

The samples are provided "as is". Do feel free to experiment or use them as you deem fit. Have fun!

"""

import requests

def authenticate(host, username, password, agent):
    request_url = '{}/Authentication/V1/Tokens'.format(host)

    response = requests.post(request_url, json = {
                'Username': username,
                'Password': password,
                'AgentId': agent
    }, headers = {
        'Accept': 'application/json'
    })

    if response.status_code == 200:
        return response.json()

    print ('Something went wrong!', response.status_code, response.reason)


def get_messages(host, token, channel_id, count):
    request_url = '{}/Collaboration/V1/Channels/{}/Messages'.format(host, channel_id)

    response = requests.get(
        request_url,
        params = {
        'take': count,
        },
        headers = {
            'Accept' : 'application/json',
            'Authorization': 'FCF {}'.format(token)}
        )

    if response.status_code == 200:
        return response.json()

    print ('Something went wrong!', response.status_code, response.reason)

def send_message(host, token, channel_id, content, is_alert):
    requestUrl = '{}/Collaboration/V1/Channels/{}/Messages'.format(host, channel_id)

    response = requests.post(
        requestUrl,
        json = {
            'Text': content,
            'IsAlert': is_alert
        },
        headers = {
            'Accept' : 'application/json',
            'Authorization': 'FCF {}'.format(token)}
        )

    if response.status_code != 200:
        print ('Something went wrong!', response.status_code, response.reason)

def get_events(host, token, channel_id, last_event_id):
    request_url = '{}/Collaboration/V1/Events'.format(host)

    parameters = {
        'last-event': last_event_id,
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
            'Authorization': 'FCF {}'.format(token)
        })

    if response.status_code != 200:
        print ('Something went wrong while getting events!', response.status_code, response.reason)
        return

    return response.json()


host = 'http://localhost:8081'
user_name = 'devlync2013\\faddad'
password = 'Password1'
agent = 'agent_1'
chat = 'chat-room:d343ec64-c867-4edf-9daf-84f25a3c8ed4'

token = authenticate(host, user_name, password, agent)

print('Here`s my token!', token)

messages = get_messages(host, token, chat, 2)

print('here are my messages!')
for message in messages:
    print('message ID: ', message['Id'], ' Alert? ', message['IsAlert'], ' Sender ', message['SenderId'], ' Text ', message['Text'])

send_message(host, token, chat, "hello world", True)

events = get_events(host, token, chat, -1)

print('here are my events!', events)

"""
MindLink API samples.

For more information, visit our developer wiki @https://wiki.mindlinksoft.com/tiki-index.php?page=MindLink+API and our engineering blog @https://engineering.mindlinksoft.com/

The samples are provided "as is". Do feel free to experiment or use them as you deem fit. Have fun!

sample_bot.py

This module shows basic usages of the ApiConnection.

"""

from api_connection import ApiConnection

def on_message_received(message_event):
    print('got a message event!', message_event)

host = 'http://localhost:8081'
user_name = 'domain\\user'
password = 'secret_password'
agent = 'agent_1'
chat = 'chat-room:guid'
message_content = 'hello world!'

connection = ApiConnection(host, user_name, password, agent)

token = connection.authenticate()

print('got a token', token)

messages = connection.get_messages(chat, 2)

print('here are my messages')
for message in messages:
    print('message ID: ', message['Id'], ' Alert? ', message['IsAlert'], ' Sender ', message['SenderId'], ' Text ', message['Text'])

print('sending a message')
connection.send_message(chat, message_content, True)

print('starting to stream')
connection.start_streaming(chat, on_message_received)

input('press enter to quit\n')

connection.stop_streaming()
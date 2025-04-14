# Chat Client-Server Application

A Java-based chat application that allows multiple clients to send text messages and files to each other through a central server. Text messages are encrypted using the PlayFair cipher algorithm.

## Features

- Send and receive text messages encrypted with PlayFair cipher
- Send and receive files (audio, video, images)
- User identification with unique usernames
- Graphical user interface built with Java Swing

## Requirements

- Java Development Kit (JDK) 8 or higher

## Project Structure

```
ChatClientServer-LTM/
├── src/
│   ├── crypto/
│   │   └── PlayFairCipher.java
│   ├── server/
│   │   ├── Server.java
│   │   └── ClientHandler.java
│   ├── client/
│   │   ├── Client.java
│   │   ├── Main.java
│   │   ├── model/
│   │   │   └── ChatModel.java
│   │   ├── view/
│   │   │   ├── ConnectionDialog.java
│   │   │   └── ChatWindow.java
│   │   └── controller/
│   │       └── ChatController.java
│   └── common/
│       ├── Message.java
│       └── MessageType.java
├── bin/
├── received_files/
├── run_server.bat
└── run_client.bat
```

## How to Compile

```
javac -d bin src/client/Client.java src/client/Main.java src/client/controller/ChatController.java src/client/model/ChatModel.java src/client/view/ChatWindow.java src/client/view/ConnectionDialog.java src/common/Message.java src/common/MessageType.java src/crypto/PlayFairCipher.java src/server/ClientHandler.java src/server/Server.java
```

## How to Run

1. Start the server by running `run_server.bat` or executing:
   ```
   java -cp bin server.Server
   ```

2. Start one or more clients by running `run_client.bat` or executing:
   ```
   java -cp bin client.Main
   ```

3. In the client connection dialog, enter:
   - Server Address: localhost (or the IP address of the server)
   - Server Port: 12345
   - Username: Your unique username

4. Click "Connect" to join the chat.

## Usage

- To send a text message, type in the text field and press Enter or click "Send".
- To send a file, click "Send File", select a file, and click "Open".
- Received files are saved in the `received_files` directory.

## Design Patterns

- **Singleton Pattern**: Used for the server to ensure only one instance exists.
- **MVC Pattern**: Used for the client to separate the model (data), view (UI), and controller (logic).
- **Observer Pattern**: Used to update the view when the model changes.

## Implementation Details

- **PlayFair Cipher**: Text messages are encrypted using the PlayFair cipher algorithm.
- **Socket Communication**: TCP sockets are used for reliable communication between clients and server.
- **Thread Safety**: Thread-safe collections are used to handle multiple clients concurrently.
- **File Transfer**: Files are sent as byte arrays and saved on the receiving client's machine.

## License

This project is open source and available under the MIT License.

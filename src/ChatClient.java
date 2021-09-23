import java.io.*;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */
    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /**
     * Attempts to establish a connection with the server
     *
     * @return boolean - false if any errors occur in startup, true if successful
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            System.out.println("Server is not currently running. The client could not be started.");
            return false;
        }
        if (socket.isConnected()) {
            // Attempt to create output stream
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
//            e.printStackTrace();
                return false;
            }

            // Attempt to create input stream
            try {
                sInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
//            e.printStackTrace();
                return false;
            }

            // Create client thread to listen from the server for incoming messages
            Runnable r = new ListenFromServer();
            Thread t = new Thread(r);
            t.start();

            // After starting, send the clients username to the server.
            try {
                sOutput.writeObject(username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }//end of start


    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end of sendMessage


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        String[] parameters = input.split(" ");
        String javaHandle = "java";
        String chatHandle = "chatClient";
        String username = "Anonymous";
        int portNumber = 1500;
        String serverAddress = "localhost";
        MessageType messageType = null;

        if (parameters.length >= 3 && parameters[2] != null) {
            username = parameters[2];
        }
        if (parameters.length >= 4 && parameters[3] != null) {
            portNumber = Integer.parseInt(parameters[3]);
        }
        if (parameters.length == 5 && parameters[4] != null) {
            serverAddress = parameters[4];
        }


        // Create your client and start it
        ChatClient client = new ChatClient(serverAddress, portNumber, username);

        if (client.start()) {
            // Send an empty message to the server
            System.out.println("Connection accepted " + serverAddress + "/" + username + ":" + client.port);
            client.sendMessage(new ChatMessage());

            String clientToServer;
            String recipient;
            while (true) {
                String message = in.nextLine();
                clientToServer = "";
                recipient = "";
                if (message.toLowerCase().equals("/logout")) {
                    messageType = MessageType.LOGOUT;
                    break;
                } else if (message.startsWith("/ttt")) {
                    messageType = MessageType.TICTACTOE;
                    String[] breakdown = message.split(" ");
                    if (breakdown.length >= 2) {
                        recipient = breakdown[1];
                    }

                    if (breakdown.length == 3) {
                        clientToServer = breakdown[2];
                    }
                } else if (message.startsWith("/list")) {
                    messageType = MessageType.LIST;
                } else if (message.startsWith("/msg")) {
                    messageType = MessageType.DM;
                    String[] breakdown = message.split(" ");
                    if (breakdown.length >= 2) {
                        recipient = breakdown[1];
                    }
                    if (breakdown.length >= 3) {
                        for (int i = 2; i < breakdown.length; i++) {
                            clientToServer += " " + breakdown[i];
                        }
                    }
                } else {
                    messageType = MessageType.MESSAGE;
                    clientToServer = message;
                }
                client.sendMessage(new ChatMessage(messageType, clientToServer, recipient));
            }//end of while
            client.sendMessage(new ChatMessage(messageType, clientToServer, recipient));
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                try {
                    //reading the message that is in the server
                    Object read = sInput.readObject();
                    String msg;
                    if (read instanceof ChatMessage) {
                        ChatMessage cm = (ChatMessage) read;
                        msg = cm.getMessage();
                    } else {
                        msg = (String) read;
                        System.out.print(msg);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Server has closed the connection.");
                    break;
                }//end of catch
            }//end of while
        }//end of run
    }
}
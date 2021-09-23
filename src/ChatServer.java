import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

final class ChatServer {
    private static int uniqueId = 0;
    // Data structure to hold all of the connected clients
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;            // port the server is hosted on

    /**
     * ChatServer constructor
     *
     * @param port - the port the server is being hosted on
     */
    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sample code to use as a reference for Tic Tac Toe
     * <p>
     * directMessage - sends a message to a specific username, if connected
     *
     * @param //message  - the string to be sent
     * @param //username - the user the message will be sent to
     */

    /*private synchronized void directMessage(String message, String username) {
        String time = sdf.format(new Date());
        String formattedMessage = time + " " + message + "\n";
        System.out.print(formattedMessage);

        for (ClientThread clientThread : clients) {
            if (clientThread.username.equalsIgnoreCase(username)) {
                clientThread.writeMsg(formattedMessage);
            }
        }
    }*/


    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        String[] parameters = input.split(" ");
        int portNumber;
        if (parameters.length == 3 && parameters[2] != null) {
            portNumber = Integer.parseInt(parameters[2]);
        } else {
            portNumber = 1500;
        }
        ChatServer server = new ChatServer(portNumber);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        System.out.println(time + " Server waiting for clients on port " + portNumber + ".");
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                  // The socket the client is connected to
        ObjectInputStream sInput;       // Input stream to the server from the client
        ObjectOutputStream sOutput;     // Output stream to the client from the server
        String username;                // Username of the connected client
        ChatMessage cm;                 // Helper variable to manage messages
        int id;                         // Identification variable for each clients
        Object lock = new Object();     //Object variable to synchronize
        ArrayList<String> tictactoeplayerslist = new ArrayList<>();
        boolean currentplayer;
        ArrayList<TicTacToeGame> ticTacToeGamelist = new ArrayList<>();
        boolean continuing = true;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//formatting the time
        String time = sdf.format(new Date());//formatting the time

        /*
         * socket - the socket the client is connected to
         * id - id of the connection
         */
        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                System.out.println(time + " " + username + " is connected");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(time + " " + username + "was unable to connect.");
//                e.printStackTrace();
            }
        }//end of ClientThread

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            while (continuing) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("The message could not be read. Please try again,");
//                    e.printStackTrace();
                    break;
                }

                //check which type cm is, and handle it based on the type, then broadcast the message in the format: username + ": " + message
                if (cm.getMessageType() != null) {
                    switch (cm.getMessageType()) {
                        case TICTACTOE:
                            if (username.equals(cm.getRecipient())) {
                                writeMessage("You cannot play with yourself\n");
                            } else {
                                currentplayer = false;

                                if (cm.getMessage().equals("")) {
                                    for (String tictactoeplayer : tictactoeplayerslist) {
                                        if (tictactoeplayer.equalsIgnoreCase(cm.getRecipient())) {
                                            currentplayer = true;
                                            break;
                                        }
                                    }
                                }//end of if
                                if (!currentplayer && cm.getMessage().equals("")) {
                                    for (ClientThread clientThread : clients) {
                                        if (clientThread.username.equalsIgnoreCase(cm.getRecipient())) {
                                            clientThread.ticTacToeGamelist.add(new TicTacToeGame());
                                            for (TicTacToeGame ticTacToeGame : clientThread.ticTacToeGamelist) {
                                                if (ticTacToeGame.getOpponent() == null) {
                                                    ticTacToeGame.TicTacToeGame(username, false);
                                                }
                                            }
                                            clientThread.tictactoeplayerslist.add((username));
                                        }
                                        if (clientThread.username.equalsIgnoreCase(username)) {
                                            clientThread.ticTacToeGamelist.add(new TicTacToeGame());
                                            for (TicTacToeGame ticTacToeGame : clientThread.ticTacToeGamelist) {
                                                if (ticTacToeGame.getOpponent() == null) {
                                                    ticTacToeGame.TicTacToeGame(cm.getRecipient(), true);
                                                }
                                            }
                                            directMessage("Started TicTacToe with ", cm.getRecipient(), true);
                                            clientThread.tictactoeplayerslist.add(cm.getRecipient());
                                        }
                                    }
                                } else if (currentplayer) {
                                    for (TicTacToeGame ticTacToeGame : ticTacToeGamelist) {
                                        if (ticTacToeGame.getOpponent().equals(cm.getRecipient())) {
                                            for (ClientThread clientThread : clients) {
                                                if (clientThread.username.equalsIgnoreCase(cm.getRecipient())) {
                                                    for (TicTacToeGame ticTacToeGameopponent : clientThread.ticTacToeGamelist) {
                                                        if (ticTacToeGameopponent.getOpponent().equals(username)) {
                                                            printBoard(ticTacToeGameopponent.synchronizeboard(ticTacToeGame.getBoard()), null);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //play the game
                                int move = -1;
                                if (!cm.getMessage().equals("") && Character.isDigit(cm.getMessage().charAt(0))) {
                                    move = Integer.parseInt(cm.getMessage());
                                }

                                if (move != -1) {
                                    makeMove(move);
                                }//end of if (move)

                                for (TicTacToeGame ticTacToeGame : ticTacToeGamelist) {// obtain the list of tictactoe game list in player 1
                                    //username-player 1
                                    //cm.getReciipient-player2
                                    if (ticTacToeGame.getOpponent().equals(cm.getRecipient())) {
                                        //find the game with the opponent of that tictactoe game who is the player 2
                                        if (ticTacToeGame.getWinner() != 'S') { // if the winner is declared
                                            if (endGame(ticTacToeGame)) {
                                                break;
                                            }
                                        }
                                    }//end of if (Tictactoegame.getOpponent())
                                }//end of for (TicTactoeGame)
                            }//end of else
                            break;
                        case LIST:
                            writeMessage(time + " List of users connected: ");
                            int count = 0;
                            for (int i = 0; i < clients.size(); i++) {
                                ClientThread clientThread = clients.get(i);
                                if (!clients.get(i).username.equals(username)) {
                                    writeMessage(clientThread.username);
                                    count++;
                                    if (count != clients.size() - 1) {
                                        writeMessage(", ");
                                    }
                                }
                                if (i == clients.size() - 1) {
                                    writeMessage("\n");
                                    break;
                                }
                            }//end of for
                            break;
                        case DM:
                            if (!cm.getRecipient().equals(username)) {
                                directMessage(username + " -> " + cm.getRecipient() + cm.getMessage(), cm.getRecipient(), false);
                            }
                            break;
                        case LOGOUT:
                            System.out.println(time + " " + username + " disconnected with a LOGOUT message");
                            continuing = false;
                            break;
                        case MESSAGE:
                            broadcast(username + ": " + cm.getMessage());
                            break;
                    }
                    // Send message back to the client
                    try {
                        sOutput.writeObject(cm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//end of if
                if (cm.getMessage() != "" && cm.getMessageType() == null) {
                    int count = 0;
                    for (ClientThread clientThread : clients) {
                        if (clientThread.username.equals(this.username) && count <= 1) {
                            count++;
                            if (count == 2) {
                                clientThread.writeMessage("The username " + username + " already exists.\n");
                                continuing = false;
                                break;
                            }
                        }
                    }
                }
            }//end of while
            remove(id);
            close();
        }//end of run

        private boolean writeMessage(String msg) {
            //Return false if the socket is not connected and true otherwise
            if (!socket.isConnected()) {//check if the socket is connected
                close();
                return false;
            } else {//socket is connected
                try {
                    sOutput.writeObject(msg);//write the message to the ClientThread's ObjectOutputStream
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } //end of WriteMessage

        private synchronized void broadcast(String message) {
            String formattedMessage = time + " " + message + "\n";
            System.out.print(formattedMessage);
            for (int i = clients.size() - 1; i >= 0; i--) {//broadcast the message to everyone in the clients arraylist
                ClientThread clientThread = clients.get(i);
                if (!clientThread.writeMessage(formattedMessage)) {
                    remove(i);
                    writeMessage(time + "User " + clientThread.username + " is disconnected.");
                }
//                clientThread.writeMessage(formattedMessage);
            }//end of for loop
        }//end of broadcast


        private synchronized void directMessage(String message, String recipient, boolean check) {
            boolean sent = false;
            String formattedMessage;
            if (recipient == null) {
                writeMessage(message);
            } else {
                if (cm.getMessageType() == MessageType.TICTACTOE && check) {
                    formattedMessage = message;
                } else {
                    formattedMessage = time + " " + message + "\n";
                }
                for (ClientThread clientThread : clients) {//check through each of the clients in the arraylist
                    if (clientThread.username.equalsIgnoreCase(cm.getRecipient())) {
                        //if the username of the client in the arraylist is equal to the username of the recipient
                        if (formattedMessage.equals("Started TicTacToe with ") || formattedMessage.equals("Board with ")) {
                            formattedMessage += username + "\n";
                        }
                        clientThread.writeMessage(formattedMessage);
                        sent = true;
                    }//end of if
                }//end of for loop

                if (!sent) {
                    formattedMessage = "User is not founded. Message not sent.\n";
                    System.out.println(formattedMessage);
                    writeMessage(formattedMessage);
                    return;
                } else {
                    if (cm.getMessageType() == MessageType.TICTACTOE && check) {
                        formattedMessage = message;
                    } else {
                        formattedMessage = time + " " + message + "\n";
                    }
                    for (ClientThread clientThread : clients) {
                        if (clientThread.username.equalsIgnoreCase(username)) {
                            {
                                if (formattedMessage.equals("Started TicTacToe with ") || formattedMessage.equals("Board with ")) {
                                    formattedMessage += cm.getRecipient() + "\n";
                                }
                                clientThread.writeMessage(formattedMessage);
                            }
                        }//end of if
                    }//end of for loop
                    if (!check) {
                        System.out.print(formattedMessage);
                    }
                }
            }
        }//end of directMessage

        private void remove(int id) {
            synchronized (lock) {
                for (ClientThread clientThread : clients) {
                    if (clientThread.id == id) {
                        clients.remove(clientThread);
                        return;
                    }//end of if
                }//end of for
            }//end of synchronized
        }//end of remove

        private void close() {
            try {
                if (socket != null) {//check if the socket exists
                    socket.close();
                }
                if (sOutput != null) {//check if the ObjectOutputStream exists
                    sOutput.close();
                }
                if (sInput != null) {//check if the ObjectInputStream exists
                    sInput.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end of close

        private void printBoard(char[][] board, String recipient) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (j == board.length - 1) {
                        directMessage(" " + board[i][j] + "\n", recipient, true);
                    } else {
                        directMessage(" " + board[i][j] + " |", recipient, true);
                    }
                }
                if (i != board.length - 1) {
                    directMessage("------------\n", recipient, true);
                }
            }
        }

        private void makeMove(int move) {
            boolean found = false;
            boolean error = false;
            for (TicTacToeGame ticTacToeGame : ticTacToeGamelist) {
                if (ticTacToeGame.getOpponent().equals(cm.getRecipient())) {
                    for (ClientThread clientThread : clients) {
                        if (clientThread.username.equalsIgnoreCase(cm.getRecipient())) {
                            if (ticTacToeGame.takeTurn(move) == -1) {
                                writeMessage("Its not your turn.\n");
                                error = true;
                            } else if (ticTacToeGame.takeTurn(move) == 0) {
                                writeMessage("Please enter a valid index.\n");
                                error = true;
                            } else {//if (move != -1) {
                                directMessage("Board with ", cm.getRecipient(), true);
                                for (TicTacToeGame ticTacToeGameopponent : clientThread.ticTacToeGamelist) {
                                    if (ticTacToeGameopponent.getOpponent().equals(username)) {
                                        printBoard(ticTacToeGameopponent.synchronizeboard(ticTacToeGame.getBoard()), cm.getRecipient());
                                        ticTacToeGameopponent.processturn();
                                        found = true;
                                        break;
                                    }//end of if (TicTacToeGame)
                                }//end of for
                            }//end of else
                        }//end of if (Clientthread.username)
                    }//end of for (Clientthread)
                }//end of if (getOpponent)
            }//end of for GameList
            if (!found && !error) {
                writeMessage("There is no game playing with " + cm.getRecipient() + "\n");
            }
        }

        private boolean endGame(TicTacToeGame ticTacToeGame) {
            boolean enduser = false;
            boolean endopponent = false;

            if (ticTacToeGame.getWinner() == 'T') {//if there is no winner- tied
                directMessage("Tied!\n", cm.getRecipient(), true);//print the statement of game ending
            } else {
                //print the statement with the winner
                directMessage("Player " + ticTacToeGame.getWinner() + " won!\n", cm.getRecipient(), true);
            }

            if (!enduser) {//for the player 1
                //remove the player 2 from the player 1's player list
                tictactoeplayerslist.remove(cm.getRecipient());
                //remove the game from the player 1's game list
                ticTacToeGamelist.remove(ticTacToeGame);
                enduser = true;
            }
            if (!endopponent) {
                for (ClientThread clientThread : clients) {//loop through the client thread
                    //find the client in the client thread who is the player 2
                    if (!endopponent && clientThread.username.equalsIgnoreCase(cm.getRecipient())) {
                        //if the client from the list is the player 2
                        //remove the player 1 from the player 2's player list
                        clientThread.tictactoeplayerslist.remove(username);
                        //loop through the player 2's game list
                        for (TicTacToeGame ticTacToeGameOpponent : clientThread.ticTacToeGamelist) {
                            //if the game with the opponent of the tictactoe game is the player 1
                            if (ticTacToeGameOpponent.getOpponent().equals(username)) {
                                //remove the game from the player 2's list
                                clientThread.ticTacToeGamelist.remove(ticTacToeGameOpponent);
                                endopponent = true;
                                break;
                            }//end of if
                        }//end of for (TictactoeGame)
                    }//end of if
                    if (endopponent) {//if already removed, stop looping through
                        break;
                    }//end of if (endopponent)
                }//end of for (clients)
            }//end of if (!endopponent)
            if (enduser && endopponent) {
                return true;
            }
            return false;
        }//end of endgame method
    }//end of clientThread method

}
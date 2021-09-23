import java.io.*;
//import java.io.IOException;
//import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.Scanner;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Types of messages
    //static final int MESSAGE = 0, LOGOUT = 1, DM = 2, LIST = 3, TICTACTOE = 4;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
    private String message;
    private String recipient;
    private MessageType messageType;

    public ChatMessage() {
        this.message = " ";
    }

    public ChatMessage(MessageType messageType, String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }

    public MessageType getMessageType() {
        return messageType;
    }

}

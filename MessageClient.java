import java.io.*;
import java.net.*;

public class MessageClient implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public MessageClient(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public void userLogin(String username, String password) {
        try {
            String loginRequest = "LOGIN:" + username + ":" + password;
            out.writeObject(loginRequest);
            out.flush();
            Object response = in.readObject();
            if (response instanceof String) {
                String serverResponse = (String) response;
                if (serverResponse.equals("SUCCESS")) {
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Login failed: " + serverResponse);
                }
            } else {
                System.err.println("Unexpected response from server during login.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }

    public void sendMessage(String sender, String receiver, String content) {
        try {
            Message message = new Message(sender, receiver, content);
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent to server: " + content);
        } catch (IOException e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }

    public void handleMessage() {
        try {
            Object response = in.readObject();
            if (response instanceof Message) {
                Message receivedMessage = (Message) response;
                System.out.println("New message from " + receivedMessage.getSender() + ": " + receivedMessage.getContent());
            } else {
                System.err.println("Invalid response received from server.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while handling message: " + e.getMessage());
        }
    }
}

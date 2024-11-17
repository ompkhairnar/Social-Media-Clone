import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocialMediaClient implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening = true;

    public SocialMediaClient(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (listening) {
                handleMessage();
            }
        } catch (Exception e) {
            System.err.println("Error in message listener: " + e.getMessage());
        }
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
            listening = false; // Stop listening if there's a fatal error
        }
    }

    /*
    public void close() {
        try {
            listening = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error while closing client: " + e.getMessage());
        }
    }
    */

    public static void main(String[] args) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        try {
            SocialMediaClient client = new SocialMediaClient(host, portNumber);

            // Start the thread to listen for incoming messages
            Thread listenerThread = new Thread(client);
            listenerThread.start();

            System.out.println("Enter your username:");
            String username = sc.nextLine();
            System.out.println("Enter your password:");
            String password = sc.nextLine();

            client.userLogin(username, password);

            boolean done = false;
            while (!done) {
                System.out.println("Enter Choice:\n1. Send Message\n2. Block User\n3. Add Friend\n4. Remove Friend\n5. Exit");
                String choice = sc.nextLine();

                switch (choice) {
                    case "1":
                        System.out.println("Enter recipient username:");
                        String recipient = sc.nextLine();
                        System.out.println("Enter message content:");
                        String content = sc.nextLine();
                        client.sendMessage(username, recipient, content);
                        break;

                    case "2":
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        // function
                        break;

                    case "3":
                        System.out.println("Enter username to add as a friend:");
                        String addUsername = sc.nextLine();
                        // function
                        break;

                    case "4":
                        System.out.println("Enter username to remove from friends:");
                        String removeUsername = sc.nextLine();
                        // function
                        break;

                    case "5":
                        done = true;
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

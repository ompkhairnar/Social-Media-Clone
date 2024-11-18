import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocialMediaClient implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean listening = true;

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

    public boolean userLogin(String username, String password) {
        try {
            String loginRequest = "LOGIN:" + username + ":" + password;
            out.writeObject(loginRequest);
            out.flush();
            System.out.printf("Sent login request for user: %s\n", username);

            Object response = in.readObject();
            if (response instanceof String) {
                String serverResponse = (String) response;
                if (serverResponse.equalsIgnoreCase("SUCCESS")) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    System.out.println("Login failed: " + serverResponse);
                    return false;
                }
            } else {
                System.err.println("Unexpected response from server.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return false;
    }

    public void sendMessage(String sender, String receiver, String content) {
        try {
            Message message = new Message(sender, receiver, content);
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent to server: " + message);

        } catch (IOException e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }

    public void handleMessage() {
        try {
            Object response = in.readObject();
            if (response instanceof Message) {
                Message receivedMessage = (Message) response;
                System.out.printf("New message from %s: %s\n",
                        receivedMessage.getSender(),
                        receivedMessage.getContent());
            } else if (response instanceof String) {
                System.out.println("Server notification: " + response);
            } else {
                System.err.println("Invalid response received from server.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while handling message: " + e.getMessage());
            listening = false;
        }
    }

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

    public static void main(String[] args) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        try {
            SocialMediaClient client = new SocialMediaClient(host, portNumber);

            // Start listener thread
            Thread listenerThread = new Thread(client);
            listenerThread.start();

            System.out.println("Enter your username:");
            String username = sc.nextLine();
            System.out.println("Enter your password:");
            String password = sc.nextLine();

            if (!client.userLogin(username, password)) {
                System.out.println("Exiting due to failed login.");
                client.close();
                return;
            }

            boolean done = false;
            while (!done) {
                System.out.println("""
                        Enter Choice:
                        1. Send Message
                        2. Exit
                        """);
                String choice = sc.nextLine();

                switch (choice) {
                    case "1":
                        System.out.println("Enter the username of the recipient:");
                        String recipient = sc.nextLine();
                        System.out.println("Enter your message:");
                        String content = sc.nextLine();
                        client.sendMessage(username, recipient, content);
                        break;
                    case "2":
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
        } finally {
            sc.close();
        }
    }
}

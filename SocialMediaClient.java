import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocialMediaClient implements Runnable, Serializable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening = true;
    private String sender;
    private String receiver;
    private String content;

    public SocialMediaClient(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
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
                System.out.println(
                        "New message from " + receivedMessage.getSender() + ": " + receivedMessage.getContent());
            } else {
                System.err.println("Invalid response received from server.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while handling message: " + e.getMessage());
            listening = false; // Stop listening if there's a fatal error
        }
    }

    public static void main(String[] args) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        try (Socket socket = new Socket(host, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login process
            System.out.println(in.readLine()); // Server: Enter username
            String username = sc.nextLine();
            out.println(username);

            System.out.println(in.readLine()); // Server: Enter password
            String password = sc.nextLine();
            out.println(password);

            // Handle login response
            String loginResponse = in.readLine();
            System.out.println(loginResponse);
            if (!loginResponse.startsWith("Successfully")) {
                return;
            }

            boolean done = false;
            while (!done) {
                // Display menu
                System.out.println("""
                        Enter Choice:
                        1. Block User
                        2. Add User
                        3. Remove Friend
                        4. Send Message
                        5. Exit
                        """);
                String choice = sc.nextLine();
                out.println(choice);

                switch (choice) {
                    case "1": // Block a user
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
                        break;
                    case "2": // Add a user
                        System.out.println("Enter username to add:");
                        String addUsername = sc.nextLine();
                        out.println(addUsername);
                        break;
                    case "3": // Remove a friend
                        System.out.println("Enter username to remove:");
                        String removeUsername = sc.nextLine();
                        out.println(removeUsername);
                        break;
                    case "4": // Send a message
                        System.out.println("Enter the username of the recipient:");
                        String recipientUsername = sc.nextLine();
                        out.println(recipientUsername);

                        System.out.println("Enter your message:");
                        String messageContent = sc.nextLine();
                        out.println(messageContent);
                        break;
                    case "5": // Exit the client
                        done = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                // Display server's response
                String serverResponse = in.readLine();
                System.out.println("Server: " + serverResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}

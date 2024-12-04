<<<<<<< HEAD
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
=======
import java.io.*;
>>>>>>> bb5888e5fbd0589784431b6f11bb01d789b25624
import java.net.Socket;
import java.util.Scanner;

/**
 * SocialMediaClient class that connects to a social media platform server
 * and integrates local message storage with the Message class.
 *
 * <p>
 * Purdue University -- CS18000 -- Fall 2024
 * </p>
 *
 * @author Sawyer, Bidit, Richard, Om
 * @version 1.0 November 17th, 2024
 */
public class SocialMediaClient {

<<<<<<< HEAD
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
                try {
                    handleMessage();
                } catch (Exception e) {
                    System.err.println("Error in message listener: " + e.getMessage());
                    listening = false; // stop listening
                    break;
                }
            }
        } finally {
            close();
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
            // Create User objects for sender and receiver
            User senderUser = new User(sender);
            User receiverUser = new User(receiver);

            // Create and use Message object
            Message messageHandler = new Message(senderUser, receiverUser, content);

            // Store the message locally
            messageHandler.messageUser(receiverUser, content);

            // Send the message to the server
            out.writeObject(messageHandler);
            out.flush();

            System.out.println("Message sent and stored locally.");
        } catch (UserException | IOException e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }

    public void retrieveMessages(String sender, String receiver) {
        try {
            // Create User objects for sender and receiver
            User senderUser = new User(sender);
            User receiverUser = new User(receiver);

            // Use the Message object to retrieve local messages
            Message messageHandler = new Message(senderUser);
            String messages = messageHandler.getMessages(receiverUser);

            System.out.println("Message history:\n" + messages);
        } catch (UserException e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
    }

    public void handleMessage() {
        try {
            Object response = in.readObject();
            if (response instanceof Message) {
                Message receivedMessage = (Message) response;
                System.out.printf("New message from %s: %s\n",
                        receivedMessage.getMessager().getUsername(),
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
        listening = false; // stop listening
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Client closed successfully.");
        } catch (IOException e) {
            System.err.println("Error while closing client: " + e.getMessage());
        }
    }

=======
>>>>>>> bb5888e5fbd0589784431b6f11bb01d789b25624
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
            if (!loginResponse.equals("Successfully Logged In!")) {
                return; // Exit if login fails
            }

            boolean done = false;
            while (!done) {
                // Display menu
                System.out.println("""
                        Enter Choice:
                        1. Block User
                        2. Add Friend
                        3. Remove Friend
                        4. Send Message
                        5. Retrieve Messages
                        6. Exit
                        """);
                String choice = sc.nextLine();
                out.println(choice);

                switch (choice) {
<<<<<<< HEAD
                    case "1":
                        System.out.println("Enter the username of the recipient:");
                        String recipient = sc.nextLine();
                        out.println(recipient);

                        System.out.println("Enter your message:");
                        String content = sc.nextLine();
                        out.println(content);
=======
                    case "1": // Block a user
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
>>>>>>> bb5888e5fbd0589784431b6f11bb01d789b25624
                        break;
                    case "2": // Add a friend
                        System.out.println("Enter username to add:");
                        String addUsername = sc.nextLine();
                        out.println(addUsername);
                        break;
                    case "3": // Remove a friend
                        System.out.println("Enter username to remove:");
                        String removeUsername = sc.nextLine();
                        out.println(removeUsername);
                        break;
<<<<<<< HEAD
                    case "5":
                        System.out.println("Enter the username of the user to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
                        break;
                    case "6":
=======
                    case "4": // Send a message
                        System.out.println("Enter the username of the recipient:");
                        String recipientUsername = sc.nextLine();
                        out.println(recipientUsername);

                        System.out.println("Enter your message:");
                        String messageContent = sc.nextLine();
                        out.println(messageContent);
                        break;
                    case "5": // Retrieve messages
                        System.out.println("Enter the username to retrieve messages with:");
                        String otherUser = sc.nextLine();
                        out.println(otherUser);
                        String messages = in.readLine();
                        System.out.println("Message history:\n" + messages);
                        continue; // Skip the server response reading at the end of the loop
                    case "6": // Exit the client
>>>>>>> bb5888e5fbd0589784431b6f11bb01d789b25624
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

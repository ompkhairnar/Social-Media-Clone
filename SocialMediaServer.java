import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Social Media Server class that connects to a social media platform server
 * and integrates local message storage with the Message class.
 *
 * <p>
 * Purdue University -- CS18000 -- Fall 2024
 * </p>
 *
 * @author Sawyer, Bidit, Richard, Om
 * @version 1.0 November 17th, 2024
 */
public class SocialMediaServer implements Runnable, SocialMediaServerInterface {
    private static final int PORT = 4545;
    private ServerSocket serverSocket;
    private FoundationDatabase database;
    private boolean running;

    public SocialMediaServer(FoundationDatabase database) {
        this.database = database;
        this.running = true;
    }

    // Starts the server by opening a ServerSocket and waiting for client
    // connections
    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Starts the server and listens for incoming client connections
    @Override
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (running) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    // Stops the server and closes the server socket
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns if the server is running (true if running)
    public boolean isRunning() {
        return running;
    }

    // Handles a client connection by starting a new ClientHandler for the given
    // socket
    @Override
    public void handleClient(Socket clientSocket) throws IOException {
        new Thread(new ClientHandler(clientSocket)).start();
    }

    // Inner class that handles communication with a connected client
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // Processes input from the client and sends responses
        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Login process
                out.println("Enter Username:");
                String username = in.readLine();
                out.println("Enter Password:");
                String password = in.readLine();

                User user;
                try {
                    user = new User(username, password);
                    out.println("Successfully Logged In!"); // Send successful login to client
                    System.out.println("User logged in: " + username); // Printed to server terminal
                } catch (UserException e) {
                    out.println("Login failed: " + e.getMessage());
                    return;
                }

                boolean done = false;
                while (!done) {
                    String choice = in.readLine();

                    switch (choice) {
                        case "1": // Block User
                            String blockUsername = in.readLine();
                            try {
                                user.blockUser(blockUsername);
                                out.println("User blocked successfully."); // Send block successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "2": // Add User
                            String addUsername = in.readLine();
                            try {
                                user.addUser(addUsername);
                                out.println("User added successfully."); // Send add successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "3": // Remove Friend
                            String removeUsername = in.readLine();
                            try {
                                user.removeFriend(removeUsername);
                                out.println("Friend removed successfully."); // Send remove successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "4": // Message a user
                            try {
                                String recipient = in.readLine(); // Read recipient username
                                String messageContent = in.readLine(); // Read message content

                                // Example of handling message storage or broadcasting
                                Message message = new Message(user); // Assuming `Message` handles storage
                                message.messageUser(recipient, messageContent);

                                out.println("Message sent successfully to " + recipient);
                                System.out.println("Message sent to " + recipient);
                                System.out.println("Message from " + user.getUsername() + " to "
                                        + recipient + ": " + messageContent);
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;


                        case "5": // Exit
                            done = true; // Exits loop
                            out.println("Goodbye!"); // Send goodbye to client
                            break;

                        default:
                            out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Closing everything on finish
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        String filename = "userStorage.csv";
        FoundationDatabase database = new FoundationDatabase(users, filename);
        SocialMediaServer server = new SocialMediaServer(database);
        new Thread(server).start();
    }
}
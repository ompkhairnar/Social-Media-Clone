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
                // out.println("Enter Username:");
                String username = in.readLine();
                System.out.println("USER: " + username);
                // out.println("Enter Password:");
                String password = in.readLine();
                System.out.println("PASS: " + password);

                User user;
                try {
                    user = new User(username, password);
                    out.println("Successfully Logged In!"); // Send successful login to client
                    System.out.println("User logged in: " + username); // Printed to server terminal
                } catch (UserException e) {
                    out.println("Login failed: " + e.getMessage());
                    return;
                }

                // boolean done = false;
                // while (!done) {
                    String choice = in.readLine();
                    System.out.println(choice);
                    switch (choice) {
                        case "1": // Block User
                            String blockUsername = in.readLine();
                            try {
                                user.blockUser(blockUsername);
                                out.println("Success"); // Send block successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "2": // Add User
                            String addUsername = in.readLine();
                            try {
                                user.addUser(addUsername);
                                out.println("Success"); // Send add successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "3": // Remove Friend
                            String removeUsername = in.readLine();
                            try {
                                user.removeFriend(removeUsername);
                                out.println("Success"); // Send remove successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "4": // Message a user
                            try {
                                // Prompt for the recipient's username
                                //out.println("Enter the username of the recipient:");
                                String recipientUsername = in.readLine();

                                // Check if the recipient exists in the FoundationDatabase
                                User recipient = null;
                                synchronized (database.getUsers()) {
                                    for (User u : database.getUsers()) {
                                        if (u.getUsername().equalsIgnoreCase(recipientUsername)) {
                                            recipient = u;
                                            break;
                                        }
                                    }
                                }

                                if (recipient == null) {
                                    out.println("Error: Recipient username does not exist.");
                                    break;
                                }

                                // Check for blocking conditions
                                if (user.getUserBlocked().contains(recipientUsername)) {
                                    out.println("Error: You cannot message a user you have blocked.");
                                    break;
                                }

                                if (recipient.getUserBlocked().contains(user.getUsername())) {
                                    out.println("Error: You have been blocked by this user.");
                                    break;
                                }

                                // Prompt for the message content
                                out.println("Enter your message:");
                                String messageContent = in.readLine();

                                // Use the Message class to append the message to the file
                                try {
                                    Message message = new Message(user);
                                    message.messageUser(recipient.getUsername(), messageContent);

                                    //out.println("Message sent successfully to " + recipient.getUsername() + ".");
                                    out.println("Success");
                                    System.out.println("Message from " + user.getUsername() + " to "
                                            + recipient.getUsername() + ": " + messageContent);
                                } catch (UserException e) {
                                    out.println("Error: " + e.getMessage());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                out.println("An error occurred while processing your request.");
                            }
                            break;
                        
                        case "5":
                            String messagerUser = in.readLine();
                            String userMessaged = in.readLine();
                            try {
                                User messager = new User(messagerUser);
                                Message msg = new Message(messager);
                                String messages = msg.getMessages(userMessaged);
                                out.println(messages);
                            } catch (UserException e) {
                                out.println("Error retrieving messages");
                            }
                        case "6": //Search
                            String searchedUsername = in.readLine();
                            System.out.println(searchedUsername);
                            boolean search = database.search(searchedUsername);
                            //System.out.println("Hello");
                            System.out.println(search);
                            if (search) {
                                //database.getUsers();
                                System.out.println(searchedUsername);
                                out.println(searchedUsername);
                            } else {
                                out.println("User does not exist.");
                            }
                        case "7": // Exit
                            //done = true; // Exits loop
                            out.println("Goodbye!"); // Send goodbye to client
                            break;

                        default:
                            out.println("Invalid choice. Please try again.");
                    }
                //}
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
        //database.readUsers(filename);
        SocialMediaServer server = new SocialMediaServer(database);
        new Thread(server).start();
    }
}

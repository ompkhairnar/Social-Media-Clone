import java.io.*;
import java.net.*;
import java.util.ArrayList;
/**
 * Social Media Server class that connects to a social media platform server
 * and integrates local message storage with the Message class.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
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

    // starts server by opening a ServerSocket and waiting for client connections
    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // starts server and listens for incoming client connections
    @Override
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (running) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    // stops server and also closes server socket
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

    // returns if server is running (true if running)
    public boolean isRunning() {
        return running;
    }

    // handles a client connection by starting a new ClientHandler for the given
    // socket
    @Override
    public void handleClient(Socket clientSocket) throws IOException {
        new Thread(new ClientHandler(clientSocket)).start();
    }

    // inner class that handles communication with a connected client
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // proccesses input from the client and sends responses
        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // login with username and password
                out.println("Enter Username:");
                String username = in.readLine();
                out.println("Enter Password:");
                String password = in.readLine();

                User user;
                try {
                    user = new User(username, password);
                    out.println("Successfully Logged In!"); // send successful login to client
                    System.out.println("User logged in: " + username); // printed to server terminal
                } catch (UserException e) {
                    out.println("Login failed: " + e.getMessage());
                    return;
                }

                boolean done = false;
                while (!done) {
                    String choice = in.readLine();

                    switch (choice) {
                        case "1": // block user
                            String blockUsername = in.readLine();
                            try {
                                user.blockUser(blockUsername);
                                out.println("User blocked successfully."); // send blocked successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "2": // add User
                            String addUsername = in.readLine();
                            try {
                                user.addUser(addUsername);
                                out.println("User added successfully."); // send add successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "3": // remove Friend
                            String removeUsername = in.readLine();
                            try {
                                user.removeFriend(removeUsername);
                                out.println("Friend removed successfully."); // send remove successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;

                        case "4": // Message a user
                            try {
                                // Prompt for the recipient's username
                                out.println("Enter the username of the recipient:");
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
                                    message.messageUser(recipient, messageContent);

                                    out.println("Message sent successfully to " + recipient.getUsername() + ".");
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

                        case "5": // exit
                            done = true; // exits loop
                            out.println("Goodbye!"); // send goodbye to client
                            break;

                        default:
                            out.println("Invalid choice. Please try again.");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // closing everything on finish
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
        String filename = "userDatabase.txt";
        FoundationDatabase database = new FoundationDatabase(users, filename);
        SocialMediaServer server = new SocialMediaServer(database);
        new Thread(server).start();
    }
}

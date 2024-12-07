import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        private User currentUser;

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
                            System.out.println("Received: " + addUsername);
                            try {
                                System.out.println(user.getUsername() + ".addUser(" + addUsername + ")");
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
                            //System.out.println(searchedUsername);
                            boolean search = database.search(searchedUsername);
                            //System.out.println("Hello");
                            //System.out.println(search);
                            if (search) {
                                //database.getUsers();
                                //System.out.println(searchedUsername);
                                String blocked = user.getUserBlocked();
                                ArrayList<String> blockedBlockedList;
                                if (!blocked.equals(" ")) {
                                    blockedBlockedList = new ArrayList<>(Arrays.asList(blocked.split(";")));
                                } else {
                                    System.out.println("hello");
                                    blockedBlockedList = new ArrayList<String>();
                                }
                                if (blockedBlockedList.contains(searchedUsername)) {
                                    //System.out.println("HERRO");
                                    out.println("Blocked");
                                }
                                else
                                    out.println(searchedUsername);
                            } else {
                                out.println("User does not exist.");
                            }
                        case "7": // Exit
                            //done = true; // Exits loop
                            out.println("Goodbye!"); // Send goodbye to client
                            break;

                        case "8": //Unblock
                            String unblockedUser = in.readLine();
                            try {
                                user.unblock(unblockedUser);
                                out.println("Success"); // Send block successful to client
                            } catch (UserException e) {
                                out.println("Error: " + e.getMessage());
                            }
                            break;
                        default:
                            out.println("Invalid command.");
                    }
                //}
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void handleBlockUser() {
            try {
                out.println("Enter username to block:");
                String blockUsername = in.readLine();
                if (blockUsername == null || blockUsername.isBlank()) {
                    out.println("Error: Username cannot be empty.");
                    return;
                }
        
                synchronized (database) {
                    try {
                        User currentUser = database.getUser(this.currentUser.getUsername()); // Fetch current user
                        currentUser.blockUser(blockUsername); // Block the user
                        out.println("User blocked successfully.");
                    } catch (UserException e) {
                        out.println("Error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Error: An error occurred while blocking user.");
                e.printStackTrace();
            }
        }

        private void handleAddFriend() {
            try {
                out.println("Enter username to add as a friend:");
                String friendUsername = in.readLine(); // Get the username of the friend to add
                if (friendUsername == null || friendUsername.isBlank()) {
                    out.println("Error: Username cannot be empty.");
                    return;
                }
        
                synchronized (database) { // Ensure thread-safe access to the database
                    try {
                        User currentUser = database.getUser(this.currentUser.getUsername()); // Get logged-in user
                        User friendUser = database.getUser(friendUsername); // Verify the friend exists
        
                        currentUser.addUser(friendUsername); // Add the friend by username
                        out.println("Friend added successfully.");
                        System.out.println(currentUser.getUsername() + " added " + friendUsername + " as a friend.");
                    } catch (UserException e) {
                        out.println("Error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Error: An error occurred while adding friend.");
                e.printStackTrace();
            }
        }
        

        private void handleRemoveFriend() {
            try {
                out.println("Enter username to remove as a friend:");
                String friendUsername = in.readLine(); // Get the username of the friend to remove
                if (friendUsername == null || friendUsername.isBlank()) {
                    out.println("Error: Username cannot be empty.");
                    return;
                }
        
                synchronized (database) { // Ensure thread-safe access to the database
                    try {
                        User currentUser = database.getUser(this.currentUser.getUsername()); // Get logged-in user
                        currentUser.removeFriend(friendUsername); // Remove the friend by username
        
                        out.println("Friend removed successfully.");
                        System.out.println(currentUser.getUsername() + " removed " + friendUsername + " from their friends.");
                    } catch (UserException e) {
                        out.println("Error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Error: An error occurred while removing friend.");
                e.printStackTrace();
            }
        }
        

        private void handleSendMessage() {
            try {
                out.println("Enter the username of the recipient:");
                String recipient = in.readLine();
                out.println("Enter your message:");
                String messageContent = in.readLine();
        
                if (recipient == null || recipient.isBlank() || messageContent == null || messageContent.isBlank()) {
                    out.println("Error: Recipient and message cannot be empty.");
                    return;
                }
        
                synchronized (database) {
                    try {
                        Message message = new Message(currentUser); // Assuming currentUser is tracked
                        message.messageUser(recipient, messageContent);
                        out.println("Message sent successfully to " + recipient + ".");
                    } catch (UserException e) {
                        out.println("Error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Error: An error occurred while sending message.");
                e.printStackTrace();
            }
        }

        private void handleLogin() {
            try {
                out.println("Enter Username:");
                String username = in.readLine();
                out.println("Enter Password:");
                String password = in.readLine();
        
                synchronized (database) {
                    try {
                        currentUser = database.getUser(username); // Fetch user from the database
                        if (currentUser.getUserPassword().equals(password)) {
                            out.println("Successfully Logged In!");
                            System.out.println("User logged in: " + username);
                        } else {
                            out.println("Login failed: Invalid password.");
                        }
                    } catch (UserException e) {
                        out.println("Login failed: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Login failed: An error occurred.");
                e.printStackTrace();
            }
        }

        private void handleSignup() {
            try {
                out.println("Enter Username:");
                String username = in.readLine();
                if (username == null || username.isBlank()) {
                    out.println("Sign up failed: Username cannot be empty.");
                    return;
                }
        
                out.println("Enter Password:");
                String password = in.readLine();
                if (password == null || password.isBlank()) {
                    out.println("Sign up failed: Password cannot be empty.");
                    return;
                }
        
                synchronized (database) {
                    try {
                        database.createUser(username, password); // Create a new user in the database
                        out.println("Account created successfully!");
                        System.out.println("New user signed up: " + username);
                    } catch (Exception e) {
                        out.println("Sign up failed: " + e.getMessage());
                        System.err.println("Sign up failed for username: " + username + " - " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                out.println("Sign up failed: An error occurred while processing the request.");
                e.printStackTrace();
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

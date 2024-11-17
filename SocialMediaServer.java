import java.io.*;
import java.net.*;
import java.util.ArrayList;

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

                        case "4": // exit
                            done = true; // exits loop
                            out.println("Goodbye!"); // send goodbye to client
                            break;
                        
                        case "5":
                            try {
                                ServerSocket serverSocket = new ServerSocket(4242);
                                Socket socket = serverSocket.accept();
                                BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());
                    
                                System.out.println("HEY");
                                String textingUser = "";
                                textingUser = clientReader.readLine();
                                String sentMsg = "";
                                System.out.println("HI");
                                do {
                                    sentMsg = clientReader.readLine();
                                    System.out.println(textingUser);
                                    BufferedReader textReader = new BufferedReader(new FileReader("messages.csv"));
                                    ArrayList<String> messages = new ArrayList<String>();
                                    ArrayList<String> receivedIndices = new ArrayList<String>();
                                    String descString = ""; 
                    
                                    int i = 0;
                                    String line = textReader.readLine();
                                    while (line != null) {
                                        String[] contents = line.split(";");
                                        String sender = contents[0];
                                        String receiver = contents[1];
                                        String message = contents[3];
                                        if (receiver.equals(textingUser)) {
                                            messages.add(message);
                                        } else if (sender.equals(textingUser)) {
                                            messages.add(message);
                                            receivedIndices.add(i + "");
                                        }
                                        i++;
                                        line = textReader.readLine();
                                    }
                                    textReader.close();
                                    messages.add(sentMsg);
                                    String messageString = String.join("\n", messages);
                                    String indexString = String.join("\n", receivedIndices);
                                    clientWriter.write(messageString);
                                    clientWriter.println();
                                    clientWriter.flush();
                                    clientWriter.write(indexString);
                                    clientWriter.println();
                                    clientWriter.flush();
                                } while (sentMsg != null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

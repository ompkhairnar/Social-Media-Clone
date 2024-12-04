import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SocialMediaServerTest {

    private SocialMediaServer server;
    private FoundationDatabase database;
    private final String userStorage = "userStorage.csv";

    @Before
    public void setUp() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        database = new FoundationDatabase(users, userStorage);

        // Initialize test data for userStorage.csv
        try (PrintWriter pw = new PrintWriter(new FileWriter(userStorage))) {
            pw.println("sawyer,password,testFriend1,testBlocked1");
            pw.println("notSawyer,12345,,testBlocked1:testBlocked2");
            pw.println("testFriend1,pass,sawyer,");
        }

        server = new SocialMediaServer(database);
        new Thread(server).start();
    }

    @After
    public void tearDown() throws Exception {
        server.stopServer();
        new File(userStorage).delete();
    }

    @Test
    public void testServerStartsAndStops() throws Exception {
        assertTrue(server.isRunning());
        server.stopServer();
        assertFalse(server.isRunning());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            assertEquals("Enter Username:", in.readLine());
            out.println("sawyer");
            assertEquals("Enter Password:", in.readLine());
            out.println("password");
            assertEquals("Successfully Logged In!", in.readLine());
        }
    }

    @Test
    public void testFailedLogin() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            assertEquals("Enter Username:", in.readLine());
            out.println("nonexistentUser");
            assertEquals("Enter Password:", in.readLine());
            out.println("wrongPassword");
            assertEquals("Login failed: Invalid credentials.", in.readLine());
        }
    }

    @Test
    public void testBlockUser() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Block user
            out.println("1"); // Choose block user option
            out.println("notSawyer");
            assertEquals("User blocked successfully.", in.readLine());
        }
    }

    @Test
    public void testAddFriend() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Add friend
            out.println("2"); // Choose add friend option
            out.println("notSawyer");
            assertEquals("User added successfully.", in.readLine());
        }
    }

    @Test
    public void testRemoveFriend() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Remove friend
            out.println("3"); // Choose remove friend option
            out.println("testFriend1");
            assertEquals("Friend removed successfully.", in.readLine());
        }
    }

    @Test
    public void testSendMessage() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Send message
            out.println("4"); // Choose send message option
            out.println("testFriend1"); // Recipient
            out.println("Hello, testFriend1!"); // Message content
            assertEquals("Message sent successfully to testFriend1.", in.readLine());
        }
    }

    @Test
    public void testRetrieveMessages() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Retrieve messages
            out.println("5"); // Choose retrieve messages option
            out.println("testFriend1"); // User to retrieve messages with
            String response = in.readLine();
            assertNotNull(response); // The response should not be null
            // You might want to add more specific assertions based on the expected format
            // of retrieved messages
        }
    }

    @Test
    public void testExitCommand() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Exit
            out.println("6"); // Choose exit option
            assertEquals("Goodbye!", in.readLine());
        }
    }
}

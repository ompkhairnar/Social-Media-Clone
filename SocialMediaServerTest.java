import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
/**
 * SocialMediaClient class that connects to a social media platform server
 * and integrates local message storage with the Message class.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @author Sawyer, Bidit, Richard, Om
 * @version 1.0 November 17th, 2024
 */

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
            assertTrue(in.readLine().startsWith("Login failed:"));
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
    public void testAddUser() throws Exception {
        try (Socket socket = new Socket("localhost", 4545);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Login
            in.readLine(); // "Enter Username:"
            out.println("sawyer");
            in.readLine(); // "Enter Password:"
            out.println("password");
            in.readLine(); // "Successfully Logged In!"

            // Add user
            out.println("2"); // Choose add user option
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
            out.println("5"); // Choose exit option
            assertEquals("Goodbye!", in.readLine());
        }
    }

    @Test
    public void testMessage() throws Exception {
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
            out.println("4"); // Choose message option
            out.println("notSawyer");
            System.out.println(in.readLine()); // "Enter your message:"
            assertEquals("Enter your message:", in.readLine());
            //out.println("bye");
            //assertEquals("Message sent successfully to acharyya.", in.readLine());
        }
    }

    @Test
    public void testMessageFailure() throws Exception {
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
            out.println("4"); // Choose message option
            out.println("acharyya");
            assertEquals("Enter the username of the recipient:", in.readLine());
        }
    }

}

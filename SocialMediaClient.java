import java.io.*;
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

    public SocialMediaClient() {

    }

    public static void main(String[] args) {
        /*int portNumber = 4545;
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
                    case "1": // Block a user
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
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
        }*/
    }

    public String client (String choice, String action) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        SocialMediaGUI gui = new SocialMediaGUI("");
        String username = gui.getUsername();
        System.out.println("Client User: " + username);
        String password = gui.getPassword();

        try (Socket socket = new Socket(host, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            /*System.out.println(in.readLine()); // Server: Enter username
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
            }*/
            //boolean done = false;
            //while (!done) {
                // Display menu
                /*System.out.println("""
                    Enter Choice:
                    1. Block User
                    2. Add Friend
                    3. Remove Friend
                    4. Send Message
                    5. Retrieve Messages
                    6. Exit
                    """);
                String choice = sc.nextLine();
                out.println(choice);*/

                out.println(username);
                out.println(password);

                switch (choice) {
                    case "1": // Block a user
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
                        String confirmBlock = in.readLine();
                        if (confirmBlock.equals("Success"))
                            return blockUsername;
                        else
                            return "";
                        //break;
                    case "2": // Add a friend
                        System.out.println("Enter username to add:");
                        String addUsername = sc.nextLine();
                        out.println(addUsername);
                        String confirmAdd = in.readLine();
                        if (confirmAdd.equals("Success"))
                            return addUsername;
                        else
                            return "";
                        //break;
                    case "3": // Remove a friend
                        System.out.println("Enter username to remove:");
                        String removeUsername = sc.nextLine();
                        out.println(removeUsername);
                        String confirmRemove = in.readLine();
                        if (confirmRemove.equals("Success"))
                            return removeUsername;
                        else
                            return "";
                        //break;
                    case "4": // Send a message
                        System.out.println("Enter the username of the recipient:");
                        String recipientUsername = sc.nextLine();
                        out.println(recipientUsername);
                        String recipientConfirm = in.readLine();
                        if (!recipientConfirm.equals("Enter your message:"))
                            return "";
                        System.out.println("Enter your message:");
                        String messageContent = sc.nextLine();
                        out.println(messageContent);
                        String messageConfirm = in.readLine();
                        if (messageConfirm.equals("Success"))
                            return messageConfirm;
                        else
                            return "";
                        //break;
                    case "5": // Retrieve messages
                        out.println(username);
                        System.out.println("Enter the username to retrieve messages with:");
                        String otherUser = sc.nextLine();
                        out.println(otherUser);
                        String messages = in.readLine();
                        //System.out.println("Message history:\n" + messages);
                        if (!messages.equals("Error retrieving messages"))
                            return messages;
                        else
                            return "";
                        //continue; // Skip the server response reading at the end of the loop
                    case "6": //Search
                        //System.out.println("");
                        //System.out.println("Enter username:");
                        //String searched = sc.nextLine();
                        out.println(choice);
                        out.println(action);
                        //System.out.println(action);
                        String exists = in.readLine();
                        if (exists.equals(action))
                            return action;
                        else
                            return "";
                    case "7": // Exit the client
                        //done = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                // Display server's response
                String serverResponse = in.readLine();
                System.out.println("Server: " + serverResponse);
                return serverResponse;
            //}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
        return "";
    }
}

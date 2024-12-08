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

    private static String username;
    private static String password;

    public SocialMediaClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static void main(String[] args) {
    
    }

    public static String client (String choice, String action) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        //String username = "";
        //String password = "";

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

                System.out.println("USER: " + username);
                out.println(username);
                out.println(password);

                User user = null;
                try {
                    user = new User(username, password);
                } catch (UserException e) {
                    e.printStackTrace();
                }
                
                String confirmation = in.readLine();
                //System.out.println("Confirmation: " + confirmation);
                switch (choice) {
                    case "1": // Block a user
                        //System.out.println("Enter username to block:");
                        //String blockUsername = sc.nextLine();
                        //out.println(blockUsername);
                        out.println(choice);
                        out.println(action);
                        String confirmBlock = in.readLine();
                        if (confirmBlock.equals("Success"))
                            return action;
                        else
                            return "";
                        //break;
                    case "2": // Add a friend
                        //System.out.println("Enter username to add:");
                        //String addUsername = sc.nextLine();
                        //out.println(addUsername);
                        out.println(choice);
                        out.println(action);
                        String confirmAdd = in.readLine();
                        if (confirmAdd.equals("Success")) {
                            System.out.println("I AM HERE: " + user.getUserFriends());
                            return action;
                        }
                        else
                            return "";
                        //break;
                    case "3": // Remove a friend
                        //System.out.println("Enter username to remove:");
                        //String removeUsername = sc.nextLine();
                        //out.println(removeUsername);
                        out.println(choice);
                        out.println(action);
                        String confirmRemove = in.readLine();
                        if (confirmRemove.equals("Success"))
                            return action;
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
                        System.out.println("Heyo: " + exists);
                        /*if (exists.equals(action))
                            return action;
                        else
                            return "";*/
                        return exists;
                    case "7": // Exit the client
                        //done = true;
                        System.out.println("Exiting...");
                        break;
                    
                    case "8": //Unblock
                        out.println(choice);
                        out.println(action);
                        String confirmUnblock = in.readLine();
                        if (confirmUnblock.equals("Success"))
                            return action;
                        else
                            return "";
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

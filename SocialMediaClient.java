import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocialMediaClient {


    public static void main(String[] args) throws UserException {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Username:");
        String username = sc.nextLine();
        System.out.println("Enter Password:");
        String password = sc.nextLine();
        try (Socket socket = new Socket(host, portNumber);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            User user = new User(username, password);
            System.out.println("Successfully Logged In!");
            boolean done = false;
            while (!done) {
                System.out.println("Enter Username to search for user: ");
                String searchUsername = sc.nextLine();
                System.out.println("Enter Choice:\n1. Block User\n2. Add User\n3. Remove friend\n4. Exit");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        user.blockUser(searchUsername);
                        break;
                    case 2:
                        user.addUser(searchUsername);
                        break;
                    case 3:
                        user.removeFriend(searchUsername);
                        break;
                    case 4:
                        done = true;
                        break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserException e) {
            throw new UserException("User Not found");
        }


    }
}

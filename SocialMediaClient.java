import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocialMediaClient {

    public static void main(String[] args) {
        int portNumber = 4545;
        String host = "localhost";
        Scanner sc = new Scanner(System.in);

        try (Socket socket = new Socket(host, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println(in.readLine());
            String username = sc.nextLine();
            out.println(username);

            System.out.println(in.readLine());
            String password = sc.nextLine();
            out.println(password);

            // Handle login response
            String loginResponse = in.readLine();
            System.out.println(loginResponse);
            if (!loginResponse.startsWith("Successfully")) {
                return;
            }

            boolean done = false;
            while (!done) {
                System.out.println("Enter Choice:\n1. Block User\n2. Add User\n3. Remove Friend\n4. Exit");
                String choice = sc.nextLine();
                out.println(choice);

                switch (choice) {
                    case "1":
                        System.out.println("Enter username to block:");
                        String blockUsername = sc.nextLine();
                        out.println(blockUsername);
                        break;
                    case "2":
                        System.out.println("Enter username to add:");
                        String addUsername = sc.nextLine();
                        out.println(addUsername);
                        break;
                    case "3":
                        System.out.println("Enter username to remove:");
                        String removeUsername = sc.nextLine();
                        out.println(removeUsername);
                        break;
                    case "4":
                        done = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                String serverResponse = in.readLine();
                System.out.println("Server: " + serverResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

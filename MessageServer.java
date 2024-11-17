import java.io.*;
import java.net.*;
import java.util.*;

/*
@author Bidit Acharyya
@Version 11/15/2024
Server for the messages between two users.
 */

public class MessageServer {
    public static void main(String[] args) {
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

        }
    }
}

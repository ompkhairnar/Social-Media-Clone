import java.io.*;
import java.util.ArrayList;
import java.util.List;
/*
@author Om Khairnar
@Version 11/03/2024
Om Wrote all code for these the message class.
 */
public class Message implements MessageInterface {
    private User messager;
    private File messages;

    //Constructor taking in the messager user
    public Message(User messager) throws UserException {
        if (!messager.isValidUser(messager)) {
            throw new UserException("Invalid user");
        }
        this.messager = messager;
    }
    //error constructor
    public Message(UserException e) {
        messager = new User(e);
    }

    //Actual method where the messager messages a user, it creates a new file
    //if the file does not already exist, but if it does it appends the file
    public void messageUser(User user, String message) {
        try {
            if (messager.isValidUser(user)) {
                String fileName = messager.getUsername() + user.getUsername() + "messages";
                File file = new File(fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(message);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getMessager() {
        return messager;
    }

    //returns the file of messages
    public String getMessages(User user) {
        StringBuilder messages = new StringBuilder();
        try {
            File file = new File(messager.getUsername() + user.getUsername() + "messages");
            if (!file.exists()) {
                return "No messages currently exist between users";
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    messages.append(line).append("\n");
                }
                reader.close();
                return messages.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages.toString();
    }

    //method to update the file real time
    public void updateFile(User user, String newMessage) throws UserException {
        List<String> fileStorage = new ArrayList<>();

        String fileName = messager.getUsername() + user.getUsername() + "messages";
        File file = new File(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileStorage.add(line);
            }
        } catch (IOException e) {
            throw new UserException("Error reading message file");
        }

        fileStorage.add(newMessage);

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String line : fileStorage) {
                pw.println(line);
            }
        } catch (IOException e) {
            throw new UserException("Could not update message file");
        }
    }
}

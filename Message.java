import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
@author Om Khairnar
@Version 11/03/2024
Om Wrote all code for the Message class.
*/

public class Message implements MessageInterface {
    private final User messager; // The user sending the messages
    private final Object fileLock = new Object(); // Lock object for thread safety
    private final String content = "";

    // Constructor taking in the messager user
    public Message(User messager) throws UserException {
        if (!messager.isValidUser(messager)) {
            throw new UserException("Invalid user");
        }
        this.messager = messager;
    }

    public Message(User messager, User receiver, String content) throws UserException {
        if (!messager.isValidUser(messager)) {
            throw new UserException("Invalid user");
        }
        this.messager = messager;
        this.content = content;
    }

    // Error constructor
    public Message(UserException e) {
        this.messager = new User(e);
    }

    // Actual method where the messager messages a user. It creates a new file
    // if the file does not already exist, but if it does, it appends the file.
    public void messageUser(User user, String message) {
        synchronized (fileLock) { // Synchronize critical section to ensure thread safety
            try {
                if (messager.isValidUser(user)) {
                    String fileName = messager.getUsername() + user.getUsername() + "messages";
                    File file = new File(fileName);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                        writer.write(message);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Getter method for the messager user
    public User getMessager() {
        return messager;
    }

    public String getContent() {
        return content;
    }

    // Returns the file of messages between users
    public String getMessages(User user) {
        synchronized (fileLock) { // Synchronize critical section to ensure thread safety
            StringBuilder messages = new StringBuilder();
            try {
                File file = new File(messager.getUsername() + user.getUsername() + "messages");
                if (!file.exists()) {
                    return "No messages currently exist between users";
                } else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            messages.append(line).append("\n");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return messages.toString();
        }
    }

    // Method to update the file in real-time
    public void updateFile(User user, String newMessage) throws UserException {
        synchronized (fileLock) { // Synchronize critical section to ensure thread safety
            List<String> fileStorage = new ArrayList<>();

            String fileName = messager.getUsername() + user.getUsername() + "messages";
            File file = new File(fileName);

            // Read existing messages into a list
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    fileStorage.add(line);
                }
            } catch (IOException e) {
                throw new UserException("Error reading message file");
            }

            // Add the new message to the list
            fileStorage.add(newMessage);

            // Write the updated list back to the file
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (String line : fileStorage) {
                    pw.println(line);
                }
            } catch (IOException e) {
                throw new UserException("Could not update message file");
            }
        }
    }
}

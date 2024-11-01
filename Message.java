import java.io.*;

public class Message implements MessageInterface {
    private User messager;
    private File messages;

    public Message(User messager) throws UserException {
        if (!isValidUser(messager)) {
            throw new UserException("Invalid user");
        }
        this.messager = messager;
    }

    public void messageUser(User user, String message) {
        try {
            if (isValidUser(user)) {
                String fileName = messager.getUsername() + user.getUsername() + "messages";
                File file = new File(fileName);
                if (file.exists()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(message);
                    writer.newLine();
                } else {
                    file.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(message);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getMessager() {
        return messager;
    }

    public String getMessages(User user) {
        String messages = "";
        try {
            File file = new File(messager.getUsername() + user.getUsername() + "messages");
            if (!file.exists()) {
                return "No messages currenly exist between users";
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    messages += line + "\n";
                }
                return messages;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

}

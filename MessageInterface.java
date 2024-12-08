public interface MessageInterface {
    public void messageUser(String username, String message);

    public User getMessager();

    public String getMessages(String username) throws UserException;

    public String getContent();

    public void updateFile(String user, String newMessage) throws UserException;
}

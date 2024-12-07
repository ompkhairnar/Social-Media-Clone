public interface MessageInterface {
    public void messageUser(String username, String message);

    public User getMessager();

    public String getMessages(String username);

    public String getContent();

    public void updateFile(User user, String newMessage) throws UserException;
}

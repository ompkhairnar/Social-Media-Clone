public interface MessageInterface {
    public void messageUser(User user, String message);

    public User getMessager();

    public String getMessages(User user);

    public void updateFile(User user, String newMessage) throws UserException;

}

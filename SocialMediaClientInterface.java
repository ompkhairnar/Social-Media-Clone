public interface SocialMediaClientInterface {
    /**
     * Logs in a user with the provided username and password.
     * 
     * @param username the username of the user
     * @param password the password of the user
     * @return true if login is successful, false otherwise
     */
    boolean userLogin(String username, String password);

    /**
     * Sends a message to another user.
     * 
     * @param sender   the username of the sender
     * @param receiver the username of the receiver
     * @param content  the content of the message
     */
    void sendMessage(String sender, String receiver, String content);

    /**
     * Handles incoming messages or server notifications.
     */
    void handleMessage();

    /**
     * Closes the client, including all streams and the socket connection.
     */
    void close();

    void retrieveMessages(String sender, String receiver);
}

/**
 * Interface for social media client
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @author Sawyer, Bidit, Richard, Om
 * @version 1.0 November 17th, 2024
 */
public interface SocialMediaClientInterface {

    boolean userLogin(String username, String password);

    void sendMessage(String sender, String receiver, String content);

    void handleMessage();

    void close();

    void retrieveMessages(String sender, String receiver);
}

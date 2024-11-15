import java.util.List;

/**
* User Interface Class.
*
* <p>Purdue University -- CS18000 -- Fall 2024</p>
*
* @author Purdue CS
* @version Nov 3, 2024
*/

public interface UserInterface {

    void addUser(String user) throws UserException;

    void blockUser(String user) throws UserException;

    void removeFriend(User user) throws UserException;

    void createUser(String username, String password, List<String> friendList, List<String> blockedList)
            throws UserException;

    String getUsername();

    String getUserPassword();

    String getUserFriends();

    String getUserBlocked();

    boolean isValidUser(User username);

}

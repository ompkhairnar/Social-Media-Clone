import java.util.List;

public interface UserInterface {

    void addUser(User user) throws UserException;

    void blockUser(User user) throws UserException;

    void removeFriend(User user) throws UserException;

    void createUser(String username, String password, List<String> friendList, List<String> blockedList)
            throws UserException;

    String getUsername();

    String getUserPassword();

    String getUserFriends();

    String getUserBlocked();

    boolean isValidUser(User username);

}

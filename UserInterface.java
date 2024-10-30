public interface UserInterface {

    void addUser(User user) throws UserException;

    void blockUser(User user) throws UserException;

    void removeFriend(User user) throws UserException;

    String getUsername();

    String getUserPassword();

    String getUserFriends();

    String getUserBlocked();

    boolean isValidUser();

}

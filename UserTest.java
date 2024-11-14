import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

/**
* User Test Class.
*
* <p>Purdue University -- CS18000 -- Fall 2024</p>
*
* @author Purdue CS
* @version Nov 3, 2024
*/

public class UserTest {

    private final String file = "userStorage.csv";

    @Before
    public void setUp() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("sawyer,password,testFriend1;testFriend2,testBlocked1");
            pw.println("notSawyer,12345,,testBlocked1:testBlocked2");
        }
    }

    @Test
    public void testUserConstructor_validLogin() throws Exception {
        User user = new User("sawyer", "password");
        assertEquals("sawyer", user.getUsername());
        assertEquals("password", user.getUserPassword());
        assertEquals("testFriend1;testFriend2", user.getUserFriends());
        assertEquals("testBlocked1", user.getUserBlocked());
    }

    @Test(expected = UserException.class)
    public void testUserConstructor_invalidLogin() throws Exception {
        new User("test123", "pass123");
    }

    @Test
    public void testAddUser_success() throws Exception {
        User user = new User("sawyer", "password");
        User friend = new User("notSawyer", "12345");

        user.addUser(friend);
        assertTrue(user.getUserFriends().contains("notSawyer"));
    }

    @Test(expected = UserException.class)
    public void testAddUser_AlreadyFriend() throws UserException {
        User user = new User("sawyer", "password");
        User friend = new User("notSawyer", "12345");

        user.addUser(friend); // This should throw an exception
        user.addUser(friend);
    }

    @Test(expected = UserException.class)
    public void testAddUser_AlreadyBlocked() throws UserException {
        User user = new User("sawyer", "password");
        User blockedUser = new User("testBlocked1", "pass", true);

        user.addUser(blockedUser); // This should throw an exception
    }

    @Test
    public void testBlockUser_SuccessfulBlock() throws Exception {
        User user = new User("sawyer", "password");
        User newBlockedUser = new User("sawyerBlocked", "pass", true);

        user.blockUser(newBlockedUser);
        assertTrue(user.getUserBlocked().contains("sawyerBlocked"));
    }

    @Test
    public void testRemoveFriend_SuccessfulRemoval() throws Exception {
        User user = new User("sawyer", "password");
        User friend = new User("sawyerRemove", "pass", true);

        user.addUser(friend);
        user.removeFriend(friend);
        assertFalse(user.getUserFriends().contains("sawyerRemove"));
    }

    @Test(expected = UserException.class)
    public void testRemoveFriend_NotAFriend() throws Exception {
        User user = new User("sawyer", "password");
        User nonFriend = new User("random", "pass");

        user.removeFriend(nonFriend); // This should throw an exception
    }

    @Test
    public void testIsUserNameTaken_ExistingUser() throws Exception {
        User user = new User("sawyer", "password");
        assertTrue(user.isUserNameTaken("sawyer"));
    }

    @Test
    public void testIsUserNameTaken_NonexistentUser() throws Exception {
        User user = new User("sawyer", "password");
        assertFalse(user.isUserNameTaken("newName"));
    }

    @Test
    public void testIsValidUser_ValidUser() throws Exception {
        User user = new User("sawyer", "password");
        assertTrue(user.isValidUser(user));
    }

    @Test(expected = UserException.class)
    public void testIsValidUser_InvalidUser() throws Exception {
        User invalidUser = new User("invalidUser", "pass");
        assertFalse(invalidUser.isValidUser(invalidUser));
    }

    @Test
    public void testUpdateCSV_FileUpdatesCorrectly() throws Exception {
        User user = new User("sawyer", "password");
        User update = new User("sawyerUpdate", "pass", true);

        user.addUser(update);
        assertTrue(user.getUserFriends().contains("sawyerUpdate"));

    }

    @Test(expected = UserException.class)
    public void testUpdateCSV_UserNotFound() throws Exception {
        User user = new User("nonexistent", "password");
        user.updateCSV(); // This should throw an exception since the user does not exist
    }
}

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

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
    public void testAddUser_AlreadyBlocked() throws Exception {
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
}

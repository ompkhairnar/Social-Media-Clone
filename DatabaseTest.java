import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class DatabaseTest {

    private final String testFile = "userDataTest.csv";
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    public static class TestCase {
        
        @Test
        public void testDatabaseConstructorExists() {
            try {
                Class<?> c = FoundationDatabase.class;
                Constructor<?> constructor = c.getConstructor(ArrayList.class, String.class);
            } catch (NoSuchMethodException e) {
                Assert.fail("Database constructor with arraylist of users and String filename argument does not exist or is not public!");
            }
        }
    }

    @Test(timeout = 1000)
    public void runTestDatabaseOut() {

        String expectedDatabaseOutput = "bidit,password123,,\nacharyya,123pass,,";

        String actualDatabaseOutput = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            String in = "";
            while ((in = reader.readLine()) != null) {
                    actualDatabaseOutput += in + "\n";
            }
        } catch (IOException a) {
            Assert.assertTrue("An IO exception was encountered while reading dataOut.txt", false);
        } catch (Exception e) {
            Assert.assertTrue("An unknown exception was encountered while reading dataOut.txt", false);
        }
        Assert.assertEquals("Make sure your FoundationDatabase is writing the outputfile correctly",
                expectedDatabaseOutput.trim(), actualDatabaseOutput.trim());
    }

    @Test
    public void readUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.readUsers(testFile);
        assertTrue("Expected readUsers to return true", result);

        assertEquals("Expected 2 users to be read", 2, fd.getUsers().size());
        assertEquals("Expected username of user 0", "bidit", fd.getUsers().get(0).getUsername());
        assertEquals("Expected password of user 0", "password123", fd.getUsers().get(0).getUserPassword());
        assertEquals("Expected username of user 1", "acharyya", fd.getUsers().get(1).getUsername());
        assertEquals("Expected password of user 1", "123pass", fd.getUsers().get(1).getUserPassword());
    }

    @Test
    public void createUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.createUser("ben", "panera123");
        assertTrue("Expected createUser to return true for valid user data", result);
        assertEquals("Expected 3 users to be read", 3, fd.getUsers().size());
        assertEquals("Expected new user username", "ben", fd.getUsers().get(2).getUsername());
        assertEquals("Expected new user passwowrd", "panera123", fd.getUsers().get(2).getUserPassword());
    }

    @Test
    public void createBadUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.createUser(" ", " ");
        assertTrue("Expected createUser to return false for invalid user data", result);
        assertEquals("Expected 2 users to be read", 2, fd.getUsers().size());
    }

    @Test
    public void viewUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        String result = fd.viewUser("bidit");
        assertEquals("Expected username that was passed into method", "bidit", result);
    }

    @Test
    public void badViewUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        String result = fd.viewUser("jonathan");
        assertEquals("Expected empty string", "", result);
    }

    @Test
    public void searchTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.search("bidit");
        assertTrue("Expected search to return true for username that exists", result);
    }

    @Test
    public void badSearchTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.search("");
        assertTrue("Expected search to return false for nonexistent username", result);
    }

    @Test
    public void deleteUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.deleteUser("bidit", "password123");
        assertTrue("Expected deleteUser to return true for existing user", result);
        assertEquals("Expected 1 users to be in users array", 1, fd.getUsers().size());
    }

    @Test
    public void badDeleteUserTest() {
        ArrayList<User> users = new ArrayList<User>();
        FoundationDatabase fd = new FoundationDatabase(users, testFile);
        boolean result = fd.deleteUser("", "");
        assertTrue("Expected deleteUser to return false for nonexistent user", result);
        assertEquals("Expected 2 users to be in users array", 2, fd.getUsers().size());
    }
}

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class MessageTest {
    private final ArrayList<User> users = new ArrayList<>();
    private final String userFileName = "userData.csv";

    @Test(timeout = 1000)
    public void ensureMessageThrowsUserException() {
        try {
            new Message(new User(" ", " "));
        } catch (Exception e) {
            Assert.assertEquals("Ensure that `Message` has a constructor that throws UserException!", UserException.class, e.getClass());
        }
    }

    @Test
    public void testBadMessageConstructorExists() {
        try {
            Class<?> c = Message.class;
            Constructor<?> constructor = c.getConstructor(UserException.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Message constructor with UserException argument does not exist or is not public!");
        }
    }

    @Test
    public void testGoodUserConstructorExists() {
        try {
            Class<?> c = Message.class;
            Constructor<?> constructor = c.getConstructor(User.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Message constructor with User messager argument does not exist or is not public!");
        }
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MessageTest.class);
        if (result.wasSuccessful()) {
            System.out.println("All MessageTest tests passed.");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    @Test
    public void testMessageConstructorValidUser() throws Exception {
        User validUser = new User("validUser", "password123");
        Message message = new Message(validUser);
        Assert.assertEquals("Expected messager to be validUser", "validUser", message.getMessager().getUsername());
    }

    @Test
    public void testMessageConstructorInvalidUser() throws Exception {
        expectedException.expect(UserException.class);
        expectedException.expectMessage("Invalid user");
        User invalidUser = new User("invalidUser", "wrongPassword");
        new Message(invalidUser);
    }

    @Test
    public void testMessageUserCreatesFile() throws Exception {
        User sender = new User("senderUser", "password123");
        User receiver = new User("receiverUser", "password456");
        Message message = new Message(sender);

        String testMessage = "Hello, this is a test message!";
        message.messageUser(receiver, testMessage);

        File messageFile = new File(sender.getUsername() + receiver.getUsername() + "messages");
        Assert.assertTrue("Expected message file to be created", messageFile.exists());
    }

    @Test
    public void testMessageUserWritesContent() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        FoundationDatabase database = new FoundationDatabase(users, userFileName);

        database.createUser("senderUser", "password456");
        database.createUser("receiverUser", "password789");

        User sender = database.getUsers().get(0); // senderUser
        User receiver = database.getUsers().get(1); // receiverUser

        Message message = new Message(sender);
        String messageContent = "Hello, this is a test message!";
        message.messageUser(receiver, messageContent);

        String expectedFileName = "senderUserreceiverUsermessages";
        File messageFile = new File(expectedFileName);
        Assert.assertTrue("Message file should exist after sending a message", messageFile.exists());

        StringBuilder actualMessageContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(messageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                actualMessageContent.append(line).append("\n");
            }
        } catch (IOException e) {
            Assert.fail("An IOException occurred while reading the message file");
        }

        Assert.assertEquals("The message content was not written correctly to the file",
                messageContent + "\n", actualMessageContent.toString());
    }


    @Test
    public void testGetMessagesWithExistingFile() throws Exception {
        User sender = new User("senderUser", "password123");
        User receiver = new User("receiverUser", "password456");
        Message message = new Message(sender);

        String testMessage = "Hello, test message for reading!";
        message.messageUser(receiver, testMessage);

        String readMessages = message.getMessages(receiver);
        Assert.assertTrue("Expected messages to contain the test message", readMessages.contains(testMessage));
    }

    @Test
    public void testGetMessagesWithNoFile() throws Exception {
        User sender = new User("senderUser", "password123");
        User receiver = new User("receiverUser", "password456");
        Message message = new Message(sender);

        String result = message.getMessages(receiver);
        Assert.assertEquals("Expected message when no file exists", "No messages currently exist between users", result);
    }
}




import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class MessageTest {

    @Test(timeout = 1000)
        public void ensureMessageThrowsUserException() {
            try {
                new Message(new User(" ", " "));
            } catch (Exception e) {
                Assert.assertEquals("Ensure that `Message` has a constructor that throws UserException!",UserException.class, e.getClass());
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
}

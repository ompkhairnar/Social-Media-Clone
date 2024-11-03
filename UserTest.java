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

public class UserTest {
    @Test(timeout = 1000)
        public void ensureUserThrowsUserException() {
            try {
                new User("Bad Username", "Bad Password");
            } catch (Exception e) {
                Assert.assertEquals("Ensure that `User` has a constructor that throws UserException!",UserException.class, e.getClass());
            }
            /*
            // Set the input        
            // Separate each input with a newline (\n). 
            String input = "Line One\nLine Two\n"; 

            // Pair the input with the expected result
            String expected = "Insert the expected output here" 

            // Runs the program with the input values
            // Replace TestProgram with the name of the class with the main method
            receiveInput(input);
            TestProgram.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct. 
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Error message if output is incorrect, customize as needed",
            expected.trim(), stuOut.trim());
            */
        }

        @Test
        public void testBadUserConstructorExists() {
            try {
                Class<?> c = User.class;
                Constructor<?> constructor = c.getConstructor(UserException.class);
            } catch (NoSuchMethodException e) {
                Assert.fail("User constructor with UserException argument does not exist or is not public!");
            }
        }

        @Test
        public void testGoodUserConstructorExists() {
            try {
                Class<?> c = User.class;
                Constructor<?> constructor = c.getConstructor(String.class, String.class);
            } catch (NoSuchMethodException e) {
                Assert.fail("User constructor with String username and password argument does not exist or is not public!");
            }
        }
}

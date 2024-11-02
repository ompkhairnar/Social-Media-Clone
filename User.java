import java.io.*;
import java.util.*;

public class User implements UserInterface {
    private String username;
    private String password;
    private List<String> friendList;
    private List<String> blockedList;
    private static final String userStorage = "userStorage.csv";

    public User(String username, String password) throws UserException {
        this.username = username;
        this.password = password;
        loginUser();
    }

    private void loginUser() throws UserException {
        try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    this.friendList = new ArrayList<>(Arrays.asList(data[2].split(";")));
                    this.blockedList = new ArrayList<>(Arrays.asList(data[3].split(";")));
                    return;
                }
            }
            throw new UserException("Invalid username or password");
        } catch (IOException e) {
            throw new UserException("Error reading file");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return password;
    }

    public String getUserFriends() {
        return String.join(";", friendList);
    }

    public String getUserBlocked() {
        return String.join(";", blockedList);
    }

    // checks if the username is taken or not
    // used later for create user and valid user
    public boolean isUserNameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // checking if the user passed in has a username in our csv
    public boolean isValidUser(User user) {
        return isUserNameTaken(user.getUsername());
    }

    // creates a new user and writes it to csv file, error if username is taken
    public void createUser(String username, String password) throws UserException {
        if (isUserNameTaken(username)) {
            throw new UserException("Username is not available");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(userStorage))) {
            pw.println(username + "," + password + ",,");
        } catch (IOException e) {
            throw new UserException("Unable to create account");
        }

    }

    // makes sure user is not already or friend or blocked then adds user to friend
    // list
    public void addUser(User user) throws UserException {
        if (friendList.contains(user.getUsername())) {
            throw new UserException("User is already your friend");
        }
        if (blockedList.contains(user.getUsername())) {
            throw new UserException("You have this user blocked");
        }
        friendList.add(user.getUsername());
        updateCSV();
    }

    // makes sure user is not blocked then adds them to blocked list
    public void blockUser(User user) throws UserException {
        if (blockedList.contains(user.getUsername())) {
            throw new UserException("User is already blocked");
        }
        blockedList.add(user.getUsername());
        updateCSV();
    }

    // makes sure user is a friend then removes them
    public void removeFriend(User user) throws UserException {
        if (!friendList.contains(user.getUsername())) {
            throw new UserException("User is not your friend");
        }
        friendList.remove(user.getUsername());
        updateCSV();
    }

    // updates our csv file by passing in any new information
    // if program doesnt find the user and their info exception is thrown
    private void updateCSV() throws UserException {
        List<String> fileStorage = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    fileStorage.add(username + "," + password + "," + getUserFriends() + getUserBlocked());
                    userFound = true;
                } else {
                    fileStorage.add(line);
                }
            }
        } catch (IOException e) {
            throw new UserException("Error reading file");
        }

        if (!userFound) {
            throw new UserException("User not found");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(userStorage))) {
            for (String line : fileStorage) {
                pw.println(line);
            }
        } catch (IOException e) {
            throw new UserException("Could not update user data");
        }
    }
}

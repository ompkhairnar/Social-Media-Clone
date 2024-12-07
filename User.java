import java.io.*;
import java.util.*;

/**
 * User Class.
 *
 * <p>
 * Purdue University -- CS18000 -- Fall 2024
 * </p>
 *
 * @author Purdue CS
 * @version Nov 3rd, 2024
 */

public class User implements UserInterface {
    private String username;
    private String password;
    private List<String> friendList;
    private List<String> blockedList;
    private static final String userStorage = "userStorage.csv";

    public User(String username, String password, boolean isNewUser) throws UserException {
        if (username.contains(" ") || password.contains(" ")) {
            throw new UserException("Bad User Data");
        }
        this.username = username;
        this.password = password;
        this.friendList = new ArrayList<>();
        this.blockedList = new ArrayList<>();
        createUser(username, password, friendList, blockedList);
    }

    public User(String username, String password) throws UserException {
        if (username == null || username.isBlank()) {
            throw new UserException("Username cannot be null or empty.");
        }
        if (password == null || password.isBlank()) {
            throw new UserException("Password cannot be null or empty.");
        }
        
        this.username = username;
        this.password = password;
    
        // Initialize empty lists for friends and blocked users
        this.friendList = new ArrayList<>();
        this.blockedList = new ArrayList<>();

        // loginUser(); REMOVE IF ERROR
    }
    

    public User(String username) throws UserException {
        if (username.contains(" ")) {
            throw new UserException("Bad User Data");
        }
        this.username = username;
        this.password = null; // Not needed for single-param usage
        this.friendList = new ArrayList<>(); // Empty for this case
        this.blockedList = new ArrayList<>(); // Empty for this case
    }

    public User(UserException e) {
        username = e.getMessage();
        password = e.getMessage();
    }

    private void loginUser() throws UserException {
        try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    synchronized (friendList) {
                        if (!data[2].equals(" "))
                            this.friendList = new ArrayList<>(Arrays.asList(data[2].split(";")));
                    }
                    synchronized (blockedList) {
                        if (!data[3].equals(" "))
                            this.blockedList = new ArrayList<>(Arrays.asList(data[3].split(";")));
                    }
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
        synchronized (friendList) {
            return String.join(";", friendList);
        }
    }

    public String getUserBlocked() {
        synchronized (blockedList) {
            return String.join(";", blockedList);
        }
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
    public void createUser(String username, String password, List<String> friendList, List<String> blockedList)
            throws UserException {
        if (isUserNameTaken(username)) {
            throw new UserException("Username is not available");
        }
        synchronized (User.class) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(userStorage, true))) {
                pw.println(username + "," + password + "," + String.join(";", friendList) + ","
                        + String.join(";", blockedList));
            } catch (IOException e) {
                throw new UserException("Unable to create account");
            }
        }
    }

    // makes sure user is not already a friend or blocked then adds user to friend
    // list
    public void addUser(String username) throws UserException {
        if (!isUserNameTaken(username)) {
            throw new UserException("Username does not exist");
        }
        synchronized (friendList) {
            if (friendList.contains(username)) {
                throw new UserException("User is already your friend");
            }
        }
        synchronized (blockedList) {
            if (blockedList.contains(username)) {
                throw new UserException("You have this user blocked");
            }
        }
        synchronized (friendList) {
            friendList.add(username);
        }
        updateCSV();
    }

    // makes sure user is not blocked then adds them to blocked list
    public void blockUser(String username) throws UserException {
        if (!isUserNameTaken(username)) {
            throw new UserException("Username does not exist");
        }
        synchronized (blockedList) {
            if (blockedList.contains(username)) {
                throw new UserException("User is already blocked");
            }
            blockedList.add(username);
        }
        synchronized (friendList) {
            friendList.remove(username);
        }
        updateCSV();
    }

    // makes sure user is a friend then removes them
    public void removeFriend(String username) throws UserException {
        if (!isUserNameTaken(username)) {
            throw new UserException("Username does not exist");
        }
        synchronized (friendList) {
            if (!friendList.contains(username)) {
                throw new UserException("User is not your friend");
            }
            friendList.remove(username);
        }
        updateCSV();
    }

    // updates our csv file by passing in any new information
    // if program doesnt find the user and their info exception is thrown
    void updateCSV() throws UserException {
        List<String> fileStorage = new ArrayList<>();
        boolean userFound = false;
        synchronized (User.class) {
            try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(username)) {
                        fileStorage.add(username + "," + password + "," + getUserFriends() + "," + getUserBlocked());
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
}

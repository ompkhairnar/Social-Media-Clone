//import static org.junit.Assert.fail;

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

    public static void main(String[] args) {
        /*User curr = null;
        try {
            curr = new User("sawyer", "password");
            curr.unblock("bidit");
        } catch (UserException e) {
            e.printStackTrace();
        }*/
    }

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

         loginUser(); //REMOVE IF ERROR
    }
    

    public User(String username) throws UserException {
        if (username.contains(" ")) {
            throw new UserException("Bad User Data");
        }
        this.username = username;
        this.password = null; // Not needed for single-param usage
        this.friendList = new ArrayList<>(); // Empty for this case
        this.blockedList = new ArrayList<>(); // Empty for this case

        loginUser(username);
    }

    public User(UserException e) {
        username = e.getMessage();
        password = e.getMessage();
    }

    private void loginUser(String user) throws UserException {
        try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                //System.out.println(data[0]);
                if (data[0].equals(user)) {
                    System.out.println("I'm in");
                    this.password = data[1];
                    synchronized (friendList) {
                        if (!data[2].equals(" ")) {
                            System.out.println("FRIENDAS: " + data[2]);
                            this.friendList = new ArrayList<>(Arrays.asList(data[2].split(";")));
                        }
                        //System.out.println(friendList.size());
                        // for (String f : friendList) {
                        //     System.out.println("Login " + data[0] + " friend: " + f);
                        // }
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

    private void loginUser() throws UserException {
        try (BufferedReader br = new BufferedReader(new FileReader(userStorage))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    synchronized (friendList) {
                        if (!data[2].equals(" ")) {
                            System.out.println("FRIENDAS: " + data[2]);
                            this.friendList = new ArrayList<>(Arrays.asList(data[2].split(";")));
                        }
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
            //System.out.println("FRIENDS: " + String.join(";", friendList));
            String userFriends = String.join(";", friendList);
            if (userFriends.equals(""))
                userFriends = " ";
            return userFriends;
        }
    }

    public String getUserBlocked() {
        synchronized (blockedList) {
            String userBlocked = String.join(";", blockedList);
            if (userBlocked.equals(""))
                userBlocked = " ";
            return userBlocked;
        }
    }

    public void setUserFriends(ArrayList<String> f) {
        friendList = f;
        System.out.println(getUserPassword());
        for (String friend : friendList) {
            System.out.println(getUsername() + "FRIEND: " + friend);
        }
    }

    public void setUserBlocked(ArrayList<String> b) {
        blockedList = b;
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
                String blockedString;
                String friendString;
                if (friendList.size() == 0)
                    friendString = " ";
                else
                    friendString = String.join(";", friendList);
                if (blockedList.size() == 0)
                    blockedString = " ";
                else
                    blockedString = String.join(";", blockedList);
                pw.println(username + "," + password + "," + friendString + ","
                        + blockedString);
            } catch (IOException e) {
                throw new UserException("Unable to create account");
            }
        }
    }

    // makes sure user is not already a friend or blocked then adds user to friend
    // list
    public void addUser(String username) throws UserException {
        User added = null;
        if (!isUserNameTaken(username)) {
            throw new UserException("Username does not exist");
        } else {
            added = new User(username);
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
            updateCSV();
            /*String addedFriends = added.getUserFriends();
            System.out.println("ADDEDFRIEND:->" + addedFriends + "<-");
            ArrayList<String> addedFriendsList;
            if (!addedFriends.equals("")) {
                addedFriendsList = new ArrayList<>(Arrays.asList(addedFriends.split(";")));
            } else {
                System.out.println("hello");
                addedFriendsList = new ArrayList<String>();
            }
            System.out.println("s" + addedFriendsList.size());
            addedFriendsList.add(getUsername());
            added.setUserFriends(addedFriendsList);*/
            String addedFriends = added.getUserFriends();
            System.out.println("ADDEDFRIEND:->" + addedFriends + "<-");
            ArrayList<String> addedFriendsList;
            if (!addedFriends.equals(" ")) {
                addedFriendsList = new ArrayList<>(Arrays.asList(addedFriends.split(";")));
            } else {
                System.out.println("hello");
                addedFriendsList = new ArrayList<String>();
            }
            if (!addedFriendsList.contains(getUsername()))
                added.addUser(getUsername());
        }
        //updateCSV();
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
        User blocked = new User(username);
        updateCSV();
        String blockedFriends = blocked.getUserFriends();
        String blockedBlocked = blocked.getUserBlocked();
        System.out.println("BLOCKEDFRIEND:->" + blockedFriends + "<-");
        ArrayList<String> blockedFriendsList;
        ArrayList<String> blockedBlockedList;
        if (!blockedFriends.equals(" ")) {
            blockedFriendsList = new ArrayList<>(Arrays.asList(blockedFriends.split(";")));
        } else {
            System.out.println("hello");
            blockedFriendsList = new ArrayList<String>();
        }
        if (!blockedBlocked.equals(" ")) {
            blockedBlockedList = new ArrayList<>(Arrays.asList(blockedBlocked.split(";")));
        } else {
            System.out.println("hello");
            blockedBlockedList = new ArrayList<String>();
        }
        if (blockedFriendsList.contains(getUsername()))
            blocked.removeFriend(getUsername());
        if (!blockedBlockedList.contains(getUsername()))
            blocked.blockUser(getUsername());
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
            User removed = new User(username);
            friendList.remove(username);
            System.out.println(getUsername() + " friends: " + friendList);
            updateCSV();
            String removedFriends = removed.getUserFriends();
            System.out.println("REMOVEDFRIEND:->" + removedFriends + "<-");
            ArrayList<String> removedFriendsList;
            if (!removedFriends.equals(" ")) {
                removedFriendsList = new ArrayList<>(Arrays.asList(removedFriends.split(";")));
            } else {
                System.out.println("hello");
                removedFriendsList = new ArrayList<String>();
            }
            if (removedFriendsList.contains(getUsername()))
                removed.removeFriend(getUsername());
        }
        //updateCSV();
    }

    public void unblock (String username) throws UserException {
        if (!isUserNameTaken(username)) {
            throw new UserException("Username does not exist");
        }
        synchronized (blockedList) {
            if (!blockedList.contains(username)) {
                throw new UserException("User is not blocked");
            }
            blockedList.remove(username);
        }
        User blocked = new User(username);
        updateCSV();
        String blockedFriends = blocked.getUserFriends();
        String blockedBlocked = blocked.getUserBlocked();
        System.out.println("BLOCKEDFRIEND:->" + blockedFriends + "<-");
        ArrayList<String> blockedBlockedList;
        if (!blockedBlocked.equals(" ")) {
            blockedBlockedList = new ArrayList<>(Arrays.asList(blockedBlocked.split(";")));
        } else {
            System.out.println("hello");
            blockedBlockedList = new ArrayList<String>();
        }
        if (blockedBlockedList.contains(getUsername()))
            blocked.unblock(getUsername());
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

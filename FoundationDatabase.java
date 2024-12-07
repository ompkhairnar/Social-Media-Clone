import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FoundationDatabase implements FoundationDatabaseInterface {
    private ArrayList<User> users;
    private String userFileName;

    public FoundationDatabase(ArrayList<User> users, String filename) {
        this.users = users;
        userFileName = filename;

        if (!readUsers(userFileName)) {
            System.err.println("Error: Failed to load users from file.");
        } else {
            System.out.println("Users loaded successfully from file.");
        }
    }

    // Bidit
    public boolean readUsers(String file) {
        try {
            File f = new File(file);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    System.out.println("Loading user: " + line); // Debugging
                    String[] arr = line.split(",");
    
                    // Ensure the line has at least two fields for username and password
                    if (arr.length < 2) {
                        System.err.println("Skipping malformed line: " + line);
                        continue;
                    }
    
                    String username = arr[0];
                    String password = arr[1];
    
                    // Use default values if friends or blocked fields are missing
                    String friends = (arr.length > 2 && !arr[2].isBlank()) ? arr[2] : "";
                    String blocked = (arr.length > 3 && !arr[3].isBlank()) ? arr[3] : "";
    
                    ArrayList<String> friendList = new ArrayList<>();
                    ArrayList<String> blockedList = new ArrayList<>();
    
                    if (!friends.isEmpty()) {
                        friendList = new ArrayList<>(List.of(friends.split(";")));
                    }
                    if (!blocked.isEmpty()) {
                        blockedList = new ArrayList<>(List.of(blocked.split(";")));
                    }
                    
                    synchronized (users) {
                        System.out.println("USER: " + username + ", PASS: " + password);
                        User user = new User(username, password); 
                        /*for (String friend : friendList) {
                            user.addUser(friend); 
                        }
                        for (String block : blockedList) {
                            user.blockUser(block); 
                        }*/
                        users.add(user);
                    }
                } catch (UserException ue) {
                    System.err.println("Skipping invalid user: " + line + " - " + ue.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error while processing line: " + line);
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + file);
            e.printStackTrace();
            return false; 
        }
        System.out.println("Loaded users: " + users.size()); // Debugging
        return true; 
    }
    
    

    // Bidit
    @Override
    public void createUser(String username, String password) throws UserException {
        synchronized (users) {
            if (search(username)) {
                throw new UserException("Username is already taken."); // Throw exception for duplicate username
            }
    
            try {
                // Create a new user
                User newUser = new User(username, password);
                users.add(newUser);
    
                // Write the new user to the file
                try (PrintWriter pw = new PrintWriter(new FileWriter(userFileName, true))) {
                    pw.println(username + "," + password + ",,");
                }
            } catch (IOException e) {
                throw new UserException("Error saving user to file: " + e.getMessage()); // Wrap IOException in UserException
            }
        }
    }

    // Richard
    public String viewUser(String username) {
        if (search(username)) {
            return username;
        }
        return "";
    }

    // Richard
    public boolean search(String username) {
        synchronized (users) {  // Lock access to users only during iteration
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    // same as above function just different name
    public User getUser(String username) throws UserException {
        System.out.println(users);
        synchronized (users) { 
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    return user; 
                }
            }
        }
        throw new UserException("User not found: " + username); 
    }

    // Richard
    public boolean deleteUser(String username, String password) {
        if (search(username)) {
            User user = null;
            synchronized (users) {  // Lock access to users only during modification
                for (User u : users) {
                    if (u.getUsername().equals(username)) {
                        user = u;
                        break;
                    }
                }
                if (user != null) {
                    users.remove(user);
                    return true;
                }
            }
        }
        return false;
    }

    // Bidit
    public boolean outputDatabase() {
        try {
            File f = new File(userFileName);
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            synchronized (users) {  // Lock access to users only during file writing
                for (User user : users) {
                    String uname = user.getUsername();
                    String pass = user.getUserPassword();
                    String friends = user.getUserFriends();
                    String blocked = user.getUserBlocked();
                    bw.write(uname + "," + pass + "," + friends + "," + blocked);
                }
            }
            bw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    // Bidit
    public ArrayList<User> getUsers() {
        synchronized (users) {  // Lock access to users only during copy creation
            return new ArrayList<>(users);  // Return a copy to avoid exposing internal state
        }
    }
}
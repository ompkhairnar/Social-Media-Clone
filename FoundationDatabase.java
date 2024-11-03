import java.util.ArrayList;
import java.io.*;

public class FoundationDatabase {
  private ArrayList<User> users;
  private String userFileName;

  public FoundationDatabase(ArrayList<User> users, String filename) {
    this.users = users;
    userFileName = filename;
  }

  //Bidit
  public boolean readUsers(String file) {
    try {
      File f = new File(userFileName);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line = br.readLine();
      while (line != null) {
        String[] arr = line.split(",");
        try {
          users.add(new User(arr[0], arr[1]));
        } catch (UserException ue) {
          users.add(new User(ue));
        }
        line = br.readLine();
      }
      br.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  }
  // Bidit
  public boolean createUser(String username, String password) {
    try {
      users.add(new User(username, password));
    } catch (UserException ue) {
      return false;
    }
    return true;
  }
  //Richard
  public String viewUser(String username) {
    // use search method to make sure that user exists
  	if (search(username)) {
  		return username;
    }
    return "";
  }
  //Richard
  public boolean search(String username) {	
	for (User user : users) {
        	if (user.getUsername().equalsIgnoreCase(username)) {  // case-insensitive + using user method
            		return true;
        	}
    	}
  	return false;
  }
  // Richard
  public boolean deleteUser(String username, String password) {
    // use search method to verify the user exists first
  	if (search(username)) {
      User user = null;
      for (User u : users) {
        if (u.getUsername().equals(username))
          user = u;
      }
  		if (user != null) { // uses getPassword from user class
        users.remove(user);
        return true;
      }
    }
    return false;
  }
}

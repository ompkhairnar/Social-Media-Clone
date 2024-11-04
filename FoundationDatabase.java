import java.util.ArrayList;
import java.io.*;

public class FoundationDatabase {
  private final ArrayList<User> users;  // Use 'final' for immutability of the reference
  private final String userFileName;

  public FoundationDatabase(ArrayList<User> users, String filename) {
    this.users = users;
    userFileName = filename;
  }

  // Bidit
  public synchronized boolean readUsers(String file) {
    try {
      File f = new File(userFileName);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line = br.readLine();
      synchronized (users) {  // synchronize access to the users list
        while (line != null) {
          String[] arr = line.split(",");
          try {
            users.add(new User(arr[0], arr[1]));
          } catch (UserException ue) {
            users.add(new User(ue));
          }
          line = br.readLine();
        }
      }
      br.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  // Bidit
  public synchronized boolean createUser(String username, String password) {
    synchronized (users) {
      try {
        users.add(new User(username, password));
      } catch (UserException ue) {
        return false;
      }
    }
    return true;
  }

  // Richard
  public synchronized String viewUser(String username) {
    if (search(username)) {
      return username;
    }
    return "";
  }

  // Richard
  public synchronized boolean search(String username) {
    synchronized (users) {
      for (User user : users) {
        if (user.getUsername().equalsIgnoreCase(username)) {
          return true;
        }
      }
    }
    return false;
  }

  // Richard
  public synchronized boolean deleteUser(String username, String password) {
    if (search(username)) {
      User user = null;
      synchronized (users) {
        for (User u : users) {
          if (u.getUsername().equals(username))
            user = u;
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
  public synchronized boolean outputDatabase() {
    try {
      File f = new File(userFileName);
      FileWriter fw = new FileWriter(f);
      BufferedWriter bw = new BufferedWriter(fw);
      synchronized (users) {
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
  public synchronized ArrayList<User> getUsers() {
    synchronized (users) {
      return new ArrayList<>(users);  // return a copy to avoid exposing internal state
    }
  }
}

public class FoundationDatabase {
  private ArrayList<User> users;
  private String userFileName;

  public FoundationDatabase(ArrayList<User> users, String filename) {
    this.users = users;
    userFileName = filename;
  }

  //Bidit
  public boolean readUsers(String file) {
  }
  // Bidit
  public boolean createUser(String username, String password) {
  }
  //Richard
  public String viewUser(String username) {
    // use search method to make sure that user exists
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
      if(username.getPassword().equals(password)) {
        users.remove(username);
      }
    }
  }
}

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
  }
  // Richard
  public boolean deleteUser(String username, String password) {
    // use search method to verify the user exists first
  }
}

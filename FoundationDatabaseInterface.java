import java.util.ArrayList;

public interface FoundationDatabaseInterface {

    public boolean readUsers(String file);

    public void createUser(String username, String password) throws UserException;

    public String viewUser(String username);

    public boolean search(String username);

    public boolean deleteUser(String username, String password);

    public boolean outputDatabase();

    public ArrayList<User> getUsers();
    
}

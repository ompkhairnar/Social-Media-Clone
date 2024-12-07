import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystemAlreadyExistsException;

public class SocialMediaGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private String loggedInUsername;
    private String loggedInPass;


    public SocialMediaGUI() {
        setTitle("Social Media Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 4545); 
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            showErrorDialog("Could not connect to the server. Please try again later.");
        }
    }

    private void initializeComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JPanel usernamePanel = new JPanel(new BorderLayout());
        JLabel usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        panel.add(usernamePanel);
        panel.add(passwordPanel);
        panel.add(buttonPanel);

        add(panel);

        loginButton.addActionListener(new LoginAction());
        signUpButton.addActionListener(new SignUpAction());
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String successMessage) {
        JOptionPane.showMessageDialog(
                this,
                successMessage,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                user = new User(username, password);
                SocialMediaGUI.this.loggedInUsername = username;
                System.out.println(SocialMediaGUI.this.loggedInUsername);
                //System.out.println("Instance in LoginAction: " + SocialMediaGUI.this);
                SocialMediaGUI.this.loggedInPass = password;
                showSuccessDialog("Login successful!");
                openMainScreen(user, false);
            } catch (UserException ex) {
                showErrorDialog("Error: " + ex.getMessage());
            }
        }
    }

    private class SignUpAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                out.println("SIGNUP");
                out.println(username);
                out.println(password);
            
                String response = in.readLine();
                if ("Account created successfully!".equals(response)) {
                    showSuccessDialog("Account created successfully!");
                    openMainScreen(new User(username, password), true);
                } else {
                    showErrorDialog("Sign up failed: " + response);
                }
            } catch (IOException | UserException ex) {
                showErrorDialog("Connection error: Could not sign up.");
            }
        }
    }

    private void openMainScreen(User user, boolean isNewUser) {
        MainScreen mainScreen = new MainScreen(this, user, isNewUser);
        mainScreen.setVisible(true);
        this.setVisible(false);
    }

    public String getUsername() {
        System.out.println(this.loggedInUsername);
        //System.out.println("Instance in getUsername: " + this);
        return this.loggedInUsername;
    }

    public String getPassword() {
        return loggedInPass;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SocialMediaGUI gui = new SocialMediaGUI();
            gui.setVisible(true);
        });
    }
}

class MainScreen extends JFrame {
    private User user;
    private boolean isNewUser;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> friendList;
    private DefaultListModel<String> friendListModel;
    private String currentFriend = null;
    private SocialMediaClient client;
    private SocialMediaGUI loginGUI;


    public MainScreen(SocialMediaGUI loginGUI, User user, boolean isNewUser) {
        this.user = user;
        this.isNewUser = isNewUser;
        this.loginGUI = loginGUI;
        setTitle("Social Media Platform");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(loginGUI);
        client = new SocialMediaClient(loginGUI.getUsername(), loginGUI.getPassword());
    }

    public User getUser() {
        return user;
    }

    private void initializeComponents(SocialMediaGUI loginGUI) {
        setLayout(new BorderLayout());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loginGUI.setVisible(true);
            this.dispose();
        });
        JTextField searchBar = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            //System.out.println("Size");
            search(searchBar.getText());
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchBar, BorderLayout.WEST);
        searchPanel.add(searchButton, BorderLayout.LINE_END);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(searchPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        //client = new SocialMediaClient();

        //left sidebar
        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFixedCellWidth(150);
        friendList.addListSelectionListener(e -> selectFriend());

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setPreferredSize(new Dimension(150, 0));
        add(friendScrollPane, BorderLayout.WEST);

        //chat display
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        add(messageScrollPane, BorderLayout.CENTER);

        //message input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendButton.doClick()); // Trigger send on Enter key

        if(!isNewUser) {
            String s = user.getUserFriends();
            String[] userFriends = s.split(";");
            for(int i = 0; i < userFriends.length; i++) {
                friendListModel.addElement(userFriends[i]);
            }
        }

    }
    private void selectFriend()  {
        try {
            currentFriend = friendList.getSelectedValue();
            Message messager = new Message(user);
            if (currentFriend != null) {
                messageArea.setText(""); // Clear the chat area
                setTitle("Chat with " + currentFriend);
                // TODO: Load chat history with the selected friend from the server or local storage
                String s = messager.getMessages(currentFriend);
                String[] messages = s.split("\n");


            }
        } catch (UserException ex) {
            ex.printStackTrace();
        }
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && currentFriend != null) {
            try {
                out.println("4");
                out.println(currentFriend);
                out.println(message);
                
                String response = in.readLine();
                if (response.startsWith("Message sent")) {
                    messageArea.append("You: " + message + "\n");
                    inputField.setText("");
                }
                else {
                    showErrorDialog(response);
                }



            } catch (IOException ex) {
                showErrorDialog("Error sending message " + ex.getMessage());
            }
        if (!message.isEmpty()) {
            messageArea.append("You: " + message + "\n");
            inputField.setText("");

            // TODO: Implement actual message sending logic with the server/client
        }
    }

   /*  private void openSearchedScreen(User user, boolean isNewUser) {
        SearchedScreen searchedScreen = new SearchedScreen(loginGUI, this, getUser(), isNewUser);
        searchedScreen.setVisible(true);
        this.setVisible(false);
    }*/

    public void displayMessage(String sender, String message) {
        messageArea.append(sender + ": " + message + "\n");
    }

    public void search (String searched) {
        String result = "1";
        System.out.println("Before: " + result);
        if (!searched.isEmpty()) {
            result = client.client("6", searched);
            System.out.println("After: " + result);
        }
        //System.out.println("After: " + result);
        //return result;
        SearchedScreen searchedScreen = new SearchedScreen(loginGUI, this, getUser(), searched);
        searchedScreen.setVisible(true);
        //this.setVisible(false);
    }
}

class SearchedScreen extends JFrame {
    private User user;
    private boolean isNewUser;
    private String searched;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> friendList;
    private DefaultListModel<String> friendListModel;
    private String currentFriend = null;
    private SocialMediaClient client;


    public SearchedScreen(SocialMediaGUI loginGUI, MainScreen mainGUI, User user, String searched) {
        this.user = user;
        //this.isNewUser = isNewUser;
        this.searched = searched;
        setTitle(searched);
        setSize(250, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(mainGUI);
        client = new SocialMediaClient(loginGUI.getUsername(), loginGUI.getPassword());
    }

    private void initializeComponents(MainScreen mainGui) {
        setLayout(new BorderLayout());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            mainGui.setVisible(true);
            this.dispose();
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(cancelButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //client = new SocialMediaClient();

        //chat display
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        add(messageScrollPane, BorderLayout.CENTER);

        //message input
        JPanel interactionPanel = new JPanel(new BorderLayout());
        String s = user.getUserFriends();
        String[] userFriends = s.split(";");
        boolean isFriend = false;
        for(int i = 0; i < userFriends.length; i++) {
            if (userFriends[i].equals(searched))
                isFriend = true;
        }
        if (isFriend) {
            JButton removeButton = new JButton("Remove");
            JButton blockButton = new JButton("Block");
            interactionPanel.add(blockButton, BorderLayout.EAST);
            interactionPanel.add(removeButton, BorderLayout.LINE_START);
        } else {
            JButton addButton = new JButton("Add");
            interactionPanel.add(addButton, BorderLayout.EAST);
        }
        add(interactionPanel, BorderLayout.SOUTH);

       //sendButton.addActionListener(e -> sendMessage());
       // inputField.addActionListener(e -> sendButton.doClick()); // Trigger send on Enter key

        /*if(!isNewUser) {
            String s = user.getUserFriends();
            String[] userFriends = s.split(";");
            for(int i = 0; i < userFriends.length; i++) {
                friendListModel.addElement(userFriends[i]);
            }
        }*/

    }
}

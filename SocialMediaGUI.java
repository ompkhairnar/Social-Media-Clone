import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public void resetLoginScreen() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        usernameField.requestFocus();
        loginButton.setEnabled(true);
        signUpButton.setEnabled(true);
    }

    private class LoginAction implements ActionListener {

        private User user;
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                user = new User(username, password);
                SocialMediaGUI.this.loggedInUsername = username;
                //System.out.println(SocialMediaGUI.this.loggedInUsername);
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
        private User user;
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                user = new User(username, password, true);
                SocialMediaGUI.this.loggedInUsername = username;
                SocialMediaGUI.this.loggedInPass = password;
                showSuccessDialog("Account created successfully!");
                openMainScreen(user, true);
            } catch (UserException ex) {
                showErrorDialog("Error: " + ex.getMessage());
            }
        }
    }

    private void openMainScreen(User user, boolean isNewUser) {
        MainScreen mainScreen = new MainScreen(this, user, isNewUser);
        mainScreen.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }

    public String getUsername() {
        //System.out.println(this.loggedInUsername);
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

    private Timer timer;

    public MainScreen(SocialMediaGUI loginGUI, User user, boolean isNewUser) {
        this.user = user;
        this.isNewUser = isNewUser;
        this.loginGUI = loginGUI;
        setTitle("Social Media Platform");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(loginGUI);
        System.out.println(loginGUI.getName());
        //System.out.println("USERRR: " + loginGUI.getUsername());
        client = new SocialMediaClient(loginGUI.getUsername(), loginGUI.getPassword());
    }

    public User getUser() {
        return user;
    }


    public void initializeComponents(SocialMediaGUI loginGUI) {
        setLayout(new BorderLayout());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            //loginGUI.resetLoginScreen();
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
        sendButton.setBackground(new Color(122, 184, 253));
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            try {
                sendMessage();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        inputField.addActionListener(e -> sendButton.doClick()); // Trigger send on Enter key

        if(!isNewUser) {
            String s = user.getUserFriends();
            System.out.println("USER IS " + user.getUsername());
            System.out.println("USER FRIENDS " + s);
            //System.out.println("Friend: " + s);
            String[] userFriends = s.split(";");
            for(int i = 0; i < userFriends.length; i++) {
                friendListModel.addElement(userFriends[i]);
            }
        }

    }

    private void selectFriend() {
        try {
            String selectedValue = friendList.getSelectedValue(); // Get the current selection
            if (selectedValue == null || selectedValue.equals(currentFriend)) {
                return;
            }
            stopTimer(); 

            currentFriend = selectedValue; // Update the current friend

            Message messager = new Message(user);
            messageArea.setText("");
            setTitle("Chat with " + currentFriend);
            String messages = messager.getMessages(currentFriend);
            String[] messagesArray = messages.split("\n");

            for (String msg : messagesArray) {
                messageArea.append(msg + "\n");
            }

            String messagesss;
            try {
                messager = new Message(user);
            } catch (UserException e) {
                e.printStackTrace();
            }
            messagesss = messager.getMessages(currentFriend);
            String[] splitMessages = messagesss.split("\n");
            String lastMessage = splitMessages[splitMessages.length - 1];
        
            createTimer(lastMessage, messager);
            
        } catch (UserException ex) {
            ex.printStackTrace();
        }
    }
    private void createTimer(String lastMessage, Message messager) {
        System.out.println("Timer started");

        final String[] lastPrintedMessage = {lastMessage};
    
        int delay = 1000;
        timer = new Timer(delay, event -> {
            try {
                if (currentFriend == null || currentFriend.isEmpty()) {
                    return;
                }
    
                String messages = messager.getMessages(currentFriend);
                String[] messagesArray = messages.split("\n");
                boolean startReading = false; 
    
                for (String message : messagesArray) {
                    // Print only if it's new
                    if (startReading) {
                        messageArea.append(message + "\n");
                        lastPrintedMessage[0] = message; // Update the last printed message
                    }
                    if(message.equals(lastPrintedMessage[0])){
                        startReading = true; 
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        
        timer.start();
    }
    
    public void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            System.out.println("Timer stopped.");
        }
    }

    private void sendMessage() throws IOException {

        try {

            String message = inputField.getText().trim();
            Message mes = new Message(user);
            if (!message.isEmpty()) {
                inputField.setText("");
                String name = mes.getFileName(currentFriend); 
                int line = countLinesInFile(name); 
                mes.messageUser(currentFriend, ((line) + ": " +user.getUsername() + ": " + message));

            }

        } catch (UserException e) {
        }

    }
    public static int countLinesInFile(String name) throws IOException {
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        }
        return lineCount;
    }

    public void displayMessage(String sender, String message) {
        messageArea.append(sender + ": " + message + "\n");
    }

    public void search (String searched) {
        String result = "1";
        //System.out.println("Before: " + result);
        if (!searched.isEmpty()) {
            result = client.client("6", searched);
            //System.out.println("After: " + result);
        
            //System.out.println("After: " + result);
            //return result;
            //if (result.equals(result))
            SearchedScreen searchedScreen = new SearchedScreen(loginGUI, this, getUser(), searched, isNewUser);
            //System.out.println("RESULT: " + result);
            if (result.equals(searched) || result.equals("Blocked"))
                searchedScreen.setVisible(true);
            //else if (result.equals("Blocked"))
            //    JOptionPane.showMessageDialog(null, searched + " is blocked", "Blocked", JOptionPane.ERROR_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, "User does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    private SocialMediaGUI loginGUI;
    private MainScreen mainGUI;


    public SearchedScreen(SocialMediaGUI loginGUI, MainScreen mainGUI, User user, String searched, boolean isNewUser) {
        this.user = user;
        //this.isNewUser = isNewUser;
        this.searched = searched;
        this.loginGUI = loginGUI;
        this.mainGUI = mainGUI;
        setTitle("User: " + searched);
        setSize(250, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(mainGUI);
        client = new SocialMediaClient(loginGUI.getUsername(), loginGUI.getPassword());
    }

    private void initializeComponents(MainScreen mainGui) {
        setLayout(new BorderLayout());

        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.GRAY);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            mainGui.setVisible(true);
            this.dispose();
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.EAST);
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
        String b = user.getUserBlocked();
        String[] userFriends = s.split(";");
        String[] userBlocked = b.split(";");
        boolean isFriend = false;
        boolean isBlocked = false;
        for(int i = 0; i < userFriends.length; i++) {
            if (userFriends[i].equals(searched))
                isFriend = true;
        }
        for(int i = 0; i < userBlocked.length; i++) {
            if (userBlocked[i].equals(searched)) {
                System.out.println("still blocked");
                isBlocked = true;
            }
        }
        if (isFriend) {
            JButton removeButton = new JButton("Remove");
            removeButton.setBackground(Color.YELLOW);
            removeButton.setOpaque(true);
            removeButton.setBorderPainted(false);
            removeButton.addActionListener(e -> {
                remove(searched);
            });
            JButton blockButton = new JButton("Block");
            blockButton.setBackground(Color.RED);
            blockButton.setOpaque(true);
            blockButton.setBorderPainted(false);
            blockButton.addActionListener(e -> {
                block(searched);
            });
            interactionPanel.add(blockButton, BorderLayout.EAST);
            interactionPanel.add(removeButton, BorderLayout.LINE_START);
        } else if (isBlocked) {
            JButton unblockButton = new JButton("Unblock");
            unblockButton.setBackground(Color.GRAY);
            unblockButton.setOpaque(true);
            unblockButton.setBorderPainted(false);
            unblockButton.addActionListener(e -> {
                unblock(searched);
            });
            interactionPanel.add(unblockButton, BorderLayout.EAST);
        } else {
            JButton addButton = new JButton("Add");
            addButton.setBackground(Color.GREEN);
            addButton.setOpaque(true);
            addButton.setBorderPainted(false);
            addButton.addActionListener(e -> {
                add(searched);
                //mainGUI.initializeComponents(loginGUI);
            });
            interactionPanel.add(addButton, BorderLayout.EAST);
        }
        add(interactionPanel, BorderLayout.SOUTH);

    }

    public void add (String addedUser) {
        String result = client.client("2", addedUser);
        if (result.equals(addedUser)) {
            User newUser = null;
            try {
                newUser = new User(loginGUI.getUsername(), loginGUI.getPassword());
            } catch (UserException e) {
                e.printStackTrace();
            }
            System.out.println("IN ADD: " + newUser.getUserFriends());
            MainScreen mainScreen = new MainScreen(loginGUI, newUser, isNewUser);
            mainScreen.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
    }

    public void remove (String removedUser) {
        String result = client.client("3", removedUser);
        if (result.equals(removedUser)) {
            User newUser = null;
            try {
                newUser = new User(loginGUI.getUsername(), loginGUI.getPassword());
            } catch (UserException e) {
                e.printStackTrace();
            }
            MainScreen mainScreen = new MainScreen(loginGUI, newUser, isNewUser);
            mainScreen.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
    }

    public void block (String blockedUser) {
        String result = client.client("1", blockedUser);
        if (result.equals(blockedUser)) {
            User newUser = null;
            try {
                newUser = new User(loginGUI.getUsername(), loginGUI.getPassword());
            } catch (UserException e) {
                e.printStackTrace();
            }
            MainScreen mainScreen = new MainScreen(loginGUI, newUser, isNewUser);
            mainScreen.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
    }

    public void unblock (String blockedUser) {
        String result = client.client("8", blockedUser);
        if (result.equals(blockedUser)) {
            User newUser = null;
            try {
                newUser = new User(loginGUI.getUsername(), loginGUI.getPassword());
            } catch (UserException e) {
                e.printStackTrace();
            }
            MainScreen mainScreen = new MainScreen(loginGUI, newUser, isNewUser);
            mainScreen.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
    }
}

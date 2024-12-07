import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

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

    public SocialMediaGUI(String nothing) {

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
        public void startServer(String username, String password) {
            try (Socket socket = new Socket("localhost", 4545);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out.println(username);
                out.println(password);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

        }

        private User user;

        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            startServer(username, password);
            try {
                user = new User(username, password);
                SocialMediaGUI.this.loggedInUsername = username;
                System.out.println(SocialMediaGUI.this.loggedInUsername);
                System.out.println("Instance in LoginAction: " + SocialMediaGUI.this);
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
                new User(username, password, true);
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
    }

    public String getUsername() {
        System.out.println(this.loggedInUsername);
        System.out.println("Instance in getUsername: " + this);
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

class MainScreen extends JFrame  {
    private User user;
    private boolean isNewUser;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> friendList;
    private DefaultListModel<String> friendListModel;
    private String currentFriend = null;
    private SocialMediaClient client;
    private Timer timer;

    public MainScreen(SocialMediaGUI loginGUI, User user, boolean isNewUser) {
        this.user = user;
        this.isNewUser = isNewUser;
        setTitle("Social Media Platform");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(loginGUI);
        
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
            // System.out.println("Size");
            System.out.println(search(searchBar.getText()));
        });
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchBar, BorderLayout.WEST);
        searchPanel.add(searchButton, BorderLayout.LINE_END);
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(searchPanel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        client = new SocialMediaClient();

        // left sidebar
        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFixedCellWidth(150);
        friendList.addListSelectionListener(e -> selectFriend());

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setPreferredSize(new Dimension(150, 0));
        add(friendScrollPane, BorderLayout.WEST);

        // chat display
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        add(messageScrollPane, BorderLayout.CENTER);

        // message input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendButton.doClick()); // Trigger send on Enter key

        if (!isNewUser) {
            String s = user.getUserFriends();
            String[] userFriends = s.split(";");
            for (int i = 0; i < userFriends.length; i++) {
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

    private void sendMessage() {

        try {

            String message = inputField.getText().trim();
            Message mes = new Message(user);
            if (!message.isEmpty()) {
                inputField.setText("");

                mes.messageUser(currentFriend, (user.getUsername() + ": " + message));

            }

        } catch (UserException e) {
        }

    }

    public void displayMessage(String sender, String message) {
        messageArea.append(sender + ": " + message + "\n");
    }

    public String search(String searched) {
        String result = "1";
        System.out.println("Before: " + result);
        if (!searched.isEmpty()) {
            result = client.client("6", searched);
        }
        System.out.println("After: " + result);
        return result;
    }

   
}

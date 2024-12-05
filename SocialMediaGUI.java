import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SocialMediaGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

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

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                new User(username, password);
                showSuccessDialog("Login successful!");
                openMainScreen();
            } catch (UserException ex) {
                showErrorDialog("Error: " + ex.getMessage());
            }
        }
    }

    private class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                new User(username, password, true);
                showSuccessDialog("Account created successfully!");
                openMainScreen();
            } catch (UserException ex) {
                showErrorDialog("Error: " + ex.getMessage());
            }
        }
    }

    private void openMainScreen() {
        MainScreen mainScreen = new MainScreen(this);
        mainScreen.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SocialMediaGUI gui = new SocialMediaGUI();
            gui.setVisible(true);
        });
    }
}


class MainScreen extends JFrame {
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> friendList; // Sidebar for friends
    private DefaultListModel<String> friendListModel; // Model for the JList
    private String currentFriend = null; // Tracks which friend is selected for chatting

    public MainScreen(SocialMediaGUI loginGUI) {
        setTitle("Social Media Platform");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents(loginGUI);
    }

    private void initializeComponents(SocialMediaGUI loginGUI) {
        setLayout(new BorderLayout());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loginGUI.setVisible(true);
            this.dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFixedCellWidth(150);
        friendList.addListSelectionListener(e -> selectFriend());

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setPreferredSize(new Dimension(150, 0));
        add(friendScrollPane, BorderLayout.WEST);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        add(messageScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendButton.doClick()); // Trigger send on Enter key

        //add from acc friendlist

        friendListModel.addElement("Alice");
        friendListModel.addElement("Bob");
        friendListModel.addElement("Charlie");
    }

    private void selectFriend() {
        currentFriend = friendList.getSelectedValue();
        if (currentFriend != null) {
            messageArea.setText("");
            setTitle("Chat with " + currentFriend);
            // TODO: Load chat history with the selected friend from the server or local storage
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (currentFriend == null) {
            JOptionPane.showMessageDialog(this, "Please select a friend to chat with!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!message.isEmpty()) {
            messageArea.append("You: " + message + "\n");
            inputField.setText("");

            // TODO: Implement actual message sending logic with the server/client for the current friend
        }
    }

    public void displayMessage(String sender, String message) {
        messageArea.append(sender + ": " + message + "\n");
    }

    public void addFriend(String friendName) {
        if (!friendListModel.contains(friendName)) {
            friendListModel.addElement(friendName);
        }
    }
}

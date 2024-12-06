import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SocialMediaGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

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
                out.println("LOGIN");
                out.println(username);
                out.println(password);
                
                String response = in.readLine(); 
                if ("Successfully Logged In!".equals(response)) {
                    showSuccessDialog("Login successful!");
                    openMainScreen(new User(username, password), false);
                } else {
                    showErrorDialog("Login failed: " + response);
                }
            } catch (IOException | UserException ex) {
                showErrorDialog("Connection error: Could not log in.");
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
    // private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    public MainScreen(SocialMediaGUI loginGUI, User user, boolean isNewUser) {
        this.user = user;
        this.isNewUser = isNewUser;
        setTitle("Social Media Platform");
        setSize(600, 400);
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
<<<<<<< HEAD
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
=======
        if (!message.isEmpty()) {
            messageArea.append("You: " + message + "\n");
            inputField.setText("");

            // TODO: Implement actual message sending logic with the server/client
>>>>>>> parent of 96acdac (updated all of gui to have messaging features but still need to figure out server connection)
        }
    }

    public void displayMessage(String sender, String message) {
        messageArea.append(sender + ": " + message + "\n");
    }
}

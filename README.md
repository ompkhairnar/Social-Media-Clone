# Project Details

## Compilation and Execution Instructions

1. **Compilation**: 
   - To compile the project, navigate to the project directory and use the following command:
     ```bash
     javac *.java
     ```
   - This command will compile all `.java` files in the directory.

2. **Execution**:
   - Run the main program using the following command:
     ```bash
     java FoundationDataBase
     ```
     
## Submission Summary

| Student | Parts Done | Parts Submitted | Date Submitted | 
| ------- | ---------- | --------------- | -------------- |
| Richard Yom | Second half of database file, README file | Database | 11/3 |
|             | SocialMediaClient: handle, send, & retrieve, readMe |  readMe file | 11/17 |
|             | readMe file, server to GUI connection               | n/a          | 12/7 |
| Om Khairnar | Messager-related files, test cases | Messenger | 11/3 |
|             | SocialMediaServer and test cases | n/a | 11/17 |
|             | View all current messages | n/a | 12/7 |
| Sawyer Boursheski | User-related files, test cases | User | 11/3 |
|                   | SocialMediaClient functionality, test cases for server | SocialMediaClient and SocialMediaServer | 11/17 |
|                   | Log-in, Log-out, Sign in screen | SocialMediaGUI | 12/7 |
| Bidit Acharyya | First half of database file, test cases | n/a | 11/3 | 
|                | SocialMediaClient and test cases | Interfaces, TestCases | 11/17 | 
|                | Search bar, User Viewer, Add, Block, and Remove functionality | n/a | 12/7 | 



## Class Descriptions

### 1. `User.java`
   - **Description**: This class represents a user in the social media platform. It manages user details and interactions, including user authentication and data handling. 
   - **Functionality**: Implements methods for user login, registration, and data retrieval.
   - **Testing**: Verified through `UserTest.java`, which covers both normal and edge cases for data processing.
   - **Relationship**: Interacts with `UserInterface.java` for UI logic and `FoundationDatabase.java` for data storage.

### 2. `UserInterface.java`
   - **Description**: Defines the interface for user operations, such as registration and login methods.
   - **Functionality**: Provides an abstraction layer for user-related functionalities.
   - **Testing**: Testing covered in `UserTest.java`.
   - **Relationship**: Implemented by `User.java` to ensure the user methods adhere to a consistent interface.

### 3. `UserException.java`
   - **Description**: A custom exception class for handling user-related errors.
   - **Functionality**: Throws specific errors in cases like failed login attempts or invalid user data.
   - **Testing**: Error scenarios tested within `UserTest.java`.
   - **Relationship**: Used in `User.java` to handle exceptions effectively.

### 4. `Message.java`
   - **Description**: This class represents a message that can be sent between users in the platform.
   - **Functionality**: Contains methods for creating, sending, and managing messages.
   - **Testing**: Verified through `MessageTest.java`.
   - **Relationship**: Works with `MessageInterface.java` and utilizes `FoundationDatabase.java` for message storage.

### 5. `MessageInterface.java`
   - **Description**: Defines the required methods for message handling, ensuring consistency in message functionalities.
   - **Functionality**: Outlines methods for message operations.
   - **Testing**: Interface functionality checked via `MessageTest.java`.
   - **Relationship**: Implemented by `Message.java`.

### 6. `MessageException.java`
   - **Description**: A custom exception class for message-related errors.
   - **Functionality**: Handles exceptions in scenarios like invalid message formats or failed sends.
   - **Testing**: Covered within `MessageTest.java`.
   - **Relationship**: Used within `Message.java`.

### 7. `FoundationDatabase.java`
   - **Description**: Central data management class for handling user and message data.
   - **Functionality**: Stores and retrieves data related to users and messages.
   - **Testing**: Verified with `DatabaseTest.java`.
   - **Relationship**: Interacts with both `User.java` and `Message.java` for data operations.

### 8. `DatabaseTest.java`
   - **Description**: Testing suite for the `FoundationDatabase` class.
   - **Functionality**: Verifies data management functionality, including user and message storage.
   - **Testing**: Covers scenarios related to database functionality, such as data retrieval and storage consistency.
   - **Relationship**: Tests `FoundationDatabase.java` directly.

### 9. `UserTest.java`
   - **Description**: Testing suite for `User.java`.
   - **Functionality**: Ensures that user operations like login and registration work correctly.
   - **Testing**: Focuses on different user scenarios, including edge cases and exception handling.
   - **Relationship**: Tests the `User.java` and `UserException.java` classes.

### 10. `MessageTest.java`
   - **Description**: Testing suite for `Message.java`.
   - **Functionality**: Ensures that message operations like sending and receiving work correctly.
   - **Testing**: Verifies message functionality and exception handling.
   - **Relationship**: Tests the `Message.java` and `MessageException.java` classes.

### 11. `SocialMediaClient.java`
- **Description**: This class represents the client-side of the social media platform. It enables communication with the server, allowing users to send messages, manage relationships (add/remove friends, block users), and retrieve messages. The client handles user inputs and sends requests to the server while processing responses from the server.
- **Functionality**:  
  - Implements methods for:
    - Sending messages.
    - Retrieving message history.
    - Adding and removing friends.
    - Blocking users.
  - Maintains a persistent connection with the server using `Socket`, `ObjectOutputStream`, and `ObjectInputStream`.
  - Includes a listener thread to handle incoming messages or notifications from the server.
- **Testing**: Verified through manual testing of client-server interactions, including edge cases like invalid usernames, login failures, and blocked user scenarios. 
- **Relationship**:  
  - Communicates with the server via `SocialMediaServer.java`.
  - Uses `Message.java` for local message storage and sending messages.
  - Interacts with `User.java` and `UserInterface.java` for user-related operations and data validation.

### 12. `SocialMediaClientInterface.java`
- **Description**: This interface provides the structure for user authentication, sending and receiving messages, and managing the client's connection to the server.
- **Functionality**:  
  - `userLogin(String username, String password)`: Authenticates the user with the server using the provided credentials.
  - `sendMessage(String sender, String receiver, String content)`: Sends a message from the sender to the receiver.
  - `handleMessage()`: Handles incoming messages or notifications from the server.
  - `close()`: Closes the connection between the client and the server, releasing all resources.
  - `retrieveMessages(String sender, String receiver)`: Retrieves the message history between the sender and the receiver.
- **Testing**: Verified through implementation in `SocialMediaClient.java`.
- **Relationship**: Implemented by `SocialMediaClient.java`.
  
### 13. `SocialMediaServer.java`
- **Description**: This class represents the server-side of the social media platform. It manages client connections, processes requests (e.g., user login, messaging, and friend management), and ensures data consistency across all clients. The server runs in a multithreaded environment to handle multiple clients simultaneously.
- **Functionality**:  
  - Implements methods for:
    - Authenticating users during login.
    - Handling requests to send messages, retrieve messages, and manage user relationships (add, remove, block).
    - Synchronizing shared resources like user data and message storage.
  - Runs a multithreaded server using `ServerSocket` and `Socket` to allow concurrent connections.
  - Provides robust error handling for failed operations or invalid inputs.
- **Testing**: Verified through integration testing with `SocialMediaClient.java`, focusing on scenarios like invalid login attempts, concurrent client connections, blocked user restrictions, and message history retrieval. SocialMediaServerTest.java
- **Relationship**:  
  - Communicates with `SocialMediaClient.java` to handle client requests and responses.
  - Uses `FoundationDatabase.java` for persistent user data storage and retrieval.
  - Relies on `User.java` and `Message.java` for user-related operations and message management.

 ### 14. `SocialMediaServerInterface.java`
- **Description**: This interface provides the structure for starting, stopping, and managing the server, as well as handling individual client connections.
- **Functionality**:  
  - `startServer()`: Starts the server and initializes the `ServerSocket` to listen for incoming client connections.
  - `stopServer()`: Stops the server and closes all active connections gracefully.
  - `isRunning()`: Returns the current status of the server (running or stopped).
  - `handleClient(Socket clientSocket)`: Handles communication with a connected client using the given socket.
- **Testing**: Verified through integration with `SocialMediaServer.java`.
- **Relationship**: Implemented by `SocialMediaServer.java`.

### `SocialMediaGUI.java`
- **Description**: 
  This class serves as the graphical user interface (GUI) for the social media platform, allowing users to log in, sign up, and navigate the platform. It provides a seamless user experience for managing user accounts and engaging with other users through a chat interface.

- **Functionality**:  
  - Provides GUI components for:
    - User login via username and password fields.
    - Account creation with validation.
    - Navigating to the main chat screen upon successful login or signup.
  - Implements listeners for buttons to handle login and signup actions, and provides feedback to users through dialog boxes.
  - Communicates with the `User` class to validate login/signup operations and manage user data.
  - Uses `MainScreen` to transition users to the main chat interface.

- **Testing**:
  - Verified through manual testing:
    - Successful and failed login attempts.
    - Account creation with valid and invalid inputs.
    - Navigation between screens.
  - Edge cases tested:
    - Handling empty fields.
    - Preventing duplicate accounts.

   ### `MainScreen class`
   - **Description**: 
     This class represents the main interface where users can engage in real-time messaging with their friends, manage their friend list, and perform user searches.
   
   - **Functionality**:  
     - Provides GUI components for:
       - A friend list displayed on the left sidebar.
       - A chat area for viewing and sending messages to the selected friend.
       - A search bar for finding other users on the platform.
       - A logout button to return to the login screen.
     - Implements listeners for friend selection, search operations, and sending messages.
     - Integrates with the `Message` class for retrieving and sending chat messages.
     - Periodically updates the chat using a `Timer` to fetch new messages in real-time.
   
   - **Testing**:
     - Verified through manual testing:
       - Sending and receiving messages.
       - Accurate display of chat history.
       - Proper handling of friend selection and search actions.
     - Edge cases tested:
       - Sending empty messages.
       - Switching between friends during active chats.

   ### `SearchedScreen class`
   - **Description**: 
     This class provides the interface for interacting with a searched user, allowing actions like adding, removing, blocking, or unblocking the user.
   
   - **Functionality**:  
     - Displays interaction options (e.g., Add Friend, Block User) based on the relationship status with the searched user.
     - Communicates with the `SocialMediaClient` class to execute user actions like adding or blocking.
     - Returns to the main screen upon completing an action or canceling.
   
   - **Testing**:
     - Verified through manual testing:
       - Correct options displayed based on user relationship (friend, blocked, or unknown).
       - Successful execution of actions like adding or blocking a user.
     - Edge cases tested:
       - Adding an already blocked user.
       - Blocking a user already on the friend list.

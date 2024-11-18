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
|             | SocialMediaClient: handle, send, & retrieve, readME |  n/a    | 11/17 |
| Om Khairnar | Messager-related files, test cases | Messenger | 11/3 |
|             | SocialMediaServer and test cases | SocialMediaServer | 11/17 |
| Sawyer Boursheski | User-related files, test cases | User | 11/3 |
|                   | SocialMediaClient functionality, test cases for server | SocialMediaClient | 11/17 |
| Bidit Acharyya | First half of database file, test cases | N/A | 11/3 | 
|             | SocialMediaClient and test cases | N/A | 11/17 | 



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
- **Description**:  
  This class represents the client-side of the social media platform. It enables communication with the server, allowing users to send messages, manage relationships (add/remove friends, block users), and retrieve messages. The client handles user inputs and sends requests to the server while processing responses from the server.

- **Functionality**:  
  - Implements methods for:
    - Sending messages.
    - Retrieving message history.
    - Adding and removing friends.
    - Blocking users.
  - Maintains a persistent connection with the server using `Socket`, `ObjectOutputStream`, and `ObjectInputStream`.
  - Includes a listener thread to handle incoming messages or notifications from the server.

- **Testing**:  
  Verified through manual testing of client-server interactions, including edge cases like invalid usernames, login failures, and blocked user scenarios. 

- **Relationship**:  
  - Communicates with the server via `SocialMediaServer.java`.
  - Uses `Message.java` for local message storage and sending messages.
  - Interacts with `User.java` and `UserInterface.java` for user-related operations and data validation.

### 12. `SocialMediaClientInterface.java`
- **Description**:  
  This interface provides the structure for user authentication, sending and receiving messages, and managing the client's connection to the server.

- **Functionality**:  
  - `userLogin(String username, String password)`: Authenticates the user with the server using the provided credentials.
  - `sendMessage(String sender, String receiver, String content)`: Sends a message from the sender to the receiver.
  - `handleMessage()`: Handles incoming messages or notifications from the server.
  - `close()`: Closes the connection between the client and the server, releasing all resources.
  - `retrieveMessages(String sender, String receiver)`: Retrieves the message history between the sender and the receiver.

- **Testing**:  
  - Verified through implementation in `SocialMediaClient.java`.

- **Relationship**:  
  - Implemented by `SocialMediaClient.java`.
  
### 13. `SocialMediaServer.java`
- **Description**:  
  This class represents the server-side of the social media platform. It manages client connections, processes requests (e.g., user login, messaging, and friend management), and ensures data consistency across all clients. The server runs in a multithreaded environment to handle multiple clients simultaneously.

- **Functionality**:  
  - Implements methods for:
    - Authenticating users during login.
    - Handling requests to send messages, retrieve messages, and manage user relationships (add, remove, block).
    - Synchronizing shared resources like user data and message storage.
  - Runs a multithreaded server using `ServerSocket` and `Socket` to allow concurrent connections.
  - Provides robust error handling for failed operations or invalid inputs.

- **Testing**:  
  Verified through integration testing with `SocialMediaClient.java`, focusing on scenarios like invalid login attempts, concurrent client connections, blocked user restrictions, and message history retrieval. SocialMediaServerTest.java

- **Relationship**:  
  - Communicates with `SocialMediaClient.java` to handle client requests and responses.
  - Uses `FoundationDatabase.java` for persistent user data storage and retrieval.
  - Relies on `User.java` and `Message.java` for user-related operations and message management.

 ### 14. `SocialMediaServerInterface.java`
- **Description**:  
  This interface provides the structure for starting, stopping, and managing the server, as well as handling individual client connections.

- **Functionality**:  
  - `startServer()`: Starts the server and initializes the `ServerSocket` to listen for incoming client connections.
  - `stopServer()`: Stops the server and closes all active connections gracefully.
  - `isRunning()`: Returns the current status of the server (running or stopped).
  - `handleClient(Socket clientSocket)`: Handles communication with a connected client using the given socket.

- **Testing**:  
  - Verified through integration with `SocialMediaServer.java`.

- **Relationship**:  
  - Implemented by `SocialMediaServer.java`.

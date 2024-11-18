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

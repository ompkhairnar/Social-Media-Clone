# CS180-Group-Project
- Opens like imsg/snapchat format
- Can see a list of your friends and click on them
    - when you click on a friend, you can message them
    - will have an option to remove or block as well
- At the top, will have a search feature to search for people
    - if user exists and you dont have them added you will have an option to add
- User class, FoundationDatabase,

CSV contents: username, password, blockedList, friendsList

When viewing user: 

FoundationDatabase: 
- Fields: ArrayList<User>, userFileName, 
- Methods: readUserCsv, createUser, viewUser, search, deleteUser

User: addFriend, blockUser, removeFriend, sendMsg, deleteMsg

Exceptions: badUserException, 

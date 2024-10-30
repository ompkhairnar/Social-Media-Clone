public class Message {
    private User messager;
    public Message(User messager) throws UserException {
//        if(usernotfound) {
//            throw new usernotfound;
//        }
        this.messager = messager;
    }
    public void messageUser(User user, String message){
        /*check if user is valid
        check if file betwen users already exists
        if not then create new file
        then add the message to the file in a new line
        */
    }
    public User getMessager() {
        return messager;
    }

}

import java.io.IOException;
import java.net.Socket;

public interface SocialMediaServerInterface {
    void startServer() throws IOException;

    void stopServer();

    boolean isRunning();

    void handleClient(Socket clientSocket) throws IOException;
}

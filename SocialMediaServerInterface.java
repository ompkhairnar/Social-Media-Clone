import java.io.IOException;
import java.net.Socket;

/**
 * Interface for social media server
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @author Sawyer, Bidit, Richard, Om
 * @version 1.0 November 17th, 2024
 */

public interface SocialMediaServerInterface {
    void startServer() throws IOException;

    void stopServer();

    boolean isRunning();

    void handleClient(Socket clientSocket) throws IOException;
}

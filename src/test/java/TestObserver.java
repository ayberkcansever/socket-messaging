import com.cansever.socket.SocketClientObserver;

import java.net.Socket;

/**
 * User: TTACANSEVER
 */
public class TestObserver implements SocketClientObserver {
    @Override
    public void connectionEstablished(Socket socket) {
        System.out.println("Connection established " + socket);
    }

    @Override
    public void connectionCouldNotEstablished(String serverIp, int serverPort) {
        System.out.println("Connection could not be established " + serverIp + "/" + serverPort);
    }

    @Override
    public void connectionLost(Socket socket) {
        System.out.println("Connection lost " + socket);
    }
}

import com.cansever.socket.SocketConnectionHandler;

/**
 * User: TTACANSEVER
 */
public class SocketServer extends SocketConnectionHandler {

    @Override
    public void handleMessage() {
        System.out.println("Received" + " " + new String(receivedMessage) + " from port: " + socket.getPort());
    }

    @Override
    public void handleHeartBeat() {
        System.out.println("Server received heartbeat...");
    }


}

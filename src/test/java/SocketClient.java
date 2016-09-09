import com.cansever.socket.SocketConnectionHandler;

/**
 * User: TTACANSEVER
 */
public class SocketClient extends SocketConnectionHandler {

    private int reconnectPeriod;

    public SocketClient() {
    }

    public SocketClient(Integer reconnectPeriod) {
        super(reconnectPeriod);
    }

    @Override
    public void handleMessage() {
        System.out.println("Port " + socket.getLocalPort() + " received \"" + new String(receivedMessage) + "\"");
    }

    @Override
    public void handleHeartBeat() {
        System.out.println("Client received heartbeat...");
    }

}

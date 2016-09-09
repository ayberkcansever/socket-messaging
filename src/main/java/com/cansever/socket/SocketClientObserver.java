package com.cansever.socket;

import java.net.Socket;

/**
 * User: TTACANSEVER
 */
public interface SocketClientObserver {
    public void connectionEstablished(Socket socket);

    public void connectionCouldNotEstablished(String serverIp, int serverPort);

    public void connectionLost(Socket socket);
}

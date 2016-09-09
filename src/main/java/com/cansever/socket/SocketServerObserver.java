package com.cansever.socket;

import java.net.Socket;

/**
 * User: TTACANSEVER
 */
public interface SocketServerObserver {

    void connectionEstablished(Socket socket);
    void connectionLost(Socket socket);

}

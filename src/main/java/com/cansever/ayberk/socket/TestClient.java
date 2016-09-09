package com.cansever.ayberk.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: TTACANSEVER
 */
public class TestClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        for(int i = 0; i < 5; i++) {

            Socket s = new Socket();
            s.connect(new InetSocketAddress("localhost", 1234));

            s.getOutputStream().write("{aga} ".concat(s.getLocalPort() + "\r").getBytes());
            s.getOutputStream().flush();

            Thread.sleep(1000);

            s.close();

        }

    }

}

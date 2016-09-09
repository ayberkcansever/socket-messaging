package com.cansever.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * User: TTACANSEVER
 */
public class SocketUtils {

    public static void readUntilFlag(Socket clientSocket, DataInputStream socketInputStream, int flag) throws IOException {
        boolean found = false;
        byte[] flagBytes = ByteUtils.intToByteArray(flag);

        byte tempBytes[] = new byte[4];
        tempBytes[0] = socketInputStream.readByte();
        tempBytes[1] = socketInputStream.readByte();
        tempBytes[2] = socketInputStream.readByte();
        tempBytes[3] = socketInputStream.readByte();
        // read until the flag is found
        while (!clientSocket.isClosed() && clientSocket.isConnected() && !found) {
            if (flagBytes[0] == tempBytes[0] &&
                    flagBytes[1] == tempBytes[1] &&
                    flagBytes[2] == tempBytes[2] &&
                    flagBytes[3] == tempBytes[3]) {
                found = true;
            } else {
                tempBytes[0] = tempBytes[1];
                tempBytes[1] = tempBytes[2];
                tempBytes[2] = tempBytes[3];
                tempBytes[3] = socketInputStream.readByte();
            }
        }
    }

    public static byte[] getHeartBeatByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put((byte) 100);
        bb.put((byte) 111);
        return bb.array();
    }

}


import com.cansever.socket.SocketConnectionHandler;
import com.cansever.socket.SocketManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: TTACANSEVER
 */
public class TestSocketHandling {

    private static boolean isServer = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        if(isServer) {
            // listens in while
            new Thread(new ServerSenderThread()).start();
            SocketManager.initAsServer(1234, 1000, SocketServer.class);
        } else {
            for(int i = 0; i < 1000; i++)
                new ClientCreatorThread().start();
        }

    }

    static class ServerSenderThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    Iterator iter = SocketManager.getClientMap().values().iterator();
                    while (iter.hasNext()) {
                        System.out.println(SocketManager.getClientMap().size());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SocketConnectionHandler client = (SocketConnectionHandler) iter.next();
                        client.sendByteMessage(("hello " + client.getSocket().toString()).getBytes());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientCreatorThread extends Thread {

        private static Set<SocketConnectionHandler> clientSet = new HashSet<SocketConnectionHandler>();

        public void run() {
            try {
                Thread.sleep(1000);

                Class[] clazz = new Class[]{Integer.class};
                Object[] args = new Object[]{10000};

                SocketConnectionHandler client1 = SocketManager.initAsClient("localhost", 1234, 1000, SocketClient.class, clazz, args, new TestObserver());
                //SocketConnectionHandler client2 = SocketManager.initAsClient("localhost", 1234, 1000, SocketClient.class, clazz, args, new TestObserver());
                //SocketConnectionHandler client3 = SocketManager.initAsClient("localhost", 1234, 1000, SocketClient.class, clazz, args, new TestObserver());
                //SocketConnectionHandler client1 = SocketManager.initAsClient("localhost", 1234, SocketClient.class, clazz, args, new TestObserver());

                while (true) {
                    Thread.sleep(2000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}

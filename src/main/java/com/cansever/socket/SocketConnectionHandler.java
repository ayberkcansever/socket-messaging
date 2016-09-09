package com.cansever.socket;

import org.bn.CoderFactory;
import org.bn.IEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * User: TTACANSEVER
 */
public abstract class SocketConnectionHandler extends Thread {

   public static final int START_FLAG = 111999;
   public static final int STOP_FLAG = 999111;

   protected IEncoder<Object> encoder;
   protected Socket socket;
   protected String serverIP;
   protected int port;
   protected String localIP = null;
   protected int localPort = -1;
   protected DataInputStream socketInputStream = null;
   protected DataOutputStream socketOutputStream = null;
   protected byte[] receivedMessage = null;
   private boolean connected = false;
   private boolean server = false;
   private boolean started = false;
   private boolean heartBeatEnabled = false;
   private boolean heartBeatThreadWorking = false;
   private boolean heartBeatControllerWorking = false;
   private long heartBeatPeriod = 8 * 1000;
   private SocketClientObserver observer;

   private int reconnectAttemptPeriod = 5000;
   private long lastHeartBeatReceivedTime = System.currentTimeMillis();
   private boolean keepRunning = true;

   public abstract void handleMessage();

   public abstract void handleHeartBeat();

   protected SocketConnectionHandler() {
   }

   protected SocketConnectionHandler(Integer reconnectAttemptPeriod) {
      this.reconnectAttemptPeriod = reconnectAttemptPeriod;
   }

   /**
    *
    * @param socket
     */
   public void initAsServer(Socket socket) {
      this.server = true;
      this.socket = socket;
      try {
         this.socketInputStream = new DataInputStream(socket.getInputStream());
         this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
         this.encoder = CoderFactory.getInstance().newEncoder("BER");
      } catch (Exception e) {
         e.printStackTrace();
      }
      if (!started) {
         started = true;
         this.start();
      }
      connected = true;
      if (observer != null) {
         observer.connectionEstablished(socket);
      }
   }

   /**
    *
    * @param socket
    * @param heartBeatPeriod
     */
   public void initAsServer(Socket socket, long heartBeatPeriod) {
      this.heartBeatPeriod = heartBeatPeriod;
      this.heartBeatEnabled = true;
      this.lastHeartBeatReceivedTime = System.currentTimeMillis();
      this.server = true;
      this.socket = socket;
      try {
         this.socketInputStream = new DataInputStream(socket.getInputStream());
         this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
         if (!heartBeatThreadWorking) {
            new HeartBeatThread().start();
         }
         if (!heartBeatControllerWorking) {
            new HeartBeatController().start();
         }
         this.encoder = CoderFactory.getInstance().newEncoder("BER");
      } catch (Exception e) {
         e.printStackTrace();
      }
      if (!started) {
         started = true;
         this.start();
      }
      connected = true;
      if (observer != null) {
         observer.connectionEstablished(socket);
      }
   }

   /**
    *
    * @param serverIp
    * @param port
    */
   public void initAsClient(String serverIp, int port) {
      this.serverIP = serverIp;
      this.port = port;
      try {
         socket = new Socket(serverIp, port);
         socket.setReuseAddress(true);
      } catch (ConnectException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      } catch (IOException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      }
      if (socket != null) {
         try {
            this.socketInputStream = new DataInputStream(socket.getInputStream());
            this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
            encoder = CoderFactory.getInstance().newEncoder("BER");
            if (observer != null) {
               observer.connectionEstablished(socket);
            }
         } catch (IOException e) {
            e.printStackTrace();
            if (observer != null) {
               observer.connectionCouldNotEstablished(serverIP, port);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         connected = true;
      } else {
         try {
            Thread.sleep(reconnectAttemptPeriod);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      if (!started) {
         started = true;
         this.start();
      }
   }

   public void initAsClient(String serverIp, int port, String localIp, int localPort) {
      this.serverIP = serverIp;
      this.port = port;
      this.localIP = localIp;
      this.localPort = localPort;

      try {
         socket = new Socket(serverIp, port, InetAddress.getByName(localIp), localPort);
         socket.setReuseAddress(true);
      } catch (ConnectException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      } catch (IOException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      }
      if (socket != null) {
         try {
            this.socketInputStream = new DataInputStream(socket.getInputStream());
            this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
            encoder = CoderFactory.getInstance().newEncoder("BER");
            if (observer != null) {
               observer.connectionEstablished(socket);
            }
         } catch (IOException e) {
            e.printStackTrace();
            if (observer != null) {
               observer.connectionCouldNotEstablished(serverIP, port);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         connected = true;
      } else {
         try {
            Thread.sleep(reconnectAttemptPeriod);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      if (!started) {
         started = true;
         this.start();
      }
   }

   /**
    * Handler is a client and connects to the server, heartbeat enabled
    * 
    * @param serverIp
    * @param port
    */
   public void initAsClient(String serverIp, int port, long heartBeatPeriod) {
      this.heartBeatPeriod = heartBeatPeriod;
      this.heartBeatEnabled = true;
      this.lastHeartBeatReceivedTime = System.currentTimeMillis();
      this.serverIP = serverIp;
      this.port = port;
      try {
         socket = new Socket(serverIp, port);
         socket.setReuseAddress(true);
      } catch (ConnectException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      } catch (IOException e) {
         if (observer != null) {
            observer.connectionCouldNotEstablished(serverIP, port);
         }
         e.printStackTrace();
      }
      if (socket != null) {
         try {
            this.socketInputStream = new DataInputStream(socket.getInputStream());
            this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
            if (!heartBeatThreadWorking) {
               new HeartBeatThread().start();
            }
            if (!heartBeatControllerWorking) {
               new HeartBeatController().start();
            }
            encoder = CoderFactory.getInstance().newEncoder("BER");
            if (observer != null) {
               observer.connectionEstablished(socket);
            }
         } catch (IOException e) {
            e.printStackTrace();
            if (observer != null) {
               observer.connectionCouldNotEstablished(serverIP, port);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
         connected = true;
         this.lastHeartBeatReceivedTime = System.currentTimeMillis();
      } else {
         try {
            Thread.sleep(reconnectAttemptPeriod);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      if (!started) {
         started = true;
         this.start();
      }
   }

   public void run() {
      while (keepRunning) {
         try {
            if (connected) {
               if (!server && socket.isClosed()) {
                  connected = false;
                  closeSocket();
                  if (observer != null) {
                     observer.connectionLost(socket);
                  }
               }
               if (!waitForMessage()) {
                  continue;
               }
               if (receivedMessage != null) {
                  handleMessage();
               }
            } else {
               if (server) {
                  keepRunning = false;
                  closeSocket();
               } else {
                  if (this.heartBeatEnabled) {
                     initAsClient(serverIP, port, heartBeatPeriod);
                  } else {
                     if (localIP != null && localPort != -1) {
                        initAsClient(serverIP, port, localIP, localPort);
                     } else {
                        initAsClient(serverIP, port);
                     }
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   protected boolean waitForMessage() {
      if (socket.isClosed() || socketInputStream == null) {
         return false;
      }

      try {
         // try to read the start flag
         SocketUtils.readUntilFlag(socket, socketInputStream, START_FLAG);

         int len = socketInputStream.readInt();
         receivedMessage = new byte[len];
         // read the data to byte array
         socketInputStream.readFully(receivedMessage, 0, len);

         // try to read the stop flag
         SocketUtils.readUntilFlag(socket, socketInputStream, STOP_FLAG);

         // heartbeat message
         if (receivedMessage.length == 2 && receivedMessage[0] == (byte) 100 && receivedMessage[1] == (byte) 111) {
            handleHeartBeat();
            lastHeartBeatReceivedTime = System.currentTimeMillis();
            receivedMessage = null;
         }

      } catch (IOException e) {
         e.printStackTrace();
         if (observer != null && connected) {
            observer.connectionLost(socket);
         }
         connected = false;
         closeSocket();
         return false;
      }

      return true;
   }

   public boolean sendByteMessage(byte[] messageBytes) {
      if (connected) {
         try {
            ByteBuffer bb = ByteBuffer.allocate(12 + messageBytes.length);
            bb.put(ByteUtils.intToByteArray(START_FLAG));
            bb.put(ByteUtils.intToByteArray(messageBytes.length));
            bb.put(messageBytes);
            bb.put(ByteUtils.intToByteArray(STOP_FLAG));
            socketOutputStream.write(bb.array());
            socketOutputStream.flush();
         } catch (IOException e) {
            if (observer != null && connected) {
               observer.connectionLost(socket);
            }
            connected = false;
            closeSocket();
            e.printStackTrace();
            return false;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
         return true;
      }
      return false;
   }

   public boolean sendBERMessage(Object message) {
      if (connected) {
         try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            encoder.encode(message, outputStream);
            ByteBuffer bb = ByteBuffer.allocate(12 + outputStream.toByteArray().length);
            bb.put(ByteUtils.intToByteArray(START_FLAG));
            bb.put(ByteUtils.intToByteArray(outputStream.toByteArray().length));
            bb.put(outputStream.toByteArray());
            bb.put(ByteUtils.intToByteArray(STOP_FLAG));
            socketOutputStream.write(bb.array());
            socketOutputStream.flush();
         } catch (IOException e) {
            if (observer != null && connected) {
               observer.connectionLost(socket);
            }
            connected = false;
            closeSocket();
            e.printStackTrace();
            return false;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
         return true;
      }
      return false;
   }

   public boolean isConnected() {
      return connected;
   }

   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   public int getReconnectAttemptPeriod() {
      return reconnectAttemptPeriod;
   }

   public Socket getSocket() {
      return socket;
   }

   public void setReconnectAttemptPeriod(int reconnectAttemptPeriod) {
      this.reconnectAttemptPeriod = reconnectAttemptPeriod;
   }

   public void registerClientObserver(SocketClientObserver observer) {
      this.observer = observer;
   }

   private void closeSocket() {
      try {
         if (server) {
            if (socket != null) {
               SocketManager.getClientMap().remove(socket.getRemoteSocketAddress().toString());
            }
         }
         if (socketInputStream != null)
            socketInputStream.close();
         if (socketOutputStream != null)
            socketOutputStream.close();
         if (socket != null)
            socket.close();
         socket = null;
      } catch (IOException e1) {
         e1.printStackTrace();
      }
   }

   class HeartBeatThread extends Thread {
      public void run() {
         heartBeatThreadWorking = true;
         while (true) {
            try {
               Thread.sleep(heartBeatPeriod);
               sendByteMessage(SocketUtils.getHeartBeatByteArray());
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }

      }
   }

   class HeartBeatController extends Thread {
      public void run() {
         heartBeatControllerWorking = true;
         while (true) {
            try {
               Thread.sleep(20);
               if (System.currentTimeMillis() - lastHeartBeatReceivedTime > 3 * heartBeatPeriod) {
                  if (observer != null && connected) {
                     observer.connectionLost(socket);
                  }
                  connected = false;
                  closeSocket();
                  break;
               }
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      }
   }
}

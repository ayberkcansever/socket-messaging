package com.cansever.socket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: TTACANSEVER
 */
public class SocketManager {

    private static ConcurrentHashMap<String, SocketConnectionHandler> clientMap = new ConcurrentHashMap<String, SocketConnectionHandler>();
    public static ConcurrentHashMap<String, SocketConnectionHandler> getClientMap() {
        return clientMap;
    }

    /**
     * waits for a client to connect to the port, then initializes the message handler with default constructor
     *
     * @param port
     * @param clazz
     * @return
     */
    public static void initAsServer(int port, Class<? extends SocketConnectionHandler> clazz) {
        initAsServer(port, clazz, null, null);
    }

    public static void initAsServer(int port, long heartBeatPeriod, Class<? extends SocketConnectionHandler> clazz) {
        initAsServer(port, heartBeatPeriod, clazz, null);
    }

    /**
     * waits for a client to connect to the port, then initializes the message handler with default constructor
     *
     * @param port
     * @param clazz
     * @return
     */
    public static void initAsServer(int port, long heartBeatPeriod, Class<? extends SocketConnectionHandler> clazz, SocketClientObserver observer) {
        initAsServer(port, heartBeatPeriod, clazz, null, null, observer);
    }


    /**
     * waits for a client to connect to the port, then initializes the message handler with constructor parameters
     *
     * @param port
     * @param clazz
     * @param argsClass
     * @param args
     * @return
     */
    public static void initAsServer(int port, Class<? extends SocketConnectionHandler> clazz, Class[] argsClass, Object[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    SocketConnectionHandler instance = (SocketConnectionHandler) clazz.getConstructor(argsClass).newInstance(args);
                    instance.initAsServer(clientSocket);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * waits for a client to connect to the port, then initializes the message handler with constructor parameters
     *
     * @param port
     * @param clazz
     * @param argsClass
     * @param args
     * @return
     */
    public static void initAsServer(int port, long heartBeatPeriod, Class<? extends SocketConnectionHandler> clazz, Class[] argsClass, Object[] args, SocketClientObserver observer) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    SocketConnectionHandler instance = (SocketConnectionHandler) clazz.getConstructor(argsClass).newInstance(args);
                    if(observer != null) {
                        instance.registerClientObserver(observer);
                    }
                    clientMap.put(clientSocket.getRemoteSocketAddress().toString(), instance);
                    instance.initAsServer(clientSocket, heartBeatPeriod);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * initializes a socket client with handler's default constructor
     *
     * @param clazz
     * @return
     */
    public static SocketConnectionHandler initAsClient(String serverIp, int port, Class<? extends SocketConnectionHandler> clazz, SocketClientObserver... observer) {
        return initAsClient(serverIp, port, clazz, null, null, observer);
    }

    /**
     * initializes a socket client with handler's default constructor, heartbeat enabled
     *
     * @param clazz
     * @return
     */
    public static SocketConnectionHandler initAsClient(String serverIp, int port, long heartBeatPeriod, Class<? extends SocketConnectionHandler> clazz, SocketClientObserver... observer) {
        return initAsClient(serverIp, port, heartBeatPeriod, clazz, null, null, observer);
    }

    /**
     * initializes a socket client which handler has constructor parameters
     *
     * @param clazz
     * @param argsClass
     * @param args
     * @return
     */
    public static SocketConnectionHandler initAsClient(String serverIp, int port, Class<? extends SocketConnectionHandler> clazz, Class[] argsClass, Object[] args, SocketClientObserver... observer) {
        SocketConnectionHandler instance = null;
        try {
            instance = (SocketConnectionHandler) clazz.getConstructor(argsClass).newInstance(args);
            instance.registerClientObserver(observer[0]);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        instance.initAsClient(serverIp, port);
        return instance;
    }


    public static SocketConnectionHandler initAsClient(String serverIp, int port, String localIp, int localPort, Class<? extends SocketConnectionHandler> clazz, SocketClientObserver... observer) {
        SocketConnectionHandler instance = null;
        try {
            instance = clazz.getConstructor(null).newInstance(null);
            instance.registerClientObserver(observer[0]);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        instance.initAsClient(serverIp, port, localIp, localPort);
        return instance;
    }

    /**
     * initializes a socket client which handler has constructor parameters and heartbeat enabled
     *
     * @param clazz
     * @param argsClass
     * @param args
     * @return
     */
    public static SocketConnectionHandler initAsClient(String serverIp, int port, long heartBeatPeriod, Class<? extends SocketConnectionHandler> clazz, Class[] argsClass, Object[] args, SocketClientObserver... observer) {
        SocketConnectionHandler instance = null;
        try {
            instance = (SocketConnectionHandler) clazz.getConstructor(argsClass).newInstance(args);
            instance.registerClientObserver(observer[0]);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        instance.initAsClient(serverIp, port, heartBeatPeriod);
        return instance;
    }


}

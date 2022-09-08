package com.ulalalab.snipe.common.instance;

public class GlobalInstance {
    //private static Selector selector = null;
    //private static Map<Client, SelectionKey> clientMap = null;
    //private static Thread serverThread = null;
    public static boolean eventServerFlag = false;

    /*
    public static Selector getSelector() throws IOException {
        if(selector==null) {
            selector = Selector.open();
        }
//        else if(selector.isOpen()) {
//            selector.close();
//            selector = Selector.open();
//        }
        return selector;
    }

    public static Map<Client, SelectionKey> getClientMap() {
        if(clientMap==null) {
            clientMap = new ConcurrentHashMap<>();
        }
        return clientMap;
    }

    public static Thread getServerThread() {
        if(serverThread==null) {
            serverThread = new Thread();
        }
        return serverThread;
    }
    */
}
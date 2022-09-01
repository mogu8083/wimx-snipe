package com.ulalalab.api.instance;

import com.ulalalab.api.server.Client;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalInstance {
    private static Selector selector = null;
    private static Map<Client, SelectionKey> clientMap = null;
    private static Thread serverThread = null;

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
}
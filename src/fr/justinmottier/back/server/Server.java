package fr.justinmottier.back.server;

import fr.justinmottier.back.JSONDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the server, which will wait for new connections
 */
public class Server implements Runnable{
    private ServerSocket socket;
    private boolean running = false;
    private final JSONDB db = new JSONDB("data.json");
    /**
     * The Sockets.
     */
    Map<SocketConnection, Thread> sockets;

    /**
     * Create a server instance
     *
     * @param port the port the server will listen on
     */
    public Server(int port) {
        this.sockets = new HashMap<>();
        try {
            this.socket = new ServerSocket(port);
            this.socket.setSoTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The thread implementation function which will start the listening process
     */
    public void run() {
        this.start();
        this.loop();
        this.stop();
    }

    /**
     * init the values to the server start
     */
    public void start() {
        this.running = true;
    }

    /**
     * loop while the server should be running
     */
    public void loop() {
        while (this.running) {
            this.waitForClient();
        }
    }

    /**
     * Stop the server and close every opened sockets
     */
    public void stop() {
        for (Map.Entry<SocketConnection, Thread> entry: this.sockets.entrySet()) {
            entry.getKey().stopSocket();
            try {
                entry.getValue().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Wait for a client connection
     *
     * @return true if a client connected, false, if timed out
     */
    public boolean waitForClient() {
        try {
            SocketConnection s = new SocketConnection(this.socket.accept(), this.db);
            Thread sThread = new Thread(s);
            this.sockets.put(s, sThread);
            sThread.start();
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    /**
     * init the values to stop the server
     */
    public void stopServer() {
        this.running = false;
    }
}

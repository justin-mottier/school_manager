package fr.justinmottier.back.server;

import com.google.gson.Gson;
import fr.justinmottier.back.JSONDB;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A socket connection between the server and a client
 */
public class SocketConnection implements Runnable {
    private final String uuid = RandomStringUtils.random(4, "ABCDEF0123456789");
    private final Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean running = false;
    private static final Gson gson = new Gson();
    private final JSONDB db;

    /**
     * Creates a SocketConnection instance
     *
     * @param socket the socket representing the connection
     * @param db     an instance of the db used to answer the requests
     */
    public SocketConnection(Socket socket, JSONDB db) {
        System.out.println("New socket opened with ID " + this.uuid);
        this.socket = socket;
        try {
            this.socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            this.socketWriter = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.db = db;
    }

    /**
     * The thread implementation function which will wait for a client request
     */
    public void run() {
        this.running = true;
        ServerRequest request;
        do {
            request = this.waitInput();
            System.out.println(this.uuid + ": " + request);
            this.processRequest(request);
        } while (this.running && request != null);
        System.out.println(this.uuid + ": Connection ended");
    }

    /**
     * Stop the socket in response to a client request
     */
    public void stopSocket() {
        this.socketWriter.println(gson.toJson(Map.of("message", "ACKNOWLEDGE")));
        this.running = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait for a request from the client
     * @return the deserialized request
     */
    private ServerRequest waitInput() {
        if (this.socket.isClosed()) {
            return null;
        }
        String payload;

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            payload = this.socketReader.readLine();
        } catch (IOException ignored) {
            payload = null;
        }

        if (payload == null) {
            return null;
        }

        return gson.fromJson(payload, ServerRequest.class);
    }

    /**
     * Handle the reception of an import file
     * @param data the datas received from the client
     */
    private void handleImport(List<String> data) {
        if (data.size() <= 3) {
            this.socketWriter.println(gson.toJson(Map.of("message", "ERROR")));
        }
        Iterator<String> it = data.iterator();
        String className = it.next();
        if (className.length() <= 2) {
            className = String.join("e", className.split(""));
        }
        String subject = it.next();
        while (it.hasNext()) {
            String[] line = it.next().split(" ");
            this.db.addPupilGrade(subject, line[1], line[0], Double.parseDouble(line[2]));
        }
        this.socketWriter.println(gson.toJson(Map.of("message", "ACKNOWLEDGE")));
    }

    /**
     * Process the request of the client
     * @param request the deserialized request
     */
    private void processRequest(ServerRequest request) {
        if (request == null || request.message == null || this.socket.isClosed()) {
            return;
        }

        switch (request.message) {
            case "GET_GRADES" -> this.socketWriter.println(gson.toJson(this.db.getClassGrades()));
            case "GET_STATS" -> this.socketWriter.println(gson.toJson(this.db.getClassStats()));
            case "GET_PUPILS" -> this.socketWriter.println(gson.toJson(this.db.getAllPupils()));
            case "SEND_IMPORT" -> {
                if (request.data != null) {
                    this.handleImport(request.data);
                } else {
                    this.socketWriter.println(gson.toJson(Map.of("message", "NO_DATA")));
                }
            }
            case "CLOSE_CONN" -> this.stopSocket();
        }
    }
}

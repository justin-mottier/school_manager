package fr.justinmottier.front;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.justinmottier.front.model.PupilModel;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A client connection to the server
 */
public class Client {
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private static final Gson gson = new Gson();

    /**
     * Create a Client instance and connect to the server
     *
     * @param host the hostname of the server
     * @param port the port number of the server
     */
    public Client(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.socketReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            this.socketWriter = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true
            );
        } catch (IOException e) {
            System.err.println("Error: The server is unreachable");
            System.exit(1);
        }
    }

    /**
     * Generate a payload ready to be sent
     *
     * @param requestName the name of the request to send
     * @return the serialized payload
     */
    public String preparePayload(String requestName) {
        return gson.toJson(Map.of("message", requestName));
    }

    /**
     * Prepare payload string.
     *
     * @param requestName the request name
     * @param data        the data
     * @return the string
     */
    public String preparePayload(String requestName, List<String> data) {
        return gson.toJson(Map.of("message", requestName, "data", data));
    }


    /**
     * Request and get response string.
     *
     * @param request the request
     * @return the string
     */
    public String requestAndGetResponse(String request) {
        return this.requestAndGetResponse(request, null);
    }

    /**
     * Send a request and wait for the server response
     *
     * @param request the name of the request to send
     * @param data    the data
     * @return the server response
     */
    public String requestAndGetResponse(String request, List<String> data) {
        if (this.socket.isClosed()) {
            return null;
        }
        if (data == null) {
            this.socketWriter.println(this.preparePayload(request));
        } else {
            this.socketWriter.println(this.preparePayload(request, data));
        }
        try {
            return this.socketReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Fetch the grades from the server
     *
     * @return a map of grades
     */
    public Map<String, Map<String, List<List<Double>>>> fetchGrades() {
        String rawResponse = this.requestAndGetResponse("GET_GRADES");
        if (rawResponse == null) {
            return new HashMap<>();
        }
        Type empMapType = new TypeToken<HashMap<String, HashMap<String, ArrayList<ArrayList<Double>>>>>() {}.getType();
        return gson.fromJson(rawResponse, empMapType);
    }

    /**
     * Fetch the stats from the server
     *
     * @return a map of stats
     */
    public Map<String, Map<String, List<Map<String, Double>>>> fetchStats() {
        String rawResponse = this.requestAndGetResponse("GET_STATS");
        if (rawResponse == null) {
            return new HashMap<>();
        }
        Type empMapType = new TypeToken<HashMap<String, HashMap<String, ArrayList<Map<String, Double>>>>>() {}.getType();
        return gson.fromJson(rawResponse, empMapType);
    }

    /**
     * Fetch all the pupils from the server
     *
     * @return the list of pupils
     */
    public List<PupilModel> fetchPupils() {
        String rawResponse = this.requestAndGetResponse("GET_PUPILS");
        if (rawResponse == null) {
            return new ArrayList<>();
        }
        Type empMapType = new TypeToken<ArrayList<PupilModel>>() {}.getType();
        return gson.fromJson(rawResponse, empMapType);
    }

    /**
     * Send import boolean.
     *
     * @param content the content
     * @return the boolean
     */
    public boolean sendImport(List<String> content) {
        String rawResponse = this.requestAndGetResponse("SEND_IMPORT", content);
        if (rawResponse == null) {
            return false;
        }

        Type empMapType = new TypeToken<HashMap<String, String>>() {}.getType();
        Map<String, String> result = gson.fromJson(rawResponse, empMapType);
        if (!result.containsKey("message") || !result.get("message").equals("ACKNOWLEDGE")) {
            return false;
        }

        return true;
    }

    /**
     * Close the connection to the server
     */
    public void closeConnection() {
        this.requestAndGetResponse("CLOSE_CONN");
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package fr.justinmottier.back.server;

import java.util.List;

/**
 * A representation of a request from a client
 */
public class ServerRequest {
    /**
     * The Message.
     */
    public String message;
    /**
     * The Data.
     */
    public List<String> data;

    public String toString() {
        String s =  "message: " + message;
        if (this.data == null) {
            return s;
        }
        return s + ", data: " + data.toString();
    }
}

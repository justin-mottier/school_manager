package fr.justinmottier.back;

import fr.justinmottier.back.server.Server;

/**
 * The main class of the backend side
 */
public class BackMain {
    private static final String DB_NAME = "data.json";
    private static final int SOCKET_PORT = 14600;

    /**
     * Generate a database and write it in the json file
     *
     * @see SchoolGenerator
     */
    public static void generateDB() {
        SchoolGenerator generator = new SchoolGenerator(DB_NAME);
        generator.generate();
    }

    /**
     * start the server
     *
     * @see Server
     */
    public static void runServer() {
        Server server = new Server(SOCKET_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stopServer();
                try {
                    serverThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args launch arguments
     */
    public static void main(String[] args) {
        generateDB();
        runServer();
    }
}

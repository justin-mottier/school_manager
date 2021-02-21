package fr.justinmottier.front;

import fr.justinmottier.front.model.PupilModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * The type Front main.
 */
public class GUI extends Application {
    private static final String SOCKET_HOST;
    static {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
        }
        SOCKET_HOST = ip;
    }

    private static final int SOCKET_PORT = 14600;

    /**
     * The constant client.
     */
    public static final Client client = new Client(SOCKET_HOST, SOCKET_PORT);

    /**
     * The Stats.
     */
    public static Map<String, Map<String, List<Map<String, Double>>>> stats;
    /**
     * The Grades.
     */
    public static Map<String, Map<String, List<List<Double>>>> grades;
    /**
     * The Pupils.
     */
    public static List<PupilModel> pupils;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Fetching the datas from the server...");
        grades = client.fetchGrades();
        stats = client.fetchStats();
        pupils = client.fetchPupils();

        System.out.println("Loading the graphic interface...");
        URL url = new File("src/fr/justinmottier/front/fxml/tabs.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("High School");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        System.out.println("Closing the connection to the server...");
        client.closeConnection();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

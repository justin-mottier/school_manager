package fr.justinmottier.front.controller;

import fr.justinmottier.common.SubjectNameHandler;
import fr.justinmottier.front.FrontMain;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * The type Insert controller.
 */
public class InsertController {

    /**
     * The Button.
     */
    public Button button;
    /**
     * The Text.
     */
    public Text text;
    /**
     * The Text info.
     */
    public Text textInfo;

    /**
     * The actions to do to initialize the view
     */
    public void initialize() {
        StringBuilder textInfoText = new StringBuilder();
        textInfoText.append("Le format de la classe peut être XeY ou XY").append(System.lineSeparator());
        textInfoText.append("Le format de la matière doit correspondre à l'une des valeurs suivantes: ").append(System.lineSeparator());
        SubjectNameHandler.getConvertionMap().forEach((x, y) -> textInfoText.append(x).append(" ou ").append(y).append(System.lineSeparator()));
        textInfo.setText(textInfoText.toString());
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import an interro file");
            File f = fileChooser.showOpenDialog(button.getScene().getWindow());
            handleFile(f);
        });
    }

    /**
     * Read the import file and send it to the server
     *
     * @param file the file
     */
    public void handleFile(File file) {
        List<String> fileContent;
        try {
            fileContent = Files.readAllLines(file.toPath());
        } catch (IOException ignored) {
            text.setText("Erreur durant la lecture du fichier");
            return;
        }
        boolean success = FrontMain.client.sendImport(fileContent);
        if (!success) {
            text.setText("Le serveur n'a pas réussi à importer le fichier");
        }

        text.setText("Fichier importé avec succés");
    }
}

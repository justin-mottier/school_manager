package fr.justinmottier.front.controller;

import fr.justinmottier.front.GUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Stats controller.
 */
public class StatsController {
    private Map<String, Map<String, List<Map<String, Double>>>> stats;

    /**
     * The Table view.
     */
    public TableView<ObservableList<StringProperty>> tableView;

    /**
     * The actions to do to initialize the view
     */
    public void initialize() {
        this.stats = GUI.stats;

        for (Map.Entry<String, Map<String, List<Map<String, Double>>>> classEntry: stats.entrySet()) {
            for (Map.Entry<String, List<Map<String, Double>>> statsEntry: classEntry.getValue().entrySet()) {
                statsEntry.getValue().forEach(x -> {
                    ObservableList<StringProperty> row = FXCollections.observableArrayList();
                    row.add(new SimpleStringProperty(classEntry.getKey()));
                    row.add(new SimpleStringProperty(statsEntry.getKey()));
                    row.add(new SimpleStringProperty(String.valueOf(statsEntry.getValue().indexOf(x))));
                    row.add(new SimpleStringProperty(String.valueOf(x.get("min"))));
                    row.add(new SimpleStringProperty(String.valueOf(x.get("max"))));
                    row.add(new SimpleStringProperty(String.valueOf(x.get("average"))));
                    row.add(new SimpleStringProperty(String.valueOf(x.get("median"))));
                    row.add(new SimpleStringProperty(String.valueOf(x.get("standardDeviation"))));
                    tableView.getItems().add(row);
                });
            }
        }

        TableColumn<ObservableList<StringProperty>, String> classColumn = new TableColumn<>("Classe");
        classColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().get(0).getValue()));
        TableColumn<ObservableList<StringProperty>, String> subjectColumn = new TableColumn<>("Matiére");
        subjectColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().get(1).getValue()));
        TableColumn<ObservableList<StringProperty>, String> interroColumn = new TableColumn<>("Examen");
        interroColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().get(2).getValue()));
        tableView.getColumns().addAll(classColumn, subjectColumn, interroColumn);
        List<String> keys = new ArrayList<>(this.stats.entrySet().iterator().next().getValue().entrySet().iterator().next().getValue().iterator().next().keySet());
        for(String key: keys) {
            TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>(key);
            column.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().get(keys.indexOf(key) + 3).getValue()));
            tableView.getColumns().add(column);
        }
    }
}

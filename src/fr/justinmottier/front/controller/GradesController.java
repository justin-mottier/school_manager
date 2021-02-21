package fr.justinmottier.front.controller;

import fr.justinmottier.front.FrontMain;
import fr.justinmottier.common.SubjectNameHandler;
import fr.justinmottier.front.model.PupilModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The type Grades controller.
 */
public class GradesController {

    /**
     * The Table view.
     */
    public TableView<ObservableList<StringProperty>> tableView;

    /**
     * The actions to do to initialize the view
     */
    public void initialize() {
        List<PupilModel> pupils = FrontMain.pupils;
        Set<String> columns = new TreeSet<>();

        pupils.forEach(x -> columns.addAll(x.grades.keySet()));

        pupils.forEach(x -> {
            ObservableList<StringProperty> row = FXCollections.observableArrayList();
            row.add(new SimpleStringProperty(x.className));
            row.add(new SimpleStringProperty(x.firstName));
            row.add(new SimpleStringProperty(x.lastName));
            columns.forEach(y -> {
                if (x.grades.containsKey(y)) {
                    row.add(new SimpleStringProperty(x.grades.get(y).stream().map(String::valueOf).reduce("", (a, b) -> a + ", " + b)));
                } else {
                    row.add(new SimpleStringProperty("-"));
                }
            });
            tableView.getItems().add(row);
        });

        TableColumn<ObservableList<StringProperty>, String> classNameColumn = new TableColumn<>("Classe");
        classNameColumn.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().get(0).get()));
        TableColumn<ObservableList<StringProperty>, String> firstNameColumn = new TableColumn<>("Prénom");
        firstNameColumn.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().get(1).get()));
        TableColumn<ObservableList<StringProperty>, String> lastNameColumn = new TableColumn<>("Nom");
        lastNameColumn.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().get(2).get()));
        tableView.getColumns().addAll(classNameColumn, firstNameColumn, lastNameColumn);
        List<String> columnsList = new ArrayList<>(columns);
        for (String column: columns) {
            TableColumn<ObservableList<StringProperty>, String> c = new TableColumn<>(SubjectNameHandler.getNameFromId(column));
            c.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().get(columnsList.indexOf(column) + 3).getValue()));
            tableView.getColumns().add(c);
        }
    }
}

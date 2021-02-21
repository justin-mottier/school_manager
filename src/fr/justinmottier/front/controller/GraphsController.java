package fr.justinmottier.front.controller;

import fr.justinmottier.front.FrontMain;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The controller for the FXML
 */
public class GraphsController {
    private Map<String, Map<String, List<Map<String, Double>>>> stats = new HashMap<>();
    private Map<String, Map<String, List<List<Double>>>> grades = new HashMap<>();
    private String currentSubject;
    private int currentInterro;

    /**
     * The Subject selector.
     */
    @FXML
    ComboBox<String> subjectSelector;
    /**
     * The Interro selector.
     */
    @FXML
    ComboBox<Integer> interroSelector;
    /**
     * The Grades bar chart x axis.
     */
    @FXML
    CategoryAxis gradesBarChartXAxis;
    /**
     * The Grades bar chart y axis.
     */
    @FXML
    NumberAxis gradesBarChartYAxis;
    /**
     * The Grades bar chart.
     */
    @FXML
    BarChart<String, Number> gradesBarChart;
    /**
     * The Grades line chart x axis.
     */
    @FXML
    NumberAxis gradesLineChartXAxis;
    /**
     * The Grades line chart y axis.
     */
    @FXML
    NumberAxis gradesLineChartYAxis;
    /**
     * The Grades line chart.
     */
    @FXML
    LineChart<Number, Number> gradesLineChart;
    /**
     * The Averages line chart.
     */
    @FXML
    LineChart<Number, Number> averagesLineChart;
    /**
     * The Averages line chart x axis.
     */
    @FXML
    NumberAxis averagesLineChartXAxis;
    /**
     * The Averages line chart y axis.
     */
    @FXML
    NumberAxis averagesLineChartYAxis;
    /**
     * The Overall averages line chart.
     */
    @FXML
    LineChart<Number, Number> overallAveragesLineChart;
    /**
     * The Overall averages line chart x axis.
     */
    @FXML
    NumberAxis overallAveragesLineChartXAxis;
    /**
     * The Overall averages line chart y axis.
     */
    @FXML
    NumberAxis overallAveragesLineChartYAxis;

    /**
     * Sets stats.
     *
     * @param stats the stats
     */
    public void setStats(Map<String, Map<String, List<Map<String, Double>>>> stats) {
        this.stats = stats;
    }

    /**
     * Sets grades.
     *
     * @param grades the grades
     */
    public void setGrades(Map<String, Map<String, List<List<Double>>>> grades) {
        this.grades = grades;
    }

    /**
     * The actions to do to initialize the view
     */
    public void initialize() {
        gradesBarChartYAxis.setLabel("Moyenne");
        gradesBarChartYAxis.setTickLabelRotation(90);
        gradesBarChartYAxis.setAutoRanging(false);
        gradesBarChartYAxis.setLowerBound(0);
        gradesBarChartYAxis.setUpperBound(20);
        gradesBarChartYAxis.setTickUnit(1);
        gradesBarChartXAxis.setLabel("Classe");
        gradesBarChart.setTitle("Moyenne des notes de chaque classe pour une épreuve");
        gradesBarChart.setLegendVisible(false);

        gradesLineChartYAxis.setLabel("Densité");
        gradesLineChartYAxis.setTickLabelRotation(90);
        gradesLineChartXAxis.setLabel("Note");
        gradesLineChartXAxis.setAutoRanging(false);
        gradesLineChartXAxis.setLowerBound(0);
        gradesLineChartXAxis.setUpperBound(20);
        gradesLineChart.setTitle("Répartition des notes de chaque classe pour une épreuve");
        gradesLineChart.setCreateSymbols(false);

        averagesLineChartYAxis.setLabel("Densité");
        averagesLineChartYAxis.setTickLabelRotation(90);
        averagesLineChartXAxis.setLabel("Note");
        averagesLineChartXAxis.setAutoRanging(false);
        averagesLineChartXAxis.setLowerBound(0);
        averagesLineChartXAxis.setUpperBound(20);
        averagesLineChart.setTitle("Répartition des moyennes de chaque classe pour une matière");
        averagesLineChart.setCreateSymbols(false);

        overallAveragesLineChartYAxis.setLabel("Densité");
        overallAveragesLineChartYAxis.setTickLabelRotation(90);
        overallAveragesLineChartXAxis.setLabel("Note");
        overallAveragesLineChartXAxis.setAutoRanging(false);
        overallAveragesLineChartXAxis.setLowerBound(0);
        overallAveragesLineChartXAxis.setUpperBound(20);
        overallAveragesLineChart.setTitle("Répartition des moyennes générale de chaque classe");
        overallAveragesLineChart.setCreateSymbols(false);

        this.currentSubject = this.subjectSelector.getSelectionModel().getSelectedItem();
        this.currentInterro = 0;

        this.subjectSelector.getSelectionModel().selectedItemProperty().addListener((selected, oldSubject, newSubject) -> {
            this.currentSubject = newSubject;
            this.currentInterro = 0;
            this.updateInterroSelector();
            this.setGradesBarChart();
            this.setGradesLineChart();
            this.setAveragesLineChart();
        });

        this.interroSelector.getSelectionModel().selectedItemProperty().addListener((selected, oldIndex, newIndex) -> {
            if (newIndex == null) {
                return;
            }
            this.currentInterro = newIndex;
            this.setGradesBarChart();
            this.setGradesLineChart();
        });
        this.grades = FrontMain.grades;
        this.stats = FrontMain.stats;
        this.subjectSelector.getItems().addAll(this.stats.entrySet().iterator().next().getValue().keySet());
        this.updateInterroSelector();

        this.setGradesBarChart();
        this.setGradesLineChart();
        this.setAveragesLineChart();
        this.setOverallAveragesLineChart();
    }

    /**
     * The initialization which have to be done after the set of grades and stats
     */
    public void initializeAfterSet() {
    }

    /**
     * Update the interro selector regarding the current subject selected
     */
    public void updateInterroSelector() {
        this.interroSelector.getItems().clear();
        for( int i = 0; i < this.stats.entrySet().iterator().next().getValue().get(this.currentSubject).size(); i++) {
            this.interroSelector.getItems().add(i);
        }
        this.interroSelector.getSelectionModel().select(0);
    }

    /**
     * Set the grades bar chart
     */
    public void setGradesBarChart() {
        XYChart.Series<String, Number> series1 = new XYChart.Series();
        SortedSet<String> keys = new TreeSet<>(this.stats.keySet());
        for (String key : keys) {
            if (!this.stats.get(key).containsKey(this.currentSubject)) {
                continue;
            }
            series1.getData().add(new XYChart.Data<>(key, this.stats.get(key).get(this.currentSubject).get(this.currentInterro).get("average")));
        }
        gradesBarChart.getData().clear();
        gradesBarChart.getData().add(series1);
    }

    /**
     * Set the grades line chart
     */
    public void setGradesLineChart() {
        gradesLineChart.getData().clear();
        SortedSet<String> keys = new TreeSet<>(this.grades.keySet());
        for (String key : keys) {
            Map<Double, Double> m = new HashMap<>();
            for(Map.Entry<String, List<List<Double>>> entry: this.grades.get(key).entrySet()) {
                if (!entry.getKey().equals(this.currentSubject)) {
                    continue;
                }
                double average = this.stats.get(key).get(this.currentSubject).get(this.currentInterro).get("average");
                double standardDeviation = this.stats.get(key).get(this.currentSubject).get(this.currentInterro).get("standardDeviation");
                NormalDistribution d = new NormalDistribution();
                try {
                    d = new NormalDistribution(average, standardDeviation);
                } catch (Exception e) {
                    System.out.println(average);
                    System.out.println(standardDeviation);
                }
                for (double i = 0; i < 20; i += 0.1) {
                    m.put(i, d.density(i));
                }
            }
            XYChart.Series<Number, Number> series = new XYChart.Series();
            m.forEach((x, y) -> series.getData().add(new XYChart.Data<>(x, y)));
            series.setName(key);
            gradesLineChart.getData().add(series);
        }
    }

    /**
     * Set the average line chart
     */
    public void setAveragesLineChart() {
        averagesLineChart.getData().clear();
        SortedSet<String> keys = new TreeSet<>(this.grades.keySet());
        for (String key : keys) {
            Map<Double, Double> m = new HashMap<>();
            for(Map.Entry<String, List<List<Double>>> entry: this.grades.get(key).entrySet()) {
                if (!entry.getKey().equals(this.currentSubject)) {
                    continue;
                }
                double average = 0;
                double standardDeviation = 0;
                for (Map<String, Double> s: this.stats.get(key).get(this.currentSubject)) {
                    average += s.get("average");
                    standardDeviation += s.get("standardDeviation");
                }
                average /= this.stats.get(key).get(this.currentSubject).size();
                standardDeviation /= this.stats.get(key).get(this.currentSubject).size();

                NormalDistribution d = new NormalDistribution(average, standardDeviation);
                for (double i = 0; i < 20; i += 0.1) {
                    m.put(i, d.density(i));
                }
            }
            XYChart.Series<Number, Number> series = new XYChart.Series();
            m.forEach((x, y) -> series.getData().add(new XYChart.Data<>(x, y)));
            series.setName(key);
            averagesLineChart.getData().add(series);
        }
    }

    /**
     * Set the overall averages line chart
     */
    public void setOverallAveragesLineChart() {
        overallAveragesLineChart.getData().clear();
        SortedSet<String> keys = new TreeSet<>(this.grades.keySet());
        for (String key : keys) {
            Map<Double, Double> m = new HashMap<>();
            for(Map.Entry<String, List<List<Double>>> entry: this.grades.get(key).entrySet()) {
                List<Double> overallAveragePerSubject = new ArrayList<>();
                for (Map.Entry<String, List<List<Double>>> classGradesEntry : this.grades.get(key).entrySet()) {
                    overallAveragePerSubject.add(
                            classGradesEntry.getValue()
                                    .stream().map(x -> x.stream().mapToDouble(Double::valueOf).average()).collect(Collectors.toList())
                                    .stream().mapToDouble(OptionalDouble::getAsDouble).average().getAsDouble()
                    );
                }
                double average = overallAveragePerSubject.stream().mapToDouble(Double::valueOf).average().getAsDouble();

                double standardDeviation = 0;
                double totalNGradesNumber = 0;

                for (Map.Entry<String, List<List<Double>>> classGradesEntry : this.grades.get(key).entrySet()) {
                    for (List<Double> gradesPerInterro : classGradesEntry.getValue()) {
                        standardDeviation += gradesPerInterro
                                .stream().map(x -> Math.abs(average - x)).collect(Collectors.toList())
                                .stream().mapToDouble(Double::valueOf).sum();
                        totalNGradesNumber += gradesPerInterro.size();
                    }
                }
                if (totalNGradesNumber != 0) {
                    standardDeviation /= totalNGradesNumber;
                } else {
                    standardDeviation = 0;
                }

                NormalDistribution d = new NormalDistribution(average, standardDeviation);
                for (double i = 0; i < 20; i += 0.1) {
                    m.put(i, d.density(i));
                }
            }
            XYChart.Series<Number, Number> series = new XYChart.Series();
            m.forEach((x, y) -> series.getData().add(new XYChart.Data<>(x, y)));
            series.setName(key);
            overallAveragesLineChart.getData().add(series);
        }
    }

}

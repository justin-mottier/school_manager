package fr.justinmottier.front.model;

import java.util.List;
import java.util.Map;

/**
 * The type Pupil model.
 */
public class PupilModel {
    /**
     * The Class name.
     */
    public String className;
    /**
     * The First name.
     */
    public String firstName;
    /**
     * The Last name.
     */
    public String lastName;
    /**
     * The Grades.
     */
    public Map<String, List<Double>> grades;
    /**
     * The Overall grades.
     */
    public Map<String, Double> overallGrades;
}

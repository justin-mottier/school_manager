package fr.justinmottier.back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A pupil with names and grades
 */
public class Pupil {
    private final String firstName;
    private final String lastName;
    private final HashMap<String, ArrayList<Double>> grades;
    private final HashMap<String, Double> overallGrades;

    private String className;

    private final transient List<String> optionalSubjects;

    /**
     * Creates a Pupil based on a first name and a last name
     *
     * @param firstName a firstname
     * @param lastName  a lastname
     */
    public Pupil(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grades = new HashMap<>();
        this.overallGrades = new HashMap<>();
        this.optionalSubjects = new ArrayList<>();
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets grades.
     *
     * @return the grades
     */
    public Map<String, ArrayList<Double>> getGrades() {
        return grades;
    }

    /**
     * Gets overall grades.
     *
     * @return the overall grades
     */
    public Map<String, Double> getOverallGrades() {
        return overallGrades;
    }

    /**
     * Sets class name.
     *
     * @param className the class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Add a grade to a pupil
     *
     * @param subjectId the subject
     * @param grade     the grade
     */
    public void addGrade(String subjectId, double grade) {
        this.addGrade(subjectId, grade, false);
    }

    /**
     * Add a grade to a pupil
     *
     * @param subjectId the subject
     * @param grade     the grade
     * @param optional  if the subject is optional
     */
    public void addGrade(String subjectId, double grade, boolean optional) {
        if (optional && !this.optionalSubjects.contains(subjectId)) {
            this.optionalSubjects.add(subjectId);
        }

        if (!this.grades.containsKey(subjectId)) {
            this.grades.put(subjectId, new ArrayList<>());
        }
        this.grades.get(subjectId).add(grade);
    }

    /**
     * Calculate the overall average for each subject
     */
    public void calculateOverallAverage() {
        this.overallGrades.clear();
        this.grades.forEach((x, y) -> this.overallGrades.put(
                x,
                Math.floor(y.stream().mapToDouble(Double::valueOf).average().getAsDouble() * 100) / 100
        ));

        double sum = 0;
        for (Map.Entry<String, Double> entry: this.overallGrades.entrySet()) {
            sum += this.optionalSubjects.contains(entry.getKey()) ? 0 : entry.getValue();
        }
        double avg = sum / (this.overallGrades.size() - this.optionalSubjects.size());
        for (String subject: this.optionalSubjects) {
            avg += 0.1 * (this.overallGrades.get(subject) % 10);
        }
        this.overallGrades.put("TOT", avg);
    }

    public String toString() {
        return this.firstName + " " + this.lastName;
    }
}

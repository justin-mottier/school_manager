package fr.justinmottier.back;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The class which will read the database formatted in json.
 */
public class JSONDB {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String dbName;
    private HashMap<String, List<Pupil>> deserializedJSON;

    /**
     * Default construcor for JSONDB
     *
     * @param dbName the name of the json file
     */
    public JSONDB(String dbName) {
        this.dbName = dbName;
        this.deserializedJSON = null;
        this.readJSON();
    }

    /**
     * Read the database file
     */
    public void readJSON() {
        try {
            FileReader reader = new FileReader(this.dbName);
            Type empMapType = new TypeToken<HashMap<String, List<Pupil>>>() {}.getType();
             this.deserializedJSON = gson.fromJson(reader, empMapType);
            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to read the data file");
        }
    }

    /**
     * Print the content of the database
     */
    public void printContent() {
        for (Map.Entry<String, List<Pupil>> entry: this.deserializedJSON.entrySet()) {
            System.out.println(entry.getKey());
            for (Pupil p : entry.getValue()) {
                System.out.println(p);
            }
        }
    }

    /**
     * Return the pupils for a specified class
     *
     * @param className the name of the class
     * @return the list of the pupils
     */
    public List<Pupil> getPupilsFromClassName(String className) {
        return this.deserializedJSON.get(className);
    }

    /**
     * Return all the pupils from the school
     *
     * @return a list of pupil
     */
    public List<Pupil> getAllPupils() {
        List<Pupil> l = new ArrayList<>();
        this.deserializedJSON.forEach((x, y) -> l.addAll(y));
        return l;
    }

    /**
     * Returns a pupil from the school
     *
     * @param firstName the first name of the pupil
     * @param lastName  the last name of the pupil
     * @return the pupil
     */
    public Pupil getPupil(String firstName, String lastName) {
        for (Map.Entry<String, List<Pupil>> entry: this.deserializedJSON.entrySet()) {
            for (Pupil p : entry.getValue()) {
                if (p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Return the pupils for a specified level
     *
     * @param classLevel the level of the class
     * @return a map containing all the pupils for the specified level, indexed by class name
     */
    public Map<String, Map<String, List<List<Double>>>> getGradesFromClassLevel(char classLevel) {
        Map<String, Map<String, List<List<Double>>>> levelGrades = new HashMap<>();
        for (Map.Entry<String, Map<String, List<List<Double>>>> allGradesEntry: this.getClassGrades().entrySet()) {
            if (allGradesEntry.getKey().charAt(0) == classLevel) {
                levelGrades.put(allGradesEntry.getKey(), allGradesEntry.getValue());
            }
        }
        return levelGrades;
    }

    /**
     * Return the grades for all the pupils
     *
     * @return a map indexed by class name containing a map indexed by subject containing a list of interros containing all the grades
     */
    public Map<String, Map<String, List<List<Double>>>> getClassGrades() {
        Map<String, Map<String, List<List<Double>>>> grades = new HashMap<>();
        for (Map.Entry<String, List<Pupil>> JSONEntry: this.deserializedJSON.entrySet()) {
            Map<String, List<List<Double>>> m = new HashMap<>();
            for (Pupil p : JSONEntry.getValue()) {
                for (Map.Entry<String, ArrayList<Double>> entry: p.getGrades().entrySet()) {
                    if (!m.containsKey(entry.getKey())) {
                        List<List<Double>> l = new ArrayList<>();
                        m.put(entry.getKey(), l);
                    }
                    while(m.get(entry.getKey()).size() != entry.getValue().size()) {
                        List<Double> l = new ArrayList<>();
                        m.get(entry.getKey()).add(l);
                    }
                    for(int i = 0; i < entry.getValue().size(); i++) {
                        m.get(entry.getKey()).get(i).add(entry.getValue().get(i));
                    }
                }
            }
            grades.put(JSONEntry.getKey(), m);
        }
        return grades;
    }

    /**
     * Return the stats of all the interros
     *
     * @return Return the statistics for each interro for each subject for each class
     */
    public Map<String, Map<String, List<Statistic>>> getClassStats() {
        Map<String, Map<String, List<Statistic>>> stats = new HashMap<>();
        Map<String, Map<String, List<List<Double>>>> allGrades = this.getClassGrades();
        for (Map.Entry<String, Map<String, List<List<Double>>>> classesGradesEntry: allGrades.entrySet()) {
            stats.put(classesGradesEntry.getKey(), new HashMap<>());
            for (Map.Entry<String, List<List<Double>>> subjectsGradesEntry: classesGradesEntry.getValue().entrySet()) {
                stats.get(classesGradesEntry.getKey()).put(subjectsGradesEntry.getKey(), new ArrayList<>());
                for (List<Double> gradesPerExam: subjectsGradesEntry.getValue()) {
                    Statistic s = new Statistic();
                    s.min = gradesPerExam.stream().mapToDouble(Double::valueOf).min().getAsDouble();
                    s.max = gradesPerExam.stream().mapToDouble(Double::valueOf).max().getAsDouble();
                    s.average = gradesPerExam.stream().mapToDouble(Double::valueOf).average().getAsDouble();
                    gradesPerExam.sort(Comparator.naturalOrder());
                    s.median = gradesPerExam.get(gradesPerExam.size() / 2);
                    s.standardDeviation = gradesPerExam.stream().mapToDouble(Double::valueOf).map(x -> Math.abs(x - s.average)).average().getAsDouble();
                    stats.get(classesGradesEntry.getKey()).get(subjectsGradesEntry.getKey()).add(s);
                }
            }
        }
        return stats;
    }

    /**
     * Add a grade from a new interro to an existing pupil
     *
     * @param subject   the subject of the new grade
     * @param firstName the pupil's first name
     * @param lastName  the pupil's last name
     * @param grade     the grade
     */
    public void addPupilGrade(String subject, String firstName, String lastName, double grade) {
        Pupil p = this.getPupil(firstName, lastName);

        if (p == null) {
            return;
        }

        p.addGrade(subject, grade);

        this.saveDB();
    }

    /**
     * Save the changes applied to the database
     */
    public void saveDB() {
        try {
            FileWriter writer = new FileWriter(this.dbName);
            gson.toJson(deserializedJSON, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to create the data file");
        }
    }
}

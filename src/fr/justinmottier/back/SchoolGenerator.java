package fr.justinmottier.back;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.javafaker.Faker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generates a fake school with all their students and their grades
 */
public class SchoolGenerator {
    private final String dbName;
    private HighSchool school = null;
    /**
     * The Pupils.
     */
    Map<String, ArrayList<Pupil>> pupils;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Generate a SchoolGenerator instance
     *
     * @param dbName the name of the file to write the datas into
     */
    public SchoolGenerator(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Populate the pupils Map and save it into the db file
     */
    public void generate() {
        try {
            this.school = gson.fromJson(new FileReader("config.json"), HighSchool.class);
            this.pupils = this.fillSchool();
            this.savePupils();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find the configuration file");
        }
    }

    /**
     * Create a pupil with a random name
     *
     * @return a randomly generated Pupil
     */
    public static Pupil generatePupil() {
        Faker faker = new Faker();
        return new Pupil(faker.name().firstName(), faker.name().lastName());
    }

    /**
     * Generate a grade based either or a normal law (12, 4) or an equally likely law
     * P(0) = P(1) = P(2) = ...
     *
     * @param useFaker to use an equally likely law
     * @return a randomly generated grade
     */
    public static double generateGrade(boolean useFaker) {
        if (useFaker) {
            Faker faker = new Faker();
            return faker.number().randomDouble(2, 0, 20);
        }
        Random r = new Random();
        double result = r.nextGaussian() * 5 + 10;
        if (result > 20) {
            result = 20;
        } else if (result < 0) {
            result = 0;
        }
        return Math.round(result * 2) / 2.0;
    }

    /**
     * Generate a grade based on a normal law (12, 4)
     *
     * @return a randomly generated grade
     */
    public static double generateGrade() {
        return generateGrade(false);
    }

    /**
     * Creates a correctly formatted class name
     *
     * @param classLevel the level of the class (6, 5, 4, 3)
     * @param letter     the letter of the class (A, B, C, ...)
     * @return the formatted class name
     */
    public static String className(int classLevel, int letter) {
        return String.valueOf(classLevel) + "e" + (char) letter;
    }

    /**
     * Write all the pupils datas in the db file
     */
    public void savePupils() {
        try {
            FileWriter writer = new FileWriter(this.dbName);
            gson.toJson(pupils, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to create the data file");
        }
    }

    /**
     * Generate all the pupils randomly
     *
     * @return a map indexed by class name containing an array of Pupil
     */
    public Map<String, ArrayList<Pupil>> fillSchool() {
        HashMap<String, ArrayList<Pupil>> pupilsG = new HashMap<>();
        for (int level = 6; level >= 3; level--) {
            for (int letter = 65; letter < 65 + school.getClassesPerLevel(); letter++) {
                String className = className(level, letter);
                if (!pupilsG.containsKey(className)) {
                    pupilsG.put(className, new ArrayList<>());
                }
                for (int nPupil = 0; nPupil < school.getPupilPerClass(); nPupil++) {
                    Pupil p = generatePupil();
                    p.setClassName(className);
                    for (Map.Entry<String, Subject> entry: school.getObligatorySubjects().entrySet()) {
                        Subject s = entry.getValue();
                        if (s.getStartsAt() < level) {
                            continue;
                        }
                        for (int nGrades = 0; nGrades < s.getGrades(); nGrades++) {
                            p.addGrade(entry.getKey(), generateGrade());
                        }
                    }
                    // Optional courses
                    boolean alreadyOnlyOne = false;
                    Map<String, Subject> optionalSubjects = school.getOptionalSubjects();
                    ArrayList<String> optionalSubjectsId = new ArrayList<>(optionalSubjects.keySet());
                    int i = 0;
                    while (i < (int) (Math.random() * 3)) {
                        String subjectId = optionalSubjectsId.get((int) (Math.random() * optionalSubjectsId.size()));
                        if (optionalSubjects.get(subjectId).getOptional() && alreadyOnlyOne) {
                            continue;
                        }
                        Subject s = optionalSubjects.get(subjectId);
                        for (int nGrades = 0; nGrades < s.getGrades(); nGrades++) {
                            p.addGrade(subjectId, generateGrade(), true);
                        }
                        if (optionalSubjects.get(subjectId).getOptional()) {
                            alreadyOnlyOne = true;
                        }
                        optionalSubjectsId.remove(subjectId);
                    }

                    p.calculateOverallAverage();
                    pupilsG.get(className).add(p);
                }
            }
        }
        return pupilsG;
    }
}

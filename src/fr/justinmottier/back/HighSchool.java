package fr.justinmottier.back;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a school, used to map the config.json to a Java class
 */
public class HighSchool {
    @SerializedName("classes_per_level")
    private int classesPerLevel;

    @SerializedName("pupil_per_class")
    private int pupilPerClass;

    private Map<String, Subject> subjects;

    /**
     * Gets classes per level.
     *
     * @return the classes per level
     */
    public int getClassesPerLevel() {
        return this.classesPerLevel;
    }

    /**
     * Gets pupil per class.
     *
     * @return the pupil per class
     */
    public int getPupilPerClass() {
        return this.pupilPerClass;
    }

    /**
     * Get the subjects which are obligatory for any student
     *
     * @return a map containing Subject instances indexed by subject id
     */
    public Map<String, Subject> getObligatorySubjects() {
        return this.subjects.entrySet().stream()
                .filter(x -> !x.getValue().getOptional())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the subjects which are optional
     *
     * @return a map containing Subject instances indexed by subject id
     */
    public Map<String, Subject> getOptionalSubjects() {
        return this.subjects.entrySet().stream()
                .filter(x -> x.getValue().getOptional())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

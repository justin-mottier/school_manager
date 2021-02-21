package fr.justinmottier.back;

import com.google.gson.annotations.SerializedName;

/**
 * A school subject
 */
public class Subject {
    private String name;
    private Boolean optional = false;
    private int grades = 3;

    @SerializedName("starts_at")
    private int startsAt = 6;

    @SerializedName("only_one")
    private Boolean onlyOne = false;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets optional.
     *
     * @return the optional
     */
    public Boolean getOptional() {
        return this.optional;
    }

    /**
     * Gets grades.
     *
     * @return the grades
     */
    public int getGrades() {
        return this.grades;
    }

    /**
     * Gets starts at.
     *
     * @return the starts at
     */
    public int getStartsAt() {
        return this.startsAt;
    }

    /**
     * Gets one.
     *
     * @return the one
     */
    public Boolean getonlyOne() {
        return this.onlyOne;
    }

}

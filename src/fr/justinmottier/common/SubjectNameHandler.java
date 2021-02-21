package fr.justinmottier.common;

import java.util.Map;

/**
 * The type Subject name handler.
 */
public class SubjectNameHandler {
    private static final Map<String, String> convertionMap = Map.ofEntries(
        Map.entry("PHY", "Physique"),
        Map.entry("MAT", "Mathématiques"),
        Map.entry("ART", "Arts"),
        Map.entry("HGE", "Histoire Géographie"),
        Map.entry("EPS", "Sport"),
        Map.entry("MUS", "Musique"),
        Map.entry("AAN", "Anglais avancé"),
        Map.entry("SVT", "Sciences naturelles"),
        Map.entry("FRA", "Français"),
        Map.entry("ANG", "Anglais"),
        Map.entry("LVE", "Langue vivante"),
        Map.entry("GRE", "Grec"),
        Map.entry("LAT", "Latin")
    );

    /**
     * Gets name from id.
     *
     * @param id the id
     * @return the name from id
     */
    public static String getNameFromId(String id) {
        return convertionMap.getOrDefault(id, null);
    }

    /**
     * Gets id from name.
     *
     * @param name the name
     * @return the id from name
     */
    public static String getIdFromName(String name) {
        for (Map.Entry<String, String> entry: convertionMap.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets convertion map.
     *
     * @return the convertion map
     */
    public static Map<String, String> getConvertionMap() {
        return convertionMap;
    }
}

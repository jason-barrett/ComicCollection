package com.example.comiccollection.data.entities;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/*
This enum defines several distinct grades that a given Copy might be given.

The ordering of the enum values defines the allowable grades from worst to best, so the
ordering matters.
 */
public enum Grade {
    Pr("PR"),
    PrFr("PR/FR"),
    Fr("FR"),
    FrG("FR/G"),
    GMinus("G-"),
    G("G"),
    GPlus("G+"),
    GVG("G/VG"),
    VGMinus("VG-"),
    VG("VG"),
    VGPlus("VG+"),
    VGF("VG/F"),
    FMinus("F-"),
    F("F"),
    FPlus("F+"),
    FVF("F/VF"),
    VFMinus("VF-"),
    VF("VF"),
    VFPlus("VF+"),
    VFNM("VF/NM"),
    NMMinus("NM-"),
    NM("NM"),
    NMPlus("NM+"),
    NMMT("NM/MT"),
    MT("MT");

    /*
    String representation of a Grade.
     */
    private final String symbol;

    Grade(String symbol) {
        this.symbol = symbol;
    }

    /*
    To help map a string into a grade, maintain this map of symbols to their Grade enums.
    We may have to preprocess the string.
     */
    private static final Map<String, Grade> stringToGrade = new HashMap<>();
    static {  // Block will run once, when this enum is first loaded.
        for( Grade grade : Grade.values() ) {
            stringToGrade.put(grade.toString(), grade);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return symbol;
    }

    /*
    Returns the Grade associated with the string passed in, or null if the string does
    not match up to a Grade.
     */
    public static Grade fromString(String gradeAsString) {
        gradeAsString = gradeAsString.toUpperCase().trim();
        gradeAsString = abbreviateGrades(gradeAsString);
        return stringToGrade.get(gradeAsString);
    }

    private static String abbreviateGrades(String gradeAsString) {
        String result = gradeAsString.replace("POOR", "PR")
                .replace("FAIR", "FR")
                .replace("VERY GOOD", "VG")
                .replace("GOOD", "G")
                .replace("VERY FINE", "VF")
                .replace("FINE", "F")
                .replace("NEAR MINT", "NM")
                .replace("MINT", "MT");

        return result;
    }
}

package com.spc.marthacombo;

/**
 * Created by shaun on 18/03/16.
 * Enables clarity on passing choices made in fragments back to the main activity,
 * and sometimes onward to other fragments
 */
public enum Choices {
    MATHS,
    SPELLING,
    SPELLING_SETUP,
    SPELLING_TEST,
    SPELLING_REVIEW,
    ERROR;

    // This method a string representation of the ENUM back to the actual ENUM
    public static Choices toChoice(String myEnumString) {
        try {
            return valueOf(myEnumString);
        } catch (Exception ex) {
            // For error cases, default to MULTIPLY
            return Choices.ERROR;
        }
    }

}

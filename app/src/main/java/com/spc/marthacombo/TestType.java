package com.spc.marthacombo;

/**
 * Created by shaun on 01/11/15.
 * MATHS TEST ACTION TYPES
 */
public enum TestType {
    RANDOM,
    SPEED,
    PRACTICE;

    public static TestType toTestType(String myEnumString) {
        try {
            return valueOf(myEnumString);
        } catch (Exception ex) {
            // For error cases, default to RANDOM
            return TestType.RANDOM;
        }
    }
}

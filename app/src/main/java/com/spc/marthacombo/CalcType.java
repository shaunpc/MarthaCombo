package com.spc.marthacombo;

/**
 * Created by shaun on 01/11/15.
 * MATHS TEST - CALCULATION TYPES - so we know which operand to use in the tests
 */
public enum CalcType {
    MULTIPLY,
    ADD,
    SUBTRACT,
    DIVIDE,
    MIXED;

    // This method a string representation of the ENUM back to the actual ENUM
    public static CalcType toCalcType(String myEnumString) {
        try {
            return valueOf(myEnumString);
        } catch (Exception ex) {
            // For error cases, default to MULTIPLY
            return CalcType.MULTIPLY;
        }
    }
}
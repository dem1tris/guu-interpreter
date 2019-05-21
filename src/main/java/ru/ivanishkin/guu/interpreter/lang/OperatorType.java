package ru.ivanishkin.guu.interpreter.lang;

public enum OperatorType {
    SET,
    CALL,
    PRINT;

    public static OperatorType get(String word) {
        switch (word) {
            case "set":
                return SET;
            case "call":
                return CALL;
            case "print":
                return PRINT;
            default:
                throw new IllegalArgumentException("Operator expected, found `" + word + "`");
        }
    }
}

package ru.ivanishkin.guu.interpreter.runtime;

import ru.ivanishkin.guu.interpreter.lang.OperatorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Operator {
    public final OperatorType type;
    private final List<String> parameters;
    public final int lineNo;

    public Operator(OperatorType type, List<String> parameters, int lineNo) {
        this.type = type;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
        this.lineNo = lineNo;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("--- line %4d: %-5s %s", lineNo, type, parameters);
    }
}

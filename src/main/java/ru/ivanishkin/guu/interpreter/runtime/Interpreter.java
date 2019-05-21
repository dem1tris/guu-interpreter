package ru.ivanishkin.guu.interpreter.runtime;

import ru.ivanishkin.guu.interpreter.lang.OperatorType;

import java.util.*;

public class Interpreter {
    private Map<String, Integer> memory = new HashMap<>();
    private Map<String, List<Operator>> definitions;

    public Interpreter(Map<String, List<Operator>> definitions) {
        this.definitions = definitions;
    }

    private void eval(final Operator op) {
        List<String> parameters = op.getParameters();
        switch (op.type) {
            case SET:
                try {
                    // todo: not only int as value
                    memory.put(parameters.get(0), Integer.parseInt(parameters.get(1)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Second argument is not an integer");
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Not enough parameters");
                }
                break;
            case CALL:
                String procName;
                try {
                    procName = parameters.get(0);
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Not enough parameters");
                }
                List<Operator> procedure = definitions.get(procName);
                if (procedure != null) {
                    for (Operator o : procedure) {
                        eval(o);
                    }
                } else {
                    throw new IllegalStateException("Undefined procedure " + procName);
                }
                break;
            case PRINT:
                String varName = parameters.get(0);
                try {
                    Integer value = memory.get(varName);
                    if (value != null) {
                        System.out.println(String.format("%s = %d", varName, value));
                    } else {
                        throw new IllegalStateException("Unbound variable " + varName);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new IndexOutOfBoundsException("Not enough parameters");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator");
        }
    }

    public void run() {
        List<Operator> main = definitions.get("main");
        if (main == null) {
            throw new IllegalStateException("`main` procedure not found");
        }
        eval(new Operator(OperatorType.CALL, Collections.singletonList("main"), 0));
    }
}

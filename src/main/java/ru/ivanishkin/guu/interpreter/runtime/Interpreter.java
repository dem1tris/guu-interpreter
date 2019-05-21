package ru.ivanishkin.guu.interpreter.runtime;

import ru.ivanishkin.guu.interpreter.lang.OperatorType;

import java.util.*;

public class Interpreter {
    private static final String EOF = ">>> end of file reached";

    private Map<String, Integer> memory = new HashMap<>();
    private Map<String, List<Operator>> definitions;
    private Deque<StackEntry> callStack = new ArrayDeque<>();
    private List<Operator> curProcedure;
    private Iterator<Operator> opIterator;

    public Interpreter(Map<String, List<Operator>> definitions) {
        this.definitions = definitions;

        if (!definitions.containsKey("main")) {
            throw new IllegalStateException("`main` procedure not found");
        }
        List<Operator> enter = Collections.singletonList(
                new Operator(OperatorType.CALL, Collections.singletonList("main"), 0)
        );
        opIterator = enter.iterator();
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
                curProcedure = definitions.get(procName);
                if (curProcedure != null) {
                    opIterator = curProcedure.iterator();
                } else {
                    throw new IllegalStateException("Undefined procedure " + procName);
                }
                break;
            case PRINT:
                String varName = parameters.get(0);
                try {
                    Integer value = memory.get(varName);
                    if (value != null) {
                        System.err.println(String.format("%s = %d", varName, value));
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
        while (!isOver()) {
            stepInto();
        }
    }

    public boolean isOver() {
        return !(opIterator.hasNext() || !callStack.isEmpty());
    }

    public void stepInto() {
        if (isOver()) {
            System.err.println(EOF);
            return;
        }
        if (callStack.peek() != null &&
                callStack.peek().operator.type != OperatorType.CALL) {
            opIterator = callStack.pop().iterator;
            System.err.println(String.format("--- %14s", "nop"));
            return;
        }
        if (!opIterator.hasNext()) {
            System.err.println(String.format("--- %14s", "ret"));
            opIterator = callStack.pop().iterator;
            return;
        }
        Operator op = opIterator.next();
        callStack.push(new StackEntry(op, opIterator));

        System.err.println(op);
        eval(op);
    }

    public void stepOver() {
        int curDepth = callStack.size();
        do {
            stepInto();
        }
        while (callStack.size() > curDepth);
    }

    public void printVars() {
        System.err.println(">>> --- variables dump ---");
        for (Map.Entry<String, Integer> v : memory.entrySet()) {
            System.err.println(String.format(">>> %6s = %d", v.getKey(), v.getValue()));
        }
        System.err.println(">>> --- end of dump ---");
    }

    public void printTrace() {
        System.err.println(">>> --- call stack top ---");
        for (StackEntry e : callStack) {
            System.err.println(e.operator);
        }
        System.err.println(">>> --- call stack bottom ---");
    }

    private static class StackEntry {
        final Operator operator;
        final Iterator<Operator> iterator;

        public StackEntry(Operator operator, Iterator<Operator> iterator) {
            this.operator = operator;
            this.iterator = iterator;
        }

        @Override
        public String toString() {
            return "{" + operator.type.toString() + ", " + iterator.hasNext() + "}";
        }
    }
}

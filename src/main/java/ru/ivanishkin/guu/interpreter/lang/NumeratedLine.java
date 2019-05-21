package ru.ivanishkin.guu.interpreter.lang;

public class NumeratedLine {
    public final int no;
    public final String line;

    public NumeratedLine(int no, String line) {
        this.no = no;
        this.line = line;
    }

    public NumeratedLine trim() {
        return new NumeratedLine(no, line.trim());
    }
}

package ru.ivanishkin.guu.interpreter.runtime;

import ru.ivanishkin.guu.interpreter.parser.Parser;

import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter(Parser.parseFrom(new StringReader(
                String.join("\n", "",
                        "sub main",
                        "set a 1",
                        "call foo",
                        "print a",
                        "   ",
                        "   ",
                        "   ",
                        "sub foo",
                        "set a 2")
        )));
        interpreter.run();
    }
}

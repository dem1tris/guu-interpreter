package ru.ivanishkin.guu.interpreter.runtime;

import ru.ivanishkin.guu.interpreter.parser.Parser;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String filePath = "";
        if (args.length == 0) {
            System.err.println("Path to the guu code file can be passed as a single program argument");
            System.err.println("Enter path:");
            try {
                filePath = userInput.readLine();
            } catch (IOException e) {
                throw new RuntimeException("I/O error", e);
            }
        } else if (args.length == 1) {
            filePath = args[0];
        } else {
            System.err.println("Too many args");
        }
        File file = new File(filePath);
        Interpreter interpreter;

        try (FileReader reader = new FileReader(file);) {
            interpreter = new Interpreter(Parser.parseFrom(reader));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return;
        } catch (IOException e) {
            System.err.println("Reading error");
            return;
        }

        System.err.println("File: " + file.getAbsolutePath());
        printHelp();

        String line;
        do {
            try {
                line = userInput.readLine();
            } catch (IOException e) {
                throw new RuntimeException("I/O error", e);
            }
            switch (line.trim()) {
                case "i":
                    interpreter.stepInto();
                    break;
                case "o":
                    interpreter.stepOver();
                    break;
                case "var":
                    interpreter.printVars();
                    break;
                case "trace":
                    interpreter.printTrace();
                    break;
                case "q":
                    System.err.println(">>> Quit");
                    return;
                default:
                    printHelp();
            }
        } while (true);
    }

    private static void printHelp() {
        System.err.println(
                ">>> Available commands:\n" +
                        ">>> i - step into\n" +
                        ">>> o - step over\n" +
                        ">>> var - print vars\n" +
                        ">>> trace - print trace\n" +
                        ">>> q - quit"
        );
    }
}

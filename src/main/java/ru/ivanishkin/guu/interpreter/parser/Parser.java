package ru.ivanishkin.guu.interpreter.parser;


import ru.ivanishkin.guu.interpreter.lang.NumeratedLine;
import ru.ivanishkin.guu.interpreter.lang.OperatorType;
import ru.ivanishkin.guu.interpreter.runtime.Operator;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.ivanishkin.guu.interpreter.Utils.firstMatchedIndex;

public class Parser {
    private final static Pattern DEF_PATTERN = Pattern.compile("^sub [a-zA-Z]\\w*\\b$");

    public static Map<String, List<Operator>> parseFrom(Reader reader) {
        BufferedReader r = new BufferedReader(reader);
        List<String> unnumberedLines = r.lines().collect(Collectors.toList());
        List<NumeratedLine> lines = IntStream.range(1, unnumberedLines.size() + 1)
                .mapToObj(i -> new NumeratedLine(i, unnumberedLines.get(i - 1)))
                .map(NumeratedLine::trim)
                .filter(s -> !s.line.isEmpty())
                .collect(Collectors.toList());

        Map<String, List<Operator>> definitions = new HashMap<>();

        do {
            String procedureDef = lines.get(0).line;
            if (!DEF_PATTERN.matcher(procedureDef).matches()) {
                throw new IllegalStateException("Definition expected");
            }
            String procedureName = procedureDef.split(" ")[1];
            lines = lines.subList(1, lines.size());

            int nextDefIndex = firstMatchedIndex(DEF_PATTERN, lines);
            List<NumeratedLine> procedureLines = lines.subList(0, nextDefIndex);
            lines = lines.subList(nextDefIndex, lines.size());

            List<Operator> code = procedureLines.stream()
                    .map(codeLine -> {
                        String[] words = codeLine.line.split(" ");
                        return new Operator(
                                OperatorType.get(words[0]),
                                new ArrayList<>(Arrays.asList(words).subList(1, words.length)),
                                codeLine.no);
                    })
                    .collect(Collectors.toList());

            if (definitions.put(procedureName, code) != null) {
                throw new IllegalStateException("Procedure " + procedureName + " already defined in upper scope");
            }
        } while (!lines.isEmpty());

        return definitions;
    }


}

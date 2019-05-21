package ru.ivanishkin.guu.interpreter;

import ru.ivanishkin.guu.interpreter.lang.NumeratedLine;

import java.util.List;
import java.util.regex.Pattern;

public abstract class Utils {
    public static int firstMatchedIndex(Pattern pat, List<NumeratedLine> list) {
        for (int i = 0; i < list.size(); i++) {
            if (pat.matcher(list.get(i).line).matches()) {
                return i;
            }
        }
        return list.size();
    }
}

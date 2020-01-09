package sto.evgeny.birthdays.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DateFormat {

    DEFAULT_FORMAT("(\\d{4})-(\\d{2})-(\\d{2})",
            new Parser() {
                @Override
                public String[] parse(Matcher matcher) {
                    return new String[]{matcher.group(1), matcher.group(2) + matcher.group(3)};
                }
            }),
    NO_YEAR_FORMAT("--(\\d{2})-(\\d{2})",
            new Parser() {
                @Override
                public String[] parse(Matcher matcher) {
                    return new String[]{null, matcher.group(1) + matcher.group(2)};
                }
            });

    private Pattern pattern;
    private Parser parser;

    DateFormat(String pattern, Parser parser) {
        this.pattern = Pattern.compile(pattern);
        this.parser = parser;
    }

    public String[] parse(String dateStr) {
        Matcher matcher = pattern.matcher(dateStr);
        if (matcher.matches()) {
            return parser.parse(matcher);
        }
        return null;
    }

    interface Parser {
        String[] parse(Matcher matcher);
    }
}

package sto.evgeny.birthdays.model;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
            }),
    DD_MMM_YYYY_FORMAT("(\\d{2}) (.{3}) (\\d{4}) г\\.",
            new Parser() {
                @Override
                public String[] parse(Matcher matcher) {
                    try {
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                        Date date = monthFormat.parse(matcher.group(2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int m = calendar.get(Calendar.MONTH) + 1;
                        return new String[]{matcher.group(3), new DecimalFormat("00").format(m) + matcher.group(1)};
                    } catch (ParseException e) {
                        System.out.printf("\n[WARN] Invalid month: '%s'", matcher.group(2));
                        return null;
                    }
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

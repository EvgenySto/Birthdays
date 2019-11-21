package sto.evgeny.birthdays.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

public enum DateFormat {

    DEFAULT_FORMAT("yyyy-MM-dd", true),
    NO_YEAR_FORMAT("--MM-dd", false),
    DD_MMM_YYYY_FORMAT("dd MMM yyyy 'г.'", true);

    private SimpleDateFormat format;
    private boolean hasYear;

    DateFormat(String pattern, boolean hasYear) {
        format = new SimpleDateFormat(pattern, Locale.getDefault());
        this.hasYear = hasYear;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public boolean hasYear() {
        return hasYear;
    }
}

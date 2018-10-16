package sto.evgeny.birthdays.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ContactData extends HashMap<String, String> {

    public static final SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("MM-dd", Locale.getDefault());
    public static final SimpleDateFormat MONTH_DAY_YEAR_FORMAT = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    public static final SimpleDateFormat DAY_MONTH_FORMAT = new SimpleDateFormat("dd MMMM", Locale.getDefault());
    public static final SimpleDateFormat DAY_MONTH_YEAR_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat DAY_MONTH_FORMAT_SHORT = new SimpleDateFormat("dd.MM", Locale.getDefault());
    public static final SimpleDateFormat DAY_MONTH_YEAR_FORMAT_SHORT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public enum Key {
        ID("ID"),
        NAME("Name"),
        MONTH_AND_DAY("MonthAndDay"),
        DATE_TO_DISPLAY("DateToDisplay"),
        DATE_TO_DISPLAY_SHORT("DateToDisplayShort"),
        HAS_YEAR("HasYear");

        private String value;
        Key(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }

    public ContactData(String id, String name, Date date, boolean hasYear) {
        put(Key.ID.value(), id);
        put(Key.NAME.value(), name);
        put(Key.MONTH_AND_DAY.value(), MONTH_DAY_FORMAT.format(date));
        put(Key.DATE_TO_DISPLAY.value(), getDisplayDate(date, hasYear, false));
        put(Key.DATE_TO_DISPLAY_SHORT.value(), getDisplayDate(date, hasYear, true));
        put(Key.HAS_YEAR.value(), String.valueOf(hasYear));
    }

    public String getId() {
        return get(Key.ID.value());
    }
    public String getName() {
        return get(Key.NAME.value());
    }
    public String getMonthAndDay() {
        return get(Key.MONTH_AND_DAY.value());
    }
    public String getDisplayDate() {
        return get(Key.DATE_TO_DISPLAY.value());
    }
    public boolean hasYear() {
        return Boolean.parseBoolean(get(Key.HAS_YEAR.value()));
    }

    private static String getDisplayDate(Date date, boolean hasYear, boolean isShort) {
        if (hasYear) {
            String year = YEAR_FORMAT.format(date);
            if (!year.startsWith("0")) {
                return isShort ? DAY_MONTH_YEAR_FORMAT_SHORT.format(date) : DAY_MONTH_YEAR_FORMAT.format(date);
            }
        }
        return isShort ? DAY_MONTH_FORMAT_SHORT.format(date) : DAY_MONTH_FORMAT.format(date);
    }
}

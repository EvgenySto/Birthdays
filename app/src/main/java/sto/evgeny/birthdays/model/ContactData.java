package sto.evgeny.birthdays.model;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class ContactData extends HashMap<String, String> {

    public static final SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("MMdd", Locale.getDefault());
    public static final SimpleDateFormat MONTH_DAY_YEAR_FORMAT = new SimpleDateFormat("MMddyyyy", Locale.getDefault());
    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    public enum Key {
        ID,
        NAME,
        MONTH_AND_DAY,
        DATE_TO_DISPLAY,
        DATE_TO_DISPLAY_SHORT
    }

    public ContactData(String id, String name, String[] date) {
        put(Key.ID.name(), id);
        put(Key.NAME.name(), name);
        put(Key.MONTH_AND_DAY.name(), date[1]);
        String year = date[0], month = date[1].substring(0, 2), day = date[1].substring(2);
        put(Key.DATE_TO_DISPLAY.name(), getDisplayDate(year, month, day, false));
        put(Key.DATE_TO_DISPLAY_SHORT.name(), getDisplayDate(year, month, day, true));
    }

    public String getId() {
        return get(Key.ID.name());
    }
    public String getName() {
        return get(Key.NAME.name());
    }
    public String getMonthAndDay() {
        return get(Key.MONTH_AND_DAY.name());
    }
    public String getDisplayDate() {
        return get(Key.DATE_TO_DISPLAY.name());
    }

    public String getDisplayDateShort() {
        return get(Key.DATE_TO_DISPLAY_SHORT.name());
    }

    private static String getDisplayDate(String year, String month, String day, boolean isShort) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String monthLong = dfs.getMonths()[Integer.parseInt(month) - 1];
        if (year != null && !year.startsWith("0")) {
            return isShort ? String.format("%s.%s.%s", day, month, year) :
                    String.format("%s %s %s", day, monthLong, year);
        }
        return isShort ? String.format("%s.%s", day, month) :
                String.format("%s %s", day, monthLong);
    }
}

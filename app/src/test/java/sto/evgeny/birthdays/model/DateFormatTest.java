package sto.evgeny.birthdays.model;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DateFormatTest {

    @Test
    public void testParse() {
        String[] date = DateFormat.DEFAULT_FORMAT.parse("2019-03-15");
        assertArrayEquals(new String[]{"2019", "0315"}, date);
        date = DateFormat.NO_YEAR_FORMAT.parse("--03-15");
        assertArrayEquals(new String[]{null, "0315"}, date);
        date = DateFormat.DEFAULT_FORMAT.parse("2019-02-29");
        assertArrayEquals(new String[]{"2019", "0229"}, date);
    }

}
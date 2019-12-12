package sto.evgeny.birthdays;

import org.junit.Test;

import java.util.Calendar;

import sto.evgeny.birthdays.model.ContactData;

import static org.junit.Assert.assertTrue;

public class DateComparatorTest {

    @Test
    public void testCompare() {
        DateComparator dateComparator = DateComparator.getInstance();
        Calendar current = Calendar.getInstance();

        current.set(2019, 4, 15);  // 2019-05-15
        dateComparator.setCurrentDate(current.getTime());

        int result = dateComparator.compare(
                new ContactData(null, null, new String[]{"2019", "0115"}),
                new ContactData(null, null, new String[]{"2019", "0915"}));
        assertTrue(result > 0);

        result = dateComparator.compare(
                new ContactData(null, null, new String[]{"2016", "0228"}),
                new ContactData(null, null, new String[]{"2016", "0229"}));
        assertTrue(result < 0);

        result = dateComparator.compare(
                new ContactData(null, null, new String[]{null, "0301"}),
                new ContactData(null, null, new String[]{null, "0229"}));
        assertTrue(result > 0);

        current.set(2019, 1, 29);  // 2019-02-29 (invalid date, converted to 2019-03-01)
        dateComparator.setCurrentDate(current.getTime());

        result = dateComparator.compare(
                new ContactData(null, null, new String[]{null, "0301"}),
                new ContactData(null, null, new String[]{null, "0229"}));
        assertTrue(result < 0);
    }
}
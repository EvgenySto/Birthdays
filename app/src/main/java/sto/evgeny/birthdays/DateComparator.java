package sto.evgeny.birthdays;

import java.util.Comparator;
import java.util.Date;

import sto.evgeny.birthdays.model.ContactData;

public class DateComparator implements Comparator<ContactData> {

    private static DateComparator INST;

    public static DateComparator getInstance() {
        if (INST == null) {
            INST = new DateComparator();
        }
        return INST;
    }

    private String currentDate = null;

    private DateComparator() {

    }

    public DateComparator setCurrentDate(Date date) {
        currentDate = ContactData.MONTH_DAY_FORMAT.format(date);
        return this;
    }

    @Override
    public int compare(ContactData o1, ContactData o2) {
        String monthAndDay1 = o1.getMonthAndDay();
        String monthAndDay2 = o2.getMonthAndDay();
        if(monthAndDay1.compareTo(currentDate) < 0 && monthAndDay2.compareTo(currentDate) >= 0 ||
                monthAndDay1.compareTo(currentDate) >= 0 && monthAndDay2.compareTo(currentDate) < 0) {
            return -monthAndDay1.compareTo(monthAndDay2);
        }
        return monthAndDay1.compareTo(monthAndDay2);
    }
}

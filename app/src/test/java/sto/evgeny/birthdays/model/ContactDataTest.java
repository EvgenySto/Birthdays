package sto.evgeny.birthdays.model;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ContactDataTest {

    @Test
    public void testData() {
        Locale.setDefault(new Locale("ru"));
        ContactData contactData = new ContactData("1", "Иван", new String[]{"1985", "0720"});
        assertEquals("1", contactData.getId());
        assertEquals("Иван", contactData.getName());
        assertEquals("0720", contactData.getMonthAndDay());
        assertEquals("20.07.1985", contactData.getDisplayDateShort());
        assertEquals("20 июля 1985", contactData.getDisplayDate());

        Locale.setDefault(new Locale("en"));
        contactData = new ContactData("2", "John", new String[]{"1985", "0720"});
        assertEquals("2", contactData.getId());
        assertEquals("John", contactData.getName());
        assertEquals("0720", contactData.getMonthAndDay());
        assertEquals("20.07.1985", contactData.getDisplayDateShort());
        assertEquals("20 July 1985", contactData.getDisplayDate());
    }
}
package sto.evgeny.birthdays.service;

import android.content.ContentResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import sto.evgeny.birthdays.model.ContactData;

public class ContactDataServiceTest implements ContactDataService {

    @Inject
    public ContactDataServiceTest() {
    }

    @Override
    public List<ContactData> getData(ContentResolver contentResolver) {
        return new ArrayList<ContactData>() {{
            add(new ContactData("1", "John", new String[]{"1985", "0915"}));
        }};
    }
}

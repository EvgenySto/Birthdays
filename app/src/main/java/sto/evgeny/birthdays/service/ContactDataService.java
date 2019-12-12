package sto.evgeny.birthdays.service;

import android.content.ContentResolver;

import java.util.List;

import sto.evgeny.birthdays.model.ContactData;

public interface ContactDataService {
    List<ContactData> getData(ContentResolver contentResolver);
}

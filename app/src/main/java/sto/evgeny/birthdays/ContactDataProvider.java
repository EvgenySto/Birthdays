package sto.evgeny.birthdays;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import sto.evgeny.birthdays.model.ContactData;
import sto.evgeny.birthdays.model.DateFormat;

public class ContactDataProvider {

    public static List<ContactData> getData(Context context) {
        Cursor contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY},
                null, null, null);

        if (contactsCursor == null) {
            return Collections.emptyList();
        }
        if (!contactsCursor.moveToFirst()) {
            contactsCursor.close();
            return Collections.emptyList();
        }

        List<String> contactIds = new ArrayList<>();
        while (!contactsCursor.isAfterLast()) {
            contactIds.add(contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)));
            contactsCursor.moveToNext();
        }
        contactsCursor.close();
        String[] idsArr = new String[contactIds.size()];
        contactIds.toArray(idsArr);

        Cursor dataCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data._ID, ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Event.START_DATE},
                ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " AND " +
                        ContactsContract.Data.CONTACT_ID + " in (" + makeArgPlaceholders(idsArr.length) + ")", idsArr,
                null);

        if (dataCursor == null) {
            return Collections.emptyList();
        }
        if (!dataCursor.moveToFirst()) {
            dataCursor.close();
            return Collections.emptyList();
        }

        List<ContactData> data = new ArrayList<>();
        while (!dataCursor.isAfterLast()) {
            String id = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String name = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
            String dateStr = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            Date date = null;
            boolean hasYear = false;
            for (DateFormat dateFormat : DateFormat.values()) {
                try {
                    date = dateFormat.getFormat().parse(dateStr);
                    hasYear = dateFormat.hasYear();
                    break;
                } catch (ParseException e) {
                    // ignore: try next format
                }
            }
            if (date != null) {
                data.add(new ContactData(id, name, date, hasYear));
            } else {
                System.out.printf("\n[WARN] Unknown date format: date '%s', contact '%s'",
                        dateStr, name);
            }
            dataCursor.moveToNext();
        }
        dataCursor.close();

        Collections.sort(data, DateComparator.getInstance().setCurrentDate(new Date()));

        return data;
    }

    private static String makeArgPlaceholders(int count) {
        String res = "";
        if (count > 0) {
            StringBuilder sb = new StringBuilder(res);
            for (int i = 0; i < count; i++) {
                sb.append("?,");
            }
            res = sb.substring(0, sb.length() - 1);
        }
        return res;
    }

}

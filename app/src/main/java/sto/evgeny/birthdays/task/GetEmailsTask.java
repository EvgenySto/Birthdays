package sto.evgeny.birthdays.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import sto.evgeny.birthdays.ListElement;
import sto.evgeny.birthdays.R;

public class GetEmailsTask extends AsyncTask<String, Void, List<ListElement>> {

    private ContentResolver contentResolver;
    private Resources resources;
    private int id;
    private Callback callback;

    public GetEmailsTask(Context context, int id, Callback callback) {
        contentResolver = context.getContentResolver();
        resources = context.getResources();
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected List<ListElement> doInBackground(String... params) {
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data._ID, ContactsContract.Data.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Email.ADDRESS},
                ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " +
                        ContactsContract.Data.CONTACT_ID + " = ? ", new String[] {params[0]}, null);
        List<ListElement> res = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                res.add(new ListElement(resources.getString(R.string.email) + ":", true));
                while (!cursor.isAfterLast()) {
                    String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    res.add(new ListElement(email, false));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return res;
    }
    @Override
    protected void onPostExecute(List<ListElement> result) {
        callback.onSuccess(id, result);
    }
}

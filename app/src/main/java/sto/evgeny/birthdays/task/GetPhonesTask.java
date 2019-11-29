package sto.evgeny.birthdays.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import sto.evgeny.birthdays.model.ListElement;
import sto.evgeny.birthdays.R;

public class GetPhonesTask extends AsyncTask<String, Void, List<ListElement>> {

    private ContentResolver contentResolver;
    private Resources resources;
    private int id;
    private Callback callback;

    public GetPhonesTask(Context context, int id, Callback callback) {
        contentResolver = context.getContentResolver();
        resources = context.getResources();
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected List<ListElement> doInBackground(String... params) {
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.Data._ID, ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                        ContactsContract.Data.CONTACT_ID + " = ? ", new String[] {params[0]}, null);
        List<ListElement> res = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                res.add(new ListElement(resources.getString(R.string.phones) + ":", ListElement.Type.GROUP_HEADER));
                while (!cursor.isAfterLast()) {
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    res.add(new ListElement(number, ListElement.Type.PHONE));
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

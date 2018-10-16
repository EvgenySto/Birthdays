package sto.evgeny.birthdays.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sto.evgeny.birthdays.DateComparator;
import sto.evgeny.birthdays.R;
import sto.evgeny.birthdays.model.ContactData;

/**
 * Created by Evgeny Stolbnikov on 22.08.2016.
 */
public class BackgroundService extends IntentService {

    private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat NO_YEAR_FORMAT = new SimpleDateFormat("--MM-dd", Locale.getDefault());

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        now = calendar.getTime();

        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("sto.evgeny.birthdays.PREF_FILE_KEY", Context.MODE_PRIVATE);
        String lastNotification = sharedPreferences.getString("LAST_NOTIFICATION", null);
        String currNotification = ContactData.MONTH_DAY_FORMAT.format(now);
        if (lastNotification != null && lastNotification.equals(currNotification)) {
            return super.onStartCommand(intent, flags, startId);
        }

        Iterator<ContactData> dataIterator = getData().iterator();
        Map<Integer, List<String>> nearestMap = new HashMap<Integer, List<String>>(){{
            put(0, new ArrayList<String>());
            put(1, new ArrayList<String>());
            put(2, new ArrayList<String>());
            put(3, new ArrayList<String>());
        }};

        while (dataIterator.hasNext()) {
            ContactData nearest = dataIterator.next();
            String mmDD = nearest.getMonthAndDay();
            String yyyy = ContactData.YEAR_FORMAT.format(now);
            try {
                Date nearestDate = ContactData.MONTH_DAY_YEAR_FORMAT.parse(mmDD + "-" + yyyy);
                int interval = (int) Math.round((nearestDate.getTime() - now.getTime()) / (double)(1000 * 60 * 60 * 24));
                if (nearestMap.containsKey(interval)) {
                    nearestMap.get(interval).add(nearest.getName());
                } else {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                // TODO
            }
        }

        final String birthday = getResources().getString(R.string.birthday);
        final String today = getResources().getString(R.string.today);
        final String tomorrow = getResources().getString(R.string.tomorrow);
        final String in2days = getResources().getString(R.string.in_2_days);
        final String in3days = getResources().getString(R.string.in_3_days);
        for (int i = 0; i < 4; i++) {
            if (nearestMap.get(i).size() == 0) {
                continue;
            }
            String label = birthday + " ";
            if (i == 0) {
                label += today;
            } else if (i == 1) {
                label += tomorrow;
            } else if (i == 2) {
                label += in2days;
            } else {
                label += in3days;
            }
            StringBuilder names = new StringBuilder();
            for (String name : nearestMap.get(i)) {
                names.append(name).append(", ");
            }
            names.setLength(names.length() - 2);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundService.this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(label)
                    .setContentText(names.toString());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(1000 + i, builder.build());
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LAST_NOTIFICATION", currNotification);
        editor.apply();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<ContactData> getData() {
        Cursor contactsCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
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

        Cursor dataCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
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
            if (dateStr.startsWith("--")) {
                try {
                    date = NO_YEAR_FORMAT.parse(dateStr);
                } catch (ParseException e) {
                    System.out.println(String.format("[WARN] Ignore unknown date format: date '%s', contact '%s'",
                            dateStr, name));
                }
            } else {
                try {
                    date = DEFAULT_FORMAT.parse(dateStr);
                    hasYear = true;
                } catch (ParseException e) {
                    System.out.println(String.format("[WARN] Ignore unknown date format: date '%s', contact '%s'",
                            dateStr, name));
                }
            }
            if (date != null) {
                data.add(new ContactData(id, name, date, hasYear));
            }
            dataCursor.moveToNext();
        }
        dataCursor.close();

        Collections.sort(data, DateComparator.getInstance().setCurrentDate(new Date()));

        return data;
    }

    private String makeArgPlaceholders(int count) {
        String res = "";
        if(count > 0) {
            StringBuilder sb = new StringBuilder(res);
            for(int i = 0; i < count; i++) {
                sb.append("?,");
            }
            res = sb.substring(0, sb.length() - 1);
        }
        return res;
    }
}

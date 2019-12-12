package sto.evgeny.birthdays.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import sto.evgeny.birthdays.R;
import sto.evgeny.birthdays.application.ComponentHolder;
import sto.evgeny.birthdays.model.ContactData;

/**
 * Created by Evgeny Stolbnikov on 22.08.2016.
 */
public class BackgroundService extends IntentService {

    @Inject
    public ContactDataService contactDataService;

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
        ((ComponentHolder) getApplicationContext()).getApplicationComponent().inject(this);

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

        Iterator<ContactData> dataIterator = contactDataService.getData(getContentResolver()).iterator();
        Map<Integer, List<String>> nearestMap = new HashMap<Integer, List<String>>(){{
            put(0, new ArrayList<String>());
            put(1, new ArrayList<String>());
            put(2, new ArrayList<String>());
            put(3, new ArrayList<String>());
        }};

        while (dataIterator.hasNext()) {
            ContactData nearest = dataIterator.next();
            String mmdd = nearest.getMonthAndDay();
            String yyyy = ContactData.YEAR_FORMAT.format(now);
            try {
                Date nearestDate = ContactData.MONTH_DAY_YEAR_FORMAT.parse(mmdd + yyyy);
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

}

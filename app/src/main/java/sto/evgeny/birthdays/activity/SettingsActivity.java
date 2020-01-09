package sto.evgeny.birthdays.activity;

import androidx.appcompat.app.AppCompatActivity;
import sto.evgeny.birthdays.R;
import sto.evgeny.birthdays.service.BackgroundService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Switch aSwitch = findViewById(R.id.notificationSwitch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startNotificationService();
                } else {
                    stopNotificationService();
                }
            }
        });

        final PendingIntent intent = PendingIntent.getService(this, 12345,
                new Intent(this, BackgroundService.class), PendingIntent.FLAG_NO_CREATE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aSwitch.setChecked(intent != null);
            }
        });
    }

    private void startNotificationService() {
        PendingIntent intent = PendingIntent.getService(this, 12345,
                new Intent(this, BackgroundService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && intent != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5000, intent);
        }
    }

    private void stopNotificationService() {
        PendingIntent intent = PendingIntent.getService(this, 12345,
                new Intent(this, BackgroundService.class), PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && intent != null) {
            alarmManager.cancel(intent);
        }
        if (intent != null) {
            intent.cancel();
        }
    }
}

package sto.evgeny.birthdays;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import sto.evgeny.birthdays.service.BackgroundService;

/**
 * Created by Evgeny on 13.05.2017.
 */

public class SettingsFragment extends Fragment {

    private Thread checkStatusThread;
    private boolean notificationsActive;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.notificationsButton);
        checkStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!checkStatusThread.isInterrupted()) {
                    final PendingIntent intent = PendingIntent.getService(SettingsFragment.this.getActivity(), 12345,
                            new Intent(SettingsFragment.this.getActivity(), BackgroundService.class), PendingIntent.FLAG_NO_CREATE);
                    SettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageButton.setBackground(getResources().getDrawable(
                                    intent != null ? R.drawable.circle_green : R.drawable.circle_red, null));
                            notificationsActive = intent != null;
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        checkStatusThread.start();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationsActive) {
                    stopNotificationService();
                } else {
                    startNotificationService();
                }
            }
        };
        view.findViewById(R.id.notificationsLabel).setOnClickListener(onClickListener);
        imageButton.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        checkStatusThread.interrupt();
        super.onDestroyView();
    }

    private void startNotificationService() {
        PendingIntent intent = PendingIntent.getService(getActivity(), 12345,
                new Intent(getActivity(), BackgroundService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5000, intent);
        }
    }

    private void stopNotificationService() {
        PendingIntent intent = PendingIntent.getService(getActivity(), 12345,
                new Intent(getActivity(), BackgroundService.class), PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(intent);
        }
        if (intent != null) {
            intent.cancel();
        }
    }
}

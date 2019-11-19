package sto.evgeny.birthdays.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import sto.evgeny.birthdays.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String appVersion;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO: log error
            appVersion = "-";
        }
        String text = getString(R.string.app_version, appVersion);
        ((TextView) findViewById(R.id.appVersionText)).setText(text);
    }
}

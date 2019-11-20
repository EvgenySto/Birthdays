package sto.evgeny.birthdays.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import sto.evgeny.birthdays.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String appVersion = "-", minSdkVer = "-", targetSdkVer = "-";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= 24) {
                minSdkVer = String.valueOf(packageInfo.applicationInfo.minSdkVersion);
                targetSdkVer = String.valueOf(packageInfo.applicationInfo.targetSdkVersion);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO: log error
        }
        ((TextView) findViewById(R.id.appVersionText))
                .setText(getString(R.string.app_version, appVersion));
        ((TextView) findViewById(R.id.appSdkVersionsText))
                .setText(getString(R.string.app_sdk_versions, minSdkVer, targetSdkVer));
        ((TextView) findViewById(R.id.appCurrentSdkText))
                .setText(getString(R.string.app_current_sdk, Build.VERSION.SDK_INT, Build.VERSION.RELEASE));
    }
}

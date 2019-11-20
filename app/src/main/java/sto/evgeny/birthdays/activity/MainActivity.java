package sto.evgeny.birthdays.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sto.evgeny.birthdays.ContactDataProvider;
import sto.evgeny.birthdays.ExtraKey;
import sto.evgeny.birthdays.R;
import sto.evgeny.birthdays.model.ContactData;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private Menu menu;
    private SimpleAdapter adapter;
    private SimpleAdapter adapterShort;
    private OnItemClickListener onItemClickListener;
    private ListView contactsListShort;
    private ListView contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            init();
        }
    }

    private void init() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                String.format("'%s' %s", getResources().getString(R.string.today_is),
                        getResources().getString(R.string.today_format)), Locale.getDefault());
        ((TextView)findViewById(R.id.currentDateText)).setText(sdf.format(new Date()));

        String[] from = {ContactData.Key.NAME.value(), ContactData.Key.DATE_TO_DISPLAY.value()};
        String[] fromShort = {ContactData.Key.NAME.value(), ContactData.Key.DATE_TO_DISPLAY_SHORT.value()};
        int[] to = {R.id.contactItem_name, R.id.contactItem_birthday};

        List<ContactData> contactDataList = ContactDataProvider.getData(this);
        adapter = new SimpleAdapter(this, contactDataList, R.layout.contact_item, from, to);
        adapterShort = new SimpleAdapter(this, contactDataList, R.layout.contact_item, fromShort, to);

        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactData item = (ContactData) adapter.getItem(position);
                long cId = Long.parseLong(item.getId());
                String cName = item.getName();
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra(ExtraKey.CONTACT_ID.name(), cId);
                intent.putExtra(ExtraKey.CONTACT_NAME.name(), cName);
                MainActivity.this.startActivity(intent);
            }
        };

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            contactsListShort = (ListView) findViewById(R.id.contactsListViewShort);
            contactsListShort.setAdapter(adapterShort);
            contactsListShort.setOnItemClickListener(onItemClickListener);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            contactsList = (ListView) findViewById(R.id.contactsListView);
            contactsList.setAdapter(adapter);
            contactsList.setOnItemClickListener(onItemClickListener);
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                init();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot get contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

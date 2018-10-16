package sto.evgeny.birthdays;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sto.evgeny.birthdays.model.ContactData;

public class MainActivity extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat NO_YEAR_FORMAT = new SimpleDateFormat("--MM-dd", Locale.getDefault());

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

        List<ContactData> contactDataList = getData();
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
            case R.id.action_settings:

                break;
            case R.id.notifications:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String makeArgPlaceholders(int count) {
        // TODO: java 8 stream collect
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

    private List<ContactData> getData() {
        Cursor contactsCursor = getContentResolver().query(Contacts.CONTENT_URI,
                new String[] {Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY},
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
            contactIds.add(contactsCursor.getString(contactsCursor.getColumnIndex(Contacts._ID)));
            contactsCursor.moveToNext();
        }
        contactsCursor.close();
        String[] idsArr = new String[contactIds.size()];
        contactIds.toArray(idsArr);

        Cursor dataCursor = getContentResolver().query(Data.CONTENT_URI,
                new String[] {Data._ID, Data.CONTACT_ID, Data.DISPLAY_NAME_PRIMARY, CommonDataKinds.Event.START_DATE},
                Data.MIMETYPE + " = '" + CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " +
                        CommonDataKinds.Event.TYPE + " = " + CommonDataKinds.Event.TYPE_BIRTHDAY + " AND " +
                        Data.CONTACT_ID + " in (" + makeArgPlaceholders(idsArr.length) + ")", idsArr,
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
            String id = dataCursor.getString(dataCursor.getColumnIndex(Data.CONTACT_ID));
            String name = dataCursor.getString(dataCursor.getColumnIndex(Data.DISPLAY_NAME_PRIMARY));
            String dateStr = dataCursor.getString(dataCursor.getColumnIndex(CommonDataKinds.Event.START_DATE));
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

}

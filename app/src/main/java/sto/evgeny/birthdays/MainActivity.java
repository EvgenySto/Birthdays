package sto.evgeny.birthdays;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String ID = "ID";
    private static final String NAME = "Name";
    private static final String DAY_AND_MONTH = "DayAndMonth";
    private static final String DATE_TO_DISPLAY = "DateToDisplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat sdf = new SimpleDateFormat("'Сегодня' dd MMMM yyyy", Locale.getDefault());
        ((TextView)findViewById(R.id.currentDateText)).setText(sdf.format(new Date()));

        Cursor contactsCursor = getContentResolver().query(Contacts.CONTENT_URI,
                new String[] {Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY},
                null, null, null);

        if (contactsCursor == null) {
            return;
        }
        if(!contactsCursor.moveToFirst()) {
            contactsCursor.close();
            return;
        }

        List<String> contactIds = new ArrayList<String>();
        while(!contactsCursor.isAfterLast()) {
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
            return;
        }
        if(!dataCursor.moveToFirst()) {
            dataCursor.close();
            return;
        }

        List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        while(!dataCursor.isAfterLast()) {
            HashMap<String,String> row = new HashMap<String,String>();
            String id = dataCursor.getString(dataCursor.getColumnIndex(Data.CONTACT_ID));
            String name = dataCursor.getString(dataCursor.getColumnIndex(Data.DISPLAY_NAME_PRIMARY));
            String dateStr = dataCursor.getString(dataCursor.getColumnIndex(CommonDataKinds.Event.START_DATE));
            Date date = null;
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date != null) {
                String year = (new SimpleDateFormat("yyyy", Locale.getDefault())).format(date);
                String datePattern = "dd MMMM";
                if(!year.startsWith("0")) {
                    datePattern += " yyyy";
                }
                String dayAndMonth = (new SimpleDateFormat("MM-dd", Locale.getDefault())).format(date);
                row.put(ID, id);
                row.put(NAME, name);
                row.put(DAY_AND_MONTH, dayAndMonth);
                row.put(DATE_TO_DISPLAY, (new SimpleDateFormat(datePattern, Locale.getDefault())).format(date));
                data.add(row);
            }
            dataCursor.moveToNext();
        }
        dataCursor.close();

        final String currDate = (new SimpleDateFormat("MM-dd", Locale.getDefault())).format(new Date());
        Comparator<HashMap<String,String>> comparator = new Comparator<HashMap<String,String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                String dayAndMonth1 = lhs.get(DAY_AND_MONTH);
                String dayAndMonth2 = rhs.get(DAY_AND_MONTH);
                if(dayAndMonth1.compareTo(currDate) < 0 && dayAndMonth2.compareTo(currDate) >= 0 ||
                        dayAndMonth1.compareTo(currDate) >= 0 && dayAndMonth2.compareTo(currDate) < 0) {
                    return -dayAndMonth1.compareTo(dayAndMonth2);
                }
                return dayAndMonth1.compareTo(dayAndMonth2);
            }
        };
        Collections.sort(data, comparator);

        String[] from = {NAME, DATE_TO_DISPLAY};
        int[] to = {R.id.contactItem_name, R.id.contactItem_birthday};

        final SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.contact_item, from, to);

        ListView contactsList = (ListView)findViewById(R.id.contactsListView);
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> item = (HashMap<String,String>)adapter.getItem(position);
                long cId = Long.parseLong(item.get(ID));
                String cName = item.get(NAME);
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra(ExtraKey.CONTACT_ID.name(), cId);
                intent.putExtra(ExtraKey.CONTACT_NAME.name(), cName);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String makeArgPlaceholders(int count) {
        String res = "";
        if(count > 0) {
            StringBuffer sb = new StringBuffer(res);
            for(int i = 0; i < count; i++) {
                sb.append("?,");
            }
            res = sb.substring(0, sb.length() - 1);
        }
        return res;
    }

}

package sto.evgeny.birthdays;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactActivity extends Activity {

	private long cId;
	private List<ListElement> elemList;
	private ListView detailed;
	private BaseAdapter baseAdapter;
	private boolean phonesReady;
	private boolean emailsReady;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		cId = getIntent().getLongExtra(ExtraKey.CONTACT_ID.name(), -1L);
		String cName = getIntent().getStringExtra(ExtraKey.CONTACT_NAME.name());
		
		((TextView)findViewById(R.id.contactNameText)).setText(cName);
		
		detailed = (ListView)findViewById(R.id.detailedInfo);
		
		elemList = new ArrayList<ListElement>();
		
		baseAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater layoutInflater = LayoutInflater.from(ContactActivity.this);
				View v;
				if(elemList.get(position).isGroupHeader()) {
					v = layoutInflater.inflate(R.layout.group_header, null);
					((TextView)v.findViewById(R.id.groupTitle)).setText(elemList.get(position).getText());
				}
				else {
					v = layoutInflater.inflate(android.R.layout.simple_list_item_1, null);
					((TextView)v.findViewById(android.R.id.text1)).setText(elemList.get(position).getText());
				}
				return v;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return elemList.get(position);
			}
			
			@Override
			public int getCount() {
				return elemList.size();
			}
		};
		
		detailed.setAdapter(baseAdapter);
		
		new GetPhonesTask().execute(String.valueOf(cId));
		new GetEmailsTask().execute(String.valueOf(cId));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class GetPhonesTask extends AsyncTask<String, Void, List<ListElement>> {
		@Override
		protected List<ListElement> doInBackground(String... params) {
			Cursor cursor = getContentResolver().query(Data.CONTENT_URI, 
					new String[] {Data._ID, Data.CONTACT_ID, CommonDataKinds.Phone.NUMBER}, 
					Data.MIMETYPE + " = '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
					Data.CONTACT_ID + " = ? ", new String[] {params[0]}, null);
			List<ListElement> res = new ArrayList<ListElement>();
			if(cursor.moveToFirst()) {
				res.add(new ListElement("Телефоны:", true));
				while(!cursor.isAfterLast()) {
					String number = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
					res.add(new ListElement(number, false));
					cursor.moveToNext();
				}
			}
			cursor.close();
			phonesReady = true;
			return res;
		}
		@Override
		protected void onPostExecute(List<ListElement> result) {
			elemList.addAll(result);
			baseAdapter.notifyDataSetChanged();
			if(phonesReady && emailsReady) {
				((ProgressBar)ContactActivity.this.findViewById(R.id.progressBarCircle)).setVisibility(View.GONE);
			}
		}
	}
	
	public class GetEmailsTask extends AsyncTask<String, Void, List<ListElement>> {
		@Override
		protected List<ListElement> doInBackground(String... params) {
			Cursor cursor = getContentResolver().query(Data.CONTENT_URI, 
					new String[] {Data._ID, Data.CONTACT_ID, CommonDataKinds.Email.ADDRESS}, 
					Data.MIMETYPE + " = '" + CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " +
					Data.CONTACT_ID + " = ? ", new String[] {params[0]}, null);
			List<ListElement> res = new ArrayList<ListElement>();
			if(cursor.moveToFirst()) {
				res.add(new ListElement("Эл. почта:", true));
				while(!cursor.isAfterLast()) {
					String email = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
					res.add(new ListElement(email, false));
					cursor.moveToNext();
				}
			}
			cursor.close();
			emailsReady = true;
			return res;
		}
		@Override
		protected void onPostExecute(List<ListElement> result) {
			elemList.addAll(result);
			baseAdapter.notifyDataSetChanged();
			if(phonesReady && emailsReady) {
				((ProgressBar)ContactActivity.this.findViewById(R.id.progressBarCircle)).setVisibility(View.GONE);
			}
		}
	}
}

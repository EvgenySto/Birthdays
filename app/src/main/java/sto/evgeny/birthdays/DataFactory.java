package sto.evgeny.birthdays;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import sto.evgeny.birthdays.application.ComponentHolder;
import sto.evgeny.birthdays.model.ContactData;
import sto.evgeny.birthdays.service.ContactDataService;

public class DataFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Intent intent;
    private List<Map<String, String>> data;
    @Inject
    public ContactDataService contactDataService;

    public DataFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }
    @Override
    public void onCreate() {
        data = new ArrayList<>();
        ((ComponentHolder) context.getApplicationContext()).getApplicationComponent().inject(this);
    }

    @Override
    public void onDataSetChanged() {
        data.clear();

        for (ContactData contactData : contactDataService.getData(context.getContentResolver()).subList(0, 3)) {
            Map<String, String> item = new HashMap<>();
            item.put("id", contactData.getId());
            item.put("name", contactData.getName());
            item.put("birthday", contactData.getDisplayDate());
            data.add(item);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        Map<String, String> item = data.get(position);
        remoteViews.setTextViewText(R.id.widgetItem_name, item.get("name"));
        remoteViews.setTextViewText(R.id.widgetItem_birthday, item.get("birthday"));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(ExtraKey.CONTACT_ID.name(), Long.parseLong(item.get("id")));
        fillInIntent.putExtra(ExtraKey.CONTACT_NAME.name(), item.get("name"));
        remoteViews.setOnClickFillInIntent(R.id.widgetItem, fillInIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

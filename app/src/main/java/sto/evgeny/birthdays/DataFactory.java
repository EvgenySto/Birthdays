package sto.evgeny.birthdays;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Intent intent;
    private List<Map<String, String>> data;

    public DataFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }
    @Override
    public void onCreate() {
        data = new ArrayList<Map<String, String>>();
    }

    @Override
    public void onDataSetChanged() {
        data.clear();
        Map<String, String> item0 = new HashMap<String, String>();
        item0.put("name", "Кто-то");
        item0.put("birthday", "Когда-то");
        data.add(item0);
        item0 = new HashMap<String, String>();
        item0.put("name", "Ещё кто-то");
        item0.put("birthday", "Позже");
        data.add(item0);
        item0 = new HashMap<String, String>();
        item0.put("name", "И ещё кто-то");
        item0.put("birthday", "Много позже");
        data.add(item0);
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

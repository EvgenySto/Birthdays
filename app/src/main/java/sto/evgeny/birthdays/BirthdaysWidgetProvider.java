package sto.evgeny.birthdays;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class BirthdaysWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			remoteViews.setRemoteAdapter(R.id.listViewWidget, new Intent(context, DataService.class));
			remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}
}

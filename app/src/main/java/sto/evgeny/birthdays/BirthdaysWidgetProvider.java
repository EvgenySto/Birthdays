package sto.evgeny.birthdays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import sto.evgeny.birthdays.activity.ContactActivity;

public class BirthdaysWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
		    Intent intent = new Intent(context, ContactActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		    remoteViews.setRemoteAdapter(R.id.listViewWidget, new Intent(context, DataService.class));
		    remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
		    remoteViews.setPendingIntentTemplate(R.id.listViewWidget, pendingIntent);
		    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}
}

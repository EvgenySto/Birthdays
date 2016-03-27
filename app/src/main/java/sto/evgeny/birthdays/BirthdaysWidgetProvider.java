package sto.evgeny.birthdays;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class BirthdaysWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for(int i = 0; i < appWidgetIds.length; i++) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			remoteViews.setTextViewText(R.id.widgetText, "Hello from widget :-)");
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
	}
}

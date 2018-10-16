package sto.evgeny.birthdays;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class DataService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DataFactory(getApplicationContext(), intent);
    }
}

package sto.evgeny.birthdays.application;

import android.app.Application;

public class MainApplication extends Application implements ComponentHolder {

    private ApplicationComponent applicationComponent = DaggerApplicationComponent.create();

    @Override
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}

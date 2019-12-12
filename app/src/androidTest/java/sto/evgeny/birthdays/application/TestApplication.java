package sto.evgeny.birthdays.application;

import android.app.Application;

public class TestApplication extends Application implements ComponentHolder {

    private ApplicationComponent applicationComponent = DaggerTestApplicationComponent.create();

    @Override
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}

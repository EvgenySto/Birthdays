package sto.evgeny.birthdays.application;

import javax.inject.Singleton;

import dagger.Component;
import sto.evgeny.birthdays.DataFactory;
import sto.evgeny.birthdays.activity.MainActivity;
import sto.evgeny.birthdays.service.BackgroundService;

@Singleton
@Component(modules = MainModule.class)
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(DataFactory dataFactory);
    void inject(BackgroundService backgroundService);
}

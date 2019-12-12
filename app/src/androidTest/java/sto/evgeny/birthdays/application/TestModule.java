package sto.evgeny.birthdays.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sto.evgeny.birthdays.service.ContactDataService;
import sto.evgeny.birthdays.service.ContactDataServiceTest;

@Module
public class TestModule {
    @Singleton
    @Provides
    public ContactDataService provideContactDataService() {
        return new ContactDataServiceTest();
    }
}

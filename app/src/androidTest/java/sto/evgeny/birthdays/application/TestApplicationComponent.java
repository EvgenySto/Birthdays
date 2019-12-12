package sto.evgeny.birthdays.application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestModule.class)
public interface TestApplicationComponent extends ApplicationComponent {
}

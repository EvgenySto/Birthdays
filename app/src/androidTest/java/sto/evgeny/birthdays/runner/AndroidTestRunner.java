package sto.evgeny.birthdays.runner;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;
import sto.evgeny.birthdays.application.TestApplication;

public class AndroidTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, TestApplication.class.getName(), context);
    }
}

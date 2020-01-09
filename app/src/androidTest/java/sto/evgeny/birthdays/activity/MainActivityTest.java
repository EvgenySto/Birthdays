package sto.evgeny.birthdays.activity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import sto.evgeny.birthdays.R;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant("android.permission.READ_CONTACTS");

    @Test
    public void mainActivityTest() {
        sleep();

        ViewInteraction actionMenuItemView = onView(withId(R.id.action_about));
        actionMenuItemView.perform(click());

        sleep();

        ViewInteraction appCompatImageButton = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.action_bar),
                                childAtPosition(
                                        withId(R.id.action_bar_container),
                                        0)),
                        1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        sleep();

        ViewInteraction actionMenuItemView1 = onView(withId(R.id.action_settings));
        actionMenuItemView1.perform(click());

        sleep();

        ViewInteraction actionMenuItemView2 = onView(withId(R.id.notificationSwitch));
        actionMenuItemView2.perform(click());

        sleep();

        ViewInteraction appCompatImageButton1 = onView(
                allOf(childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton1.perform(click());

        sleep();

        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(withId(R.id.contactsListViewShort))
                .atPosition(0);
        linearLayout2.perform(click());

        sleep();

        ViewInteraction appCompatImageButton2 = onView(
                allOf(childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

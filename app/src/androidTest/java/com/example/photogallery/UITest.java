package com.example.photogallery;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void searchForAnImageInGivenTimeRange(){
//        onView(withId(R.id.btnSearch)).perform(click());
//        onView(withId(R.id.etFromDateTime)).perform(clearText());
//        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-26"), closeSoftKeyboard());
//        onView(withId(R.id.etToDateTime)).perform(clearText());
//        onView(withId(R.id.etToDateTime)).perform(typeText("2020-09-27"), closeSoftKeyboard());
//        onView(withId(R.id.go)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("Cat")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("115355"))));
    }

    @Test
    public void searchForAnImageInGivenTimeRangeUsingCaptionFilter(){
//        onView(withId(R.id.btnSearch)).perform(click());
//        onView(withId(R.id.etFromDateTime)).perform(clearText());
//        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-22"), closeSoftKeyboard());
//        onView(withId(R.id.etToDateTime)).perform(clearText());
//        onView(withId(R.id.etToDateTime)).perform(typeText("2020-09-23"), closeSoftKeyboard());
//        onView(withId(R.id.etKeywords)).perform(typeText("TV Animation"), closeSoftKeyboard());
//        onView(withId(R.id.go)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("TV Animation")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("115506"))));
    }

    @Test
    public void searchForAnImageInGivenTimeRangeUsingCaptionFilterAndScrollRight(){
//        onView(withId(R.id.btnSearch)).perform(click());
//        onView(withId(R.id.etFromDateTime)).perform(clearText());
//        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-22"), closeSoftKeyboard());
//        onView(withId(R.id.etToDateTime)).perform(clearText());
//        onView(withId(R.id.etToDateTime)).perform(typeText("2020-09-23"), closeSoftKeyboard());
//        onView(withId(R.id.etKeywords)).perform(typeText("TV Animation"), closeSoftKeyboard());
//        onView(withId(R.id.go)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("TV Animation")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("115506"))));
//        onView(withId(R.id.btnNext)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("TV Animation - Red icon")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("115839"))));
    }

}


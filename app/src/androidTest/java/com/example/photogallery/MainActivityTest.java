package com.example.photogallery;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Rule
    private ActivityScenario<MainActivity> ac = new ActivityScenario<>(MainActivity.class);

    @Test
    public void test_isActivityInView() {
    }
}
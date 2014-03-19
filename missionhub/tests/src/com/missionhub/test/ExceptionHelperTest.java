package com.missionhub.test;

import android.test.InstrumentationTestCase;

import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.WebViewException;

public class ExceptionHelperTest extends InstrumentationTestCase {

    public void testIgnoredThrowable() {

        Throwable t = new Exception("Test");
        ExceptionHelper ex = new ExceptionHelper(t);
        assertFalse(ex.isIgnoredThrowable(t));

        Throwable t2 = new WebViewException(1, "Test", "http://example.com");
        ExceptionHelper ex2 = new ExceptionHelper(t2);
        assertTrue(ex2.isIgnoredThrowable(t2));
    }

}
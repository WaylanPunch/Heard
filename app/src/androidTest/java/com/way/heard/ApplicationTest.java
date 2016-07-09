package com.way.heard;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.way.heard.utils.Util;

import junit.framework.Assert;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCompare() {
        Assert.assertFalse(Util.compare(null,"123"));
        Assert.assertTrue(Util.compare("123","123"));
    }
}
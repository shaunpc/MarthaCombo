package com.spc.marthacombo;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shaun on 15/03/16.
 * Enables the testing of the MeTime class
 */
public class MeTime_UnitTest {

    private MeTime mMeTime;

    @Before
    public void setUp() {
        Context context = null;
        mMeTime = new MeTime(context);
    }

    @Test
    public void meTime_on_creation_returns_zero() {
        int resultMinutes = mMeTime.getMinutes();
        assertEquals(0, resultMinutes);
    }

    @Test
    public void meTime_increase_15_minutes() {
        mMeTime.setMinutes(20);
        int resultMinutes = mMeTime.increase(15);
        assertEquals(35, resultMinutes);
    }

    @Test
    public void meTime_decrease_15_minutes() {
        mMeTime.setMinutes(20);
        int resultMinutes = mMeTime.decrease(15);
        assertEquals(5, resultMinutes);
    }

    @Test
    public void meTime_decrease_to_negative() {
        mMeTime.setMinutes(10);
        int resultMinutes = mMeTime.decrease(15);
        assertEquals(0, resultMinutes);
    }

    @Test
    public void meTime_check_persistence() {
        mMeTime.setMinutes(33); // should persist 33
        MeTime meTime = new MeTime();  // new object should obtain from prefs file
        int resultMinutes = meTime.getMinutes();
        assertEquals(33, resultMinutes);
    }
}

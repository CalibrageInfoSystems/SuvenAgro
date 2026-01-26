package com.cis.palm360.palmgrow.SuvenAgro;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.*;

import androidx.test.InstrumentationRegistry;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.oilpalm3f.mainapp", appContext.getPackageName());
    }
}

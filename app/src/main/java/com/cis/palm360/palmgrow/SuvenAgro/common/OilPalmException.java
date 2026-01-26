package com.cis.palm360.palmgrow.SuvenAgro.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by siva on 06/07/17.
 */

//Handles exception
public class OilPalmException extends Exception
{

    private static final long serialVersionUID = 1997753363232807009L;

    public OilPalmException()
    {
    }

    public OilPalmException(String message)
    {
        super(message);
    }

    public OilPalmException(Throwable cause)
    {
        super(cause);
    }

    public OilPalmException(String message, Throwable cause)
    {
        super(message, cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public OilPalmException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

} 

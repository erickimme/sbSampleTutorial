package com.example.sbandroidsampleapptutorial.main;

import android.app.Application;

import com.example.sbandroidsampleapptutorial.utils.PreferenceUtils;
import com.sendbird.android.SendBird;

public class BaseApplication extends Application {
    private static final String APP_ID = "90EEACA0-2C62-4328-9F71-C7806FABDD75"; // eric demo 90EEACA0-2C62-4328-9F71-C7806FABDD75 //yell 5E0AEB67-04A0-4438-AC5A-AFC13661449A

    @Override
    public void onCreate(){
        super.onCreate();
        PreferenceUtils.init(getApplicationContext());

        SendBird.init(APP_ID, getApplicationContext());

    }
}

package project.doublepark.doublepark;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by jabez on 12/5/2017.
 */

public class MyApplication extends MultiDexApplication {
    //To make it compatible with sdk version below 22.
    private static MyApplication mainApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public static synchronized MyApplication getInstance() {
        return mainApplication;
    }
}

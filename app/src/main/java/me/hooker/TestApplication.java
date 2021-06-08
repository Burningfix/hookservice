package me.hooker;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initHook();
        } catch (Throwable e) {
            Log.i("sanbo",Log.getStackTraceString(e));
        }
    }

    private void initHook() throws Exception {
        Object defaultSingleton = null;
        if (Build.VERSION.SDK_INT >= 26) {
            Class activityManagerClass = Class.forName("android.app.ActivityManager");
            Field songletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            songletonField.setAccessible(true);
            defaultSingleton = songletonField.get(activityManagerClass);
        } else {
            Class activityManagerClass = Class.forName("android.app.ActivityManagerNative");
            Field songletonField = activityManagerClass.getDeclaredField("gDefault");
            songletonField.setAccessible(true);
            defaultSingleton = songletonField.get(activityManagerClass);
        }
        Class singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        Class iActivityManagerClass = Class.forName("android.app.IActivityManager");
        Object iActivityManager = mInstanceField.get(defaultSingleton);
        Object iActivityManagerProxy = Proxy.newProxyInstance(getClassLoader(),
                new Class[]{iActivityManagerClass}, new IActivityManagerProxy(iActivityManager));
        mInstanceField.set(defaultSingleton, iActivityManagerProxy);
    }
}
package me.hooker.servie;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.hooker.MainActivity;

public class ProxyService extends Service {


    public final static String TARGET_SERVICE = "TargetService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if (intent == null || !intent.hasExtra(TARGET_SERVICE)) {
            return START_STICKY;
        }
        String targetServiceName = intent.getStringExtra(TARGET_SERVICE);
        Log.e(TAG, "targetServiceName = " + targetServiceName);
        if (targetServiceName == null) {
            return START_STICKY;
        }
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Method getApplicationThreadMethod =
                    activityThreadClass.getDeclaredMethod("getApplicationThread");
            getApplicationThreadMethod.setAccessible(true);
            Field sCurrentActivityThreadField =
                    activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadField.get(activityThreadClass);
            Object applicationThread = getApplicationThreadMethod.invoke(sCurrentActivityThread);

            Class iInterfaceClass = Class.forName("android.os.IInterface");
            Method asbinderMethod = iInterfaceClass.getDeclaredMethod("asBinder");
            asbinderMethod.setAccessible(true);
            Object token = asbinderMethod.invoke(applicationThread);

            Class serviceClass = Class.forName("android.app.Service");
            Method attachMethod = serviceClass.getDeclaredMethod("attach",
                    Context.class, activityThreadClass, String.class, IBinder.class, Application.class, Object.class);
            attachMethod.setAccessible(true);

            Object defaultSingleton = null;
            if (Build.VERSION.SDK_INT >= 26) {
                Class activityManagerClass = Class.forName("android.app.ActivityManager");
                Field activityManagerSinletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
                activityManagerSinletonField.setAccessible(true);
                defaultSingleton = activityManagerSinletonField.get(activityManagerClass);
            } else {
                Class activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                Field activityManagerSinletonField = activityManagerClass.getDeclaredField("gDefault");
                activityManagerSinletonField.setAccessible(true);
                defaultSingleton = activityManagerSinletonField.get(activityManagerClass);
            }
            Class singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object iActivityManager = mInstanceField.get(defaultSingleton);
            Service TargetService = (Service) Class.forName(targetServiceName).newInstance();
            attachMethod.invoke(TargetService, this, sCurrentActivityThread,
                    targetServiceName, token, getApplication(), iActivityManager);
            TargetService.onCreate();
            TargetService.onStartCommand(intent, flags, startId);
        } catch (Exception e) {
            e.printStackTrace();
            return START_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private final String TAG = "sanbo.proxy";

    private void logd(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    private void loge(String info) {
        Log.println(Log.ERROR, TAG, info);
    }

    private void logi(String info) {
        Log.println(Log.INFO, TAG, info);
    }
}
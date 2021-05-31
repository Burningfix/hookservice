package me.hooker.servie;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NoDefineService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logi("Service is created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logi("Service is started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logi("Service is Destroy");
    }

    private final String mClassName = NoDefineService.class.getName();

    private final String TAG = "sanbo." + mClassName;

    private void logd(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    private void logi(String info) {
        Log.println(Log.INFO, TAG, "[" + mClassName + "] " + info);
    }
}

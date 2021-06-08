package me.hooker.servie;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TargetService extends Service {

    private final String mClassName = "Target";

    private final String TAG = "sanbo." + mClassName;

    private void logd(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    private void loge(String info) {
        Log.println(Log.ERROR, TAG, info);
    }

    private void logi(String info) {
        Log.println(Log.INFO, TAG, "[" + mClassName + "] " + info);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
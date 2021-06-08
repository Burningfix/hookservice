package me.hooker;

import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import me.hooker.servie.ProxyService;

public class IActivityManagerProxy implements InvocationHandler {

    private final String mClassName = "IAMProxy";

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

    private Object iActivityManager;

    public IActivityManagerProxy(Object iActivityManager) {
        this.iActivityManager = iActivityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startService")) {
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    intent = (Intent) args[i];
                    index = i;
                    break;
                }
            }
            logd("intent:" + intent);
            if (intent.getComponent().getClassName().contains("TargetService")) {
                Intent proxyIntent = new Intent();
                proxyIntent.putExtra(ProxyService.TARGET_SERVICE, intent.getComponent().getClassName());
                proxyIntent.setClassName("com.zacky.serviceplugintest", "com.zacky.serviceplugintest.ProxyService");
                logd("proxyIntent:" + proxyIntent);
                args[index] = proxyIntent;
                Log.e(TAG, "HOOK SUCCESS");
            }
        }
        return method.invoke(iActivityManager, args);
    }
}
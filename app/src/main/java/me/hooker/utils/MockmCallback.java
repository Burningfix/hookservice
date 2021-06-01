package me.hooker.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import me.hooker.utils.RefInvoke;


class MockmCallback implements Handler.Callback {

    Handler mBase;

    public MockmCallback(Handler base) {
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {

        try {
//            logd(Log.getStackTraceString(new Exception("xxx")));
            logd("handleMessage [" + msg.what + "] " + msg.toString());
            switch (msg.what) {

                // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
                // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                case 114:
                    handleCreateService(msg);
                    break;
                case 115:
                    ServiceArgsData(msg);
                    break;
            }

            mBase.handleMessage(msg);

        } catch (Throwable e) {
            loge(Log.getStackTraceString(e));
        }
        return true;
    }

    private void ServiceArgsData(Message msg) {
        // 这里简单起见,直接取出插件Servie
        Object obj = msg.obj;
        logd("ServiceArgsData obj:" + obj);

        Intent args = (Intent) RefInvoke.getFieldObject(obj, "args");
        logd("ServiceArgsData args:" + args);
    }

    private void handleCreateService(Message msg) {
        // 这里简单起见,直接取出插件Servie
        Object obj = msg.obj;

        ServiceInfo serviceInfo = (ServiceInfo) RefInvoke.getFieldObject(obj, "info");
        logd("handleCreateService serviceInfo:" + serviceInfo);

//        String realServiceName = null;

//        for (String key : UPFApplication.pluginServices.keySet()) {
//            String value = UPFApplication.pluginServices.get(key);
//            if(value.equals(serviceInfo.name)) {
//                realServiceName = key;
//                break;
//            }
//        }
//
//        serviceInfo.name = realServiceName;
    }

    private final String TAG = "sanbo." + MockmCallback.this.getClass().getName();

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


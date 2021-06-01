package me.hooker.utils;


import android.os.Handler;
import android.util.Log;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import me.hooker.MainActivity;

public class AMSHookHelper {

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookAMN() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {

        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = null;
        if (android.os.Build.VERSION.SDK_INT <= 25) {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");
        } else {
            //获取ActivityManager的单例IActivityManagerSingleton，他其实就是之前的gDefault
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManager", "IActivityManagerSingleton");
        }
        logd("hookAMN gDefault:" + gDefault);

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");
        logd("hookAMN mInstance[android.util.Singleton]:" + mInstance);

        // 创建一个这个对象的代理对象MockClass1, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> classB2Interface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{classB2Interface},
                new MockSingleton(mInstance));
        logd("hookAMN proxy:" + proxy);

        //把gDefault的mInstance字段，修改为proxy
        Class class1 = gDefault.getClass();
        logd("hookAMN class1:" + class1);
        RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
        logi("hookAMN SUCCESS");

    }

    public static void hookActivityThread() throws Exception {

        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        logd("hookActivityThread currentActivityThread:" + currentActivityThread);

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvoke.getFieldObject(currentActivityThread, "mH");
        logd("hookActivityThread mH:" + mH);

        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new MockmCallback(mH));
        logi("hookActivityThread SUCCESS");

    }


    private final static String TAG = "sanbo." + AMSHookHelper.class.getName();

    private static void logd(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    private static void loge(String info) {
        Log.println(Log.ERROR, TAG, info);
    }

    private static void logi(String info) {
        Log.println(Log.INFO, TAG, "[" + AMSHookHelper.class.getName() + "] " + info);
    }
}

package me.hooker.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


class MockSingleton implements InvocationHandler {


    Object mBase;

    public MockSingleton(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        logi("====invoke==="+method.toString());

        try {
            if ("startService".equals(method.getName())) {
                // 只拦截这个方法
                // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱

                // 找到参数里面的第一个Intent 对象
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }

                //get StubService form UPFApplication.pluginServices
                Intent rawIntent = (Intent) args[index];

                logi("invoke startService rawIntent: " + rawIntent);

                ComponentName ct = rawIntent.getComponent();
                String rawServiceName = ct.getClassName();
//                String stubServiceName = UPFApplication.pluginServices.get(rawServiceName);
                logi("invoke startService rawServiceName: " + rawServiceName);
//                logi("invoke startService stubServiceName: " + stubServiceName);

//                // 已声明的跳转未声明的
//                if ("me.hooker.servie.DefineService".equals(rawServiceName)) {
//                    int x = rawIntent.getIntExtra("JUMP", -1);
//                    logi("invoke startService JUMP: " + x);
//                    if (x == 1) {
//                        ComponentName componentName = new ComponentName(ct.getPackageName(), "me.hooker.servie.NoDefineService");
//                        Intent newIntent = new Intent();
//                        newIntent.setComponent(componentName);
//                        logi("invoke startService newIntent: " + newIntent);
//
//                        // Replace Intent, cheat AMS
//                        args[index] = newIntent;
//                    }
//                }

//                // replace Plugin Service of StubService
//                ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
//                Intent newIntent = new Intent();
//                newIntent.setComponent(componentName);
//
//                // Replace Intent, cheat AMS
//                args[index] = newIntent;

                // 替换成声明了的。能打开，但是不会回调114
                // 即handleMessage [114] { when=-4ms
                //                          what=114 obj=
                //                          CreateServiceData
                //                          {token=android.os.BinderProxy@f720f4d
                //                              className=me.hooker.servie.DefineService
                //                              packageName=me.hooker intent=null
                //                           }
                //                           target=android.app.ActivityThread$H
                //                           }
                if (rawServiceName.equals("me.hooker.servie.NoDefineService")) {
                    ComponentName componentName = new ComponentName(ct.getPackageName(), "me.hooker.servie.DefineService");
                    Intent newIntent = new Intent();
                    newIntent.setComponent(componentName);
                    logi("invoke startService newIntent: " + newIntent);

                    // Replace Intent, cheat AMS
                    args[index] = newIntent;
                }


                logd("hook startService success");
                return method.invoke(mBase, args);
            } else if ("stopService".equals(method.getName())) {
                // 只拦截这个方法
                // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱

                // 找到参数里面的第一个Intent 对象
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }

//                //get StubService form UPFApplication.pluginServices
                Intent rawIntent = (Intent) args[index];

                logi("invoke stopService rawIntent: " + rawIntent);

                String rawServiceName = rawIntent.getComponent().getClassName();
//                String stubServiceName = UPFApplication.pluginServices.get(rawServiceName);
//
//                // replace Plugin Service of StubService
//                ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
//                Intent newIntent = new Intent();
//                newIntent.setComponent(componentName);
//
//                // Replace Intent, cheat AMS
//                args[index] = newIntent;

                logd("hook stopService success");
                return method.invoke(mBase, args);
            }
        } catch (Throwable e) {
            logd(Log.getStackTraceString(e));
        }

        return method.invoke(mBase, args);
    }

    private final String TAG = "sanbo." + MockSingleton.this.getClass().getName();

    private void logd(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    private void logi(String info) {
        Log.println(Log.INFO, TAG, info);
    }


}
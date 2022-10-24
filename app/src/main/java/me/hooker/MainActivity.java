package me.hooker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import me.hooker.servie.DefineService;
import me.hooker.servie.NoDefineService;
import me.hooker.utils.AMSHookHelper;

public class MainActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            AMSHookHelper.hookAMN();
            AMSHookHelper.hookActivityThread();
        } catch (Throwable e) {
            loge(Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    ServiceConnection mconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logi("onServiceConnected name:" + name + "----service:" + service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logi("onServiceDisconnected name:" + name);

        }
    };

    public void onClick(View view) {
        try {
            Intent define = new Intent(MainActivity.this, DefineService.class);
            Intent noDefine = new Intent(MainActivity.this, NoDefineService.class);
            switch (view.getId()) {
                case R.id.btnStartDefineService:
                    logd("点击开启定义的服务");
                    startService(define);
                    break;
                case R.id.btnStopDefineService:
                    logd("点击停止定义的服务");
                    stopService(define);
                    break;
                case R.id.btnBindDefineService:
                    logd("点击绑定定义的服务");
                    bindService(define, mconn, BIND_AUTO_CREATE);
                    break;
                case R.id.btnUnbindDefineService:
                    logd("点击反绑定定义的服务");
                    unbindService(mconn);
                    break;
                case R.id.btnStartNodefineService:
                    logd("点击开启未定义的服务");
                    startService(noDefine);
//                    Intent vx = new Intent(MainActivity.this, DefineService.class);
//                    vx.putExtra("JUMP", 1);
//                    startService(vx);
                    break;
                case R.id.btnStopNodefineService:
                    logd("点击停止未定义的服务");
                    stopService(noDefine);
                    break;
                case R.id.btnBindNodefineService:
                    logd("点击绑定未定义的服务");
                    bindService(noDefine, mconn, BIND_AUTO_CREATE);
                    break;
                case R.id.btnUnbindNodefineService:
                    logd("点击反绑定未定义的服务");
                    unbindService(mconn);
                    break;
            }
        } catch (Throwable e) {
            loge(Log.getStackTraceString(e));
        }
    }

    private final String mClassName = MainActivity.class.getName();

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

}
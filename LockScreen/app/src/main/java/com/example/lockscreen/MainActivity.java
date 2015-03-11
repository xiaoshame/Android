package com.example.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by xiaozhisong on 15-3-6.
 */
public class MainActivity extends Activity{
    //管理设备，可以实现屏幕锁定，亮度调节，恢复出厂设置等功能
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    //
    private static final int REQUEST_CODE_ADD_DEVICE_ADMIN = 10001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取设备管理服务
        devicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this,AdminReceiver.class);
        //判断是否有权限(激活了设备管理器)
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.lockNow();
            finish();
        }else {
            //没有权限，获取权限
            startAddDeviceAdminAty();
        }
        //放在最后，这样锁屏的时候不会闪一下
        setContentView(R.layout.activity_main);
        //锁屏之后就立即kill掉我们的Activity，避免资源的浪费;
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //获取权限
    private void startAddDeviceAdminAty(){
        //启动设备管理器(隐式Intent) 在AndroidMainfest.xml中设定相应过滤
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        //描述
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活后才能使用锁屏功能");
        startActivityForResult(intent,REQUEST_CODE_ADD_DEVICE_ADMIN);
    }

    //startActivityForResult中启动的活动结束后调用
    //判断权限是否获取成功，获取成功则锁屏并finish()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取权限成功
        if(requestCode == REQUEST_CODE_ADD_DEVICE_ADMIN &&
                resultCode == Activity.RESULT_OK){
            devicePolicyManager.lockNow();
            finish();
        }else{
            //权限获取失败，不再获取关闭程序
            finish();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}

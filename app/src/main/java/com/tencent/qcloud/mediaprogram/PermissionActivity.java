package com.tencent.qcloud.mediaprogram;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "PermissionActivity";

    private long lastPressTimestamp = System.currentTimeMillis() / 1000;
    private final byte PERMISSION_REQUEST_CODE = 1;
    private HomeKeyListener homeKeyListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        if(!hasPermissionGranted()){
            requestPermission();
        }else {
            Log.d(TAG, "已授权");
            jump2MainActivity();
        }
        registerHomeListener();
    }

    private boolean hasPermissionGranted(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }

    /**
     * 1) 第一次进入，则 显示允许或者不允许+是否不再提醒
     * 2）当第二次进入，即第一次不允许；
     * 若是第一次不允许且未勾选不再提示，则以第一次一样；
     * 若是第一次不允许且勾选了不再提示，则回调时，始终是不允许且不再有提示，且需要在app管理界面去授权
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE); //则弹出授权对话框,且不需要注意是否已授权了，因为已授权的则不会弹出框
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                boolean isAllPermissionGranted = true;
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        //Log.d(TAG, permissions[i] + "|" + grantResults[i]);
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            isAllPermissionGranted = false;
                            break;
                        }
                    }
                }else {
                   // Log.d(TAG, "must be judge");
                    isAllPermissionGranted = false;
                }
                if(isAllPermissionGranted){
                   // Log.d(TAG, "授权成功");
                    jump2MainActivity();
                }else {
                    //Log.e(TAG, "授权失败");
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        //再提示
                        Toast.makeText(this, "请授予该APP权限", Toast.LENGTH_SHORT)
                                .show();
                    }else {
                        //不再提示
                        Toast.makeText(this, "请到设置中授予该APP权限", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        long currentPressTimestamp = System.currentTimeMillis() / 1000;
        if(currentPressTimestamp - lastPressTimestamp > 2){
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT)
                    .show();
            lastPressTimestamp = currentPressTimestamp;
            return;
        }else {
            finish();
        }
        //super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "event =" + event.getAction());
        super.onTouchEvent(event);
        if(!hasPermissionGranted()){
            requestPermission();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterHomeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterHomeListener();
    }

    private void jump2MainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void jump2Setting(){
        Intent setIntent = new Intent();
        setIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        setIntent.addCategory(Intent.CATEGORY_DEFAULT);
        setIntent.setData(Uri.parse("package:" + this.getPackageName()));
        setIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        setIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        setIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(setIntent);
    }

    /**
     * handle pressed the key(Home)
     */
    private void registerHomeListener() {
        homeKeyListener = new HomeKeyListener(this);
        homeKeyListener
                .setOnHomePressedListener(new HomeKeyListener.OnHomePressedListener() {

                    @Override
                    public void onHomePressed() {
                        finish();
                    }

                    @Override
                    public void onHomeLongPressed() {
                    }
                });
        homeKeyListener.registerHomeReceive();
    }

    private void unRegisterHomeListener() {
        if (homeKeyListener != null) {
            homeKeyListener.unregisterHomeReceiver();
        }
        homeKeyListener = null;
    }
}

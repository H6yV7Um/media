package com.tencent.qcloud.mediaprogram;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("XIAO", "onCreate");
        setContentView(R.layout.activity_main);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.e("XIAO", "onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e("XIAO", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("XIAO", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("XIAO", "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}

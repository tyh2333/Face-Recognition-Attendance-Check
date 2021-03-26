package com.example.IOT_Proj1.face_check;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;

import com.example.IOT_Proj1.tools.MyHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn_search;
    Button btn_upload_face;
    Button btn_check;
    Button btn_delete;
    Button btn_out;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {   //onCreate()方法创建活动
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   //setContentView()方法给当前的活动加载布局
        btn_search = findViewById(R.id.search);   //findViewById()方法返回view对象，需要向下转型成Button对象
        btn_search.setOnClickListener(this);

        btn_upload_face =findViewById(R.id.upload_face);
        btn_upload_face.setOnClickListener(this);

        btn_check=findViewById(R.id.check);
        btn_check.setOnClickListener(this);

        btn_delete=findViewById(R.id.delete);
        btn_delete.setOnClickListener(this);

        btn_out=findViewById(R.id.out);
        btn_out.setOnClickListener(this);

//        MyHelper hhh = new MyHelper(MainActivity.this);
        readRequest();    //获取相机拍摄权限
    }


    @Override
    public void onClick(View v) {
        //  利用安卓显式intent切换活动: 第一个参数要求提供启动活动的上下文，第二个是指定启动的目标活动
        //  startActivity()启动当前活动
        if(v.getId()==R.id.search){
            Intent in = new Intent(this, SearchActivity.class);
            startActivity(in);
        }else if(v.getId()==R.id.upload_face){
            Intent in = new Intent(this, UploadFaceActivity.class);
            startActivity(in);
        }else  if(v.getId()==R.id.check){
            Intent in=new Intent(this, CheckActivity.class);
            startActivity(in);
        }else if(v.getId()==R.id.delete){
            Intent in=new Intent(this, DeleteActivity.class);
            startActivity(in);
        }else{
            System system = null;
            system.exit(0);
        }
    }

    void readRequest() {             // 能够拍照和读写SD卡的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
            }
        }
    }
}

package com.example.IOT_Proj1.face_check;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.IOT_Proj1.tools.Base64Util;
import com.example.IOT_Proj1.tools.GsonUtils;
import com.example.IOT_Proj1.tools.HttpUtil;
import com.example.IOT_Proj1.tools.MyHelper;
import com.example.IOT_Proj1.tools.toolsUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadFaceActivity extends AppCompatActivity implements View.OnClickListener {
    //进行必要的定义，注册需要上传用户名，名字和人脸照片等信息
    //定义登录的姓名和ID号
    String names, IDs;
    //定义登录按钮
    Button btn_Login;
    //定义输入的文本域
    EditText et_name, et_ID;
    //定义图片的路径
    private String imagePath = null;
    //定义图片的uri
    private Uri imageUri;
    //定义返回值，判断图片的来源
    private int Photo_ALBUM = 1, CAMERA = 2;
    int FLAG = 0;
    private Bitmap bp = null;   //定义一个空位图存储图片位置信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btn_Login = (Button) findViewById(R.id.btn_login);
        btn_Login.setOnClickListener(this);
        et_name = (EditText) findViewById(R.id.name);
        et_ID = (EditText) findViewById(R.id.ID);
        FLAG = 0;
    }
    // TODO : 检查昵称是否有效: 即是否为纯数字和字母的组合
    boolean check(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
                continue;
            else  return false;
        }
        return true;
    }
    // TODO： 上传按钮被点击：弹出对话框--> 选择上传照片的方式
    @Override
    public void onClick(View v) {
        names = et_name.getText().toString().trim();
        IDs = et_ID.getText().toString().trim();       //getText()方法输入文本
        if (names.equals("") || IDs.equals("")) {
            Toast.makeText(this, "ID and Name Required", Toast.LENGTH_SHORT).show();    //弹出Toast提示
        } else if (check(IDs) == false) {
            Toast.makeText(this, "ID format is not right", Toast.LENGTH_SHORT).show();
        } else {
            // 如果成功的话就设置对话框标题
            new AlertDialog.Builder(UploadFaceActivity.this)   //AlertDialog提供当前对话框
                    .setTitle("Upload face")
                    // 设置显示的内容
                    .setMessage("choose one way to upload the face img")
                    //设置对话框上的按钮
                    // TODO : return按钮
                    .setPositiveButton("Return",
                            new DialogInterface.OnClickListener() {  // 添加确定按钮
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {  // 确定按钮的响应事件
                                }
                            })
                    // TODO : 从相册上传按钮 :
                    .setNeutralButton("Upload From Album", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //选择数据
                            Intent in = new Intent(Intent.ACTION_PICK);
                            in.setType("image/*"); //设置选择的数据为图片
                            startActivityForResult(in, Photo_ALBUM);  //利用数据回转接收相册里的图片
                        }
                    })
                    // TODO : 拍照按钮 :
                    .setNegativeButton("Use Camera",
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    //7.0版本拍照必加如下代码
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    builder.detectFileUriExposure();

                                    //临时照片存储地
                                    File outputImage = new File(Environment.getExternalStorageDirectory()
                                            + File.separator + "face.jpg");
                                    try {
                                        if (outputImage.exists()) {
                                            outputImage.delete();
                                        }
                                        //创建临时地址
                                        outputImage.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //获取Uri
                                    imageUri = Uri.fromFile(outputImage);   //将file转成uri对象
                                    imagePath = outputImage.getAbsolutePath();

                                    //是传递你要保存的图片的路径，系统会根据你提供的地址进行保存图片
//                                    Log.i("拍照图片路径", imagePath);

                                    //跳转相机
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    //相片输出路径
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                    //返回照片路径
                                    startActivityForResult(intent, CAMERA);
                                }
                            }).show();// 在按键响应事件中显示此对话框
        }
    }

    /**
     * 进行数据回转，将uri传回上一个界面
     * 之后将图片传入服务器，来接受返回的信息
     * requestCode ： 启动活动时传入的请求码，
     * resultCode  ： 返回数据时传入的处理结果
     *    data     ： 携带着返回数据的intent
     **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 首先由第一个参数判断来源，第二个参数判断处理结果是否成功，最后从data中取值并打印
        // （1）相册选择图片
        if (requestCode == CAMERA) {
            bp = toolsUnit.getimage(imagePath);
            runthreaad();
        }
        else if (requestCode == Photo_ALBUM) {
            if (data != null) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                //获得图片的绝对路径
                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                cursor.close();
                bp = toolsUnit.getimage(imagePath);
                runthreaad();
            }
        }

    }

    void runthreaad() {   //新建线程
        new Thread(new Runnable() {
            @Override
            public void run() {   //开启
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add"; //人脸查找的地址
                try {  //将图片以二进制形式读取并转码为BASE64
                    byte[] bytes1 = toolsUnit.getBytesByBitmap(bp);
                    String image1 = Base64Util.encode(bytes1);
                    Map<String, Object> map = new HashMap<>();  //用MAP将要上传的信息来导入
                    map.put("image", image1);
                    map.put("group_id", "face");
                    map.put("user_id", IDs); /// 发送数据的时候带上ID
                    map.put("user_info", "abc");
                    map.put("liveness_control", "HIGH");
                    map.put("image_type", "BASE64");
                    map.put("quality_control", "HIGH");
//                    Gson new_gson = new GsonBuilder().create();
//                    String param = new_gson.toJson(map); //将map中的信息转为JSON格式，由于体积更小，方便在网络传输

                    String param = GsonUtils.toJson(map);
                    // 这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间，
                    // 客户端可自行缓存，过期后重新获取。

                    String clientId = "0VADVdH2CiNT4j1yE1pU6jNI"; // my AK
                    String clientSecret = "ntWSW7C2kDZ0AeBAmGni4GHGseQ3Gk6o"; // my SK
                    String accessToken = toolsUnit.getAuth(clientId, clientSecret);
                    String result = HttpUtil.post(url, accessToken, "application/json", param);

                    Gson gson = new Gson();   //利用GSON解析JSON的数据
                    // fromJosn(str, class the obj belongs to)
                    Multi_result_bean Result_bean = gson.fromJson(result, Multi_result_bean.class);
                    int Error_code = Result_bean.getError_code();  //获取返回值的错误码
                    if (Error_code == 0) {
                        SQLiteDatabase db;
                        MyHelper myhelper = new MyHelper(UploadFaceActivity.this);
                        db = myhelper.getWritableDatabase();
                        // 往 哪个数据库 + 哪个表 + 插入信息
                        myhelper.Insert(db, "name_id", names, IDs);   //利用数据库存储上传的names and ids
                        Looper.prepare();    //Looper类用来封装消息循环和消息队列，用在线程中进行消息处理
                        Toast.makeText(UploadFaceActivity.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {
                        String error_message = "Upload failed：" + Result_bean.getError_msg();
                        Looper.prepare();
                        Toast.makeText(UploadFaceActivity.this, error_message, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


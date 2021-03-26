package com.example.IOT_Proj1.face_check;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {
    private String ImagePath = null; //图片路径
    private Uri imageUri;//图片URI
    private int CAMERA = 1;
    private Bitmap bp = null;      //位图
    Button btn_camera;   //还是一个是相册选取，一个是拍照获取
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        btn_camera = (Button) findViewById(R.id.use_camera);
        btn_camera.setOnClickListener(this);
    }
    @SuppressLint("NewApi") // 屏蔽一切新api中才能使用的方法报的android lint错误
    @Override
    public void onClick(View v) {          //点击拍照，返回值为带地址的intent
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();            //7.0拍照必加
            File outputImage = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "face.jpg");     //临时照片存储地
            try {                               //文件分割符
                if (outputImage.exists()) {   //如果临时地址有照片，先清除
                    outputImage.delete();
                }
                outputImage.createNewFile();    ///创建临时地址
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageUri = Uri.fromFile(outputImage);              //获取Uri
            // 是传递你要保存的图片的路径，打开相机后，点击拍照按钮，系统就会根据你提供的地址进行保存图片
            ImagePath = outputImage.getAbsolutePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //跳转相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);            //相片输出路径
            startActivityForResult(intent, CAMERA);                        // call camera
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            bp = toolsUnit.getimage(ImagePath);
            runthread();  //开启线程，传入图片
    }

    void runthread() {   //新建线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search";
                try {
                    byte[] bytes1 = toolsUnit.getBytesByBitmap(bp);
                    String image1 = Base64Util.encode(bytes1);
                    Map<String, Object> map = new HashMap<>();
                    map.put("image", image1);
                    map.put("image_type", "BASE64");
                    map.put("group_id_list", "face");
                    map.put("max_face_num",3); //最多处理人脸的数目,最大值10
                    map.put("match_threshold", 80);
                    map.put("quality_control", "HIGH");
                    map.put("liveness_control", "HIGH");
                    map.put("max_user_num",1);
                    /*FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片
                    赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个。*/
//                    Gson new_gson = new GsonBuilder().create();
//                    String param = new_gson.toJson(map);
                    String param = GsonUtils.toJson(map);
                    String clientId = "0VADVdH2CiNT4j1yE1pU6jNI"; // my AK
                    String clientSecret = "ntWSW7C2kDZ0AeBAmGni4GHGseQ3Gk6o"; // my SK
                    String accessToken = toolsUnit.getAuth(clientId, clientSecret);
//                    Header: Content-Type: application/json;
                    String result = HttpUtil.post(url, accessToken, "application/json", param);
                    Gson gson = new Gson();
                    Multi_result_bean Result_bean = gson.fromJson(result, Multi_result_bean.class);
                    int Error_code = Result_bean.getError_code();
                    if (Error_code == 0) {
                        List<Multi_result_bean.FaceList.UserList> face_list = Result_bean.getResult().getFace_list();
                        String user = "";
//                        face_list 是返回人脸库中所有的比对结果，用id和score组成，然后比较是否有score大于阈值即可；
                        for (int i = 0; i < face_list.size(); i++){
                            if (face_list.get(i).getUser_list().size() != 0) { //判断是人脸是否在数据库中
                                double score = face_list.get(i).getUser_list().get(0).getScore(); //一层层进入，获取到score
                                user = face_list.get(i).getUser_list().get(0).getUser_id(); //获取用户名
                                if (score >= 78.0) { //分数大于78.0分，判断为同一个人，提示打卡成功
                                    SQLiteDatabase db;
                                    MyHelper myHelper = new MyHelper(CheckActivity.this);
                                    db = myHelper.getWritableDatabase();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
//                                    call insert_two in MyHelper to insert Check info into table: time-id;
                                    myHelper.Insert_two(db, "time_id", df.format(new Date()), user);
                                }
                            }
                        }
                        Looper.prepare();
                        Toast.makeText(CheckActivity.this, user + " Face Check Successfully！\n" +
                                "  You   Are   All   Set !   ", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {
                        String error_message = "Check Failed \n：" + Result_bean.getError_msg();
                        Looper.prepare();
                        Toast.makeText(CheckActivity.this, error_message, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

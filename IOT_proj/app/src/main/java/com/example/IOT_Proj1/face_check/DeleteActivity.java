package com.example.IOT_Proj1.face_check;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.IOT_Proj1.tools.HttpUtil;
import com.example.IOT_Proj1.tools.MyHelper;
import com.example.IOT_Proj1.tools.toolsUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
/*
*   activity_delete user in the database
*/
public class DeleteActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_confirm;
    EditText et_user;
    String S_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        btn_confirm = (Button)findViewById(R.id.confirm);
        btn_confirm.setOnClickListener(this);
        et_user=(EditText)findViewById(R.id.user);
    }

    @Override
    public void onClick(View v) {
       S_user = et_user.getText().toString().trim(); // get user to delete
       runthread();
    }

    void runthread()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url ="https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("group_id", "face");
                    map.put("user_id", S_user);
                    Gson new_gson = new GsonBuilder().create();
                    String param = new_gson.toJson(map);
                    /**
                     * 获取API访问token
                     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
                     * @param ak - 百度云官网获取的 API Key
                     * @param sk - 百度云官网获取的 Securet Key
                     * @return assess_token
                     */
                    // 百度云官网获取的 API Key
                    String clientId = "0VADVdH2CiNT4j1yE1pU6jNI"; // my AK
                    // 百度云官网获取的 Securet Key
                    String clientSecret = "ntWSW7C2kDZ0AeBAmGni4GHGseQ3Gk6o"; // my SK
                    String accessToken = toolsUnit.getAuth(clientId, clientSecret);
                    String result = HttpUtil.post(url, accessToken, "application/json", param);
                    System.out.println(result);

                    Gson gson=new Gson();
                    Multi_result_bean Result_bean = gson.fromJson(result,Multi_result_bean.class);
                    int Error_code = Result_bean.getError_code();
                    if(Error_code==0){
                        String message="id=\""+  S_user   +"\"";
                        SQLiteDatabase db;
                        MyHelper mh = new MyHelper(DeleteActivity.this);
                        db = mh.getWritableDatabase();
                        // 调用 MyHelper 中的delete方法
                        mh.Delete(db,"name_id",message);
                        Looper.prepare();
                        Toast.makeText(DeleteActivity.this,"Delete Successfully" , Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }else{
                        String message="id=\""+  S_user   +"\"";
                        SQLiteDatabase db;
                        MyHelper mh= new MyHelper(DeleteActivity.this);
                        db = mh.getWritableDatabase();
                        mh.Delete(db,"name_id",message);
                        String error_message="Delete Failed："+Result_bean.getError_msg();
                        Looper.prepare();
                        Toast.makeText(DeleteActivity.this,error_message , Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
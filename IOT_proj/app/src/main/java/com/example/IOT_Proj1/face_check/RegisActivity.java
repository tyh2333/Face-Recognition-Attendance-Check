package com.example.IOT_Proj1.face_check;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisActivity extends AppCompatActivity implements View.OnClickListener {
    // 定义注册需要用的组件：用户名，密码，电话号码: login in button, back button
    private EditText username;
    private EditText password;
    private EditText phone;
    LoginHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        db = new LoginHelper(RegisActivity.this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.phone);
        Button btn_logoin = (Button) findViewById(R.id.btn_logoin);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_logoin.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logoin:
                submit(); break;
            case R.id.btn_back:
                finish(); break;
        }
    }
//    (1) Check empty: check if Username, password, phone are all filled, if not, print warning!
//    (2) Check Dup: Cursor to traverse database , if not find , print successful;
    private void submit() {
        String usernameString = username.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();

        if (TextUtils.isEmpty(usernameString)) {
            Toast.makeText(this, "Username Required", Toast.LENGTH_SHORT).show(); return;
        }
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show(); return;
        }
        if (TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, "Tel Required", Toast.LENGTH_SHORT).show(); return;
        }

        // TODO : (1) validate failed : warning  (2) validate success : insert into database.
        Cursor cursor = db.selectLogin();
        while(cursor.moveToNext()){
            if (cursor.getString(0).equals(usernameString)){
                Toast.makeText(this, "user existed, Please use other names", Toast.LENGTH_SHORT).show();
                return; // return earlier;
            }
        }
        db.insertLogin(usernameString,passwordString,phoneString); // insert this new info to database
        Toast.makeText(this, "Register Successfully!", Toast.LENGTH_SHORT).show();
        cursor.close();
        finish();
    }
}
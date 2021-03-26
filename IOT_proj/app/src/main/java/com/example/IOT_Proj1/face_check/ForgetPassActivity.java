package com.example.IOT_Proj1.face_check;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class ForgetPassActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText phone;
    private Button btn_logoin;
    private Button btn_back;
    LoginHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        db=new LoginHelper(ForgetPassActivity.this);
        username = (EditText) findViewById(R.id.username);
        phone = (EditText) findViewById(R.id.phone);

        btn_logoin = (Button) findViewById(R.id.btn_logoin);
        btn_logoin.setOnClickListener(this);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logoin:
                submit();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void submit() {
//         username + tel : get password
        String usernameString = username.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            Toast.makeText(this, "Username Required", Toast.LENGTH_SHORT).show(); return;
        }
        String phoneString = phone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, "Tel number Required", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db.selectLogin();
        while(cursor.moveToNext()){
            if (cursor.getString(0).equals(usernameString) && cursor.getString(2).equals(phoneString)){
                String password = cursor.getString(1);
                Toast.makeText(this, "Username:"+usernameString+"\n Password:"+password, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, "User Doesn't Exist or Wrong Tel Number!", Toast.LENGTH_SHORT).show();
    }
}
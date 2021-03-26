package com.example.IOT_Proj1.face_check;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button btn_logoin;
    private TextView tv_register;
    private TextView tv_password;
    LoginHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("WelCome Professor! ");
        db = new LoginHelper(LoginActivity.this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_logoin = (Button) findViewById(R.id.btn_logoin);
        tv_register = (TextView) findViewById(R.id.tv_regier);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisActivity.class);
                startActivity(intent);
            }
        });
        tv_password = (TextView) findViewById(R.id.tv_passwd);
        tv_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });
        btn_logoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_logoin) {
            //  (1) check if filled both username and password:
            String usernameString = username.getText().toString().trim();
            if (TextUtils.isEmpty(usernameString)) {
                Toast.makeText(this, "Username Required ", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordString = password.getText().toString().trim();
            if (TextUtils.isEmpty(passwordString)) {
                Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show();
                return;
            }
            // （2） check if valid to log in, then do MainActivity
            Cursor cursor = db.selectLogin();
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(usernameString) && cursor.getString(1).equals(passwordString)) {
                    Toast.makeText(this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
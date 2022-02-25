package com.example.xiangcedemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity {

    private EditText et_name;
    private EditText et_pwd;
    private EditText et_repeatPwd;
    private Button bt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("注册");

        et_name = findViewById(R.id.et_name);
        et_pwd = findViewById(R.id.et_pwd);
        et_repeatPwd = findViewById(R.id.et_repeatPwd);
        bt_register = findViewById(R.id.bt_register);


    }
}
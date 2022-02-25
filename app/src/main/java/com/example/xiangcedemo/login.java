package com.example.xiangcedemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    SharedPreferences sp;
    private EditText et_name;
    private EditText et_pwd;
    private CheckBox cb_rememberpwd;
    private CheckBox cb_autologin;
    private Button bt_login;
    private Button bt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        initView();

        //TODO 回显数据

        boolean rememberpwd = sp.getBoolean("rememberpwd",false); //如果获取是空 则返回默认值
        boolean autologin = sp.getBoolean("autologin",false);

        //记住密码
        if (rememberpwd){
            //获取 sp 中的 name 和 pwd 并保存到Edittext
            String name = sp.getString("name","");
            String pwd = sp.getString("pwd","");
            et_name.setText(name);
            et_pwd.setText(pwd);
            cb_rememberpwd.setChecked(true);
        }

        //自动登录
        if (autologin){
            cb_autologin.setChecked(true);
            Toast.makeText(this, "自动登录成功", Toast.LENGTH_SHORT).show();
        }
    }

    //初始化
    private void initView() {
        et_name = findViewById(R.id.et_name);
        et_pwd = findViewById(R.id.et_pwd);
        cb_rememberpwd = findViewById(R.id.cb_rememberpwd);
        cb_autologin = findViewById(R.id.cb_autologin);
        bt_login = findViewById(R.id.bt_login);
        bt_register = findViewById(R.id.bt_register);

        MyOnClickListener l = new MyOnClickListener();

        bt_login.setOnClickListener(l);
        bt_register.setOnClickListener(l);
    }

    public class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_login:
                    //登陆操作
                    String name = et_name.getText().toString().trim();
                    String pwd = et_pwd.getText().toString().trim();
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)){
                        Toast.makeText(login.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //判断记住密码是否打勾
                        if (cb_rememberpwd.isChecked()){
                            //用户名和密码需要保存 同时 记住密码的状态也需保存
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("name",name);
                            editor.putString("pwd",pwd);
                            editor.putBoolean("rememberpwd",true);
                            editor.apply();

                        }

                        //判断自动登陆是否打勾
                        if (cb_autologin.isChecked()){
                            SharedPreferences.Editor editor = sp.edit();
                            //保存自动登录的状态
                            editor.putBoolean("autologin",true);
                            editor.apply();
                        }
                    }

                    Intent intent = new Intent();
                    intent.setClass(login.this, MainActivity.class);//从login页面跳转至MainActivity页面
                    startActivity(intent);
                    login.this.finish();

                    break;

                case R.id.bt_register:
                    //注册操作
                    break;

            }
        }
    }
}
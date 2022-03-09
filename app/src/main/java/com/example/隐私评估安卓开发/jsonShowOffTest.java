package com.example.隐私评估安卓开发;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class jsonShowOffTest extends AppCompatActivity {
    TextView tv;
    String apitest;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try{
                apitest = apiTest.getWeatherOfCity("西安");
//
            }
            catch (Exception e){
                e.printStackTrace();
            }

            System.out.println(apitest);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_show_off_test);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        Button apiInformation = (Button) findViewById(R.id.apiInformation);

        Intent intent=getIntent();
        String obj;
        obj = intent.getStringExtra("sss");
        tv = (TextView) findViewById(R.id.tv);
        tv.setText(ObjectanalysisTest(obj));

        apiInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载资源菜单
        getMenuInflater ().inflate (R.menu.menu_options,menu);  //第一个传入的参数是你创建的menu的名字
        return true;  //一定要return true 才会显示出来
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menu_about:
                Toast.makeText(this,"点击了关于",+Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_exit:
                Toast.makeText(this,"点击了退出",+Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_setting:
                Toast.makeText(this,"点击了设置",+Toast.LENGTH_SHORT).show();
                return true;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //解析json字符串
    private String ObjectanalysisTest(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String name = jsonObject.getString("name");
            int age = jsonObject.optInt("age");
            String sex = jsonObject.optString("sex");
            System.out.println("name：" + name + "  age：" + age + "  sex：" + sex);
            return "name：" + name + "  age：" + age + "  sex：" + sex;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
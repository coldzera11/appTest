package com.example.隐私评估安卓开发;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 2;
    private EditText et;
    private ImageView picture;
    private String imagePath = null;
    private String text = "";
    private JSONObject obj;

    private final Map<String, Object> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnChoosePhoto = (Button) findViewById(R.id.choose_photo);
        picture = (ImageView) findViewById(R.id.picture);
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //打开相册选择图片
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissino();
            }
        });


        et = findViewById(R.id.et);
        Button information = (Button) findViewById(R.id.information);
        Button parse = (Button) findViewById(R.id.parse);
        Button jsonStringCreat = (Button) findViewById(R.id.jsonStringCreat);

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = et.getText().toString();
                Log.e("leo", "输入的内容是: "+text);
            }
        });


        parse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(obj);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, jsonShowOffTest.class);//从MainActivity页面跳转至jsonShowOffTest页面
                intent.putExtra("sss",obj.toString());
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        jsonStringCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createJsonByMap();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载资源菜单
        getMenuInflater ().inflate (R.menu.menu_options,menu);  //第一个传入的参数是你创建的menu的名字
        return true;  //一定要return true 才会显示出来
    }

    //选择菜单
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

    //创造json字符串
    public void createJsonByMap() {
        data.put("xinxi", "test");
        data.put("name", "张三");
        data.put("age", 22);
        data.put("sex", "male");
//        data.put("is_student", true);
//        data.put("hobbies", new String[] {"hiking", "swimming"});

        obj = new JSONObject(data);
        System.out.println(obj);
        String result = obj.optString("name");
        System.out.println(result);

    }


    private void requestPermissino() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            openAlbum();
        }

    }

    private void openAlbum(){
        Log.d("tag","-----------执行了openAlbum");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO); //打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"你拒绝了该权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=13){
                        //4.1及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); //根据图片路径显示图片
    }

    //将选择的图片Uri转换为路径
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!= null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.e("zzz", "getImagePath: "+path);
        return path;
    }

    //展示图片
    private void displayImage(String imagePath){
        if(imagePath!=null && !imagePath.equals("")){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            //存储上次选择的图片路径，用以再次打开app设置图片
            SharedPreferences sp = getSharedPreferences("sp_img",MODE_PRIVATE);  //创建xml文件存储数据，name:创建的xml文件名
            SharedPreferences.Editor editor = sp.edit(); //获取edit()
            editor.putString("imgPath",imagePath);
            editor.apply();
        }else {
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置再次app时显示的图片
        SharedPreferences sp = getSharedPreferences("sp_img", MODE_PRIVATE);
        //取出上次存储的图片路径设置此次的图片展示
        String beforeImagePath = sp.getString("imgPath", null);
        displayImage(beforeImagePath);
    }

}


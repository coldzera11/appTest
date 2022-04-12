package com.example.隐私评估安卓开发;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TAKE = 1;
    private static final int REQUEST_CODE_CHOOSE = 0;
    private Uri imageUri;
    private ImageView ivAvatar;

    private EditText et;
    private String text = "";
    private JSONObject obj;

    private final Map<String, Object> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAvatar = findViewById(R.id.picture);
        et = findViewById(R.id.et);
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
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, personalSetting.class);
                startActivity(intent);
                MainActivity.this.finish();
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

        obj = new JSONObject(data);
        System.out.println(obj);
        String result = obj.optString("name");
        System.out.println(result);

    }


    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //执行拍照操作
            doTake();

        }else{
            //申请权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
    }

    private void doTake() {
        File imageTemp = new File(getExternalCacheDir(),"imageOut.jpeg");
        if (imageTemp.exists()){
            imageTemp.delete();
        }
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > 24){
            imageUri = FileProvider.getUriForFile(this,"com.example.隐私评估安卓开发.fileprovider",imageTemp);
        }else {
            imageUri = Uri.fromFile(imageTemp);
        }

        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,REQUEST_CODE_TAKE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                doTake();
            }else{
                Toast.makeText(this,"fail to get the permission for camera",Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == 0){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openAlbum();
            }else{
                Toast.makeText(this,"fail to get the permission for album",Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE){
            if (resultCode == RESULT_OK){
                //获取拍摄的照片
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ivAvatar.setImageBitmap(bitmap);

                    String imageToBase64 = imageUtil.imageToBase64(bitmap);
                    SharedPreferences sp = getSharedPreferences("sp_img",MODE_PRIVATE);  //创建xml文件存储数据，name:创建的xml文件名
                    SharedPreferences.Editor editor = sp.edit(); //获取edit()
                    editor.putString("imgPath",imageToBase64);
                    editor.apply();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_CODE_CHOOSE){
            handleImageOnApi19(data);

        }
    }

    @TargetApi(19)
    private void handleImageOnApi19(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String documentId = DocumentsContract.getDocumentId(uri);
            if (TextUtils.equals(uri.getAuthority(),"com.android.providers.media.documents")){
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);

            }else if (TextUtils.equals(uri.getAuthority(),"com.android.providers.downloads.documents")){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public downloads"),Long.valueOf(documentId));
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

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection){

        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null && !imagePath.equals("")){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ivAvatar.setImageBitmap(bitmap);

            String imageToBase64 = imageUtil.imageToBase64(bitmap);
            SharedPreferences sp = getSharedPreferences("sp_img",MODE_PRIVATE);  //创建xml文件存储数据，name:创建的xml文件名
            SharedPreferences.Editor editor = sp.edit(); //获取edit()
            editor.putString("imgPath",imageToBase64);
            editor.apply();

        }
    }


    public void choosePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            //打开相册
            openAlbum();

        }else{
            //申请权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }


    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_CHOOSE);

    }

    //再次启动程序时恢复上次选中的图片
    @Override
    protected void onResume() {
        super.onResume();
        //设置再次app时显示的图片
        SharedPreferences sp = getSharedPreferences("sp_img", MODE_PRIVATE);
        //取出上次存储的图片路径设置此次的图片展示
        if (sp.getString("imgPath", null) != null){
            String beforeImagePath = sp.getString("imgPath", null);

            Bitmap bitmap = imageUtil.base64toImage(beforeImagePath);
            ivAvatar.setImageBitmap(bitmap);
        }

    }

    //拿到输入的文本内容并且传输至后端 接收后端处理后的数据
    public void information(View view) {
        text = et.getText().toString();
        new Thread(){
            @Override
            public void run() {
                StringBuffer strBuf = new StringBuffer();
                String cityName = text;
                try {
                    String baiduUrl = "http://1.117.163.15:8080/login?username=coldzera&pwd=" + cityName;
                    URL url = new URL(baiduUrl);// 根据自己的服务器地址填写
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));// 转码。
                    String line = null;
                    while ((line = reader.readLine()) != null)
                        strBuf.append(line + "\n");
                    reader.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String value = strBuf.toString();
                // 匹配规则
                String reg = "<td>(.*)</td>";
                Pattern pattern = Pattern.compile(reg);

                // 内容 与 匹配规则 的测试
                Matcher matcher = pattern.matcher(value);

                String outBI = "";
                String outLOC = "";
                String outJOB = "";
                String outEDU = "";

                if( matcher.find() ){
                    // 包含前后的两个字符
//            System.out.println(matcher.group());
                    // 不包含前后的两个字符
                    outBI = matcher.group(1);
                    System.out.println(matcher.group(1));
                }
                if( matcher.find() ){
                    outLOC = matcher.group(1);
                    System.out.println(matcher.group(1));
                }
                if( matcher.find() ){
                    outJOB = matcher.group(1);
                    System.out.println(matcher.group(1));
                }
                if( matcher.find() ){
                    outEDU = matcher.group(1);
                    System.out.println(matcher.group(1));
                }
                et.setText("outBI: " + outBI + "\n"
                        + "outLOC:" + outLOC + "\n"
                        + "outJOB:" + outJOB + "\n"
                        + "outEDU:" + outEDU + "\n");
            }
        }.start();
    }
}


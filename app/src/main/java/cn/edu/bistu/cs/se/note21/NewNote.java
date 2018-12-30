package cn.edu.bistu.cs.se.note21;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.bistu.cs.se.note211.R;

public class NewNote extends AppCompatActivity {

    EditText headline;
    EditText new_content;
    EditText weath;
    Button save;
    Button cancel;
    Button take_photo;
    Button choose_photo;
    ImageView photo;
    static final int TAKE_POTHO=1;
    static final int CHOOSE_PHOTO=2;
    Uri uri;
    MyDataBase myDatabase;
    Notes notes;
    int ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        headline=(EditText) findViewById(R.id.headline);
        new_content=(EditText) findViewById(R.id.content);

        weath=(EditText)findViewById(R.id.weather);

        save=(Button) findViewById(R.id.saveButton);
        cancel=(Button)findViewById(R.id.Cancel);

        take_photo=(Button)findViewById(R.id.TakePhoto);
        photo=(ImageView)findViewById(R.id.photo);
        choose_photo=(Button) findViewById(R.id.ChoosePhoto);
        myDatabase=new MyDataBase(this);
        Intent intent=this.getIntent();
        ids=intent.getIntExtra("ids", 0);//默认为0，不为0,则为修改数据时跳转过来的
        if(ids!=0){
            notes=myDatabase.getTiandCon(ids);
            headline.setText(notes.getTitle());
            new_content.setText(notes.getContent());
            SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
            weath.setText(preferences.getString(String.valueOf(notes.getIds()),""));
            photo.setImageBitmap(stringToBitmap1(notes.getPicture()));
        }//保存按钮的点击事件，他和返回按钮是一样的功能，所以都调用isSave()方法；
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                photo.setDrawingCacheEnabled(true);
                isSave();
                photo.setDrawingCacheEnabled(false);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewNote.this,MainActivity.class);
                startActivity(intent);
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outImage.exists()) {
                        outImage.delete();
                    }
                    outImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                if(Build.VERSION.SDK_INT>=24) {
                    uri= FileProvider.getUriForFile(NewNote.this,"com.example.gdzc.cameraalbumtest.fileprovider",outImage);
                } else {
                    uri=Uri.fromFile(outImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent,TAKE_POTHO);
            }
        });
        choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(NewNote.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(NewNote.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
    }
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    /*
     * 返回按钮调用的方法。
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
            case TAKE_POTHO:
                if(resultCode==RESULT_OK) {
                    try{
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        photo.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    public void onRequestPermissionsResult(int requestCode,String[] permission,int [] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"Yon denied the perission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+ "=" +id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/pubic_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            photo.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    /* @Override
    public void onBackPressed() {
        new AlertDialog.Builder(NewNote.this)
                .setTitle("保存")
                .setMessage("是否保存笔记")
                .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(NewNote.this,MainActivity.class);
                        startActivity(intent);
                        NewNote.this.finish();
                    }
                })
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       isSave();
                    }
                })
                .create().show();
    }

    */
    private void isSave(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String times = formatter.format(curDate);
        String title = headline.getText().toString();
        String content = new_content.getText().toString();//是要修改数据
        String weather = weath.getText().toString();
        //photo.setDrawingCacheEnabled(true);
        Bitmap bitmap = photo.getDrawingCache();
       // photo.setDrawingCacheEnabled(false);
        String picture = bitmapToString1(bitmap);
        if(ids!=0){
            notes=new Notes(ids, title,content, times,picture);
            myDatabase.toUpdate(notes);
            Intent intent = new Intent(NewNote.this,MainActivity.class);
            startActivity(intent);
            NewNote.this.finish();
        } else{//新建日记
            notes=new Notes(title,content,times,picture);
            myDatabase.toInsert(notes);
            Intent intent=new Intent(NewNote.this,MainActivity.class);
            startActivity(intent);
            NewNote.this.finish();
        }
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString(String.valueOf(notes.getIds()),weather);
        editor.apply();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }
    public static String bitmapToString1(Bitmap bitmap){
        //用户在活动中上传的图片转换成String进行存储
        String string;
        if(bitmap!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();// 转为byte数组
            string=Base64.encodeToString(bytes,Base64.DEFAULT);
            return string;
        }
        else{
            return "";
        }
    }

    public static Bitmap stringToBitmap1(String string){
        //数据库中的String类型转换成Bitmap
        Bitmap bitmap;
        if(string!=null){
            byte[] bytes= Base64.decode(string,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }
        else {
            return null;
        }
    }
}



package cn.edu.bistu.cs.se.note21;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MyDataBase {

    Context context;
    MyOpenHelper myHelper;
    SQLiteDatabase myDatabase;
    //创建数据库/
    public MyDataBase(Context con){
        this.context=con;
        myHelper=new MyOpenHelper(context);
    }
    /*
     * 得到ListView的数据，从数据库里查找后解析
     */
    public ArrayList<Notes> getArray(){
        ArrayList<Notes> array = new ArrayList<Notes>();
        ArrayList<Notes> array1 = new ArrayList<Notes>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select ids,title,content,times,picture from mydate" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex("ids"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String times = cursor.getString(cursor.getColumnIndex("times"));
            String picture = cursor.getString(cursor.getColumnIndex("picture"));
            Notes notes = new Notes(id, title,content,times,picture);
            array.add(notes);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i >0; i--) {
            array1.add(array.get(i-1));
        }
        return array1;
    }
    public ArrayList<Notes> findArray(String str){
        ArrayList<Notes> array = new ArrayList<Notes>();
        ArrayList<Notes> array1 = new ArrayList<Notes>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select ids,title,content,times,picture from mydate where title like '%"+str+"%'" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex("ids"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String times = cursor.getString(cursor.getColumnIndex("times"));
            String picture = cursor.getString(cursor.getColumnIndex("picture"));
            Notes notes = new Notes(id, title,content,times,picture);
            array.add(notes);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i >0; i--) {
            array1.add(array.get(i-1));
        }
        return array1;
    }
    /*
     * 返回可能要修改的数据
     */
    public Notes getTiandCon(int id){
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select title,content,picture from mydate where ids='"+id+"'" , null);
        cursor.moveToFirst();
        String title=cursor.getString(cursor.getColumnIndex("title"));
        String content=cursor.getString(cursor.getColumnIndex("content"));
        String picture = cursor.getString(cursor.getColumnIndex("picture"));
        Notes notes=new Notes(id,title,content,picture);
        myDatabase.close();
        return notes;
    }

    /*
     * 用来修改日记
     */
    public void toUpdate(Notes notes){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL(
                "update mydate set title='"+ notes.getTitle()+
                        "',times='"+notes.getTimes()+
                        "',content='"+notes.getContent() +
                        "',picture='"+notes.getPicture() +
                        "' where ids='"+ notes.getIds()+"'");
        myDatabase.close();
    }

    /*
     * 用来增加新的日记
     */
    public void toInsert(Notes notes){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into mydate(title,content,times,picture)values('"
                + notes.getTitle()+"','"
                +notes.getContent()+"','"
                +notes.getTimes()+"','"
                +notes.getPicture()
                +"')");
        myDatabase.close();
    }

    /*
     * 长按点击后选择删除日记
     */
    public void toDelete(int ids){
        myDatabase  = myHelper.getWritableDatabase();
        myDatabase.execSQL("delete from mydate where ids="+ids+"");
        myDatabase.close();
    }
}

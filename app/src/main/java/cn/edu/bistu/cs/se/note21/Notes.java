package cn.edu.bistu.cs.se.note21;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Notes {
    private String title;   //标题
    private String content; //内容
    private String times;   //时间
    private int ids;        //编号
    private String picture;//图片
    private String weather;


    public Notes(int id,String title,String content,String times,String picture){
        this.ids=id;
        this.title=title;
        this.content=content;
        this.times=times;
        this.picture = picture;
    }
    public Notes(int id,String title,String content,String picture){
        this.ids=id;
        this.title=title;
        this.content=content;
        this.picture = picture;
    }
    public Notes(String title,String content,String times,String picture){
        this.title=title;
        this.content=content;
        this.times=times;
        this.picture = picture;
    }
    public int getIds() {
        return ids;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTimes() {
        return times;
    }

    public String getPicture(){ return picture; }

    public String getWeather(){ return weather; }

    public void setWeather(String weather){ this.weather = weather; }

 /*   public String getWeather(){
        SharedPreferences preferences = SharedPreferences.getSharedPreferences("data",MODE_PRIVATE);
    }*/
}
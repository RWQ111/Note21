package cn.edu.bistu.cs.se.note21;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.bistu.cs.se.note211.R;


public class MyAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<Notes> array;
    public MyAdapter(LayoutInflater inf,ArrayList<Notes> arry){
        this.inflater=inf;
        this.array=arry;
    }

    @Override
    public int getCount() {
        return array.size();
    }
    @Override
    public Object getItem(int position) {
        return array.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView=inflater.inflate(R.layout.list_item, null);
            vh.tit=(TextView) convertView.findViewById(R.id.title);
            vh.tim=(TextView) convertView.findViewById(R.id.time);
      //      vh.con=(TextView) convertView.findViewById(R.id.input);
            vh.pic=(ImageView)convertView.findViewById(R.id.picture);
            vh.wea=(TextView)convertView.findViewById(R.id.weather);
            convertView.setTag(vh);
        }
        vh=(ViewHolder) convertView.getTag();
        vh.tit.setText(array.get(position).getTitle());
        vh.tim.setText(array.get(position).getTimes());
   //     vh.con.setText(array.get(position).getContent());
        vh.pic.setImageBitmap(stringToBitmap(array.get(position).getPicture()));
        vh.wea.setText(array.get(position).getWeather());
        return convertView;
    }
    class ViewHolder{
        TextView tit,tim,wea;//con;
        ImageView pic;

    }
    public static Bitmap stringToBitmap(String string){
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
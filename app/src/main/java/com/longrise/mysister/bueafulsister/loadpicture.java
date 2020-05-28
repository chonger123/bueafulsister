package com.longrise.mysister.bueafulsister;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ${Chonger} on 2020/5/28 0028.
 */

public class loadpicture implements Handler.Callback{
    private Context context;
    private ImageView imageView;
    private String imgurl;
    private byte[] imgstream;
    public Handler handler;
    public loadpicture(Context context){
        this.context = context;
        handler= new Handler(this);
    }
    public void loaddata(ImageView loadImg, String imgUrl){
        this.imageView = loadImg;
        this.imgurl = imgUrl;
        Drawable drawable = imageView.getDrawable();
        if(null != drawable && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(null != bitmap && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
        getThearddata();
    }

    private void getThearddata(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url  = new URL(imgurl);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    if(connection.getResponseCode() == 200){
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int length = -1;
                        while ((length =inputStream.read(bytes)) != -1){
                            byteArrayOutputStream.write(bytes,0,length);
                        }
                        imgstream = byteArrayOutputStream.toByteArray();
                        inputStream.close();
                        byteArrayOutputStream.close();
                        handler.sendEmptyMessage(0x123);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == 0x123){
            if(imgstream != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgstream,0,imgstream.length);
                imageView.setImageBitmap(bitmap);
            }
        }
        return false;
    }
}

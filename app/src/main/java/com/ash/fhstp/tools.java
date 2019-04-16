package com.ash.fhstp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class tools {
    static String LoadData(String inFile, Activity act) {
        // load Text data from assets
        String tContents = "";
        try {
            InputStream stream = act.getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (Exception e) {
            //
        }
        return tContents;
    }
    static String extract(StringBuilder text, String from, String to){
        // extract String
        int indx = text.indexOf(from);
        if(indx<0)return "";
        text = text.delete(0, indx + from.length());
        indx = text.indexOf(to);
        if(indx<0)return "";
        return text.substring(0,indx);
    }
    static String extract(String text, String from, String to){
        // extract String
        int indx = text.indexOf(from);
        if(indx<0)return "";
        text = text.substring(indx + from.length());
        indx = text.indexOf(to);
        if(indx<0)return "";
        return text.substring(0,indx);
    }
    static String getLang(Context context){
        // load main language
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("lang", "en");
    }
    static void setLang(Context context, String lang){
        // set main language
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lang",lang);
        editor.apply();
    }
    static boolean isDbOk(Context context){
        // check if database exists
        String appDataPath = context.getApplicationInfo().dataDir;
        File file = new File(appDataPath + "/databases/stpolten.db");
        return file.exists();
    }
    static void createDatabase(Context context){
        // restore database
        String appDataPath = context.getApplicationInfo().dataDir;
        File dbFolder = new File(appDataPath + "/databases");
        dbFolder.mkdir();
        File dbFile = new File(appDataPath + "/databases/stpolten.db");
        try {
            InputStream inputStream = context.getAssets().open("stpolten.db");
            OutputStream outputStream = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))>0)
            {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e){
            //
        }
    }
    static SQLiteDatabase getDb(Context context){
        // get or create database
        return SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("stpolten.db"),
                null);
    }
    static Bitmap loadAssetBitmap(Context context, String path) throws Exception{
        // load Bitmap asset
        InputStream is = context.getAssets().open(path);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }
}

interface MyEvent {
    void doThis();
}
class downloadText extends AsyncTask<String, String, String> {
    // download Text async
    private MyEvent eventHandler;
    boolean error;
    StringBuilder response;
    @Override
    protected String doInBackground(String... _url) {
        int count;
        error = true;
        try {
            URL url = new URL(_url[0]+"?_=" + System.currentTimeMillis());
            HttpsURLConnection connection = ( HttpsURLConnection ) url.openConnection();
            //URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            connection.setUseCaches(false);
            connection.connect();
            InputStream input = url.openStream();
            byte data[] = new byte[1000];
            response = new StringBuilder();
            while ((count = input.read(data)) >= 0) {
                response.append(new String(data, 0, count, "UTF-8"));
            }
            input.close();
            error = response.length() == 0;

        } catch (Exception e) {
            //Log.i("AAAA", e.getMessage());
            error = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(eventHandler!=null)eventHandler.doThis();
    }
    void onComplete(MyEvent ev) {
        this.eventHandler = ev;
    }
    public String getString(){
        return this.response.toString();
    }
}
class downloadImg extends AsyncTask<String, String, String> {
    // download Image async
    private MyEvent eventHandler;
    boolean error;
    private Bitmap btmp;
    String URL;
    downloadImg(String URL){
        this.URL = URL;
    }
    @Override
    protected String doInBackground(String... _url) {
        int count;
        error = true;
        try {
            URL url = new URL(_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream input = url.openStream();
            this.btmp = BitmapFactory.decodeStream(input);
            error = false;
            input.close();

        } catch (Exception e) {
            error = true;
        }
        return null;
    }
    @Override
    protected void onPostExecute(String file_url) {
        if(eventHandler!=null)eventHandler.doThis();
    }
    void onComplete(MyEvent ev) {
        this.eventHandler = ev;
    }
    Bitmap getBitmap(){
        return this.btmp;
    }
}
class downloadImgs extends AsyncTask<Void, Void, Void> {
    // download multiple Images async
    private MyEvent eventHandler;
    boolean error;
    private ArrayList<downloadImg> imgs;
    private int doneCnt;
    downloadImgs(){
        doneCnt = 0;
        imgs = new ArrayList<downloadImg>();
    }
    void addImg(String url){
        final downloadImg img = new downloadImg(url);
        img.onComplete(new MyEvent() {
            @Override
            public void doThis() {
                if(img.error)
                    error = true;
                else
                    doneCnt++;
                if(doneCnt>=imgs.size() && eventHandler!=null)
                    eventHandler.doThis();
            }
        });
        this.imgs.add(img);
    }
    public int getCnt(){
        return this.imgs.size();
    }
    downloadImg getImg(int id){
        return this.imgs.get(id);
    }
    @Override
    protected void onPreExecute() {
        error = false;
        this.doneCnt = 0;
    }
    @Override
    protected Void doInBackground(Void... arg) {
        for(int i=0;i<imgs.size();i++) {
            downloadImg img = imgs.get(i);
            img.execute(img.URL);
        }
        return null;
    }
    void onComplete(MyEvent ev) {
        this.eventHandler = ev;
    }
}
class loadWebAsset extends AsyncTask<String, String, String> {
    // load asset async
    @SuppressLint("StaticFieldLeak")
    private Activity act;
    private MyEvent eventHandler;
    boolean error;
    private String response;
    @Override
    protected String doInBackground(String... _url) {
        error = true;
        try {
            this.response = tools.LoadData(_url[0],this.act);
            error = response.length() == 0;

        } catch (Exception e) {
            error = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {
        if(eventHandler!=null)eventHandler.doThis();
    }
    void setAct(Activity act){this.act=act;}
    void onComplete(MyEvent ev) {
        this.eventHandler = ev;
    }
    public String getString(){
        return this.response;
    }
}

package com.ash.fhstp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Locale;

public class StudyDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        // load language
        String lang = tools.getLang(getBaseContext());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        // set title
        setTitle(getText(R.string.app_name));
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_details);
        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_chevron_leftdp);
        // create loading dialog
        final fhstpLoading dialog = new fhstpLoading(StudyDetails.this);
        // async asset loader
        final loadWebAsset loader = new loadWebAsset();
        loader.setAct(this);
        loader.onComplete(new MyEvent() {
            @Override
            public void doThis() {
                if(loader.error){
                    // Error
                }else{
                    WebView wv = findViewById(R.id.wv_studyDetails);
                    wv.setWebViewClient(new WebViewClient(){
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            dialog.dismiss();
                        }
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            return true;
                        }
                    });
                    wv.loadData(loader.getString(), "text/html; charset=utf-8", "utf-8");
                }
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                loader.execute(getIntent().getExtras().getString("name"));
            }
        });
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.ash.fhstp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.res.Configuration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load language
        lang = tools.getLang(getBaseContext());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        // set main title
        setTitle(getText(R.string.app_name));
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set main toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menu_icon);
        // create a loading dialog
        final fhstpLoading dialog = new fhstpLoading(MainActivity.this);
        dialog.show();
        // Load online news
        final String _url = getText(R.string.news_url).toString();
        //Log.i("AAAA",_url);
        final downloadText dt = new downloadText();
        dt.onComplete(new MyEvent() {
            @Override
            public void doThis() {
                final LinearLayout newsBox = findViewById(R.id.news_list);
                final LayoutInflater indlater = LayoutInflater.from(MainActivity.this.getApplicationContext());
                if(dt.error) {
                    // Cannot load
                    View row = indlater.inflate(R.layout.news_item, newsBox, false);
                    ((ImageView)row.findViewById(R.id.news_img)).setImageResource(R.drawable.ic_noimg);
                    ((TextView)row.findViewById(R.id.news_title)).setText(getText(R.string.connectfailed));
                    row.findViewById(R.id.news_date).setVisibility(View.INVISIBLE);
                    newsBox.addView(row);
                    dialog.dismiss();
                }else{
                    // Load images and show
                    final downloadImgs images = new downloadImgs();
                    final ArrayList<String[]> newsRows = new ArrayList<>();
                    String temp;
                    while(true){
                        temp = tools.extract(dt.response, "<article class=\"c-card","</article>");
                        if(temp.equals(""))break;
                        String imgUrl = tools.extract(temp,"<img class=\"progressive-media-image\"",">");
                        imgUrl = tools.extract(imgUrl,"data-src=\"","\"");
                        String newsTitle = tools.extract(temp,"<a class=\"h-text__color--primary\"","/a>");
                        String newsUrl = "https://www.fhstp.ac.at/" + tools.extract(newsTitle,"href=\"","\"");
                        newsTitle = tools.extract(newsTitle,">","<");
                        String newsDate = tools.extract(temp,"itemprop=\"datePublished\">","</time>");
                        //
                        newsRows.add(new String[] {newsTitle,newsUrl,newsDate,imgUrl});
                        images.addImg(imgUrl);
                    }
                    images.onComplete(new MyEvent() {
                        @Override
                        public void doThis() {
                            for(int i=0;i<newsRows.size();i++) {
                                final String nRow[] = newsRows.get(i);
                                View row = indlater.inflate(R.layout.news_item, newsBox, false);
                                ImageView _img = row.findViewById(R.id.news_img);
                                TextView _title = row.findViewById(R.id.news_title);
                                TextView _date = row.findViewById(R.id.news_date);
                                LinearLayout _btn = row.findViewById(R.id.news_button);
                                _title.setText(nRow[0]);
                                _date.setText(nRow[2]);
                                _btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(nRow[1]));
                                        startActivity(i);
                                    }
                                });
                                if(images.error){
                                    // Cannot load
                                    _img.setImageResource(R.drawable.ic_noimg);
                                }else{
                                    // Done
                                    _img.setImageBitmap(images.getImg(i).getBitmap());
                                }
                                newsBox.addView(row);
                            }
                            dialog.dismiss();
                        }
                    });
                    images.execute();
                }

            }
        });
        dt.execute(_url);
        // load button click animation
        final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click_anim);
        // social and contact buttons
        findViewById(R.id.social_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/fhstp"));
                startActivity(i);
            }
        });
        findViewById(R.id.social_linkedin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/edu/school?id=10171"));
                startActivity(i);
            }
        });
        findViewById(R.id.social_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://twitter.com/fh_stpoelten"));
                startActivity(i);
            }
        });
        findViewById(R.id.social_youtube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.youtube.com/user/FHStPoelten"));
                startActivity(i);
            }
        });
        findViewById(R.id.social_instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.instagram.com/fhstp/"));
                startActivity(i);
            }
        });
        findViewById(R.id.make_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:+432742313228200"));
                startActivity(i);
            }
        });
        findViewById(R.id.send_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","csc@fhstp.ac.at", null));
                startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
            }
        });
        // create main database if it doesn't exist
        if(!tools.isDbOk(getBaseContext()))
            tools.createDatabase(getBaseContext());
        // config drawer and its items
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        switch(menuItem.getItemId()){
                            case R.id.nav_de:
                            case R.id.nav_en:
                                String newlang = menuItem.getItemId()==R.id.nav_de?"de":"en";
                                if(lang.equals(newlang))break;
                                tools.setLang(MainActivity.this, newlang);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(getString(R.string.language_changed));
                                builder.setCancelable(false);
                                builder.setIcon(android.R.drawable.ic_dialog_info);
                                builder.setTitle(getString(R.string.menu_lang));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                drawerLayout.closeDrawers();
                                return true;
                            case R.id.nav_fields:
                                startActivity(new Intent(getBaseContext(), FieldsOfStudy.class));
                                return true;
                            case R.id.nav_staff:
                                intent = new Intent(getBaseContext(), StaffList.class);
                                intent.putExtra("table", "");
                                intent.putExtra("title", "");
                                startActivity(intent);
                                return true;
                            case R.id.nav_courses:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "courses_" + lang);
                                intent.putExtra("title", getString(R.string.menu_courses));
                                intent.putExtra("icon", R.drawable.ic_courses);
                                startActivity(intent);
                                return true;
                            case R.id.nav_deps:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "departments_" + lang);
                                intent.putExtra("title", getString(R.string.menu_deps));
                                intent.putExtra("icon", R.drawable.ic_departments);
                                startActivity(intent);
                                return true;
                            case R.id.nav_divisions:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "divisions_" + lang);
                                intent.putExtra("title", getString(R.string.menu_divisions));
                                intent.putExtra("icon", R.drawable.ic_divisions);
                                startActivity(intent);
                                return true;
                            case R.id.nav_inst:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "institute_" + lang);
                                intent.putExtra("title", getString(R.string.menu_inst));
                                intent.putExtra("icon", R.drawable.ic_institutes);
                                startActivity(intent);
                                return true;
                            case R.id.nav_progs:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "programs_" + lang);
                                intent.putExtra("title", getString(R.string.menu_progs));
                                intent.putExtra("icon", R.drawable.ic_programs);
                                startActivity(intent);
                                return true;
                            case R.id.nav_projects:
                                intent = new Intent(getBaseContext(), ListViewer.class);
                                intent.putExtra("table", "projects_" + lang);
                                intent.putExtra("title", getString(R.string.menu_projects));
                                intent.putExtra("icon", R.drawable.ic_projects);
                                startActivity(intent);
                                return true;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}

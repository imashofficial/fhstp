package com.ash.fhstp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class StaffActivity extends AppCompatActivity {

    private int code;
    private String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load language
        this.lang = tools.getLang(getBaseContext());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        String title = getIntent().getStringExtra("title");
        // set title
        setTitle(title.equals("") ? getText(R.string.menu_staff) : title);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_chevron_leftdp);
        // load intent variables
        this.code = getIntent().getIntExtra("code", 1);
        // select staff record from database
        SQLiteDatabase db = tools.getDb(getBaseContext());
        final Cursor staff = db.rawQuery("SELECT trim(nameprefix || ' ' || fname || ' ' || lname || ' ' || namesuffix) as name, * FROM staff WHERE code=" + this.code, null);
        staff.moveToFirst();
        // load staff photo
        ImageView iv = findViewById(R.id.staff_photo);
        try {
            iv.setImageBitmap(tools.loadAssetBitmap(getBaseContext(), "photos/" + this.code + ".jpg"));
        }
        catch(Exception ex){
            iv.setImageResource(R.drawable.ic_noimg);
        }
        // set name and jobtitle
        TextView tv = findViewById(R.id.staff_name);
        tv.setText(staff.getString(staff.getColumnIndex("name")));
        TextView jt = findViewById(R.id.staff_jobtitle);
        jt.setText(staff.getString(staff.getColumnIndex("jobtitle_"+this.lang)));
        // retrieve and set staff info
        Cursor dataset;
        int rowCount;
        StringBuilder text;
        // departments
        TextView department_header = findViewById(R.id.staff_department_header);
        TextView department = findViewById(R.id.staff_department);
        dataset = db.rawQuery("SELECT * FROM departments_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            department.setText(text.toString().trim());
        }else{
            department_header.setVisibility(View.GONE);
            department.setVisibility(View.GONE);
        }
        dataset.close();
        // divisions
        TextView division_header = findViewById(R.id.staff_division_header);
        TextView division = findViewById(R.id.staff_division);
        dataset = db.rawQuery("SELECT * FROM divisions_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            division.setText(text.toString().trim());
        }else{
            division_header.setVisibility(View.GONE);
            division.setVisibility(View.GONE);
        }
        dataset.close();
        // institutes
        TextView institutes_header = findViewById(R.id.staff_institutes_header);
        TextView institutes = findViewById(R.id.staff_institutes);
        dataset = db.rawQuery("SELECT * FROM institute_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            institutes.setText(text.toString().trim());
        }else{
            institutes_header.setVisibility(View.GONE);
            institutes.setVisibility(View.GONE);
        }
        dataset.close();
        // programs
        TextView programs_header = findViewById(R.id.staff_programs_header);
        TextView programs = findViewById(R.id.staff_programs);
        dataset = db.rawQuery("SELECT * FROM programs_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            programs.setText(text.toString().trim());
        }else{
            programs_header.setVisibility(View.GONE);
            programs.setVisibility(View.GONE);
        }
        dataset.close();
        // courses
        TextView courses_header = findViewById(R.id.staff_courses_header);
        TextView courses = findViewById(R.id.staff_courses);
        dataset = db.rawQuery("SELECT * FROM courses_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            courses.setText(text.toString().trim());
        }else{
            courses_header.setVisibility(View.GONE);
            courses.setVisibility(View.GONE);
        }
        dataset.close();
        // bio
        TextView bio_header = findViewById(R.id.staff_bio_header);
        TextView bio = findViewById(R.id.staff_bio);
        dataset = db.rawQuery("SELECT * FROM bio_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            text = new StringBuilder();
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                text.append("• " + dataset.getString(dataset.getColumnIndex("name")) + "\n");
            }
            bio.setText(text.toString().trim());
        }else{
            bio_header.setVisibility(View.GONE);
            bio.setVisibility(View.GONE);
        }
        dataset.close();
        //
        final LayoutInflater indlater = LayoutInflater.from(getBaseContext());
        // projects
        TextView projects_header = findViewById(R.id.staff_projects_header);
        final LinearLayout projects = findViewById(R.id.staff_projects);
        dataset = db.rawQuery("SELECT * FROM projects_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                View row = indlater.inflate(R.layout.simple_item_with_photo, projects, false);
                ((TextView)row.findViewById(R.id.item_title)).setText(
                        dataset.getString(dataset.getColumnIndex("name")));
                ((ImageView)row.findViewById(R.id.item_img)).setImageResource(R.drawable.ic_projects);
                final String url = dataset.getString(dataset.getColumnIndex("url"));
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                projects.addView(row);
            }
        }else{
            projects_header.setVisibility(View.GONE);
            projects.setVisibility(View.GONE);
        }
        dataset.close();
        // links
        final LinearLayout links = findViewById(R.id.staff_links);
        View row = indlater.inflate(R.layout.simple_item_with_photo, links, false);
        ((TextView)row.findViewById(R.id.item_title)).setText(getString(R.string.online_profile));
        ((ImageView)row.findViewById(R.id.item_img)).setImageResource(R.mipmap.ic_launcher);
        final String url1 = staff.getString(staff.getColumnIndex("onlineprofile_" + this.lang));
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url1));
                startActivity(i);
            }
        });
        links.addView(row);
        final String url2 = staff.getString(staff.getColumnIndex("researchprofile_" + this.lang)).trim();
        if(!url2.equals("")){
            row = indlater.inflate(R.layout.simple_item_with_photo, links, false);
            ((TextView)row.findViewById(R.id.item_title)).setText(getString(R.string.research_profile));
            ((ImageView)row.findViewById(R.id.item_img)).setImageResource(R.mipmap.ic_launcher);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url2));
                    startActivity(i);
                }
            });
            links.addView(row);
        }
        final String url3 = staff.getString(staff.getColumnIndex("cv_" + this.lang)).trim();
        if(!url3.equals("")){
            row = indlater.inflate(R.layout.simple_item_with_photo, links, false);
            ((TextView)row.findViewById(R.id.item_title)).setText(getString(R.string.cv));
            ((ImageView)row.findViewById(R.id.item_img)).setImageResource(R.drawable.ic_cv);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url3));
                    startActivity(i);
                }
            });
            links.addView(row);
        }
        dataset = db.rawQuery("SELECT * FROM links_" + this.lang + " WHERE code=" +
                this.code, null);
        rowCount = dataset.getCount();
        if(rowCount>0){
            for(int i=0;i<rowCount;i++){
                dataset.moveToPosition(i);
                row = indlater.inflate(R.layout.simple_item_with_photo, links, false);
                ((TextView)row.findViewById(R.id.item_title)).setText(
                        dataset.getString(dataset.getColumnIndex("name"))
                );
                ((ImageView)row.findViewById(R.id.item_img)).setImageResource(R.drawable.ic_links);
                final String url = dataset.getString(dataset.getColumnIndex("url"));
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                links.addView(row);
            }
        }
        dataset.close();
        // contact
        TextView contact = findViewById(R.id.staff_contact);
        text = new StringBuilder();
        String temp = staff.getString(staff.getColumnIndex("phone_txt")).trim();
        if(!temp.equals("")) text.append("T: " + temp + "\n");
        temp = staff.getString(staff.getColumnIndex("mobile_txt")).trim();
        if(!temp.equals("")) text.append("M: " + temp + "\n");
        temp = staff.getString(staff.getColumnIndex("emailaddress")).trim();
        text.append(temp);
        contact.setText(text.toString().trim());
        // load button click animation
        final Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.click_anim);
        // set social and contact buttons
        findViewById(R.id.make_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+staff.getString(staff.getColumnIndex("phone_no"))));
                startActivity(i);
            }
        });
        findViewById(R.id.send_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(anim);
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",staff.getString(staff.getColumnIndex("emailaddress")), null));
                startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
            }
        });
        ImageView sms = findViewById(R.id.send_sms);
        final String mobile = staff.getString(staff.getColumnIndex("mobile_no")).trim();
        if(mobile.equals("")){
            sms.setVisibility(View.GONE);
        }else{
            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(anim);
                    Uri uri = Uri.parse("smsto:"+mobile);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(intent);
                }
            });
        }
        // close database
        db.close();
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

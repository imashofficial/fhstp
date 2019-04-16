package com.ash.fhstp;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class FieldsOfStudy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load language
        String lang = tools.getLang(getBaseContext());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        // title
        setTitle(getText(R.string.menu_fields));
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields_of_study);
        // main toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_chevron_leftdp);
        // load resource arrays
        int item_texts[] = {
                R.string.study_dmw,
                R.string.study_dmdt,
                R.string.study_dis,
                R.string.study_dbm,
                R.string.study_dge,
                R.string.study_dso
        };
        int item_colors[] = {
                R.color.study_dmw,
                R.color.study_dmdt,
                R.color.study_dis,
                R.color.study_dbm,
                R.color.study_dge,
                R.color.study_dso
        };
        final String item_html[] = {
                "dmw",
                "dmdt",
                "dis",
                "dbm",
                "dge",
                "dso"
        };
        // create page items
        LinearLayout study_items = findViewById(R.id.study_items);
        LayoutInflater indlater = LayoutInflater.from(FieldsOfStudy.this.getApplicationContext());
        for(int i=0;i<6;i++){
            // inflate xml
            View row = indlater.inflate(R.layout.study_item, study_items, false);
            ImageView _left = row.findViewById(R.id.study_left);
            TextView _text = row.findViewById(R.id.study_text);
            ImageView _right = row.findViewById(R.id.study_right);
            ConstraintLayout _btn = row.findViewById(R.id.study_button);
            // set
            _left.setColorFilter(getResources().getColor(item_colors[i]));
            _text.setText(getText(item_texts[i]));
            _right.setColorFilter(getResources().getColor(item_colors[i]));
            // when item clicked
            final int id = i;
            _btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FieldsOfStudy.this, StudyDetails.class);
                    intent.putExtra("name",item_html[id] + ".html");
                    startActivity(intent);
                }
            });
            // add it
            study_items.addView(row);
        }
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

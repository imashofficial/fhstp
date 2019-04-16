package com.ash.fhstp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import java.util.Locale;

public class StaffList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Cursor dataset;
    private String lang, table, title;

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
        // load intent variables
        this.title = getIntent().getStringExtra("title");
        this.table = getIntent().getStringExtra("table");
        // set title
        setTitle(this.title.equals("") ? getText(R.string.menu_staff) : this.title);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_chevron_leftdp);
        // set searchView
        SearchView sv = findViewById(R.id.search_view);
        EditText et = sv.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        et.setTextColor(getResources().getColor(R.color.white));
        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toggleSearch(false);
                return false;
            }
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                selectRecords(s);
                mAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                selectRecords(s);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        // set main list
        recyclerView = findViewById(R.id.staffList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // select records from database
        selectRecords("");
        // set data adapter
        mAdapter = new staffAdapter();
        recyclerView.setAdapter(mAdapter);
    }
    void selectRecords(String filter) {
        String join = "";
        String where = "";
        if(!this.table.equals("") && !this.title.equals("")) {
            join = " INNER JOIN " + this.table + " on staff.code=" +
                    this.table + ".code";
            where = " AND " + this.table + ".name like '" + this.title + "'";
        }
        SQLiteDatabase db = tools.getDb(getBaseContext());
        dataset = db.rawQuery(
                "select staff.code as code, trim(nameprefix || ' ' || fname || ' ' || lname || ' ' || namesuffix) as name, jobtitle_" +
                        this.lang + " as job from staff" +
                        join +
                        " WHERE (fname LIKE '%" + filter +
                        "%' OR lname LIKE '%" + filter +
                        "%' OR nameprefix LIKE '%" + filter +
                        "%' OR namesuffix LIKE '%" + filter +
                        "%')" + where + " ORDER BY lname,fname;", null);
        dataset.moveToFirst();
        db.close();
    }
    void toggleSearch(boolean search) {
        findViewById(R.id.search_view).setVisibility(search ? View.VISIBLE : View.GONE);
        findViewById(R.id.toolbar).setVisibility(search ? View.GONE : View.VISIBLE);
    }
    class staffAdapter extends RecyclerView.Adapter<staffAdapter.staff> {
        @Override
        public staffAdapter.staff onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.staff_item, parent, false);
            staff stf = new staff(0, v);
            return stf;
        }
        @Override
        public void onBindViewHolder(staff holder, int position) {
            dataset.moveToPosition(position);
            holder.name.setText(dataset.getString(dataset.getColumnIndex("name")));
            holder.jobTitle.setText(dataset.getString(dataset.getColumnIndex("job")));
            int code = dataset.getInt(dataset.getColumnIndex("code"));
            holder.code = code;
            try {
                holder.photo.setImageBitmap(tools.loadAssetBitmap(getBaseContext(), "photos/" + code + ".jpg"));
            } catch (Exception ex) {
                holder.photo.setImageResource(R.drawable.ic_noimg);
            }
        }
        @Override
        public int getItemCount() {
            return dataset.getCount();
        }
        //
        class staff extends RecyclerView.ViewHolder implements View.OnClickListener {
            private int code;
            private TextView name, jobTitle;
            private ImageView photo;

            public staff(int code, View v) {
                super(v);
                v.setOnClickListener(this);
                this.code = code;
                TextView name = v.findViewById(R.id.staff_name);
                TextView jobTitle = v.findViewById(R.id.staff_jobtitle);
                ImageView photo = v.findViewById(R.id.staff_img);
                this.name = name;
                this.jobTitle = jobTitle;
                this.photo = photo;
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), StaffActivity.class);
                intent.putExtra("code", this.code);
                intent.putExtra("title", title);
                startActivity(intent);
            }
            public TextView getName() {
                return name;
            }
            public void setName(TextView name) {
                this.name = name;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                this.onBackPressed();
                return true;
            case R.id.search_btn:
                // start searching
                SearchView sv = findViewById(R.id.search_view);
                toggleSearch(true);
                sv.setIconified(false);
                sv.requestFocusFromTouch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate search menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return true;
    }
}

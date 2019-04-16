package com.ash.fhstp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ListViewer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Cursor dataset;
    private String lang, table, title;
    private int icon;
    private boolean isProjectList;

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
        this.icon = getIntent().getIntExtra("icon", R.drawable.ic_noimg);
        // check if showing projects list
        this.isProjectList = this.table.startsWith("project");
        // set title
        setTitle(title);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_viewer);
        // set main toolbar
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
        // select records from db
        selectRecords("");
        // set adapter
        mAdapter = new ListViewer.listAdapter();
        recyclerView.setAdapter(mAdapter);
        // show a help message if it's projects list
        if(this.isProjectList) Toast.makeText(getBaseContext(), getString(R.string.press_and_hold),
                Toast.LENGTH_LONG).show();
    }
    void selectRecords(String filter) {
        SQLiteDatabase db = tools.getDb(getBaseContext());
        dataset = db.rawQuery(
                "SELECT DISTINCT name" + (this.isProjectList?",url":"") + " from " +
                        this.table + " WHERE name like '%" + filter + "%' ORDER BY name;",
                null);
        dataset.moveToFirst();
        db.close();
    }
    void toggleSearch(boolean search) {
        findViewById(R.id.search_view).setVisibility(search ? View.VISIBLE : View.GONE);
        findViewById(R.id.toolbar).setVisibility(search ? View.GONE : View.VISIBLE);
    }
    //
    class listAdapter extends RecyclerView.Adapter<ListViewer.listAdapter.item> {
        @Override
        public ListViewer.listAdapter.item onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_item_with_photo, parent, false);
            ListViewer.listAdapter.item itm = new ListViewer.listAdapter.item("", v);
            return itm;
        }

        @Override
        public void onBindViewHolder(ListViewer.listAdapter.item holder, int position) {
            dataset.moveToPosition(position);
            holder.title = dataset.getString(dataset.getColumnIndex("name"));
            if(isProjectList)
                holder.url = dataset.getString(dataset.getColumnIndex("url"));
            holder.name.setText(holder.title);
            holder.photo.setImageResource(icon);
        }

        @Override
        public int getItemCount() {
            return dataset.getCount();
        }

        //
        class item extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            private String url;
            private String title;
            private TextView name;
            private ImageView photo;

            public item(String title, View v) {
                super(v);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
                this.title = title;
                TextView name = v.findViewById(R.id.item_title);
                ImageView photo = v.findViewById(R.id.item_img);
                this.name = name;
                this.photo = photo;
            }

            @Override
            public void onClick(View v) {
                if(isProjectList) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(this.url));
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getBaseContext(), StaffList.class);
                    intent.putExtra("table", table);
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            }

            @Override
            public boolean onLongClick(View v) {
                if(isProjectList){
                    Intent intent = new Intent(getBaseContext(), StaffList.class);
                    intent.putExtra("table", table);
                    intent.putExtra("title", title);
                    startActivity(intent);
                    return true;
                }else return false;
            }

            public String getTitle() {
                return title;
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

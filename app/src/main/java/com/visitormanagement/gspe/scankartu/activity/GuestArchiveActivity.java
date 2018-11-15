package com.visitormanagement.gspe.scankartu.activity;

import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.visitormanagement.gspe.scankartu.R;
import com.visitormanagement.gspe.scankartu.adapter.GenericCardAdapter;
import com.visitormanagement.gspe.scankartu.database.GenericCardDBSource;
import com.visitormanagement.gspe.scankartu.model.GenericCard;

import java.util.ArrayList;
import java.util.List;

public class GuestArchiveActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RelativeLayout emptyLayout;

    List<GenericCard> dataSet;
    GenericCardAdapter adapter;

    private GenericCardDBSource dbSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_archive);

        dataSet = new ArrayList<>();
        getDataSource();

        emptyLayout = findViewById(R.id.layout_empty_data);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new GenericCardAdapter(this, dataSet);
        recyclerView.setAdapter(adapter);

        if (dataSet.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    public void getDataSource(){
        try {
            dbSource = new GenericCardDBSource(getApplicationContext());
            dbSource.open();
            dataSet = dbSource.getAllGenericCard();
            dbSource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataSource();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

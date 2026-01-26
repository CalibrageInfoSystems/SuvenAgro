package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter.NurserySaplingDetailsAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.NurserySaplingDetails;

import java.util.ArrayList;
import java.util.List;

public class ViewNurserySaplingDetails extends AppCompatActivity {

    private TextView noData;
    private RecyclerView nurserySaplingDetailsList;
    private List<NurserySaplingDetails> fullDataList = new ArrayList<>();
    private NurserySaplingDetailsAdapter adapter;
    private DataAccessHandler dataAccessHandler;

    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nursery_sapling_details);
/*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Nursery Sapling Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("View Dispatch Sapling Details");

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        loadNurserySaplingDetails();
    }

    private void initUI() {
        nurserySaplingDetailsList = findViewById(R.id.nursery_sapling_details);
        noData = findViewById(R.id.no_data_text);
        nurserySaplingDetailsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadNurserySaplingDetails() {
        String query = Queries.getNurserySaplingDetail(CommonConstants.PLOT_CODE);
        List<NurserySaplingDetails> nurserySaplingDetails = (List<NurserySaplingDetails>) dataAccessHandler.getNurserySaplingDetails(query,1);

        if (nurserySaplingDetails != null && !nurserySaplingDetails.isEmpty()) {
            nurserySaplingDetailsList.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            fullDataList = nurserySaplingDetails;
            adapter = new NurserySaplingDetailsAdapter(fullDataList, dataAccessHandler);
            nurserySaplingDetailsList.setAdapter(adapter);
        } else {
            nurserySaplingDetailsList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

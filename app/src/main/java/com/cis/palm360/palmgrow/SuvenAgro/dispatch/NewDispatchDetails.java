package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter.NewDispatchDetailsAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.DispatchRequestsModel;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class NewDispatchDetails extends AppCompatActivity {

    private RecyclerView dispatchrequestsList;
    private NewDispatchDetailsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private static final String LOG_TAG = ViewDispatchRequests.class.getName();
    private Toolbar toolbar;
    private DataAccessHandler dataAccessHandler;
    private List<DispatchRequestsModel> dispatchRequests = new ArrayList<>();
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dispatch_details);
        setSupportActionBar(toolbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dispatch Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        loadDispatchDetails();
    }

    private void initUI() {
        dispatchrequestsList = findViewById(R.id.dispatchrequests_list);
        dispatchrequestsList.setLayoutManager(new LinearLayoutManager(this));
        noData = findViewById(R.id.no_data_text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDispatchDetails() {

        dataAccessHandler.getDispatchRequests(Queries.getInstance().getDispatchRequestsQuery("852", CommonConstants.PLOT_CODE), new ApplicationThread.OnComplete<List<DispatchRequestsModel>>() {
            @Override
            public void execute(boolean success, final List<DispatchRequestsModel> dispatchRequestslist, String msg) {
                ProgressBar.hideProgressBar();
                ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        dispatchRequests = dispatchRequestslist;
                        Log.d("dispatchRequestslist", dispatchRequestslist.size() + " | " + Queries.getInstance().getDispatchRequestsQuery("852", CommonConstants.PLOT_CODE));

                        if (dispatchRequestslist == null || dispatchRequestslist.isEmpty()) {
                            noData.setVisibility(View.VISIBLE);
                            dispatchrequestsList.setVisibility(View.GONE);
                            return;
                        } else {
                            noData.setVisibility(View.GONE);
                            dispatchrequestsList.setVisibility(View.VISIBLE);
                        }

                        adapter = new NewDispatchDetailsAdapter(dispatchRequestslist,NewDispatchDetails.this, dataAccessHandler);
                        layoutManager = new LinearLayoutManager(NewDispatchDetails.this, LinearLayoutManager.VERTICAL, false);
                        dispatchrequestsList.setLayoutManager(layoutManager);
                        dispatchrequestsList.setAdapter(adapter);

                    }
                });
            }
        });

    }
}
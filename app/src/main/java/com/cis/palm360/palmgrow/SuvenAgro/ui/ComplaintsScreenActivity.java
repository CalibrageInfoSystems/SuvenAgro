package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.ComplaintDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintStatusHistory;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Complaints;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintsDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by RAMESH BABU on 02-07-2017.
 */

//Displaying Complaints Screen
public class ComplaintsScreenActivity extends AppCompatActivity implements View.OnClickListener, ComplaintsDetailsRecyclerAdapter.ClickListener, UpdateUiListener {
    private static final String LOG_TAG = ComplaintsScreenActivity.class.getName();
    private RecyclerView complaints_list;
    private ProgressBar progress;
    private Button complaint_add, viewCurrentComplaintsBtn;
    private ComplaintsDetailsRecyclerAdapter ComplaintsDetailsRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private Toolbar toolbar;
    private Intent  intent;
    private boolean isPlot;
    private ActionBar actionBar;
    String fromscreen;
    private DataAccessHandler dataAccessHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_complaints_screen);
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Complaints");

        intent= getIntent();
        if(intent!=null){
            isPlot = intent.getBooleanExtra("plot",false);
        }
        setUI();

        Complaints complaints = (Complaints) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_DETAILS);
        viewCurrentComplaintsBtn.setVisibility((complaints != null) ? View.VISIBLE : View.GONE);

    }

    private void setUI() {
        complaints_list = findViewById(R.id.complaints_list);
        complaint_add = findViewById(R.id.complaint_add);
        viewCurrentComplaintsBtn = findViewById(R.id.view_complaint);
        progress = findViewById(R.id.progress);
        dataAccessHandler = new DataAccessHandler(this);

        fromscreen = intent.getStringExtra("fromsearchfarmer");
        bindData();
        List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);
        List<Integer> activityRightIds = new ArrayList<>();
        for (Map<String, String> right : activityRights) {
            try {
                activityRightIds.add(Integer.parseInt(right.get("Id")));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Log.d("ActivityRightIds", activityRightIds.toString());
        if (activityRightIds.contains(16)) {
            complaints_list.setVisibility(View.VISIBLE);

        }
        else{
            complaints_list.setVisibility(View.GONE);
        }
        // ✅ Show/hide button based on ActivityRight ID and source screen
        if (fromscreen != null && fromscreen.equalsIgnoreCase("fromsearchfarmer")) {
            complaint_add.setVisibility(View.GONE);
        } else {
            if (activityRightIds.contains(15)) {
                complaint_add.setVisibility(View.VISIBLE);
            } else {
                complaint_add.setVisibility(View.GONE);
            }
        }

        viewCurrentComplaintsBtn.setOnClickListener(view -> {
            ViewCurrentComplaintFragment viewCurrentComplaintFragment = new ViewCurrentComplaintFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, viewCurrentComplaintFragment)
                    .addToBackStack(null)
                    .commit();
        });

        complaint_add.setOnClickListener(view -> {
            System.out.println("add ====================================>");
            Bundle dataBundle = new Bundle();
            dataBundle.putBoolean(CommonConstants.KEY_NEW_COMPLAINT, true);
            ComplaintDetailsFragment complaintDetailsFragment = new ComplaintDetailsFragment();
            complaintDetailsFragment.setArguments(dataBundle);
            complaintDetailsFragment.setUpdateUiListener(ComplaintsScreenActivity.this);
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, complaintDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void bindData() {
        com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "", new Runnable() {
            @Override
            public void run() {
                final DataAccessHandler dataAccessHandler = new DataAccessHandler(ComplaintsScreenActivity.this);

                if (fromscreen != null && fromscreen.equalsIgnoreCase("fromsearchfarmer")) {
                    dataAccessHandler.getComplaintsByUser(Queries.getInstance().getComplaintToDisplay(isPlot, CommonConstants.PLOT_CODE), new ApplicationThread.OnComplete<List<ComplaintsDetails>>() {
                        @Override
                        public void execute(boolean success, final List<ComplaintsDetails> complaintsDetails , String msg) {
                            com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar.hideProgressBar();
                            ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                                @Override
                                public void run() {
                                    ComplaintsDetailsRecyclerAdapter = new ComplaintsDetailsRecyclerAdapter(ComplaintsScreenActivity.this, complaintsDetails, dataAccessHandler);
                                    layoutManager = new LinearLayoutManager(ComplaintsScreenActivity.this, LinearLayoutManager.VERTICAL, false);
                                    complaints_list.setLayoutManager(layoutManager);
                                    complaints_list.setAdapter(ComplaintsDetailsRecyclerAdapter);
                                    ComplaintsDetailsRecyclerAdapter.setOnClickListener(ComplaintsScreenActivity.this);
                                }
                            });
                        }
                    });
                }else{
                    dataAccessHandler.getComplaintsByUser(Queries.getInstance().getComplaintToDisplayagainistplotcode(CommonConstants.PLOT_CODE), new ApplicationThread.OnComplete<List<ComplaintsDetails>>() {
                        @Override
                        public void execute(boolean success, final List<ComplaintsDetails> complaintsDetails , String msg) {
                            com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar.hideProgressBar();
                            ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                                @Override
                                public void run() {
                                    ComplaintsDetailsRecyclerAdapter = new ComplaintsDetailsRecyclerAdapter(ComplaintsScreenActivity.this, complaintsDetails, dataAccessHandler);
                                    layoutManager = new LinearLayoutManager(ComplaintsScreenActivity.this, LinearLayoutManager.VERTICAL, false);
                                    complaints_list.setLayoutManager(layoutManager);
                                    complaints_list.setAdapter(ComplaintsDetailsRecyclerAdapter);
                                    ComplaintsDetailsRecyclerAdapter.setOnClickListener(ComplaintsScreenActivity.this);
                                }
                            });
                        }
                    });
                }



            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String complaintCode) {
        Log.v(LOG_TAG, "### complant code " + complaintCode);
        DataAccessHandler dataAccessHandler = new DataAccessHandler(this);

        Complaints complaintsList = (Complaints) dataAccessHandler.getComplaints(Queries.getInstance().getComplaintDataByCode(complaintCode), 0);

        List<ComplaintStatusHistory> complaintsStatusHistory = (List<ComplaintStatusHistory>) dataAccessHandler.getComplaintStatusHistory
                (Queries.getInstance().getComplaintStatusHistoryByCode(complaintCode), 1);

        List<ComplaintTypeXref> complaintsTypeXref = (List<ComplaintTypeXref>) dataAccessHandler
                .getComplaintTypeXref(Queries.getInstance().getComplaintXrefByCode(complaintCode), 1);

        List<ComplaintRepository> complaintsRepository = (List<ComplaintRepository>) dataAccessHandler
                .getComplaintRepository(Queries.getInstance().getComplaintRepositoryByCode(complaintCode), 1);

        if (null != complaintsList) {
            DataManager.getInstance().addData(DataManager.COMPLAINT_DETAILS, complaintsList);
            DataManager.getInstance().addData(DataManager.COMPLAINT_STATUS_HISTORY, complaintsStatusHistory);
            DataManager.getInstance().addData(DataManager.COMPLAINT_TYPE, complaintsTypeXref);
            DataManager.getInstance().addData(DataManager.COMPLAINT_REPOSITORY, complaintsRepository);
        }

        CommonConstants.COMPLAINT_CODE = complaintCode;

        Bundle dataBundle = new Bundle();
        dataBundle.putString("complaintCode", complaintCode);
        dataBundle.putBoolean(CommonConstants.KEY_NEW_COMPLAINT, false);
        ComplaintDetailsFragment complaintDetailsFragment = new ComplaintDetailsFragment();
        complaintDetailsFragment.setArguments(dataBundle);
        complaintDetailsFragment.setUpdateUiListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, complaintDetailsFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void updateUserInterface(int position) {
        Log.v(LOG_TAG, "@@@ ui update called");
        bindData();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindData();
    }
}



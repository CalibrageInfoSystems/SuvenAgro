package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter.AdvanceDetailsAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RaiseDispatchSaplingsRequestActivity extends AppCompatActivity implements AdvanceDetailsAdapter.ClickListener {

    RecyclerView advanceDetailsList;
    private AdvanceDetailsAdapter adapter;
    private List<AdvancedDetails> advancedDetailsList = new ArrayList<>();
    private DataAccessHandler dataAccessHandler;
    LinkedHashMap<Integer, String> paymentmodeMap;
    TextView noData;
    private Integer getPendingSaplingsCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_saplings_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Raise Dispatch Saplings Request");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        getPendingSaplingsCount = dataAccessHandler.fetchPendingSaplingsCount(Queries.getInstance().fetchPendingSaplingsCount(CommonConstants.PLOT_CODE));

        loadAdvanceDetails();
    }

    //    private void loadAdvanceDetails() {
//
//        String query = Queries.getAdvancedDetails();
//        List<AdvancedDetails> advancedDetailsList = (List<AdvancedDetails>) dataAccessHandler.getAdvancedDetails(query, 1);
//        Log.d("xxx", advancedDetailsList.toString());
//        if (advancedDetailsList != null && !advancedDetailsList.isEmpty()) {
//            advancedDetailsList = advancedDetailsList;
//            adapter = new AdvanceDetailsAdapter(advancedDetailsList, this);
//            advanceDetailsList.setAdapter(adapter);
//        } else {
//            Toast.makeText(this, "No Records Found", Toast.LENGTH_SHORT).show();
//        }
//
//    }
    private void loadAdvanceDetails() {

        String query = Queries.getAllAdvancedDetails(CommonConstants.PLOT_CODE);
        //   String query = Queries.getAdvancedDetails();
        this.advancedDetailsList = (List<AdvancedDetails>) dataAccessHandler.getAdvancedDetails(query, 1);
        Log.d("xxx", advancedDetailsList.toString());
        paymentmodeMap = dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
        if (advancedDetailsList != null && !advancedDetailsList.isEmpty()) {
            advanceDetailsList.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            adapter = new AdvanceDetailsAdapter(advancedDetailsList, this,paymentmodeMap,dataAccessHandler);
            advanceDetailsList.setAdapter(adapter);
        } else {
            advanceDetailsList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }





    private void initUI() {
        advanceDetailsList = findViewById(R.id.advanceDetailsList);
        advanceDetailsList.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    public void onItemClicked(AdvancedDetails model) {
        if (getPendingSaplingsCount > 0) {
            Intent intent = new Intent(this, AddRaiseDispatchSaplingsRequestActivity.class);
            intent.putExtra(CommonConstants.advancedModel, model);
            startActivity(intent);
        } else {
            UiUtils.showCustomToastMessage("No Pending Saplings Found", RaiseDispatchSaplingsRequestActivity.this, 0);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
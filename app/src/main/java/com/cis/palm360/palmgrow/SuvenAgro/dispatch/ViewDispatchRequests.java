package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;

import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmersDataforImageUploading;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter.DispatchRequestsRecyclerviewAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.DispatchRequestsModel;
import com.cis.palm360.palmgrow.SuvenAgro.ui.HomeScreen;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewDispatchRequests extends AppCompatActivity implements DispatchRequestsRecyclerviewAdapter.ClickListener, UpdateUiListener {

    Button dispatchsaplingwithoutrequest;
    private RecyclerView dispatchrequests_list;
    private DispatchRequestsRecyclerviewAdapter DispatchRequests_RecyclerviewAdapter;
    private LinearLayoutManager layoutManager;
    private static final String LOG_TAG = ViewDispatchRequests.class.getName();
    private ActionBar actionBar;
    private Toolbar toolbar;
    private DataAccessHandler dataAccessHandler;
    private List<DispatchRequestsModel> dispatchRequests;
    private TextView noData;
    Integer stateID, ZoneID;
    private Integer getPendingSaplingsCount;
    ArrayList<FarmersDataforImageUploading> farmersdata;
    int isDripRequiredAndStatus = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_dispatch_requests);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("View Dispatch Requests");

        dataAccessHandler = new DataAccessHandler(ViewDispatchRequests.this);

        dispatchsaplingwithoutrequest = findViewById(R.id.dispatchsaplingwithoutrequest);
        dispatchrequests_list = findViewById(R.id.dispatchrequests_list);
        noData = findViewById(R.id.no_data_text);

        stateID = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getStateIdForPlotCode(CommonConstants.PLOT_CODE));
        ZoneID = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getZoneIdForPlotCode(CommonConstants.PLOT_CODE));
        android.util.Log.e("getStateIdForPlotCode", stateID + " | " + ZoneID + " | " + CommonConstants.PLOT_CODE);

        getPendingSaplingsCount = dataAccessHandler.fetchPendingSaplingsCount(Queries.getInstance().fetchPendingSaplingsCount(CommonConstants.PLOT_CODE));
        CommonConstants.districtIdPlot = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());
        Log.e("districtIdPlot", CommonConstants.districtIdPlot);





//        String dripCheck = CommonUiUtils.getDripStatus(this);
//        Log.e("isDrip", dripCheck + " " );
//        if (dripCheck != null && !dripCheck.isEmpty()) {
//            UiUtils.showCustomToastMessage(
//                    dripCheck,
//                    ViewDispatchRequests.this,
//                    1
//            );
//        }
//        if (isDrip) {
//            UiUtils.showCustomToastMessage("Complete Drip Irrigation Flow", ViewDispatchRequests.this, 1);
//           return;
//        } else {
           // UiUtils.showCustomToastMessage("No Pending Saplings Found", ViewDispatchRequests.this, 0);
            // Continue normal flow
          //  Toast.makeText(context, "Drip Irrigation required & status found", Toast.LENGTH_SHORT).show();
//        }


        String dripCheck = CommonUiUtils.getDripStatus(ViewDispatchRequests.this);
        Log.e("isDrip", "Drip Status: " + dripCheck);

// assuming you already have this flag somewhere
        boolean hasRequiredStatus = CommonUiUtils.hasRequiredStatus(ViewDispatchRequests.this);

        if (dripCheck != null && !hasRequiredStatus) {
            UiUtils.showCustomToastMessage(
                    dripCheck,
                    ViewDispatchRequests.this,
                    1
            );
            dispatchsaplingwithoutrequest.setClickable(false);
            dispatchsaplingwithoutrequest.setEnabled(false);
            dispatchsaplingwithoutrequest.setBackground(getResources().getDrawable(R.drawable.btn_diable));
            dispatchrequests_list.setVisibility(View.GONE);
        }
        else {
            dispatchrequests_list.setVisibility(View.VISIBLE);
            fetchAndBindDispatchRequests();
            dispatchsaplingwithoutrequest.setClickable(true);
            dispatchsaplingwithoutrequest.setEnabled(true);
            dispatchsaplingwithoutrequest.setBackground(getResources().getDrawable(R.drawable.btn_stateful));
        }

        dispatchsaplingwithoutrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getPendingSaplingsCount > 0) {
                    if (ZoneID == null || stateID == null) {
                        UiUtils.showCustomToastMessage("Please Do Master Sync", ViewDispatchRequests.this, 0);
                    } else {
                        dataAccessHandler.getReceiptNumbersAgainstPlotCode(
                                Queries.getInstance().getReceiptNumbersfromPlotCode(CommonConstants.PLOT_CODE),
                                new ApplicationThread.OnComplete<List<String>>() {
                                    @Override
                                    public void execute(boolean success, final List<String> receiptNumbers, String msg) {
                                        ApplicationThread.uiPost("ReceiptNumbers", "", new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("dispatchsaplingwithoutrequest", "Queries" + Queries.getInstance().getReceiptNumbersAgainstPlotCode(CommonConstants.PLOT_CODE));
                                                Log.d("dispatchsaplingwithoutrequest", "receiptNumbers" + receiptNumbers.size());

                                                if (receiptNumbers != null && !receiptNumbers.isEmpty()) {
                                                    ArrayList<String> modifiedReceiptNumbers = new ArrayList<>(receiptNumbers);

                                                    modifiedReceiptNumbers.add(0, "Select Receipt Number");

                                                    Intent mintent = new Intent(ViewDispatchRequests.this, AddDispatchSaplingsActivity.class);
                                                    mintent.putStringArrayListExtra(CommonConstants.reciptNumbers, modifiedReceiptNumbers);
                                                    mintent.putExtra("From", "WithoutRequest");
                                                    startActivity(mintent);
                                                } else {
                                                    Log.d("dispatchsaplingwithoutrequest", "Queries" + Queries.getInstance().getReceiptNumbersAgainstPlotCode(CommonConstants.PLOT_CODE));
                                                    Log.d("dispatchsaplingwithoutrequest", "receiptNumbers" + receiptNumbers);
                                                //  UiUtils.showCustomToastMessage("No Receipt Numbers Found", ViewDispatchRequests.this, 0);
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                } else {
                    UiUtils.showCustomToastMessage("No Pending Saplings Found", ViewDispatchRequests.this, 0);
                }
            }
        });
    }

    public void showWarningDialog(String title, String message) {
        final Dialog dialog = new Dialog(ViewDispatchRequests.this);
        dialog.setContentView(R.layout.custom_alert_dailog);

        // Prevent dismiss on outside touch and back button
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        LinearLayout btnContainer = dialog.findViewById(R.id.btnContainer);
        LinearLayout okBtnContainer = dialog.findViewById(R.id.okBtnContainer);
        Button okBtn = dialog.findViewById(R.id.okBtn);
        TextView msg = dialog.findViewById(R.id.test);

        btnContainer.setVisibility(View.GONE);
        okBtnContainer.setVisibility(View.VISIBLE);

        dialogTitle.setText(title);
        msg.setText(message);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String dripCheck = CommonUiUtils.getDripStatus(ViewDispatchRequests.this);
        Log.e("isDrip", "Drip Status: " + dripCheck);

// assuming you already have this flag somewhere
        boolean hasRequiredStatus = CommonUiUtils.hasRequiredStatus(ViewDispatchRequests.this);

        if (dripCheck != null && !hasRequiredStatus) {
            UiUtils.showCustomToastMessage(
                    dripCheck,
                    ViewDispatchRequests.this,
                    1
            );
            dispatchsaplingwithoutrequest.setClickable(false);
            dispatchsaplingwithoutrequest.setEnabled(false);
            dispatchsaplingwithoutrequest.setBackground(getResources().getDrawable(R.drawable.btn_diable));
            dispatchrequests_list.setVisibility(View.GONE);
        }
        else {
            dispatchrequests_list.setVisibility(View.VISIBLE);
            fetchAndBindDispatchRequests();
            dispatchsaplingwithoutrequest.setClickable(true);
            dispatchsaplingwithoutrequest.setEnabled(true);
            dispatchsaplingwithoutrequest.setBackground(getResources().getDrawable(R.drawable.btn_stateful));
        }

    }

    private void fetchAndBindDispatchRequests() {
        getPendingSaplingsCount = dataAccessHandler.fetchPendingSaplingsCount(Queries.getInstance().fetchPendingSaplingsCount(CommonConstants.PLOT_CODE));

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
                            dispatchrequests_list.setVisibility(View.GONE);
                            return;
                        } else {
                            noData.setVisibility(View.GONE);
                            dispatchrequests_list.setVisibility(View.VISIBLE);
                        }

                        DispatchRequests_RecyclerviewAdapter = new DispatchRequestsRecyclerviewAdapter(ViewDispatchRequests.this, dispatchRequestslist, dataAccessHandler, ViewDispatchRequests.this);
                        layoutManager = new LinearLayoutManager(ViewDispatchRequests.this, LinearLayoutManager.VERTICAL, false);
                        dispatchrequests_list.setLayoutManager(layoutManager);
                        dispatchrequests_list.setAdapter(DispatchRequests_RecyclerviewAdapter);
                        DispatchRequests_RecyclerviewAdapter.setOnClickListener(ViewDispatchRequests.this);
                    }
                });
            }
        });
    }

    @Override
    public void updateUserInterface(int refreshPosition) {

    }

    @Override
    public void onItemClicked(DispatchRequestsModel model) {
        if (getPendingSaplingsCount > 0) {
            if (ZoneID == null || stateID == null) {
                UiUtils.showCustomToastMessage("Please Do Master Sync", ViewDispatchRequests.this, 0);
            } else {
                Intent mintent = new Intent(this, AddDispatchSaplingsActivity.class);
                mintent.putExtra(CommonConstants.dispatchModel, model);
                mintent.putExtra("From", "WithRequest");
                startActivity(mintent);
            }
        } else {
            UiUtils.showCustomToastMessage("No Pending Saplings Found", ViewDispatchRequests.this, 0);
        }
    }

    @Override
    public void onItemDeleteClicked(DispatchRequestsModel model) {
        // custom dialog



        farmersdata = dataAccessHandler.getFarmerDetailsforImageUploading(Queries.getInstance().getfarmerdetailsforimageuploading(CommonConstants.FARMER_CODE));

        // custom dialog
        final Dialog dialog = new Dialog(ViewDispatchRequests.this);
        dialog.setContentView(R.layout.dispatch_request_delete_dialog);

        Button yesDialogButton = dialog.findViewById(R.id.Yes);
        Button noDialogButton = dialog.findViewById(R.id.No);
        TextView msg = dialog.findViewById(R.id.test);
        msg.setText("Are You Sure You Want To Cancel This Dispatch Request?");

        TextView field_code = dialog.findViewById(R.id.plotcode_txt);
        TextView grower_code = dialog.findViewById(R.id.tv_grower_code);
        TextView receipt_number = dialog.findViewById(R.id.receiptId);
        TextView imported_saplings = dialog.findViewById(R.id.NoofImportedSaplings_text);
        TextView indigenous_saplings = dialog.findViewById(R.id.noofIndegenoussapling_txt);
        TextView saplings = dialog.findViewById(R.id.noofsaplingstodispatch_text);

        field_code.setText(": "+model.getPlotCode());
        grower_code.setText(": "+CommonConstants.FARMER_CODE);
        receipt_number.setText(": "+model.getReceiptNumber());
        imported_saplings.setText(": "+ model.getNoOfImportedSaplingsToDispatch());
        indigenous_saplings.setText(": "+ model.getNoOfIndigenousSaplingsToDispatch());
        saplings.setText(": "+ model.getNoOfSaplingsToDispatch());
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement update model status to close
                String query = Queries.getInstance().updateStatusToCancel(model.getReceiptNumber(), CommonConstants.USER_ID, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                Log.e("updateStatusToCancel", query);
                dataAccessHandler.updateStatusToCancel(query, new ApplicationThread.OnComplete<Integer>() {
                    @Override
                    public void execute(boolean success, Integer result, String message) {
                        if (success) {
//                    finish();
                            UiUtils.showCustomToastMessage(message, ViewDispatchRequests.this, 0);
                            fetchAndBindDispatchRequests();

                        } else {
                            Log.e("UpdateStatus", message);
                            UiUtils.showCustomToastMessage(message, ViewDispatchRequests.this, 1);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
// Navigate to home screen
            Intent intent = new Intent(this, HomeScreen.class); // Replace with your actual home screen activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeScreen.class); // Replace with your actual home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }
}
package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.HarvestingActivity;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmersDataforImageUploading;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotAuditDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvanceMst;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.DispatchRequestsModel;

import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddDispatchSaplingsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Toolbar toolbar;
    Spinner spinner_advance_receiptNo;
    private LinkedHashMap<String, String> advancereceiptdataMap;
    private DataAccessHandler dataAccessHandler;
    EditText pending_advance_sapling, indigenous_grower_contribution, indigenous_subsidy_price, imported_grower_contribution_price, imported_subsidy_price,
            total_saplings_dispatch, imported_sapling_dispatches, indigenous_sapling_dispatches, sapling_dispatched, pickup_date, comments;

    Button add_btn;
    LinearLayout total_saplings_dispatch_layout;

    public String receiptCode;
    public Integer pendingSaplings;
    DispatchRequestsModel mDispatchRequestsModel;
    ArrayList<String> receiptNumbers;
    private int noOfSaplingsAdvancePaidFor = 0;
    private int AdvancePaidPENDINGSAPLINGS = 0;
    private int importedSaplings = 0;
    private int indigenousSaplings = 0;
    private int totalSaplingsPaidFor = 0;
    private int totalImpSaplingsPaidFor = 0;
    private int totalIndSaplingsPaidFor = 0;

    ArrayList<FarmersDataforImageUploading> farmersdata;

    ArrayList<PlotAuditDetails> plotAuditDetailsData;
    ArrayList<DispatchRequestsModel> dispatchRequestsModels;
    TextView fieldCode,growerCode,growerName,fieldVillage;
    String fullname = "", middleName = "";
    TextView imported_saplings, indigenous_saplings ,saplings_advance;
    AdvancedDetails model;
    TextView saplings_advance_paid,imported_saplings_issued,indigenous_saplings_issued;
    private List<AdvancedDetails> mAdvancedDetailsList;
    private Map<String, Integer> totalAdvanceData;
    private String financalSubStringYear = "";// this will hold yyyy-MM-dd format
    public int financialYear;
    private String days = "";
    private Button view_details;
    private  String Advace_Receiptnum;
    String Navigationfrom;
    int isDripRequiredAndStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispatch_saplings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Dispatch Sapling Details");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initUi();
        }
        setActions();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initUi() {

        dataAccessHandler = new DataAccessHandler(this);

        fieldCode = findViewById(R.id.fieldCode);
        growerCode = findViewById(R.id.growerCode);
        growerName= findViewById(R.id.growerName);
        fieldVillage = findViewById(R.id.fieldVillage);

        imported_saplings_issued = findViewById(R.id.imported_saplings_issued);
        indigenous_saplings_issued = findViewById(R.id.indigenous_saplings_issued);
        saplings_advance_paid = findViewById(R.id.saplings_advance_paid);
        spinner_advance_receiptNo = findViewById(R.id.spinner_advance_receiptNo);
        pending_advance_sapling = findViewById(R.id.pending_advance_sapling);

        indigenous_grower_contribution = findViewById(R.id.indigenous_grower_contribution);
        indigenous_subsidy_price = findViewById(R.id.indigenous_subsidy_price);
        imported_grower_contribution_price = findViewById(R.id.imported_grower_contribution_price);
        imported_subsidy_price = findViewById(R.id.imported_subsidy_price);

        imported_sapling_dispatches = findViewById(R.id.imported_sapling_dispatches);
        indigenous_sapling_dispatches = findViewById(R.id.indigenous_sapling_dispatches);
        sapling_dispatched = findViewById(R.id.sapling_dispatched);
        pickup_date = findViewById(R.id.pickup_date);
        comments = findViewById(R.id.comments);
        spinner_advance_receiptNo = findViewById(R.id.spinner_advance_receiptNo);
        pending_advance_sapling = findViewById(R.id.pending_advance_sapling);

        total_saplings_dispatch_layout = findViewById(R.id.total_saplings_dispatch_layout);
        indigenous_grower_contribution = findViewById(R.id.indigenous_grower_contribution);
        indigenous_subsidy_price = findViewById(R.id.indigenous_subsidy_price);
        imported_grower_contribution_price = findViewById(R.id.imported_grower_contribution_price);
        imported_subsidy_price = findViewById(R.id.imported_subsidy_price);

        total_saplings_dispatch = findViewById(R.id.total_saplings_dispatch);
        imported_sapling_dispatches = findViewById(R.id.imported_sapling_dispatches);
        indigenous_sapling_dispatches = findViewById(R.id.indigenous_sapling_dispatches);
        sapling_dispatched = findViewById(R.id.sapling_dispatched);
        view_details = findViewById(R.id.view_details);

        add_btn = findViewById(R.id.add_btn);
        final Calendar calendar1 = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar1);
        financialYear = fiscalDate.getFiscalYear();
        financalSubStringYear = String.valueOf(financialYear).substring(2,4);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
            String financalDate = "01/04/"+String.valueOf(financialYear);
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(financalDate);
            long diff = date1.getTime() - date2.getTime();
            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS)+1);
            days = StringUtils.leftPad(noOfDays,3,"0");
            android.util.Log.e("====>","days -->"+days+financialYear+diff);

        }catch (Exception e){
            e.printStackTrace();
        }

//        pendingSaplings = getIntent().getExtras().getInt("PendingSaplings");

//        String query = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = 1 AND ZoneId = 16 AND SourceOfSaplings = 55 ORDER BY SourceOfSaplings";
//        String query2 = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = 1 AND ZoneId = 16 AND SourceOfSaplings = 56 ORDER BY SourceOfSaplings";

        Integer stateID  = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getStateIdForPlotCode(CommonConstants.PLOT_CODE));
        Integer ZoneID  = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getZoneIdForPlotCode(CommonConstants.PLOT_CODE));


        android.util.Log.e("getStateIdForPlotCode", stateID + " " + ZoneID);
        if (ZoneID == null || stateID == null) {
            showWarningDialog("Warning", "Please Do Master Sync");
            return;
        }

        String query = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = " + stateID + " AND ZoneId = "+ ZoneID +" AND SourceOfSaplings = 55 ORDER BY SourceOfSaplings";
        String query2 = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = " + stateID + " AND ZoneId = "+ ZoneID +" AND SourceOfSaplings = 56 ORDER BY SourceOfSaplings";



        List<AdvanceMst> list = dataAccessHandler.getAdvanceMstData(query);
        List<AdvanceMst> list2 = dataAccessHandler.getAdvanceMstData(query2);

/*        imported_grower_contribution_price.setFocusable(false);
        imported_grower_contribution_price.setEnabled(false);

        imported_subsidy_price.setFocusable(false);
        imported_subsidy_price.setEnabled(false);

        indigenous_grower_contribution.setFocusable(false);
        indigenous_grower_contribution.setEnabled(false);

        indigenous_subsidy_price.setFocusable(false);
        indigenous_subsidy_price.setEnabled(false);

        pending_advance_sapling.setFocusable(false);
        pending_advance_sapling.setEnabled(false);

        sapling_dispatched.setFocusable(false);
        sapling_dispatched.setEnabled(false);*/

//        pickup_date.setFocusable(false);
//        pickup_date.setEnabled(false);

        if (list != null && !list.isEmpty()) {
            Float farmerContribution = list.get(0).getFarmerContributionReceived();
            Float subsidyPrice = list.get(0).getSubsidyPrice();

            imported_grower_contribution_price.setText(farmerContribution != null ? String.valueOf(farmerContribution) : "0.0");
            imported_subsidy_price.setText(subsidyPrice != null ? String.valueOf(subsidyPrice) : "0.0");
        } else {
            imported_grower_contribution_price.setText("0.0");
            imported_subsidy_price.setText("0.0");
        }

        if (list2 != null && !list2.isEmpty()) {
            Float farmerContribution2 = list2.get(0).getFarmerContributionReceived();
            Float subsidyPrice2 = list2.get(0).getSubsidyPrice();

            indigenous_grower_contribution.setText(farmerContribution2 != null ? String.valueOf(farmerContribution2) : "0.0");
            indigenous_subsidy_price.setText(subsidyPrice2 != null ? String.valueOf(subsidyPrice2) : "0.0");
        } else {
            indigenous_grower_contribution.setText("0.0");
            indigenous_subsidy_price.setText("0.0");
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        pickup_date.setText(currentDate);


// xxx data binding
        mDispatchRequestsModel = (DispatchRequestsModel) getIntent().getSerializableExtra(CommonConstants.dispatchModel);

        if (mDispatchRequestsModel != null) {
            initializeSpinnerWithValue(spinner_advance_receiptNo, mDispatchRequestsModel.getReceiptNumber());
            total_saplings_dispatch.setText(mDispatchRequestsModel.getNoOfSaplingsToDispatch().toString());
            Advace_Receiptnum = mDispatchRequestsModel.getAdvanceReceiptNumber();
            noOfSaplingsAdvancePaidFor = mDispatchRequestsModel.getNoOfSaplingsToDispatch();
            importedSaplings = mDispatchRequestsModel.getNoOfImportedSaplingsToDispatch();
            imported_sapling_dispatches.setText(String.valueOf(importedSaplings));
            imported_sapling_dispatches.setEnabled(false);

            indigenousSaplings = mDispatchRequestsModel.getNoOfIndigenousSaplingsToDispatch();
            indigenous_sapling_dispatches.setText(String.valueOf(indigenousSaplings));
            indigenous_sapling_dispatches.setEnabled(false);

            sapling_dispatched.setText(String.valueOf(importedSaplings + indigenousSaplings));
            sapling_dispatched.setEnabled(false);

            comments.setText(mDispatchRequestsModel.getComments());
        }

        receiptNumbers = getIntent().getStringArrayListExtra(CommonConstants.reciptNumbers);
         Navigationfrom = getIntent().getStringExtra("From");
        // Log.e("Navigationfrom===262", Navigationfrom + "");
        if (receiptNumbers != null) {
            loadSnipperWithList(spinner_advance_receiptNo, receiptNumbers);

//            total_saplings_dispatch_layout.setVisibility(View.GONE);
        }
        view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDispatchSaplingsActivity.this, ViewNurserySaplingDetails.class);
                startActivity(intent);
            }
        });
        String fetchAdvanceDetailsQuery = Queries.getAllAdvancedDetails(CommonConstants.PLOT_CODE);
        //   String query = Queries.getAdvancedDetails();
        totalAdvanceData = (Map<String, Integer>) dataAccessHandler.getAdvanceDetailsAgainstPlot(Queries.getInstance().getAdvanceDetailsAgainstPlot(CommonConstants.PLOT_CODE));


        if (totalAdvanceData != null) {
            int totalSaplingsPaidFor = totalAdvanceData.getOrDefault("totalSaplingsPaidFor", 0);
            int importedSaplings = totalAdvanceData.getOrDefault("totalImpSaplingsPaidFor", 0);
            int indigenousSaplings = totalAdvanceData.getOrDefault("totalIndSaplingsPaidFor", 0);

            saplings_advance_paid.setText(" :  " + totalSaplingsPaidFor);
            imported_saplings_issued.setText(" :  " + importedSaplings);
            indigenous_saplings_issued.setText(" :  " + indigenousSaplings);

        }
    }

    public void showWarningDialog(String title, String message) {
        final Dialog dialog = new Dialog(AddDispatchSaplingsActivity.this);
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

    private void initializeSpinnerWithValue(Spinner spinner, String value) {
        List<String> list = new ArrayList<>();
        list.add(value); // Add the provided value as the only item

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0);
    }

    private void loadSnipperWithList(Spinner spinner, List<String> values) {
        if (values == null || values.isEmpty()) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0);
    }

    /*public static String convertDateToServerFormat(String dateString) {
        try {
            // Input format: dd-MM-yyyy
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            // Output format: yyyy-MM-dd
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // or handle default fallback
        }
    }*/

    public static String convertDateToServerFormat(String dateString) {
        String[] possibleFormats = {"dd-MM-yyyy", "dd/MM/yyyy"};
        for (String format : possibleFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                Date date = inputFormat.parse(dateString);

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException ignored) {
            }
        }
        return null; // fallback if no format matched
    }


    private void setActions() {
        farmersdata = dataAccessHandler.getFarmerDetailsforImageUploading(Queries.getInstance().getfarmerdetailsforimageuploading(CommonConstants.FARMER_CODE));
        plotAuditDetailsData = dataAccessHandler.getPlotDetailsforAudit(Queries.getInstance().getPlotDetailsforAudit(CommonConstants.PLOT_CODE));


        if (!TextUtils.isEmpty(farmersdata.get(0).getMiddleName()) && !
                farmersdata.get(0).getMiddleName().equalsIgnoreCase("null")) {
            middleName = farmersdata.get(0).getMiddleName();
        }
        fullname = farmersdata.get(0).getFirstName().trim() + " " + middleName + " " + farmersdata.get(0).getLastName().trim();

        android.util.Log.d("Grower Name", fullname + "");
        android.util.Log.d("Grower Code", CommonConstants.FARMER_CODE);
        android.util.Log.d("Grower Plot Code", CommonConstants.PLOT_CODE);

        growerName.setText(" :  " + fullname);
        growerCode.setText(" :  " + CommonConstants.FARMER_CODE);
        fieldCode.setText(" :  " + CommonConstants.PLOT_CODE);
        fieldVillage.setText(" :  " + plotAuditDetailsData.get(0).getVillageName());
        String  ReceiptNumber = dataAccessHandler.getGenerateReceiptNumberforNurseryDispatch(
                Queries.getInstance().getMaxNumberQueryForNurserySaplingDetailsReceiptNumber(financalSubStringYear + days), financalSubStringYear + days);
        android.util.Log.e("====>getGenerateReceiptNumberforNurseryDispatch", "" + ReceiptNumber);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  ReceiptNumber = dataAccessHandler.getGenerateReceiptNumberforNurseryDispatch(
                        Queries.getInstance().getMaxNumberQueryForNurserySaplingDetailsReceiptNumber(financalSubStringYear + days), financalSubStringYear + days);
                android.util.Log.e("====>getGenerateReceiptNumber", "" + ReceiptNumber);
                Log.e("Navigationfrom===398", Navigationfrom + "");
                receiptNumbers = getIntent().getStringArrayListExtra(CommonConstants.reciptNumbers);
                if (validate()) {
//                    receiptNumbers = getIntent().getStringArrayListExtra(CommonConstants.reciptNumbers);
                    List<LinkedHashMap> adddispatchdetails = new ArrayList<>();
                    LinkedHashMap map = new LinkedHashMap();
                    map.put("PlotCode", CommonConstants.PLOT_CODE);
                    map.put("SaplingPickUpDate", convertDateToServerFormat(pickup_date.getText().toString().trim()));
                    map.put("NoOfSaplingsDispatched", sapling_dispatched.getText().toString().trim());
                    map.put("NoOfImportedSaplingsDispatched", imported_sapling_dispatches.getText().toString().trim());
                    map.put("NoOfIndigenousSaplingsDispatched", indigenous_sapling_dispatches.getText().toString().trim());
                    //map.put("ReceiptNumber", spinner_advance_receiptNo.getSelectedItem());
                    map.put("ReceiptNumber", ReceiptNumber);
                    map.put("CreatedByUserId", CommonConstants.USER_ID);
                    map.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    map.put("UpdatedByUserId", CommonConstants.USER_ID);
                    map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                    map.put("NurseryId", null);
                    map.put("SaplingSourceId", null);
                    map.put("SaplingVendorId", null);
                    map.put("CropVarietyId", null);
                    map.put("PurchaseDate", null);
                    map.put("BatchNo", null);

                    if (Navigationfrom.equals("WithRequest")) {
                        map.put("AdvanceReceiptNumber", Advace_Receiptnum);


//            total_saplings_dispatch_layout.setVisibility(View.GONE);
                    }
                    else
                    {
                        map.put("AdvanceReceiptNumber", spinner_advance_receiptNo.getSelectedItem().toString().trim());
                    }

                    map.put("Comments", comments.getText().toString().trim());
                    map.put("ServerUpdatedStatus", 0);

                    adddispatchdetails.add(map);
                    Log.e("======histoty", adddispatchdetails + "");


                    dataAccessHandler.saveData(DatabaseKeys.TABLE_NURSERYSAPLING_DETAILS, adddispatchdetails, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {


                            if (success) {
                                dataAccessHandler.updateDispatchSaplingsStatus(Queries.getInstance().updateDispatchSaplingsStatus(spinner_advance_receiptNo.getSelectedItem().toString()), new ApplicationThread.OnComplete<Integer>() {
                                    @Override
                                    public void execute(boolean success, Integer result, String message) {
                                        if (success) {
                                            finish();
                                            UiUtils.showCustomToastMessage("Dispatch Saplings Added Successfully", AddDispatchSaplingsActivity.this, 0);
                                        } else {
                                            Log.e("UpdateStatus", message);
                                            UiUtils.showCustomToastMessage(message, AddDispatchSaplingsActivity.this, 1);
                                        }
                                    }
                                });
                            } else {
                                Log.d(HarvestingActivity.class.getSimpleName(), "==>  Analysis ==> TABLE_HarvestorVisitHistory INSERT Failed");
                                UiUtils.showCustomToastMessage("Failed to Insert Dispatch Saplings", AddDispatchSaplingsActivity.this, 1);
                            }


                        }
                    });
                }
            }
        });

        imported_sapling_dispatches.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                updateTotalDispatched();
            }
        });

        indigenous_sapling_dispatches.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                updateTotalDispatched();
            }
        });

        pickup_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH); // Note: 0-based
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddDispatchSaplingsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Format selected date to dd-MM-yyyy
                                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                        selectedDay, selectedMonth + 1, selectedYear);
                                pickup_date.setText(formattedDate);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

//        advancereceiptdataMap = dataAccessHandler.getGenericData(Queries.getInstance().getAdvancedDetails("ADV-0725-102"));
//        spinner_advance_receiptNo.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(this, "Advance Receipt", advancereceiptdataMap));

//        pending_advance_sapling.setText(pendingSaplings + "");

        spinner_advance_receiptNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString().trim();
                Log.d("SpinnerSelected", "Selected item: " + selectedItem);
                if (!selectedItem.equals("Select Receipt Number")){
                    fetchAdvanceDetailsByReceiptNumber(selectedItem);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle when nothing is selected
            }
        });


    }

    private void getPendingSaplingsCount(String receiptNumber) {


/*        dataAccessHandler.getNurserySaplingDetailsForPendingSaplingsCount(
                Queries.getInstance().getNurserySaplingDetailsForPendingSaplingsCount(CommonConstants.PLOT_CODE, receiptNumber),
                new ApplicationThread.OnComplete<List<AdvancedDetails>>() {
                    @Override
                    public void execute(boolean success, final List<AdvancedDetails> receiptNumbers, String msg) {

                        Log.d("getPendingSaplingsCount", "Query: " + Queries.getInstance().getNurserySaplingDetailsForPendingSaplingsCount(CommonConstants.PLOT_CODE, receiptNumber));
                        ApplicationThread.uiPost("", "", new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SpinnerSelected", "Selected item: " + selectedItem);
                                UiUtils.showCustomToastMessage("Called Pending Saplings Count", AddDispatchSaplingsActivity.this, 0);
                            }
                        });
                    }
                });*/

        dataAccessHandler.getNurserySaplingDetailsForPendingSaplingsCount(
                Queries.getInstance().getNurserySaplingDetailsForPendingSaplingsCount(CommonConstants.PLOT_CODE, receiptNumber),
                new ApplicationThread.OnComplete<Integer>() {
                    @Override
                    public void execute(boolean success, final Integer totalDispatched, String msg) {
                        ApplicationThread.uiPost("getPendingSaplingsCount", "", new Runnable() {
                            @Override
                            public void run() {
//                                noOfSaplingsAdvancePaidFor = mDispatchRequestsModel.getNoOfSaplingsToDispatch();

                                pending_advance_sapling.setText(String.valueOf(noOfSaplingsAdvancePaidFor - totalDispatched));
                                Log.d("PendingCount", "Queries: " + Queries.getInstance().getNurserySaplingDetailsForPendingSaplingsCount(CommonConstants.PLOT_CODE, receiptNumber));

                                Log.d("PendingCount", "Total Dispatched: " + totalDispatched + " | " + (noOfSaplingsAdvancePaidFor - totalDispatched));

                                AdvancePaidPENDINGSAPLINGS = (noOfSaplingsAdvancePaidFor - totalDispatched);

                                Log.d("PendingCount ======",  "PendingCount"+ AdvancePaidPENDINGSAPLINGS );

                            }
                        });
                    }
                }
        );


    }

    // Method to calculate total safely
    private void updateTotalDispatched() {
        String imported = imported_sapling_dispatches.getText().toString().trim();
        String indigenous = indigenous_sapling_dispatches.getText().toString().trim();

        int importedVal = imported.isEmpty() ? 0 : Integer.parseInt(imported);
        int indigenousVal = indigenous.isEmpty() ? 0 : Integer.parseInt(indigenous);

        sapling_dispatched.setText(String.valueOf(importedVal + indigenousVal));
    }

    public boolean validate() {

        if (validateSnipper(spinner_advance_receiptNo)) {
            UiUtils.showCustomToastMessage("Please Select Advance Receipt Number", this, 1);
            spinner_advance_receiptNo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(imported_sapling_dispatches.getText().toString())) {
            // farmer_f_name.setError(getResources().getString(R.string.error_farmer_first_name));
            UiUtils.showCustomToastMessage("Please Enter Imported Sapling Dispatches", this, 1);
            imported_sapling_dispatches.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(imported_sapling_dispatches.getText().toString())) {
            try {
                int value = Integer.parseInt(imported_sapling_dispatches.getText().toString().trim());
                if (value == 0) {
                    UiUtils.showCustomToastMessage("Imported Sapling  cannot be 0", this, 1);
                    imported_sapling_dispatches.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Please enter a valid number", this, 1);
                imported_sapling_dispatches.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(indigenous_sapling_dispatches.getText().toString())) {
            // farmer_last_name.setError(getResources().getString(R.string.error_farmer_last_name));
            UiUtils.showCustomToastMessage("Please Enter Indigenous Sapling Dispatches",this, 1);
            indigenous_sapling_dispatches.requestFocus();
            return false;
        }

        int dispatchCount = noOfSaplingsAdvancePaidFor;
        int noOfIndigenousSaplings = parseInt(indigenous_sapling_dispatches.getText().toString().trim());
        int noOfImportedSaplings = parseInt(imported_sapling_dispatches.getText().toString().trim());

        Log.d("Total Saplings", noOfIndigenousSaplings + " " + noOfImportedSaplings + " " + dispatchCount);

        if ((noOfIndigenousSaplings + noOfImportedSaplings) > dispatchCount) {
            UiUtils.showCustomToastMessage("Total Saplings Count should be greater than Dispatch Sapling Count", this, 1);
            return false;
        }
        if (Navigationfrom.equals("WithoutRequest")) {
        if ( (noOfIndigenousSaplings + noOfImportedSaplings) > AdvancePaidPENDINGSAPLINGS) {
            UiUtils.showCustomToastMessage("Dispatch Sapling Count should be Less than or equal to Pending Saplings Count", this, 1);
            return false;
        }
        }
//        indigenousSaplings importedSaplings
        if (importedSaplings < noOfImportedSaplings) {
            UiUtils.showCustomToastMessage("No of Imported Saplings should be greater than Dispatch Imported Saplings Count", this, 1);
            return false;
        }

        if (indigenousSaplings < noOfIndigenousSaplings) {
            UiUtils.showCustomToastMessage("No of Indigenous Saplings should be greater than Dispatch Indigenous Saplings Count", this, 1);
            return false;
        }

        if (pickup_date.getText().toString().trim().isEmpty()) {
            UiUtils.showCustomToastMessage("Please Select Pickup Date", this, 1);
            return false;
        }

        return true;

    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean validateSnipper(Spinner spinner) {
        Object selectedItem = spinner.getSelectedItem();
        return selectedItem == null || selectedItem.toString().trim().isEmpty() || (selectedItem.equals("Select Receipt Number"));
    }

    public void fetchAdvanceDetailsByReceiptNumber(String receiptNumber) {
        dataAccessHandler.fetchAdvanceDetailsByReceiptNumber(
                Queries.getInstance().getAdvancedDetailsByReceiptNumber(receiptNumber),
                new ApplicationThread.OnComplete<List<AdvancedDetails>>() {
                    @Override
                    public void execute(boolean success, final List<AdvancedDetails> receiptNumbers, String msg) {
                        ApplicationThread.uiPost("fetchAdvanceDetailsByReceiptNumber", "", new Runnable() {
                            @Override
                            public void run() {
                                Log.d("fetchAdvanceDetailsByReceiptNumber", "Queries" + Queries.getInstance().getAdvancedDetailsByReceiptNumber(receiptNumber));
                                Log.d("fetchAdvanceDetailsByReceiptNumber", "receiptNumbers" + receiptNumbers.size());
                                if (!receiptNumbers.isEmpty()) {
                                    AdvancedDetails mAdvancedDetails = receiptNumbers.get(0);
                                    noOfSaplingsAdvancePaidFor = mAdvancedDetails.getNoOfSaplingsAdvancePaidFor();
                                    importedSaplings = mAdvancedDetails.getNoOfImportedSaplingsToBeIssued();
                                    indigenousSaplings = mAdvancedDetails.getNoOfIndigenousSaplingsToBeIssued();
                                    total_saplings_dispatch.setText(String.valueOf(noOfSaplingsAdvancePaidFor));
                                    if (total_saplings_dispatch != null) {
                                        total_saplings_dispatch.setText(String.valueOf(noOfSaplingsAdvancePaidFor));
                                    } else {
                                        Log.e("DISPATCH_ERROR", "total_saplings_dispatch is null!");
                                    }

                                    total_saplings_dispatch.setEnabled(false);
                                    getPendingSaplingsCount(receiptNumber);
                                    /*AdvancedDetails mAdvancedDetails = receiptNumbers.get(0);

//                                    total_saplings_dispatch.setText(mDispatchRequestsModel.getNoOfSaplingsToDispatch().toString());
                                    total_saplings_dispatch.setText(String.valueOf(mAdvancedDetails.getNoOfSaplingsAdvancePaidFor()));

                                    noOfSaplingsAdvancePaidFor = mAdvancedDetails.getNoOfSaplingsAdvancePaidFor();
//                                    importedSaplings = mDispatchRequestsModel.getNoOfImportedSaplingsToDispatch();
                                    importedSaplings = mAdvancedDetails.getNoOfImportedSaplingsToBeIssued();
                                    imported_sapling_dispatches.setText(String.valueOf(importedSaplings));
                                    imported_sapling_dispatches.setEnabled(false);

//                                    indigenousSaplings = mDispatchRequestsModel.getNoOfIndigenousSaplingsToDispatch();
                                    indigenousSaplings = mAdvancedDetails.getNoOfIndigenousSaplingsToBeIssued();
                                    indigenous_sapling_dispatches.setText(String.valueOf(indigenousSaplings));
                                    indigenous_sapling_dispatches.setEnabled(false);

                                    sapling_dispatched.setText(String.valueOf(importedSaplings + indigenousSaplings));
                                    sapling_dispatched.setEnabled(false);
                                    comments.setText(mAdvancedDetails.getComments());*/
                                } else {
                                    Log.d("fetchAdvanceDetailsByReceiptNumber", "Queries" + Queries.getInstance().getReceiptNumbersAgainstPlotCode(CommonConstants.PLOT_CODE));
                                    Log.d("fetchAdvanceDetailsByReceiptNumber", "receiptNumbers" + receiptNumbers);
//                                    UiUtils.showCustomToastMessage("No Receipt Numbers Found", AddDispatchSaplingsActivity.this, 0);
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
// Navigate to home screen
            Intent intent = new Intent(this, ViewDispatchRequests.class); // Replace with your actual home screen activity
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
        Intent intent = new Intent(this, ViewDispatchRequests.class); // Replace with your actual home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }
}
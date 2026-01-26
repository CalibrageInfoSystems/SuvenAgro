package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmersDataforImageUploading;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotAuditDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AddRaiseDispatchSaplingsRequestActivity extends AppCompatActivity {
    String[]  receiptNumber ={"Select Advance Receipt Number","receiptNo1","receiptNo2"};
    EditText date_of_lifting , no_indigenous_saplings_dispatch, no_imported_saplings_dispatch, comments, advance_receipt_number, sapling_dispatched2;
    //    Spinner receipt_number;

    private DataAccessHandler dataAccessHandler;
    Button save_btn;
    AdvancedDetails model;
    private int noOfSaplingsAdvancePaidFor = 0;
    private int importedSaplings = 0;
    private int indigenousSaplings = 0;
    Spinner payment, plantation;
    ArrayList<FarmersDataforImageUploading> farmersdata;
    ArrayList<PlotAuditDetails> plotAuditDetailsData;
    TextView fieldCode,growerCode,growerName,fieldVillage, dispatch_sapling_count, imp_saplings_paid_for, ind_saplings_paid_for;
    String fullname = "", middleName = "";
    private LinkedHashMap<String, String> paymentmodeMap,PlantaionMethodMap;
    private String dateOfLiftingForApi = "";

    private String financalSubStringYear = "";// this will hold yyyy-MM-dd format
    public int financialYear;
    private String days = "";
    Button view_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_raise_dispatch_saplings_request);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }


        TextView liftingDateLabel = findViewById(R.id.expected_date);
        TextView dispatchSaplingLabel = findViewById(R.id.dispatch_count);

        date_of_lifting = findViewById(R.id.expected_lifting_date);
        dispatch_sapling_count = findViewById(R.id.dispatch_sapling_count);
        imp_saplings_paid_for = findViewById(R.id.imp_saplings_paid_for);
        ind_saplings_paid_for = findViewById(R.id.ind_saplings_paid_for);
        no_indigenous_saplings_dispatch = findViewById(R.id.no_indigenous_saplings_dispatch);
        no_imported_saplings_dispatch = findViewById(R.id.no_imported_saplings_dispatch);
        comments = findViewById(R.id.comments);
        advance_receipt_number = findViewById(R.id.advance_receipt_number);
        sapling_dispatched2 = findViewById(R.id.sapling_dispatched2);
        save_btn = findViewById(R.id.save_btn);
        view_details = findViewById(R.id.view_details);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Dispatch Saplings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        model = (AdvancedDetails) getIntent().getSerializableExtra(CommonConstants.advancedModel);

        if (model != null) {
            advance_receipt_number.setText(model.getReceiptNumber());
            advance_receipt_number.setFocusable(false);

            noOfSaplingsAdvancePaidFor = model.getNoOfSaplingsAdvancePaidFor();
            importedSaplings = model.getNoOfImportedSaplingsToBeIssued();
            indigenousSaplings = model.getNoOfIndigenousSaplingsToBeIssued();
            dispatch_sapling_count.setText(" :  " + noOfSaplingsAdvancePaidFor);
//            fieldCode.setText(" :  " + CommonConstants.PLOT_CODE);
            imp_saplings_paid_for.setText(" :  " + importedSaplings);
            ind_saplings_paid_for.setText(" :  " + indigenousSaplings);

        }

//        setRedStarLabel(liftingDateLabel,"Expected Date of Lifting ");
//        setRedStarLabel(dispatchSaplingLabel,"Dispatch Sapling Count ");
        view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRaiseDispatchSaplingsRequestActivity.this, NewDispatchDetails.class);
                startActivity(intent);
            }
        });
        dataAccessHandler = new DataAccessHandler(this);

        String  DispatchrequestCode1 = dataAccessHandler.getGenerateReceiptNumberforDispatchrequest(
                Queries.getInstance().getMaxNumberQueryForSaplingDispatchRequestReceiptNumber(financalSubStringYear+days),financalSubStringYear+days);
        android.util.Log.e("====>DispatchrequestCode165", "" + DispatchrequestCode1);

        date_of_lifting.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddRaiseDispatchSaplingsRequestActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Display format: DD/MM/YYYY
                        String displayDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                        date_of_lifting.setText(displayDate);
                        date_of_lifting.setError(null);

                        // Store/send format: YYYY-MM-DD
                        dateOfLiftingForApi = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        save_btn.setOnClickListener(v->{

            String  DispatchrequestCode = dataAccessHandler.getGenerateReceiptNumberforDispatchrequest(
                    Queries.getInstance().getMaxNumberQueryForSaplingDispatchRequestReceiptNumber(financalSubStringYear+days),financalSubStringYear+days);
            Log.e("====>DispatchrequestCode", "" + DispatchrequestCode);
//            if (validateForm()) {
//                List<LinkedHashMap> dataToInsert = new ArrayList<>();
//                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//
//                map.put("AdvanceId", model.getAdvanceReceivedArea());
//                map.put("ReceiptNumber", advance_receipt_number.getText().toString());
//                map.put("NoOfIndigenousSaplingsToDispatch", parseInt(no_indigenous_saplings_dispatch.getText().toString()));
//                map.put("NoOfImportedSaplingsToDispatch", parseInt(no_imported_saplings_dispatch.getText().toString()));
//                map.put("StatusId", 1);
//                map.put("NoOfSaplingsToDispatch", parseInt(dispatch_sapling_count.getText().toString()));
//                map.put("ExpDateOfPickup", date_of_lifting.getText());
//                map.put("IsActive", 1);
//                map.put("Comments", comments.getText());
//
//
//                map.put("CreatedByUserId", CommonConstants.USER_ID);
//                map.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                map.put("UpdateByUserId", CommonConstants.USER_ID);
//                map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                map.put("ServerUpdatedStatus", 0);
//
//                Log.d("AddRaiseDispatchSaplingsRequestActivity", map.toString());
//                dataToInsert.add(map);
//
//                /*dataAccessHandler.saveData("SaplingDispatchRequest", dataToInsert, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Toast.makeText(AddRaiseDispatchSaplingsRequestActivity.this, "Dispatch Saplings are Added Successfully!", Toast.LENGTH_SHORT).show();
//                            finish(); // or clear the fields
//                        } else {
//                            Toast.makeText(AddRaiseDispatchSaplingsRequestActivity.this, "Failed to Submit Dispatch Saplings", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });*/
//            }
            if (validateForm()) {
                List<LinkedHashMap> dataToInsert = new ArrayList<>();
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();

                map.put("AdvanceReceiptNumber", model.getReceiptNumber());
              //  map.put("ReceiptNumber", advance_receipt_number.getText().toString());
                map.put("ReceiptNumber", DispatchrequestCode);
                map.put("NoOfIndigenousSaplingsToDispatch", parseInt(no_indigenous_saplings_dispatch.getText().toString()));
                map.put("NoOfImportedSaplingsToDispatch", parseInt(no_imported_saplings_dispatch.getText().toString()));
                map.put("StatusId", 852);
                map.put("NoOfSaplingsToDispatch", parseInt(sapling_dispatched2.getText().toString()));
                map.put("ExpDateOfPickup", dateOfLiftingForApi);
                map.put("IsActive", 1);
                map.put("Comments", comments.getText());


                map.put("CreatedByUserId", CommonConstants.USER_ID);
                map.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                map.put("UpdatedByUserId", CommonConstants.USER_ID);
                map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                map.put("ServerUpdatedStatus", 0);

                Log.d("AddRaiseDispatchSaplingsRequestActivity", map.toString());
                dataToInsert.add(map);

                dataAccessHandler.saveData("SaplingDispatchRequest", dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            UiUtils.showCustomToastMessage("Dispatch Saplings are Added Successfully!", AddRaiseDispatchSaplingsRequestActivity.this, 0);
                            finish(); // or clear the fields
                        } else {
                            UiUtils.showCustomToastMessage("Failed to Submit Dispatch Saplings", AddRaiseDispatchSaplingsRequestActivity.this, 1);
                        }

                    }
                });
            }
        });
        initView();
        setViews();
    }

    private void initView() {

        fieldCode = findViewById(R.id.fieldCode);
        growerCode = findViewById(R.id.growerCode);
        growerName = findViewById(R.id.growerName);
        fieldVillage = findViewById(R.id.fieldVillage);
        payment = findViewById(R.id.spinnerModePayment);
        plantation = findViewById(R.id.spinnerPlantationType);
    }

    private void setViews() {
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


//        paymentmodeMap = dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
//        if (paymentmodeMap != null) {
//            ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.fromMap(paymentmodeMap, "Mode Of Payments"));
//            adapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            payment.setAdapter(adapterPayment);
//        }

        paymentmodeMap = dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
        if (paymentmodeMap != null) {
            ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.fromMap(paymentmodeMap, "Mode Of Payments"));
            adapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            payment.setAdapter(adapterPayment);

            if (model != null) {
                String paymentKey = String.valueOf((int) Double.parseDouble(String.valueOf(model.getModeOfPayment())));
                String selectedPayment = paymentmodeMap.get(paymentKey);
                if (selectedPayment != null) {
                    int position = adapterPayment.getPosition(selectedPayment);
                    payment.setSelection(position);
                    Log.d("Payment Adapter", " Selected Payment : "+ selectedPayment);
                    payment.setEnabled(false);
                }
            }
        }


        no_imported_saplings_dispatch.addTextChangedListener(new TextWatcher() {

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

        no_indigenous_saplings_dispatch.addTextChangedListener(new TextWatcher() {

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

        PlantaionMethodMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("106"));
        if (PlantaionMethodMap != null) {
            ArrayAdapter<String> adapterPlantation = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.fromMap(PlantaionMethodMap, "Type Of Plantation"));
            adapterPlantation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            plantation.setAdapter(adapterPlantation);

            if (model != null) {
                String plantationKey = String.valueOf((int) Double.parseDouble(String.valueOf(model.getPlantationTypeId())));
                String selectedPlantation = PlantaionMethodMap.get(plantationKey);
                if (selectedPlantation != null) {
                    int position = adapterPlantation.getPosition(selectedPlantation);
                    plantation.setSelection(position);
                    Log.d("Plantation Adapter", "Selected Plantation: " + selectedPlantation);

                    plantation.setEnabled(false);
                } else {
                    Log.e("Plantation Adapter", "Plantation type not found for key: " + plantationKey);
                }
            }
        }
    }

    private void updateTotalDispatched() {
        String imported = no_imported_saplings_dispatch.getText().toString().trim();
        String indigenous = no_indigenous_saplings_dispatch.getText().toString().trim();

        int importedVal = imported.isEmpty() ? 0 : Integer.parseInt(imported);
        int indigenousVal = indigenous.isEmpty() ? 0 : Integer.parseInt(indigenous);

        sapling_dispatched2.setText(String.valueOf(importedVal + indigenousVal));
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }


    private boolean validateForm() {

        String receipt = advance_receipt_number.getText().toString().trim();
        String lifting_date = date_of_lifting.getText().toString().trim();
        String dispatch_count = dispatch_sapling_count.getText().toString().trim();
        String noOfIndigenous = no_indigenous_saplings_dispatch.getText().toString().trim();
        String noOfImported = no_imported_saplings_dispatch.getText().toString().trim();
        Log.d("Total Saplings 1", noOfImported + " | " + noOfIndigenous + " | " + dispatch_count);

        if (receipt.isEmpty()){
            UiUtils.showCustomToastMessage("Please Enter Advance Receipt Number", this, 1);
            return false;
        }

        if (lifting_date.isEmpty()){
            UiUtils.showCustomToastMessage("Please Select Expected Date of Lifting", this, 1);
            return false;
        }

        if (dispatch_count.isEmpty()){
            UiUtils.showCustomToastMessage("Please Enter Dispatch Sapling Count", this, 1);
            return false;

        }

        int dispatchCount = noOfSaplingsAdvancePaidFor;
        int noOfIndigenousSaplings = parseInt(noOfIndigenous);
        int noOfImportedSaplings = parseInt(noOfImported);

        Log.d("Total Saplings", noOfIndigenousSaplings + " | " + noOfImportedSaplings + " | " + dispatchCount);
        //        indigenousSaplings importedSaplings
        if (importedSaplings < noOfImportedSaplings) {
            UiUtils.showCustomToastMessage("No of Imported Saplings should be greater than Dispatch Imported Saplings Count", this, 1);
            return false;
        }

        if (indigenousSaplings < noOfIndigenousSaplings) {
            UiUtils.showCustomToastMessage("No of Indigenous Saplings should be greater than Dispatch Indigenous Saplings Count", this, 1);
            return false;
        }

        if ((noOfIndigenousSaplings + noOfImportedSaplings) <= 0){
            UiUtils.showCustomToastMessage("Please Enter Dispatch Saplings", this, 1);
            return false;
        }

        if ((noOfIndigenousSaplings + noOfImportedSaplings) > dispatchCount) {
            UiUtils.showCustomToastMessage("Total Saplings Count should be greater than Dispatch Sapling Count", this, 1);
            return false;
        }
        return true;
    }

    private void setRedStarLabel(TextView textView, String labelText) {
        SpannableStringBuilder builder = new SpannableStringBuilder(labelText);
        SpannableString redStar = new SpannableString("*");
        redStar.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(redStar);
        textView.setText(builder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
// Navigate to home screen
            Intent intent = new Intent(this, RaiseDispatchSaplingsRequestActivity.class); // Replace with your actual home screen activity
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
        Intent intent = new Intent(this, RaiseDispatchSaplingsRequestActivity.class); // Replace with your actual home screen activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }

}


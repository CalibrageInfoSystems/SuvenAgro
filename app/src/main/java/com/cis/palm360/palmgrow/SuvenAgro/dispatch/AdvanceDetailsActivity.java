package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CommonUtilsNavigation;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.PalmOilDatabase;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmersDataforImageUploading;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotAuditDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvanceMst;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.kal.rackmonthpicker.MonthType;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdvanceDetailsActivity extends AppCompatActivity {


    EditText month_of_planting, advance_received_date, surveyNumberET, advance_received_area, Cheque_number, Cheque_date, Deposited_bank_name,upi_number;
    Spinner payment, plantation;
    ArrayList<FarmersDataforImageUploading> farmersdata;
    AdvancedDetails adavancedetails;
    String navfrom;
    ArrayList<PlotAuditDetails> plotAuditDetailsData;
    private DataAccessHandler dataAccessHandler;
    private PalmOilDatabase palmOilDatabase;
    TextView fieldCode, growerCode, growerName, fieldVillage;
    String fullname = "", middleName = "";
    Button addBtn;
    LinearLayout check_layout, deposited_bank_layout,upi_layout;
    private LinkedHashMap<String, String> paymentmodeMap, PlantaionMethodMap;
    EditText imported_saplings, indigenous_saplings, saplings_advance_paid, imported_saplings_price, indigenous_saplings_price, total_saplings_price,
            total_transportation_cost, grower_imported_price, grower_indigenous_price, grower_contribution_received, grower_contribution_transportation,
            subsidy_price_imported, subsidy_price_indigenous, sapling_subsidy_price, subsidy_transportation_cost, comments,upiNumber;

    Float etTotalImportedTransportationCost,
            etImportedFarmerContributionTransportationCost, etTotalIndigenousTransportationCost, etIndigenousFarmerContributionTransportationCost;
    private String selectedpaymentmodeId;

    private String advanceReceivedDateForApi;
    private String chequeDateForApi;
    private boolean isUserInteraction = false;
    private String Expected_MonthsendFormat;
    private String financalSubStringYear = "";// this will hold yyyy-MM-dd format
    public int financialYear;
    private String days = "";
    TextWatcher saplingWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateSaplings();

            editAdvanceDetailsCalculations();
        }

        private void validateSaplings() {
            try {
                String importedStr = imported_saplings.getText().toString().trim();
                String indigenousStr = indigenous_saplings.getText().toString().trim();
                String advanceStr = saplings_advance_paid.getText().toString().replace(",", "").trim();

                // Integer-only check
                int imported = importedStr.isEmpty() ? 0 : Integer.parseInt(importedStr);
                int indigenous = indigenousStr.isEmpty() ? 0 : Integer.parseInt(indigenousStr);
                int advance = advanceStr.isEmpty() ? 0 : Integer.parseInt(advanceStr);

                int totalIssued = imported + indigenous;

//                if (totalIssued != advance) {
//                    Toast.makeText(AdvanceDetailsActivity.this, "Total Saplings Issued Must Match Advance Paid For (" + advance + ")", Toast.LENGTH_SHORT).show();
//                }

            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Please enter valid integer values only", AdvanceDetailsActivity.this, 1);
            }
        }
    };
    private void editAdvanceDetailsCalculations() {
        try {
            int importedCount = safeParseInt(imported_saplings);
            int indigenousCount = safeParseInt(indigenous_saplings);

            float importedPrice = safeParseFloat(imported_saplings_price);
            float indigenousPrice = safeParseFloat(indigenous_saplings_price);

            float farmerContribImported = safeParseFloat(grower_imported_price);
            float farmerContribIndigenous = safeParseFloat(grower_indigenous_price);

            // Total saplings price
            float totalSaplingsPrice = (importedCount * importedPrice) + (indigenousCount * indigenousPrice);
            total_saplings_price.setText(String.format(Locale.US, "%.2f", totalSaplingsPrice));

            // Farmer contribution received
            float farmerContributionReceived = (importedCount * farmerContribImported) + (indigenousCount * farmerContribIndigenous);
            grower_contribution_received.setText(String.format(Locale.US, "%.2f", farmerContributionReceived));

            // Subsidy prices
            float subsidyPriceImported = importedPrice - farmerContribImported;
            float subsidyPriceIndigenous = indigenousPrice - farmerContribIndigenous;
            subsidy_price_imported.setText(String.format(Locale.US, "%.2f", subsidyPriceImported));
            subsidy_price_indigenous.setText(String.format(Locale.US, "%.2f", subsidyPriceIndigenous));

            // Saplings subsidy
            float saplingsSubsidyPrice = (importedCount * subsidyPriceImported) + (indigenousCount * subsidyPriceIndigenous);
            sapling_subsidy_price.setText(String.format(Locale.US, "%.2f", saplingsSubsidyPrice));

            // Transport costs (default to 0 if not set)
            float totalImportedTransport = etTotalImportedTransportationCost != null ? etTotalImportedTransportationCost : 0f;
            float importedFarmerTransport = etImportedFarmerContributionTransportationCost != null ? etImportedFarmerContributionTransportationCost : 0f;
            float totalIndigenousTransport = etTotalIndigenousTransportationCost != null ? etTotalIndigenousTransportationCost : 0f;
            float indigenousFarmerTransport = etIndigenousFarmerContributionTransportationCost != null ? etIndigenousFarmerContributionTransportationCost : 0f;

            // Even if indigenousCount = 0, this will still calculate
            float subsidyTransportCost = (importedCount * totalImportedTransport) + (indigenousCount * totalIndigenousTransport);
            float farmerTransportCost = (importedCount * importedFarmerTransport) + (indigenousCount * indigenousFarmerTransport);
            float totalTransportCost = subsidyTransportCost + farmerTransportCost;

            // Always bind to UI
            subsidy_transportation_cost.setText(String.format(Locale.US, "%.2f", subsidyTransportCost));
            grower_contribution_transportation.setText(String.format(Locale.US, "%.2f", farmerTransportCost));
            total_transportation_cost.setText(String.format(Locale.US, "%.2f", totalTransportCost));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float safeFloat(Float value) {
        return value != null ? value : 0f;
    }
    private int safeParseInt(EditText editText) {
        try {
            String value = editText.getText().toString().trim();
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private float safeParseFloat(EditText editText) {
        try {
            String value = editText.getText().toString().trim();
            return value.isEmpty() ? 0f : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0f;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advance_details);
        adavancedetails = (AdvancedDetails) DataManager.getInstance().getDataFromManager(DataManager.ADVANCE_DETAILS_UPDATE);


        navfrom = getIntent().getExtras().getString("isFromUpdate");

        Log.d("navfrom", navfrom);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Plantation DD");

            // Enable back arrow
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        dataAccessHandler = new DataAccessHandler(AdvanceDetailsActivity.this);
        palmOilDatabase = new PalmOilDatabase(this);
        initView();
        setViews();
        bindData();


    }

    private void bindData() {


        if (adavancedetails != null) {

            Log.d("adavancedetails", adavancedetails.getReceiptNumber());

            Double modeOfPaymentDouble = adavancedetails.getModeOfPayment();
            int modeOfPayment = modeOfPaymentDouble.intValue(); // safely convert to int

            Log.d("modeofpayment", modeOfPayment + "");

            String paymentMode = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getlookupdata(modeOfPayment)
            );

            Log.d("paymentMode", paymentMode + "");
            String inputDateStr = adavancedetails.getDateOfAdvanceReceived(); // e.g., "27/06/2025"
            String formattedDate = "";

            try {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date date = inputFormat.parse(inputDateStr);
                if (date != null) {
                    formattedDate = outputFormat.format(date); // e.g., "2025-06-27"
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            advance_received_date.setText(formattedDate + "");
            // initializeSpinnerWithValue(payment, adavancedetails.getDateOfAdvanceReceived() + "");
//            initializeSpinnerWithValue(payment, paymentMode + "");

            payment.setSelection(CommonUtilsNavigation.getvalueFromHashMap(paymentmodeMap, modeOfPayment));


            Log.d("typeofplantationId", adavancedetails.getPlantationTypeId() + "");

//            surveyNumberET.setText(adavancedetails.getSurveyNumber() + "");
            String surveyNumber = adavancedetails.getSurveyNumber();

            if (surveyNumber == null || surveyNumber.equalsIgnoreCase("null")) {
                surveyNumberET.setText("");
            } else {
                surveyNumberET.setText(surveyNumber);
            }

//            month_of_planting.setText(adavancedetails.getExpectedMonthOfPlanting() + "");
            String inputDate = adavancedetails.getExpectedMonthOfPlanting(); // e.g., "2025-04-30" or "2023-08-24T11:44:39.13"

            if (inputDate != null && !inputDate.equalsIgnoreCase("null") && !inputDate.trim().isEmpty()) {
                Date parsedDate = null;

                // Possible input formats
                String[] possibleFormats = {
                        "yyyy-MM-dd'T'HH:mm:ss.SS",
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "yyyy-MM-dd"
                };

                for (String format : possibleFormats) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                        parsedDate = inputFormat.parse(inputDate);
                        if (parsedDate != null) break;
                    } catch (ParseException ignored) {
                    }
                }

                if (parsedDate != null) {
                    try {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH);
                        String MonthformattedDate = outputFormat.format(parsedDate); // e.g., "April-2025"
                        month_of_planting.setText(MonthformattedDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        month_of_planting.setText("");
                    }
                } else {
                    month_of_planting.setText("");
                }

            } else {
                month_of_planting.setText("");
            }

            String typeofPlantation = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getTypecdDesc(adavancedetails.getPlantationTypeId())
            );

            Log.d("typeofplantationIdname", typeofPlantation + "");


//            initializeSpinnerWithValue(plantation, typeofPlantation + "");

            plantation.setSelection(CommonUtilsNavigation.getvalueFromHashMap(PlantaionMethodMap, adavancedetails.getPlantationTypeId()));


            Log.d("getSaplingsAdvancePaid", adavancedetails.getNoOfSaplingsAdvancePaidFor() + "");
            Log.d("getAdvanceReceivedArea", adavancedetails.getAdvanceReceivedArea() + "");

            advance_received_area.setText(adavancedetails.getAdvanceReceivedArea() + "");
            imported_saplings.setText(adavancedetails.getNoOfImportedSaplingsToBeIssued() + "");
            indigenous_saplings.setText(adavancedetails.getNoOfIndigenousSaplingsToBeIssued() + "");
            saplings_advance_paid.setText(adavancedetails.getNoOfSaplingsAdvancePaidFor() + "");
            imported_saplings_price.setText(adavancedetails.getTotalPriceOfImportedSaplings() + "");
            indigenous_saplings_price.setText(String.format(Locale.US, "%.2f", adavancedetails.getTotalPriceOfIndigenousSaplings()));
            total_saplings_price.setText(String.format(Locale.US, "%.2f", adavancedetails.getTotalSaplingsPrice()));
            total_transportation_cost.setText(String.format(Locale.US, "%.2f", adavancedetails.getTotalTransportationcost()));
            grower_imported_price.setText(String.format(Locale.US, "%.2f", adavancedetails.getFarmerContributionPriceForImportedSaplings()));
            grower_indigenous_price.setText(String.format(Locale.US, "%.2f", adavancedetails.getFarmerContributionPriceForIndigenousSaplings()));

            grower_contribution_received.setText(String.format(Locale.US, "%.2f", adavancedetails.getFarmerContributionReceived()));
            grower_contribution_transportation.setText(String.format(Locale.US, "%.2f", adavancedetails.getFarmerContributionTransportationcost()));
            subsidy_price_imported.setText(String.format(Locale.US, "%.2f", adavancedetails.getSubsidyPriceForImportedSaplings()));
            subsidy_price_indigenous.setText(String.format(Locale.US, "%.2f", adavancedetails.getSubsidyPriceForIndigenousSaplings()));

            if (adavancedetails.getChequeNo().equalsIgnoreCase("null") || adavancedetails.getChequeNo().isEmpty()) {
                Cheque_number.setText("");
            } else {
                Cheque_number.setText(adavancedetails.getChequeNo());
            }

            String BanknameStr = adavancedetails.getDepositedBankName();

            if (BanknameStr == null || BanknameStr.equalsIgnoreCase("null") || BanknameStr.trim().isEmpty()) {
                Deposited_bank_name.setText("");
            }else{
                Deposited_bank_name.setText(adavancedetails.getDepositedBankName());
            }
            String upinum = adavancedetails.getUPINo();

            if (upinum == null || upinum.equalsIgnoreCase("null") || upinum.trim().isEmpty()) {
                upi_number.setText("");
            }else{
                upi_number.setText(adavancedetails.getUPINo());
            }

            String chequeDateStr = adavancedetails.getChequeDate();

            if (chequeDateStr == null || chequeDateStr.equalsIgnoreCase("null") || chequeDateStr.trim().isEmpty()) {
                Cheque_date.setText("");
            } else {
                String formattedcheckDate = "";
                try {
                    // Input format is yyyy-MM-dd (from DB or server)
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    // Desired output format is dd/MM/yyyy (for UI)
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    Date date = inputFormat.parse(chequeDateStr);
                    if (date != null) {
                        formattedcheckDate = outputFormat.format(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Cheque_date.setText(formattedcheckDate);
            }


            sapling_subsidy_price.setText(adavancedetails.getSubsidyPrice() + "");
            subsidy_transportation_cost.setText(adavancedetails.getSubsidyTransportationcost() + "");
            String Comments = adavancedetails.getComments();

            if (Comments == null || Comments.equalsIgnoreCase("null")) {
                comments.setText("");
            } else {
                comments.setText(Comments);
            }
            // comments.setText(adavancedetails.getComments() + "");

        } else {
            adavancedetails = new AdvancedDetails();
        }


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

    private void setViews() {
        imported_saplings.addTextChangedListener(saplingWatcher);
        indigenous_saplings.addTextChangedListener(saplingWatcher);
        farmersdata = dataAccessHandler.getFarmerDetailsforImageUploading(Queries.getInstance().getfarmerdetailsforimageuploading(CommonConstants.FARMER_CODE));

        if (!TextUtils.isEmpty(farmersdata.get(0).getMiddleName()) && !
                farmersdata.get(0).getMiddleName().equalsIgnoreCase("null")) {
            middleName = farmersdata.get(0).getMiddleName();
        }
        fullname = farmersdata.get(0).getFirstName().trim() + " " + middleName + " " + farmersdata.get(0).getLastName().trim();

        Log.d("Grower Name", fullname + "");
        Log.d("Grower Code", CommonConstants.FARMER_CODE);
        Log.d("Grower Plot Code", CommonConstants.PLOT_CODE);

        growerName.setText(" :  " + fullname);
        growerCode.setText(" :  " + CommonConstants.FARMER_CODE);
        fieldCode.setText(" :  " + CommonConstants.PLOT_CODE);
        plotAuditDetailsData = dataAccessHandler.getPlotDetailsforAudit(Queries.getInstance().getPlotDetailsforAudit(CommonConstants.PLOT_CODE));
        Log.d("TotalPalmArea", plotAuditDetailsData.get(0).getTotalPalmArea() + "");
        Log.d("CropVareity", plotAuditDetailsData.get(0).getCropVareity() + "");
        Log.d("DateofPlanting", plotAuditDetailsData.get(0).getDateofPlanting() + "");
        Log.d("ClusterName", plotAuditDetailsData.get(0).getClusterName() + "");
        Log.d("VillageName", plotAuditDetailsData.get(0).getVillageName() + "");
        Log.d("MandalName", plotAuditDetailsData.get(0).getMandalName() + "");
        Log.d("DistrictName", plotAuditDetailsData.get(0).getDistrictName() + "");
        fieldVillage.setText(" :  " + plotAuditDetailsData.get(0).getVillageName());
        paymentmodeMap = dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
        if (paymentmodeMap != null) {
            ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.fromMap(paymentmodeMap, "Mode Of Payments"));
            adapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            payment.setAdapter(adapterPayment);

        }
        payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                String selectedType1 = payment.getSelectedItem().toString().trim();

                if (position1 > 0) {
                    selectedpaymentmodeId = CommonUtils.getKeyFromValue(paymentmodeMap, selectedType1);

                    // Only clear fields if user selected
                    if (isUserInteraction) {
                        Cheque_number.setText("");
                        Cheque_date.setText("");
                        Deposited_bank_name.setText("");
                        upi_number.setText("");
                    }

                    if ("182".equals(selectedpaymentmodeId)) {
                        check_layout.setVisibility(View.VISIBLE);
                        deposited_bank_layout.setVisibility(View.GONE);
                        upi_layout.setVisibility(View.GONE);
                    } else if ("230".equals(selectedpaymentmodeId)) {
                        check_layout.setVisibility(View.GONE);
                        deposited_bank_layout.setVisibility(View.VISIBLE);
                        upi_layout.setVisibility(View.GONE);
                    }else if ("351".equals(selectedpaymentmodeId)) {
                        check_layout.setVisibility(View.GONE);
                        deposited_bank_layout.setVisibility(View.GONE);
                        upi_layout.setVisibility(View.VISIBLE);
                    } else {
                        check_layout.setVisibility(View.GONE);
                        deposited_bank_layout.setVisibility(View.GONE);
                        upi_layout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        PlantaionMethodMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("106"));
        if (PlantaionMethodMap != null) {
            ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.fromMap(PlantaionMethodMap, "Type Of Plantation"));
            adapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            plantation.setAdapter(adapterPayment);

        }
        plantation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                String selectedType1 = plantation.getSelectedItem().toString().trim();
                String areaStr = advance_received_area.getText().toString().trim();

                if (!areaStr.isEmpty() && !selectedType1.equalsIgnoreCase("Select Type of Plantation")) {
                    try {
                        double area = Double.parseDouble(areaStr);
                        String plantationId = CommonUtils.getKeyFromValue(PlantaionMethodMap, selectedType1);
                        double saplingCountPerAcre = dataAccessHandler.getSaplingCount(plantationId);

                        int totalSaplings = (int) Math.round(area * saplingCountPerAcre);  // Rounded value
                        saplings_advance_paid.setText(String.valueOf(totalSaplings));
                        Log.e("====>totalSaplings", "" + totalSaplings);
                    } catch (Exception e) {
                        e.printStackTrace();
                        saplings_advance_paid.setText("");
                    }
                } else {
                    saplings_advance_paid.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        advance_received_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {

                    String input = s.toString();
                    if (input.contains(".")) {
                        int index = input.indexOf(".");
                        if (input.length() - index - 1 > 2) {
                            s.delete(index + 3, input.length());
                            return; // exit early to avoid parsing mid-edit
                        }
                    }

                    String areaStr = s.toString().trim();
                    String selectedPlantation = plantation.getSelectedItem().toString();
                    if (!areaStr.isEmpty() && !selectedPlantation.equalsIgnoreCase("Select Type of Plantation")) {
                        double area = Double.parseDouble(areaStr);

                        // Get plantationMethodId from map
                        String plantationId = CommonUtils.getKeyFromValue(PlantaionMethodMap, selectedPlantation);
                        double saplingCountPerAcre = dataAccessHandler.getSaplingCount(plantationId);

                        int totalSaplings = (int) Math.round(area * saplingCountPerAcre);  // Rounded value
                        saplings_advance_paid.setText(String.valueOf(totalSaplings));
                        Log.e("====>totalSaplings", "" + totalSaplings);
                    } else {
                        saplings_advance_paid.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SavedPlotCode", CommonConstants.PLOT_CODE);

                financalSubStringYear = String.valueOf(financialYear).substring(2, 4);
                String ReceiptNumber = dataAccessHandler.getGenerateReceiptNumber(
                        Queries.getInstance().getMaxNumberQueryForReceiptNumber(financalSubStringYear + days), financalSubStringYear + days);
                Log.e("====>gerateReceiptNumber", "" + ReceiptNumber);


                String input = month_of_planting.getText().toString(); // e.g., "May-2025"
                try {
                    // Parse the input string into a Date object
                    SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH);
                    Date date = inputFormat.parse(input);

                    // Use Calendar to determine the last day of the month
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, lastDay);

                    // Format to "yyyy/MM/dd"
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Expected_MonthsendFormat = outputFormat.format(calendar.getTime());

                    Log.d("FormattedDate", Expected_MonthsendFormat); // Output like "2025/05/31"

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (dataisValid()) {


                    List<LinkedHashMap> dataToInsert = new ArrayList<>();
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();


                    if ("fromUpdate".equalsIgnoreCase(navfrom)) {

                        String receiptNumberforedit = getIntent().getStringExtra("receiptNumberforedit");
                        if (receiptNumberforedit == null) {
                            UiUtils.showCustomToastMessage("Missing receipt number", AdvanceDetailsActivity.this, 1);
                            return;
                        }

                        String formattedChequeDate = "";
                        String formattedAdvanceReceivedDate = "";

                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                            String chequeDateInput = Cheque_date.getText().toString().trim();
                            String advanceDateInput = advance_received_date.getText().toString().trim();

                            if (!chequeDateInput.isEmpty()) {
                                Date chequeDate = inputFormat.parse(chequeDateInput);
                                if (chequeDate != null) {
                                    formattedChequeDate = outputFormat.format(chequeDate);
                                }
                            }

                            if (!advanceDateInput.isEmpty()) {
                                Date advanceDate = inputFormat.parse(advanceDateInput);
                                if (advanceDate != null) {
                                    formattedAdvanceReceivedDate = outputFormat.format(advanceDate);
                                    Log.e("AdvanceReceiveddate", formattedAdvanceReceivedDate);
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            UiUtils.showCustomToastMessage("Invalid date format", AdvanceDetailsActivity.this, 1);
                            return;
                        }

                        //  Map<String, Object> map = new HashMap<>();
                        map.put("PlotCode", CommonConstants.PLOT_CODE);
                        map.put("FarmerContributionReceived", parseDouble(grower_contribution_received.getText().toString()));
                        map.put("DateOfAdvanceReceived", formattedAdvanceReceivedDate);
                        map.put("ExpectedMonthOfPlanting", Expected_MonthsendFormat);
                        map.put("NoOfSaplingsAdvancePaidFor", parseInt(saplings_advance_paid.getText().toString()));
                        map.put("NoOfImportedSaplingsToBeIssued", parseInt(imported_saplings.getText().toString()));
                        map.put("NoOfIndigenousSaplingsToBeIssued", parseInt(indigenous_saplings.getText().toString()));
                        map.put("AdvanceReceivedArea", parseDouble(advance_received_area.getText().toString()));
                        map.put("SurveyNumber", surveyNumberET.getText().toString().trim());
                        map.put("ReceiptNumber", receiptNumberforedit);
                        map.put("Comments", comments.getText().toString().trim());

                        map.put("CreatedByUserId", adavancedetails.getCreatedByUserId());
                        map.put("CreatedDate", adavancedetails.getCreatedDate());
                        map.put("UpdatedByUserId", CommonConstants.USER_ID);
                        map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                        map.put("FarmerContributionPriceForIndigenousSaplings", parseDouble(grower_indigenous_price.getText().toString()));
                        map.put("FarmerContributionPriceForImportedSaplings", parseDouble(grower_imported_price.getText().toString()));
                        map.put("ModeOfPayment", CommonUtils.getKeyFromValue(paymentmodeMap, payment.getSelectedItem().toString()));
                        map.put("ChequeNo", Cheque_number.getText().toString().trim());
                        map.put("ChequeDate", formattedChequeDate);
                        map.put("BankId", 0);

                        map.put("TotalPriceOfImportedSaplings", parseDouble(imported_saplings_price.getText().toString()));
                        map.put("TotalPriceOfIndigenousSaplings", parseDouble(indigenous_saplings_price.getText().toString()));
                        map.put("TotalSaplingsPrice", parseDouble(total_saplings_price.getText().toString()));
                        map.put("SubsidyPriceForImportedSaplings", parseDouble(subsidy_price_imported.getText().toString()));
                        map.put("SubsidyPriceForIndigenousSaplings", parseDouble(subsidy_price_indigenous.getText().toString()));
                        map.put("SubsidyPrice", parseDouble(sapling_subsidy_price.getText().toString()));
                        map.put("TotalTransportationcost", parseDouble(total_transportation_cost.getText().toString()));
                        map.put("FarmerContributionTransportationcost", parseDouble(grower_contribution_transportation.getText().toString()));
                        map.put("SubsidyTransportationcost", parseDouble(subsidy_transportation_cost.getText().toString()));
                        map.put("DepositedBankName", Deposited_bank_name.getText().toString().trim());
                        map.put("PlantationTypeId", CommonUtils.getKeyFromValue(PlantaionMethodMap, plantation.getSelectedItem().toString()));
                        map.put("ServerUpdatedStatus", 0);
                        map.put("FileName", "");
                        map.put("FileExtension", "");
                        map.put("FileLocation", "");
                        map.put("UPINo",  upi_number.getText().toString().trim());

                        //  List<Map<String, Object>> dataToInsert = new ArrayList<>();
                        dataToInsert.add(map);

                        String whereCondition = " where ReceiptNumber = '" + receiptNumberforedit + "'";
                        Log.e("Update", "Data to insert/update: " + dataToInsert);

                        dataAccessHandler.updateData("AdvancedDetails", dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    UiUtils.showCustomToastMessage("Advance Details Updated Successfully", AdvanceDetailsActivity.this, 0);
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                } else {
                                    UiUtils.showCustomToastMessage("Failed to submit advance details", AdvanceDetailsActivity.this, 1);
                                }
                            }
                        });
                    } else {
                        Log.d("Whattodo", "PleaseInsert");
                        map.put("PlotCode", CommonConstants.PLOT_CODE);
                        map.put("FarmerContributionReceived", parseDouble(grower_contribution_received.getText().toString()));
                        map.put("DateOfAdvanceReceived", advanceReceivedDateForApi);
                        //  map.put("DateOfAdvanceReceived",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        map.put("ExpectedMonthOfPlanting", Expected_MonthsendFormat); // Add parsing if used
                        map.put("NoOfSaplingsAdvancePaidFor", parseInt(saplings_advance_paid.getText().toString()));
                        map.put("NoOfImportedSaplingsToBeIssued", parseInt(imported_saplings.getText().toString()));
                        map.put("NoOfIndigenousSaplingsToBeIssued", parseInt(indigenous_saplings.getText().toString()));
                        map.put("AdvanceReceivedArea", parseDouble(advance_received_area.getText().toString()));
                        map.put("SurveyNumber", surveyNumberET.getText().toString().trim());
                        map.put("ReceiptNumber", ReceiptNumber);
                        map.put("Comments", comments.getText().toString().trim());

                        map.put("CreatedByUserId", CommonConstants.USER_ID);
                        map.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        map.put("UpdatedByUserId", CommonConstants.USER_ID);
                        map.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                        map.put("FarmerContributionPriceForIndigenousSaplings", parseDouble(grower_indigenous_price.getText().toString()));
                        map.put("FarmerContributionPriceForImportedSaplings", parseDouble(grower_imported_price.getText().toString()));
                        map.put("ModeOfPayment", CommonUtils.getKeyFromValue(paymentmodeMap, payment.getSelectedItem().toString()));
                        map.put("ChequeNo", Cheque_number.getText().toString().trim());
                        map.put("ChequeDate", chequeDateForApi);
                        map.put("BankId", 0);

                        map.put("TotalPriceOfImportedSaplings", parseDouble(imported_saplings_price.getText().toString()));
                        map.put("TotalPriceOfIndigenousSaplings", parseDouble(indigenous_saplings_price.getText().toString()));
                        map.put("TotalSaplingsPrice", parseDouble(total_saplings_price.getText().toString()));
                        map.put("SubsidyPriceForImportedSaplings", parseDouble(subsidy_price_imported.getText().toString()));
                        map.put("SubsidyPriceForIndigenousSaplings", parseDouble(subsidy_price_indigenous.getText().toString()));
                        map.put("SubsidyPrice", parseDouble(sapling_subsidy_price.getText().toString()));
                        map.put("TotalTransportationcost", parseDouble(total_transportation_cost.getText().toString()));
                        map.put("FarmerContributionTransportationcost", parseDouble(grower_contribution_transportation.getText().toString()));
                        map.put("SubsidyTransportationcost", parseDouble(subsidy_transportation_cost.getText().toString()));
                        map.put("DepositedBankName", Deposited_bank_name.getText().toString().trim());
                        map.put("PlantationTypeId", CommonUtils.getKeyFromValue(PlantaionMethodMap, plantation.getSelectedItem().toString()));
                        map.put("ServerUpdatedStatus", 0);
                        map.put("FileName", "");
                        map.put("FileExtension", "");
                        map.put("FileLocation", "");
                        map.put("UPINo",  upi_number.getText().toString().trim());

                        dataToInsert.add(map);
                        Log.e("====>", "dataToInsert -->submit" + dataToInsert);
                        dataAccessHandler.saveData("AdvancedDetails", dataToInsert, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    UiUtils.showCustomToastMessage("Advance Details Submitted Successfully", AdvanceDetailsActivity.this, 0);
                                    //  UiUtils.showCustomToastMessage(
                                    //  Toast.makeText(AdvanceDetailsActivity.this, "Advance details submitted successfully!", Toast.LENGTH_SHORT).show();
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish(); // or clear the fields
                                } else {
                                    UiUtils.showCustomToastMessage("Failed to submit advance details", AdvanceDetailsActivity.this, 1);

                                }
                            }
                        });

                    }


                }
            }
        });

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

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value.trim());
        } catch (Exception e) {
            return 0f;
        }
    }

    private String getTextValue(EditText editText) {
        return editText.getText().toString();
    }

    private float roundTwoDecimals(float value) {
        return Math.round(value * 100f) / 100f;
    }

    private boolean dataisValid() {
        if (surveyNumberET.getText().toString().trim().isEmpty()) {
            //advance_received_date.setError("Advance Received Date is required");
            UiUtils.showCustomToastMessage("Please Enter Survey Number", this, 1);
            return false;
        }

        if (month_of_planting.getText().toString().trim().isEmpty()) {
            //advance_received_date.setError("Advance Received Date is required");
            UiUtils.showCustomToastMessage("Please Enter Month of Planting", this, 1); //Advance Received Date is required", this, 0);
            return false;
        }

        if (advance_received_date.getText().toString().trim().isEmpty()) {
            //advance_received_date.setError("Advance Received Date is required");
            UiUtils.showCustomToastMessage("Please Enter Advance Received Date", this, 1);
            return false;
        }


        if (payment.getSelectedItemPosition() == 0) {
            UiUtils.showCustomToastMessage("Select Mode of Payment", this, 1);
            return false;
        }

        if ("182".equals(selectedpaymentmodeId)) { // Cheque
            if (TextUtils.isEmpty(Cheque_number.getText().toString().trim())) {
                UiUtils.showCustomToastMessage("Enter Cheque Number", this, 1);
                return false;
            }
            if (TextUtils.isEmpty(Cheque_date.getText().toString().trim())) {
                UiUtils.showCustomToastMessage("Enter Cheque Date", this, 1);
                return false;
            }
        }

        if ("230".equals(selectedpaymentmodeId)) { // Deposited Bank
            if (TextUtils.isEmpty(Deposited_bank_name.getText().toString().trim())) {
                UiUtils.showCustomToastMessage("Enter Deposited Bank Name", this, 1);
                return false;
            }

        }

        if ("351".equals(selectedpaymentmodeId)) { // Deposited Bank
            if (TextUtils.isEmpty(upi_number.getText().toString().trim())) {
                UiUtils.showCustomToastMessage("Enter UPI Number", this, 1);
                return false;
            }

        }

        if (plantation.getSelectedItemPosition() == 0) {
            UiUtils.showCustomToastMessage("Select Type of Plantation", this, 1);
            // Toast.makeText(this, "Select Type of Plantation", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (advance_received_area.getText().toString().trim().isEmpty()) {

            UiUtils.showCustomToastMessage("Please Enter Advance Received Area", this, 1);
            // advance_received_area.setError("Advance Received Area is required");
            return false;
        }

        if (advance_received_area.getText().toString().trim().equals("0")) {

            UiUtils.showCustomToastMessage(" Advance Received Area Should be Greater Than 0", this, 1);
            // advance_received_area.setError("Advance Received Area is required");
            return false;
        }

        try {
            String importedStr = imported_saplings.getText().toString().trim();
            String indigenousStr = indigenous_saplings.getText().toString().trim();
            String advanceStr = saplings_advance_paid.getText().toString().replace(",", "").trim();

            int imported = importedStr.isEmpty() ? 0 : Integer.parseInt(importedStr);
            int indigenous = indigenousStr.isEmpty() ? 0 : Integer.parseInt(indigenousStr);
            int advance = advanceStr.isEmpty() ? 0 : Integer.parseInt(advanceStr);

            int totalIssued = imported + indigenous;

            if (totalIssued != advance) {
                UiUtils.showCustomToastMessage("Total Saplings Issued Must Match Advance Paid For (" + advance + ")", this, 1);
                return false;
            }
        } catch (NumberFormatException e) {
            UiUtils.showCustomToastMessage("Please enter valid integer values for saplings and advance", this, 1);
            return false;
        }

        return true;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        // Label TextViews
//        TextView surveyNumberLabel = findViewById(R.id.survey_number);
        TextView expMonthLabel = findViewById(R.id.exp_month_of_planting);
        TextView advanceDateLabel = findViewById(R.id.advance_receivedDate);
        TextView modeOfPaymentLabel = findViewById(R.id.mode_of_payment);
        TextView typePlantationLabel = findViewById(R.id.type_of_plantation);
        TextView areaLabel = findViewById(R.id.advance_received_area_txt);
        fieldCode = findViewById(R.id.fieldCode);
        growerCode = findViewById(R.id.growerCode);
        growerName = findViewById(R.id.growerName);
        fieldVillage = findViewById(R.id.fieldVillage);
        check_layout = findViewById(R.id.check_layout);
        Cheque_number = findViewById(R.id.Cheque_number);
        Cheque_date = findViewById(R.id.Cheque_date);
        deposited_bank_layout = findViewById(R.id.deposited_bank_layout);
        upi_layout = findViewById(R.id.upi_layout);
        Deposited_bank_name = findViewById(R.id.Deposited_bank_name);
        upi_number = findViewById(R.id.upi_number);
        // Input Fields
        surveyNumberET = findViewById(R.id.surveyNumber);
        month_of_planting = findViewById(R.id.month_of_planting);
        advance_received_date = findViewById(R.id.advance_received_date);
        advance_received_area = findViewById(R.id.advance_received_Area);

        payment = findViewById(R.id.spinnerModePayment);
        plantation = findViewById(R.id.spinnerPlantationType);
        imported_saplings = findViewById(R.id.imported_saplings);
        indigenous_saplings = findViewById(R.id.indigenous_saplings);
        saplings_advance_paid = findViewById(R.id.saplings_advance_paid);
        imported_saplings_price = findViewById(R.id.imported_saplings_price);
        indigenous_saplings_price = findViewById(R.id.indigenous_saplings_price);
        total_saplings_price = findViewById(R.id.total_saplings_price);
        total_transportation_cost = findViewById(R.id.total_transportation_cost);
        grower_imported_price = findViewById(R.id.grower_imported_price);
        grower_indigenous_price = findViewById(R.id.grower_indigenous_price);
        grower_contribution_received = findViewById(R.id.grower_contribution_received);
        grower_contribution_transportation = findViewById(R.id.grower_contribution_transportation);
        subsidy_price_imported = findViewById(R.id.subsidy_price_imported);
        subsidy_price_indigenous = findViewById(R.id.subsidy_price_indigenous);
        sapling_subsidy_price = findViewById(R.id.sapling_subsidy_price);
        subsidy_transportation_cost = findViewById(R.id.subsidy_transportation_cost);
        comments = findViewById(R.id.comments);

        payment.setOnTouchListener((v, event) -> {
            isUserInteraction = true;
            return false; // Let the spinner behave normally
        });
        final Calendar calendar1 = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar1);
        financialYear = fiscalDate.getFiscalYear();
        financalSubStringYear = String.valueOf(financialYear).substring(2, 4);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
            String financalDate = "01/04/" + String.valueOf(financialYear);
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(financalDate);
            long diff = date1.getTime() - date2.getTime();
            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
            days = StringUtils.leftPad(noOfDays, 3, "0");
            android.util.Log.e("====>", "days -->" + days + financialYear + diff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        bindAdvanceDetails();

// Advance Received Date Picker
        advance_received_date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AdvanceDetailsActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Display format: DD/MM/YYYY
                        String displayDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                        advance_received_date.setText(displayDate);
                        advance_received_date.setError(null);

                        // Store/send format: YYYY-MM-DD
                        String apiDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        // Save this value to send to API later
                        advanceReceivedDateForApi = apiDate;  // You must declare this variable in your activity
                    },
                    year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

// Cheque Date Picker
        Cheque_date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AdvanceDetailsActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Display format: DD/MM/YYYY
                        String displayDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                        Cheque_date.setText(displayDate);
                        Cheque_date.setError(null);

                        // Store/send format: YYYY-MM-DD
                        String apiDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        chequeDateForApi = apiDate;  // Declare this variable in your activity
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        month_of_planting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RackMonthPicker rackMonthPicker = new RackMonthPicker(AdvanceDetailsActivity.this)
                        .setLocale(Locale.ENGLISH)
                        .setPositiveButton(new DateMonthDialogListener() {
                            @Override
                            public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                                String monthShortName = new DateFormatSymbols().getShortMonths()[month - 1];
                                String selected = monthShortName + "-" + year;
                                month_of_planting.setText(selected);
                            }
                        })
                        .setNegativeButton(new OnCancelMonthDialogListener() {
                            @Override
                            public void onCancel(AlertDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setMonthType(MonthType.TEXT)
                        .setPositiveText("OK")
                        .setNegativeText("Cancel");
                rackMonthPicker.show();
            }
        });


        // Add Button Functionality
        addBtn = findViewById(R.id.add_btn);
    }
    private void bindAdvanceDetails() {
        dataAccessHandler = new DataAccessHandler(this);

        Integer stateID = dataAccessHandler.getOnlyOneIntValueFromDb(
                Queries.getInstance().getStateIdForPlotCode(CommonConstants.PLOT_CODE));
        Integer ZoneID = dataAccessHandler.getOnlyOneIntValueFromDb(
                Queries.getInstance().getZoneIdForPlotCode(CommonConstants.PLOT_CODE));

        if (ZoneID == null || stateID == null) {
            showWarningDialog("Warning", "Please Do Master Sync");
            return;
        }

        Log.e("getStateIdForPlotCode", "" + stateID + " " + ZoneID);

        String query = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = " + stateID +
                " AND ZoneId = " + ZoneID + " AND SourceOfSaplings = 55 ORDER BY SourceOfSaplings";
        String query2 = "SELECT * FROM AdvanceMst WHERE TypeOfLifting = 578 AND StateId = " + stateID +
                " AND ZoneId = " + ZoneID + " AND SourceOfSaplings = 56 ORDER BY SourceOfSaplings";

        String ReceiptNumber = dataAccessHandler.getGenerateReceiptNumber(
                Queries.getInstance().getMaxNumberQueryForReceiptNumber(financalSubStringYear + days),
                financalSubStringYear + days);
        Log.e("====>ReceiptNumberADV", "" + ReceiptNumber);

        List<AdvanceMst> list = dataAccessHandler.getAdvanceMstData(query);
        List<AdvanceMst> list2 = dataAccessHandler.getAdvanceMstData(query2);

        // ✅ Check for null or empty
        if ((list == null || list.isEmpty())) {
            UiUtils.showCustomToastMessage("There is no Sapling Rates in Masters", this, 1);
            return;
        }

        // Populate Imported Saplings
        if (list != null && !list.isEmpty()) {
            for (AdvanceMst item : list) {
                imported_saplings_price.setText(String.format(Locale.US, "%.2f", item.getTotalSaplingsPrice()));
                subsidy_price_imported.setText(String.format(Locale.US, "%.2f", item.getSubsidyPrice()));
                grower_imported_price.setText(String.format(Locale.US, "%.2f", item.getFarmerContributionReceived()));
                etTotalImportedTransportationCost = item.getSubsidyTransportationCost();
                etImportedFarmerContributionTransportationCost = item.getFarmerContributionTransportationCost();
            }
        }
        else{
            imported_saplings_price.setText("0.00");
            subsidy_price_imported.setText("0.00");
            grower_imported_price.setText("0.00");
        }

        // Populate Indigenous Saplings
        if (list2 != null && !list2.isEmpty()) {
            for (AdvanceMst item2 : list2) {
                indigenous_saplings_price.setText(String.format(Locale.US, "%.2f", item2.getTotalSaplingsPrice()));
                subsidy_price_indigenous.setText(String.format(Locale.US, "%.2f", item2.getSubsidyPrice()));
                grower_indigenous_price.setText(String.format(Locale.US, "%.2f", item2.getFarmerContributionReceived()));
                etTotalIndigenousTransportationCost = item2.getSubsidyTransportationCost();
                etIndigenousFarmerContributionTransportationCost = item2.getFarmerContributionTransportationCost();
            }
        }
        else {
            indigenous_saplings_price.setText("0.00");
            subsidy_price_indigenous.setText("0.00");
            grower_indigenous_price.setText("0.00");
        }
    }

    public void showWarningDialog(String title, String message) {
        final Dialog dialog = new Dialog(AdvanceDetailsActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // or onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRedStarLabel(TextView textView, String labelText) {
        SpannableStringBuilder builder = new SpannableStringBuilder(labelText);
        SpannableString redStar = new SpannableString("*");
        redStar.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(redStar);
        textView.setText(builder);
    }
}

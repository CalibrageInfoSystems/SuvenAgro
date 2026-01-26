package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.ConversionBankDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.ConversionIDProofFragment;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.BankDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataSavingHelper;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigationModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FollowUp;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.IdentityProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Plot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.ui.OilPalmBaseActivity;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;


//Registration Modules Screen
public class RegistrationFlowScreen extends OilPalmBaseActivity implements UpdateUiListener, WaterSoilTypeDialogFragment.onTypeSelected {
    private static final String LOG_TAG = RegistrationFlowScreen.class.getName();
    private Button personalDetailsBtn;
    private Button plotDetailsBtn;
    private Button wspBtn, drip_irrigation;
    private Button plotGeoTagBtn;
    private Button cpBtn;
    private Button referalsBtn;
    private Button marketSurveyBtn;
    private Button idProofBtn;
    private Button bankDetailsBtn;
    private Button finishBtn;
    private LinearLayout areaExtensionRel;
    private ActionBar actionBar;
    public FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    List<IdentityProof> identityProofs = new ArrayList<>();
    private PersonalDetailsFragment personalDetailsFragment;
    private DripIrrigationFragment dripIrrigation;
    private ConversionPotentialFragment conversionPotentialFragment;
    private DataAccessHandler dataAccessHandler;
    List<PlotIrrigationTypeXref> savedIrrigationList;
    private FollowUp followUp;
    boolean isDripSelected = false;
    //Boolean isGeoTagTaken = false;
    private Plot savedPlot;
    List<FileRepository> savedPictureList = new ArrayList<>();
    //Initializing the Class & UI
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.content_registration_flow_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.areaExtensionRel = (LinearLayout) findViewById(R.id.areaExtensionRel);
        this.finishBtn = (Button) findViewById(R.id.finishBtn);
        this.marketSurveyBtn = (Button) findViewById(R.id.marketSurveyBtn);
        this.referalsBtn = (Button) findViewById(R.id.referalsBtn);
        this.cpBtn = (Button) findViewById(R.id.cpBtn);
        this.plotGeoTagBtn = (Button) findViewById(R.id.plotGeoTagBtn);
        this.wspBtn = (Button) findViewById(R.id.wspBtn);
        this.drip_irrigation = (Button) findViewById(R.id.drip_irrigation);
        this.plotDetailsBtn = (Button) findViewById(R.id.plotDetailsBtn);
        this.personalDetailsBtn = (Button) findViewById(R.id.personalDetailsBtn);
        bankDetailsBtn = findViewById(R.id.bankDetailsBtn);
        idProofBtn = findViewById(R.id.idProofBtn);


        if (CommonUtils.isNewPlotRegistration()) {
            setTile(getResources().getString(R.string.existing_farmer_registration));
        } else if (CommonUtils.isFromFollowUp()) {
            setTile(getResources().getString(R.string.followup));
        } else {
            setTile(getResources().getString(R.string.new_farmer_registration));
        }

        dataAccessHandler = new DataAccessHandler(this);


        setviews();

    }

    //Setting the OnClick Listeners
    private void setviews() {
        if (CommonUtils.isFromFollowUp()) {
            if (null != DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS)) {
                Farmer savedFarmerData = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
                 savedPlot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
                if (CommonConstants.FARMER_CODE.equalsIgnoreCase(savedPlot.getFarmercode())) {
                    CommonConstants.FARMER_CODE = savedFarmerData.getCode();
                    CommonConstants.PLOT_CODE = savedPlot.getCode();
                    Log.d("CommonPlotCode", CommonConstants.FARMER_CODE);
                    Log.d("CommonPlotCode", CommonConstants.PLOT_CODE);


                } else {
                    UiUtils.showCustomToastMessage("Grower Code Is Not Matches", RegistrationFlowScreen.this, 1);
                    finish();
                }
            }
            savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
            if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
                savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
            }

            boolean isDripSelected = false;

            if (savedIrrigationList != null && !savedIrrigationList.isEmpty()) {
                for (PlotIrrigationTypeXref model : savedIrrigationList) {
                    if (model.getRecmIrrgId() == 391 && model.getIsDripInstalled() == 1) { // 391 = Drip Irrigation ID
                        isDripSelected = true;
                        break;
                    }
                }
            }

//            if (CommonUiUtils.checkFordripDetails(this)) {
//                drip_irrigation.setVisibility(View.VISIBLE);
//            }
//            else{
//                drip_irrigation.setVisibility(View.GONE);
//            }
            if (isDripSelected) {
                Log.d("HomeCheck", "Drip Irrigation is selected.");

                drip_irrigation.setVisibility(View.VISIBLE);
                // Take action: show message, lock spinner, show info, etc.
            } else {
                drip_irrigation.setVisibility(View.GONE);
                Log.d("HomeCheck", "Drip Irrigation not selected.");
            }
            if (null != DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation)) {
                drip_irrigation.setBackgroundColor(getResources().getColor(R.color.green_dark));
                List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);

                Log.d("DripData", "Total Drip Items: " + dripList.size());

                for (DripIrrigationModel model : dripList) {
                    Log.d("DripData", "statusTypeId: " + model.getStatusTypeId()
                            + ", dripStatusDone: " + model.getDripStatusDone()
                            + ", comments: " + model.getComments()
                            + ", fileLocation: " + model.getFileLocation()
                            + ", date: " + model.getDate());
                }
            }

        }


        personalDetailsBtn.setOnClickListener(this);
        plotDetailsBtn.setOnClickListener(this);
        wspBtn.setOnClickListener(this);
        drip_irrigation.setOnClickListener(this);
        plotGeoTagBtn.setOnClickListener(this);
        cpBtn.setOnClickListener(this);
        referalsBtn.setOnClickListener(this);
        marketSurveyBtn.setOnClickListener(this);
        idProofBtn.setOnClickListener(this);
        bankDetailsBtn.setOnClickListener(this);

        finishBtn.setOnClickListener(this);
        // finishBtn.setEnabled(CommonUiUtils.isMandatoryDataEnteredForNRF());

    }

    //OnClick Listeners
    @Override
    public void onClick(View view) {
        followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);

        // If not found, try fetching from DB
        if (followUp == null) {
            followUp = (FollowUp) dataAccessHandler.getFollowupData(Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }
        int id = view.getId();

        if (id == R.id.personalDetailsBtn) {
            personalDetailsFragment = new PersonalDetailsFragment();
            personalDetailsFragment.setUpdateUiListener(this);
            replaceFragment(personalDetailsFragment);
        }

        if (id == R.id.drip_irrigation) {
            dripIrrigation = new DripIrrigationFragment();
            dripIrrigation.setUpdateUiListener(this);
            replaceFragment(dripIrrigation);
        }

        if (id == R.id.idProofBtn) {

            Farmer f1 = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);

            if (f1 != null) {
                final DataAccessHandler dataAccessHandlerObj = new DataAccessHandler(this);
                boolean recordExistedid = dataAccessHandlerObj.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInIDTable(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE));
                if (recordExistedid) {
                    Bundle idproofsBundle = new Bundle();
                    idproofsBundle.putString("whichScreen", "conversionidproofshomepage");
                    ConversionIDProofFragment conversionIDProofFragment = new ConversionIDProofFragment();
                    conversionIDProofFragment.setUpdateUiListener(this);
                    replaceFragment(conversionIDProofFragment, idproofsBundle);
                    //    replaceFragment(new CropMaintanenceIdProofsDetails());
                } else {
                    Bundle idproofsBundle = new Bundle();
                    idproofsBundle.putString("whichScreen", "conversionidproofshomepage");
                    ConversionIDProofFragment conversionIDProofFragment = new ConversionIDProofFragment();
                    conversionIDProofFragment.setUpdateUiListener(this);
                    replaceFragment(conversionIDProofFragment, idproofsBundle);
                }
            } else {
                UiUtils.showCustomToastMessage("Please Take Personal Details Data", RegistrationFlowScreen.this, 1);
            }

        }
        if (id == R.id.bankDetailsBtn) {
            Farmer f2 = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);

            if (f2 != null) {
                final DataAccessHandler dataAccessHandler = new DataAccessHandler(this);
                boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_FARMERBANK, "FarmerCode", CommonConstants.FARMER_CODE));
                if (recordExisted) {
                    replaceFragment(new BankDetailsFragment());
                } else {
                    Bundle bankBundle = new Bundle();
                    bankBundle.putString("whichScreen", "conversionBankhomepage");
                    ConversionBankDetailsFragment conversionBankDetailsFragment = new ConversionBankDetailsFragment();
                    conversionBankDetailsFragment.setUpdateUiListener(this);
                    replaceFragment(conversionBankDetailsFragment, bankBundle);
                }
            } else {
                UiUtils.showCustomToastMessage("Please Take Personal Details Data", RegistrationFlowScreen.this, 1);

            }
        }
        if (id == R.id.plotDetailsBtn) {

            Farmer f = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);

            if (f != null) {
                PlotDetailsFragment plotDetailsFragment = new PlotDetailsFragment();
                plotDetailsFragment.setUpdateUiListener(this);
                replaceFragment(plotDetailsFragment);
            } else {
                UiUtils.showCustomToastMessage("Please Take Personal Details Data", RegistrationFlowScreen.this, 1);
            }
        }

        if (id == R.id.wspBtn) {
//                replaceFragment(new WSPDetailsFragment());
            FragmentManager fm = getSupportFragmentManager();
            WaterSoilTypeDialogFragment mWaterSoilTypeDialogFragment = new WaterSoilTypeDialogFragment();
            mWaterSoilTypeDialogFragment.setOnTypeSelected(this);
            mWaterSoilTypeDialogFragment.show(fm, "fragment_edit_name");
        }

        if (id == R.id.plotGeoTagBtn) {
            GeoTagFragment geoTagFragment = new GeoTagFragment();
            geoTagFragment.setUpdateUiListener(this);
            replaceFragment(geoTagFragment);
        }

        if (id == R.id.cpBtn) {
            conversionPotentialFragment = new ConversionPotentialFragment();
            conversionPotentialFragment.setUpdateUiListener(this);
            replaceFragment(conversionPotentialFragment);
        }

        if (id == R.id.referalsBtn) {
            ReferralsFragment referralsFragment = new ReferralsFragment();
            referralsFragment.setUpdateUiListener(this);
            replaceFragment(referralsFragment);
        }

        if (id == R.id.marketSurveyBtn) {
            startActivity(new Intent(RegistrationFlowScreen.this, MarketSurveyScreen.class));
        }

        if (id == R.id.finishBtn) {
            String dripError = CommonUiUtils.getDripValidationError(this);
//            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//
//         //   Log.d("DripData", "Total Drip Items: " + dripList.size());
//
//            for (DripIrrigationModel model : dripList) {
//                Log.d("DripData", "statusTypeId: " + model.getStatusTypeId()
//                        + ", dripStatusDone: " + model.getDripStatusDone()
//                        + ", comments: " + model.getComments()
//                        + ", fileLocation: " + model.getFileLocation()
//                        + ", date: " + model.getDate());
//            }
// Try to get DripIrrigation list from DataManager
//                List<DripIrrigationModel> savedList =
//                        (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//
//// If drip is selected, but drip irrigation list is empty/null, insert a new default record
//                if (isDripSelected && (savedList == null || savedList.isEmpty())) {
//                    savedList = new ArrayList<>();
//
//                    DripIrrigationModel model = new DripIrrigationModel();
//                    model.setPlotCode(CommonConstants.PLOT_CODE); // Replace with actual plot code
//                    model.setDripStatusDone(0);
//
//                    // Assuming 832 is the 'Servedone' status type ID
//                    model.setStatusTypeId(826);  // Replace 832 with the actual ID if needed
//                    model.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//
//                    savedList.add(model);
//                    DataManager.getInstance().addData(DataManager.DripIrrigation, savedList);
//
//                    Log.d("DripInsert", "Inserted one default drip irrigation record with status 'No' and type 'Servedone'");
//                }


            Log.d("xxx", "Conversion Score: " + CommonUiUtils.isConversionPotentialScoreis10(this) +"&&"+ (DataManager.getInstance().getDataFromManager(DataManager.PPB1) == null || DataManager.getInstance().getDataFromManager(DataManager.PPB2) == null));
//
//            if (CommonUiUtils.checkFarmerDetailsForConversion(this)) {
//                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please provide Government Grower Code in Personal Details Screen", RegistrationFlowScreen.this, 1);
//            }
            if (CommonUiUtils.checkDifferentlyAbledForConversion(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please Select Differently Abled in Personal Details Screen", RegistrationFlowScreen.this, 1);
            }
           else if (CommonUiUtils.checkFarmerTypeForConversion(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please Select Grower Type in Personal Details Screen", RegistrationFlowScreen.this, 1);
            }
            else if (CommonUiUtils.checkforIdentityDetails(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please provide Aadhaar details.", RegistrationFlowScreen.this, 1);
            } else if (CommonUiUtils.checkBankDetails(this)) {
                UiUtils.showCustomToastMessage("Grower is ready to convert. Please provide Bank Details.", RegistrationFlowScreen.this, 1);
            }
            else if (!CommonUiUtils.checkppbimages(this,savedPlot)){
                UiUtils.showCustomToastMessage("Grower is Ready To Convert, So Please Upload PPB Images in Field Details.", RegistrationFlowScreen.this, 1);
            }


            else if (CommonUiUtils.checkForGeoTag(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert, So Please Take GeoTag", RegistrationFlowScreen.this, 1);
            } else if (CommonUiUtils.checkForWaterSoilPowerDetails(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please Take Water, Soil, Power Details", RegistrationFlowScreen.this, 1);
            } else if (CommonUiUtils.checkHorticultureAndLandType(this)) {
                UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please Select  Land Type in Field Details Screen", RegistrationFlowScreen.this, 1);
            } else if (dripError != null && !dripError.isEmpty()) {
                UiUtils.showCustomToastMessage(
                        dripError, 
                        RegistrationFlowScreen.this,
                        1
                );


            }


           /*        else if (CommonUiUtils.checkFordripDetails(this)) {
                       UiUtils.showCustomToastMessage("Grower is Ready To Convert Yes So Please Take Drip Irrigation Details", RegistrationFlowScreen.this, 1);
                   }*/
               /*    else  if (CommonUiUtils.isDripDetailsEntered()) {
                    UiUtils.showCustomToastMessage("Grower is Ready To Convert , So Please complete all Drip Irrigation steps",RegistrationFlowScreen.this, 1);

                }*/
            // Get FollowUp data


            // If followUp is available and farmer is ready to convert, override button state
/*               else if (followUp != null && followUp.getIsfarmerreadytoconvert() == 1) {
                    ProgressBar.showProgressBar(RegistrationFlowScreen.this, "Please wait data is Inserting in DataBase.....");

                    DataSavingHelper.saveRecordIntoFarmerHistory(this, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            ProgressBar.hideProgressBar();

                            if (success) {
                                Log.e(LOG_TAG, "@@@ address data saved successfully");
                                Log.e(LOG_TAG, String.valueOf(success));

                                String toastMessage = CommonUtils.isNewRegistration()
                                        ? "Data saved successfully"
                                        : "Data updated successfully";

                                UiUtils.showCustomToastMessage(toastMessage, RegistrationFlowScreen.this, 0);

                                // Optionally delay finish() to let the toast be seen
                                new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1500); // 1.5 second delay
                            } else {
                                UiUtils.showCustomToastMessage("Data saving failed", RegistrationFlowScreen.this, 1);
                            }
                        }
                    });
                }*/
            else {
                ProgressBar.showProgressBar(RegistrationFlowScreen.this, "Please wait data is Inserting in DataBase.....");

                DataSavingHelper.saveFarmerAddressData(this, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        ProgressBar.hideProgressBar();

                        if (success) {
                            Log.e(LOG_TAG, "@@@ address data saved successfully");
                            Log.e(LOG_TAG, String.valueOf(success));

                            String toastMessage = CommonUtils.isNewRegistration()
                                    ? "Data saved successfully"
                                    : "Data updated successfully";

                            UiUtils.showCustomToastMessage(toastMessage, RegistrationFlowScreen.this, 0);

                            // Optionally delay finish() to let the toast be seen
                            new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1500); // 1.5 second delay
                        } else {
                            UiUtils.showCustomToastMessage("Data saving failed", RegistrationFlowScreen.this, 1);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiRefresh();
    }

    //Refresh UI based on Fields filled
    public void uiRefresh() {

        Log.d("NRFCheck", "NRF Mandatory Check: " + CommonUiUtils.isMandatoryDataEnteredForNRF());
        if (CommonUtils.isFromFollowUp()) {

            boolean isMandatoryDataEntered = CommonUiUtils.isMandatoryDataEnteredForFollowUp();

            // Set button state based on mandatory data
            finishBtn.setEnabled(isMandatoryDataEntered);
            finishBtn.setFocusable(isMandatoryDataEntered);
            finishBtn.setClickable(isMandatoryDataEntered);
            finishBtn.setAlpha(isMandatoryDataEntered ? 1.0f : 0.5f);

            // Get FollowUp data
            followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);

            // If not found, try fetching from DB
            if (followUp == null) {
                followUp = (FollowUp) dataAccessHandler.getFollowupData(
                        Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
            }

            // If followUp is available and farmer is ready to convert, override button state
            if (followUp != null && followUp.getIsfarmerreadytoconvert() == 1) {
                finishBtn.setEnabled(true);
                finishBtn.setFocusable(true);
                finishBtn.setClickable(true);
                finishBtn.setAlpha(1.0f);
            }


        } else if (!CommonUtils.isFromFollowUp()) {
            finishBtn.setEnabled(CommonUiUtils.isMandatoryDataEnteredForNRF());
            finishBtn.setFocusable(CommonUiUtils.isMandatoryDataEnteredForNRF());
            finishBtn.setClickable(CommonUiUtils.isMandatoryDataEnteredForNRF());
            if (CommonUiUtils.isMandatoryDataEnteredForNRF()) {
                finishBtn.setAlpha(1.0f);
            } else {
                finishBtn.setAlpha(0.5f);
            }
        } else {
            finishBtn.setEnabled(true);
            finishBtn.setFocusable(true);
            finishBtn.setClickable(true);
        }
        Log.d("NRFCheck", "Farmer Address: " + DataManager.getInstance().getDataFromManager(DataManager.FARMER_ADDRESS_DETAILS));
        Log.d("NRFCheck", "Farmer Personal: " + DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS));
        Log.d("NRFCheck", "Plot Address: " + DataManager.getInstance().getDataFromManager(DataManager.PLOT_ADDRESS_DETAILS));
        Log.d("NRFCheck", "Plot Details: " + DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS));
        Log.d("NRFCheck", "Followup: " + DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP));

        if (null != DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS)) {
            Farmer savedFarmerData = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
            CommonConstants.FARMER_CODE = savedFarmerData.getCode();
            personalDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        boolean BankrecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_FARMERBANK, "FarmerCode", CommonConstants.FARMER_CODE));
        boolean IDrecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInIDTable(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE));
        List<?> idProofs = (List<?>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
        if (IDrecordExisted) {
            idProofBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        } else if ((idProofs == null || idProofs.isEmpty()) || (DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA) == null)) {
            idProofBtn.setBackgroundResource(R.drawable.rounded_btn);
        } else {
            idProofBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        if (null != DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS)) {
            bankDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
        if (BankrecordExisted) {
            bankDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
        Log.d("NRFCheck", "TypeOfIrrigation: " + DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation));
        savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
        if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
            savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
        }

        boolean isDripSelected = false;

        if (savedIrrigationList != null && !savedIrrigationList.isEmpty()) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if (model.getRecmIrrgId() == 391 && model.getIsDripInstalled() == 0) { // 391 = Drip Irrigation ID
                    Log.d("NRFCheck", "TypeOfIrrigation: 535" + DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation));
                    isDripSelected = true;
                    break;
                }
                Log.d("NRFCheck", "TypeOfIrrigation:540 " + DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation));
            }
        }
//        if (CommonUiUtils.checkFordripDetails(this)) {
//            drip_irrigation.setVisibility(View.VISIBLE);
//        }
        //    checkFordripDetails
        if (isDripSelected && null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS) ) {
            Log.d("HomeCheck", "Drip Irrigation is selected.");

            drip_irrigation.setVisibility(View.VISIBLE);
            // Take action: show message, lock spinner, show info, etc.
        } else {
            drip_irrigation.setVisibility(View.GONE);
            Log.d("HomeCheck", "Drip Irrigation not selected.");
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation)) {
            drip_irrigation.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }


//        if (null != DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA)) {
//            identityProofs = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
//        }
//        if (null != DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA) && !identityProofs.isEmpty()) {
//            idProofBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
//        } else {
//            idProofBtn.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
//        }
//        if (null != DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS)) {
//            bankDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
//        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS) || CommonUtils.isFromFollowUp()) {
            plotDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryGeoTagCheck(CommonConstants.PLOT_CODE));
        if (existed) {
            plotGeoTagBtn.setEnabled(false);
            plotGeoTagBtn.setFocusable(false);
            plotGeoTagBtn.setClickable(false);
            plotGeoTagBtn.setAlpha(0.5f);
            plotGeoTagBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        } else {
            plotGeoTagBtn.setEnabled(true);
            plotGeoTagBtn.setFocusable(true);
            plotGeoTagBtn.setClickable(true);
            plotGeoTagBtn.setAlpha(1.0f);

            if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG)) {
                plotGeoTagBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
            }
        }
//        DripIrrigation saveddripdata = (DripIrrigation) dataAccessHandler.getDripIrrigationDetails(
//                Queries.getInstance().getdipirrigationBinding(CommonConstants.PLOT_CODE), 0
//        );
//        if (saveddripdata != null) {
//            drip_irrigation.setBackgroundColor(getResources().getColor(R.color.green_dark));
//        }

//        if (CommonUtils.isFromFollowUp()) {
//            ArrayList<WaterResource> mWaterTypeModelList = (ArrayList<WaterResource>) dataAccessHandler.getWaterResourceData(Queries.getInstance().getWaterResourceBinding(CommonConstants.PLOT_CODE), 1);
//            ArrayList<PlotIrrigationTypeXref> msoilTypeIrrigationModelList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
//            if ((null != mWaterTypeModelList && !mWaterTypeModelList.isEmpty()) || (null != msoilTypeIrrigationModelList && !msoilTypeIrrigationModelList.isEmpty())) {
//                wspBtn.setBackgroundColor(getResources().getColor(R.color.gray));
//            }
//        } else {
//            if (null != DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER)
//                    || null != DataManager.getInstance().getDataFromManager(DataManager.SoilType)) {
//                wspBtn.setBackgroundColor(getResources().getColor(R.color.gray));
//            }
//        }

        // Fetch data from DataManager
        List<?> sourceOfWater = (List<?>) DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER);
    ;

// Debugging logs
        Log.d("NRFCheck", "Source Of Water: " + sourceOfWater);


// Check if either list is null or empty

        if ((sourceOfWater == null || sourceOfWater.isEmpty()) ||
                (DataManager.getInstance().getDataFromManager(DataManager.SoilType) == null)) {
            Log.d("NRFCheck", "IF block executed - White Button");
            // Disable button - gray color
            wspBtn.setBackgroundResource(R.drawable.rounded_btn);

        } else {
            Log.d("NRFCheck", "ELSE block executed - Gray Button");
            wspBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
            // Enable button - white or rounded button
           // wspBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            // or
            // wspBtn.setBackgroundResource(R.drawable.rounded_btn);
        }

/*
        Log.d("NRFCheck", "Source Of Water:" + DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER ));
        Log.d("NRFCheck", "Soil Type:" + DataManager.getInstance().getDataFromManager(DataManager.SoilType ));
        if (null != DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER)
                || null != DataManager.getInstance().getDataFromManager(DataManager.SoilType)) {
            wspBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }else {
//            wspBtn.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
//            wspBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            wspBtn.setBackgroundResource(R.drawable.rounded_btn);


        }
*/

        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP)) {
            cpBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

       /* if (null != DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA)) {
            referalsBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }*/
        List<?> referralData = (List<?>) DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA);
        // Debugging logs
        Log.d("NRFCheck", "Referral Data: " + referralData);

        if ((referralData == null || referralData.isEmpty()) ||
                (DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA) == null)) {
            Log.d("NRFCheck", "IF block executed - White Button");
            referalsBtn.setBackgroundResource(R.drawable.rounded_btn);

        } else {
            Log.d("NRFCheck", "ELSE block executed - Gray Button");
            referalsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));

        }

        if (null != DataManager.getInstance().getDataFromManager(DataManager.MARKET_SURVEY_DATA)) {
            marketSurveyBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        if (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_NEW_PLOT)
                || CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_FOLLOWUP)) {
            Farmer selectedFarmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
            if (null != selectedFarmer && CommonUiUtils.isMarketSurveyAddedForFarmer(this, Queries.getInstance().getMarketSurveyFromFarmerCode(selectedFarmer.getCode()))) {
                Log.v(LOG_TAG, "#### checking for market survey already market survey data entered");
                marketSurveyBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
                marketSurveyBtn.setAlpha(0.5f);
                marketSurveyBtn.setEnabled(false);
                marketSurveyBtn.setFocusable(false);
                marketSurveyBtn.setClickable(false);
            }
        }
    }

    @Override
    public void updateUserInterface(int position) {
        Log.v(LOG_TAG, "@@@ ui update called");
        uiRefresh();
    }

    //on Water Type Selected
    @Override
    public void onTypeSelected(int type) {
        AreaWaterTypeFragment areaWaterTypeFragment = new AreaWaterTypeFragment();
        areaWaterTypeFragment.setUpdateUiListener(this);
        SoilTypeFragment soilTypeFragment = new SoilTypeFragment();
        soilTypeFragment.setUpdateUiListener(this);
        replaceFragment(type == 1 ? areaWaterTypeFragment : soilTypeFragment);
    }
}

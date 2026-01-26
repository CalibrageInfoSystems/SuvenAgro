package com.cis.palm360.palmgrow.SuvenAgro.conversion;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areacalculator.PreViewAreaCalScreen;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.AreaWaterTypeFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.DripIrrigationFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.PersonalDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.PlotDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.ReferralsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.SoilTypeFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.WaterSoilTypeDialogFragment;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.BankDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CropMaintanenceIdProofsDetails;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataSavingHelper;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigationModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.IdentityProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.DisplayPlotsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropModel;
import com.cis.palm360.palmgrow.SuvenAgro.ui.OilPalmBaseActivity;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;


//Conversion flow modules
public class ConversionMainFlowActivity extends OilPalmBaseActivity implements UpdateUiListener, WaterSoilTypeDialogFragment.onTypeSelected {

    private Button personalDetailsBtn, conversionbankdetailsBtn, conversionidproofdetailsBtn, plotDetailsBtn,
            plantationdetailsBtn, plotGeoTagBtn, interCropBtn,drip_irrigation,
            wspBtn, digitalcontractBtn, referalsBtn;
    private RelativeLayout contentregistrationflowscreen;
    private ActionBar actionBar;
    private Button finishBtn;
    List<IdentityProof> identityProofs = new ArrayList<>();
    List<CropModel> cropModelList = new ArrayList<>();
    private boolean doubleback = false;
    List<PlotIrrigationTypeXref> savedIrrigationList;
    private DripIrrigationFragment dripIrrigation;
    private DataAccessHandler dataAccessHandler;
    //Initializing the Class & UI Initialization
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.conversion_modules_flow_screen, null);
        contentregistrationflowscreen = parentView.findViewById(R.id.content_conversion_flow_screen);
        baseLayout.addView(contentregistrationflowscreen, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        referalsBtn = findViewById(R.id.referalsBtn);
        digitalcontractBtn = findViewById(R.id.digitalcontractBtn);
        wspBtn = findViewById(R.id.wspBtn);
        conversionidproofdetailsBtn = findViewById(R.id.conversionidproofdetailsBtn);
        plotGeoTagBtn = findViewById(R.id.plotGeoTagBtn);
        interCropBtn = findViewById(R.id.interCropDetailsBtn);
        plantationdetailsBtn = findViewById(R.id.plantationdetailsBtn);
        plotDetailsBtn = findViewById(R.id.plotDetailsBtn);
        conversionbankdetailsBtn = findViewById(R.id.conversionbankdetailsBtn);
        personalDetailsBtn = findViewById(R.id.personalDetailsBtn);
        finishBtn = findViewById(R.id.finishBtn);
        drip_irrigation = (Button) findViewById(R.id.drip_irrigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        updateActionBarTitle(getString(R.string.conversiontitle));
        dataAccessHandler = new DataAccessHandler(this);
        setviews();
    }

    //UI setup
    private void setviews() {
        personalDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));

        // plotDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        DataAccessHandler dataAccessHandler = new DataAccessHandler(this);
        boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE));
        if (recordExisted) {
            conversionidproofdetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
            conversionbankdetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
        boolean sourceResourcerecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_SOILRESOURCE, "PlotCode", CommonConstants.PLOT_CODE));
        boolean waterResourcerecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_WATERESOURCE, "PlotCode", CommonConstants.PLOT_CODE));
        if (waterResourcerecordExisted || sourceResourcerecordExisted) {
            wspBtn.setBackgroundColor(getResources().getColor(R.color.gray));
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



        personalDetailsBtn.setOnClickListener(this);
        conversionbankdetailsBtn.setOnClickListener(this);
        plotDetailsBtn.setOnClickListener(this);
        plantationdetailsBtn.setOnClickListener(this);
        plotGeoTagBtn.setOnClickListener(this);
        wspBtn.setOnClickListener(this);
        drip_irrigation.setOnClickListener(this);
        digitalcontractBtn.setOnClickListener(this);
        referalsBtn.setOnClickListener(this);
        conversionidproofdetailsBtn.setOnClickListener(this);
        interCropBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
    }

    public void updateActionBarTitle(final String title) {
        actionBar.setTitle(title);
    }


    //On Click Listeners

        @Override
        public void onClick(View view) {

            int id = view.getId();



            if (id == R.id.personalDetailsBtn) {
                PersonalDetailsFragment personalDetailsFragment = new PersonalDetailsFragment();
                personalDetailsFragment.setUpdateUiListener(this);
                replaceFragment(personalDetailsFragment);
            }
            else if (id == R.id.conversionidproofdetailsBtn) {
                final DataAccessHandler dataAccessHandlerObj = new DataAccessHandler(this);
                boolean recordExistedid = dataAccessHandlerObj.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInIDTable(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE));
                if (recordExistedid) {
                    replaceFragment(new CropMaintanenceIdProofsDetails());
                } else {
                    Bundle idproofsBundle = new Bundle();
                    idproofsBundle.putString("whichScreen", "conversionidproofshomepage");
                    ConversionIDProofFragment conversionIDProofFragment = new ConversionIDProofFragment();
                    conversionIDProofFragment.setUpdateUiListener(this);
                    replaceFragment(conversionIDProofFragment, idproofsBundle);
                }
            }
            else if (id == R.id.conversionbankdetailsBtn) {
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
            }
            else  if(id == R.id.plotDetailsBtn) {
                PlotDetailsFragment plotDetailsFragment = new PlotDetailsFragment();
                plotDetailsFragment.setUpdateUiListener(this);
                replaceFragment(plotDetailsFragment);
            }
            else if (id == R.id.plantationdetailsBtn) {
                if (null == DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS)) {
                    UiUtils.showCustomToastMessage("First Fill the Field Details", ConversionMainFlowActivity.this, 1);
                } else {
                    ConversionPlantationFragment conversionPlantationFragment = new ConversionPlantationFragment();
                    conversionPlantationFragment.setUpdateUiListener(this);
                    Bundle plantationBundle = new Bundle();
                    plantationBundle.putString("whichScreen", "conversionPlothomepage");
                    replaceFragment(conversionPlantationFragment, plantationBundle);
                }
            }
            else if (id == R.id.wspBtn) {
//                replaceFragment(new WSPDetailsFragment());
                FragmentManager fm = getSupportFragmentManager();
                WaterSoilTypeDialogFragment mWaterSoilTypeDialogFragment = new WaterSoilTypeDialogFragment();
                mWaterSoilTypeDialogFragment.setOnTypeSelected(this);
                mWaterSoilTypeDialogFragment.show(fm, "fragment_edit_name");
            }
            if (id == R.id.drip_irrigation) {
                dripIrrigation = new DripIrrigationFragment();
                dripIrrigation.setUpdateUiListener(this);
                replaceFragment(dripIrrigation);
            }
            else if (id == R.id.plotGeoTagBtn) {
                if (null == DataManager.getInstance().getDataFromManager(DataManager.PLANTATION_CON_DATA)) {
                    UiUtils.showCustomToastMessage("First Fill The Field Details And Plantation Details", ConversionMainFlowActivity.this, 1);
                } else {
                    startActivity(new Intent(ConversionMainFlowActivity.this, PreViewAreaCalScreen.class));
                }

                //startActivity(new Intent(ConversionMainFlowActivity.this, PreViewAreaCalScreen.class));
            }
            else if (id == R.id.interCropDetailsBtn) {
                InterCropDetailsFragment interCropDetailsFragment = new InterCropDetailsFragment();
                interCropDetailsFragment.setUpdateUiListener(this);
                replaceFragment(interCropDetailsFragment);
            }

            else  if (id == R.id.digitalcontractBtn) {
//                if (null == DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG)){
//                    UiUtils.showCustomToastMessage("Please Take the Plot Boundaries", ConversionMainFlowActivity.this, 1);
//                }else {
                ConversionDigitalContractFragment conversionDigitalContractFragment = new ConversionDigitalContractFragment();
                conversionDigitalContractFragment.setUpdateUiListener(this);
                replaceFragment(conversionDigitalContractFragment);
//            }
               }
            else if (id == R.id.referalsBtn) {
                ReferralsFragment referralsFragment = new ReferralsFragment();
                referralsFragment.setUpdateUiListener(this);
                replaceFragment(referralsFragment);
            }
              if (id == R.id.finishBtn) {

                if (!CommonUiUtils.isFarmerPhotoTaken(ConversionMainFlowActivity.this)) {
                    UiUtils.showCustomToastMessage("Please Take Grower Picture", ConversionMainFlowActivity.this, 1);
                }
//                else if (!CommonUiUtils.isConversionPlotDataEntered()) {
//                    UiUtils.showCustomToastMessage("Please Enter Required Field Details", ConversionMainFlowActivity.this, 1);
//                }
                else if (!CommonUiUtils.isWSPowerDataEntered(ConversionMainFlowActivity.this)) {
                    UiUtils.showCustomToastMessage("Please Enter Water Soil Details", ConversionMainFlowActivity.this, 1);
                } else {
                    ProgressBar.showProgressBar(ConversionMainFlowActivity.this, "Please wait data is Updating in DataBase.....");
                    DataSavingHelper.saveFarmerAddressData(this, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                ProgressBar.hideProgressBar();
                                DisplayPlotsFragment.plotCode = "";
                                UiUtils.showCustomToastMessage("Conversion Details Data Updated Successfully", ConversionMainFlowActivity.this, 0);
                                finish();
                            } else {
                                DisplayPlotsFragment.plotCode = "";
                                ProgressBar.hideProgressBar();
                                // Log.pushExceptionToCrashlytics(new OilPalmException("Data saving failed in conversion "+msg+"-"+result));
                                UiUtils.showCustomToastMessage("Data saving failed " + msg + "-" + result, ConversionMainFlowActivity.this, 1);
                            }
                        }
                    });
                }

        }
    }

    @Override
    public void updateUserInterface(int refreshPosition) {
        uiRefresh();
    }

    //Update UI based on conditions

    public void uiRefresh() {
        finishBtn.setEnabled(CommonUiUtils.isMandatoryDataEnteredForConversion(this));
        finishBtn.setFocusable(CommonUiUtils.isMandatoryDataEnteredForConversion(this));
        finishBtn.setClickable(CommonUiUtils.isMandatoryDataEnteredForConversion(this));
        if (CommonUiUtils.isMandatoryDataEnteredForConversion(this)) {
            finishBtn.setAlpha(1.0f);
        } else {
            finishBtn.setAlpha(0.5f);
        }

        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS) || CommonUtils.isFromFollowUp()) {
            plotDetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA)) {
            identityProofs = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA) && !identityProofs.isEmpty()) {
            conversionidproofdetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        } else {
            //conversionidproofdetailsBtn.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS)) {
            conversionbankdetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
        {
            cropModelList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA);
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA))
        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA) && !cropModelList.isEmpty()) {
            interCropBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        } else {
            interCropBtn.setBackground(getResources().getDrawable(R.drawable.rounded_btn));
        }
        if (null != DataManager.getInstance().getDataFromManager(DataManager.PLANTATION_CON_DATA)) {
            plantationdetailsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
       if (null != DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_BOUNDARIES)) {
            plotGeoTagBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        if (ConversionDigitalContractFragment.isContractAgreed) {
            digitalcontractBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

        List<?> sourceOfWater = (List<?>) DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER);
        Log.d("NRFCheck", "Source Of Water: " + sourceOfWater);
        if ((sourceOfWater == null || sourceOfWater.isEmpty()) || (DataManager.getInstance().getDataFromManager(DataManager.SoilType) == null)) {
            Log.d("NRFCheck", "IF block executed - White Button");
            wspBtn.setBackgroundResource(R.drawable.rounded_btn);
        } else {
            Log.d("NRFCheck", "ELSE block executed - Gray Button");
            wspBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }

/*
        if (null != DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER)
                || null != DataManager.getInstance().getDataFromManager(DataManager.SoilType)) {
            wspBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }
*/

        List<?> referralData = (List<?>) DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA);
        Log.d("NRFCheck", "Referral Data: " + referralData);
        if ((referralData == null || referralData.isEmpty()) ||
                (DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA) == null)) {
            Log.d("NRFCheck", "IF block executed - White Button");
            referalsBtn.setBackgroundResource(R.drawable.rounded_btn);
        } else {
            Log.d("NRFCheck", "ELSE block executed - Gray Button");
            referalsBtn.setBackgroundColor(getResources().getColor(R.color.green_dark));
        }
       /* if (null != DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA)) {
            referalsBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }*/
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
    }

    @Override
    public void onTypeSelected(int type) {
        AreaWaterTypeFragment areaWaterTypeFragment = new AreaWaterTypeFragment();
        areaWaterTypeFragment.setUpdateUiListener(this);
        SoilTypeFragment soilTypeFragment = new SoilTypeFragment();
        soilTypeFragment.setUpdateUiListener(this);
        replaceFragment(type == 1 ? areaWaterTypeFragment : soilTypeFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiRefresh();
    }

 /*   @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            uiRefresh();
        }
    }*/


    //What should happen on back click
    @Override
    public void onBackPressed() {

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            uiRefresh();
        }
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            super.onBackPressed();
            fm.popBackStack();

        } else {
            if (doubleback) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finish();
//                    finishAffinity();
                }
            } else {
                doubleback = true;
                // custom dialog
                final Dialog dialog = new Dialog(ConversionMainFlowActivity.this);
                dialog.setContentView(R.layout.custom_alert_dailog);

                Button yesDialogButton = dialog.findViewById(R.id.Yes);
                Button noDialogButton = dialog.findViewById(R.id.No);
                TextView msg = dialog.findViewById(R.id.test);
                msg.setText("If You Want to Exit Click Yes,but The Data Will Clear.....");
                // if button is clicked, close the custom dialog
                yesDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                noDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleback = false;
                    }
                }, 2000);
            }
        }
    }
}

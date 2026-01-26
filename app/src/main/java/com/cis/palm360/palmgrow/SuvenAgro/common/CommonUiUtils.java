package com.cis.palm360.palmgrow.SuvenAgro.common;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.ConversionDigitalContractFragment;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Complaints;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Address;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Disease;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigation;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigationModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmerBank;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FollowUp;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Ganoderma;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.GeoBoundaries;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.IdentityProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.MainPestModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Nutrient;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Plot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.SoilResource;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.WaterResource;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siva on 22/05/17.
 */

// Commonly used UI methods are written here

public class CommonUiUtils {
    private DataAccessHandler dataAccessHandler ;
    //Checks whether all mandatory data is entered or not in Registration Screen
    public static boolean isMandatoryDataEnteredForNRF() {
        return DataManager.getInstance().getDataFromManager(DataManager.FARMER_ADDRESS_DETAILS) != null
                && DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS) != null
                && DataManager.getInstance().getDataFromManager(DataManager.PLOT_ADDRESS_DETAILS) != null
                && DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS) != null
                && DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP) != null;
    }

    //Checks whether all mandatory data is entered or not in Conversion Screen
    public static boolean isMandatoryDataEnteredForConversion(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean idProofsRecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE));
        boolean farmerBankRecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_FARMERBANK, "FarmerCode", CommonConstants.FARMER_CODE));

        return (DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA) != null || idProofsRecordExisted)
                && (DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS) != null || farmerBankRecordExisted)
                && DataManager.getInstance().getDataFromManager(DataManager.PLANTATION_CON_DATA) != null

//                && DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_BOUNDARIES) != null
                && ConversionDigitalContractFragment.isContractAgreed;
    }

    //Checks whether all mandatory data is entered or not in Followup Screen
    public static boolean isMandatoryDataEnteredForFollowUp() {
        return DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP) != null;
    }

    //Checks whether all mandatory data is entered or not in Crop Maintenance Screen
    public static boolean isMandatoryDataEnteredForCropMaintenance() {
        return DataManager.getInstance().getDataFromManager(DataManager.CURRENT_PLANTATION) != null
                && (DataManager.getInstance().getDataFromManager(DataManager.WEEDING_HEALTH_OF_PLANTATION_DETAILS) != null ||   CommonConstants.CURRENT_TREE==0)
                && DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG) != null;

    }

    //Checks whether market survery added or not

    public static boolean isMarketSurveyAddedForFarmer(final Context context, final String query) {
        DataAccessHandler accessHandler = new DataAccessHandler(context);
        return accessHandler.checkValueExistedInDatabase(query);
    }

    //To set the Address Strings
    public static void setGeoGraphicalData(Farmer selectedFarmer, Context context) {
        DataAccessHandler accessHandler = new DataAccessHandler(context);
        CommonConstants.stateId = String.valueOf(selectedFarmer.getStateid());
        CommonConstants.districtId = String.valueOf(selectedFarmer.getDistictid());
        CommonConstants.mandalId = String.valueOf(selectedFarmer.getMandalid());
        CommonConstants.villageId = String.valueOf(selectedFarmer.getVillageid());
        CommonConstants.stateCode = accessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCodeFromId("State", CommonConstants.stateId));
        CommonConstants.districtCode = accessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCodeFromId("District", CommonConstants.districtId));
        CommonConstants.mandalCode = accessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCodeFromId("Mandal", CommonConstants.mandalId));
        CommonConstants.villageCode = accessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCodeFromId("Village", CommonConstants.villageId));
    }

    //To Reset the Data
    public static void resetPrevRegData() {
        DataManager.getInstance().deleteData(DataManager.FARMER_ADDRESS_DETAILS);
        DataManager.getInstance().deleteData(DataManager.FARMER_PERSONAL_DETAILS);
        DataManager.getInstance().deleteData(DataManager.FILE_REPOSITORY);
        DataManager.getInstance().deleteData(DataManager.PLOT_ADDRESS_DETAILS);
        DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS);
        DataManager.getInstance().deleteData(DataManager.PLOT_CURRENT_CROPS_DATA);
        DataManager.getInstance().deleteData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA);
        DataManager.getInstance().deleteData(DataManager.SOURCE_OF_WATER);
        DataManager.getInstance().deleteData(DataManager.SoilType);
        DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
        DataManager.getInstance().deleteData(DataManager.PLOT_GEO_BOUNDARIES);
        DataManager.getInstance().deleteData(DataManager.PLOT_FOLLOWUP);
        DataManager.getInstance().deleteData(DataManager.REFERRALS_DATA);
        DataManager.getInstance().deleteData(DataManager.MARKET_SURVEY_DATA);
        DataManager.getInstance().deleteData(DataManager.OIL_TYPE_MARKET_SURVEY_DATA);
        DataManager.getInstance().deleteData(DataManager.ID_PROOFS_DATA);
        DataManager.getInstance().deleteData(DataManager.FARMER_BANK_DETAILS);
        DataManager.getInstance().deleteData(DataManager.PLANTATION_CON_DATA);
        DataManager.getInstance().deleteData(DataManager.COMPLAINT_DETAILS);
        DataManager.getInstance().deleteData(DataManager.COMPLAINT_REPOSITORY);
        DataManager.getInstance().deleteData(DataManager.COMPLAINT_STATUS_HISTORY);
        DataManager.getInstance().deleteData(DataManager.COMPLAINT_TYPE);
        DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_DETAILS);
        DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_REPOSITORY);
        DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_STATUS_HISTORY);
        DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_TYPE);
        DataManager.getInstance().deleteData(DataManager.LANDLORD_BANK_DATA);
        DataManager.getInstance().deleteData(DataManager.LANDLORD_LEASED_DATA);
        DataManager.getInstance().deleteData(DataManager.LANDLORD_IDPROOFS_DATA);
        DataManager.getInstance().deleteData(DataManager.DripIrrigation);
        DataManager.getInstance().deleteData(DataManager.TypeOfIrrigation);
        DataManager.getInstance().deleteData(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);
        DataManager.getInstance().deleteData(DataManager.MISSING_PLANTATION_IMAGES);
        DataManager.getInstance().deleteData(DataManager.RYTHU_DIARY);
        //  DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
        DataManager.getInstance().deleteData(DataManager.CHEMICAL_DETAILS);
        DataManager.getInstance().deleteData(DataManager.CP_FILE_REPOSITORY_PLANTATION);

        ConversionDigitalContractFragment.isContractAgreed = false;
        CommonConstants.isGeoTagTaken = false;
        CommonConstants.isFromPlotDetails = false;
        CommonConstants.leased = false;
        CommonConstants.isplotupdated = false;
        CommonConstants.PLOT_CODE = "";
        CommonConstants.FARMER_CODE = "";
        CommonConstants.districtId = "";
        CommonConstants.districtIdPlot = "";
    }

    //Checks whether Geotag/Geo Boundaries are taken or not
    public static boolean checkForGeoTag(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryGeoTagCheck(CommonConstants.PLOT_CODE));
        if (existed) {
            return false;
        }
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            return false;
        }

        GeoBoundaries geoBoundaries = (GeoBoundaries) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG);
//        if(CommonUtils.isNewRegistration() && CommonConstants.isFromPlotDetails == false){
//        GeoBoundaries geoBoundaries = (GeoBoundaries) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG);
//            return followUp.getIsfarmerreadytoconvert() == 1 && geoBoundaries == null;
//        }else if (CommonUtils.isNewPlotRegistration() && CommonConstants.isFromPlotDetails == false){
//            GeoBoundaries geoBoundaries = (GeoBoundaries) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG);
//            return followUp.getIsfarmerreadytoconvert() == 1 && geoBoundaries == null;
//        }else{
//            return followUp.getIsfarmerreadytoconvert() == 1;
//        }
        return followUp.getIsfarmerreadytoconvert() == 1 && geoBoundaries == null;
    }

    //Checks whether Identity Proof details entered or not
//    public static boolean checkforIdentityDetails(final Context context){
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        boolean existed=dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryIdentityCheck(CommonConstants.FARMER_CODE));
//        if (existed) {
//            return false;
//        }
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null) {
//            return false;
//        }
//       List<IdentityProof> identityProof=(ArrayList<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
//    //    return followUp.getIsfarmerreadytoconvert() == 1;
//        return followUp.getIsfarmerreadytoconvert() == 1 &&  identityProof == null && identityProof.isEmpty();
//
//    }
    public static boolean checkforIdentityDetails(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        // Check if identity details exist in the database
        boolean identityExists = dataAccessHandler.checkValueExistedInDatabase(
                Queries.getInstance().queryIdentityCheck(CommonConstants.FARMER_CODE)
        );

        if (identityExists) {
            Log.i("IdentityCheck", "Identity details already exist for GrowerCode: " + CommonConstants.FARMER_CODE);

            // Fetch follow-up data to confirm farmer's readiness to convert
            FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
            if (followUp != null && followUp.getIsfarmerreadytoconvert() == 1) {
                Log.d("IdentityCheck", "Grower is ready to convert. Aadhaar verification required.");

                // Retrieve identity proofs from database
                List<IdentityProof> dbProofsList = (List<IdentityProof>) dataAccessHandler.getSelectedIdProofsData(
                        Queries.getInstance().getFarmerIdentityProof(CommonConstants.FARMER_CODE), 1
                );
                List<IdentityProof> identityProofsList = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
                // Check if Aadhaar is present
                boolean hasAadhaar = false;
                if (dbProofsList != null) {
                    for (IdentityProof proof : dbProofsList) {
                        if (proof.getIdprooftypeid() == 60) { // Aadhaar type ID
                            hasAadhaar = true;
                            break;
                        }
                    }
                }
                if (identityProofsList != null) {
                    for (IdentityProof proof : identityProofsList) {
                        if (proof.getIdprooftypeid() == 60) { // Aadhaar type ID
                            hasAadhaar = true;
                            break;
                        }
                    }
                }
                if (!hasAadhaar) {
                    Log.e("IdentityCheck", "Grower is ready to convert, but Aadhaar (Typecdid 60) is missing.");
                    return true; // Missing Aadhaar
                }
            }

            Log.i("IdentityCheck", "Identity details and Aadhaar are valid for GrowerCode: " + CommonConstants.FARMER_CODE);
            return false; // All checks passed
        }

        // If identity details do not exist, perform further checks
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            Log.w("IdentityCheck", "No follow-up data found in DataManager for Farmer Code: " + CommonConstants.FARMER_CODE);
            return false;
        }

        // Fetch identity proofs data
        List<IdentityProof> identityProofsList = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
        if (identityProofsList == null) {
            Log.d("IdentityCheck", "No identity proofs found in DataManager. Fetching from database...");
            identityProofsList = (List<IdentityProof>) dataAccessHandler.getSelectedIdProofsData(
                    Queries.getInstance().getFarmerIdentityProof(CommonConstants.FARMER_CODE), 1
            );
        }

        // Check readiness for conversion
        if (followUp.getIsfarmerreadytoconvert() == 1) {
            if (identityProofsList == null || identityProofsList.isEmpty()) {
                Log.e("IdentityCheck", "Farmer is ready to convert, but no identity proof data is available.");
                return true; // Missing identity proofs
            }

            // Check specifically for Aadhaar (Typecdid == 60)
            boolean hasAadhaar = false;
            for (IdentityProof proof : identityProofsList) {
                if (proof.getIdprooftypeid() == 60) {
                    hasAadhaar = true;
                    break;
                }
            }

            if (!hasAadhaar) {
                Log.e("IdentityCheck", "Farmer is ready to convert, but Aadhaar (Typecdid 60) is missing.");
                return true; // Missing Aadhaar
            }
        }

        Log.i("IdentityCheck", "All identity details are valid for Farmer Code: " + CommonConstants.FARMER_CODE);
        return false; // All checks passed
    }



//    public static boolean checkforIdentityDetails(final Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        boolean identityExists = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryIdentityCheck(CommonConstants.FARMER_CODE)
//        );
//
//        if (identityExists) {
//            return false;
//        }
//
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null) {
//            return false;
//        }
//
//        //    List<IdentityProof> identityProofs = (ArrayList<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
//        List<IdentityProof> identityProofsList = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
//        if (identityProofsList == null) {
//            identityProofsList = (List<IdentityProof>) dataAccessHandler.getSelectedIdProofsData(
//                    Queries.getInstance().getFarmerIdentityProof(CommonConstants.FARMER_CODE), 1
//            );
//            // Check if follow-up indicates readiness to convert
//            if (followUp.getIsfarmerreadytoconvert() == 1) {
//                if (identityProofsList == null || identityProofsList.isEmpty()) {
//                    // No identity proof data available
//                    return true;
//                }
//            }
//                // Check specifically for Aadhaar (Typecdid == 60)
//                boolean hasAadhaar = false;
//                for (IdentityProof proof : identityProofsList) {
//                    if (proof.getIdprooftypeid() == 60) {
//                        hasAadhaar = true;
//                        break;
//                    }
//                }
//
//                // Return true if Aadhaar is missing
//                return !hasAadhaar;
//            }
//
//            return false;
//        }

    //Checks whether Bank details entered or not
    public static boolean checkBankDetails(final Context context){ 
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryBankChecking(CommonConstants.FARMER_CODE));
        if(existed){
            return false;
        }
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if(followUp == null){
            return  false;
        }
        FarmerBank farmerBank = (FarmerBank) DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS);
        return followUp.getIsfarmerreadytoconvert() == 1 && farmerBank == null;

    }

    //Checks whether Horticulture & Land type details entered or not
    public static boolean checkHorticultureAndLandType(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryGeoTagCheck(CommonConstants.PLOT_CODE));
        if (existed) {
            return false;
        }
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            return false;
        }
        Plot plot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        if (plot == null)
        {
            return false;
        }

        Integer landId = plot.getLandTypeId();
        Log.v("@@@landID",""+landId);
        return followUp.getIsfarmerreadytoconvert() == 1  && landId == null && plot.getTotalAreaUnderHorticulture() == 0.0f ;
    }


    //Checks whether Soil,Power & Water details entered or not
    public static boolean checkForWaterSoilPowerDetails(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryWaterResourceCheck(CommonConstants.PLOT_CODE));

        if (existed) {
            return false;
        }
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            return false;
        }
        List<WaterResource> waterResource = (ArrayList<WaterResource>) DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER);
        return followUp.getIsfarmerreadytoconvert() == 1 && waterResource == null;
    }

    public static boolean isConversionPotentialScoreis10(final Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            return false;
        }
        return followUp.getIsfarmerreadytoconvert() == 1;
    }
    public static boolean checkFordripDetails(final Context context, String plotCode) {
        boolean foundInMemory = false;
        boolean foundInDb = false;

        // 1️⃣ Check in-memory (DataManager)
        List<PlotIrrigationTypeXref> savedIrrigationList =
                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

        if (savedIrrigationList != null && !savedIrrigationList.isEmpty()) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if (model != null && model.getRecmIrrgId() == 391 && model.getIsDripInstalled() == 0) {
                    Log.d("DripCheck", "✅ Found in-memory drip irrigation (391) recommended & not installed");
                    foundInMemory = true;
                    break;
                }
            }
        } else {
            Log.d("DripCheck", "ℹ️ No in-memory irrigation list");
        }

        // 2️⃣ Check in Database
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String query = "SELECT * FROM PlotIrrigationTypeXref WHERE PlotCode = '" + plotCode + "'";
        List<PlotIrrigationTypeXref> dbIrrigationList =
                (List<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(query,1);

        if (dbIrrigationList != null && !dbIrrigationList.isEmpty()) {
            for (PlotIrrigationTypeXref model : dbIrrigationList) {
                if (model != null && model.getRecmIrrgId() == 391 && model.getIsDripInstalled() == 0) {
                    Log.d("DripCheck", "✅ Found in DB drip irrigation (391) recommended & not installed");
                    foundInDb = true;
                    break;
                }
            }
        } else {
            Log.d("DripCheck", "ℹ️ No DB irrigation list for plot: " + plotCode);
        }

        // 3️⃣ If found in either place, return true
        return foundInMemory || foundInDb;
    }


//
//    public static boolean checkFordripDetails(final Context context) {
//        List<PlotIrrigationTypeXref> savedIrrigationList =
//                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
//
//        if (savedIrrigationList != null && !savedIrrigationList.isEmpty()) {
//            for (PlotIrrigationTypeXref model : savedIrrigationList) {
//                if (model != null) {
//                    Log.d("DripCheck", "RecmIrrgId: " + model.getRecmIrrgId() + ", IsInstalled: " + model.getIsDripInstalled());
//
//                    if (model.getRecmIrrgId() == 391 && model.getIsDripInstalled() == 0) {
//                        Log.d("DripCheck", "✅ Drip Irrigation (391) recommended and not yet installed");
//                        return true;
//                    }
//                }
//            }
//        } else {
//            Log.d("DripCheck", "❌ savedIrrigationList is null or empty");
//        }
//
//        Log.d("DripCheck", "ℹ️ Drip Irrigation (391) either not recommended or already installed");
//        return false;
//    }


    public static boolean checkFarmerDetailsForConversion(final Context context) {
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null || followUp.getIsfarmerreadytoconvert() != 1) {
            Log.d("ConversionCheck", "FollowUp is null or farmer is not ready to convert.");
            return false;
        }

        Farmer farmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (farmer == null) {
            Log.d("ConversionCheck", "Farmer details are missing.");
            return false;
        }

        String govtFarmerCode = farmer.getGovtFarmerCode();
        Log.v("@@@GovtFarmerCode", "" + govtFarmerCode);

        boolean isGovtFarmerCodeEmpty = govtFarmerCode == null || govtFarmerCode.trim().isEmpty();

        if (isGovtFarmerCodeEmpty) {
          //  Toast.makeText(context, "Govt Farmer Code is required for conversion.", Toast.LENGTH_SHORT).show();
            Log.w("ConversionCheck", "Govt Farmer Code is empty.");
        }

        return isGovtFarmerCodeEmpty;
    }
//    public static boolean checkDifferentlyAbledForConversion(final Context context) {
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null || followUp.getIsfarmerreadytoconvert() != 1) {
//            return false;
//        }
//
//        Farmer farmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
//        if (farmer == null) {
//            return false;
//        }
//
//        Integer isDifferentlyAbled = farmer.getIsDifferentlyAbled(); // nullable Integer
//        boolean isDifferentlyAbledUnset = (isDifferentlyAbled == null);
//
//        return isDifferentlyAbledUnset;
//    }

    public static boolean checkDifferentlyAbledForConversion(final Context context) {
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null || followUp.getIsfarmerreadytoconvert() != 1) {
            return false;
        }

        Farmer farmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (farmer == null) {
            return false;
        }

        String govtFarmerCode = farmer.getGovtFarmerCode();
        Integer isDifferentlyAbled = farmer.getIsDifferentlyAbled(); // nullable Integer
        Integer farmerTypeId = farmer.getFarmerTypeId();

        Log.v("@@@FarmerTypeId", "" + farmerTypeId);

       // boolean isDifferentlyAbledUnset = isDifferentlyAbled == null ;
       boolean isDifferentlyAbledUnset = isDifferentlyAbled == null || isDifferentlyAbled == 0;
      return isDifferentlyAbledUnset;
    }


    public static boolean checkFarmerTypeForConversion(final Context context) {
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null || followUp.getIsfarmerreadytoconvert() != 1) {
            return false;
        }

        Farmer farmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (farmer == null) {
            return false;
        }

        String govtFarmerCode = farmer.getGovtFarmerCode();
        int isDifferentlyAbled = farmer.getIsDifferentlyAbled(); // primitive int
        Integer farmerTypeId = farmer.getFarmerTypeId();

        Log.v("@@@GovtFarmerCode", "" + govtFarmerCode);
        Log.v("@@@IsDifferentlyAbled", "" + isDifferentlyAbled);
        Log.v("@@@FarmerTypeId", "" + farmerTypeId);

        boolean isGovtFarmerCodeEmpty = govtFarmerCode == null || govtFarmerCode.trim().isEmpty();
//        boolean isDifferentlyAbledUnset = isDifferentlyAbled == 0; // assuming 0 means not set
        boolean isFarmerTypeIdNull = farmerTypeId == null;

        return isFarmerTypeIdNull;
    }




    public static boolean checkgvtplotcodeforConversion(final Context context) {
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null || followUp.getIsfarmerreadytoconvert() != 1) {
            return false;
        }
        Plot plotData = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        //Farmer farmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (plotData == null) {
            return false;
        }

        String govtPlot = plotData.getGovtPlotCode();


        Log.v("@@@govtPlot", "" + govtPlot);


        boolean isGovtPlotCodeEmpty = govtPlot == null || govtPlot.trim().isEmpty();


        return isGovtPlotCodeEmpty ;
    }




    public static boolean checkFordripshow(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryDripCheck(CommonConstants.PLOT_CODE));

        if (existed) {
            return false;
        }
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            return false;
        }
        DripIrrigation dripsource = (DripIrrigation) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        return followUp.getIsfarmerreadytoconvert() == 1 && dripsource == null;
    }
    //Checks whether Farmer Photo Taken or not
    public static boolean isFarmerPhotoTaken(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getSelectedFileRepositoryCheckQuery(CommonConstants.FARMER_CODE, 193));
        if (existed) {
            return true;
        }
        FileRepository fileRepository = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.FILE_REPOSITORY);
        return fileRepository != null && fileRepository.getPicturelocation() != null && !fileRepository.getPicturelocation().equalsIgnoreCase("null");
    }
    public static boolean isFarmerPhotoSavedInDB(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getSelectedFileRepositoryCheckQuery(CommonConstants.FARMER_CODE, 193));
        return existed;
    }


    //Checks whether Soil,Power & Water details entered or not in Conversion
    public static boolean isWSPowerDataEntered(final Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean existed = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getWaterResourceBinding(CommonConstants.PLOT_CODE));
        if (existed) {
            return true;
        }
        boolean existed2 = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().getSoilResourceBinding(CommonConstants.PLOT_CODE));
        if (existed2) {
            return true;
        }

        List<WaterResource> mWaterTypeModelList = (ArrayList<WaterResource>) DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER);
        if (mWaterTypeModelList != null) {
            return true;
        }

        SoilResource msoilTypeModel = (SoilResource) DataManager.getInstance().getDataFromManager(DataManager.SoilType);
        return msoilTypeModel != null;

    }

    //Checks whether required plots data is entered or not while conversion
    public static boolean isConversionPlotDataEntered() {
        Plot plotData = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        if (plotData == null) {
            return false;
        } else {
            Integer plotCareTakerStatus = plotData.getIsplothandledbycaretaker();
            return !(plotCareTakerStatus == null || plotCareTakerStatus == 0);
        }
    }
    public static boolean isDripDetailsEntered() {
        // Logging all individual drip activity values
        Log.d("DripCheck", "isFieldMarkingDone: " + CommonConstants.isFieldMarkingDone);
        Log.d("DripCheck", "isPittingDone: " + CommonConstants.isPittingDone);
        Log.d("DripCheck", "isDripSurveyDone: " + CommonConstants.isDripSurveyDone);
        Log.d("DripCheck", "isAdministrationPermissionGranted: " + CommonConstants.isAdministrationPermissionGranted);
        Log.d("DripCheck", "isDripSharePaid: " + CommonConstants.isDripSharePaid);
        Log.d("DripCheck", "isDripInstalled: " + CommonConstants.isDripInstalled);

        boolean isFieldMarkingDone = CommonConstants.isFieldMarkingDone != null && CommonConstants.isFieldMarkingDone == 1;
        boolean isPittingDone = CommonConstants.isPittingDone != null && CommonConstants.isPittingDone == 1;
        boolean isDripSurveyDone = CommonConstants.isDripSurveyDone != null && CommonConstants.isDripSurveyDone == 1;
        boolean isAdministrationPermissionGranted = CommonConstants.isAdministrationPermissionGranted != null && CommonConstants.isAdministrationPermissionGranted == 1;
        boolean isDripSharePaid = CommonConstants.isDripSharePaid != null && CommonConstants.isDripSharePaid == 1;
        boolean isDripInstalled = CommonConstants.isDripInstalled != null && CommonConstants.isDripInstalled == 1;

        Log.d("DripCheck", "isFieldMarkingDone valid: " + isFieldMarkingDone);
        Log.d("DripCheck", "isPittingDone valid: " + isPittingDone);
        Log.d("DripCheck", "isDripSurveyDone valid: " + isDripSurveyDone);
        Log.d("DripCheck", "isAdministrationPermissionGranted valid: " + isAdministrationPermissionGranted);
        Log.d("DripCheck", "isDripSharePaid valid: " + isDripSharePaid);
        Log.d("DripCheck", "isDripInstalled valid: " + isDripInstalled);

        boolean allCompleted = isFieldMarkingDone && isPittingDone && isDripSurveyDone &&
                isAdministrationPermissionGranted && isDripSharePaid && isDripInstalled;

        Log.d("DripCheck", "All drip steps completed: " + allCompleted);

        CommonConstants.isDripCompleted = allCompleted;

//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//
//        if (followUp == null) {
//            Log.d("DripCheck", "FollowUp data is null");
//            return false;
//        } else {
//            Log.d("DripCheck", "FollowUp.isfarmerreadytoconvert: " + followUp.getIsfarmerreadytoconvert());
//        }
//
//        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() != null && followUp.getIsfarmerreadytoconvert() == 1;
//
//        Log.d("DripCheck", "Is farmer ready to convert: " + isFarmerReady);

        boolean finalResult = CommonConstants.isDripCompleted;

        Log.d("DripCheck", "Final result of isDripDetailsEntered: " + finalResult);

        return finalResult;
    }

/*    public static boolean isDripDetailsEntered() {
        // Check if all drip-related activities are completed
        boolean allCompleted =
                CommonConstants.isFieldMarkingDone != null && CommonConstants.isFieldMarkingDone == 1 &&
                        CommonConstants.isPittingDone != null && CommonConstants.isPittingDone == 1 &&
                        CommonConstants.isDripSurveyDone != null && CommonConstants.isDripSurveyDone == 1 &&
                        CommonConstants.isAdministrationPermissionGranted != null && CommonConstants.isAdministrationPermissionGranted == 1 &&
                        CommonConstants.isDripSharePaid != null && CommonConstants.isDripSharePaid == 1 &&
                        CommonConstants.isDripInstalled != null && CommonConstants.isDripInstalled == 1;

        // Set the global flag
        CommonConstants.isDripCompleted = allCompleted;

        // Fetch FollowUp object
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);

        // If follow-up data is not available, drip details cannot be considered entered
        if (followUp == null) {
            return false;
        }

        // Return true only if all drip steps are completed and farmer is ready to convert
        return CommonConstants.isDripCompleted && followUp.getIsfarmerreadytoconvert() != null && followUp.getIsfarmerreadytoconvert() == 1;
    }*/

    //Checks whether required plots data is entered or not while crop maintenance
    public static boolean isConversionPlotAddressDataEntered() {
        Address plotData = (Address) DataManager.getInstance().getDataFromManager(DataManager.VALIDATE_PLOT_ADDRESS_DETAILS);
        if (plotData == null) {
            return false;
        } else {
            String plotCareTakerStatus = plotData.getLandmark();
            return !TextUtils.isEmpty(plotCareTakerStatus);
        }
    }

    //Checks whether required farmer data is entered or not while crop maintenance
    public static boolean isFarmerMandatoryDataEntered() {
        Farmer farmerData = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (farmerData == null) {
            return false;
        } else {
            Integer anualIncomeTypeId = farmerData.getAnnualincometypeid();
//            String gaurdianName = farmerData.getGuardianname();
            return !(anualIncomeTypeId == null || anualIncomeTypeId == 0) ;
        }
    }

    //Checks whether required plot data is entered or not while crop maintenance
    public static boolean isPlotDataEntered() {
        Plot enteredPlot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        if (enteredPlot.getPlotownershiptypeid() == null && enteredPlot.getIsplothandledbycaretaker() == null) {
            return false;
        } else {

            return true ;
        }
    }
    public static boolean isGandermaDatacheck() {
        Ganoderma entereddata = (Ganoderma) DataManager.getInstance()
                .getDataFromManager(DataManager.GANODERMA_DETAILS);

        if (entereddata == null) {
            Log.d("GanodermaDataCheck", "Ganoderma data is null or not entered.");
            return false;
        }

        Log.d("GanodermaDataCheck", "Ganoderma data found: " + entereddata.toString());
        return true;
    }

public static boolean isGandermaDataEntered(Context context) {
    // Retrieve the list of Pest objects
    List<Disease> DiseaseList = (List<Disease>) DataManager.getInstance().getDataFromManager(DataManager.DISEASE_DETAILS);
    DataAccessHandler  dataAccessHandler = new DataAccessHandler(context);
    // Check if the list is null or empty
    if (DiseaseList == null || DiseaseList.isEmpty()) {
        Log.d("PestCheck", "Pest list is empty or null.");
        return false;
    }
    else{
        Log.d("PestCheck", DiseaseList.size()+"");
    }

    // Iterate through the Pest list
    for (Disease disease : DiseaseList) {
        Log.d("Disease Check", "Checking Disease ID: " + disease.getDiseaseid());
        String Diseasename = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(disease.getDiseaseid()));
        if (Diseasename.equalsIgnoreCase("Ganoderma")) {
            Log.d("DiseasenameCheck", "Ganoderma (Diseasename == 349) found.");
            return true; // Return true if any Pest has pestId == 349
        }
    }

    // Log and return false if no Pest with pestId == 349 is found
    Log.d("Disease Check", "Ganoderma  not found in the list.");
    return false;
}


    public static boolean isDripACheck(Context context) {
        List<PlotIrrigationTypeXref> savedIrrigationList =
                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

        if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
            Log.d("DripCheck", "Irrigation list is empty or null.");
            return false;
        } else {
            Log.d("DripCheck", "Irrigation list size: " + savedIrrigationList.size());
        }

        for (PlotIrrigationTypeXref model : savedIrrigationList) {
            if (model != null &&
                    model.getRecmIrrgId() == 391 &&
                    "0".equals(model.getIsDripInstalled())) {

                Log.d("DripCheck", "Drip Irrigation found (ID: 391 and Installed).");
                return true;
            }
        }

        Log.d("DripCheck", "Drip not found in the list.");
        return false;
    }

    public static boolean ispestdata(Context context) {
        // Retrieve the list of Pest objects
        List<MainPestModel> pestList = (List<MainPestModel>) DataManager.getInstance().getDataFromManager(DataManager.MAIN_PEST_DETAIL);

        // Check if the list is null or empty
        if (pestList == null || pestList.isEmpty()) {
            Log.d("ispestdata", "Pest list is empty or null.");
            return false;
        } else {
            Log.d("ispestdata", "Pest list size: " + pestList.size());
            return true;
        }
    }

    public static boolean isDISEASEdata(Context context) {
        // Retrieve the list of Pest objects
        List<Disease> DiseaseList = (List<Disease>) DataManager.getInstance().getDataFromManager(DataManager.DISEASE_DETAILS);

        // Check if the list is null or empty
        if (DiseaseList == null || DiseaseList.isEmpty()) {
            Log.d("isDiseaseList", "DiseaseList list is empty or null.");
            return false;
        } else {
            Log.d("isDiseaseList", "DiseaseList list size: " + DiseaseList.size());
            return true;
        }
    }
    public static boolean isNDdata(Context context) {
        // Retrieve the list of Pest objects
        List<Nutrient> NutrientList = (List<Nutrient>) DataManager.getInstance().getDataFromManager(DataManager.NUTRIENT_DETAILS);

        // Check if the list is null or empty
        if (NutrientList == null || NutrientList.isEmpty()) {
            Log.d("isNutrientList", "NutrientList list is empty or null.");
            return false;
        } else {
            Log.d("isNutrientList", "NutrientList list size: " + NutrientList.size());
            return true;
        }
    }


//    public static boolean isGandermaDataEntered(Context context) {
//        // Retrieve the list of Pest objects
//        List<MainPestModel> pestList = (List<MainPestModel>) DataManager.getInstance()
//                .getDataFromManager(DataManager.MAIN_PEST_DETAIL);
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//
//        // Check if the list is null or empty
//        if (pestList == null || pestList.isEmpty()) {
//            Log.d("PestCheck", "Pest list is empty or null.");
//            return false;
//        }
//
//        // Iterate through the Pest list
//        for (MainPestModel pest : pestList) {
//            Log.d("PestCheck", "Checking Pest ID: " + pest.getPest().getPestid());
//            String pestName = dataAccessHandler.getOnlyOneValueFromDb(
//                    Queries.getInstance().getlookupdata(pest.getPest().getPestid()));
//
//            if ("Ganoderma".equalsIgnoreCase(pestName)) {
//                Log.d("PestCheck", "Ganoderma infestation detected.");
//                return true; // Return true if Ganoderma is found
//            }
//        }
//
//        // If no Ganoderma infestation is detected
//        Log.d("PestCheck", "Ganoderma infestation not found in the list.");
//        return false;
//    }

// Unit tests for isGandermaDataEntered()
//@Test
//public void testIsGandermaDataEntered_withNullList() {
//    DataManager.getInstance().setDataForManager(DataManager.PEST_DETAILS, null);
//    assertFalse(CommonUiUtils.isGandermaDataEntered());
//}
//
//@Test
//public void testIsGandermaDataEntered_withEmptyList() {
//    DataManager.getInstance().setDataForManager(DataManager.PEST_DETAILS, new ArrayList<>());
//    assertFalse(CommonUiUtils.isGandermaDataEntered());
//}
//
//@Test
//public void testIsGandermaDataEntered_withGanderma() {
//    List<Pest> pests = new ArrayList<>();
//    pests.add(new Pest(349, "Ganderma"));
//    DataManager.getInstance().setDataForManager(DataManager.PEST_DETAILS, pests);
//    assertFalse(CommonUiUtils.isGandermaDataEntered());
//}
//
//@Test
//public void testIsGandermaDataEntered_withoutGanderma() {
//    List<Pest> pests = new ArrayList<>();
//    pests.add(new Pest(348, "Other"));
//    DataManager.getInstance().setDataForManager(DataManager.PEST_DETAILS, pests);
//    assertTrue(CommonUiUtils.isGandermaDataEntered());
//}
//
//
//











    public static boolean isComplaintsDataEntered() {
        Complaints complaintsData = (Complaints) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_DETAILS);
        return complaintsData != null;
    }
/*
    public static String DripValidationError(Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String isDripStr;
        List<PlotIrrigationTypeXref> savedIrrigationList;
      //  CommonConstants.districtIdPlot = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());

        if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
            Log.e("districtIdPlot", CommonConstants.districtIdPlot);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        }
        else{
           String PlotdistrictId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());
            Log.e("districtIdPlot", PlotdistrictId);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(PlotdistrictId));
        }
//        List<PlotIrrigationTypeXref> savedIrrigationList;
//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());

        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            followUp = (FollowUp) dataAccessHandler.getFollowupData(
                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (followUp == null) return null;

        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;

        savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
        if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
            savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
        }
        boolean hasDripRecommended = false;
        boolean hasDripInstalled = false;

        if (savedIrrigationList != null) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if (model != null) {
                    if (model.getRecmIrrgId() == 391 || model.getIrrigationtypeid() == 391) {
                        hasDripRecommended = true;
                    }


                }
            }
        }

        // ✅ Scenario 1: Drip required, farmer ready, but drip not recommended
        if (isDrip && isFarmerReady && !hasDripRecommended) {
            return "Drip Irrigation is mandatory for the selected district. Please recommend Drip Irrigation in Irrigation Details.";
        }



        return null; // no error
    }
*/

    public static String DripValidationError(Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String isDripStr;

        Log.e("DripValidation", "Starting drip validation check...");

        // ✅ Step 1: Determine whether to use district from CommonConstants or DB
        if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
            Log.e("DripValidation", "New registration or new plot registration detected");
            Log.e("DripValidation", "districtIdPlot from CommonConstants: " + CommonConstants.districtIdPlot);

            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        } else {
            String plotDistrictId = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getPlotDistrictId());
            Log.e("DripValidation", "Existing plot detected");
            Log.e("DripValidation", "District ID from plot table: " + plotDistrictId);

            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(plotDistrictId));
        }

        Log.e("DripValidation", "isDripStr from DB: " + isDripStr);
        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());
        Log.e("DripValidation", "Is Drip required? " + isDrip);

        // ✅ Step 2: Get FollowUp info
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            Log.e("DripValidation", "FollowUp data not found in DataManager. Fetching from DB...");
            followUp = (FollowUp) dataAccessHandler.getFollowupData(
                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (followUp == null) {
            Log.e("DripValidation", "FollowUp data is still null. Exiting check.");
            return null;
        }

        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;
        Log.e("DripValidation", "Is farmer ready to convert? " + isFarmerReady);

        // ✅ Step 3: Get Irrigation Type details
        List<PlotIrrigationTypeXref> dataManagerList =
                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

        List<PlotIrrigationTypeXref> dbList =
                (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(
                        Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);

        List<PlotIrrigationTypeXref> combinedList = new ArrayList<>();
        if (dataManagerList != null) combinedList.addAll(dataManagerList);
        if (dbList != null) combinedList.addAll(dbList);

        Log.e("DripValidation", "Combined irrigation list count: " + combinedList.size());

        // ✅ Step 4: Check for Drip irrigation (ID = 391)
        boolean hasDripRecommended = false;
        for (PlotIrrigationTypeXref model : combinedList) {
            if (model != null) {
                Log.e("DripValidation", "Checking record → RecID: " + model.getRecmIrrgId()
                        + ", TypeID: " + model.getIrrigationtypeid());

                if (model.getRecmIrrgId() == 391 || model.getIrrigationtypeid() == 391) {
                    hasDripRecommended = true;
                    Log.e("DripValidation", "✅ Drip found (391) in combined list");
                    break;
                }
            }
        }

        Log.e("DripValidation", "Has Drip Recommended? " + hasDripRecommended);

        // ✅ Step 5: Final validation
        if (isDrip && isFarmerReady && !hasDripRecommended) {
            Log.e("DripValidation", "❌ Validation failed: Returning error message");
            return "Drip Irrigation is mandatory for the selected district. Please recommend Drip Irrigation in Irrigation Details.";
        }

        Log.e("DripValidation", "✅ Validation passed: Returning NULL");
        return null;
    }


/*
    public static String getDripValidationError(Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String isDripStr;
        List<PlotIrrigationTypeXref> savedIrrigationList;
        //  CommonConstants.districtIdPlot = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());

        if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
            Log.e("districtIdPlot", CommonConstants.districtIdPlot);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        }
        else{
            String PlotdistrictId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());
            Log.e("districtIdPlot", PlotdistrictId);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(PlotdistrictId));
        }
//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());

        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            followUp = (FollowUp) dataAccessHandler.getFollowupData(
                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (followUp == null) return null;

        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;

         savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
        if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
            savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
        }
        boolean hasDripRecommended = false;
        boolean hasDripInstalled = false;

        if (savedIrrigationList != null) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if ((model.getRecmIrrgId() != null && model.getRecmIrrgId() == 391) || (model.getIrrigationtypeid() == 391)) {
                    hasDripRecommended = true;
                }

                if (model.getIsDripInstalled() != null && model.getIsDripInstalled() == 1) {
                    hasDripInstalled = true;
                }
            }
        }


        // ✅ Scenario 1: Drip required, farmer ready, but drip not recommended
        if (isDrip && isFarmerReady && !hasDripRecommended) {
            return "Drip is recommended. Please recommend Drip irrigation in Irrigation Details.";
        }

        // ✅ New Condition: Drip recommended, but farmer not ready → skip error
        if (hasDripRecommended && !isFarmerReady) {
            return null;
        }

        // ✅ Scenario 2: Drip is recommended → Check if form is filled
        if (hasDripRecommended) {
            if (hasDripInstalled) {
                return null; // already installed, skip
            }

            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);

            boolean hasRequiredStatus = false;
            if (dripList != null) {
                for (DripIrrigationModel model : dripList) {
                    int statusTypeId = model.getStatusTypeId();

                    if (statusTypeId == 826 || statusTypeId == 848) {
                        hasRequiredStatus = true;
                        break;
                    }
                }
            }

            if (!hasRequiredStatus) {
                return "Please Enter Drip Irrigation Details.";
            }
        }


        return null; // no error
    }
*/

    public static String getDripValidationError(Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String isDripStr;
        List<PlotIrrigationTypeXref> savedIrrigationList;
        //  CommonConstants.districtIdPlot = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());

        if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
            Log.e("districtIdPlot", CommonConstants.districtIdPlot);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        }
        else{
            String PlotdistrictId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());
            Log.e("districtIdPlot", PlotdistrictId);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(PlotdistrictId));
        }
//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());

        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            followUp = (FollowUp) dataAccessHandler.getFollowupData(
                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (followUp == null) return null;

        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;

        savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
        if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
            savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
        }
        boolean hasDripRecommended = false;
        boolean hasDripInstalled = false;

        if (savedIrrigationList != null) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if ((model.getRecmIrrgId() != null && model.getRecmIrrgId() == 391) || (model.getIrrigationtypeid() == 391)) {
                    hasDripRecommended = true;
                }

                if (model.getIsDripInstalled() != null && model.getIsDripInstalled() == 1) {
                    hasDripInstalled = true;
                }
            }
        }


        // ✅ Scenario 1: Drip required, farmer ready, but drip not recommended
        if (isDrip && isFarmerReady && !hasDripRecommended) {
            return "Drip is recommended. Please recommend Drip irrigation in Irrigation Details.";
        }

        // ✅ New Condition: Drip recommended, but farmer not ready → skip error
        if (hasDripRecommended && !isFarmerReady) {
            return null;
        }

        // ✅ Scenario 2: Drip is recommended → Check if form is filled
        if (hasDripRecommended) {
            if (hasDripInstalled) {
                return null; // already installed, skip
            }
            String plotCode = CommonConstants.PLOT_CODE;
            Log.d("hasRequiredStatus", "hasRequiredStatus() - PlotCode: " + plotCode);

            String dripQuery = Queries.getInstance().getDripIrrigation(plotCode);
            Log.d("hasRequiredStatus", "hasRequiredStatus() - Drip query: " + dripQuery);

            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
            if (dripList == null || dripList.isEmpty()) {
                dripList = (ArrayList<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(dripQuery, 1);
            }
            boolean hasRequiredStatus = false;
            if (dripList != null) {
                for (DripIrrigationModel model : dripList) {
                    int statusTypeId = model.getStatusTypeId();

                    if (statusTypeId == 826 || statusTypeId == 848) {
                        hasRequiredStatus = true;
                        break;
                    }
                }
            }

            if (!hasRequiredStatus) {
                return "Please Enter Drip Irrigation Details.";
            }
        }


        return null; // no error
    }

    public static boolean checkppbimages(final Context context, Plot savedPlot) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        Object ppb1 = DataManager.getInstance().getDataFromManager(DataManager.PPB1);
        Object ppb2 = DataManager.getInstance().getDataFromManager(DataManager.PPB2);

        Log.d("PPBCheck", "Step 1: DataManager PPB1=" + (ppb1 == null ? "null" : "available")
                + ", PPB2=" + (ppb2 == null ? "null" : "available"));

        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (followUp == null) {
            followUp = (FollowUp) dataAccessHandler.getFollowupData(
                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (followUp == null) {
            Log.d("PPBCheck", "Step 2: FollowUp not found → return false");
            return false;
        }

        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;
        Log.d("PPBCheck", "Step 3: isFarmerReady=" + isFarmerReady);

        if (isFarmerReady) {
            // Case 1: Found in DataManager
            if (ppb1 != null && ppb2 != null) {
                Log.d("PPBCheck", "Step 4: PPB1 & PPB2 found in DataManager → return true");
                return true;
            }

            // Case 2: Check DB
            Log.d("PPBCheck", "Step 5: Missing in DataManager. Checking DB...");
            if (CommonConstants.PLOT_CODE != null) {
                List<FileRepository> savedPictureList = dataAccessHandler.getSelectedppbRepository(
                        Queries.getInstance().getSelectedppbRepositoryQuery(CommonConstants.PLOT_CODE, 844)
                );

                Log.d("PPBCheck", "Step 6: DB returned → " +
                        (savedPictureList == null ? "null" : "size=" + savedPictureList.size()));

                if (savedPictureList == null || savedPictureList.size() != 2) {
//                    UiUtils.showCustomToastMessage(
//                            "Grower is Ready To Convert, So Please Upload PPB Images in Field Details.",
//                            context,
//                            1
//                    );
                    return false; // ❌ Missing
                } else {
                    Log.d("PPBCheck", "Step 6b: Found 2 images in DB → return true");
                    return true;
                }
            } else {
                Log.d("PPBCheck", "Step 5b: Saved plot is null/no code → return false");
                UiUtils.showCustomToastMessage("Plot details not available", context, 1);
                return false;
            }
        }

        // Farmer not ready → skip check
        Log.d("PPBCheck", "Step 7: Farmer not ready → return true");
        return true;
    }
    public static String getDripStatus(Context context) {
        final String TAG = "DripStatusCheck";
        final int DRIP_TYPE_ID = 391;          // Drip type id in IrrigationType tables
        final int REQUIRED_STATUS_ID = 831;    // "Administration Sanctioned"

        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        try {
            Log.d(TAG, "===== getDripStatus() START =====");
            Log.d(TAG, "PlotCode: " + CommonConstants.PLOT_CODE);

            // 1) Determine which districtId to use and whether Drip is required in that district
            String districtIdToUse;
            if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
                districtIdToUse = CommonConstants.districtIdPlot;
                Log.d(TAG, "New registration path. districtIdPlot (from CC): " + districtIdToUse);
            } else {
                districtIdToUse = dataAccessHandler.getOnlyOneValueFromDb(
                        Queries.getInstance().getPlotDistrictId());
                Log.d(TAG, "Existing plot path. districtIdPlot (from DB): " + districtIdToUse);
            }

            String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(districtIdToUse));
            Log.d(TAG, "isDripStr from DB: " + isDripStr);

            boolean isDripRequired = false;
            if (isDripStr != null) {
                String s = isDripStr.trim();
                // Be flexible: DB may store boolean as "true/false" or "1/0"
                isDripRequired = "1".equals(s) || "true".equalsIgnoreCase(s)
                        || "y".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
            }
            Log.d(TAG, "isDripRequired (parsed): " + isDripRequired);

            // 2) Fetch PlotIrrigationTypeXref from DB for this plot
            String irrQuery = Queries.getInstance()
                    .getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE);
            Log.d(TAG, "IrrigationXref Query: " + irrQuery);

            @SuppressWarnings("unchecked")
            List<PlotIrrigationTypeXref> savedIrrigationList =
                    (List<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(irrQuery, 1);

            Log.d(TAG, "IrrigationXref rows: " + (savedIrrigationList == null ? 0 : savedIrrigationList.size()));

            boolean hasDripRecommended = false;
            boolean hasDripInstalled = false;

            if (savedIrrigationList != null) {
                for (int i = 0; i < savedIrrigationList.size(); i++) {
                    PlotIrrigationTypeXref m = savedIrrigationList.get(i);
                    Log.d(TAG, "Xref[" + i + "]: irrTypeId=" + m.getIrrigationtypeid()
                            + ", recmIrrId=" + m.getRecmIrrgId()
                            + ", isDripInstalled=" + m.getIsDripInstalled()
                            + ", name=" + m.getName());

                    if ((m.getRecmIrrgId() != null && m.getRecmIrrgId() == DRIP_TYPE_ID)
                            || m.getIrrigationtypeid() == DRIP_TYPE_ID) {
                        hasDripRecommended = true;
                    }

                    if (m.getIsDripInstalled() != null && m.getIsDripInstalled() == 1) {
                        hasDripInstalled = true;
                    }
                }
            }

            Log.d(TAG, "hasDripRecommended: " + hasDripRecommended);
            Log.d(TAG, "hasDripInstalled: " + hasDripInstalled);

            // 3) RULE A: Drip is required but not recommended anywhere -> prompt recommendation
            if (isDripRequired && !hasDripRecommended) {
                Log.d(TAG, "RETURN: Drip required but NOT recommended.");
                return "Drip is recommended. Please recommend Drip irrigation in Type of Irrigation.";
            }

            // 4) If Drip already installed -> all good
            if (hasDripInstalled) {
                Log.d(TAG, "RETURN: Drip already installed -> no error.");
                return null;
            }

            // 5) Fetch DripIrrigation progress rows from DB for this plot
            String dripQuery = Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE);
            Log.d(TAG, "DripIrrigation Query: " + dripQuery);

            @SuppressWarnings("unchecked")
            List<DripIrrigationModel> dripList =
                    (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(dripQuery, 1);

            int dripCount = (dripList == null ? 0 : dripList.size());
            boolean dripStarted = dripCount > 0;

            Log.d(TAG, "DripIrrigation rows: " + dripCount);
            Log.d(TAG, "dripStarted: " + dripStarted);

            boolean hasRequiredStatus = false;
            if (dripList != null) {
                for (int i = 0; i < dripList.size(); i++) {
                    DripIrrigationModel di = dripList.get(i);
                    Log.d(TAG, "Drip[" + i + "]: statusTypeId=" + di.getStatusTypeId()

                          );
                    if (di.getStatusTypeId() == REQUIRED_STATUS_ID) {
                        hasRequiredStatus = true;
                        Log.d(TAG, "Drip row reached REQUIRED_STATUS_ID=" + REQUIRED_STATUS_ID);
                        break;
                    }
                }
            }
            Log.d(TAG, "hasRequiredStatus(" + REQUIRED_STATUS_ID + "): " + hasRequiredStatus);

            // 6) RULE B (your new rule): If Drip is required AND farmer has started Drip,
            //    then status must have reached REQUIRED_STATUS_ID -> else show error.
            if (isDripRequired && dripStarted && !hasRequiredStatus) {
                Log.d(TAG, "RETURN: Drip required + started, but NOT yet at required status.");
                return "Please Complete Drip Irrigation Flow Up to Administration Sanctioned.";
            }

            // 7) Otherwise, no error
            Log.d(TAG, "RETURN: No error.");
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Exception in getDripStatus: " + e.getMessage(), e);
            // Optionally return a safe message or null
            return "Unable to validate Drip status at the moment. Please try again.";
        } finally {
            Log.d(TAG, "===== getDripStatus() END =====");
        }
    }

    public static boolean hasRequiredStatus(Context context) {
        final String TAG = "CommonUiUtils";
        final int REQUIRED_STATUS_ID = 831; // Administration Sanctioned
        try {
            DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            String plotCode = CommonConstants.PLOT_CODE;
            Log.d(TAG, "hasRequiredStatus() - PlotCode: " + plotCode);

            String dripQuery = Queries.getInstance().getDripIrrigation(plotCode);
            Log.d(TAG, "hasRequiredStatus() - Drip query: " + dripQuery);

            @SuppressWarnings("unchecked")
            List<DripIrrigationModel> dripList =
                    (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(dripQuery, 1);

            int count = dripList == null ? 0 : dripList.size();
            Log.d(TAG, "hasRequiredStatus() - drip rows: " + count);

            if (dripList != null) {
                for (int i = 0; i < dripList.size(); i++) {
                    DripIrrigationModel di = dripList.get(i);
                    int status = di.getStatusTypeId();
                    Log.d(TAG, "hasRequiredStatus() - row[" + i + "] statusTypeId=" + status
                        );
                    if (status == REQUIRED_STATUS_ID) {
                        Log.d(TAG, "hasRequiredStatus() - required status found (row " + i + ")");
                        return true;
                    }
                }
            }

            Log.d(TAG, "hasRequiredStatus() - required status NOT found");
            return false;

        } catch (Exception e) {
            Log.e("CommonUiUtils", "hasRequiredStatus() exception: " + e.getMessage(), e);
            return false; // fail-safe: treat as not completed
        }
    }

/*
    public static String getDripStatus(Context context) {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String isDripStr;
        List<PlotIrrigationTypeXref> savedIrrigationList;
        //  CommonConstants.districtIdPlot = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());

            String PlotdistrictId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotDistrictId());
            Log.e("districtIdPlot", PlotdistrictId);
            isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getisdriprequiredQuery(PlotdistrictId));

//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());

      //  savedIrrigationList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

        savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
        boolean hasDripRecommended = false;
        boolean hasDripInstalled = false;

        if (savedIrrigationList != null) {
            for (PlotIrrigationTypeXref model : savedIrrigationList) {
                if ((model.getRecmIrrgId() != null && model.getRecmIrrgId() == 391) || (model.getIrrigationtypeid() == 391)) {
                    hasDripRecommended = true;
                }

                if (model.getIsDripInstalled() != null && model.getIsDripInstalled() == 1) {
                    hasDripInstalled = true;
                }
            }
        }


        // ✅ Scenario 1: Drip required, farmer ready, but drip not recommended
        if (isDrip  && !hasDripRecommended) {
            return "Drip is recommended. Please recommend Drip irrigation in Type of Irrigation.";
        }

        // ✅ New Condition: Drip recommended, but farmer not ready → skip error
        if (hasDripRecommended) {
            return null;
        }

        // ✅ Scenario 2: Drip is recommended → Check if form is filled
        if (hasDripRecommended) {
            if (hasDripInstalled) {
                return null; // already installed, skip
            }

//            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
//                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE), 1);

            boolean hasRequiredStatus = false;
            if (dripList != null) {
                for (DripIrrigationModel model : dripList) {
                    int statusTypeId = model.getStatusTypeId();

                    if (statusTypeId == 831) {
                        hasRequiredStatus = true;
                        break;
                    }
                }
            }

            if (!hasRequiredStatus) {
                return "Please Complete Drip Irrigation Flow Up to Administration Sanctioned.";
            }
        }


        return null; // no error
    }
*/

//    public static String getDripValidationError(Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//
//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
//        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());
//
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null) {
//            followUp = (FollowUp) dataAccessHandler.getFollowupData(
//                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
//        }
//
//        if (followUp == null) return null;
//
//        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;

//        List<PlotIrrigationTypeXref> savedIrrigationList =
//                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
//
//        boolean hasDripRecommended = false;
//        boolean hasDripInstalled = false;
//
//        if (savedIrrigationList != null) {
//            for (PlotIrrigationTypeXref model : savedIrrigationList) {
//                if (model != null) {
//                    if (model.getRecmIrrgId() == 391) {
//                        hasDripRecommended = true;
//                    }
//                    if (model.getIsDripInstalled() == 1) {
//                        hasDripInstalled = true;
//                    }
//                }
//            }
//        }
//
//        // ✅ Scenario 1: Drip required, farmer ready, but drip not recommended
//        if (isDrip && isFarmerReady && !hasDripRecommended) {
//            return "Drip is recommended. Please recommend Drip irrigation in Type of Irrigation.";
//        }
//
//        // ✅ Scenario 2: Drip is recommended → Check if form is filled
//        if (hasDripRecommended) {
//            // ✅ If Drip is also installed → skip validation
//            if (hasDripInstalled) {
//                return null;
//            }
//
//            // ❌ Not installed → Check if form is filled
//            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
//                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//
//            boolean dripFormFilled = false;
//            if (dripList != null) {
//                for (DripIrrigationModel model : dripList) {
//                    if (model.getStatusTypeId() == 826) {
//                        dripFormFilled = true;
//                        break;
//                    }
//                }
//            }
//
//            if (!dripFormFilled) {
//               // return "TRIGGER_INSERT";
//                return "Please fill Drip Irrigation form as it is already recommended.";
//            }
//        }
//
////        // ✅ Scenario 3: Drip not recommended and not installed → trigger insert
////        if (!hasDripRecommended && !hasDripInstalled) {
////            return "TRIGGER_INSERT";
////        }
//
//        return null; // no error
//    }

//    public static String getDripValidationError(Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//
//        String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(
//                Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
//        boolean isDrip = isDripStr != null && Boolean.parseBoolean(isDripStr.trim());
//
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null) {
//            followUp = (FollowUp) dataAccessHandler.getFollowupData(
//                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
//        }
//
//        if (followUp == null) return null;
//
//        boolean isFarmerReady = followUp.getIsfarmerreadytoconvert() == 1;
//
//        List<PlotIrrigationTypeXref> savedIrrigationList =
//                (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
//
//        boolean hasDripRecommended = false;
//        boolean hasDripinstalled = false;
//        if (savedIrrigationList != null) {
//            for (PlotIrrigationTypeXref model : savedIrrigationList) {
//                if (model != null && model.getRecmIrrgId() == 391) {
//                    hasDripRecommended = true;
//
//                    break;
//                }
//                if (model != null && model.getIsDripInstalled() == 1) {
//                    hasDripinstalled = true;
//                }
//            }
//        }
//
//        // Scenario 1: Drip required and farmer ready, but drip not recommended
//        if (isDrip && isFarmerReady && !hasDripRecommended) {
//            return "Drip is recommended. Please recommend Drip irrigation in Type of Irrigation.";
//        }
//
//        // Scenario 2: Drip recommended but form not filled
//        if (hasDripRecommended ) {
//            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
//                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//
//            boolean dripFormFilled = false;
//            if (dripList != null) {
//                for (DripIrrigationModel model : dripList) {
//                    if (model.getStatusTypeId() == 826 && model.getDripStatusDone() == 1) {
//                        dripFormFilled = true;
//                        break;
//                    }
//                }
//            }
//
//            if (!dripFormFilled) {
//                return "Please fill Drip Irrigation form as it is already recommended.";
//            }
//        }
//
//        if (!hasDripRecommended && !hasDripinstalled) {
//            return "TRIGGER_INSERT"; // Special signal
//        }
//        return null;
//    }

public static InputFilter getDecimalDigitsInputFilter(final int maxTotalDigits,
                                                      final int maxDigitsAfterDecimal,
                                                      final Context context) {
    return new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            // Construct the new value if input is applied
            String newValue = dest.toString().substring(0, dstart)
                    + source.toString().substring(start, end)
                    + dest.toString().substring(dend);

            // Allow deleting/backspace
            if (source.length() == 0) return null;

            // Only digits and dot
            if (!newValue.matches("[0-9.]*")) return "";

            // Prevent multiple dots
            int firstDot = newValue.indexOf('.');
            if (firstDot != -1 && firstDot != newValue.lastIndexOf('.')) return "";

            // If only a dot, show validation
            if (newValue.equals(".")) {
                UiUtils.showCustomToastMessage("Please enter a valid number", context , 1);
                return ""; // reject input
            }

            String[] parts = newValue.split("\\.", -1); // include empty trailing parts
            String beforeDecimal = parts.length > 0 ? parts[0] : "";
            String afterDecimal = parts.length > 1 ? parts[1] : "";

            // Limit digits after decimal
            if (afterDecimal.length() > maxDigitsAfterDecimal) return "";

            // Limit total digits (before + after decimal)
            int totalDigits = beforeDecimal.length() + afterDecimal.length();
            if (totalDigits > maxTotalDigits) return "";

            return null; // Accept input
        }
    };
}



}

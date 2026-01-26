package com.cis.palm360.palmgrow.SuvenAgro.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.cis.palm360.palmgrow.SuvenAgro.alerts.AlertType;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigationModel;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.PlotDetailsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ActivityLog;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Address;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Alerts;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintStatusHistory;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Complaints;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CookingOil;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropMaintenanceHistory;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Disease;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmerBank;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmerHistory;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Fertilizer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FollowUp;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.GeoBoundaries;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Harvest;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Healthplantation;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.IdentityProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.InterCropPlantationXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.LandlordBank;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.LandlordIdProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.MarketSurvey;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.NeighbourPlot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Nutrient;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Pest;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PestChemicalXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Plantation;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Plot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotCurrentCrop;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotGapFillingDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotLandlord;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.RecommndFertilizer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Referrals;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.SoilResource;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Uprootment;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.WaterResource;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Weed;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.WhiteFlyAssessment;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.YieldAssessment;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants.Crop_Maintenance;
import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants.isGeoTagTaken;
import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.isFromCropMaintenance;
import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.isNewPlotRegistration;

/**
 * Created by siva on 17/05/17.
 */


//This class is used for saving data into the db
@SuppressWarnings("unchecked")
public class DataSavingHelper {

    private static final String LOG_TAG = DataSavingHelper.class.getName();
    private static String initialPestCode;
    private static int saveCropCount;

    //to save farmer address data
    public static void   saveFarmerAddressData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Address farmerAddress = (Address) DataManager.getInstance().getDataFromManager(DataManager.FARMER_ADDRESS_DETAILS);
        if (null != farmerAddress) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_ADDRESS, "Code", farmerAddress.getCode()));

            if (recordExisted && farmerAddress.getCreateddate() != null) {
                farmerAddress.setCreatedbyuserid(farmerAddress.getCreatedbyuserid());
                farmerAddress.setCreateddate(farmerAddress.getCreateddate());
            } else {
                farmerAddress.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                farmerAddress.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            farmerAddress.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerAddress.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            farmerAddress.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//            farmerAddress.setCode(CommonConstants.ADDRESS_CODE_PREFIX + CommonConstants.FARMER_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(farmerAddress));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting address data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            if (CommonUtils.isNewRegistration() || !recordExisted) {
                saveRecordIntoActivityLog(context, CommonConstants.Farmer_Personal_Details);
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_ADDRESS, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ address data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.FARMER_ADDRESS_DETAILS);
                            saveFarmerPersonalData(context, oncomplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ address data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                Boolean dataUpdated = (Boolean) DataManager.getInstance().getDataFromManager(DataManager.IS_FARMER_DATA_UPDATED);
                if (dataUpdated == null || dataUpdated) {
                    saveRecordIntoActivityLog(context, CommonConstants.Farmer_Personal_Details);
                    String whereCondition = " where  Code='" + farmerAddress.getCode() + "'";
                    dataAccessHandler.updateData(DatabaseKeys.TABLE_ADDRESS, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                Log.v(LOG_TAG, "@@@ address data updated successfully");
                                DataManager.getInstance().deleteData(DataManager.FARMER_ADDRESS_DETAILS);
                                DataManager.getInstance().addData(DataManager.IS_FARMER_DATA_UPDATED, false);
                                saveFarmerPersonalData(context, oncomplete);
                            } else {
                                Log.e(LOG_TAG, "@@@ address data update failed due to " + msg);
                            }
                        }
                    });
                } else {
                    savePlotAddress(context, oncomplete);
                }
            }

        }
    }

    //to save farmers personal information
    public static void saveFarmerPersonalData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Farmer farmerPersonalDetails = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        if (null == farmerPersonalDetails) {
            oncomplete.execute(false, "data saving failed for farmerPersonalDetails", "");
            return;
        }

        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_FARMER, "Code", farmerPersonalDetails.getCode()));

        if (recordExisted &&  farmerPersonalDetails.getCreateddate() != null) {
            farmerPersonalDetails.setCreatedbyuserid(farmerPersonalDetails.getCreatedbyuserid());
            farmerPersonalDetails.setCreateddate(farmerPersonalDetails.getCreateddate());
        } else {
            farmerPersonalDetails.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerPersonalDetails.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }
        if (CommonUtils.isNewRegistration() || isNewPlotRegistration()){
            CommonConstants.FARMER_CODE = farmerPersonalDetails.getCode();
        }
        farmerPersonalDetails.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        farmerPersonalDetails.setServerupdatedstatus(0);
        farmerPersonalDetails.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        if (null != farmerPersonalDetails) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(farmerPersonalDetails));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());


            if (CommonUtils.isNewRegistration() || !recordExisted) {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FARMER, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ farmerPersonalDetails data saved successfully");
                            saveRecordIntoActivityLog(context, CommonConstants.Farmer_Personal_Details);
                            DataManager.getInstance().deleteData(DataManager.FARMER_PERSONAL_DETAILS);
                            if (CommonUtils.isNewRegistration() || CommonUtils.isFromConversion()) {
                                saveFarmerPictureData(context, oncomplete);
                            } else {
                                savePlotAddress(context, oncomplete);
                            }
                        } else {
                            oncomplete.execute(false, "data saving failed for farmerPersonalDetails", "");
                            Log.e(LOG_TAG, "@@@ farmerPersonalDetails data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                String whereCondition = " where  Code='" + farmerPersonalDetails.getCode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_FARMER, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ farmer data updated successfully");
                            DataManager.getInstance().deleteData(DataManager.FARMER_PERSONAL_DETAILS);
                            if (CommonUtils.isNewRegistration() || CommonUtils.isFromConversion()||CommonUtils.isFromFollowUp()) {
                                saveFarmerPictureData(context, oncomplete);
                            } else {
                                savePlotAddress(context, oncomplete);
                            }
                        } else {
                            Log.e(LOG_TAG, "@@@ farmer data update failed due to " + msg);
                        }
                    }
                });
            }
        }
    }

    //to save healthofplantation image data
    public static void saveHOPPictureData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.HOP_FILE_REPOSITORY_PLANTATION);
        if (null != fileRepo) {
            fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(fileRepo));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPicture data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            //  fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ farmerPictureDetails data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.HOP_FILE_REPOSITORY_PLANTATION);
                        saveHarvestData(context, oncomplete);
//                        saveGeoTagData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ farmerPictureDetails data saving failed due to " + msg);
                    }
                    saveHarvestData(context, oncomplete);
                }
            });
        } else {
            saveHarvestData(context, oncomplete);
        }
    }
/*

    public static void saveMissingTreeImages(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List<FileRepository> fileRepo = (List<FileRepository>) DataManager.getInstance().getDataFromManager(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);
        if (null != fileRepo) {
           fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(fileRepo));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting Missing tree images data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "Missing tree images saved successfully");
                        DataManager.getInstance().deleteData(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);
                        saveGeoTagData(context, oncomplete);
                        saveWaterResourceData(context, oncomplete);
//                        saveGeoTagData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ Missing tree images data saving failed due to " + msg);
                    }
                    saveGeoTagData(context, oncomplete);
                    saveWaterResourceData(context, oncomplete);
                }
            });
        } else {
            saveGeoTagData(context, oncomplete);
            saveWaterResourceData(context, oncomplete);
        }
    }

*/

    public static void saveMissingTreeImages(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List<FileRepository> fileRepoList = (List<FileRepository>) DataManager.getInstance()
                .getDataFromManager(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);

        Log.e(LOG_TAG, "MISSING_PLANTATION_IMAGES_BY_FINISH: "+(fileRepoList != null && !fileRepoList.isEmpty()));
        if (fileRepoList != null && !fileRepoList.isEmpty()) {
            final List dataToInsert = new ArrayList();
            Gson gson = new GsonBuilder().serializeNulls().create();

            for (FileRepository fileRepo : fileRepoList) {
                fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                try {
                    JSONObject ccData = new JSONObject(gson.toJson(fileRepo));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "MISSING_PLANTATION_IMAGES_BY_FINISH: error while converting fileRepo data");
                }
            }

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "MISSING_PLANTATION_IMAGES_BY_FINISH: MISSING_TREE_IMAGES data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);
                        saveRaithuDiary(context, oncomplete);

                    } else {
                        Log.e(LOG_TAG, "MISSING_PLANTATION_IMAGES_BY_FINISH: MISSING_TREE_IMAGES data saving failed due to " + msg);
                        saveRaithuDiary(context, oncomplete);
                    }
                }
            });
        }



        else {
            // If no data found, just proceed
            //
            saveRaithuDiary(context, oncomplete);

        }
    }

    public static void saveRaithuDiary(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.RYTHU_DIARY);

        Log.d("saveCheck", "repo from DataManager = " + (fileRepo == null ? "NULL" : fileRepo.getPicturelocation()));

        if (fileRepo != null) {
            fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData;
            List dataToInsert;

            try {
                ccData = new JSONObject(gson.toJson(fileRepo));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
                Log.v("saveRaithuDiary", "@@ entered data " + ccData.toString());
            } catch (JSONException e) {
                Log.e("saveRaithuDiary", "@@@ error while converting RaithuDiary data", e);
                oncomplete.execute(false, null, "Error parsing data");
                return;
            }

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v("saveRaithuDiary", "@@@ RaithuDiary image saved successfully");
                        DataManager.getInstance().deleteData(DataManager.RYTHU_DIARY);
                        saveHarvestData(context, oncomplete);
                    } else {
                        Log.e("saveRaithuDiary", "@@@ RaithuDiary image saving failed due to " + msg);
                        oncomplete.execute(false, result, msg);
                    }
                }
            });

        } else {
            Log.e("saveRaithuDiary", "@@@ No diary image" );
            //   oncomplete.execute(false, result, oncomplete);
            // No diary image → continue other saves
            //   saveHarvestData(context, oncomplete);
//            saveGeoTagData(context, oncomplete);
//            saveWaterResourceData(context, oncomplete);
        }
    }

    /*

        public static void savePPBImages(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
            List<FileRepository> fileRepoList = (List<FileRepository>) DataManager.getInstance()
                    .getDataFromManager(DataManager.PLOT_DETAILS_IMAGES);

            Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: "+(fileRepoList != null && !fileRepoList.isEmpty()));
            if (fileRepoList != null && !fileRepoList.isEmpty()) {
                final List dataToInsert = new ArrayList();
                Gson gson = new GsonBuilder().serializeNulls().create();

                for (FileRepository fileRepo : fileRepoList) {
                    fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                    try {
                        JSONObject ccData = new JSONObject(gson.toJson(fileRepo));
                        dataToInsert.add(CommonUtils.toMap(ccData));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: error while converting fileRepo data");
                    }
                }

                final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "PLOT_DETAILS_IMAGES: PLOT_DETAILS_IMAGES data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS_IMAGES);
    //                        saveHarvestData(context, oncomplete);
                        } else {
                            Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: PLOT_DETAILS_IMAGES data saving failed due to " + msg);
    //                        saveHarvestData(context, oncomplete);
                        }
                    }
                });
            } else {
                // If no data found, just proceed
    //            saveGeoTagData(context, oncomplete);
    //            saveWaterResourceData(context, oncomplete);
            }
        }
    */
    public static void savePPBImages(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List<FileRepository> fileRepoList = (List<FileRepository>) DataManager.getInstance()
                .getDataFromManager(DataManager.PLOT_DETAILS_IMAGES);

        Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: "+(fileRepoList != null && !fileRepoList.isEmpty()));
        if (fileRepoList != null && !fileRepoList.isEmpty()) {
            final List dataToInsert = new ArrayList();
            Gson gson = new GsonBuilder().serializeNulls().create();

            for (FileRepository fileRepo : fileRepoList) {
                fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                try {
                    JSONObject ccData = new JSONObject(gson.toJson(fileRepo));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: error while converting fileRepo data");
                }
            }

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {

                        Log.v(LOG_TAG, "PLOT_DETAILS_IMAGES: PLOT_DETAILS_IMAGES data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS_IMAGES);
                        saveCurrentCropsData(context, oncomplete);

//                        saveHarvestData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "PLOT_DETAILS_IMAGES: PLOT_DETAILS_IMAGES data saving failed due to " + msg);
//                        saveHarvestData(context, oncomplete);
                        saveCurrentCropsData(context, oncomplete);

                    }
                }
            });
        } else {
            if (CommonUtils.isNewRegistration() || CommonUtils.isFromFollowUp() || CommonUtils.isFromConversion() || CommonUtils.isNewPlotRegistration()) {
                saveCurrentCropsData(context, oncomplete);
                //    saveRecordIntoFarmerHistory(context, oncomplete);
                //  saveWaterResourceData(context, oncomplete);
            }
            // If no data found, just proceed
//            saveGeoTagData(context, oncomplete);
//            saveWaterResourceData(context, oncomplete);
        }
    }



    //to save plantation image data from conversion
    public static void save_Con_Plant_PictureData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.CP_FILE_REPOSITORY_PLANTATION);
        if (null != fileRepo) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(fileRepo));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPicture data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
//            fileRepo.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ ConversionPlantationPictureDetails data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.CP_FILE_REPOSITORY_PLANTATION);
//
                        if (CommonUtils.isFromCropMaintenance()  || CommonUtils.isVisitRequests()) {
                            saveCropMaintenanceHistoryData(context, oncomplete);
                        } else {
                            saveInterCropsData(context, oncomplete);
                        }
                    } else {
                        Log.e(LOG_TAG, "@@@ ConversionPlantationPictureDetails data saving failed due to " + msg);
                    }
//                    saveHarvestData(context, oncomplete);

                }
            });
        } else {
//            saveHarvestData(context, oncomplete);
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveCropMaintenanceHistoryData(context, oncomplete);
            } else {
                saveInterCropsData(context, oncomplete);
            }
        }
    }

    //to save farmer image
    public static void saveFarmerPictureData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.FILE_REPOSITORY);
        if (null != fileRepo) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(fileRepo));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPicture data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FILEREPOSITORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ farmerPictureDetails data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.FILE_REPOSITORY);
                        savePlotAddress(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ farmerPictureDetails data saving failed due to " + msg);
                    }
                }
            });

        } else {
            savePlotAddress(context, oncomplete);
        }
    }

    //to save plot address data
    public static void savePlotAddress(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Address farmerPlotAddress = (Address) DataManager.getInstance().getDataFromManager(DataManager.PLOT_ADDRESS_DETAILS);
        if (null != farmerPlotAddress) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_ADDRESS, "Code", farmerPlotAddress.getCode()));

            if (recordExisted && farmerPlotAddress.getCreateddate() != null) {
                farmerPlotAddress.setCreatedbyuserid(farmerPlotAddress.getCreatedbyuserid());
                farmerPlotAddress.setCreateddate(farmerPlotAddress.getCreateddate());
            } else {
                farmerPlotAddress.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                farmerPlotAddress.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            farmerPlotAddress.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerPlotAddress.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            farmerPlotAddress.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            farmerPlotAddress.setCode(CommonConstants.ADDRESS_CODE_PREFIX + CommonConstants.PLOT_CODE);
            farmerPlotAddress.setIsactive(1);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(farmerPlotAddress));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting address data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            if (CommonUtils.isNewRegistration()) {
                saveRecordIntoActivityLog(context, CommonConstants.New_plot_registration_new_farmer);
            } else if (CommonUtils.isNewPlotRegistration()) {
                saveRecordIntoActivityLog(context, CommonConstants.New_plot_registration_existing_farmer);
            }
            if (CommonUtils.isNewRegistration() || isNewPlotRegistration() || !recordExisted) {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_ADDRESS, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ address data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_ADDRESS_DETAILS);
                            savePlotData(context, oncomplete);
                        } else {
                            oncomplete.execute(false, "data saving failed for farmerPersonalDetails", "");
                            Log.e(LOG_TAG, "@@@ address data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                Boolean isDataUpdated = (Boolean) DataManager.getInstance().getDataFromManager(DataManager.IS_PLOTS_DATA_UPDATED);
                if (null == isDataUpdated || isDataUpdated) {
                    String whereCondition = " where  Code = '" + farmerPlotAddress.getCode() + "'";
                    dataAccessHandler.updateData(DatabaseKeys.TABLE_ADDRESS, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                Log.v(LOG_TAG, "@@@ address data updated successfully");
                                DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, false);
                                DataManager.getInstance().deleteData(DataManager.PLOT_ADDRESS_DETAILS);
                                savePlotData(context, oncomplete);
                            } else {
                                Log.e(LOG_TAG, "@@@ address data update failed due to " + msg);
                            }
                        }
                    });
                } else {
                    if (CommonUtils.isFromFollowUp()) {
                        saveRecordIntoFarmerHistory(context, oncomplete);
                    }
                    else  if (CommonUtils.isVisitRequests()) {
                        savePlantation(context, oncomplete);
                    }
                    else {
                        saveGeoTagData(context, oncomplete);
                    }

                }
            }
        } else {
            if (CommonUtils.isFromFollowUp() || CommonUtils.isFromConversion() || CommonUtils.isVisitRequests()) {
                savePlotData(context, oncomplete);
            }
            else if( CommonUtils.isFromCropMaintenance() && CommonConstants.isplotupdated == true) {
                savePlotData(context, oncomplete);
            }else if (CommonUtils.isFromCropMaintenance() && CommonConstants.isplotupdated == false) {
                saveWaterResourceData(context, oncomplete);
                saveGeoTagData(context, oncomplete);
            }  else
            {
                oncomplete.execute(false, "data saving failed for farmerPlotAddress", "");
            }
        }
    }

    //to save plot data
    /*
    private static void savePlotData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Plot enteredPlot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        if (null != enteredPlot) {

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_PLOT, "Code", enteredPlot.getCode()));

            if (recordExisted && enteredPlot.getCreateddate() != null) {
                enteredPlot.setCreatedbyuserid(enteredPlot.getCreatedbyuserid());
                enteredPlot.setCreateddate(enteredPlot.getCreateddate());
            } else {
                enteredPlot.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                enteredPlot.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }


            enteredPlot.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            enteredPlot.setServerupdatedstatus(0);
            enteredPlot.setIsActive(1);
            enteredPlot.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            if (CommonUtils.isNewRegistration() || isNewPlotRegistration()){
                if(TextUtils.isEmpty(CommonConstants.PLOT_CODE))
                {
                    CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(PlotDetailsFragment.financalYrDays), PlotDetailsFragment.financalYrDays);
                }
            }else{
                CommonConstants.FARMER_CODE = enteredPlot.getFarmercode();
                CommonConstants.PLOT_CODE = enteredPlot.getCode();
            }

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(enteredPlot));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            if (CommonUtils.isNewRegistration() || isNewPlotRegistration() || !recordExisted) {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOT, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ savePlotData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS);
                            if (CommonUtils.isFromConversion()){

                                saveOilPalmCropData(context,oncomplete);

                            } else {

                                saveCurrentCropsData(context, oncomplete);
                            }
                        } else {
                            oncomplete.execute(false, "data saving failed for savePlotData", "");
                            Log.e(LOG_TAG, "@@@ savePlotData data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                String whereCondition = " where  Code='" + enteredPlot.getCode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_PLOT, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ savePlotData data updated successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS);
                            if(CommonUtils.isPlotSplitFarmerPlots()){
                                Toast.makeText(context, "updated", Toast.LENGTH_SHORT).show();
                            }else {
                                if (CommonUtils.isFromConversion()){

                                    saveOilPalmCropData(context,oncomplete);

                                }else {
                                    saveCurrentCropsData(context, oncomplete);
                                }

                                if (CommonUtils.isFromCropMaintenance()) {
                                    if (CommonConstants.leased) {
                                        saveLandLordDetails(context, oncomplete);
                                    }
                                }
                            }

                        } else {
                            oncomplete.execute(false, "savePlotData data update failed due to", "");
                            Log.e(LOG_TAG, "@@@ savePlotData data update failed due to " + msg);
                        }
                    }
                });

            }
        } else {
            if (CommonUtils.isFromFollowUp() || CommonUtils.isFromConversion()) {
                saveRecordIntoFarmerHistory(context, oncomplete);
            }
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveWaterResourceData(context, oncomplete);
            } else {
                Log.v(LOG_TAG, "@@@@ problem while saving data");
            }
        }
    }
*/

    private static void savePlotData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Plot enteredPlot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);
        if (null != enteredPlot) {

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_PLOT, "Code", enteredPlot.getCode()));

            if (recordExisted && enteredPlot.getCreateddate() != null) {
                enteredPlot.setCreatedbyuserid(enteredPlot.getCreatedbyuserid());
                enteredPlot.setCreateddate(enteredPlot.getCreateddate());
            } else {
                enteredPlot.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                enteredPlot.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }


            enteredPlot.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            enteredPlot.setServerupdatedstatus(0);
            enteredPlot.setIsActive(1);
            enteredPlot.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            if (CommonUtils.isNewRegistration() || isNewPlotRegistration()){
                if(TextUtils.isEmpty(CommonConstants.PLOT_CODE))
                {
                    CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(PlotDetailsFragment.financalYrDays), PlotDetailsFragment.financalYrDays);
                }
            }else{
                CommonConstants.FARMER_CODE = enteredPlot.getFarmercode();
                CommonConstants.PLOT_CODE = enteredPlot.getCode();
            }

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(enteredPlot));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            if (CommonUtils.isNewRegistration() || isNewPlotRegistration() || !recordExisted) {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOT, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ savePlotData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS);
                            if (CommonUtils.isFromConversion()){

                                saveOilPalmCropData(context,oncomplete);

                            } else {

                                savePPBImages(context, oncomplete);
                            }
                        } else {
                            oncomplete.execute(false, "data saving failed for savePlotData", "");
                            Log.e(LOG_TAG, "@@@ savePlotData data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                String whereCondition = " where  Code='" + enteredPlot.getCode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_PLOT, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ savePlotData data updated successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_DETAILS);
                            if(CommonUtils.isPlotSplitFarmerPlots()){
                                UiUtils.showCustomToastMessage("updated", context, 0);
                            }else {
                                if (CommonUtils.isFromConversion()){

                                    saveOilPalmCropData(context,oncomplete);

                                }

                                else  if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                                    saveWaterResourceData(context, oncomplete);
                                    if (CommonConstants.leased) {
                                        saveLandLordDetails(context, oncomplete);
                                    }
                                }
                                else {
                                    savePPBImages(context, oncomplete);
                                    Log.v(LOG_TAG, "@@@@ problem while saving data");
                                }
                                //TODO


//                              if (CommonUtils.isFromCropMaintenance()) {
//                                    if (CommonConstants.leased) {
//                                        saveLandLordDetails(context, oncomplete);
//                                    }
//                               }
                            }

                        } else {
                            oncomplete.execute(false, "savePlotData data update failed due to", "");
                            Log.e(LOG_TAG, "@@@ savePlotData data update failed due to " + msg);
                        }
                    }
                });

            }
        } else {
            if (CommonUtils.isFromFollowUp()) {
                savePPBImages(context, oncomplete);

            }
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveWaterResourceData(context, oncomplete);
            } else {
                Log.v(LOG_TAG, "@@@@ problem while saving data");
            }
        }
    }



    //to save crop data
    private static void saveOilPalmCropData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        PlotCurrentCrop plotCurrentCrop = (PlotCurrentCrop) DataManager.getInstance().getDataFromManager(DataManager.PlotCurrent_Crop_Conversion);
        if (null != plotCurrentCrop) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(plotCurrentCrop));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting PlotCurretnCrop data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            // Update the Plot Cureent crop Details at the time of conversion.

            dataAccessHandler.upDataPlotCureentCropStatus(CommonConstants.PLOT_CODE);

            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOTCURRENTCROP, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ PlotCurretnCrop data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.PlotCurrent_Crop_Conversion);
                        saveNeighBourCropsData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ PlotCurretnCrop data saving failed due to " + msg);
                    }
                }
            });


        }else {
            saveNeighBourCropsData(context, oncomplete);
        }
    }

    //to save current crop data
    public static void saveCurrentCropsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        saveCurrentCropsForPlotData(context, 0, oncomplete);

    }

    //to save neighbours crops data
    public static void saveNeighBourCropsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        saveNeighbourPlotsData(context, oncomplete);

    }

    //to save water resource data
    public static void saveWaterResourceData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getWaterResourceData();
        Log.v(LOG_TAG+"saveWaterResourceData", dataToSave.size()+"");
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Water_Soil_Power_details_update);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean checkRecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().queryWaterResourceCheck(CommonConstants.PLOT_CODE));
            // dataAccessHandler.deleteRow(DatabaseKeys.TABLE_WATERESOURCE, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_WATERESOURCE, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveWaterResourceData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.SOURCE_OF_WATER);
                        savePlotIrrigationTypeXrefData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveWaterResourceData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveWaterResourceData", "");
                    }
                }
            });
        } else {
            savePlotIrrigationTypeXrefData(context, oncomplete);
        }
    }

    //to save plot irrigation xref data
    public static void savePlotIrrigationTypeXrefData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getPlotIrrigationTypeXrefData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            //   dataAccessHandler.deleteRow(DatabaseKeys.TABLE_PLOTIRRIGATIONTYPEXREF, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOTIRRIGATIONTYPEXREF, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ savePlotIrrigationTypeXrefData data saved successfully");

                        saveSoilResourceData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ savePlotIrrigationTypeXrefData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for savePlotIrrigationTypeXrefData", "");
                    }
                }
            });
        } else {
            if (CommonUiUtils.checkFordripDetails(context,CommonConstants.PLOT_CODE)) {
                savedripirrigation(context,false, oncomplete); // Drip is recommended and not yet installed
            } else {
                if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                    savePlantation(context, oncomplete); //TODO ROJA
                } else {
                    saveGeoTagData(context, oncomplete);
                }
                // saveGeoTagData(context, oncomplete); // Otherwise skip drip, save geotag only //TODO
            }
            // saveSoilResourceData(context, oncomplete);
        }
    }

    //to save fertilizer data
    public static void saveFertilizerData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getFertilizerData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Fertilizer_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FERTLIZER, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveFERTLIZERData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.FERTILIZER);
                        CommonConstants.fertilizerapplydate = "";
                        saveRecommndFertilizerData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveFERTLIZER data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveFertilizerData", "");
                        saveRecommndFertilizerData(context, oncomplete);
//                        savePestData(context, oncomplete);
                    }
                }
            });
        } else {
            saveRecommndFertilizerData(context, oncomplete);
//            savePestData(context, oncomplete);
        }
    }

    //to save recommended fertilizer data
    public static void saveRecommndFertilizerData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getRecommndFertilizerData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Fertilizer_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_FERTLIZER, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_RECOMMND_FERTLIZER, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ save_recommnd_fertlizerdata data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.RECMND_FERTILIZER);
                        savePestData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ save_recommnd_fertlizerd data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for save_recommnd_fertlizerd", "");
                        savePestData(context, oncomplete);
                    }
                }
            });
        } else {
            savePestData(context, oncomplete);
        }
    }

    //to save pest data
    public static void savePestData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getPestData(context);
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Pest_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_PEST, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PEST, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ PEST data saved successfully");

                        savePestChemicalXrefData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ savePEST data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for savePestData", "");
                        savePestChemicalXrefData(context, oncomplete);
                    }
                }
            });
        } else {
            savePestChemicalXrefData(context, oncomplete);
        }
    }

    //to save pest chemical xref data
    public static void savePestChemicalXrefData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getPestChemicalXrefData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_PESTCHEMICALXREF, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PESTCHEMICALXREF, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ PESTCHEMICALXREF data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.CHEMICAL_DETAILS);
                        DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
                        DataManager.getInstance().deleteData(DataManager.MAIN_PEST_DETAIL);
//                        saveganodermadata(context, oncomplete);
                        saveDiseaseData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ savePESTCHEMICALXREF data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for savePestChemicalXrefData", "");
//                        saveganodermadata(context, oncomplete);
                        saveDiseaseData(context, oncomplete);
                    }
                }
            });
        } else {
            saveDiseaseData(context, oncomplete);
            //  saveganodermadata(context, oncomplete);
        }
    }




    //to save disease data
    public static void saveDiseaseData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getDiseaseData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Disease_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_DISEASE, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_DISEASE, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ DISEASE data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.CHEMICAL_DETAILS);
                        DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
                        DataManager.getInstance().deleteData(DataManager.MAIN_PEST_DETAIL);
//                        DataManager.getInstance().deleteData(DataManager.DISEASE_DETAILS);

                        saveWeedData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ save DISEASE data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveDiseaseData", "");
                        // saveganodermadata(context, oncomplete);
                        saveWeedData(context, oncomplete);
                    }
                }
            });
        } else {
            //  saveganodermadata(context, oncomplete);
            saveWeedData(context, oncomplete);
        }
    }

    //to save weed data
    public static void saveWeedData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Weed WeedModel = (Weed) DataManager.getInstance().getDataFromManager(DataManager.WEEDING_DETAILS);
        if (null != WeedModel) {
            WeedModel.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            WeedModel.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            WeedModel.setPlotCode(CommonConstants.PLOT_CODE);
            WeedModel.setIsweavilsseen(1);
            WeedModel.setIsactive(1);
            WeedModel.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            WeedModel.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            WeedModel.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            WeedModel.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(WeedModel));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting WeedModel data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            saveRecordIntoActivityLog(context, CommonConstants.Weed_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_WEED, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_WEED, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ TABLE_WEED data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.WEEDING_DETAILS);
                        DataManager.getInstance().deleteData(DataManager.DISEASE_DETAILS);
                        saveYieldeData(context,oncomplete);

                    } else {
                        saveYieldeData(context, oncomplete);
                        Log.e(LOG_TAG, "@@@ TABLE_WEED data saving failed due to " + msg);
                    }
                }
            });
        } else {
            saveYieldeData(context,oncomplete);
        }
    }

    //to save health plantation data
    public static void saveHealthplantationData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Healthplantation HealthplantationModel = (Healthplantation) DataManager.getInstance().getDataFromManager(DataManager.WEEDING_HEALTH_OF_PLANTATION_DETAILS);
        if (null != HealthplantationModel) {
            HealthplantationModel.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            HealthplantationModel.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            HealthplantationModel.setPlantationstatetypeid(1);
            HealthplantationModel.setIsactive(1);
            HealthplantationModel.setPlotCode(CommonConstants.PLOT_CODE);
            HealthplantationModel.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            HealthplantationModel.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            HealthplantationModel.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            HealthplantationModel.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(HealthplantationModel));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting HealthplantationModel data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            saveRecordIntoActivityLog(context, CommonConstants.Crop_Maintenance_Plantation_Category);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_HEALTHPLANTATION, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ HEALTHPLANTATION data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.WEEDING_HEALTH_OF_PLANTATION_DETAILS);
                        saveHOPPictureData(context, oncomplete);
                    } else {
                        saveHOPPictureData(context, oncomplete);
                        Log.e(LOG_TAG, "@@@ HEALTHPLANTATION data saving failed due to " + msg);
                    }
                }
            });
        } else {
            saveHOPPictureData(context, oncomplete);
        }
    }

    //to save harvest data
    public static void saveHarvestData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        Harvest HarvestModel = (Harvest) DataManager.getInstance().getDataFromManager(DataManager.FFB_HARVEST_DETAILS);
        if (null != HarvestModel) {
            HarvestModel.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            HarvestModel.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            HarvestModel.setPlotCode(CommonConstants.PLOT_CODE);
            HarvestModel.setIsActive(1);
            HarvestModel.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            HarvestModel.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            HarvestModel.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            HarvestModel.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(HarvestModel));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting HarvestModel data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            saveRecordIntoActivityLog(context, CommonConstants.Harvest_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_HARVEST, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ FFB_HARVEST_DETAILS data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.FFB_HARVEST_DETAILS);
                        saveNutrientData(context, oncomplete);
//                        oncomplete.execute(true, "data saved successfully", "");
                    } else {
                        Log.e(LOG_TAG, "@@@ FFB_HARVEST_DETAILS data saving failed due to " + msg);
//                        oncomplete.execute(false, "data saving failed", "");
                        saveNutrientData(context, oncomplete);
                    }
                }
            });
        } else {
            saveNutrientData(context, oncomplete);
        }
    }

    //to save soil resource data
    public static void saveSoilResourceData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        SoilResource soilResourceModel = (SoilResource) DataManager.getInstance().getDataFromManager(DataManager.SoilType);
        if (null != soilResourceModel) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean checkRecordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().querySoilResourceCheck(CommonConstants.PLOT_CODE));

            if (checkRecordExisted && soilResourceModel.getCreateddate() != null) {
                soilResourceModel.setCreatedbyuserid(soilResourceModel.getCreatedbyuserid());
                soilResourceModel.setCreateddate(soilResourceModel.getCreateddate());
            } else {
                soilResourceModel.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                soilResourceModel.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            soilResourceModel.setPlotcode(CommonConstants.PLOT_CODE);
            soilResourceModel.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            soilResourceModel.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            soilResourceModel.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            soilResourceModel.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(soilResourceModel));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting SoilResourceData data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

//            if (checkRecordExisted) {
//                String whereCondition = " where  PlotCode= '" + CommonConstants.PLOT_CODE + "'";
//                dataAccessHandler.updateData(DatabaseKeys.TABLE_SOILRESOURCE, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ address data updated successfully");
//                            DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, false);
//                            DataManager.getInstance().deleteData(DataManager.SoilType);
//                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                           savePlantation(context, oncomplete); //TODO ROJA
//                            } else {
//                                saveGeoTagData(context, oncomplete);
//                            }
//                        } else {
//                            Log.e(LOG_TAG, "@@@ address data update failed due to " + msg);
//                        }
//                    }
//                });
//            } else {
//
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_SOILRESOURCE, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ SoilResource data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.SoilType);
                        //    savedripirrigation(context, oncomplete); // Drip is recommended and not yet installed
                        if (CommonUiUtils.checkFordripDetails(context,CommonConstants.PLOT_CODE)) {
                            savedripirrigation(context,false, oncomplete); // Drip is recommended and not yet installed
                        } else {
                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                                savePlantation(context, oncomplete); //TODO ROJA
                            } else {
                                saveGeoTagData(context, oncomplete);
                            }
                            //  saveGeoTagData(context, oncomplete); // Otherwise skip drip, save geotag only //TODO
                        }



                    } else {
                        Log.e(LOG_TAG, "@@@ SoilResource data saving failed due to " + msg);
                    }
                }
            });
            // }

        } else {
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                savePlantation(context, oncomplete);
            } else {
                saveGeoTagData(context, oncomplete);
            }
        }
    }

    public static void savedripirrigation(final Context context,  boolean isDirectSave,final ApplicationThread.OnComplete<String> oncomplete) {
        final List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        if (dripList == null || dripList.isEmpty()) {
            Log.w(LOG_TAG, "DripIrrigationModel list is null or empty, skipping drip save but continuing flow.");

            // ✅ Don't mark it as failure, mark as success
            oncomplete.execute(true, "No drip data", "");

            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                savePlantation(context, oncomplete);
            } else {
                saveGeoTagData(context, oncomplete);
            }
            return;
        }


        String plotCode = CommonConstants.PLOT_CODE;

        // Step 1: Fetch TypeCdDmt SortOrder map
        Map<Integer, Integer> sortOrderMap = dataAccessHandler.getStatusTypeSortOrderMap();

        // Step 2: Find the StatusTypeId with max SortOrder
        int maxSortOrder = -1;
        int maxStatusTypeId = -1;
        for (DripIrrigationModel model : dripList) {
            int sortOrder = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sortOrder = sortOrderMap.getOrDefault(model.getStatusTypeId(), -1);
            }
            if (sortOrder > maxSortOrder) {
                maxSortOrder = sortOrder;
                maxStatusTypeId = model.getStatusTypeId();
            }
        }

        // Step 3: Deactivate all records for the plotCode (only once)
        String deactivateAllQuery = "UPDATE DripIrrigation SET IsActive = 0, ServerUpdatedStatus = 0 WHERE PlotCode = '" + plotCode + "' And IsActive = 1  ";
        dataAccessHandler.executeRawQuery(deactivateAllQuery);
        Log.d(LOG_TAG, "Deactivated all previous DripIrrigation records for plot: " + plotCode);

        // Step 4: Begin save loop
        int finalCount = dripList.size();
        int[] saveCount = {0};

        for (DripIrrigationModel model : dripList) {
            int statusTypeId = model.getStatusTypeId();

            // Special deactivation for 826 case
            if (statusTypeId == 826 && model.getDripStatusDone() == 1) {
                String deactivate848Query = "UPDATE DripIrrigation SET IsActive = 0, ServerUpdatedStatus = 0 " +
                        "WHERE PlotCode = '" + plotCode + "' AND StatusTypeId = 848 AND IsActive = 1";
                dataAccessHandler.executeRawQuery(deactivate848Query);
                Log.d(LOG_TAG, "Deactivated 848 due to 826 DripStatusDone=1");
            }

            // Set fields
            model.setPlotCode(plotCode);
            model.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            model.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            model.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            model.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            model.setServerUpdatedStatus(0);

            // Only the highest SortOrder StatusTypeId is IsActive
            model.setIsActive(statusTypeId == maxStatusTypeId ? 1 : 0);

            // Clean fields
            model.setComments(safeString(model.getComments()));
            model.setFileName(safeString(model.getFileName()));
            model.setFileLocation(safeString(model.getFileLocation()));
            model.setFileExtension(safeString(model.getFileExtension()));
            model.setAckFileName(safeString(model.getAckFileName()));
            model.setAckFileLocation(safeString(model.getAckFileLocation()));
            model.setAckFileExtension(safeString(model.getAckFileExtension()));

            Log.d("DSDDFileLocation", safeString(model.getFileLocation()));
            Log.d("DSinsertAckFileLocation", safeString(model.getAckFileLocation()));
            Log.d("getAmount", model.getAmount() + "");
            model.setAmount(model.getAmount() == null ? 0.0 : model.getAmount());
            model.setDdBank(safeString(model.getDdBank()));
            model.setDdBankAccountNumber(safeString(model.getDdBankAccountNumber()));
            model.setDdChequeNumber(safeString(model.getDdChequeNumber()));
            model.setDate(formatDateToDbSafe(model.getDate()));
            model.setTrenchMarkingTypeId(safeFlag(model.getTrenchMarkingTypeId()));
            model.setHorticultureRecommendedPlants(safeFlag(model.getHorticultureRecommendedPlants()));
            model.setCompanyId(safeFlag(model.getCompanyId()));
            model.setCompanyId(safeFlag(model.getCompanyId()));
            model.setHOId(safeFlag(model.getHOId()));


            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                JSONObject jsonObject = new JSONObject(gson.toJson(model));
                Map<String, Object> dataMap = CommonUtils.toMap(jsonObject);
                List<Map<String, Object>> dataList = new ArrayList<>();
                dataList.add(dataMap);

                boolean exists = dataAccessHandler.checkValueExistedInDatabase(
                        Queries.getInstance().checkRecordStatusInTable(
                                DatabaseKeys.DripIrrigation,
                                "PlotCode",
                                "StatusTypeId",
                                plotCode,
                                statusTypeId
                        )
                );

                if (exists) {
                    String whereCondition = " WHERE PlotCode = '" + plotCode + "' AND StatusTypeId = " + statusTypeId;
                    dataAccessHandler.updateData(DatabaseKeys.DripIrrigation, dataList, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCount[0]++;
                            Log.d(LOG_TAG, success ? "DripIrrigation updated." : "Update failed: " + msg);
                            if (saveCount[0] == finalCount) {

                                String inactivequery =
                                        "UPDATE DripIrrigation SET IsActive = 0 WHERE PlotCode = '" + plotCode + "' And IsActive = 1 ";
                                String updateIsActiveSql =
                                        "UPDATE DripIrrigation SET IsActive = 1 WHERE Id = (" +
                                                "   SELECT d.Id FROM DripIrrigation d " +
                                                "   JOIN TypeCdDmt t ON d.StatusTypeId = t.TypeCdId " +
                                                "   WHERE d.PlotCode = '" + plotCode + "' " +
                                                "   ORDER BY t.SortOrder DESC LIMIT 1);";

                                dataAccessHandler.executeRawQuery(inactivequery);
                                dataAccessHandler.executeRawQuery(updateIsActiveSql);
                                if (saveCount[0] == finalCount) {
                                    finalizeDripSave(context, isDirectSave, oncomplete);
                                }

                            }
                        }
                    });
                } else {
                    dataAccessHandler.insertDataOld(DatabaseKeys.DripIrrigation, dataList, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCount[0]++;
                            Log.d(LOG_TAG, success ? "DripIrrigation inserted." : "Insert failed: " + msg);
                            if (saveCount[0] == finalCount) {

                                String inactivequery =
                                        "UPDATE DripIrrigation SET IsActive = 0 WHERE PlotCode = '" + plotCode + "' AND IsActive = 1 ";
                                String updateIsActiveSql =
                                        "UPDATE DripIrrigation SET IsActive = 1 WHERE Id = (" +
                                                "   SELECT d.Id FROM DripIrrigation d " +
                                                "   JOIN TypeCdDmt t ON d.StatusTypeId = t.TypeCdId " +
                                                "   WHERE d.PlotCode = '" + plotCode + "' " +
                                                "   ORDER BY t.SortOrder DESC LIMIT 1);";

                                dataAccessHandler.executeRawQuery(inactivequery);
                                dataAccessHandler.executeRawQuery(updateIsActiveSql);
                                if (saveCount[0] == finalCount) {
                                    finalizeDripSave(context, isDirectSave, oncomplete);
                                }
                            }
                        }
                    });
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error serializing DripIrrigationModel", e);
            }
        }
    }

/*
    public static void savedripirrigation(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        final List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        if (dripList == null || dripList.isEmpty()) {
            Log.e(LOG_TAG, "DripIrrigationModel list is null or empty, skipping save.");
            oncomplete.execute(false, "No drip data", "");
            saveGeoTagData(context, oncomplete);
            return;
        }

        String plotCode = CommonConstants.PLOT_CODE;

        // 🔁 Track latest record index for each StatusTypeId
        Map<Integer, Integer> latestIndexMap = new HashMap<>();
        for (int i = 0; i < dripList.size(); i++) {
            latestIndexMap.put(dripList.get(i).getStatusTypeId(), i);
        }

        // 🔁 Process each model for insert/update
        int finalCount = dripList.size();
        int[] saveCount = {0};

        for (int i = 0; i < finalCount; i++) {
            DripIrrigationModel model = dripList.get(i);
            int statusTypeId = model.getStatusTypeId();
            if (statusTypeId == 826 && model.getDripStatusDone() == 1) {
                String deactivate848Query = "UPDATE " + DatabaseKeys.DripIrrigation +
                        " SET IsActive = 0, ServerUpdatedStatus = 0 " +
                        "WHERE PlotCode = '" + plotCode + "' AND StatusTypeId = 848 AND IsActive = 1";
                dataAccessHandler.executeRawQuery(deactivate848Query);
                Log.d(LOG_TAG, "848 record deactivated due to 826 DripStatusDone=1: " + deactivate848Query);
            }
            else{
                String deactivate848Query = "UPDATE " + DatabaseKeys.DripIrrigation +
                        " SET IsActive = 0, ServerUpdatedStatus = 0 " +
                        "WHERE PlotCode = '" + plotCode + "'AND IsActive = 1";
                dataAccessHandler.executeRawQuery(deactivate848Query);
                Log.d(LOG_TAG, "848 record deactivated due to 826 DripStatusDone=1: " + deactivate848Query);
            }
            // ✅ Set core fields
            model.setPlotCode(plotCode);
            model.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            model.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            model.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            model.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            model.setServerUpdatedStatus(0);
            model.setIsActive(i == latestIndexMap.get(statusTypeId) ? model.getIsActive() : 0);

            // ✅ Clean values
            model.setComments(safeString(model.getComments()));
            if (model.getFileName() != null && !model.getFileName().trim().isEmpty()) {
                model.setFileName(safeString(model.getFileName()));
            }
            if (model.getFileLocation() != null && !model.getFileLocation().trim().isEmpty()) {
                model.setFileLocation(safeString(model.getFileLocation()));
            }
            if (model.getFileExtension() != null && !model.getFileExtension().trim().isEmpty()) {
                model.setFileExtension(safeString(model.getFileExtension()));
            }

            if (model.getAckFileName() != null && !model.getAckFileName().trim().isEmpty()) {
                model.setAckFileName(safeString(model.getAckFileName()));
            }
            if (model.getAckFileLocation() != null && !model.getAckFileLocation().trim().isEmpty()) {
                model.setAckFileLocation(safeString(model.getAckFileLocation()));
            }
            if (model.getAckFileExtension() != null && !model.getAckFileExtension().trim().isEmpty()) {
                model.setAckFileExtension(safeString(model.getAckFileExtension()));
            }
            model.setDdBank(safeString(model.getDdBank()));
            model.setDdBankAccountNumber(safeString(model.getDdBankAccountNumber()));
            model.setDdChequeNumber(safeString(model.getDdChequeNumber()));
            model.setDate(formatDateToDbSafe(model.getDate()));
            model.setTrenchMarkingTypeId(safeFlag(model.getTrenchMarkingTypeId()));
            model.setHorticultureRecommendedPlants(safeFlag(model.getHorticultureRecommendedPlants()));
            model.setCompanyId(safeFlag(model.getCompanyId()));
//            model.setAckFileExtension(safeString(model.getAckFileExtension()));
//            model.setAckFileLocation(safeString(model.getAckFileLocation()));
//            model.setAckFileName(safeString(model.getAckFileName()));

            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                JSONObject jsonObject = new JSONObject(gson.toJson(model));
                Map<String, Object> dataMap = CommonUtils.toMap(jsonObject);
                List<Map<String, Object>> dataList = new ArrayList<>();
                dataList.add(dataMap);

                // 🔍 Check if record exists
                boolean exists = dataAccessHandler.checkValueExistedInDatabase(
                        Queries.getInstance().checkRecordStatusInTable(
                                DatabaseKeys.DripIrrigation,
                                "PlotCode",
                                "StatusTypeId",
                                plotCode,
                                statusTypeId
                        )
                );

                if (exists) {
                    // ✅ UPDATE existing record
                    String whereCondition = " WHERE PlotCode = '" + plotCode + "' AND StatusTypeId = " + statusTypeId;
                    dataAccessHandler.updateData(DatabaseKeys.DripIrrigation, dataList, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCount[0]++;
                            Log.d(LOG_TAG, success ? "DripIrrigation record updated." : "Update failed: " + msg);
                            oncomplete.execute(true, "Drip data saved directly", "");
                            UiUtils.showCustomToastMessage(success ? "DripIrrigation record updated." : "Update failed: " + msg, context, 0);
                            if (saveCount[0] == finalCount) {
                                finalizeDripSave(context, oncomplete);
                            }
                        }
                    });
                } else {
                    // ✅ INSERT new record
                    dataAccessHandler.insertDataOld(DatabaseKeys.DripIrrigation, dataList, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCount[0]++;
                            UiUtils.showCustomToastMessage(success ? "DripIrrigation record inserted." : "Insert failed: " + msg, context, 0);

                            Log.d(LOG_TAG, success ? "DripIrrigation record inserted." : "Insert failed: " + msg);
                            if (saveCount[0] == finalCount) {
                                finalizeDripSave(context, oncomplete);
                            }
                        }
                    });
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error serializing DripIrrigationModel", e);
            }
        }

        // ✅ Final cleanup

    }
*/

    //    static void finalizeDripSave(Context context, ApplicationThread.OnComplete<String> oncomplete) {
//        DataManager.getInstance().deleteData(DataManager.TypeOfIrrigation);
//        DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//        Log.v(LOG_TAG, "All Drip records processed. Calling postSaveCleanup.");
//        oncomplete.execute(true, "Success", "");
//      //  postSaveCleanup(context, oncomplete);
//    }
    private static void finalizeDripSave(Context context, boolean isDirectSave, ApplicationThread.OnComplete<String> oncomplete) {
        DataManager.getInstance().deleteData(DataManager.TypeOfIrrigation);
        DataManager.getInstance().deleteData(DataManager.DripIrrigation);
        if (isDirectSave) {
            // 🔁 Direct save: just call oncomplete and exit
            oncomplete.execute(true, "Success", "");
        } else {
            //   saveGeoTagData(context, oncomplete);
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                savePlantation(context, oncomplete); //TODO ROJA
            } else {
                saveGeoTagData(context, oncomplete);
            }
            // 👣 Step-by-step: continue to GeoTag, Conversion, etc.
            //  saveGeoTagData(context, oncomplete);
        }
    }
//    public static void savedripirrigation(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
//        final List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        final List<Map<String, Object>> dataToInsert = new ArrayList<>();
//
//        if (dripList == null || dripList.isEmpty()) {
//            Log.e(LOG_TAG, "DripIrrigationModel list is null or empty, skipping save.");
//            oncomplete.execute(false, "No drip data", "");
//            saveGeoTagData(context, oncomplete);
//            return;
//        }
//
//        String plotCode = CommonConstants.PLOT_CODE;
//
//        // 🔁 Find latest record index for each StatusTypeId
//        Map<Integer, Integer> latestIndexMap = new HashMap<>();
//        for (int i = 0; i < dripList.size(); i++) {
//            latestIndexMap.put(dripList.get(i).getStatusTypeId(), i);
//        }
//
//        // 🔄 Deactivate previous DB entries for each StatusTypeId
//        Set<Integer> handledStatusTypes = new HashSet<>();
//        for (DripIrrigationModel model : dripList) {
//            int statusTypeId = model.getStatusTypeId();
//            if (!handledStatusTypes.contains(statusTypeId)) {
//                handledStatusTypes.add(statusTypeId);
//
//                String deactivateQuery = "UPDATE " + DatabaseKeys.DripIrrigation +
//                        " SET IsActive = 0, ServerUpdatedStatus = 0 " +
//                        "WHERE PlotCode = '" + plotCode + "' AND StatusTypeId = " + statusTypeId + " AND IsActive = 1";
//
//                dataAccessHandler.executeRawQuery(deactivateQuery);
//                Log.d(LOG_TAG, "Deactivated previous records: " + deactivateQuery);
//            }
//        }
//
//        for (int i = 0; i < dripList.size(); i++) {
//            DripIrrigationModel model = dripList.get(i);
//
//            // ✅ Set core audit fields
//            model.setPlotCode(plotCode);
//            model.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
//            model.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//            model.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
//            model.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//            model.setServerUpdatedStatus(0); // Always reset before syncing
//
//            // ✅ Only latest of same StatusTypeId is active
//            int latestIndex = latestIndexMap.get(model.getStatusTypeId());
//            model.setIsActive(i == latestIndex ? model.getIsActive() : 0);
//
//            // ✅ Clean values (null-safe)
//            model.setComments(safeString(model.getComments()));
//            model.setFileName(safeString(model.getFileName()));
//            model.setFileLocation(safeString(model.getFileLocation()));
//            model.setFileExtension(safeString(model.getFileExtension()));
//            model.setDdBank(safeString(model.getDdBank()));
//            model.setDdBankAccountNumber(safeString(model.getDdBankAccountNumber()));
//            model.setDdChequeNumber(safeString(model.getDdChequeNumber()));
//            model.setDate(formatDateToDbSafe(model.getDate()));
//            model.setTrenchMarkingTypeId(safeFlag(model.getTrenchMarkingTypeId()));
//            model.setHorticultureRecommendedPlants(safeFlag(model.getHorticultureRecommendedPlants()));
//            model.setCompanyId(safeFlag(model.getCompanyId()));
//            model.setAckFileExtension(safeString(model.getAckFileExtension()));
//            model.setAckFileLocation(safeString(model.getAckFileLocation()));
//            model.setAckFileName(safeString(model.getAckFileName()));
//
//            try {
//                // Convert to Map<String, Object> using Gson + JSONObject
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                JSONObject jsonObject = new JSONObject(gson.toJson(model));
//                Map<String, Object> map = CommonUtils.toMap(jsonObject);
//                dataToInsert.add(map);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "Error converting model to JSON", e);
//            }
//        }
//
//        // ✅ Insert into DB
//        if (!dataToInsert.isEmpty()) {
//            dataAccessHandler.insertDataOld(DatabaseKeys.DripIrrigation, dataToInsert, new ApplicationThread.OnComplete<String>() {
//                @Override
//                public void execute(boolean success, String result, String msg) {
//                    if (success) {
//                        Log.v(LOG_TAG, "Drip irrigation data inserted successfully.");
//                        DataManager.getInstance().deleteData(DataManager.TypeOfIrrigation);
//                        DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//                    } else {
//                        Log.e(LOG_TAG, "Insertion failed: " + msg);
//                    }
//                    postSaveCleanup(context, oncomplete);
//                }
//            });
//        } else {
//            Log.i(LOG_TAG, "No data to insert into DripIrrigation.");
//            postSaveCleanup(context, oncomplete);
//        }
//    }

    private static String formatDateToDbSafe(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) return null;

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(inputDate.replace("\\", "")); // remove any escape characters
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e("DateFormatError", "Invalid date: " + inputDate, e);
            return inputDate; // fallback to original if parsing fails
        }
    }




//    public static void savedripirrigation(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
//        List<Map<String, Object>> dataToSave = getDripIrrigationData();
//
//        if (dataToSave != null && !dataToSave.isEmpty()) {
//            // Correctly cast to List<DripIrrigationModel> and get the first item
//            List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//            DripIrrigationModel dripdata = (dripList != null && !dripList.isEmpty()) ? dripList.get(0) : null;
//
//            if (dripdata == null) {
//                Log.e(LOG_TAG, "DripIrrigationModel data is null, skipping save.");
//                oncomplete.execute(false, "No drip data", "");
//                return;
//            }
//
//            Log.v(LOG_TAG + " getDripIrrigationData", dataToSave.size() + "");
//            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(
//                    Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.DripIrrigation, "PlotCode", dripdata.getPlotCode()));
//            Log.v(LOG_TAG + " recordExisted in savedripirrigation ", recordExisted + "");
//
//            if (!recordExisted) {
//                dataAccessHandler.insertDataOld(DatabaseKeys.DripIrrigation, dataToSave, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ dripirrigation data saved successfully");
//                            DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                                // savePlantation(context, oncomplete);
//                            } else {
//                                saveGeoTagData(context, oncomplete);
//                            }
//                        } else {
//                            Log.e(LOG_TAG, "@@@ dripirrigation data saving failed: " + msg);
//                            oncomplete.execute(false, "Drip data insert failed", msg);
//                        }
//                    }
//                });
//            } else {
//                String whereCondition = " where PlotCode='" + CommonConstants.PLOT_CODE + "'";
//                dataAccessHandler.updateData(DatabaseKeys.DripIrrigation, dataToSave, true, whereCondition, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ dripirrigation data updated successfully");
//                            DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                                // savePlantation(context, oncomplete);
//                            } else {
//                                saveGeoTagData(context, oncomplete);
//                            }
//                        } else {
//                            Log.e(LOG_TAG, "@@@ DripIrrigation update failed: " + msg);
//                            oncomplete.execute(false, "Drip data update failed", msg);
//                        }
//                    }
//                });
//            }
//        } else {
//            savePlotIrrigationTypeXrefData(context, oncomplete);
//        }
//    }


    //    public static void savedripirrigation(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
//        List dataToSave = getDripIrrigationData();
//
//        if (null != dataToSave && !dataToSave.isEmpty() && dataToSave.size() != 0) {
//            DripIrrigation dripdata = (DripIrrigation) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
//            Log.v(LOG_TAG+"getdripirrigationData", dataToSave.size()+"");
//            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.DripIrrigation, "PlotCode", dripdata.getPlotCode()));
//            Log.v(LOG_TAG+"recordExisted  in savedripirrigation ", recordExisted+"");
//            //saveRecordIntoActivityLog(context, CommonConstants.Water_Soil_Power_details_update);
//            if (!recordExisted) {
//                dataAccessHandler.insertDataOld(DatabaseKeys.DripIrrigation, dataToSave, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ dripirrigation data saved successfully");
//                            DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                                //   savePlantation(context, oncomplete); //TODO ROJA
//                            } else {
//                                saveGeoTagData(context, oncomplete);
//                            }
//                        } else {
//                            Log.e(LOG_TAG, "@@@ saveWaterResourceData data saving failed due to " + msg);
//                            oncomplete.execute(false, "data saving failed for saveWaterResourceData", "");
//                        }
//                    }
//                });
//            } else {
//                String whereCondition = " where  PlotCode='" + CommonConstants.PLOT_CODE + "'";
//                dataAccessHandler.updateData(DatabaseKeys.DripIrrigation, dataToSave, true, whereCondition, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ dripirrigation data updated successfully");
//                            DataManager.getInstance().deleteData(DataManager.DripIrrigation);
//                            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                                //   savePlantation(context, oncomplete); //TODO ROJA
//                            } else {
//                                saveGeoTagData(context, oncomplete);
//                            }
//                        } else {
//                            Log.e(LOG_TAG, "@@@ DripIrrigation update    failed due to " + msg);
//                            oncomplete.execute(false, "data saving failed for DripIrrigation", "");
//                        }
//                    }
//                });
//            }
//        }
//    else {
//            savePlotIrrigationTypeXrefData(context, oncomplete);
//        }
//    }
    //to save currentcrop for plots data
    private static void saveCurrentCropsForPlotData(final Context context, final int saveCount, final ApplicationThread.OnComplete<String> oncomplete) {
        final List<CropModel> currentCropsList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_CURRENT_CROPS_DATA);
        if (null != currentCropsList && currentCropsList.size() > 0) {
            saveCropCount = 0;
            for (CropModel cropModel : currentCropsList) {
                List dataToInsert = new ArrayList();
                PlotCurrentCrop plotCurrentCrop = new PlotCurrentCrop();
                plotCurrentCrop.setCropid(cropModel.cropId);
                plotCurrentCrop.setCurrentcroparea(Double.parseDouble(cropModel.recName));
                plotCurrentCrop.setPlotcode(CommonConstants.PLOT_CODE);
                plotCurrentCrop.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plotCurrentCrop.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plotCurrentCrop.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plotCurrentCrop.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plotCurrentCrop.setServerupdatedstatus(0);
                plotCurrentCrop.setIsactive(cropModel.isActive);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(plotCurrentCrop));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                    DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                    boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_PLOTCURRENTCROP, "PlotCode", "CropId", CommonConstants.PLOT_CODE, cropModel.cropId));
                    if (recordExisted) {
                        Log.v(LOG_TAG, "@@@@ update record");
                        String whereCondition = " where CropId = " + cropModel.cropId + " and PlotCode = '" + CommonConstants.PLOT_CODE + "'";
                        dataAccessHandler.updateData(DatabaseKeys.TABLE_PLOTCURRENTCROP, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                saveCropCount++;
                                if (saveCropCount == currentCropsList.size()) {
                                    Log.v(LOG_TAG, "@@@ saveCurrentCropsData data saved successfully");
                                    DataManager.getInstance().deleteData(DataManager.PLOT_CURRENT_CROPS_DATA);
                                    saveNeighBourCropsData(context, oncomplete);

                                } else {
                                    Log.v(LOG_TAG, "@@@ next record " + saveCount);
                                }
                                if (success) {

                                    Log.v(LOG_TAG, "@@@ saveCurrentCropsData data saving success" + msg);
                                } else {
                                    Log.e(LOG_TAG, "@@@ saveCurrentCropsData data saving failed due to " + msg);
                                }
                            }
                        });
                    } else {
                        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOTCURRENTCROP, dataToInsert, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                saveCropCount++;
                                if (saveCropCount == currentCropsList.size()) {
                                    Log.v(LOG_TAG, "@@@ saveCurrentCropsData data saved successfully");
                                    DataManager.getInstance().deleteData(DataManager.PLOT_CURRENT_CROPS_DATA);
                                    saveNeighBourCropsData(context, oncomplete);
                                } else {
                                    Log.v(LOG_TAG, "@@@ next record " + saveCount);
                                }
                                if (success) {

                                } else {
                                    Log.e(LOG_TAG, "@@@ saveCurrentCropsData data saving failed due to " + msg);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        } else {
            saveNeighBourCropsData(context, oncomplete);
        }
    }

    //to save neighbourplots data
    private static void saveNeighbourPlotsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        final List<CropModel> neighbourCropsList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA);
        if (null != neighbourCropsList && !neighbourCropsList.isEmpty()) {
            saveCropCount = 0;
            for (CropModel cropModel : neighbourCropsList) {
                List dataToInsert = new ArrayList();
                NeighbourPlot plotCurrentCrop = new NeighbourPlot();
                plotCurrentCrop.setCropid(cropModel.cropId);
                plotCurrentCrop.setName(cropModel.recName);
                plotCurrentCrop.setPlotCode(CommonConstants.PLOT_CODE);
                plotCurrentCrop.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plotCurrentCrop.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plotCurrentCrop.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plotCurrentCrop.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plotCurrentCrop.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                plotCurrentCrop.setIsactive(cropModel.isActive);
                DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_NEIGHBOURPLOT, "PlotCode", "CropId", CommonConstants.PLOT_CODE, cropModel.cropId));
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(plotCurrentCrop));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }

                if (recordExisted) {
                    Log.v(LOG_TAG, "@@@@ update record");
                    String whereCondition = " where CropId = " + cropModel.cropId + " and PlotCode = '" + CommonConstants.PLOT_CODE + "'";
                    dataAccessHandler.updateData(DatabaseKeys.TABLE_NEIGHBOURPLOT, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCropCount++;
                            if (saveCropCount == neighbourCropsList.size()) {
                                DataManager.getInstance().deleteData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA);
                                if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                                    saveWaterResourceData(context, oncomplete);
                                } else {
                                    saveRecordIntoFarmerHistory(context, oncomplete);
                                }
                            } else {
                                Log.v(LOG_TAG, "@@@ next record " + saveCropCount);
                            }
                            if (success) {
                                Log.v(LOG_TAG, "@@@ saveNeighbourPlotsData data saving success" + msg);
                            } else {
                                Log.e(LOG_TAG, "@@@ saveNeighbourPlotsData data saving failed due to " + msg);
                            }
                        }
                    });
                } else {
                    dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_NEIGHBOURPLOT, dataToInsert, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            saveCropCount++;
                            if (saveCropCount == neighbourCropsList.size()) {
                                DataManager.getInstance().deleteData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA);
                                if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                                    saveWaterResourceData(context, oncomplete);
                                } else {
                                    saveRecordIntoFarmerHistory(context, oncomplete);
                                }
                            } else {
                                Log.v(LOG_TAG, "@@@ next record " + saveCropCount);
                            }
                            if (success) {

                                Log.v(LOG_TAG, "@@@ insert saveNeighbourPlotsData data saving success" + msg);
                                if (CommonUtils.isFromCropMaintenance()) {
                                    saveWaterResourceData(context, oncomplete);
                                }
                            } else {
                                Log.e(LOG_TAG, "@@@ insert saveNeighbourPlotsData data saving failed due to " + msg);
                            }
                        }
                    });
                }
            }
        } else {
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveWaterResourceData(context, oncomplete);
            } else {
                saveRecordIntoFarmerHistory(context, oncomplete);
            }
        }
    }

    //to save water resources data
    private static List getWaterResourceData() {
        List<WaterResource> waterResourceList = (List<WaterResource>) DataManager.getInstance().getDataFromManager(DataManager.SOURCE_OF_WATER);
        List dataToInsert = new ArrayList();
        if (null != waterResourceList && !waterResourceList.isEmpty()) {
            for (WaterResource mWaterResource : waterResourceList) {
                mWaterResource.setPlotcode(CommonConstants.PLOT_CODE);
                mWaterResource.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mWaterResource.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWaterResource.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mWaterResource.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWaterResource.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mWaterResource.setIsactive(1);
                mWaterResource.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                Log.e(LOG_TAG+ "=====1190", waterResourceList.get(0).getCropMaintenanceCode());
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mWaterResource));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting WaterResource data");
                }
            }
        }
        return dataToInsert;
    }
    //to save water resources data
    private static List<Map<String, Object>> getDripIrrigationData() {
        List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        List<Map<String, Object>> dataToInsert = new ArrayList<>();

        if (dripList != null && !dripList.isEmpty()) {
            for (DripIrrigationModel dripData : dripList) {
                dripData.setPlotCode(CommonConstants.PLOT_CODE);
                dripData.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                dripData.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                dripData.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                dripData.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                dripData.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                dripData.setIsActive(1);

                dripData.setComments(safeString(dripData.getComments()));
                dripData.setFileName(safeString(dripData.getFileName()));
                dripData.setFileLocation(safeString(dripData.getFileLocation()));
                dripData.setDdBank(safeString(dripData.getDdBank()));
                dripData.setDdBankAccountNumber(safeString(dripData.getDdBankAccountNumber()));
                dripData.setDdChequeNumber(safeString(dripData.getDdChequeNumber()));
                dripData.setDate(safeString(dripData.getDate()));
                dripData.setTrenchMarkingTypeId(safeFlag(dripData.getTrenchMarkingTypeId()));
                dripData.setHorticultureRecommendedPlants(safeFlag(dripData.getHorticultureRecommendedPlants()));

                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dripData));
                    dataToInsert.add(CommonUtils.toMap(jsonObject));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error converting dripData to JSON", e);
                }

                Log.d(LOG_TAG + " [DripData]", "Processed plotCode: " + dripData.getPlotCode());
            }
        } else {
            Log.w(LOG_TAG, "No DripIrrigation data found.");
        }

        return dataToInsert;
    }

    // Converts -1 or null to null, else returns value
    private static Integer safeFlag(Integer value) {
        return (value != null && value == -1) ? null : value;
    }

    // Converts blank string to null
    private static String safeString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : null;
    }


    //to save plot irrigation xref
    private static List getPlotIrrigationTypeXrefData() {
        List<PlotIrrigationTypeXref> plotIrrigationTypeXrefList = (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);
        List dataToInsert = new ArrayList();
        if (null != plotIrrigationTypeXrefList && !plotIrrigationTypeXrefList.isEmpty()) {
            for (PlotIrrigationTypeXref mPlotIrrigationTypeXref : plotIrrigationTypeXrefList) {
                mPlotIrrigationTypeXref.setPlotcode(CommonConstants.PLOT_CODE);
                mPlotIrrigationTypeXref.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mPlotIrrigationTypeXref.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPlotIrrigationTypeXref.setName(mPlotIrrigationTypeXref.getName());
                mPlotIrrigationTypeXref.setIrrigationtypeid(mPlotIrrigationTypeXref.getIrrigationtypeid());
                mPlotIrrigationTypeXref.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mPlotIrrigationTypeXref.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPlotIrrigationTypeXref.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mPlotIrrigationTypeXref.setIsactive(1);
                mPlotIrrigationTypeXref.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mPlotIrrigationTypeXref));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting PlotIrrigationTypeXref data");
                }
            }
        }
        return dataToInsert;
    }

    //to save fertilizer data
    private static List getFertilizerData() {
        List<Fertilizer> FertilizerList = (List<Fertilizer>) DataManager.getInstance().getDataFromManager(DataManager.FERTILIZER);
        List dataToInsert = new ArrayList();
        if (null != FertilizerList && FertilizerList.size() > 0) {
            for (Fertilizer mFertilizer : FertilizerList) {
                mFertilizer.setPlotcode(CommonConstants.PLOT_CODE);
                mFertilizer.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mFertilizer.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mFertilizer.setRatescale(1);
                mFertilizer.setComments("");
                mFertilizer.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                mFertilizer.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mFertilizer.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mFertilizer.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mFertilizer.setIsactive(1);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mFertilizer));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mFertilizer data");
                }
            }
        }
        return dataToInsert;
    }

    //to get recommended fertilizer data
    private static List getRecommndFertilizerData() {
        List<RecommndFertilizer> FertilizerList = (List<RecommndFertilizer>) DataManager.getInstance().getDataFromManager(DataManager.RECMND_FERTILIZER);
        List dataToInsert = new ArrayList();
        if (null != FertilizerList && FertilizerList.size() > 0) {
            for (RecommndFertilizer mFertilizer : FertilizerList) {
                mFertilizer.setPlotcode(CommonConstants.PLOT_CODE);
                mFertilizer.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mFertilizer.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mFertilizer.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                mFertilizer.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mFertilizer.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mFertilizer.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mFertilizer.setIsactive(1);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mFertilizer));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mFertilizer data");
                }
            }
        }
        return dataToInsert;
    }
    // to save pest data
//    private static List getPestData(Context context) {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        String initialCode = CommonConstants.PEST_CODE_PREFIX +CommonConstants.TAB_ID+CommonConstants.PLOT_CODE;
//        Log.v(LOG_TAG, "@@@ initialCode " + initialCode);
//        String ccQuery = Queries.getInstance().getMaxPestCode(initialCode);
//        Log.v(LOG_TAG, "@@@ ccQuery " + ccQuery);
//        initialPestCode = dataAccessHandler.getLastVistCodeFromDb(ccQuery);
//        Log.v(LOG_TAG, "@@@ query " + initialPestCode);
////  String pestMaxnum = dataAccessHandler.getOnlyOneValueFromDb(ccQuery);
//        List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
//        List dataToInsert = new ArrayList();
//        if (PestList != null && !PestList.isEmpty()) {
//            for (int i = 0; i < PestList.size(); i++) {
//                Pest mPest = PestList.get(i);
//                mPest.setCode(localPestCode(initialPestCode));
//                mPest.setPlotCode(CommonConstants.PLOT_CODE);
//                mPest.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setIsresultsseen(1);
//                mPest.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//                mPest.setIsactive(1);
//                mPest.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
//                PestList.set(i, mPest);
//                JSONObject ccData = null;
//                try {
//                    Gson gson = new GsonBuilder().serializeNulls().create();
//                    ccData = new JSONObject(gson.toJson(mPest));
//                    dataToInsert.add(CommonUtils.toMap(ccData));
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, "@@@ error while converting mPest data");
//                }
//                if (i == PestList.size()) {
//                    DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
//                    DataManager.getInstance().addData(DataManager.PEST_DETAILS, PestList);
//                }
//            }
//        }
//        return dataToInsert;
//    }
//
//
//    //to save pest chemical xref data
//    private static List getPestChemicalXrefData() {
//        List<PestChemicalXref> PestChemicalXrefList = (List<PestChemicalXref>) DataManager.getInstance().getDataFromManager(DataManager.CHEMICAL_DETAILS);
//        List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
//        List dataToInsert = new ArrayList();
//        if (null != PestChemicalXrefList && !PestChemicalXrefList.isEmpty()) {
//            for (int i = 0; i < PestChemicalXrefList.size(); i++) {
//                PestChemicalXref mPestChemicalXref = PestChemicalXrefList.get(i);
//                mPestChemicalXref.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
//                mPestChemicalXref.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPestChemicalXref.setPestCode(PestList.get(i).getCode());
//                mPestChemicalXref.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
//                mPestChemicalXref.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPestChemicalXref.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//                mPestChemicalXref.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
//                JSONObject ccData = null;
//                try {
//                    Gson gson = new GsonBuilder().serializeNulls().create();
//                    ccData = new JSONObject(gson.toJson(mPestChemicalXref));
//                    dataToInsert.add(CommonUtils.toMap(ccData));
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, "@@@ error while converting mPestChemicalXref data");
//                }
//            }
//        }
//        return dataToInsert;
//    }
//    // to save pest data
//    private static List getPestData(Context context) {
//
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        String initialCode = CommonConstants.PEST_CODE_PREFIX +CommonConstants.TAB_ID+CommonConstants.PLOT_CODE;
//        Log.d("initialCode", initialCode + "");
//        String maxnumber = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getMaxPestCode(CommonConstants.PLOT_CODE));
//        Log.d("maxnumber", maxnumber + "");
//
//        String convertedNum = "";
//        if (!TextUtils.isEmpty(maxnumber)) {
//            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxnumber), 3);
//        } else {
//            convertedNum = CommonUtils.serialNumber(1, 3);
//        }
//
//        StringBuilder pestCoder = new StringBuilder();
//        pestCoder.append(initialCode)
//                .append("-")
//                .append(convertedNum);
//
//        Log.d("pestCoder", pestCoder.toString() + "");
//
//        List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
//        List dataToInsert = new ArrayList();
//        if (PestList != null && !PestList.isEmpty()) {
//            for (int i = 0; i < PestList.size(); i++) {
//                Pest mPest = PestList.get(i);
//                mPest.setCode(pestCoder.toString() + "");
//                mPest.setPlotCode(CommonConstants.PLOT_CODE);
//                mPest.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setIsresultsseen(1);
//                mPest.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//                mPest.setIsactive(1);
//                mPest.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
//                PestList.set(i, mPest);
//                JSONObject ccData = null;
//                try {
//                    Gson gson = new GsonBuilder().serializeNulls().create();
//                    ccData = new JSONObject(gson.toJson(mPest));
//                    dataToInsert.add(CommonUtils.toMap(ccData));
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, "@@@ error while converting mPest data");
//                }
//                if (i == PestList.size()) {
//                    DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
//                    DataManager.getInstance().addData(DataManager.PEST_DETAILS, PestList);
//                }
//            }
//        }
//        return dataToInsert;
//    }
//    private static List getPestData(Context context) {
//
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        String initialCode = CommonConstants.PEST_CODE_PREFIX + CommonConstants.TAB_ID + CommonConstants.PLOT_CODE;
//        Log.d("initialCode", initialCode + "");
//
//        String maxnumber = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getMaxPestCode(CommonConstants.PLOT_CODE));
//        Log.d("maxnumber", maxnumber + "");
//
//        int startingNumber = 1;
//        if (!TextUtils.isEmpty(maxnumber)) {
//            startingNumber = Integer.parseInt(maxnumber) + 1;
//        }
//        List<MainPestModel> PestList = (List<MainPestModel>)
//                DataManager.getInstance().getDataFromManager(DataManager.MAIN_PEST_DETAIL);
//        //  List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
//        List<Map<String, Object>> dataToInsert = new ArrayList<>();
//
//        if (PestList != null && !PestList.isEmpty()) {
//            for (int i = 0; i < PestList.size(); i++) {
//                Pest mPest = PestList.get(i).getPest();
//
//                // Generate unique Code for each record
//                String convertedNum = CommonUtils.serialNumber(startingNumber + i, 3);
//                StringBuilder pestCoder = new StringBuilder();
//                pestCoder.append(initialCode)
//                        .append("-")
//                        .append(convertedNum);
//
//                Log.d("pestCoder", pestCoder.toString() + "");
//
//                // Set dynamically generated Code and other attributes
//                mPest.setCode(pestCoder.toString());
//                mPest.setPlotCode(CommonConstants.PLOT_CODE);
//                mPest.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setIsresultsseen(1);
//                mPest.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                mPest.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                mPest.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//                mPest.setIsactive(1);
//                mPest.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
//
//                // Update the list and prepare for insertion
//                PestList.set(i, mPest);
//
//                try {
//                    Gson gson = new GsonBuilder().serializeNulls().create();
//                    JSONObject ccData = new JSONObject(gson.toJson(mPest));
//                    dataToInsert.add(CommonUtils.toMap(ccData));
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, "@@@ error while converting mPest data");
//                }
//            }
//
//            // Update DataManager with modified PestList
    ////           DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
    ////            DataManager.getInstance().addData(DataManager.PEST_DETAILS, PestList);
//        }
//
//        return dataToInsert;
//    }
    private static List getPestData(Context context) {

        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String initialCode = CommonConstants.PEST_CODE_PREFIX + CommonConstants.TAB_ID + CommonConstants.PLOT_CODE;
        Log.d("initialCode", initialCode + "");

        String maxnumber = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getMaxPestCode(CommonConstants.PLOT_CODE));
        Log.d("maxnumber", maxnumber + "");

        int startingNumber = 1;
        if (!TextUtils.isEmpty(maxnumber)) {
            startingNumber = Integer.parseInt(maxnumber) + 1;
        }

        List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
        List<Map<String, Object>> dataToInsert = new ArrayList<>();

        if (PestList != null && !PestList.isEmpty()) {
            for (int i = 0; i < PestList.size(); i++) {
                Pest mPest = PestList.get(i);

                // Generate unique Code for each record
                String convertedNum = CommonUtils.serialNumber(startingNumber + i, 3);
                StringBuilder pestCoder = new StringBuilder();
                pestCoder.append(initialCode)
                        .append("-")
                        .append(convertedNum);

                Log.d("pestCoder", pestCoder.toString() + "");

                // Set dynamically generated Code and other attributes
                mPest.setCode(pestCoder.toString());
                mPest.setPlotCode(CommonConstants.PLOT_CODE);
                mPest.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mPest.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPest.setIsresultsseen(1);
                mPest.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mPest.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPest.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mPest.setIsactive(1);
                mPest.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);

                // Update the list and prepare for insertion
                PestList.set(i, mPest);

                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    JSONObject ccData = new JSONObject(gson.toJson(mPest));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mPest data");
                }
            }

            // Update DataManager with modified PestList
            //  DataManager.getInstance().deleteData(DataManager.PEST_DETAILS);
            //   DataManager.getInstance().addData(DataManager.PEST_DETAILS, PestList);
        }

        return dataToInsert;
    }

    //to save pest chemical xref data
    private static List getPestChemicalXrefData() {
        List<PestChemicalXref> PestChemicalXrefList = (List<PestChemicalXref>) DataManager.getInstance().getDataFromManager(DataManager.CHEMICAL_DETAILS);
        List<Pest> PestList = (List<Pest>) DataManager.getInstance().getDataFromManager(DataManager.PEST_DETAILS);
        //Log.e("=========>PestList", PestList.size() + "");
//        Log.e("=========>PestChemicalXrefList", PestChemicalXrefList.size() + "");
        List dataToInsert = new ArrayList();
        if (null != PestChemicalXrefList && !PestChemicalXrefList.isEmpty()) {
            for (int i = 0; i < PestChemicalXrefList.size(); i++) {
                PestChemicalXref mPestChemicalXref = PestChemicalXrefList.get(i);
                mPestChemicalXref.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mPestChemicalXref.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPestChemicalXref.setPestCode(PestList.get(i).getCode());
                mPestChemicalXref.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mPestChemicalXref.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mPestChemicalXref.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mPestChemicalXref.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mPestChemicalXref));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mPestChemicalXref data");
                }
            }
        }
        return dataToInsert;
    }

    //to save disease data
    private static List getDiseaseData() {
        List<Disease> DiseaseList = (List<Disease>) DataManager.getInstance().getDataFromManager(DataManager.DISEASE_DETAILS);
        List dataToInsert = new ArrayList();
        if (null != DiseaseList && !DiseaseList.isEmpty()) {
            for (Disease mDisease : DiseaseList) {
                mDisease.setPlotCode(CommonConstants.PLOT_CODE);
                mDisease.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mDisease.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mDisease.setIsdiseasenoticedinpreviousvisit(1);
                mDisease.setIsproblemrectified(1);
                mDisease.setIsresultseen(1);
                mDisease.setProblemrectifiedcomments("");
                mDisease.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                mDisease.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mDisease.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                mDisease.setIsactive(1);
                mDisease.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mDisease));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mDisease data");
                }
            }
        }
        return dataToInsert;
    }

    //to save geo tag data
    public static void saveGeoTagData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        GeoBoundaries geoBoundaries = null;

        if (CommonUtils.isFromConversion()) {
            List dataToSave = getGeoBoundriesData();
            if (null != dataToSave && !dataToSave.isEmpty()) {
                saveRecordIntoActivityLog(context, CommonConstants.Plot_Point_Geo_Boundaries);
                final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveNeighBourCropsData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_GEO_BOUNDARIES);
                            savePlantation(context, oncomplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ saveNeighBourCropsData data saving failed due to " + msg);
                            oncomplete.execute(false, "data saving failed for saveGeoTagData", "");
                        }
                    }
                });
            } else {
                savePlantation(context, oncomplete);
            }
        }
        else if (CommonUtils.isNewRegistration() && CommonConstants.isFromPlotDetails == true) {
            List dataToSave = getGeoBoundriesData();
            if (null != dataToSave && !dataToSave.isEmpty()) {
                final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ Geo Boundaries from Plot data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_GEO_BOUNDARIES);
                            saveConversionPotential(context, oncomplete);
                            //oncomplete.execute(true, "GeoBoundaries from Plot data saving success", "");

                        } else {
                            Log.e(LOG_TAG, "@@@  Geo Boundaries from Plot data data saving failed due to " + msg);
                            oncomplete.execute(false, "data saving failed for saveGeoBoundariesData", "");
                        }
                    }
                });
            }

        }
        else if (CommonUtils.isNewPlotRegistration() && CommonConstants.isFromPlotDetails == true) {
            List dataToSave = getGeoBoundriesData();
            if (null != dataToSave && !dataToSave.isEmpty()) {
                final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ Geo Boundaries from Plot data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_GEO_BOUNDARIES);
                            saveConversionPotential(context, oncomplete);
                            //oncomplete.execute(true, "GeoBoundaries from Plot data saving success", "");
                        } else {
                            Log.e(LOG_TAG, "@@@  Geo Boundaries from Plot data data saving failed due to " + msg);
                            oncomplete.execute(false, "data saving failed for saveGeoBoundariesData", "");
                        }
                    }
                });
            }
        }


        if (isFromCropMaintenance()) {
            geoBoundaries = (GeoBoundaries) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG);
//                DataAccessHandler dataAccessHandler =new DataAccessHandler();
//                String ccQuery = Queries.getInstance().getMaxCropMaintenanceHistoryCode(CommonConstants.PLOT_CODE);
//                String cropMaintenanceHistoryCode = dataAccessHandler.getOnlyOneValueFromDb(ccQuery);
//                CommonConstants.CROP_MAINTENANCE_HISTORY_CODE = generateCropMaintenanceCode(cropMaintenanceHistoryCode);
//            geoBoundaries.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            geoBoundaries.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
        }
        if (isGeoTagTaken == true){
            geoBoundaries = (GeoBoundaries) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_TAG);

            if (geoBoundaries != null && geoBoundaries.getLatitude() != 0 && geoBoundaries.getLongitude() != 0) {
                geoBoundaries.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                geoBoundaries.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                geoBoundaries.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                geoBoundaries.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                geoBoundaries.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                geoBoundaries.setPlotcode(CommonConstants.PLOT_CODE);

                if (null != geoBoundaries) {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    JSONObject ccData = null;
                    List dataToInsert = null;
                    try {
                        ccData = new JSONObject(gson.toJson(geoBoundaries));
                        dataToInsert = new ArrayList();
                        dataToInsert.add(CommonUtils.toMap(ccData));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "@@@ error while converting geo tag data");
                    }
                    Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
                    saveRecordIntoActivityLog(context, CommonConstants.Plot_Point_Geo_Tag);
                    final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                    if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration() || CommonUtils.isFromFollowUp() || CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToInsert, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    Log.v(LOG_TAG, "@@@ geo tag data saved successfully");
                                    DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
                                    if (isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//                                        oncomplete.execute(true, "data saving success saveGeoTagData", "");
//                                        saveHarvestData(context, oncomplete);
                                        ComplaintStatusHistory complaintStatusHistory = (ComplaintStatusHistory) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_STATUS_HISTORY);
                                        if (complaintStatusHistory != null) {
                                            saveComplaintHistoryData(context, oncomplete);
                                        } else {
                                            oncomplete.execute(true, "data saving success saveGeoTagData", "");
                                        }
                                    } else {
                                        if (CommonConstants.isFromPlotDetails == false) {
                                            saveConversionPotential(context, oncomplete);
                                        }
                                    }
                                } else {
                                    Log.e(LOG_TAG, "@@@ geo tag data saving failed due to " + msg);
                                    oncomplete.execute(false, "data saving failed saveGeoTagData", "");
                                }
                            }
                        });
                    } else {
                        String whereCondition = " where  PlotCode='" + CommonConstants.PLOT_CODE + "'";
                        dataAccessHandler.updateData(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    Log.v(LOG_TAG, "@@@ geo tag data updated successfully");
                                    DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
//                                oncomplete.execute(true, "data updated successfully", "");
                                    saveConversionPotential(context, oncomplete);
                                } else {
                                    Log.e(LOG_TAG, "@@@ geo tag data update failed due to " + msg);
                                    oncomplete.execute(false, "data saving failed for TABLE_GEOBOUNDARIES", "");
                                }
                            }
                        });
                    }
                }
            }
        }else {
            saveConversionPotential(context, oncomplete);
        }
    }

    //to save potential score for conversion
    public static void saveConversionPotential(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
        if (null != followUp) {
            followUp.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            followUp.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            followUp.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            followUp.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            followUp.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            followUp.setPlotcode(CommonConstants.PLOT_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(followUp));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveConversionPotential data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            saveRecordIntoActivityLog(context, CommonConstants.Followup_Log_Conversion_Potential_Score);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            if (followUp.getPotentialscore() >= 7) {
                saveRecordIntoAlerts(context, AlertType.ALERT_PLOT_FOLLOWUP.getValue());
            }
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FOLLOWUP, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveConversionPotential data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.PLOT_FOLLOWUP);
                        saveReferralsData(context, oncomplete);
                        saveGeoTagData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveConversionPotential data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed saveConversionPotential", "");
                    }
                }
            });

        } else {
            if (CommonUtils.isFromFollowUp()) {
                saveReferralsData(context, oncomplete);

            } else if (isFromCropMaintenance() || CommonUtils.isVisitRequests()){
                oncomplete.execute(true, "data saving failed for saveConversionPotential", "");

            }
//            else {
//
//                oncomplete.execute(false, "data saving failed for saveConversionPotential", "");
//            }
        }
    }


    //to save referrals data
    public static void saveReferralsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getReferralsData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_REFERRALS, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ address data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.REFERRALS_DATA);
                        if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                            saveFertilizerData(context, oncomplete);
//                            saveRecommndFertilizerData(context, oncomplete);
                        } else if (CommonUtils.isFromConversion()||CommonUtils.isNewRegistration() || CommonUtils.isFromFollowUp()) {
                            saveIdProofsData(context, oncomplete);
                        }else {
                            saveMarketSurveyData(context, oncomplete);
                        }
                    } else {
                        Log.e(LOG_TAG, "@@@ address data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveReferralsData", "");
                    }
                }
            });
        } else {
            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveFertilizerData(context, oncomplete);
            } else if (CommonUtils.isFromConversion() ||CommonUtils.isNewRegistration()|| CommonUtils.isNewPlotRegistration() || CommonUtils.isFromFollowUp() ) {
                saveIdProofsData(context, oncomplete);
            }else {
                saveMarketSurveyData(context, oncomplete);
            }
        }
    }

    //to save referrals data
    private static List getReferralsData() {
        List<Referrals> referralsList = (List<Referrals>) DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA);
        List dataToInsert = new ArrayList();
        if (null != referralsList && referralsList.size() > 0) {
            for (Referrals referral : referralsList) {

                referral.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                referral.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                referral.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                referral.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                referral.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));

                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(referral));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    //to save Id proofs data
    public static void saveIdProofsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getIdProofsData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            // dataAccessHandler.deleteRow(DatabaseKeys.TABLE_IDENTITYPROOF, "FarmerCode", CommonConstants.FARMER_CODE, true, oncomplete); //Todo ROJA
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_IDPROOFS, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ address data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.ID_PROOFS_DATA);
//                        oncomplete.execute(true, "data saved successfully", "");
                        saveBanksData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ address data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveIdProofsData", "");
                    }
                }
            });
        } else {
//            oncomplete.execute(true, "data saved successfully", "");
            saveBanksData(context, oncomplete);
        }
    }

    //to get Id proofs data
    private static List getIdProofsData() {
        List<IdentityProof> idProofsList = (List<IdentityProof>) DataManager.getInstance().getDataFromManager(DataManager.ID_PROOFS_DATA);
        List dataToInsert = new ArrayList();
        if (null != idProofsList && idProofsList.size() > 0) {
            for (IdentityProof identityProof : idProofsList) {
                identityProof.setFarmercode(CommonConstants.FARMER_CODE);
                identityProof.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                identityProof.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                identityProof.setIsActive(1);
                identityProof.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                identityProof.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                identityProof.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));

                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(identityProof));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    // Farmer History data
    private static void saveRecordIntoFarmerHistory(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        FarmerHistory farmerHistory = new FarmerHistory();
        farmerHistory.setFarmercode(CommonConstants.FARMER_CODE);
        farmerHistory.setPlotcode(CommonConstants.PLOT_CODE);
        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);

        if (CommonUtils.isFromFollowUp() && (followUp.getIsfarmerreadytoconvert() == 0)){


            if (CommonUtils.isFromConversion() ) {
                if (CommonConstants.leased) {
                    saveLandLordDetails(context, oncomplete);
                }
            } else if(CommonUtils.isPlotSplitFarmerPlots()) {
                savePlotData(context,oncomplete);
            }else {
                saveConversionPotential(context,oncomplete);
            }
        }else {
            // Log.d("PotentialScore", followUp.getIsfarmerreadytoconvert() + "");
            if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
//                if (CommonConstants.leased) {
//                    saveLandLordDetails(context, oncomplete);
//                }
                if (null != followUp) {
                    if (null != followUp.getIsfarmerreadytoconvert()) {
                        if (followUp.getIsfarmerreadytoconvert() == 1) {
                            farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_READY_TO_CONVERT))));
                        } else if (followUp.getIsfarmerreadytoconvert() == 0) {
                            farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_PROSPECTIVE))));
                        }
                    }
                } else {
                    oncomplete.execute(true, "no data to save", "");
                    return;
                }
            }
            else if (CommonUtils.isFromConversion()) {
                farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_CONVERTED))));
            } else if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_UPROOTED))));
            }else  if(CommonUtils.isPlotSplitFarmerPlots()){
                farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_GEO_BOUNDARIES_TAKEN))));
            }else{
                farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_READY_TO_CONVERT))));
            }

            farmerHistory.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerHistory.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

            farmerHistory.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerHistory.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            farmerHistory.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            farmerHistory.setIsactive(1);

            JSONObject ccData = null;
            List dataToInsert = new ArrayList();
            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ccData = new JSONObject(gson.toJson(farmerHistory));
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoFarmerHistory data");
            }

// update the previous plotstatus..........

//        dataAccessHandler.upDataPlotStatus(farmerHistory.getPlotcode());
            dataAccessHandler.upDataPlotStatus(CommonConstants.PLOT_CODE);


            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FARMERHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saved successfully");
                        //oncomplete.execute(true, "data saved successfully", "");
                        if (CommonUtils.isFromConversion() || CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
                            if (CommonConstants.leased) {

                                saveLandLordDetails(context, oncomplete);

                            }
                            else{
                                saveWaterResourceData(context, oncomplete);
                            }
                        } else if(CommonUtils.isPlotSplitFarmerPlots()) {
                            savePlotData(context,oncomplete);
                        }else{
                            saveWaterResourceData(context, oncomplete);
                        }
                    } else {
                        Log.e(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed saveRecordIntoFarmerHistory", "");
                    }
                }
            });
        }

    }

//    public static void saveRecordIntoFarmerHistory(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
//        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        FarmerHistory farmerHistory = new FarmerHistory();
//        farmerHistory.setFarmercode(CommonConstants.FARMER_CODE);
//        farmerHistory.setPlotcode(CommonConstants.PLOT_CODE);
//        FollowUp followUp = (FollowUp) DataManager.getInstance().getDataFromManager(DataManager.PLOT_FOLLOWUP);
//        if (followUp == null) {
//            followUp = (FollowUp) dataAccessHandler.getFollowupData(
//                    Queries.getInstance().getFollowUpBinding(CommonConstants.PLOT_CODE), 0);
//        }
// if (CommonUtils.isFromFollowUp() && (followUp.getIsfarmerreadytoconvert() == 1)) {
//            Log.d("FollowUpCheck", "Farmer is ready to convert from follow-up");
//
//            if (!CommonUiUtils.isDripDetailsEntered()) {
//                Log.d("FollowUpCheck Drip ", "Drip details entered, setting status to DripPending");
//                String dripPendingIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                        Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_DripPending));
//                farmerHistory.setStatustypeid(Integer.parseInt(dripPendingIdStr));
//            } else {
//                Log.d("FollowUpCheck isDripCompleted", "Drip details not entered, setting status to Initiated");
//                String initiatedIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                        Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_READY_TO_CONVERT));
//                farmerHistory.setStatustypeid(Integer.parseInt(initiatedIdStr));
//            }
//
//        //    proceedToSaveFarmerHistory(context, farmerHistory, dataAccessHandler, oncomplete);
//        }
//
//      else  if (CommonUtils.isFromFollowUp() && (followUp.getIsfarmerreadytoconvert() == 0)){
//
//
//
//            if (CommonUtils.isFromConversion()) {
//                if (CommonConstants.leased) {
//                    saveLandLordDetails(context, oncomplete);
//                }
//            } else if(CommonUtils.isPlotSplitFarmerPlots()) {
//                savePlotData(context,oncomplete);
//            }else {
//                saveConversionPotential(context,oncomplete);
//            }
//        }
//
//        else {
//     // Log.d("PotentialScore", followUp.getIsfarmerreadytoconvert() + "");
//     if (CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()) {
//      /*          String isDripStr = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getisdriprequiredQuery(CommonConstants.districtIdPlot));
//                Log.d("DripCheck", "Raw result from DB: " + isDripStr);
//
//                boolean isDrip = isDripStr != null && isDripStr.equals("true");*/
//         if (followUp != null) {
//             Log.d("FollowUpCheck", "followUp is not null");
//
//             Integer isReadyToConvert = followUp.getIsfarmerreadytoconvert();
//             Log.d("FollowUpCheck", "isReadyToConvert value: " + isReadyToConvert);
//
//             if (isReadyToConvert != null) {
//                 if (isReadyToConvert == 1) {
//                     Log.d("FollowUpCheck", "Farmer is ready to convert");
//                     if (CommonUiUtils.checkFordripDetails(context) || CommonUiUtils.isDripACheck(context)) {
//
//                         if (!CommonUiUtils.isDripDetailsEntered()) {
//                             Log.d("FollowUpCheck", "Drip details  entered, setting status to DripPending");
//                             String dripPendingIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                                     Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_DripPending));
//                             Log.d("FollowUpCheck", "DripPending Status ID: " + dripPendingIdStr);
//                             farmerHistory.setStatustypeid(Integer.parseInt(dripPendingIdStr));
//                         } else {
//                             Log.d("FollowUpCheck", "Drip details NOT entered, setting status to not Initiated");
//                             String initiatedIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                                     Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_Initiated));
//                             Log.d("FollowUpCheck", "Initiated Status ID: " + initiatedIdStr);
//                             farmerHistory.setStatustypeid(Integer.parseInt(initiatedIdStr));
//                         }
//                     }
//                     else{
//                         Log.d("FollowUpCheck", "Drip not available");
//                         String initiatedIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                                 Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_Initiated));
//                         Log.d("FollowUpCheck", "Initiated Status ID: " + initiatedIdStr);
//                         farmerHistory.setStatustypeid(Integer.parseInt(initiatedIdStr));
//                     }
//                 }
//
//                 else if (isReadyToConvert == 0) {
//                     Log.d("FollowUpCheck", "Farmer is NOT ready to convert, setting status to Initiated");
//                     String initiatedIdStr = dataAccessHandler.getOnlyOneValueFromDb(
//                             Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_Initiated));
//                     Log.d("FollowUpCheck", "Initiated Status ID: " + initiatedIdStr);
//                     farmerHistory.setStatustypeid(Integer.parseInt(initiatedIdStr));
//                 } else {
//                     Log.w("FollowUpCheck", "Unexpected value for isReadyToConvert: " + isReadyToConvert);
//                 }
//             } else {
//                 Log.w("FollowUpCheck", "isReadyToConvert is null");
//             }
//         }
//
///*                if (null != followUp) {
//                    if (null != followUp.getIsfarmerreadytoconvert()) {
//                        if (followUp.getIsfarmerreadytoconvert() == 1 && !CommonUiUtils.isDripDetailsEntered()){
//                            farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_DripPending))));
//                        }
//                       else if (followUp.getIsfarmerreadytoconvert() == 1) {
//                            farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_Initiated))));
//                        } else if (followUp.getIsfarmerreadytoconvert() == 0) {
//                            farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_Initiated))));
//                        }
//
//                    }
//                }*/
//         else {
//             oncomplete.execute(true, "no data to save", "");
//             return;
//         }
//     } else if (CommonUtils.isFromConversion()) {
//         farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_CONVERTED))));
//     } else if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
//         farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_UPROOTED))));
//     } else if (CommonUtils.isPlotSplitFarmerPlots()) {
//         farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_GEO_BOUNDARIES_TAKEN))));
//     } else {
//         farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_ID_READY_TO_CONVERT))));
//     }
// }
//            farmerHistory.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//            farmerHistory.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//
//            farmerHistory.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//            farmerHistory.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//            farmerHistory.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//            farmerHistory.setIsactive(1);
//
//            JSONObject ccData = null;
//            List dataToInsert = new ArrayList();
//            try {
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                ccData = new JSONObject(gson.toJson(farmerHistory));
//                dataToInsert.add(CommonUtils.toMap(ccData));
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoFarmerHistory data");
//            }
//
//// update the previous plotstatus..........
//
    ////        dataAccessHandler.upDataPlotStatus(farmerHistory.getPlotcode());
//            dataAccessHandler.upDataPlotStatus(CommonConstants.PLOT_CODE);
//
//
//            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FARMERHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
//                @Override
//                public void execute(boolean success, String result, String msg) {
//                    if (success) {
//                        Log.v(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saved successfully");
//                        //oncomplete.execute(true, "data saved successfully", "");
//                        if (CommonUtils.isFromConversion()) {
//                            saveLandLordDetails(context, oncomplete);
//                        } else if(CommonUtils.isPlotSplitFarmerPlots()) {
//                            savePlotData(context,oncomplete);
//                        }  if (CommonUtils.isFromFollowUp() ) {
//                            savedripirrigation(context, oncomplete);
//                        }
//                        else{
//                            saveWaterResourceData(context, oncomplete);
//                        }
//                    } else {
//                        Log.e(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saving failed due to " + msg);
//                        oncomplete.execute(false, "data saving failed saveRecordIntoFarmerHistory", "");
//                    }
//                }
//            });
//        }
//


    //to save market survey data
    public static void saveMarketSurveyData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        final MarketSurvey marketSurvey = (MarketSurvey) DataManager.getInstance().getDataFromManager(DataManager.MARKET_SURVEY_DATA);
        if (null != marketSurvey) {


//            marketSurvey.setCode(CommonConstants.MARKET_SURVEY_CODE_PREFIX + CommonConstants.FARMER_CODE);
            marketSurvey.setCode(CommonConstants.MarketSurveyCode);
            marketSurvey.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            marketSurvey.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            marketSurvey.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            marketSurvey.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            marketSurvey.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            marketSurvey.setIsActive(1);
            marketSurvey.setFarmerCode(CommonConstants.FARMER_CODE);
            marketSurvey.setFarmerName(CommonConstants.farmerFirstName + " " + CommonConstants.farmerMiddleName + " " + CommonConstants.farmerLastName);

            JSONObject ccData = null;
            List dataToInsert = new ArrayList();
            try {
                Gson gson = new GsonBuilder().serializeNulls().create();
                ccData = new JSONObject(gson.toJson(marketSurvey));
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveMarketSurveyData data");
            }
            saveRecordIntoActivityLog(context, CommonConstants.Market_Survey);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_MARKETSURVEY, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveMarketSurveyData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.MARKET_SURVEY_DATA);
                        saveCookingOilData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveMarketSurveyData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for saveMarketSurveyData", "");
                    }
                }
            });
        } else {
            oncomplete.execute(true, "data saved successfully", "");
        }
    }

    //to save cooking oil data
    public static void saveCookingOilData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getCookingOilData(context, CommonConstants.FARMER_CODE);
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COOKINGOIL, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveCookingOilData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.OIL_TYPE_MARKET_SURVEY_DATA);
                        oncomplete.execute(true, "data saved successfully", "");
                    } else {
                        Log.e(LOG_TAG, "@@@ saveCookingOilData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed OIL_TYPE_MARKET_SURVEY_DATA", "");
                    }
                }
            });
        } else {
            oncomplete.execute(true, "data not available", "");
        }
    }

    //to get cooking oil data
    private static List getCookingOilData(Context context, String farmerCode) {
        List<CookingOil> cookingOilList = (List<CookingOil>) DataManager.getInstance().getDataFromManager(DataManager.OIL_TYPE_MARKET_SURVEY_DATA);
        List dataToInsert = new ArrayList();
        if (null != cookingOilList && cookingOilList.size() > 0) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            for (CookingOil cookingOil : cookingOilList) {
//                cookingOil.setMarketSurveyCode(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getMarketSurveyId(farmerCode)));
                cookingOil.setMarketSurveyCode(CommonConstants.MarketSurveyCode);
                cookingOil.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                cookingOil.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                cookingOil.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                cookingOil.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                cookingOil.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));

                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(cookingOil));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    //to save intercrops data
    public static void saveInterCropsData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getInterCropData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Intercrop);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveInterCropsData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.PLOT_INTER_CROP_DATA);
                        DataManager.getInstance().deleteData(DataManager.PLOT_INTER_CROP_DATA_PAIR);
                        saveReferralsData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveInterCropsData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed saveInterCropsData", "");
                    }
                }
            });
        } else {
            saveReferralsData(context, oncomplete);
        }
    }

    //to get intercrop data
    private static List getInterCropData() {
        List<CropModel> interCropsList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA);
        List dataToInsert = new ArrayList();
        if (null != interCropsList && !interCropsList.isEmpty()) {
            for (CropModel cropModel : interCropsList) {
                InterCropPlantationXref interCropPlantationXref = new InterCropPlantationXref();
                interCropPlantationXref.setCropId(cropModel.cropId);

                interCropPlantationXref.setRecmCropId(cropModel.recId);
                Log.v("@@@interCrop",""+cropModel.recId);
                interCropPlantationXref.setPlotCode(CommonConstants.PLOT_CODE);
                interCropPlantationXref.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                interCropPlantationXref.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                interCropPlantationXref.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                interCropPlantationXref.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                interCropPlantationXref.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                interCropPlantationXref.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(interCropPlantationXref));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    //to save banks sata
    public static void saveBanksData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        FarmerBank farmerBank = (FarmerBank) DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS);
        if (null != farmerBank) {

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_FARMERBANK, "FarmerCode", CommonConstants.FARMER_CODE));
            if (recordExisted && null != farmerBank.getCreatedDate()) {
                farmerBank.setCreatedbyuserid(farmerBank.getCreatedbyuserid());
                farmerBank.setCreatedDate(farmerBank.getCreatedDate());
            } else {
                farmerBank.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                farmerBank.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            farmerBank.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            farmerBank.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            farmerBank.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            farmerBank.setFarmercode(CommonConstants.FARMER_CODE);
            farmerBank.setIsActive(1);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(farmerBank));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveBanksData data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            saveRecordIntoActivityLog(context, CommonConstants.Farmer_Bank_Details);
            if (recordExisted) {
                String whereCondition = " where  FarmerCode='" + farmerBank.getFarmercode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_FARMERBANK, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveBanksData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.FARMER_BANK_DETAILS);

                            if(CommonUtils.isNewRegistration())
                            {
                                saveMarketSurveyData(context, oncomplete);
                            }
                            else {
                                oncomplete.execute(true, "saveBanksData successfully", "");

                            }
                        } else {
                            Log.e(LOG_TAG, "@@@ saveBanksData update failed due to " + msg);
                        }

                    }
                });
            } else {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FARMERBANK, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveBanksData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.FARMER_BANK_DETAILS);
                            if(CommonUtils.isNewRegistration())
                            {
                                saveMarketSurveyData(context, oncomplete);
                            }
                            else {
                                oncomplete.execute(true, "saveBanksData successfully", "");

                            }
                        } else {
                            Log.e(LOG_TAG, "@@@ saveBanksData data saving failed due to " + msg);
                            oncomplete.execute(false, "data saving failed for saveBanksData", "");
                        }
                    }
                });
            }
        } else {
            if(CommonUtils.isNewRegistration())
            {
                saveMarketSurveyData(context, oncomplete);
            }
            else {
                oncomplete.execute(true, "no data for bank details", "");

            }
        }
    }

    //to get geo boundaries data
    public static List<GeoBoundaries> getGeoBoundriesData() {
        List geoBoundaries = new ArrayList<>();
        List<GeoBoundaries> savedGeoBoundaries = (List<GeoBoundaries>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_GEO_BOUNDARIES);
        if (null != savedGeoBoundaries && !savedGeoBoundaries.isEmpty()) {
            for (GeoBoundaries gpsCoordinate : savedGeoBoundaries) {
                Log.d("beforeinsertlat", gpsCoordinate.getLatitude() + "");
                Log.d("beforeinsertlong", gpsCoordinate.getLongitude() + "");
                gpsCoordinate.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                gpsCoordinate.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                gpsCoordinate.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                gpsCoordinate.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                gpsCoordinate.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                gpsCoordinate.setPlotcode(CommonConstants.PLOT_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(gpsCoordinate));
                    geoBoundaries.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return geoBoundaries;
    }

    //to get plantation data
    public static List<Plantation> getPlantationData() {
        List plantationList = new ArrayList<>();
        List<Plantation> savedPlantationData = (List<Plantation>) DataManager.getInstance().getDataFromManager(DataManager.PLANTATION_CON_DATA);
        if (null != savedPlantationData && !savedPlantationData.isEmpty()) {
            for (Plantation plantation : savedPlantationData) {
                plantation.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plantation.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plantation.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plantation.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                plantation.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                plantation.setPlotcode(CommonConstants.PLOT_CODE);
                plantation.setIsActive(1);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(plantation));
                    plantationList.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return plantationList;
    }

    //conversion plantation details
    public static void savePlantation(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getPlantationData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLANTATION, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ savePlantation data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.PLANTATION_CON_DATA);
                        save_Con_Plant_PictureData(context, oncomplete);
                    }
                    else {
//                        save_Con_Plant_PictureData(context, oncomplete);
                        Log.e(LOG_TAG, "@@@ savePlantation data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for savePlantation", "");
                    }
                }
            });
        }
        else {

            if (CommonUtils.isFromCropMaintenance() || CommonUtils.isVisitRequests()) {
                saveCropMaintenanceHistoryData(context, oncomplete);
            } else {
                saveInterCropsData(context, oncomplete);
            }
        }
    }

    //to save landlord details
    public static void saveLandLordDetails(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        PlotLandlord plotLandlord = (PlotLandlord) DataManager.getInstance().getDataFromManager(DataManager.LANDLORD_LEASED_DATA);
        if (null != plotLandlord) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_PLOTLANDLORD, "PlotCode", plotLandlord.getPlotcode()));

            if (recordExisted && null != plotLandlord.getCreateddate()) {
                plotLandlord.setCreatedbyuserid(plotLandlord.getCreatedbyuserid());
                plotLandlord.setCreateddate(plotLandlord.getCreateddate());
            } else {
                plotLandlord.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                plotLandlord.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            plotLandlord.setPlotcode(CommonConstants.PLOT_CODE);
            plotLandlord.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            plotLandlord.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            plotLandlord.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            plotLandlord.setIsactive(1);

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(plotLandlord));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveLandLordDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            if (recordExisted) {
                String whereCondition = " where  PlotCode = '" + plotLandlord.getPlotcode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_PLOTLANDLORD, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveLandLordDetails data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.LANDLORD_LEASED_DATA);
                            saveLandLordBankDetails(context, onComplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ saveLandLordDetails data saving failed due to " + msg);
                            onComplete.execute(false, "data saving failed for saveLandLordDetails", "");
                        }
                    }
                });
            } else {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PLOTLANDLORD, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveLandLordDetails data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.LANDLORD_LEASED_DATA);
                            saveLandLordBankDetails(context, onComplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ saveLandLordDetails data saving failed due to " + msg);
                            onComplete.execute(false, "data saving failed for saveLandLordDetails", "");
                        }
                    }
                });
            }
        } else {
//            onComplete.execute(false, "data saving failed for saveLandLordDetails", "");
            saveWaterResourceData(context, onComplete);
        }
    }

    //to save landlord bank details
    public static void saveLandLordBankDetails(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        LandlordBank landlordBank = (LandlordBank) DataManager.getInstance().getDataFromManager(DataManager.LANDLORD_BANK_DATA);
        if (null != landlordBank) {

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(DatabaseKeys.TABLE_LANDLORDBANK, "PlotCode", landlordBank.getPlotcode()));

            if (recordExisted && null != landlordBank.getCreatedDate()) {
                landlordBank.setCreatedbyuserid(landlordBank.getCreatedbyuserid());
                landlordBank.setCreatedDate(landlordBank.getCreatedDate());
            } else {
                landlordBank.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                landlordBank.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            landlordBank.setPlotcode(CommonConstants.PLOT_CODE);
            landlordBank.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            landlordBank.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            landlordBank.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            landlordBank.setIsActive(1);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(landlordBank));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveLandLordBankDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());

            if (recordExisted) {
                String whereCondition = " where  PlotCode = '" + landlordBank.getPlotcode() + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_LANDLORDBANK, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveLandLordBankDetails data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.LANDLORD_BANK_DATA);
                            saveLandLordIdProofs(context, onComplete);

                        } else {
                            Log.e(LOG_TAG, "@@@ saveLandLordBankDetails data saving failed due to " + msg);
                            onComplete.execute(false, "data saving failed for saveLandLordBankDetails", "");
                        }
                    }
                });
            } else {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_LANDLORDBANK, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveLandLordBankDetails data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.LANDLORD_BANK_DATA);
                            saveLandLordIdProofs(context, onComplete);

                        } else {
                            Log.e(LOG_TAG, "@@@ saveLandLordBankDetails data saving failed due to " + msg);
                            onComplete.execute(false, "data saving failed for saveLandLordBankDetails", "");
                        }
                    }
                });
            }

        } else {
            onComplete.execute(false, "data saving failed for saveLandLordBankDetails", "");
        }
    }

    //to save landlord Id proofs data
    public static void saveLandLordIdProofs(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        List dataToSave = getLandLordIdProofsData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_LANDLORDIDPROOFS, "PlotCode", CommonConstants.PLOT_CODE, true, onComplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_LANDLORDIDPROOFS, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveLandLordIdProofs data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.LANDLORD_IDPROOFS_DATA);
                        saveWaterResourceData(context, onComplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ address saveLandLordIdProofs saving failed due to " + msg);
                        onComplete.execute(false, "saveLandLordIdProofs saving failed", "");
                    }
                }
            });
        } else {
            saveWaterResourceData(context, onComplete);
        }
    }

    //to get landlord Id proofs data
    private static List getLandLordIdProofsData() {
        List<LandlordIdProof> idProofsLandLordList = (List<LandlordIdProof>) DataManager.getInstance().getDataFromManager(DataManager.LANDLORD_IDPROOFS_DATA);
        List dataToInsert = new ArrayList();
        if (null != idProofsLandLordList && idProofsLandLordList.size() > 0) {
            for (LandlordIdProof identityProof : idProofsLandLordList) {
                identityProof.setPlotCode(CommonConstants.PLOT_CODE);
                identityProof.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                identityProof.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                identityProof.setIsActive(1);
                identityProof.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                identityProof.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                identityProof.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));

                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(identityProof));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    //to save nutrient data
    public static void saveNutrientData(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        List dataToSave = getNutrientData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_NUTRIENT, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveNutrientData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.NUTRIENT_DETAILS);
                        saveUpRootmentDetails(context, onComplete);
                        saveGapfillingDetails(context, onComplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ address saveNutrientData saving failed due to " + msg);
                        onComplete.execute(false, "saveNutrientData saving failed for saveNutrientData", "");
                    }
                }
            });
        } else {
            saveUpRootmentDetails(context, onComplete);
            saveGapfillingDetails(context, onComplete);
        }
    }

    public static void  saveGapfillingDetails(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        PlotGapFillingDetails plotgapfillingdetails =
                (PlotGapFillingDetails) DataManager.getInstance().getDataFromManager(DataManager.PlotGapFilling_Details);

        if (plotgapfillingdetails != null && plotgapfillingdetails.getPlotCode() != null
                && !plotgapfillingdetails.getPlotCode().equalsIgnoreCase("null")) {

            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject gapfillingData = null;
            List dataToInsert = null;
            try {
                gapfillingData = new JSONObject(gson.toJson(plotgapfillingdetails));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(gapfillingData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while inserting PlotGapFillingDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + gapfillingData.toString());

            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PlotGapFillingDetails, dataToInsert,
                    new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                Log.v(LOG_TAG, "@@@ Gapfilling data saved successfully");
                                DataManager.getInstance().deleteData(DataManager.PlotGapFilling_Details);
                            } else {
                                Log.e(LOG_TAG, "@@@ save gapfilling data saving failed due to " + msg);
                                onComplete.execute(false, "data saving failed for PlotGapFillingDetails", "");
                            }
                        }
                    });

        } else {
            Log.e(LOG_TAG, "@@@ PlotGapFillingDetails is NULL or PlotCode is NULL");
            //   onComplete.execute(false, "data saving failed: no PlotGapFillingDetails found", "");
        }
    }

    /*   public static void saveGapfillingDetails(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
           PlotGapFillingDetails plotgapfillingdetails = (PlotGapFillingDetails) DataManager.getInstance().getDataFromManager(DataManager.PlotGapFilling_Details);
           if (!plotgapfillingdetails.getPlotCode().equalsIgnoreCase("null")) {
               Gson gson = new GsonBuilder().serializeNulls().create();
               JSONObject gapfillingData = null;
               List dataToInsert = null;
               try {
                   gapfillingData = new JSONObject(gson.toJson(plotgapfillingdetails));
                   dataToInsert = new ArrayList();
                   dataToInsert.add(CommonUtils.toMap(gapfillingData));
               } catch (JSONException e) {
                   Log.e(LOG_TAG, "@@@ error while inserting  PlotGapFillingDetails data");
               }
               Log.v(LOG_TAG, "@@ entered data " + gapfillingData.toString());
               //  saveRecordIntoActivityLog(context, CommonConstants.Uprootment_Details);

               final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
               dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_PlotGapFillingDetails, dataToInsert, new ApplicationThread.OnComplete<String>() {
                   @Override
                   public void execute(boolean success, String result, String msg) {
                       if (success) {
                           Log.v(LOG_TAG, "@@@ Gapfilling data saved successfully");
                           DataManager.getInstance().deleteData(DataManager.PlotGapFilling_Details);

                       } else {
                           Log.e(LOG_TAG, "@@@ save gapfilling data saving failed due to " + msg);
                           onComplete.execute(false, "data saving failed for PlotGapFillingDetails", "");
                       }
                   }
               });
           } else {
   //            onComplete.execute(false, "data saving failed for saveUpRootmentDetails", "");
             //  saveGeoTagData(context, onComplete);
           }
       }
   */
    // to get nutrient data
    private static List getNutrientData() {
        List<Nutrient> netrientDetailsList = (List<Nutrient>) DataManager.getInstance().getDataFromManager(DataManager.NUTRIENT_DETAILS);
        List dataToInsert = new ArrayList();
        if (null != netrientDetailsList && netrientDetailsList.size() > 0) {
            for (Nutrient nutrient : netrientDetailsList) {
                nutrient.setPlotcode(CommonConstants.PLOT_CODE);
                nutrient.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                nutrient.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                nutrient.setIsactive(1);
                nutrient.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                nutrient.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                nutrient.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
                nutrient.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(nutrient));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting farmerPersonalDetails data");
                }
            }
        }
        return dataToInsert;
    }

    //to save uprootment details
    public static void saveUpRootmentDetails(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        Uprootment uprootment = (Uprootment) DataManager.getInstance().getDataFromManager(DataManager.CURRENT_PLANTATION);
        if (null != uprootment) {
            uprootment.setPlotcode(CommonConstants.PLOT_CODE);
            uprootment.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            uprootment.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            uprootment.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
            uprootment.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            uprootment.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
            uprootment.setIsactive(1);
            uprootment.setCropMaintenanceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            List dataToInsert = null;
            try {
                ccData = new JSONObject(gson.toJson(uprootment));
                dataToInsert = new ArrayList();
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting saveUpRootmentDetails data");
            }
            Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
            saveRecordIntoActivityLog(context, CommonConstants.Uprootment_Details);
//            if (uprootment.getMissingtreescount() > 1) {
//                saveRecordIntoAlerts(context, AlertType.ALERT_PLOT_MISSING_TREES.getValue());
//            }
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_UPROOTMENT, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveUpRootmentDetails data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.CURRENT_PLANTATION);

                        saveMissingTreeImages(context, onComplete);


//                        if (uprootment.getMissingtreescount() > 1) { //Todo ROja
//                            saveMissingTreeImages(context, onComplete);
//                        }
//                        else{
//                            saveRaithuDiary(context, onComplete);
//                        }
//
//                        else{
//                            saveHarvestData(context, onComplete);
////                            saveGeoTagData(context, onComplete);
////                            saveWaterResourceData(context, onComplete);
//                        }


                    } else {
                        Log.e(LOG_TAG, "@@@ saveUpRootmentDetails data saving failed due to " + msg);
                        onComplete.execute(false, "data saving failed for saveUpRootmentDetails", "");
                    }
                }
            });
        } else {
            saveRaithuDiary(context, onComplete);
            //    saveHarvestData(context, onComplete);
//            onComplete.execute(false, "data saving failed for saveUpRootmentDetails", "");
            //   saveGeoTagData(context, onComplete); //TODO
        }
    }

    //to save pest code
    public static String localPestCode(final  String pestCode) {

        if (!TextUtils.isEmpty(pestCode)) {
            String[] pestCodeArr = pestCode.split("-");
            int c = pestCodeArr.length;
            int pestNum = Integer.parseInt(pestCodeArr[c-1]);
            initialPestCode = pestCodeArr[0] + "-" + (pestNum + 1);
            return initialPestCode;
        } else {
            String initialCode = CommonConstants.PEST_CODE_PREFIX +
                    CommonConstants.TAB_ID +
                    CommonConstants.PLOT_CODE;
            initialPestCode = initialCode + "-" + 1;
            return initialPestCode;
        }

    }

    //to generate crop maintenance code
    public static String generateCropMaintenanceCode(final String lastCropMaintenanceCode) {
        String cropMaintenanceCode = "";
        String days = "";
        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        int financialYear = fiscalDate.getFiscalYear();
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
            Log.v(LOG_TAG,"days -->"+days);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(lastCropMaintenanceCode)) {
            int i = lastCropMaintenanceCode.lastIndexOf("-");
            String cropMaintenanceCodeArr = lastCropMaintenanceCode.substring(i+1);
            int pestNum = Integer.parseInt(cropMaintenanceCodeArr);
            //cropMaintenanceCode = lastCropMaintenanceCode.substring(0,i) +days+ "-" + (pestNum + 1);
            cropMaintenanceCode = CommonConstants.CROP_CODE_PREFIX + CommonConstants.TAB_ID + CommonConstants.PLOT_CODE+days +"-" + (pestNum + 1);
            Log.v("@@@Code","cropCode -->"+cropMaintenanceCode);
            return cropMaintenanceCode;
        } else {
            String initialCode = CommonConstants.CROP_CODE_PREFIX + CommonConstants.TAB_ID + CommonConstants.PLOT_CODE+days;
            cropMaintenanceCode = initialCode + "-" + 1;
            Log.v("@@@Code","cropCode -->"+cropMaintenanceCode);
            return cropMaintenanceCode;
        }
    }

    //to generate unique complaints code
    public static String generateComplaintsCode(final String lastCollectionCode) {
        String complaintCode = "";
        if (!TextUtils.isEmpty(lastCollectionCode)) {
            String[] complaintCodeArr = lastCollectionCode.split("-");
            int pestNum = Integer.parseInt(complaintCodeArr[1]);
            complaintCode = complaintCodeArr[0] + "-" + (pestNum + 1);
            return complaintCode;
        } else {
            //String initialCode = CommonConstants.COMP_CODE_PREFIX + CommonConstants.PLOT_CODE;
            String initialCode = CommonConstants.COMP_CODE_PREFIX + CommonConstants.TAB_ID + CommonConstants.PLOT_CODE;
            complaintCode = initialCode + "-" + 1;
            return complaintCode;
        }

//        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//        String ccQuery = Queries.getInstance().getMaxCropMaintenanceHistoryCode(CommonConstants.PLOT_CODE);
//        String complaintsCode = dataAccessHandler.getOnlyOneValueFromDb(ccQuery);
//        String cropMaintenanceCode = "";
//        if (!TextUtils.isEmpty(complaintsCode)) {
//            String[] cropMaintenanceCodeArr = complaintsCode.split("-");
//            int pestNum = Integer.parseInt(cropMaintenanceCodeArr[1]);
//            cropMaintenanceCode = cropMaintenanceCodeArr[0] + "-" + (pestNum + 1);
//            return cropMaintenanceCode;
//        } else {
//            String initialCode = CommonConstants.COMP_CODE_PREFIX + CommonConstants.PLOT_CODE;
//            cropMaintenanceCode = initialCode + "-" + 1;
//            return cropMaintenanceCode;
//        }
    }

    //to save  cropmaintenance history data
    public static void saveCropMaintenanceHistoryData(final Context context, final ApplicationThread.OnComplete<String> applicationThread) {
        CropMaintenanceHistory cropMaintenanceHistory = new CropMaintenanceHistory();
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        String ccQuery = Queries.getInstance().getMaxCropMaintenanceHistoryCode(CommonConstants.TAB_ID + CommonConstants.PLOT_CODE, CommonConstants.USER_ID);
        Log.v("@@@query","q "+ccQuery);
        String cropMaintenanceHistoryCode = dataAccessHandler.getLastVistCodeFromDb(ccQuery);
        CommonConstants.CROP_MAINTENANCE_HISTORY_CODE = generateCropMaintenanceCode(cropMaintenanceHistoryCode);
        String visitNumber = "";
        if (!TextUtils.isEmpty(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE)) {
            String[] cropArr = CommonConstants.CROP_MAINTENANCE_HISTORY_CODE.split("-");
            if (cropArr != null && cropArr.length > 0) {
                visitNumber = cropArr[1];
            }
        }

        cropMaintenanceHistory.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        cropMaintenanceHistory.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        cropMaintenanceHistory.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
        cropMaintenanceHistory.setIsActive(1);
        cropMaintenanceHistory.setName(CommonConstants.CROP_MAINTENANCE_HISTORY_NAME + visitNumber);
        cropMaintenanceHistory.setPlotCode(CommonConstants.PLOT_CODE);
        cropMaintenanceHistory.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        cropMaintenanceHistory.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        cropMaintenanceHistory.setCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
        cropMaintenanceHistory.setIsVerified(false);
        cropMaintenanceHistory.setOTP(null);

        JSONObject ccData = null;
        List dataToInsert = new ArrayList();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(cropMaintenanceHistory));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoActivityLog data");
        }
        saveRecordIntoActivityLog(context, Crop_Maintenance);
        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_CROPMAINTENANCEHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@ save RecordInto TABLE_CROPMAINTENANCEHISTORYdata saved successfully");
                    saveInterCropsData(context, applicationThread);

                } else {
                    Log.e(LOG_TAG, "@@@ saveRecordIntoActivityLog data saving failed due to " + msg);
                }
            }
        });
    }














    //to save data into alerts
    public static void saveRecordIntoAlerts(final Context context, final int alertType) {
        Alerts alerts = new Alerts();
        alerts.setPlotCode(CommonConstants.PLOT_CODE);
        alerts.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        alerts.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//        alerts.setAlertType(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//        alerts.setAlertType(alertType);
//        alerts.setServerUpdatedStatus(1);

        JSONObject ccData = null;
        List dataToInsert = new ArrayList();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(alerts));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoAlerts data");
        }

        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_NOTIFICATIONS, dataToInsert, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@ saveRecordIntoAlerts data saved successfully");
                } else {
                    Log.e(LOG_TAG, "@@@ saveRecordIntoAlerts data saving failed due to " + msg);
                }
            }
        });
    }

    //to save data into activitylog
    public static void saveRecordIntoActivityLog(final Context context, final int activityLogTypeId) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setFarmerCode(CommonConstants.FARMER_CODE);
        activityLog.setPlotCode(CommonConstants.PLOT_CODE);
        activityLog.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        activityLog.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        activityLog.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
        activityLog.setActivityTypeId(activityLogTypeId);

        JSONObject ccData = null;
        List dataToInsert = new ArrayList();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(activityLog));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoActivityLog data");
        }

        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_ACTIVITYLOG, dataToInsert, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@ saveRecordIntoActivityLog data saved successfully");
                } else {
                    Log.e(LOG_TAG, "@@@ saveRecordIntoActivityLog data saving failed due to " + msg);
                }
            }
        });
    }

    //to save complaint history data
    public static void saveComplaintHistoryData(final Context context, final ApplicationThread.OnComplete<String> applicationThread) {
        final ComplaintStatusHistory complaintStatusHistory = (ComplaintStatusHistory) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_STATUS_HISTORY);
        boolean isRecordExistedInDb = false;
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        if (!TextUtils.isEmpty(complaintStatusHistory.getComplaintCode())) {
            isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
                    .checkRecordStatusInTable(DatabaseKeys.TABLE_COMPLAINTSTATUSHISTORY, "ComplaintCode", complaintStatusHistory.getComplaintCode()));
        }

        if (isRecordExistedInDb && complaintStatusHistory.getCreatedByUserId() != 0 && complaintStatusHistory.getCreatedDate() != null) {
            complaintStatusHistory.setCreatedByUserId(complaintStatusHistory.getCreatedByUserId());
            complaintStatusHistory.setCreatedDate(complaintStatusHistory.getCreatedDate());
        } else {
            complaintStatusHistory.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintStatusHistory.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }

        complaintStatusHistory.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
        complaintStatusHistory.setIsActive(1);
        complaintStatusHistory.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        complaintStatusHistory.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        complaintStatusHistory.setComplaintCode(complaintStatusHistory.getComplaintCode());
        complaintStatusHistory.setAssigntoUserId(CommonConstants.USER_ID);

        JSONObject ccData = null;
        List dataToInsert = new ArrayList();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(complaintStatusHistory));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveComplaintHistoryData data");
        }

        if (isRecordExistedInDb) {
            dataAccessHandler.updateComplaintStatus(complaintStatusHistory.getComplaintCode());
        }

        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COMPLAINTSTATUSHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@ saveComplaintHistoryData data saved successfully");
                    DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_STATUS_HISTORY);
                    saveComplaintTypeXref(context,complaintStatusHistory.getComplaintCode(), applicationThread);
                } else {
                    Log.e(LOG_TAG, "@@@ saveComplaintHistoryData data saving failed due to " + msg);
                    applicationThread.execute(false, "saving failed due to " + msg, "");
                }
            }
        });
    }


    public static void saveComplaintHistoryDataEditMode(final Context context, final ApplicationThread.OnComplete<String> applicationThread) {
        ComplaintStatusHistory complaintStatusHistory = (ComplaintStatusHistory) DataManager.getInstance().getDataFromManager(DataManager.COMPLAINT_STATUS_HISTORY);
        boolean isRecordExistedInDb = false;
        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        if (!TextUtils.isEmpty(complaintStatusHistory.getComplaintCode())) {
            isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
                    .checkRecordStatusInTable(DatabaseKeys.TABLE_COMPLAINTSTATUSHISTORY, "ComplaintCode", complaintStatusHistory.getComplaintCode()));
        }

        if (isRecordExistedInDb && complaintStatusHistory.getCreatedByUserId() != 0 && complaintStatusHistory.getCreatedDate() != null) {
            complaintStatusHistory.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintStatusHistory.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        } else {
            String ccQuery = Queries.getInstance().getMaxComplaintsHistoryCode(CommonConstants.PLOT_CODE,CommonConstants.USER_ID);
            String complaintHistoryCode = dataAccessHandler.getOnlyOneValueFromDb(ccQuery);
            //CommonConstants.COMPLAINT_CODE = generateComplaintsCode(complaintHistoryCode);
            complaintStatusHistory.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintStatusHistory.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }

        complaintStatusHistory.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
        complaintStatusHistory.setIsActive(1);
        complaintStatusHistory.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        complaintStatusHistory.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        complaintStatusHistory.setComplaintCode(complaintStatusHistory.getComplaintCode());
        complaintStatusHistory.setAssigntoUserId(CommonConstants.USER_ID);

        JSONObject ccData = null;
        List dataToInsert = new ArrayList();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(complaintStatusHistory));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveComplaintHistoryData data");
        }

        if (isRecordExistedInDb) {
            dataAccessHandler.updateComplaintStatus(complaintStatusHistory.getComplaintCode());
        }

        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COMPLAINTSTATUSHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@ saveComplaintHistoryData data saved successfully");
                    DataManager.getInstance().deleteData(DataManager.COMPLAINT_STATUS_HISTORY);
                    applicationThread.execute(true, "saveComplaintHistoryData", "");
                } else {
                    Log.e(LOG_TAG, "@@@ saveComplaintHistoryData data saving failed due to " + msg);
                    applicationThread.execute(false, "saving failed due to " + msg, "");
                }
            }
        });
    }

    //to save complainttype xref
    public static void saveComplaintTypeXref(final Context context,final String ComplaintCode, final ApplicationThread.OnComplete<String> onComplete) {
        ComplaintTypeXref complaintTypeXref = (ComplaintTypeXref) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_TYPE);
        complaintTypeXref.setComplaintCode(ComplaintCode);
        JSONObject ccData = null;
        List dataToInsert = new ArrayList();

        DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        boolean isRecordExistedInDb = false;

        if (!TextUtils.isEmpty(ComplaintCode)) {
            isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
                    .checkRecordStatusInTable(DatabaseKeys.TABLE_COMPLAINTTYPEXREF, "ComplaintCode", ComplaintCode));
        }

        if (isRecordExistedInDb && complaintTypeXref.getCreatedByUserId() != 0 && complaintTypeXref.getCreatedDate() != null) {
            complaintTypeXref.setCreatedByUserId(complaintTypeXref.getCreatedByUserId());
            complaintTypeXref.setCreatedDate(complaintTypeXref.getCreatedDate());
        } else {
            complaintTypeXref.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintTypeXref.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }

        complaintTypeXref.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        complaintTypeXref.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            ccData = new JSONObject(gson.toJson(complaintTypeXref));
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting saveRecordIntoActivityLog data");
        }


        if (isRecordExistedInDb) {
            String whereCondition = " where  ComplaintCode = '" + ComplaintCode + "'";
            dataAccessHandler.updateData(DatabaseKeys.TABLE_COMPLAINTTYPEXREF, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveComplaintTypeXref data saved successfully");
                        saveComplaintData(context,ComplaintCode, onComplete);
                        DataManager.getInstance().deleteData(DataManager.COMPLAINT_TYPE);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveComplaintTypeXref data saving failed due to " + msg);
                    }
                }
            });
        } else {
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COMPLAINTTYPEXREF, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveComplaintTypeXref data saved successfully");
                        saveComplaintData(context,ComplaintCode, onComplete);
                        DataManager.getInstance().deleteData(DataManager.COMPLAINT_TYPE);
                    } else {
                        Log.e(LOG_TAG, "@@@ saveComplaintTypeXref data saving failed due to " + msg);
                    }
                }
            });
        }

    }

    //to save complaint data
    public static void saveComplaintData(final Context context,final String ComplaintCode, final ApplicationThread.OnComplete<String> onComplete) {
        Complaints farmerComplaintsData = (Complaints) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_DETAILS);
        List dataToInsert = new ArrayList();
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        boolean isRecordExistedInDb = false;

        if (!TextUtils.isEmpty(ComplaintCode)) {
            isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
                    .checkRecordStatusInTable(DatabaseKeys.TABLE_COMPLAINT, "Code", ComplaintCode));
        }



        farmerComplaintsData.setCode(ComplaintCode);
        farmerComplaintsData.setPlotCode(CommonConstants.PLOT_CODE);
        farmerComplaintsData.setIsActive(1);
        if (isRecordExistedInDb && farmerComplaintsData.getCreatedByUserId() != 0 && farmerComplaintsData.getCreatedDate() != null) {
            farmerComplaintsData.setCreatedByUserId(farmerComplaintsData.getCreatedByUserId());
            farmerComplaintsData.setCreatedDate(farmerComplaintsData.getCreatedDate());
        } else {
            farmerComplaintsData.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            farmerComplaintsData.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }
        farmerComplaintsData.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        farmerComplaintsData.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        farmerComplaintsData.setServerUpdatedStatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));

        Gson gson = new GsonBuilder().serializeNulls().create();

        JSONObject ccData = null;
        try {
            ccData = new JSONObject(gson.toJson(farmerComplaintsData));
            dataToInsert = new ArrayList();
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@@@ error while converting complaint data");
        }

        if (null != dataToInsert) {
            if (isRecordExistedInDb) {
                String whereCondition = " where  Code = '" + ComplaintCode + "'";
                dataAccessHandler.updateData(DatabaseKeys.TABLE_COMPLAINT, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveComplaintTypeXref data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_DETAILS);
                            saveComplaintRepositoryData(context,ComplaintCode, onComplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ saveComplaintTypeXref data saving failed due to " + msg);
                        }
                    }
                });
            } else {
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COMPLAINT, dataToInsert, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ saveComplaintData data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_DETAILS);
                            saveComplaintRepositoryData(context,ComplaintCode, onComplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ saveComplaintData saving failed due to " + msg);
                            onComplete.execute(false, "saveComplaintData saving failed for saveComplaintData", "");
                        }
                    }
                });
            }
        }
    }


    //to save complaint repo data
    public static void saveComplaintRepositoryData(final Context context,final String ComplaintCode, final ApplicationThread.OnComplete<String> onComplete) {
        List dataToSave = getComplaintRepository(ComplaintCode);
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_COMPLAINTREPOSITORY, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ saveComplaintHistoryData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.NEW_COMPLAINT_REPOSITORY);
                        onComplete.execute(true, "saveComplaintRepositoryData saving success for saveComplaintData", "");
                    } else {
                        Log.e(LOG_TAG, "@@@ saveComplaintData saving failed due to " + msg);
                        onComplete.execute(false, "saveComplaintData saving failed for saveComplaintData", "");
                    }
                }
            });
        } else {
            onComplete.execute(true, "saveComplaintRepositoryData saving success for saveComplaintData", "");
        }
    }

    //to get complaint repo data
    private static List getComplaintRepository(String ComplaintCode) {
        List dataToInsert;
        List<ComplaintRepository> farmerComplaintsRepositoryDataList = (List<ComplaintRepository>) DataManager.getInstance().getDataFromManager(DataManager.NEW_COMPLAINT_REPOSITORY);
        dataToInsert = new ArrayList();
        for (ComplaintRepository complaintRepository : farmerComplaintsRepositoryDataList) {
            complaintRepository.setComplaintCode(ComplaintCode);
            complaintRepository.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintRepository.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            complaintRepository.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
            complaintRepository.setServerUpdatedStatus(0);
            complaintRepository.setIsActive(1);
            complaintRepository.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            Gson gson = new GsonBuilder().serializeNulls().create();
            JSONObject ccData = null;
            try {
                ccData = new JSONObject(gson.toJson(complaintRepository));
                dataToInsert.add(CommonUtils.toMap(ccData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "@@@ error while converting complaint data");
            }
        }

        return dataToInsert;
    }
    public static void updatePlotSliptFarmerGeoboundaries(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        if (CommonUtils.isPlotSplitFarmerPlots()) {
            List dataToSave = getGeoBoundriesData();
            DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            boolean isRecordExistedInDb = false;

            if (!TextUtils.isEmpty(CommonConstants.PLOT_CODE)) {
                isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
                        .checkRecordStatusInTable(DatabaseKeys.TABLE_GEOBOUNDARIES, "PlotCode", "GeoCategoryTypeId", CommonConstants.PLOT_CODE, 206));
            }

            if (isRecordExistedInDb) {
                // Delete existing data
                String deleteCondition = "PlotCode = '" + CommonConstants.PLOT_CODE + "' AND GeoCategoryTypeId = 206";
                dataAccessHandler.deleteData(DatabaseKeys.TABLE_GEOBOUNDARIES, deleteCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ Existing boundaries deleted successfully");
                        } else {
                            Log.e(LOG_TAG, "@@@ Failed to delete existing boundaries due to " + msg);
                        }
                    }
                });
            }

            if (null != dataToSave && !dataToSave.isEmpty()) {
                saveRecordIntoActivityLog(context, CommonConstants.Plot_Point_Retake_Geo_Boundaries);

                // Insert new boundaries
                dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            Log.v(LOG_TAG, "@@@ New boundaries data saved successfully");
                            DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
                            saveRecordIntoFarmerHistory(context, oncomplete);
                        } else {
                            Log.e(LOG_TAG, "@@@ Failed to save new boundaries due to " + msg);
                            oncomplete.execute(false, "Data saving failed for saveGeoTagData", "");
                        }
                    }
                });
            } else {
                Log.v(LOG_TAG, "@@@ No boundaries data to save");
            }
        }
    }

//    //to update farmer geo boundaries //TODO ROJA
//    public static  void updatePlotSliptFarmerGeoboundaries(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
//        if (CommonUtils.isPlotSplitFarmerPlots()) {
//            List dataToSave = getGeoBoundriesData();
//            boolean isRecordExistedInDb = false;
//            DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            if (!TextUtils.isEmpty(CommonConstants.PLOT_CODE)) {
//                isRecordExistedInDb = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance()
//                        .checkRecordStatusInTable(DatabaseKeys.TABLE_GEOBOUNDARIES, "PlotCode","GeoCategoryTypeId", CommonConstants.PLOT_CODE,206));
//            }
//            if(isRecordExistedInDb){
//
//                GeoBoundaries geoBoundaries = new GeoBoundaries();
//                geoBoundaries.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
//                geoBoundaries.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                geoBoundaries.setServerupdatedstatus(Integer.parseInt(CommonConstants.ServerUpdatedStatus));
//
//
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                JSONObject ccData = null;
//                ArrayList dataToInsert = null;
//                try {
//                    ccData = new JSONObject(gson.toJson(geoBoundaries));
//                    dataToInsert = new ArrayList();
//                    dataToInsert.add(CommonUtils.toMap(ccData));
//                } catch (JSONException e) {
//                    Log.e(LOG_TAG, "@@@ error while converting complaint data");
//                }
//                String whereCondition = " where  Code = '" + CommonConstants.PLOT_CODE + "'";
//                dataAccessHandler.updateData(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            Log.v(LOG_TAG, "@@@ saveComplaintTypeXref data saved successfully");
//
//                        } else {
//                            Log.e(LOG_TAG, "@@@ saveComplaintTypeXref data saving failed due to " + msg);
//                        }
//
//                    }
//
//                });
//                if (null != dataToSave && !dataToSave.isEmpty()) {
//                    saveRecordIntoActivityLog(context, CommonConstants.Plot_Point_Retake_Geo_Boundaries);
//
//                    dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
//                        @Override
//                        public void execute(boolean success, String result, String msg) {
//                            if (success) {
//                                Log.v(LOG_TAG, "@@@ saveNeighBourCropsData data saved successfully");
//                                DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
//                                saveRecordIntoFarmerHistory(context,oncomplete);
//                            } else {
//                                Log.e(LOG_TAG, "@@@ saveNeighBourCropsData data saving failed due to " + msg);
//                                oncomplete.execute(false, "data saving failed for saveGeoTagData", "");
//                            }
//                        }
//                    });
//                }
//                else {
//                }
//
//            }else{
//                if (null != dataToSave && !dataToSave.isEmpty()) {
//                    saveRecordIntoActivityLog(context, CommonConstants.Plot_Point_Retake_Geo_Boundaries);
//
//                    dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_GEOBOUNDARIES, dataToSave, new ApplicationThread.OnComplete<String>() {
//                        @Override
//                        public void execute(boolean success, String result, String msg) {
//                            if (success) {
//                                Log.v(LOG_TAG, "@@@ saveNeighBourCropsData data saved successfully");
//                                DataManager.getInstance().deleteData(DataManager.PLOT_GEO_TAG);
//                                saveRecordIntoFarmerHistory(context,oncomplete);
//                            } else {
//                                Log.e(LOG_TAG, "@@@ saveNeighBourCropsData data saving failed due to " + msg);
//                                oncomplete.execute(false, "data saving failed for saveGeoTagData", "");
//                            }
//                        }
//                    });
//                } else {
//
//                }
//            }
//
//
//        }
//    }

    //to save yield data
    public static void saveYieldeData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = grtYieldData();
        Log.i("@@@Y",dataToSave.size()+" ");
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.Yield_Details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
//            dataAccessHandler.deleteRow(DatabaseKeys.TABLE_DISEASE, "PlotCode", CommonConstants.PLOT_CODE, true, oncomplete);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_YIELDASSESMENT, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ YieldAssesmentData data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.YIELD_ASSESSMENT);
                        saveWhiteData(context, oncomplete);
//                        saveHealthplantationData(context, oncomplete);

                    } else {
                        Log.e(LOG_TAG, "@@@ save YieldAssesmentData data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for YieldAssesmentData", "");
                        saveWhiteData(context, oncomplete);
//                        saveHealthplantationData(context, oncomplete);
                    }
                }
            });
        }
        else {
            saveWhiteData(context, oncomplete);
//            saveHealthplantationData(context, oncomplete);
        }

    }


    //to get yield data
    private static List grtYieldData() {
        List<YieldAssessment> yieldAssessmentList = (List<YieldAssessment>) DataManager.getInstance().getDataFromManager(DataManager.YIELD_ASSESSMENT);
        List dataToInsert = new ArrayList();
        if (null != yieldAssessmentList && !yieldAssessmentList.isEmpty()) {
            for (YieldAssessment mYieldAssemt : yieldAssessmentList) {
                mYieldAssemt.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mYieldAssemt.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mYieldAssemt.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mYieldAssemt.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mYieldAssemt.setIsActive(1);
                mYieldAssemt.setCropMaintenaceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mYieldAssemt));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mYieldAssemt data");
                }
            }
        }
        return dataToInsert;
    }

    // to save whitefly data
    public static void saveWhiteData(final Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        List dataToSave = getWhiteFlyData();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            saveRecordIntoActivityLog(context, CommonConstants.white_fly_details);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_WHITE, dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        Log.v(LOG_TAG, "@@@ WhiteField data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.WHITE_FLY);
                        saveHealthplantationData(context, oncomplete);
                    } else {
                        Log.e(LOG_TAG, "@@@ save WhiteField data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for WhiteField", "");
                        saveHealthplantationData(context, oncomplete);
                    }
                }
            });
        } else {
            saveHealthplantationData(context, oncomplete);
        }
    }

    //to get whitefly data
    private static List getWhiteFlyData() {

        Calendar calendar;

        String currentYearStr, previousYearStr, secondpreviousYearStr;

        calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate(calendar);
        final String financialYear = fiscalDate.getFinancialYearr(calendar);

        currentYearStr = financialYear + "";
        previousYearStr = Integer.parseInt(financialYear) - 1 + "";
        secondpreviousYearStr = Integer.parseInt(financialYear) - 2 + "";

        List<WhiteFlyAssessment> whiteFlyAssessmentListcurrentFY = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY);
        List<WhiteFlyAssessment> whiteFlyAssessmentListsecondPreviousFY = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY_18);
        List<WhiteFlyAssessment> whiteFlyAssessmentListPreviousFY = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY_19);
        List dataToInsert = new ArrayList();
        if (null != whiteFlyAssessmentListcurrentFY && !whiteFlyAssessmentListcurrentFY.isEmpty()) {
            for (WhiteFlyAssessment mWhiteAssemt : whiteFlyAssessmentListcurrentFY) {
                mWhiteAssemt.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setServerUpdatedStatus(0);
                mWhiteAssemt.setIsActive(1);
                mWhiteAssemt.setYear(Integer.parseInt(currentYearStr));
                mWhiteAssemt.setCropMaintenaceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mWhiteAssemt));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mYieldAssemt data");
                }
            }
        }
        if (null != whiteFlyAssessmentListsecondPreviousFY && !whiteFlyAssessmentListsecondPreviousFY.isEmpty()) {
            for (WhiteFlyAssessment mWhiteAssemt : whiteFlyAssessmentListsecondPreviousFY) {
                mWhiteAssemt.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setServerUpdatedStatus(0);
                mWhiteAssemt.setIsActive(1);
                mWhiteAssemt.setYear(Integer.parseInt(secondpreviousYearStr));
                mWhiteAssemt.setCropMaintenaceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mWhiteAssemt));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mYieldAssemt data");
                }
            }
        }
        if (null != whiteFlyAssessmentListPreviousFY && !whiteFlyAssessmentListPreviousFY.isEmpty()) {
            for (WhiteFlyAssessment mWhiteAssemt : whiteFlyAssessmentListPreviousFY) {
                mWhiteAssemt.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                mWhiteAssemt.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                mWhiteAssemt.setServerUpdatedStatus(0);
                mWhiteAssemt.setIsActive(1);
                mWhiteAssemt.setYear(Integer.parseInt(previousYearStr));
                mWhiteAssemt.setCropMaintenaceCode(CommonConstants.CROP_MAINTENANCE_HISTORY_CODE);
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mWhiteAssemt));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting mYieldAssemt data");
                }
            }
        }
        return dataToInsert;
    }

//    public static  void updateFarmerHistory(Context  context, final ApplicationThread.OnComplete oncomplete){
//        DataAccessHandler dataAccessHandler = new DataAccessHandler();
//        FarmerHistory farmerHistory = new FarmerHistory();
//        farmerHistory.setPlotcode(CommonConstants.PLOT_CODE);
//        dataAccessHandler.upDataPlotStatus(farmerHistory.getPlotcode());
//        farmerHistory.setStatustypeid(Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotStatuesId(CommonConstants.STATUS_TYPE_GEO_BOUNDARIES_TAKEN))));
//        dataAccessHandler.insertDataOld(DatabaseKeys.TABLE_FARMERHISTORY, dataToInsert, new ApplicationThread.OnComplete<String>() {
//            @Override
//            public void execute(boolean success, String result, String msg) {
//                if (success) {
//                    Log.v(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saved successfully");
////                    oncomplete.execute(true, "data saved successfully", "");
//                    if (CommonUtils.isFromConversion()) {
//
//                    } else {
//
//                    }
//                } else {
//                    Log.e(LOG_TAG, "@@@ saveRecordIntoFarmerHistory data saving failed due to " + msg);
//                    oncomplete.execute(false, "data saving failed saveRecordIntoFarmerHistory", "");
//                }
//            }
//        });
//
//    }


}

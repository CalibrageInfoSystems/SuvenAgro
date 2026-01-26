package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Config;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.FiscalDate;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.IdProofsListAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Address;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.BankDataModel;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ExistingFarmerData;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.LandlordBank;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.LandlordIdProof;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.NeighbourPlot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Plot;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotCurrentCrop;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotLandlord;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.DisplayPlotsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropModel;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.InteractiveScrollView;
import com.cis.palm360.palmgrow.SuvenAgro.utils.DateFormats;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class PlotDetailsFragment extends Fragment implements MultiEntryDialogFragment.onDataSelectedListener, GenericListItemClickListener, EditEntryDialogFragment.OnDataEditChangeListener, IdProofsListAdapter.idProofsClickListener, LandLordIdProofListAdapter.idProofsClickListener {

    public static final int NEIGHBOUR_PLOT_TYPE = 1;
    public static final int CURRENT_CROP_TYPE = 0;
    private static final int IMAGE1_REQUEST_CODE = 200;
    private static final int IMAGE2_REQUEST_CODE = 201;
    private static final int REQUEST_CAMERA_PERMISSION = 202;
    private static final String LOG_TAG = PlotDetailsFragment.class.getName();
    public static double currentCropArea = 0.0;
    public List<CropModel> currentCropList, neighbourPlotsList;
    public List<Pair<String, String>> dataPair = new ArrayList<>();
    public int statePos, districtPos, mandalPos, villagePos;
    protected boolean userStateSelect = true, userDisSelect = true, userManSelect = true, userVillageSelect = true;
    private Plot plot = new Plot();
    EditText branchNameSpin,bankDetailsSpin;
    TextView plotareatv, underpalmtv, areaofcc_tv;
    private Spinner regionSpinner, stateSpinner, districtSpinner, mandalSpinner,
            villageSpinner, properBoundryFencingSpinner, ownerShipSpin, incomeSpin,
            careTakerSpinner, idproof, landTypeSpn;
    private EditText plotareaedt, surveyNoedt, adangalNoedt,areaUnderSuitablePalmEdit;
    private EditText landmarkedt,plotaddress, careTakerNameEdit, careTakerNumberEdit,pincode;
    private android.widget.TextView stateTxt, ClusterNameTv;
    private String plotcodeStr, totalplotareaStr, surveyNoedtStr, adangalNoedtStr,
            landmarkStr, plotaddressStr, properboundaryfencingStr, ownershipStr;
    private RecyclerView currentCropRecyclerView;
    private ImageView addRowImg;
    private RecyclerView neighbourPlotRecyclerView;
    private ImageView addRowImgNbPlot, farmer_image_1, farmer_image_2;
    private Button farmerSaveBtn;
    private LinearLayout parent, area_suitable_palm, currentCropsLayout, incomeLayout;
    private com.cis.palm360.palmgrow.SuvenAgro.uihelper.InteractiveScrollView scrollView;
    private ImageView bottomScroll;
    private View rootView;
    private DataAccessHandler dataAccessHandler;
    private LinkedHashMap<String, Pair> stateDataMap = null, districtDataMap, mandalDataMap, villagesDataMap;
    private String villageCodeStr = "";
    private GenericAdapter genericAdapter;
    private SingleItemAdapter neighbourPlotAdapter;
    private int selectedPosition;
    private int selectedType;
    private TextView plotCodeTxt;
    private LinkedHashMap ownerShipMap, incomeDataMap,landTypeMap;
    private List<Pair> neighbourPlotPair = new ArrayList<>();
    private Address savedAddressData = new Address();
    private boolean isUpdateData;
    private UpdateUiListener updateUiListener;
    private FileRepository savedPictureData = null;
    private String globalpincode = "";
    private List<String> ifscCodesList; // List to store IFSC codes
    private LinkedHashMap<String, String> bankCodeDataMap;
    private LinkedHashMap<String, String> branchNameDataMap;
    private LinkedHashMap<String, String> idProofsData, mainData;
    private List<LandlordIdProof> identityProofsList;
    private LinkedHashMap<String, String> cropDataMap;
    private Calendar myCalendar = Calendar.getInstance();
    private LinearLayout landLordLayout, converstionSurveyLayout, careTakerlayout, careTakerEntrylayout, idprooflayout;
    private EditText LLHolderName, LLMobileNo, LLAccountName, LLAccountNumber, LLleaseDate, LLleaseEndDate, totalAreaHorticulture,farmer_gvt_id;
    private String LLHolderNameStr, LLMobileNoStr, LLAccountNameStr, LLIFSCcodeStr, LLAccountNumberStr, LLleaseDateStr, LLleaseEndDateStr, bankdetailnameStr, branchnameStr, aadharStr;
    private String formattedEndDate ,formattedStartDate;
    private ImageView addIDProof;
    private AutoCompleteTextView LLIFSCcode;
    private LandLordIdProofListAdapter idProofsListAdapter;
    private RecyclerView idProofsRecyclerView;
    private LandlordBank landlordBank = null;
    private PlotLandlord plotLandLord = null;
    private AlertDialog alert;
    private int leaseActiveValue;
    //private boolean leased;
    private boolean picker;
    private List<String> mandalIds, districtIds, stateIds, userVillages;
    private String plotAreaStr, totalAreaStr;
    private double plotArea = 0.0, totalArea = 0.0;
    private int checkUpdateValueBank = 0, checkUpdateValueBranch = 0;
    private boolean fromDataBase = false;
    public int financialYear;
    private String days = "";
    private String financalSubStringYear = "";
    public static String financalYrDays = "";
    private List<BankDataModel> bankDataModelList = new ArrayList<>();

    String farmerCurrentImage1;
    String farmerCurrentImage2;
    int REQUEST_CODE = 111;
    int RESULT_OK = 222;
    int bankId;
    List<FileRepository> savedPictureList = new ArrayList<>();
    private String mCurrentPhotoPath;
    ArrayList<ExistingFarmerData> data;
    private  FarmerViewDetailsAdapter farmerViewDetailsAdapter;
    private boolean isUserInteraction = false;
    public PlotDetailsFragment() {

    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Creating the View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_plot_details, container, false);

        dataAccessHandler = new DataAccessHandler(getActivity());
        CommonConstants.PLOT_CODE = DisplayPlotsFragment.plotCode;

        initUI();
        bindGeoGraphicalLocation();
       handleAddressData();

        cropDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
        genericAdapter = new GenericAdapter(getActivity());
        genericAdapter.setGenericListItemClickListener(PlotDetailsFragment.this);
        currentCropList = new ArrayList<>();
        neighbourPlotsList = new ArrayList<>();

        neighbourPlotAdapter = new SingleItemAdapter();
        neighbourPlotRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        neighbourPlotAdapter.setEditClickListener(this);
        neighbourPlotRecyclerView.setAdapter(neighbourPlotAdapter);
        plot = (Plot) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS);


        if ((CommonUtils.isFromFollowUp() || CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance())) {
            Log.v(LOG_TAG, "#### here plot code already generated and saved in database");
            fromDataBase = true;
        }

        if (fromDataBase && plot == null) {
            Log.v(LOG_TAG, "@@@@@ getting data from database");
            plot = (Plot) dataAccessHandler.getSelectedPlotData(Queries.getInstance().getSelectedPlot(CommonConstants.PLOT_CODE), 0);
            Address selectedPlotAddress = (Address) dataAccessHandler.getSelectedFarmerAddress(Queries.getInstance().getSelectedPlotAddress(plot.getAddesscode()), 0);
            List<PlotCurrentCrop> selectedPlotCurrentCrop = (List<PlotCurrentCrop>) dataAccessHandler.getSelectedPlotCurrentCropData(Queries.getInstance().getSelectedPlotCurrentCrop(plot.getCode()), 1);
            List<NeighbourPlot> selectedNeighbourPlotData = (List<NeighbourPlot>) dataAccessHandler.getSelectedNeighbourPlotData(Queries.getInstance().getSelectedNeighbourPlot(plot.getCode()), 1);
            if (null != plot) {
                isUpdateData = true;
                DataManager.getInstance().addData(DataManager.PLOT_DETAILS, plot);
                DataManager.getInstance().addData(DataManager.PLOT_ADDRESS_DETAILS, selectedPlotAddress);
                DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA, convertToCurrentCropModel(selectedPlotCurrentCrop));
                DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA, convertToCropModel(selectedNeighbourPlotData));
                DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA_PAIR, dataPair);
                DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA_PAIR, neighbourPlotPair);
                handleViewEnableAndDisable();
                bindData();
            } else {
                UiUtils.showCustomToastMessage("Error While Getting Local Plot Data", getActivity(), 1);
            }
        } else if (plot != null) {
            isUpdateData = true;
            bindData();
            landlordBank = (LandlordBank) DataManager.getInstance().getDataFromManager(DataManager.LANDLORD_BANK_DATA);
            if (null == landlordBank) {
                landlordBank = (LandlordBank) dataAccessHandler.getLandLordBankData(Queries.getInstance().queryLandLordBankData(CommonConstants.PLOT_CODE), 0);
            }
            if (null != landlordBank) {
                bindLandLordBankData();
            }

            plotLandLord = (PlotLandlord) DataManager.getInstance().getDataFromManager(DataManager.LANDLORD_LEASED_DATA);
            if (null == plotLandLord) {
                plotLandLord = (PlotLandlord) dataAccessHandler.getPlotLandLordData(Queries.getInstance().queryPlotLandlordData(CommonConstants.PLOT_CODE), 0);
            }
            //plotLandLord = (PlotLandlord) dataAccessHandler.getPlotLandLordData(Queries.getInstance().queryPlotLandlordData(CommonConstants.PLOT_CODE), 0);
            if (plotLandLord != null) {
                bindLandLordPersonalData();
            }
        }

        if (fromDataBase) {
            handleViewEnableAndDisable();
        }

       // if (CommonUtils.isFromCropMaintenance() && landlordBank == null) {
            landlordBank = (LandlordBank) dataAccessHandler.getLandLordBankData(Queries.getInstance().queryLandLordBankData(CommonConstants.PLOT_CODE), 0);
            plotLandLord = (PlotLandlord) dataAccessHandler.getPlotLandLordData(Queries.getInstance().queryPlotLandlordData(CommonConstants.PLOT_CODE), 0);
      //  }
        if (plot != null && plot.getCode() != null) {
            savedPictureList = dataAccessHandler.getSelectedppbRepository(
                    Queries.getInstance().getSelectedppbRepositoryQuery(plot.getCode(), 844)
            );
            Log.e("PlotDetailsFragment", savedPictureList.size()+"Plot Code" + plot.getCode());
        } else {
            Log.e("PlotDetailsFragment", "Plot or Plot Code is NULL");
        }
      //  List<FileRepository> savedPictureList = dataAccessHandler.getSelectedppbRepository(Queries.getInstance().getSelectedppbRepositoryQuery(plot.getCode(), 844));

        if (savedPictureList != null && !savedPictureList.isEmpty()) {
            for (int i = 0; i < savedPictureList.size(); i++) {
                FileRepository savedPictureData = savedPictureList.get(i);
                String path = savedPictureData.getPicturelocation();

                final String imageUrl = CommonUtils.getImageUrl(savedPictureData);

                if (i == 0) {
                    // Bind first image
                    loadImage(imageUrl, path, farmer_image_1);
                } else if (i == 1) {
                    // Bind second image
                    loadImage(imageUrl, path, farmer_image_2);
                }
            }
        }

        return rootView;
    }

    private void loadImage(final String imageUrl, final String path, final ImageView targetImageView) {
        Picasso.get()
                .load(imageUrl)
                .into(targetImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Success
                    }

                    @Override
                    public void onError(Exception e) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            Glide.with(getActivity())
                                    .load(path)
                                    .into(targetImageView);
                        }
                    }
                });
    }




    //Enable/Disable the fields
    private void handleViewEnableAndDisable() {

        plotareaedt.setEnabled(false);
        plotareaedt.setCursorVisible(false);
        plotareaedt.setClickable(false);
        plotareaedt.setFocusable(false);
//        landmarkedt.setEnabled(false);
//        landmarkedt.setFocusable(false);
//        plotaddress.setEnabled(false);
//        plotaddress.setFocusable(false);

//        LLIFSCcode.setEnabled(false);
//        LLIFSCcode.setFocusable(false);

    }

    //Binding Data to Spinners & fields
    @SuppressLint("SetTextI18n")
    public void bindData() {
        if (fromDataBase) {
            stateSpinner.setEnabled(false);
            stateSpinner.setFocusable(false);
            districtSpinner.setEnabled(false);
            districtSpinner.setFocusable(false);
            mandalSpinner.setEnabled(false);
            mandalSpinner.setFocusable(false);
            villageSpinner.setEnabled(false);
            villageSpinner.setFocusable(false);
        }

        savedAddressData = (Address) DataManager.getInstance().getDataFromManager(DataManager.PLOT_ADDRESS_DETAILS);

        if (DataManager.getInstance().getDataFromManager(DataManager.PPB1) != null) {
            farmerCurrentImage1 = (String) DataManager.getInstance().getDataFromManager(DataManager.PPB1);
            setImageFromFilePath(farmerCurrentImage1, farmer_image_1);
        }

        if (DataManager.getInstance().getDataFromManager(DataManager.PPB2) != null) {
            farmerCurrentImage2 = (String) DataManager.getInstance().getDataFromManager(DataManager.PPB2);
            setImageFromFilePath(farmerCurrentImage2, farmer_image_2);
        }


        if (isUpdateData && savedAddressData == null) {
            savedAddressData = (Address) dataAccessHandler.getSelectedFarmerAddress(Queries.getInstance().getSelectedPlotAddress(plot.getAddesscode()), 0);
            DataManager.getInstance().addData(DataManager.PLOT_ADDRESS_DETAILS, savedAddressData);
            List<PlotCurrentCrop> selectedPlotCurrentCrop = (List<PlotCurrentCrop>) dataAccessHandler.getSelectedPlotCurrentCropData(Queries.getInstance().getSelectedPlotCurrentCrop(plot.getCode()), 1);
            List<NeighbourPlot> selectedNeighbourPlotData = (List<NeighbourPlot>) dataAccessHandler.getSelectedNeighbourPlotData(Queries.getInstance().getSelectedNeighbourPlot(plot.getCode()), 1);
            DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA, convertToCurrentCropModel(selectedPlotCurrentCrop));
            DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA, convertToCropModel(selectedNeighbourPlotData));
            DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA_PAIR, dataPair);
            DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA_PAIR, neighbourPlotPair);
        }

        if (null != savedAddressData) {
            CommonConstants.statePlotId = String.valueOf(savedAddressData.getStateid());
            CommonConstants.districtIdPlot = String.valueOf(savedAddressData.getDistictid());
            CommonConstants.mandalIdPlot = String.valueOf(savedAddressData.getMandalid());
            CommonConstants.villageIdPlot = String.valueOf(savedAddressData.getVillageid());
            Log.v("bindData", "stateId=" + CommonConstants.statePlotId +
                    " districtId=" + CommonConstants.districtIdPlot +
                    " mandalId=" + CommonConstants.mandalIdPlot +
                    " villageId=" + CommonConstants.villageIdPlot);
            districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserDistricts(TextUtils.join(",", districtIds), CommonConstants.statePlotId));
            Log.v("bindData", "districtDataMap keys: " + districtDataMap.keySet());

            mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserMandals(TextUtils.join(",", mandalIds), (TextUtils.join(",", districtIds))));
            villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserVillages(TextUtils.join(",", userVillages), (TextUtils.join(",", mandalIds))));

            statePos = CommonUtils.getIndex(stateDataMap.keySet(), String.valueOf(savedAddressData.getStateid())) + 1;
            districtPos = CommonUtils.getIndex(districtDataMap.keySet(), String.valueOf(savedAddressData.getDistictid())) + 1;
            mandalPos = CommonUtils.getIndex(mandalDataMap.keySet(), String.valueOf(savedAddressData.getMandalid())) + 1;
            villagePos = CommonUtils.getIndex(villagesDataMap.keySet(), String.valueOf(savedAddressData.getVillageid())) + 1;
            stateSpinner.setSelection(statePos);

            ArrayAdapter<String> spinnerDistrictArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
            spinnerDistrictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(spinnerDistrictArrayAdapter);
            CommonConstants.stateName = stateSpinner.getSelectedItem().toString();
            Log.v("@@@state",""+CommonConstants.statePlotId+" "+stateDataMap.size());
            Pair statePair = stateDataMap.get(CommonConstants.statePlotId);
            CommonConstants.stateCodePlot = statePair.first.toString();
            districtSpinner.setSelection(districtPos);

            Log.v(LOG_TAG, "@@@ Selected districtIdPlot " + CommonConstants.districtIdPlot);
            mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserMandals(TextUtils.join(",", mandalIds), TextUtils.join(",", districtIds)));
            ArrayAdapter<String> spinnerMandalArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
            spinnerMandalArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mandalSpinner.setAdapter(spinnerMandalArrayAdapter);
            CommonConstants.districtName = districtSpinner.getSelectedItem().toString();
            Pair districtPair = districtDataMap.get(CommonConstants.districtIdPlot);
            if (districtPair != null) {
                CommonConstants.districtCodePlot = districtPair.first.toString();
                Log.v("bindData", "District found: code=" + CommonConstants.districtCodePlot);
            } else {
                Log.e("bindData", "District pair is NULL for id " + CommonConstants.districtIdPlot);
            }
            CommonConstants.districtCodePlot = districtPair.first.toString();
            mandalSpinner.setSelection(mandalPos);

            Log.v(LOG_TAG, "@@@ Selected mandalIdPlot " + CommonConstants.mandalIdPlot);
            CommonConstants.mandalName = mandalSpinner.getSelectedItem().toString();
            Pair mandalPair = mandalDataMap.get(CommonConstants.mandalIdPlot);
            CommonConstants.mandalCodePlot = mandalPair.first.toString();
            villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserVillages(TextUtils.join(",", userVillages), (TextUtils.join(",", mandalIds))));

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            villageSpinner.setAdapter(spinnerArrayAdapter);
            villageSpinner.setSelection(villagePos);

            villageCodeStr = villageSpinner.getSelectedItem().toString();
            Pair villagePair = villagesDataMap.get(CommonConstants.villageIdPlot);
            CommonConstants.villageCodePlot = villagePair.first.toString();

            CommonConstants.villageName = villageSpinner.getSelectedItem().toString();
            if (!CommonUtils.isFromFollowUp() && !CommonUtils.isFromConversion() && !CommonUtils.isFromCropMaintenance()) {
                CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(financalYrDays), financalYrDays);
            }
            setPinCode();
            String ClusterName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getClusterName(CommonConstants.villageIdPlot));
            ClusterNameTv.setText(" " + ClusterName);
            plotCodeTxt.setText(plot.getCode());
            plotareaedt.setText(String.valueOf(plot.getTotalplotarea()));
            CommonConstants.TotalPlot_Area = String.valueOf(plot.getTotalplotarea());
            areaUnderSuitablePalmEdit.setText(String.valueOf(plot.getSuitablePalmOilArea()));
            pincode.setText("" + savedAddressData.getPincode());
            landmarkedt.setText("" + savedAddressData.getLandmark());
            plotaddress.setText("" + savedAddressData.getAddressline1());
            if (null != plot.getIsplothandledbycaretaker()) {
                careTakerSpinner.setSelection(plot.getIsplothandledbycaretaker() == 1 ? 1 : 2);
                if (plot.getIsplothandledbycaretaker() == 1) {
                    careTakerNumberEdit.setText(plot.getCaretakercontactnumber());
                    careTakerNameEdit.setText(plot.getCaretakername());
                }
            } else {
                String plotcaretakercode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().gePlotCareTakerfromDB(CommonConstants.PLOT_CODE));
                if (plotcaretakercode == null || plotcaretakercode.equalsIgnoreCase("null") || plotcaretakercode.isEmpty()) {

                } else {
                    int caretakercode = Integer.parseInt(plotcaretakercode);
                    careTakerSpinner.setSelection(caretakercode == 1 ? 1 : 2);
                    if (caretakercode == 1) {
                        careTakerNumberEdit.setText(plot.getCaretakercontactnumber());
                        careTakerNameEdit.setText(plot.getCaretakername());
                    }
                }
                //   }
            }
            if (plot.getGovtPlotCode() != null && !plot.getGovtPlotCode().equalsIgnoreCase("null")) {
                farmer_gvt_id.setText(plot.getGovtPlotCode());
            }else {
                farmer_gvt_id.setText(" ");
            }
            if (CommonUtils.isFromCropMaintenance() && TextUtils.isEmpty(plotaddress.getText().toString())) {
                plotaddress.setEnabled(true);
                plotaddress.setFocusable(true);
                plotaddress.setClickable(true);
                plotaddress.setCursorVisible(true);
            }
            if (CommonUtils.isFromCropMaintenance() && TextUtils.isEmpty(savedAddressData.getLandmark())) {
                landmarkedt.setEnabled(true);
                landmarkedt.setFocusable(true);
                landmarkedt.setClickable(true);
                landmarkedt.setCursorVisible(true);
            }
            if (null != plot.getIsBoundryFencing()) {
                properBoundryFencingSpinner.setSelection(plot.getIsBoundryFencing() == 1 ? 1 : 2);
            }
            incomeSpin.setSelection(CommonUtils.getIndex(incomeDataMap.keySet(), String.valueOf(plot.getCropincometypeid())) + 1);
            ownerShipSpin.setSelection(CommonUtils.getIndex(ownerShipMap.keySet(), String.valueOf(plot.getPlotownershiptypeid())) + 1);
            landTypeSpn.setSelection(CommonUtils.getIndex(landTypeMap.keySet(), String.valueOf(plot.getLandTypeId())) + 1);
            totalAreaHorticulture.setText(""+plot.getTotalAreaUnderHorticulture());
//            if (plot.getGovtPlotCode() != null) {
//                farmer_gvt_id.setText(plot.getGovtPlotCode());
//            }

            dataPair = (List<Pair<String, String>>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_CURRENT_CROPS_DATA_PAIR);
            neighbourPlotPair = (List<Pair>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA_PAIR);

            if (dataPair != null && dataPair.size() > 0) {
                genericAdapter.updateAdapter(dataPair);
                currentCropRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                currentCropRecyclerView.setAdapter(genericAdapter);
                currentCropList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_CURRENT_CROPS_DATA);
            }

            if (neighbourPlotPair != null && neighbourPlotPair.size() > 0) {
                neighbourPlotAdapter.updateAdapter(neighbourPlotPair);
                neighbourPlotsList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA);
            }

       //     if (CommonUtils.isFromCropMaintenance() || CommonUtils.isFromConversion()) {
                if (TextUtils.isEmpty(plot.getSurveynumber()) || plot.getSurveynumber().equalsIgnoreCase("null")) {
                    surveyNoedt.setText("");
                } else {
                    surveyNoedt.setText("" + plot.getSurveynumber());
                }

                if (TextUtils.isEmpty(plot.getAdangalnumber()) || plot.getAdangalnumber().equalsIgnoreCase("null")) {
                    adangalNoedt.setText("");
                } else {
                    adangalNoedt.setText("" + plot.getAdangalnumber());
                }

         //   }



         //   if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {

        }
        else {
            UiUtils.showCustomToastMessage("Error While Getting Address Details", getActivity(), 1);
        }
    }


    private void checkCameraPermissionAndOpenCamera(int requestCode) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera(requestCode);
        }
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the data from the intent
            String result = data.getStringExtra("result_key");
            Log.d("calculatedPlotArea", result + "");
            plotareaedt.setText(result);
            // Do something with the data
            // ...
        }

        if (requestCode == IMAGE1_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    farmerCurrentImage1 = saveBitmapAndReturnPath(imageBitmap);
                    setImageFromFilePath(farmerCurrentImage1, farmer_image_1);
                }
            }
        }

        if (requestCode == IMAGE2_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    farmerCurrentImage2 = saveBitmapAndReturnPath(imageBitmap);
                    setImageFromFilePath(farmerCurrentImage2, farmer_image_2);
                }
            }
        }
    }

    private String saveBitmapAndReturnPath(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {
            // Create a unique filename
            String fileName = "ppb" + System.currentTimeMillis() + ".jpg";

            // Save to app's cache directory
            File file = new File(requireContext().getCacheDir(), fileName);
            FileOutputStream out = new FileOutputStream(file);

            // Compress and save bitmap to file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setImageFromFilePath(String filePath, ImageView imageView) {
        if (filePath != null && imageView != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e("ImageLoadError", "Failed to decode image from path: " + filePath);
            }
        } else {
            Log.e("ImageLoadError", "filePath or imageView is null");
        }
    }


    public void setImage(Bitmap bitmap, ImageView imageView) {
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }


    //Initializing the UI
    @SuppressLint("ClickableViewAccessibility")
    public void initUI() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(activity.getResources().getString(R.string.plot_detailss));
        this.bottomScroll = rootView.findViewById(R.id.bottomScroll);
        this.scrollView = rootView.findViewById(R.id.scrollView);
        this.parent = rootView.findViewById(R.id.parent);
        this.farmerSaveBtn = rootView.findViewById(R.id.plotSaveBtn);
        this.addRowImgNbPlot = rootView.findViewById(R.id.addRowImgNbPlot);
        this.farmer_image_1 = rootView.findViewById(R.id.farmer_image_1);
        this.farmer_image_2 = rootView.findViewById(R.id.farmer_image_2);
        this.neighbourPlotRecyclerView = rootView.findViewById(R.id.neighbourPlotRecyclerView);
        this.addRowImg = rootView.findViewById(R.id.addRowImg);
        this.currentCropRecyclerView = rootView.findViewById(R.id.currentCropRecyclerView);
        this.stateTxt = rootView.findViewById(R.id.stateTxt);
        this.pincode = rootView.findViewById(R.id.pincode);
        this.plotaddress = rootView.findViewById(R.id.plot_address);
        this.landmarkedt = rootView.findViewById(R.id.land_mark_edt);
        this.areaUnderSuitablePalmEdit = rootView.findViewById(R.id.area_suitable_palm_edit);
        this.plotareaedt = rootView.findViewById(R.id.plot_area_edt);
        this.plotareatv = rootView.findViewById(R.id.plotareatv);
        this.areaofcc_tv = rootView.findViewById(R.id.areaofcc_tv);
        this.underpalmtv = rootView.findViewById(R.id.underpalmtv);
        this.villageSpinner = rootView.findViewById(R.id.villageName);
        this.mandalSpinner = rootView.findViewById(R.id.mandalName);
        this.stateSpinner = rootView.findViewById(R.id.statespin);
        this.regionSpinner = rootView.findViewById(R.id.regionSpin);
        this.districtSpinner = rootView.findViewById(R.id.districtSpin);
//        farmer_image_1 = rootView.findViewById(R.id.farmer_image_1);
//        farmer_image_2 = rootView.findViewById(R.id.farmer_image_2);
        plotCodeTxt = rootView.findViewById(R.id.plotCodeTxt);
        properBoundryFencingSpinner = rootView.findViewById(R.id.ipbfSpinner);
        ownerShipSpin = rootView.findViewById(R.id.ownerShipSpinner);
        incomeSpin = rootView.findViewById(R.id.incomefromCurrentCropsSpinner);
        careTakerSpinner = rootView.findViewById(R.id.careTakerSpinner);
        surveyNoedt = rootView.findViewById(R.id.Survey_no_edt);
        adangalNoedt = rootView.findViewById(R.id.adangal_edt);
        ClusterNameTv = rootView.findViewById(R.id.Cluster_Name);
        //LandLord Intilize
        LLHolderName = rootView.findViewById(R.id.landNameEdt);
        LLMobileNo = rootView.findViewById(R.id.landContactEdt);
        LLAccountName = rootView.findViewById(R.id.landlord_account_holder_name);
        LLAccountNumber = rootView.findViewById(R.id.landlord_account_number);
        LLIFSCcode = rootView.findViewById(R.id.landlord_ifsc_code);
        LLleaseDate = rootView.findViewById(R.id.lease_datefrom_edt);
        LLleaseEndDate = rootView.findViewById(R.id.lease_dateto_edt);
        bankDetailsSpin = rootView.findViewById(R.id.bankCodeSpin);
        branchNameSpin = rootView.findViewById(R.id.branchSpin);
        addIDProof = rootView.findViewById(R.id.addRowlandLordImg);
        idProofsRecyclerView = rootView.findViewById(R.id.idProofsRecyclerView);
        careTakerNameEdit = rootView.findViewById(R.id.caretakernameEdt);
        careTakerNumberEdit = rootView.findViewById(R.id.caretakercontactnumlEdt);

        landLordLayout = rootView.findViewById(R.id.landlord_details_screen);
        careTakerlayout = rootView.findViewById(R.id.careTakerlayout);
        careTakerEntrylayout = rootView.findViewById(R.id.careTakerEntrylayout);
        converstionSurveyLayout = rootView.findViewById(R.id.converstionSurveyLayout);
        area_suitable_palm = rootView.findViewById(R.id.area_suitable_palm);
        incomeLayout = rootView.findViewById(R.id.incomeLayout);
        currentCropsLayout = rootView.findViewById(R.id.currentCropsLayout);
        totalAreaHorticulture = rootView.findViewById(R.id.totalAreaHorticulture);
        farmer_gvt_id = rootView.findViewById(R.id.farmer_gvt_id);
        landTypeSpn = rootView.findViewById(R.id.landTypeSpn);

        // Finical Year
        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();

        plotareatv.setText(getString(R.string.plot_area, Config.UOM));
        underpalmtv.setText(getString(R.string.under_palm, Config.UOM));
        areaofcc_tv.setText(getString(R.string.area_of_current_crop, Config.UOM));


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
            financalYrDays = String.valueOf(financialYear).substring(2, 4) + days;


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (CommonUtils.isFromConversion()) {
            area_suitable_palm.setVisibility(View.GONE);
        } else {
            area_suitable_palm.setVisibility(View.GONE);
        }

        if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {
            incomeLayout.setVisibility(View.GONE);
            currentCropsLayout.setVisibility(View.GONE);
        } else {
            incomeLayout.setVisibility(View.VISIBLE);
            currentCropsLayout.setVisibility(View.VISIBLE);
            areaUnderSuitablePalmEdit.setVisibility(View.GONE);
        }

        if (CommonUtils.isFromFollowUp()) {
            currentCropsLayout.setVisibility(View.GONE);
        }

        //IDProof Details
        idProofsData = mainData = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("12"));

        // Try to get from DataManager
  identityProofsList = (List<LandlordIdProof>) DataManager.getInstance()
          .getDataFromManager(DataManager.LANDLORD_IDPROOFS_DATA);

// If null, fetch from DB


        if (identityProofsList == null) {
            identityProofsList = (List<LandlordIdProof>) dataAccessHandler.getLandLordIDProofsData(
                    Queries.getInstance().queryLandLordIdproofData(CommonConstants.PLOT_CODE), 1
            );
        }

// Ensure list is not null
        if (identityProofsList == null) {
            identityProofsList = new ArrayList<>();
        }

// Show/Hide RecyclerView
        idProofsRecyclerView.setVisibility(identityProofsList.isEmpty() ? View.GONE : View.VISIBLE);

// Setup RecyclerView
        idProofsRecyclerView.setHasFixedSize(true);
        idProofsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        );

        idProofsListAdapter = new LandLordIdProofListAdapter(getActivity(), identityProofsList, mainData);
        idProofsListAdapter.setIdProofsClickListener(PlotDetailsFragment.this);
        idProofsRecyclerView.setAdapter(idProofsListAdapter);

   /*     if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {
            converstionSurveyLayout.setVisibility(View.VISIBLE);
            careTakerEntrylayout.setVisibility(View.VISIBLE);
        } else {
            converstionSurveyLayout.setVisibility(View.GONE);
        }*/

        //Binding Data to Spinner & On Click Listeners
        String[] boundaryFencingArray = getActivity().getResources().getStringArray(R.array.yesOrNo_values);
        List<String> boundaryList = Arrays.asList(boundaryFencingArray);
        ArrayAdapter<String> spinnerBoundryArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, boundaryList);
        spinnerBoundryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        properBoundryFencingSpinner.setAdapter(spinnerBoundryArrayAdapter);

        String[] careTakerArray = getActivity().getResources().getStringArray(R.array.yesOrNo_values);
        List<String> careTakerList = Arrays.asList(careTakerArray);
        ArrayAdapter<String> spinnerCareTakerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, careTakerList);
        spinnerCareTakerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        careTakerSpinner.setAdapter(spinnerCareTakerArrayAdapter);

        scrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {


            }
        });

/*        plotareaedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("plotareaedt", "Clicked");
                // Log.d("PlotCodegenerated", CommonConstants.PLOT_CODE);
                if (TextUtils.isEmpty(CommonConstants.PLOT_CODE)){
                    Log.d("PlotCode", "isempty");
                    Log.d("PlotCodegenerated", CommonConstants.PLOT_CODE);
                    UiUtils.showCustomToastMessage("Please Select above Location details to Take GeoBoundaries", getActivity(), 0);
                }else{
                    Log.d("PlotCode", "isnotempty");
                    Log.d("PlotCodegenerated", CommonConstants.PLOT_CODE);
                    Intent gpsarea = new Intent(getContext(), PreViewAreaCalScreen.class);
                    startActivityForResult(gpsarea, REQUEST_CODE);
                }

            }
        });*/

        ownerShipSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ownerShipSpin.getSelectedItem().toString().equalsIgnoreCase("Leased")) {
                    // if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {
                    leaseActiveValue = 1;
                    CommonConstants.leased = true;

                    landLordLayout.setVisibility(View.VISIBLE);
                    // }

                } else {
                    idProofsListAdapter.clearData();
                    idProofsListAdapter.notifyDataSetChanged();
                    leaseActiveValue = 0;
                    LLHolderName.setText("");
                    LLIFSCcode.setText("");
                    LLAccountName.setText("");
                    LLAccountNumber.setText("");
                    LLHolderName.setText("");
                    LLMobileNo.setText("");
                    LLleaseDate.setText("");
                    LLleaseEndDate.setText("");
                    landLordLayout.setVisibility(View.GONE);
                    CommonConstants.leased = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LLMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.matches("^$|^[6-9][0-9]*$")) {
                    LLMobileNo.setText("");
                    UiUtils.showCustomToastMessage("Enter a Valid Mobile Number", getActivity(), 1);
                    return;
                }
            }
        });

        careTakerSpinner.setOnTouchListener((v, event) -> {
            isUserInteraction = true;
            return false; // Let the spinner behave normally
        });
        careTakerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (careTakerSpinner.getSelectedItem().toString().equalsIgnoreCase("Yes")) {
                    careTakerlayout.setVisibility(View.VISIBLE);
                    if (isUserInteraction) {
                        // 👉 Clear only when the user changes to "Yes"
                        careTakerNameEdit.setText("");
                        careTakerNumberEdit.setText("");
                    } else {
                        // 👉 On initial load, just bind existing caretaker data
                        if (plot != null) {
                            bindCareTakerData();
                        }
                    }
                } else {
                    careTakerlayout.setVisibility(View.GONE);
                    if (isUserInteraction) {
                        // 👉 If user selects "No", clear the fields
                        careTakerNameEdit.setText("");
                        careTakerNumberEdit.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        careTakerNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.matches("^$|^[6-9][0-9]*$")) {
                    careTakerNumberEdit.setText("");
                    UiUtils.showCustomToastMessage("Enter a Valid Mobile Number", getActivity(), 1);
                    return;
                }
            }
        });

        addRowImg.setOnClickListener(v -> {
            MultiEntryDialogFragment multiEntryDialogFragment = new MultiEntryDialogFragment();
            multiEntryDialogFragment.setOnDataSelectedListener(PlotDetailsFragment.this);
            Bundle inpuptBundle = new Bundle();
            inpuptBundle.putInt("type", CURRENT_CROP_TYPE);
            multiEntryDialogFragment.setArguments(inpuptBundle);
            String backStateName = multiEntryDialogFragment.getClass().getName();
            FragmentManager mFragmentManager = getChildFragmentManager();
            multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
        });

        addRowImgNbPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiEntryDialogFragment multiEntryDialogFragment = new MultiEntryDialogFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(PlotDetailsFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", NEIGHBOUR_PLOT_TYPE);
                inpuptBundle.putInt("neighbourPlotCount", neighbourPlotPair.size());
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
            }
        });

        farmer_image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkCameraPermissionAndOpenCamera(IMAGE1_REQUEST_CODE);
            }
        });

        farmer_image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkCameraPermissionAndOpenCamera(IMAGE2_REQUEST_CODE);
            }
        });

//        farmer_image_1.setOnClickListener(v -> {
//
//            UiUtils.showCustomToastMessage("Called", getActivity(), 1);
//            checkCameraPermissionAndOpenCamera(IMAGE1_REQUEST_CODE);
//        });
//
//        farmer_image_2.setOnClickListener(v -> {
//            checkCameraPermissionAndOpenCamera(IMAGE2_REQUEST_CODE);
//        });

        farmerSaveBtn.setOnClickListener(v -> {

            if (CommonUtils.isFromCropMaintenance()){
                CommonConstants.isplotupdated = true;
            }else{
                CommonConstants.isplotupdated = false;
            }

            if (isValid()) {
                CommonUtils.hideSoftKeyboard(getActivity());
                try {
                    saveState();
//                        Toast.makeText(getActivity(),"TotalPlotArea"+CommonConstants.TotalPlot_Area,Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        savedAddressData = (Address) DataManager.getInstance().getDataFromManager(DataManager.PLOT_ADDRESS_DETAILS);
        if (savedAddressData == null) {
            savedAddressData = new Address();
        }

        ifscCodesList = new ArrayList<>();
        ifscCodesList = dataAccessHandler.getSingleListData(Queries.getInstance().getIFSClist());
        // Create the adapter for the AutoCompleteTextView


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, ifscCodesList);

        LLIFSCcode.setAdapter(adapter);


        // Optional: Set a listener to perform actions when an item is selected from the dropdown
        LLIFSCcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                LLIFSCcodeStr= (String) parent.getItemAtPosition(position);
                Log.e("=========>ifsccodeStr",LLIFSCcodeStr+"");
                // Perform any action with the selected IFSC code here

                bankDataModelList = dataAccessHandler.getbankData(Queries.getInstance().getbankdetails(LLIFSCcodeStr));

                if (bankDataModelList.size() == 0) {
                    //Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_account_number_label), Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Please Enter Valid IFSC Code", getActivity(), 1);
                    bankDetailsSpin.setText("Bank Not Available");
                    branchNameSpin.setText("Branch Not Available");
                } else {
                    Log.d("Roja", "=====> bank name:" + bankDataModelList.get(0).getbankname() + "===" + bankDataModelList.get(0).getIFSCCode());

                    for (BankDataModel bank : bankDataModelList
                    ) {

                        bankDetailsSpin.setText(bankDataModelList.get(0).getbankname());
                        branchNameSpin.setText(bankDataModelList.get(0).getBranchName());
                        bankId = bank.getBankTypeId();
                        Log.d("Roja", "=====> bank name:" + bank.getBankTypeId() + "===" + bank.getIFSCCode());
                    }

                }

//

            }
        });


        //BankDetails

        LLIFSCcode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s.toString().length() >= 5) {
                    LLIFSCcodeStr = LLIFSCcode.getText().toString().toUpperCase();

                    bankDataModelList = dataAccessHandler.getbankData(Queries.getInstance().getbankdetails(LLIFSCcodeStr));

                    if (bankDataModelList.size() == 0) {
                        //Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_account_number_label), Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Please Enter Valid IFSC Code", getActivity(), 1);
                        bankDetailsSpin.setText("Bank Not Available");
                        branchNameSpin.setText("Branch Not Available");
                    } else {
                        com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log.d("Roja", "=====> bank name:" + bankDataModelList.get(0).getbankname() + "===" + bankDataModelList.get(0).getIFSCCode());

                        for (BankDataModel bank : bankDataModelList
                        ) {

                            bankDetailsSpin.setText(bankDataModelList.get(0).getbankname());
                            branchNameSpin.setText(bankDataModelList.get(0).getBranchName());
                            bankId = bank.getBankTypeId();
                            Log.d("Roja", "=====> bank name:" + bank.getBankTypeId() + "===" + bank.getIFSCCode());
                        }

                    }
                }
                else{
                    bankDetailsSpin.setText(" ");
                    branchNameSpin.setText(" ");
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
//        bankCodeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("3"));
//        ArrayAdapter<String> bankCodeSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(bankCodeDataMap, "BankName"));
//        bankCodeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        bankDetailsSpin.setAdapter(bankCodeSpinnerArrayAdapter);
//
//        bankDetailsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (bankCodeDataMap != null && bankCodeDataMap.size() > 0 && bankDetailsSpin.getSelectedItemPosition() != 0 && checkUpdateValueBank == 0) {
//                    CommonConstants.bankTypeId = bankCodeDataMap.keySet().toArray(new String[bankCodeDataMap.size()])[i - 1];
//                    branchNameDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getBranchDetails(CommonConstants.bankTypeId));
//                    ArrayAdapter<String> branchNameSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(branchNameDataMap, "BranchName"));
//                    branchNameSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    branchNameSpin.setAdapter(branchNameSpinnerArrayAdapter);
//                    bankdetailnameStr = bankDetailsSpin.getSelectedItem().toString();
//                } else {
//                    checkUpdateValueBank = 0;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        branchNameSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (branchNameDataMap != null && branchNameDataMap.size() > 0 && branchNameSpin.getSelectedItemPosition() != 0 && checkUpdateValueBranch == 0) {
//                    CommonConstants.branchTypeId = branchNameDataMap.keySet().toArray(new String[branchNameDataMap.size()])[i - 1];
//                    LLIFSCcode.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getIfscCode(CommonConstants.branchTypeId)));
//                    branchnameStr = branchNameSpin.getSelectedItem().toString();
//                } else {
//                    checkUpdateValueBranch = 0;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });



        addIDProof.setOnClickListener(v -> displayIdProofsDialog());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        LLleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                picker = true;
            }
        });

        LLleaseEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker = false;
            }
        });

        /*LLleaseEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                );

                // ✅ Normalize to today's midnight
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // ✅ Move to tomorrow
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                // ✅ Set tomorrow as min date
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                datePickerDialog.show();
                picker = false;
            }
        });*/

        plotareaedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                plotAreaStr = plotareaedt.getText().toString();
                if (TextUtils.isEmpty(plotAreaStr) || !validateDoubles() && (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance())) {
                    areaUnderSuitablePalmEdit.setText("");
                }
            }
        });

        areaUnderSuitablePalmEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                totalAreaStr = areaUnderSuitablePalmEdit.getText().toString();
                if (!validateDoubles() && (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance())) {
                    if (!TextUtils.isEmpty(plotAreaStr)) {
                        areaUnderSuitablePalmEdit.setText(totalAreaStr.substring(0, totalAreaStr.length() - 1));
                        areaUnderSuitablePalmEdit.setSelection(totalAreaStr.length());
                    }
                }
            }
        });


        landTypeMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtComplaintsTypeData("53"));
        ArrayAdapter<String> landTypeSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(landTypeMap, "Land Type"));
        landTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landTypeSpn.setAdapter(landTypeSpinnerAdapter);

    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            // Get the data from the intent
//            String result = data.getStringExtra("result_key");
//            Log.d("calculatedPlotArea", result + "");
//            plotareaedt.setText(result);
//            // Do something with the data
//            // ...
//        }
//    }

    //Binding the Landlord fields.
    private void bindLandLordPersonalData() {
        Log.e("==========>2554",plotLandLord.getLandlordname());
        LLHolderName.setText(plotLandLord.getLandlordname());
        LLMobileNo.setText(plotLandLord.getLandlordcontactnumber());
        LLleaseDate.setText(formatDate(plotLandLord.getLeasestartdate()));
        LLleaseEndDate.setText(formatDate(plotLandLord.getLeaseenddate()));
    }
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr; // fallback to original if parsing fails
        }
    }

    private void bindCareTakerData(){
//        careTakerNameEdit.setText(plot.getCaretakername());
        String caretakerName = plot.getCaretakername();

        if (caretakerName != null && !"null".equalsIgnoreCase(caretakerName.trim()) && !caretakerName.trim().isEmpty()) {
            careTakerNameEdit.setText(caretakerName.trim());
        } else {
            careTakerNameEdit.setText("");
        }
        careTakerNumberEdit.setText(plot.getCaretakercontactnumber());
    }

    //Binding Landlord bank details
    private void bindLandLordBankData() {
        checkUpdateValueBank = 1;
        checkUpdateValueBranch = 1;
        CommonConstants.bankTypeId = String.valueOf(landlordBank.getBankid());
        String bankTypeId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getBankTypeId(CommonConstants.bankTypeId));
        // bankDetailsSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bankCodeDataMap, Integer.parseInt(bankTypeId)));
        // branchNameDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getBranchDetails(String.valueOf(landlordBank.getBankid())));

        LLAccountName.setText(landlordBank.getAccountholdername());
        LLAccountNumber.setText(landlordBank.getAccountnumber());

//        branchNameDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getBranchDetails(bankTypeId));
//        ArrayAdapter<String> branchNameSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(branchNameDataMap, "BranchName"));
//        branchNameSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        branchNameSpin.setAdapter(branchNameSpinnerArrayAdapter);
//        int branchPos = CommonUtilsNavigation.getvalueFromHashMap(branchNameDataMap, landlordBank.getBankid());
//Log.v(LOG_TAG, "##### selected branch pos " + branchPos);
//        branchNameSpin.setSelection(branchPos);
        LLIFSCcode.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getIfscCode(String.valueOf(landlordBank.getBankid()))));
//        LLIFSCcode.setEnabled(false);
//        LLIFSCcode.setFocusable(false);
//        LLIFSCcode.setClickable(false);
    }

    //filtering Id Proofs
    public void filterIdProofs() {
        if (identityProofsList != null) {
            for (LandlordIdProof identityProof : identityProofsList) {
                idProofsData.remove(String.valueOf(identityProof.getIDProofTypeId()));
            }
        }
    }


    //Adding Id Proof Dialog
    private void displayIdProofsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View promptView = layoutInflater.inflate(R.layout.dialog_idproof, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);
        final EditText aadhar_edt = promptView.findViewById(R.id.idproofsEdt);
        filterIdProofs();
        idproof = promptView.findViewById(R.id.idProofsSpin);
        idprooflayout = promptView.findViewById(R.id.idprooflayout);
        idprooflayout.setVisibility(View.GONE);
        idproof.setAdapter(UiUtils.createAdapter(getActivity(), idProofsData, "Id proof"));

        idproof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aadhar_edt.setText("");
                String selectedProof = "" + idproof.getSelectedItem().toString();
                aadhar_edt.setInputType(selectedProof.contains("Aadhaar") ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);

                if (selectedProof.contains("Driving")) {
                    aadhar_edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                } else if (selectedProof.contains("PAN")) {
                    aadhar_edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                } else if (selectedProof.contains("Aadhaar")) {
                    aadhar_edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                }
                else if (selectedProof.contains("Passport")) {
                    aadhar_edt.setHint("Passport Number");
                    aadhar_edt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    aadhar_edt.setInputType(InputType.TYPE_CLASS_TEXT);
                    aadhar_edt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                }else if(selectedProof.contains("Voter")){
                    aadhar_edt.setHint("VoterId Number");
                    aadhar_edt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                } else if (selectedProof.contains("Dummy")) {
                    aadhar_edt.setHint(" ");
                    aadhar_edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button SaveBtn = promptView.findViewById(R.id.SaveBtn);
        final Button CancelBtn = promptView.findViewById(R.id.CancelBtn);
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aadharStr = aadhar_edt.getText().toString();
                if (!CommonUtils.isEmptySpinner(idproof)) {
                    if (!TextUtils.isEmpty(aadharStr)) {
                        String selectedProof = idproof.getSelectedItem().toString();

                        if (TextUtils.isEmpty(selectedProof)) {
                            UiUtils.showCustomToastMessage("Please Select Id Proof", getActivity(), 1);
                            return;
                        }

                        if (selectedProof.equalsIgnoreCase(getResources().getString(R.string.adhar_number))) {
                            if (!TextUtils.isDigitsOnly(aadharStr)) {
                                UiUtils.showCustomToastMessage("Enter Only Numbers", getActivity(), 1);
                                return;
                            }
                            if (aadharStr.length() < 12) {
                                UiUtils.showCustomToastMessage("Enter Proper Aadhaar Card Number", getActivity(), 1);
                                return;
                            } else {
                                updateIdProofsAdapter(idproof.getSelectedItem().toString(), aadharStr);
                                alert.cancel();
                            }
                        } else if (selectedProof.equalsIgnoreCase(getResources().getString(R.string.pancard_number))) {
                            if (aadharStr.length() < 10) {
                                UiUtils.showCustomToastMessage("Enter Proper PAN Card Number", getActivity(), 1);
                                return;
                            } else {
                                updateIdProofsAdapter(idproof.getSelectedItem().toString(), aadharStr);
                                alert.cancel();
                            }
                        } else if (selectedProof.equalsIgnoreCase(getResources().getString(R.string.drive_number))) {
                            if (aadharStr.length() < 16) {
                                UiUtils.showCustomToastMessage("Enter Proper Driving License Number", getActivity(), 1);
                                return;
                            } else {
                                updateIdProofsAdapter(idproof.getSelectedItem().toString(), aadharStr);
                                alert.cancel();
                            }
                        } else {
                            updateIdProofsAdapter(idproof.getSelectedItem().toString(), aadharStr);
                            alert.cancel();
                        }

                    } else {
                        UiUtils.showCustomToastMessage("Please Enter the Id Proof Detail", getActivity(), 1);
                    }
                } else {
                    UiUtils.showCustomToastMessage("Please Select Id Proof", getActivity(), 1);
                }
            }
        });
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
        alert = alertDialogBuilder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    //Update IdProof Data
    private void updateIdProofsAdapter(String idProofName, String enteredNumber) {
        String idProofId = CommonUtils.getKeyFromValue(mainData, idProofName);
        LandlordIdProof identityProof = new LandlordIdProof();
        identityProof.setIDProofTypeId(Integer.parseInt(idProofId));
        identityProof.setIdProofNumber(enteredNumber);
        identityProofsList.add(identityProof);
        idProofsRecyclerView.setVisibility(View.VISIBLE);
        mainData = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("12"));
        idProofsListAdapter.updateData(identityProofsList, mainData);
    }

    //Validations
    @SuppressLint("LongLogTag")
    private boolean isValid() {

        plotcodeStr = plotCodeTxt.getText().toString().trim();
        totalplotareaStr = plotareaedt.getText().toString().trim();
        landmarkStr = landmarkedt.getText().toString().trim();
        plotaddressStr = plotaddress.getText().toString().trim();
        surveyNoedtStr = surveyNoedt.getText().toString().trim();
        adangalNoedtStr = adangalNoedt.getText().toString().trim();

        LLHolderNameStr = LLHolderName.getText().toString().trim();
        LLMobileNoStr = LLMobileNo.getText().toString().trim();
        LLAccountNameStr = LLAccountName.getText().toString().trim();
        LLAccountNumberStr = LLAccountNumber.getText().toString().trim();
        LLIFSCcodeStr = LLIFSCcode.getText().toString().trim();
        LLleaseDateStr = LLleaseDate.getText().toString();
        LLleaseEndDateStr = LLleaseEndDate.getText().toString().trim();
        Log.e("LLleaseEndDateStr", LLleaseEndDateStr);
        Log.e("LLleaseDateStr", LLleaseDateStr);

         formattedEndDate = changeDateFormat(LLleaseEndDateStr);
         formattedStartDate = changeDateFormat(LLleaseDateStr);

        Log.e("LLleaseformatedEndDateStr", formattedEndDate);   // 👉 2025-11-22
        Log.e("LLleaseformatedDateStr", formattedStartDate);
        if (CommonUtils.isEmptySpinner(stateSpinner)) {
            UiUtils.showCustomToastMessage("Please Select State", getActivity(), 1);
            return false;
        }

        if (CommonUtils.isEmptySpinner(districtSpinner)) {
            UiUtils.showCustomToastMessage("Please Select District", getActivity(), 1);
            return false;
        }

        if (CommonUtils.isEmptySpinner(mandalSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Mandal", getActivity(), 1);
            return false;
        }

        if (CommonUtils.isEmptySpinner(villageSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Village", getActivity(), 1);
            return false;
        }
        if (TextUtils.isEmpty(CommonConstants.PLOT_CODE)) {
         //   com.cis.palm360.cloudhelper.Log.pushLogToCrashlytics(PlotDetailsFragment.class.getSimpleName() + "\n" + CommonConstants.PLOT_CODE);

            CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(financalYrDays), financalYrDays);
            UiUtils.showCustomToastMessage("Field Code is Generated", getActivity(), 0);
            return false;
        }

        //   if (CommonUtils.isFromConversion()) {
        if (TextUtils.isEmpty(surveyNoedtStr)) {
            UiUtils.showCustomToastMessage("Please Enter Survey Number", getActivity(), 1);
            surveyNoedt.requestFocus();
            return false;
        }
        //   }
        if (TextUtils.isEmpty(adangalNoedtStr)) {
            UiUtils.showCustomToastMessage("Please Enter Passbook Number", getActivity(), 1);
            adangalNoedt.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(totalplotareaStr)) {
            UiUtils.showCustomToastMessage("Please Enter Total Field Area", getActivity(), 1);
            plotareaedt.requestFocus();
            return false;
        }else {
            double plotAreaValue = Double.parseDouble(totalplotareaStr);
            if (totalplotareaStr.startsWith(".")){
                UiUtils.showCustomToastMessage("Please Enter Valid Field Area", getActivity(), 1);
                plotareaedt.requestFocus();
                return false;
            }else if (plotAreaValue <= 0 ){
                UiUtils.showCustomToastMessage("Field Area should be greater than 0", getActivity(), 1);
                plotareaedt.requestFocus();
                return false;
            }
        }

        String plotAreaStr = plotareaedt.getText().toString().trim();
        String horticultureAreaStr = totalAreaHorticulture.getText().toString().trim();
        String plotgovcode = farmer_gvt_id.getText().toString().trim();

// Validate required field
        if (plotAreaStr.isEmpty()) {
            UiUtils.showCustomToastMessage("Please Enter Field Area", getActivity(), 1);
            plotareaedt.requestFocus();
            return false;
        }


// Parse plotArea safely
        double plotArea = Double.parseDouble(plotAreaStr);

// Only validate horticulture area if it's entered
        if (!horticultureAreaStr.isEmpty()) {
            double horticultureArea = Double.parseDouble(horticultureAreaStr);

            if (horticultureArea > plotArea) {
                UiUtils.showCustomToastMessage("Horticulture Area cannot be greater than Total Field Area", getActivity(), 1);
                totalAreaHorticulture.requestFocus();
                return false;
            }
        }


// Continue with form submission

        if (TextUtils.isEmpty(landmarkStr)) {
            UiUtils.showCustomToastMessage("Please Enter landmark", getActivity(), 1);
            landmarkedt.requestFocus();
            return false;
        }
        if (landmarkStr.length() < 10) {
            UiUtils.showCustomToastMessage("Landmark must be at least 10 characters", getActivity(), 1);
            landmarkedt.requestFocus();
            return false;
        }


        if (TextUtils.isEmpty(plotaddressStr)) {
            UiUtils.showCustomToastMessage("Please Enter Field Address", getActivity(), 1);
            plotaddress.requestFocus();
            return false;
        }
        if (plotaddressStr.length() < 10) {
            UiUtils.showCustomToastMessage("Address must be at least 10 characters", getActivity(), 1);
            plotaddress.requestFocus();
            return false;
        }

        if (CommonUtils.isEmptySpinner(ownerShipSpin)) {
            UiUtils.showCustomToastMessage("Please Select Ownership", getActivity(), 1);
            return false;
        }

        if (CommonConstants.leased) {
            if (!TextUtils.isEmpty(LLMobileNoStr) ){
                if (LLMobileNoStr.length() < 10) {
                    UiUtils.showCustomToastMessage("Please Enter Proper LandLord Contact Number", getActivity(), 1);
                    LLMobileNo.requestFocus();
                    return false;

                }
            }
            // if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {
            if (TextUtils.isEmpty(surveyNoedtStr)) {
                UiUtils.showCustomToastMessage("Please Enter Survey Number", getActivity(), 1);
                surveyNoedt.requestFocus();
                return false;
            }
            //   }

//            if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {

            if (TextUtils.isEmpty(LLHolderNameStr)) {
                UiUtils.showCustomToastMessage("Please Enter LandLord Holder Name", getActivity(), 1);
                LLHolderName.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(LLMobileNoStr) || LLMobileNoStr.length() < 10) {
                UiUtils.showCustomToastMessage("Please Enter Mobile Number", getActivity(), 1);
                LLMobileNo.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(LLleaseDateStr)) {
                UiUtils.showCustomToastMessage("Please Enter Lease Start Date", getActivity(), 1);
                LLleaseDate.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(LLleaseEndDateStr)) {
                UiUtils.showCustomToastMessage("Please Enter Lease End Date", getActivity(), 1);
                LLleaseEndDate.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(LLAccountNameStr)) {
                UiUtils.showCustomToastMessage("Please Enter LandLord Account Holder Name", getActivity(), 1);
                LLAccountName.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(LLAccountNumberStr)) {
                UiUtils.showCustomToastMessage("Please Enter LandLord Account Number", getActivity(), 1);
                LLAccountNumber.requestFocus();
                return false;
            }
            if (LLAccountNumberStr.length() < 9 || LLAccountNumberStr.length() > 18) {
                UiUtils.showCustomToastMessage("Please Enter Valid Account Number", getActivity(), 1);
                LLAccountNumber.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(LLIFSCcodeStr)) {
                //Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_account_number_label), Toast.LENGTH_SHORT).show();
                UiUtils.showCustomToastMessage(getResources().getString(R.string.error_ifsc_code), getActivity(), 1);

                LLIFSCcode.requestFocus();
                return false;
            }
            if(LLIFSCcodeStr.length() <  11){
                UiUtils.showCustomToastMessage("Please Enter Valid IFSC Code", getActivity(), 1);
                LLIFSCcode.requestFocus();
                return false;
            }
            if( bankDetailsSpin.getText().toString().equalsIgnoreCase("Bank Not Available")){
                UiUtils.showCustomToastMessage("Please Enter Valid IFSC Code", getActivity(), 1);
                LLIFSCcode.requestFocus();
                return false;
            }

       /*         if (plotgovcode.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please enter Government Field ID ", getActivity(), 0);
                    farmer_gvt_id.requestFocus();
                    return false;
                }*/
//                if (CommonUtils.isEmptySpinner(bankDetailsSpin)) {
//                    UiUtils.showCustomToastMessage("Please select LandLord Bank Name", getActivity(), 0);
//                    return false;
//                }
//                if (CommonUtils.isEmptySpinner(branchNameSpin)) {
//                    UiUtils.showCustomToastMessage("Please select LandLord Branch Name", getActivity(), 0);
//                    return false;
//                }


//            }
        }

        if (CommonUtils.isEmptySpinner(careTakerSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Caretaker Type", getActivity(), 1);
            careTakerSpinner.requestFocus();
            return false;
        }

        if (careTakerSpinner.getSelectedItemPosition() == 1) {
            if (TextUtils.isEmpty(careTakerNameEdit.getText().toString().trim())) {
                UiUtils.showCustomToastMessage("Please Enter Caretaker Name", getActivity(), 1);
                careTakerNameEdit.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(careTakerNumberEdit.getText().toString()) || careTakerNumberEdit.getText().toString().length() < 10) {
                //   primary_contactno.setError(getResources().getString(R.string.error_contact_number));
                UiUtils.showCustomToastMessage("Please Enter Proper Caretaker Number", getActivity(), 1);
                careTakerNumberEdit.requestFocus();
                return false;
            }

        }



//        if (CommonUtils.isEmptySpinner(properBoundryFencingSpinner)) {
//            UiUtils.showCustomToastMessage("Please Select Proper Boundary Fencing", getActivity(), 0);
//            return false;
//        }


        if (!CommonUtils.isFromCropMaintenance() && !CommonUtils.isFromConversion()) {
            if (CommonUtils.isEmptySpinner(incomeSpin)) {
                UiUtils.showCustomToastMessage("Please Select Income From Current Crop", getActivity(), 1);
                return false;
            }
        }

        if (!CommonUtils.isFromCropMaintenance() && !CommonUtils.isFromConversion() && !CommonUtils.isFromFollowUp()) {
            if (dataPair == null || dataPair.isEmpty()) {
                UiUtils.showCustomToastMessage("Please Enter Current Crop Grown", getActivity(), 1);
                return false;
            }
        }

//        if (farmerCurrentImage1 == null || farmerCurrentImage2 == null) {
//            UiUtils.showCustomToastMessage("Please Upload PPB Images", getActivity(), 0);
//            return false;
//        }

    //    if (CommonUtils.isFromCropMaintenance() || CommonUtils.isFromConversion()) {

    //    }

        double currentCropArea = 0.0;
        if (currentCropList != null && !currentCropList.isEmpty()) {
            for (CropModel cropModel : currentCropList) {
                try {
                    if (cropModel.isActive == 1)
                        currentCropArea = currentCropArea + Double.parseDouble(cropModel.recName);
                } catch (NumberFormatException nfe) {
                    Log.e(LOG_TAG, "@@@@ error while parsing number");
                }
            }
        }

        if (plotArea < currentCropArea) {
            UiUtils.showCustomToastMessage("Current Crops Area Should Not Exceed The Total Field Area", getActivity(), 1);
            return false;
        }

        return true;
    }

    private static String changeDateFormat(String inputDate) {
        if (inputDate == null || inputDate.trim().isEmpty()) {
            return ""; // return empty if no date entered
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(inputDate.trim()); // trim removes extra spaces
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return inputDate; // fallback
        }
    }

    //Set Pincode based on villageId
    public void setPinCode() {
        globalpincode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPincode(CommonConstants.villageIdPlot));
        if (!TextUtils.isEmpty(globalpincode)) {
            pincode.setText(globalpincode);
            pincode.setEnabled(false);
            pincode.setCursorVisible(false);
            pincode.setClickable(false);
            pincode.setTextColor(Color.BLACK);
        }
    }

    //saving the details plot and address
    public void saveState() {
        ///
        savePlotDetails();
        saveAddressData();

        if (CommonUtils.isFromCropMaintenance() && CommonConstants.isplotupdated == true){
            DataManager.getInstance().addData(DataManager.PLOT_DETAILS, plot);
        }else if(CommonUtils.isFromConversion() || CommonUtils.isFromFollowUp() ){
            DataManager.getInstance().addData(DataManager.PLOT_DETAILS, plot);
        }else if(CommonUtils.isNewRegistration() || CommonUtils.isNewPlotRegistration()){
            DataManager.getInstance().addData(DataManager.PLOT_DETAILS, plot);
        }

        DataManager.getInstance().addData(DataManager.VALIDATE_PLOT_ADDRESS_DETAILS, savedAddressData);
        DataManager.getInstance().addData(DataManager.PLOT_ADDRESS_DETAILS, savedAddressData);
        DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA_PAIR, neighbourPlotPair);
        DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA_PAIR, dataPair);
        if(farmerCurrentImage1 != null) {
            DataManager.getInstance().addData(DataManager.PPB1, farmerCurrentImage1);
            savePictureData(farmerCurrentImage1);
        }

        if(farmerCurrentImage2 != null) {
            DataManager.getInstance().addData(DataManager.PPB2, farmerCurrentImage2);
            savePictureData(farmerCurrentImage2);
        }


        if (null != neighbourPlotsList) {
            DataManager.getInstance().addData(DataManager.PLOT_NEIGHBOURING_PLOTS_DATA, neighbourPlotsList);
        }

        DataManager.getInstance().addData(DataManager.PLOT_CURRENT_CROPS_DATA, currentCropList);

        if (CommonConstants.leased) {
            saveLandLordDetails();
        } else {
            if (fromDataBase) {
                DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, true);
            } else {
                DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, false);
            }
            updateUiListener.updateUserInterface(0);
            CommonConstants.Flags.isPlotsDataUpdated = true;
            getFragmentManager().popBackStack();
        }


    }

    //saving landlord details
    private void saveLandLordDetails() {
        PlotLandlord landLordLeaseData = new PlotLandlord();
        landLordLeaseData.setPlotcode(CommonConstants.PLOT_CODE);
        landLordLeaseData.setLandlordname(LLHolderNameStr);
        landLordLeaseData.setLandlordcontactnumber(LLMobileNoStr);
        landLordLeaseData.setLeasestartdate(formattedStartDate);
        landLordLeaseData.setLeaseenddate(formattedEndDate);
        landLordLeaseData.setIsactive(leaseActiveValue);
        landLordLeaseData.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        landLordLeaseData.setCreateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        landLordLeaseData.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        landLordLeaseData.setUpdateddate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        DataManager.getInstance().addData(DataManager.LANDLORD_LEASED_DATA, landLordLeaseData);

        saveLandLordBankDetailsData();
        saveLandLordIDproofs();

    }

    //Saving Landlord Idproofs
    private void saveLandLordIDproofs() {
        DataManager.getInstance().addData(DataManager.LANDLORD_IDPROOFS_DATA, identityProofsList);
    }

    //Saving Landlord Bankdetails
    private void saveLandLordBankDetailsData() {
        LandlordBank landlordBank = new LandlordBank();

        if (!TextUtils.isEmpty(LLIFSCcode.getText().toString())) {

            //String bankId = CommonUtils.getKeyFromValue(bankCodeDataMap, bankDetailsSpin.getSelectedItem().toString());
            //   String branchId = CommonUtils.getKeyFromValue(branchNameDataMap, branchNameSpin.getSelectedItem().toString());
            landlordBank.setBankid(bankId);
            // landlordBank.set(Integer.parseInt(bankId));
            landlordBank.setAccountholdername(LLAccountNameStr);
            landlordBank.setAccountnumber(LLAccountNumberStr);
            DataManager.getInstance().addData(DataManager.LANDLORD_BANK_DATA, landlordBank);
            LLIFSCcode.setText("");
            LLAccountName.setText("");
            LLAccountNumber.setText("");
            LLHolderName.setText("");
            LLMobileNo.setText("");
            LLleaseDate.setText("");
            LLleaseEndDate.setText("");
            updateUiListener.updateUserInterface(0);
        }else{

        }

        // LLHolderName, LLMobileNo, LLAccountName, LLAccountNumber, LLleaseDate, LLleaseEndDate, totalAreaHorticulture;

        CommonConstants.Flags.isPlotsDataUpdated = true;
        if (fromDataBase) {
            DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, true);
        } else {
            DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, false);
        }
        getFragmentManager().popBackStack();
    }

    //Saving Plot Details
    public void savePlotDetails() {
        if (plot == null) {
            plot = new Plot();
        }
        plot.setTotalplotarea(Double.parseDouble(plotareaedt.getText().toString().trim()));
        if (!TextUtils.isEmpty(areaUnderSuitablePalmEdit.getText().toString())) {
            plot.setSuitablePalmOilArea(Double.parseDouble(areaUnderSuitablePalmEdit.getText().toString().trim()));
        } else {
            plot.setSuitablePalmOilArea(0.0);
        }

        if (!TextUtils.isEmpty(totalAreaHorticulture.getText().toString())) {
            plot.setTotalAreaUnderHorticulture(Float.parseFloat(totalAreaHorticulture.getText().toString().trim()));
        } else {
            plot.setSuitablePalmOilArea(0.0);
        }

        if (!TextUtils.isEmpty(farmer_gvt_id.getText().toString())) {
            plot.setGovtPlotCode(farmer_gvt_id.getText().toString().trim());
        } else {
            plot.setGovtPlotCode(null);
        }
        plot.setAddesscode(CommonConstants.ADDRESS_CODE_PREFIX + CommonConstants.PLOT_CODE);
        if (TextUtils.isEmpty(CommonConstants.PLOT_CODE)) {

            CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(financalYrDays), financalYrDays);

        }
        plot.setCode(CommonConstants.PLOT_CODE);
        plot.setFarmercode(CommonConstants.FARMER_CODE);

        String incomeTypeId = CommonUtils.getKeyFromValue(incomeDataMap, incomeSpin.getSelectedItem().toString());
        String ownerShipTypeId = CommonUtils.getKeyFromValue(ownerShipMap, ownerShipSpin.getSelectedItem().toString());
        String landTypeid = CommonUtils.getKeyFromValue(landTypeMap, landTypeSpn.getSelectedItem().toString());

        plot.setPlotownershiptypeid((!TextUtils.isEmpty(ownerShipTypeId)) ? Integer.parseInt(CommonUtils.getKeyFromValue(ownerShipMap, ownerShipSpin.getSelectedItem().toString())) : null);
        plot.setCropincometypeid((!TextUtils.isEmpty(incomeTypeId)) ? Integer.parseInt(CommonUtils.getKeyFromValue(incomeDataMap, incomeSpin.getSelectedItem().toString())) : null);
        plot.setLandTypeId((!TextUtils.isEmpty(landTypeid)) ? Integer.parseInt(CommonUtils.getKeyFromValue(landTypeMap, landTypeSpn.getSelectedItem().toString())) : null);
        if (!CommonUtils.isEmptySpinner(properBoundryFencingSpinner)) {
            plot.setIsBoundryFencing(properBoundryFencingSpinner.getSelectedItemPosition() == 1 ? 1 : 2);
        } else {
            plot.setIsBoundryFencing(null);
        }
     //   if (CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()) {
            plot.setSurveynumber(surveyNoedtStr);
            plot.setAdangalnumber(adangalNoedtStr);
            plot.setIsplothandledbycaretaker(careTakerSpinner.getSelectedItemPosition() == 1 ? 1 : 2);
            if (careTakerSpinner.getSelectedItemPosition() == 1) {
                plot.setCaretakercontactnumber(careTakerNumberEdit.getText().toString());
                plot.setCaretakername(careTakerNameEdit.getText().toString().trim());
            }
        //}
    }

    //binding data to address spinners
    public void saveAddressData() {
        savedAddressData.setAddressline1(plotaddress.getText().toString().trim());
        savedAddressData.setAddressline2("");
        savedAddressData.setAddressline3("");
        savedAddressData.setLandmark(landmarkedt.getText().toString().trim());
        savedAddressData.setVillageid(Integer.parseInt(CommonConstants.villageIdPlot));
        savedAddressData.setMandalid(Integer.parseInt(CommonConstants.mandalIdPlot));
        savedAddressData.setDistictid(Integer.parseInt(CommonConstants.districtIdPlot));
        savedAddressData.setStateid(Integer.parseInt(CommonConstants.statePlotId));
        savedAddressData.setCountryid(Integer.parseInt(CommonConstants.countryID));
        savedAddressData.setPincode(pincode.getText().toString().trim().length() == 0 ? 0 : new BigInteger(pincode.getText().toString().trim()).intValue());
        DataManager.getInstance().addData(DataManager.PLOT_ADDRESS_DETAILS, savedAddressData);
    }

    //Handling
    public void handleAddressData() {
/*
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userStateSelect) {
                    userStateSelect = false;
                } else {
                    if (stateDataMap != null && stateDataMap.size() > 0 && stateSpinner.getSelectedItemPosition() != 0) {
                        CommonConstants.statePlotId = stateDataMap.keySet().toArray(new String[stateDataMap.size()])[i - 1];
                        Log.v(LOG_TAG, "@@@ Selected plot State " + CommonConstants.statePlotId);
                        districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserDistricts(TextUtils.join(",", districtIds), CommonConstants.statePlotId));
                        ArrayAdapter<String> spinnerDistrictArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
                        spinnerDistrictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(spinnerDistrictArrayAdapter);
                        CommonConstants.stateName = stateSpinner.getSelectedItem().toString();
                        Pair statePair = stateDataMap.get(CommonConstants.statePlotId);
                        CommonConstants.stateCodePlot = statePair.first.toString();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userDisSelect) {
                    userDisSelect = false;
                } else if (districtDataMap != null && districtDataMap.size() > 0 && districtSpinner.getSelectedItemPosition() != 0) {
                    CommonConstants.districtIdPlot = districtDataMap.keySet().toArray(new String[districtDataMap.size()])[i - 1];
                    Log.v(LOG_TAG, "@@@ Selected State " + CommonConstants.districtIdPlot);
                    mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().setUserMandals(TextUtils.join(",", mandalIds), CommonConstants.districtIdPlot));
                    ArrayAdapter<String> spinnerMandalArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
                    spinnerMandalArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mandalSpinner.setAdapter(spinnerMandalArrayAdapter);
                    CommonConstants.districtName = districtSpinner.getSelectedItem().toString();
                    Pair districtPair = districtDataMap.get(CommonConstants.districtIdPlot);
                    CommonConstants.districtCodePlot = districtPair.first.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mandalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userManSelect) {
                    userManSelect = false;
                } else if (mandalDataMap != null && mandalDataMap.size() > 0 && mandalSpinner.getSelectedItemPosition() != 0) {
                    CommonConstants.mandalIdPlot = mandalDataMap.keySet().toArray(new String[mandalDataMap.size()])[i - 1];
                    Log.v(LOG_TAG, "@@@ Selected plot mandal " + CommonConstants.mandalIdPlot);
                    CommonConstants.mandalName = mandalSpinner.getSelectedItem().toString();
                    Pair mandalPair = mandalDataMap.get(CommonConstants.mandalIdPlot);
                    CommonConstants.mandalCodePlot = mandalPair.first.toString();
                    CommonConstants.prevMandalPos = i;
                    villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().setUserVillages(TextUtils.join(",", userVillages), CommonConstants.mandalIdPlot));

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    villageSpinner.setAdapter(spinnerArrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userVillageSelect) {
                    userVillageSelect = false;
                } else if (villagesDataMap != null && villagesDataMap.size() > 0 && villageSpinner.getSelectedItemPosition() != 0) {
                    CommonConstants.villageIdPlot = villagesDataMap.keySet().toArray(new String[villagesDataMap.size()])[i - 1];
                    villageCodeStr = villageSpinner.getSelectedItem().toString();
                    Pair villagePair = villagesDataMap.get(CommonConstants.villageIdPlot);
                    CommonConstants.villageCodePlot = villagePair.first.toString();
                    CommonConstants.villageName = villageSpinner.getSelectedItem().toString();
                    CommonConstants.prevVillagePos = i;
                    if (!CommonUtils.isFromFollowUp() && !CommonUtils.isFromConversion() && !CommonUtils.isFromCropMaintenance()) {
                        financalSubStringYear = String.valueOf(financialYear).substring(2, 4);
                        CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(financalSubStringYear + days), financalYrDays);
                    }
                    plotCodeTxt.setText(CommonConstants.PLOT_CODE);
                    String ClusterName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getClusterName(CommonConstants.villageIdPlot));
                    ClusterNameTv.setText("" + ClusterName);
                    setPinCode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        ownerShipMap = dataAccessHandler.getGenericData(Queries.getInstance().getPlotOwnerShip());
        String[] ownership = CommonUtils.fromMap(ownerShipMap, "Owner ship");
        ArrayAdapter<String> ownerShipAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ownership);
        ownerShipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownerShipSpin.setAdapter(ownerShipAdapter);

        incomeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getAnualIncome());
        String[] income = CommonUtils.fromMap(incomeDataMap, "Income");
        ArrayAdapter<String> incomeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, income);
        incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeSpin.setAdapter(incomeAdapter);

    }


    @Override
    public void onDataSelected(int type, Bundle bundle) {
        if (type == CURRENT_CROP_TYPE) { //cropId
            dataPair.add(Pair.create(bundle.getString("cropName"), bundle.getString("areaAllocated")));
            currentCropList.add(new CropModel(bundle.getString("cropName"), Integer.parseInt(bundle.getString("cropId")), bundle.getString("areaAllocated"), 1));
            genericAdapter.updateAdapter(dataPair);
            currentCropRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            currentCropRecyclerView.setAdapter(genericAdapter);
        } else if (type == NEIGHBOUR_PLOT_TYPE) {
            neighbourPlotsList.add(new CropModel(bundle.getString("cropName"), Integer.parseInt(bundle.getString("cropId")), bundle.getString("neightbourPlot")));
//            neighbourPlotPair.add(Pair.create(bundle.getString("cropName"), bundle.getString("cropId")));
            neighbourPlotPair.add(Pair.create(bundle.getString("neightbourPlot"), bundle.getString("cropName")));
            neighbourPlotAdapter.updateAdapter(neighbourPlotPair);
        }
    }

    @Override
    public void onEditClicked(int position, int tag) {
        EditEntryDialogFragment editEntryDialogFragment = new EditEntryDialogFragment();
        editEntryDialogFragment.setOnDataEditChangeListener(this);
        Bundle inputBundle = new Bundle();
        selectedPosition = position;
        selectedType = tag;
        if (tag == 1) {
            inputBundle.putInt("typeDialog", EditEntryDialogFragment.TYPE_EDIT_BOX);
            inputBundle.putString("title", dataPair.get(position).first);
            inputBundle.putString("prevData", dataPair.get(position).second + "-" + "Area of Current Crop");
            editEntryDialogFragment.setArguments(inputBundle);
        } else {
            inputBundle.putInt("typeDialog", EditEntryDialogFragment.TYPE_SPINNER);
            inputBundle.putString("title", neighbourPlotPair.get(position).first.toString());
            inputBundle.putString("prevData", neighbourPlotPair.get(position).second.toString());
            editEntryDialogFragment.setArguments(inputBundle);
        }
        FragmentManager mFragmentManager = getChildFragmentManager();
        editEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
    }

    @Override
    public void onDeleteClicked(int position, int tag) {
        Log.v(LOG_TAG, "@@@ delete clicked " + position);
        if (tag == 0) {
            CropModel cropModel = neighbourPlotsList.get(position);
            if (fromDataBase) {
                cropModel.isActive = 0;
                neighbourPlotsList.set(position, cropModel);
            } else {
                neighbourPlotsList.remove(position);
            }
           /* if(dataPair.size()>0){
            dataPair.remove(position);
            }*/
            neighbourPlotPair.remove(position);
            neighbourPlotAdapter.notifyDataSetChanged();
        } else {
            CropModel cropModel = currentCropList.get(position);
            if (fromDataBase) {
                cropModel.isActive = 0;
                currentCropList.set(position, cropModel);
            } else {
                currentCropList.remove(position);
            }
            dataPair.remove(position);
            genericAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDataEdited(Bundle dataBundle) {
        if (selectedType == 1) {
            currentCropList.set(selectedPosition, new CropModel(currentCropList.get(selectedPosition).cropName, currentCropList.get(selectedPosition).cropId, dataBundle.getString("inputValue"), 1));
            dataPair.set(selectedPosition, Pair.create(dataPair.get(selectedPosition).first, dataBundle.getString("inputValue")));
            genericAdapter.notifyDataSetChanged();
        } else {
            neighbourPlotsList.set(selectedPosition, new CropModel(neighbourPlotsList.get(selectedPosition).cropName, neighbourPlotsList.get(selectedPosition).cropId, dataBundle.getString("inputValue")));
            neighbourPlotPair.set(selectedPosition, Pair.create(neighbourPlotPair.get(selectedPosition).first, dataBundle.getString("inputValue")));
            neighbourPlotAdapter.notifyDataSetChanged();
        }
    }
    public List<CropModel> convertToCropModel(List<NeighbourPlot> data) {
        List<CropModel> npDataList = new ArrayList<>();

        // Logging the size of the input data list
        Log.d("convertToCropModel", "Input data size: " + data.size());

        for (NeighbourPlot neighbourPlot : data) {
            // Logging each NeighbourPlot object
            Log.d("convertToCropModel", "NeighbourPlot - Name: " + neighbourPlot.getName() +
                    ", CropId: " + neighbourPlot.getCropid() +
                    ", IsActive: " + neighbourPlot.getIsactive());

            // Getting crop name from cropDataMap
            String cropName = cropDataMap.get(String.valueOf(neighbourPlot.getCropid()));

            // Logging the crop name retrieved from cropDataMap
            Log.d("convertToCropModel", "Crop name retrieved from cropDataMap: " + cropName);

            // Creating and adding CropModel to the list
            CropModel cropModel = new CropModel(
                    neighbourPlot.getName(),
                    neighbourPlot.getCropid(),
                    cropName,
                    neighbourPlot.getIsactive()
            );
            npDataList.add(cropModel);

            // Logging the created CropModel
            Log.d("convertToCropModel", "Added CropModel: " + cropModel.toString());

            // Creating Pair and adding it to neighbourPlotPair
            Pair<String, String> pair = Pair.create(neighbourPlot.getName(), cropName);
            neighbourPlotPair.add(pair);

            // Logging the Pair added
            Log.d("convertToCropModel", "Added Pair to neighbourPlotPair: " + pair.toString());
        }

        // Logging the final size of npDataList
        Log.d("convertToCropModel", "Converted data size (npDataList): " + npDataList.size());

        return npDataList;
    }

//    public List<CropModel> convertToCropModel(List<NeighbourPlot> data) {
//        List<CropModel> npDataList = new ArrayList<>();
//        for (NeighbourPlot neighbourPlot : data) {
//            npDataList.add(new CropModel(neighbourPlot.getName(), neighbourPlot.getCropid(), cropDataMap.get(String.valueOf(neighbourPlot.getCropid())), neighbourPlot.getIsactive()));
//            neighbourPlotPair.add(Pair.create(neighbourPlot.getName(), cropDataMap.get(String.valueOf(neighbourPlot.getCropid()))));
//        }
//        return npDataList;
//    }

    public List<CropModel> convertToCurrentCropModel(List<PlotCurrentCrop> data) {
        List<CropModel> currentCropList = new ArrayList<>();
        for (PlotCurrentCrop currentCrop : data) {
            currentCropList.add(new CropModel(cropDataMap.get(String.valueOf(currentCrop.getCropid())), currentCrop.getCropid(), String.valueOf(currentCrop.getCurrentcroparea()), currentCrop.getIsactive()));
            dataPair.add(Pair.create(cropDataMap.get(String.valueOf(currentCrop.getCropid())), String.valueOf(currentCrop.getCurrentcroparea())));
        }
        return currentCropList;
    }

    @Override
    public void onEditClicked(int position) {
        Log.v(LOG_TAG, "@@@ edit clicked " + position);
        showEditDialog(position);
    }


    @Override
    public void onDeleteClicked(int position) {
        Log.v(LOG_TAG, "@@@ delete clicked " + position);
        identityProofsList.remove(position);
        idProofsListAdapter.notifyDataSetChanged();

    }

    private void showEditDialog(final int position) {
        mainData = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("12"));
        final EditText idEdit = new EditText(getActivity());
        final String title = mainData.get(String.valueOf(identityProofsList.get(position).getIDProofTypeId()));
        idEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        idEdit.setText(identityProofsList.get(position).getIdProofNumber());
        @SuppressLint("RestrictedApi") final AlertDialog.Builder idProofsBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Edit")
                .setMessage(title)
                .setView(idEdit, 20, 0, 20, 0)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog idProofsDialog = idProofsBuilder.create();
        idProofsDialog.setCancelable(false);
        idProofsDialog.setCanceledOnTouchOutside(false);
        idProofsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button muteBtn = idProofsDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                muteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(idEdit.getText().toString())) {
                            if (!TextUtils.isEmpty(title) && title.equalsIgnoreCase(getResources().getString(R.string.adhar_number))
                                    && !TextUtils.isDigitsOnly(idEdit.getText().toString())) {
//                                Toast.makeText(getActivity(), "Aadhar Card Accepts Only Numbers", Toast.LENGTH_SHORT).show();
                                UiUtils.showCustomToastMessage("Aadhaar Card Accepts Only Numbers", getActivity(), 1);
                                return;
                            }
                            LandlordIdProof identityProof = new LandlordIdProof();
                            identityProof.setIDProofTypeId(identityProofsList.get(position).getIDProofTypeId());
                            identityProof.setIdProofNumber(idEdit.getText().toString());
                            identityProofsList.set(position, identityProof);
                            idProofsListAdapter.updateData(identityProofsList, mainData);
                            idProofsDialog.dismiss();
                        } else {
//                            Toast.makeText(getActivity(), "Please Enter Id Proof Value", Toast.LENGTH_SHORT).show();
                            UiUtils.showCustomToastMessage("Please Enter Id Proof Value", getActivity(), 1);
                        }
                    }
                });
            }
        });

        idProofsDialog.show();

    }

    private void updateLabel() {
        String displayFormat = "dd/MM/yyyy";    // For UI
        String saveFormat = "yyyy-MM-dd";       // For DB & comparison (safe)

        SimpleDateFormat sdfDisplay = new SimpleDateFormat(displayFormat, Locale.US);
        SimpleDateFormat sdfSave = new SimpleDateFormat(saveFormat, Locale.US);

        if (picker) {
            // Start date
            String displayDate = sdfDisplay.format(myCalendar.getTime());
            String saveDate = sdfSave.format(myCalendar.getTime());

            LLleaseDate.setText(displayDate);
            LLleaseDateStr = saveDate;   // stored safely as yyyy-MM-dd
            Log.e("LLleaseStartDateStr", LLleaseDateStr);

            // Reset end date
            LLleaseEndDate.setText("");
            LLleaseEndDateStr = "";

        } else {
            // End date
            String displayEndDate = sdfDisplay.format(myCalendar.getTime());
            String saveEndDate = sdfSave.format(myCalendar.getTime());
            Log.e("LLleaseStartDateStr", LLleaseDateStr +"");
            Log.e("formattedStartDate", formattedStartDate +"");
            LLleaseDateStr = LLleaseDate.getText().toString();
            formattedStartDate = changeDateFormat(LLleaseDateStr);
// Show validation only if BOTH are empty
            if (TextUtils.isEmpty(LLleaseDateStr) && TextUtils.isEmpty(formattedStartDate)) {
                UiUtils.showCustomToastMessage("Please Select Lease Start Date", getActivity(), 1);
                return;
            }

            Log.e("LLleaseStartDateStr", LLleaseDateStr);
            Log.e("LLEndDate",saveEndDate);
    /*        if (DateFormats.compareyyyyMmDdFormatDateStrings(LLleaseDateStr, saveEndDate)) {
                LLleaseEndDate.setText(displayEndDate);
                LLleaseEndDateStr = saveEndDate;
            } else */

                if (DateFormats.compareyyyyMmDdFormatDateStrings(formattedStartDate, saveEndDate)) {
                LLleaseEndDate.setText(displayEndDate);
                LLleaseEndDateStr = saveEndDate;
            } else {
                LLleaseEndDateStr = "";
                LLleaseEndDate.setText("");
                UiUtils.showCustomToastMessage("Lease End Date Should Be Greater Than Start Date", getActivity(), 1);
            }
        }
    }


/*
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (picker) {

            //start date
            LLleaseDate.setText(sdf.format(myCalendar.getTime()));
            LLleaseDateStr = LLleaseDate.getText().toString();
            LLleaseEndDate.setText("");
            LLleaseEndDateStr = "";
            //  lease = leaseDateFromStr;
        } else {
            LLleaseEndDateStr = sdf.format(myCalendar.getTime());

            if (TextUtils.isEmpty(LLleaseDateStr)) {
                Toast.makeText(getActivity(), "Please Select Lease Start Date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (DateFormats.compareMMddyyyyFormatDateStrings(LLleaseDateStr, LLleaseEndDateStr)) {

                LLleaseEndDate.setText(LLleaseEndDateStr);
            } else {
                LLleaseEndDateStr = "";
                LLleaseEndDate.setText("");

                Toast.makeText(getActivity(), "Lease End Date Should Be Greater Than Start Date", Toast.LENGTH_SHORT).show();
            }
        }

    }
*/

    public void bindGeoGraphicalLocation() {
        userVillages = dataAccessHandler.getSingleListData(Queries.getInstance().getUserVillageIds(CommonConstants.USER_ID));
        mandalIds = dataAccessHandler.getSingleListData(Queries.getInstance().getUserMandalIds(TextUtils.join(",", userVillages)));
        districtIds = dataAccessHandler.getSingleListData(Queries.getInstance().getUserDistrictIds(TextUtils.join(",", mandalIds)));
        stateIds = dataAccessHandler.getSingleListData(Queries.getInstance().getUserStateIds(TextUtils.join(",", districtIds)));
        stateDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserStates(TextUtils.join(",", stateIds)));
        if (stateDataMap != null && stateDataMap.size() == 1) {
            // Auto-select state
            String key = new ArrayList<>(stateDataMap.keySet()).get(0);
            Pair statePair = stateDataMap.get(key);
            CommonConstants.statePlotId = key;
            CommonConstants.stateCodePlot = statePair.first.toString();
            CommonConstants.stateName = statePair.second.toString();

            Log.d("DEBUG", "Auto-select district: " + CommonConstants.districtName);
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(stateDataMap, "State"));
            stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateSpinner.setAdapter(stateAdapter);
            // Find the correct index in districtList
            int position = stateAdapter.getPosition(CommonConstants.stateName);

            if (position != -1) {
                stateSpinner.setSelection(position); // auto-binds value
            }

            // Auto-select state spinner
         //   stateSpinner.setAdapter(null);
            loadDistricts(key);
        }
        else {
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(stateDataMap, "State"));
            stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateSpinner.setAdapter(stateAdapter);

            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (position == 0) {
//                        // Clear all dependent spinners
//                        districtSpinner.setAdapter(null);
//                        mandalSpinner.setAdapter(null);
//                        villageSpinner.setAdapter(null);
//                        ClusterNameTv.setText("");
//                        CommonConstants.statePlotId = null;
//                        CommonConstants.stateName = "";
//                        CommonConstants.stateCodePlot = "";
//                        return;
//                    }

                    if (userStateSelect) {
                        userStateSelect = false;
//                        districtSpinner.setAdapter(null);
//                        mandalSpinner.setAdapter(null);
                    } else {
                        if (stateDataMap != null && stateDataMap.size() > 0 && stateSpinner.getSelectedItemPosition() != 0) {
                            CommonConstants.statePlotId = stateDataMap.keySet().toArray(new String[stateDataMap.size()])[position - 1];
                            Log.v(LOG_TAG, "@@@ Selected plot State " + CommonConstants.statePlotId);
//                            districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserDistricts(TextUtils.join(",", districtIds), CommonConstants.statePlotId));
//                            ArrayAdapter<String> spinnerDistrictArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
//                            spinnerDistrictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            districtSpinner.setAdapter(spinnerDistrictArrayAdapter);
                            CommonConstants.stateName = stateSpinner.getSelectedItem().toString();
                            Pair statePair = stateDataMap.get(CommonConstants.statePlotId);
                            CommonConstants.stateCodePlot = statePair.first.toString();
                            loadDistricts(CommonConstants.statePlotId);
                        }
                        else{
                            districtSpinner.setAdapter(null);
                            mandalSpinner.setAdapter(null);
                            villageSpinner.setAdapter(null);
                            ClusterNameTv.setText("");
                            pincode.setText("");
                            plotCodeTxt.setText("");
                            CommonConstants.statePlotId = null;
                            CommonConstants.stateName = "";
                            CommonConstants.stateCodePlot = "";
                            return;
                        }
                    }
//                    if (stateDataMap != null && stateDataMap.size() > 0 && stateSpinner.getSelectedItemPosition() != 0) {
//                        CommonConstants.stateId = new ArrayList<>(stateDataMap.keySet()).get(position - 1);
//                        Pair pair = stateDataMap.get(CommonConstants.stateId);
//                        CommonConstants.stateCode = pair.first.toString();
//                        CommonConstants.stateName = pair.second.toString();
//
//                        loadDistricts(CommonConstants.stateId);
//                    }
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
//        ArrayAdapter<String> spinnerStateArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(stateDataMap, "State"));
//        spinnerStateArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        stateSpinner.setAdapter(spinnerStateArrayAdapter);
    }

    private void loadDistricts(String stateId) {
        Log.d("DEBUG", "Loading Districts for StateId: " + stateId);

        // Reset dependent data/maps/spinners first
        mandalDataMap = null;
        villagesDataMap = null;
        mandalSpinner.setAdapter(null);
        villageSpinner.setAdapter(null);
        ClusterNameTv.setText("");
        districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserDistricts(TextUtils.join(",", districtIds),stateId));
      //  districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getDistrictQuery(stateId));
        Log.d("DEBUG", "District Map Size: " + (districtDataMap != null ? districtDataMap.size() : "null"));


        if (districtDataMap != null && districtDataMap.size() == 1) {


            String districtId = new ArrayList<>(districtDataMap.keySet()).get(0);
            Pair pair = districtDataMap.get(districtId);

            CommonConstants.districtIdPlot = districtId;
            CommonConstants.districtCode = pair.first.toString();
            CommonConstants.districtName = pair.second.toString();

            Log.d("DEBUG", "Auto-select district: " + CommonConstants.districtName);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(adapter);
            // Find the correct index in districtList
            int position = adapter.getPosition(CommonConstants.districtName);

            if (position != -1) {
                districtSpinner.setSelection(position); // auto-binds value
            }

            // Optionally disable spinner
            // districtSpin.setEnabled(false);

            // Load mandals
          loadMandals(districtId);
        }





        else if (districtDataMap != null && districtDataMap.size() > 1) {
            // Multiple districts – allow selection
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(adapter);
            Log.d("DEBUG", "District spinner set with multiple items.");

            districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                    if (position == 0) {
//                        mandalSpinner.setAdapter(null);
//                        villageSpinner.setAdapter(null);
//                        ClusterNameTv.setText("");
//                        CommonConstants.districtIdPlot = null;
//                        return;
//                    }

                    if (userDisSelect) {
                        userDisSelect = false;
                    } else if (districtDataMap != null && districtDataMap.size() > 0 && districtSpinner.getSelectedItemPosition() != 0) {
                        CommonConstants.districtIdPlot = districtDataMap.keySet().toArray(new String[districtDataMap.size()])[position - 1];
                        Log.v(LOG_TAG, "@@@ Selected plot District: " + CommonConstants.districtIdPlot);
//                        mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().setUserMandals(TextUtils.join(",", mandalIds), CommonConstants.districtIdPlot));
//                        ArrayAdapter<String> spinnerMandalArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
//                        spinnerMandalArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        mandalSpinner.setAdapter(spinnerMandalArrayAdapter);
                        CommonConstants.districtName = districtSpinner.getSelectedItem().toString();
                        Pair districtPair = districtDataMap.get(CommonConstants.districtIdPlot);
                        CommonConstants.districtCodePlot = districtPair.first.toString();
                        Log.d("DEBUG", "Selected district: " + CommonConstants.districtIdPlot + " | " + CommonConstants.districtName);

                        loadMandals(CommonConstants.districtIdPlot);
                    }
                    else{
                        mandalSpinner.setAdapter(null);
                        villageSpinner.setAdapter(null);
                        ClusterNameTv.setText("");
                        pincode.setText("");
                        plotCodeTxt.setText("");
                        CommonConstants.districtIdPlot = null;
                    }



                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else {
            Log.d("DEBUG", "No districts found for stateId: " + stateId);
            districtSpinner.setAdapter(null);  // Clear spinner
        }
    }

    private void loadMandals(String districtId) {
        villagesDataMap = null;
        villageSpinner.setAdapter(null);
        ClusterNameTv.setText("");
      // mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getMandalsQuery(districtId));
        mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserMandals(TextUtils.join(",", mandalIds), districtId));

        if (mandalDataMap != null && mandalDataMap.size() == 1) {
            String key = new ArrayList<>(mandalDataMap.keySet()).get(0);
            Pair pair = mandalDataMap.get(key);

            CommonConstants.mandalIdPlot = key;
            CommonConstants.mandalCodePlot = pair.first.toString();
            CommonConstants.mandalName = pair.second.toString();

            Log.d("DEBUG", "Auto-selected mandal: " + CommonConstants.mandalId + " | " + CommonConstants.mandalName);

            // Bind adapter with single item so it shows in spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mandalSpinner.setAdapter(adapter);

            int position = adapter.getPosition(CommonConstants.mandalName);

            if (position != -1) {
                mandalSpinner.setSelection(position); // auto-binds value
            }
            loadVillages(key);
        } else if (mandalDataMap != null && mandalDataMap.size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mandalSpinner.setAdapter(adapter);

            mandalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (position == 0) {
//                        villageSpinner.setAdapter(null);
//                        ClusterNameTv.setText("");
//                        CommonConstants.mandalIdPlot = null;
//                        return;
//                    }
                    if (userManSelect) {
                        userManSelect = false;
                    } else if (mandalDataMap != null && mandalDataMap.size() > 0 && mandalSpinner.getSelectedItemPosition() != 0) {
                        CommonConstants.mandalIdPlot = mandalDataMap.keySet().toArray(new String[mandalDataMap.size()])[position - 1];
                        Log.v(LOG_TAG, "@@@ Selected plot mandal " + CommonConstants.mandalIdPlot);
                        CommonConstants.mandalName = mandalSpinner.getSelectedItem().toString();
                        Pair mandalPair = mandalDataMap.get(CommonConstants.mandalIdPlot);
                        CommonConstants.mandalCodePlot = mandalPair.first.toString();
                        CommonConstants.prevMandalPos = position;
                        loadVillages(CommonConstants.mandalIdPlot);
//                        villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().setUserVillages(TextUtils.join(",", userVillages), CommonConstants.mandalIdPlot));
//
//                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
//                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        villageSpinner.setAdapter(spinnerArrayAdapter);
                    }
else{

                        villageSpinner.setAdapter(null);
                        ClusterNameTv.setText("");
                        pincode.setText("");
                        plotCodeTxt.setText("");
                        CommonConstants.mandalIdPlot = null;
                    }


                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        } else {
            mandalSpinner.setAdapter(null);
            Log.d("DEBUG", "No mandals found for districtId: " + districtId);
        }
    }

    private void loadVillages(String mandalId) {
       // villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getVillagesQuery(mandalId));
        villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUserVillages(TextUtils.join(",", userVillages), mandalId));

        if (villagesDataMap != null && villagesDataMap.size() == 1) {
            String key = new ArrayList<>(villagesDataMap.keySet()).get(0);
            Pair pair = villagesDataMap.get(key);

            CommonConstants.villageIdPlot = key;
            CommonConstants.villageCodePlot = pair.first.toString();
            CommonConstants.villageName = pair.second.toString();

            Log.d("DEBUG", "Auto-selected village: " + CommonConstants.villageId + " | " + CommonConstants.villageName);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            villageSpinner.setAdapter(adapter);

            int position = adapter.getPosition(CommonConstants.villageName);
            if (position != -1) {
                villageSpinner.setSelection(position); // auto-select
            }

            // ✅ Add this logic even if only one village exists
            CommonConstants.villageIdPlot = key;
            villageCodeStr = CommonConstants.villageName;
            CommonConstants.villageCodePlot = CommonConstants.villageCode;
            CommonConstants.prevVillagePos = position;

            if (!CommonUtils.isFromFollowUp() && !CommonUtils.isFromConversion() && !CommonUtils.isFromCropMaintenance()) {
                financalSubStringYear = String.valueOf(financialYear).substring(2, 4);
                CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(
                        Queries.getInstance().getMaxNumberForPlotQuery(financalSubStringYear + days),
                        financalYrDays
                );
            }

            plotCodeTxt.setText(CommonConstants.PLOT_CODE);

            String clusterName = dataAccessHandler.getOnlyOneValueFromDb(
                    Queries.getInstance().getClusterName(CommonConstants.villageIdPlot)
            );
            ClusterNameTv.setText("" + clusterName);

            setPinCode();
        }
 else if (villagesDataMap != null && villagesDataMap.size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            villageSpinner.setAdapter(adapter);

            villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (userVillageSelect) {
                        userVillageSelect = false;
                    } else if (villagesDataMap != null && villagesDataMap.size() > 0 && villageSpinner.getSelectedItemPosition() != 0) {
                        CommonConstants.villageIdPlot = villagesDataMap.keySet().toArray(new String[villagesDataMap.size()])[position - 1];
                        villageCodeStr = villageSpinner.getSelectedItem().toString();
                        Pair villagePair = villagesDataMap.get(CommonConstants.villageIdPlot);
                        CommonConstants.villageCodePlot = villagePair.first.toString();
                        CommonConstants.villageName = villageSpinner.getSelectedItem().toString();
                        CommonConstants.prevVillagePos = position;
                        if (!CommonUtils.isFromFollowUp() && !CommonUtils.isFromConversion() && !CommonUtils.isFromCropMaintenance()) {
                            financalSubStringYear = String.valueOf(financialYear).substring(2, 4);
                            CommonConstants.PLOT_CODE = dataAccessHandler.getGeneratedPlotId(Queries.getInstance().getMaxNumberForPlotQuery(financalSubStringYear + days), financalYrDays);
                        }
                        plotCodeTxt.setText(CommonConstants.PLOT_CODE);
                        String ClusterName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getClusterName(CommonConstants.villageIdPlot));
                        ClusterNameTv.setText("" + ClusterName);
                        setPinCode();
                    }
                    else{
                        ClusterNameTv.setText("");
                        pincode.setText("");
                        plotCodeTxt.setText("");
                       CommonConstants.villageIdPlot = null;
                    }
//                    CommonConstants.villageId = new ArrayList<>(villagesDataMap.keySet()).get(position - 1);
//                    Pair pair = villagesDataMap.get(CommonConstants.villageId);
//                    CommonConstants.villageCode = pair.first.toString();
//                    CommonConstants.villageName = pair.second.toString();


                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        } else {
            villageSpinner.setAdapter(null);
            Log.d("DEBUG", "No villages found for mandalId: " + mandalId);
        }
    }



    public boolean validateDoubles() {
        if (!TextUtils.isEmpty(plotAreaStr) && !plotAreaStr.equalsIgnoreCase(".") && !TextUtils.isEmpty(totalAreaStr) && !totalAreaStr.equalsIgnoreCase(".")) {
            try {
                plotArea = Double.parseDouble(plotAreaStr);
                totalArea = Double.parseDouble(totalAreaStr);
                if (plotArea >= totalArea) {
                    getAreaLeft(plotArea, totalArea);
                } else {
//                    Toast.makeText(getActivity(), "Total Area Under Palm Should Not Exceed Plot Area", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Total Area Under Palm Should Not Exceed Plot Area", getActivity(), 1);
                    return false;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(plotAreaStr) && !plotAreaStr.equalsIgnoreCase(".")) {
            plotArea = Double.parseDouble(plotAreaStr);
            getAreaLeft(plotArea, 0.0);
        }

        return true;
    }

    public double getAreaLeft(final Double plotAreaDouble, final Double totalAreaDouble) {
        double diff = plotAreaDouble - totalAreaDouble;
        return diff;
    }
    private void savePictureData(String imageLocation) {
        if (imageLocation == null || imageLocation.trim().isEmpty()) {
            return;
        }

        savedPictureData = new FileRepository();
        savedPictureData.setFarmercode(CommonConstants.FARMER_CODE);
        savedPictureData.setPlotcode(CommonConstants.PLOT_CODE);
        savedPictureData.setModuletypeid(CommonConstants.plotDetailsModuleTypeId); // 844
        savedPictureData.setFilename(CommonConstants.PLOT_CODE);
        savedPictureData.setPicturelocation(imageLocation);
        savedPictureData.setFileextension(CommonConstants.JPEG_FILE_SUFFIX);
        savedPictureData.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        savedPictureData.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setServerUpdatedStatus(0);
        savedPictureData.setIsActive(1);
        savedPictureData.setCropMaintenanceCode(null);
        savedPictureData.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        // Get existing images list from DataManager
        List<FileRepository> imageList = (List<FileRepository>) DataManager
                .getInstance()
                .getDataFromManager(DataManager.PLOT_DETAILS_IMAGES);

        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        // Check if image already exists
        boolean isUpdated = false;
        for (int i = 0; i < imageList.size(); i++) {
            FileRepository existing = imageList.get(i);
            if (existing.getPlotcode().equals(CommonConstants.PLOT_CODE) &&
                    existing.getFilename().equals(CommonConstants.PLOT_CODE) &&
                    existing.getPicturelocation().equals(imageLocation)) {
                // Already exists, skip adding
                isUpdated = true;
                break;
            }
        }

        // Add only if not already present
        if (!isUpdated) {
            imageList.add(savedPictureData);
        }

        DataManager.getInstance().addData(DataManager.PLOT_DETAILS_IMAGES, imageList);
    }

 /*   private void savePictureData(String imageLocation) {
        savedPictureData=new FileRepository();
        savedPictureData.setFarmercode(CommonConstants.FARMER_CODE);
        savedPictureData.setPlotcode(CommonConstants.PLOT_CODE);
        savedPictureData.setModuletypeid(CommonConstants.plotDetailsModuleTypeId); // 844
        savedPictureData.setFilename(CommonConstants.PLOT_CODE);
        savedPictureData.setPicturelocation(imageLocation);
        savedPictureData.setFileextension(CommonConstants.JPEG_FILE_SUFFIX);
        savedPictureData.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        savedPictureData.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setServerUpdatedStatus(0);
        savedPictureData.setIsActive(1);
        savedPictureData.setCropMaintenanceCode(null);
        savedPictureData.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//        DataManager.getInstance().addData(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH, savedPictureData);

        List<FileRepository> imageList = (List<FileRepository>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_DETAILS_IMAGES);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        imageList.add(savedPictureData);
        DataManager.getInstance().addData(DataManager.PLOT_DETAILS_IMAGES, imageList);
    }
*/

}


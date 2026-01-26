package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.isFromConversion;
import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.isFromCropMaintenance;
import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.isFromFollowUp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CommonUtilsNavigation;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.SoilResource;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


//Soil/Power Type Entry Fragment
public class SoilTypeFragment extends Fragment implements SoilTypeAdapter.OnCartChangedListener, EditEntryDialogFragment.OnDataEditChangeListener {

    private View rootView;
    private ActionBar actionBar;
    private Spinner soiltype, plotprioritizationSpin, typeofirrigationSpin,soilNatureType,irrigationRecSpn, soilClassificationSpinner;
    private EditText noofhourspowerEdit, water_commentsEdit,irrigatedArea;
    private Button  irigationSaveBtn, saveBtn,soilhistoryBtn;
    private RecyclerView mRecyclerView,mRecyclerView2;
    private SoilTypeAdapter mSoilTypeAdapter,mSoilTypeAdapter1;
    private SoilIrrigationAdapter SoilTypeAdapter;
    private int selectedPosition = 0;
    private Spinner powerAvailSpin;
    private LinkedHashMap<String, String> soilTypeMap, typeofirrigationMap, plotPrioritizationMap,soilNatureTypeMap, soilClassification,typeofirrigationMap2;
    private DataAccessHandler dataAccessHandler;
    private SoilResource msoilTypeModel;
    private ArrayList<PlotIrrigationTypeXref> msoilTypeIrrigationModelList = new ArrayList<>();
    private ArrayList<PlotIrrigationTypeXref> Irrigationdata = new ArrayList<>();
    ArrayList<PlotIrrigationTypeXref> soilTypeIrrigationModelList = new ArrayList<>();
    private boolean updateFromDb;
    private UpdateUiListener updateUiListener;
    private ArrayList<SoilResource> SoilResourcelastvisitdatamap;
    LinearLayout pridripCompanyLL,SecdripCompanyLL,dripexistLL,dripexistyesLL,dripexistnoLL;
    Spinner dripcmneySpin,dripcmney2Spin,dripexist,existdripcmneySpin,watwrpumpSpin;
    private LinkedHashMap<String, String> primaryDripCompanyMap, SecondaryDripCompanyMap,dripcompanyMap,PumpTypeMap;
    String selectedPriDripCompany,selectedsecDripCompany,selectedDripCompany,selectePumptype, selectedExistedDripCompany;
    int isDrip = 0;
    EditText HPIDnum,capacity,DripInstalledDate;
    PlotIrrigationTypeXref msoilTypeIrrigationModel;
    String dripInstalledStatus;
    String selectedpump_type,selecteDripCompanyId,selectedPriDripCompanyId,selectedsecDripCompanyId,selectePumpid;
    private Calendar myCalendar = Calendar.getInstance();
    private int selectedSoilClassificationId = -1;
    private String selectedTypeIrrigationId;
    List<PlotIrrigationTypeXref> savedIrrigationList;
    LinearLayout nohours_lyt;
    List<String> allIrrigationTypes = new ArrayList<>();
    public SoilTypeFragment() {
        // Required empty public constructor
    }

    // Flag to avoid first-time auto trigger
    final boolean[] isUserInteracted = {false};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_soil_type, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(activity.getResources().getString(R.string.water_soil_power_details));

        initViews();
        setViews();

        return rootView;
    }

    private void initViews() {
        dataAccessHandler = new DataAccessHandler(getActivity());

        soiltype = (Spinner) rootView.findViewById(R.id.soiltype);
        plotprioritizationSpin = (Spinner) rootView.findViewById(R.id.plotprioritizationSpin);
        typeofirrigationSpin = (Spinner) rootView.findViewById(R.id.typeofirrigationSpin);
        powerAvailSpin = (Spinner) rootView.findViewById(R.id.poweravailSpin);
        noofhourspowerEdit = (EditText) rootView.findViewById(R.id.noofhourspowerEdit);
        water_commentsEdit = (EditText) rootView.findViewById(R.id.water_commentsEdit);
        irigationSaveBtn = (Button) rootView.findViewById(R.id.irigationSaveBtn);
        saveBtn = (Button) rootView.findViewById(R.id.saveBtn);
        soilhistoryBtn = (Button) rootView.findViewById(R.id.soilhistoryBtn);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        mRecyclerView2 = (RecyclerView) rootView.findViewById(R.id.recycler2);
        irrigatedArea = rootView.findViewById(R.id.irrigatedArea);
        soilNatureType = rootView.findViewById(R.id.soilNatureType);
        irrigationRecSpn = rootView.findViewById(R.id.irrigationRecSpn);
        soilClassificationSpinner = rootView.findViewById(R.id.soilClassificationSpinner);
        pridripCompanyLL = rootView.findViewById(R.id.pridripCompanyLL);
        SecdripCompanyLL = rootView.findViewById(R.id.SecdripCompanyLL);
        soilhistoryBtn.setVisibility(isFromCropMaintenance() ? View.VISIBLE : View.GONE);
        dripcmneySpin = rootView.findViewById(R.id.dripcmneySpin);
        dripcmney2Spin = rootView.findViewById(R.id.dripcmney2Spin);
        dripexist = rootView.findViewById(R.id.dripexist);
        dripexistyesLL = rootView.findViewById(R.id.dripexistyesLL);
        dripexistnoLL = rootView.findViewById(R.id.dripexistnoLL);
        dripexistLL = rootView.findViewById(R.id.dripexistLL);
        HPIDnum = rootView.findViewById(R.id.HPIDnum);
        existdripcmneySpin = rootView.findViewById(R.id.existdripcmneySpin);
        capacity = rootView.findViewById(R.id.capacity);
        watwrpumpSpin = rootView.findViewById(R.id.watwrpumpSpin);
        DripInstalledDate = rootView.findViewById(R.id.DripInstalledDate);
        nohours_lyt = (LinearLayout) rootView.findViewById(R.id.nohours_lyt);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews() {
        soilTypeMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("35"));
        soilNatureTypeMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("54"));
        typeofirrigationMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("36"));

        plotPrioritizationMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("37"));
        soilClassification = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("143"));


        soiltype.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Soil Type", soilTypeMap));
        soilNatureType.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Soil Nature Type", soilNatureTypeMap));
        plotprioritizationSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Prioritization", plotPrioritizationMap));
        selectedPosition = 0;

        allIrrigationTypes.add("Select"); // first default item
        allIrrigationTypes.addAll(typeofirrigationMap.values());

        powerAvailSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(powerAvailSpin.getSelectedItemPosition() == 1){
                    nohours_lyt.setVisibility(View.VISIBLE);
                }else {
                    nohours_lyt.setVisibility(View.GONE);
                    noofhourspowerEdit.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        typeofirrigationSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Type", typeofirrigationMap));
        Log.d("xxx", soilClassification.toString());
        soilClassificationSpinner.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Soil Classification", soilClassification));
        irrigationRecSpn.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Type", typeofirrigationMap));

        typeofirrigationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                dripexist.setSelection(0);
                capacity.setText("");
                HPIDnum.setText("");
                DripInstalledDate.setText("");
                existdripcmneySpin.setSelection(0);
                String selectedType1 = typeofirrigationSpin.getSelectedItem().toString().trim();
                String selectedType2 = irrigationRecSpn.getSelectedItem() != null ? irrigationRecSpn.getSelectedItem().toString().trim() : "";

                String selectedTypeIrrigationId = CommonUtils.getKeyFromValue(typeofirrigationMap, selectedType1);
                String selectedRecId = CommonUtils.getKeyFromValue(typeofirrigationMap, selectedType2);

                String dripError = CommonUiUtils.DripValidationError(getActivity()); // Check if drip is mandatory
                boolean isDripMandatory = !(dripError == null || dripError.isEmpty());
                Log.e("====selectedType1",selectedType1+"==selectedType2"+selectedType2 +"isDripMandatory" +isDripMandatory);
                // 🚫 Disallow same selection only if Drip is mandatory or selection is not both "None"

                if (position1 > 0 && selectedType1.equals(selectedType2)) {
                    if (!isDripMandatory && "None".equalsIgnoreCase(selectedType1)) {
                        // Allow same "None" selection if drip is not mandatory
                    } else {
                        UiUtils.showCustomToastMessage("Same selection not allowed in both fields", getContext(), 1);
                        typeofirrigationSpin.setSelection(0);
                        return;
                    }
                }

                if (position1 == 0) {
                    dripexistLL.setVisibility(View.GONE);
                    dripexistLL.setVisibility(View.GONE);
                    dripexistyesLL.setVisibility(View.GONE);
                }

                // 💧 Drip layout handling
             else   if (position1 > 0) {
                    if ("391".equals(selectedTypeIrrigationId)) {
                        dripexistLL.setVisibility(View.VISIBLE);
                        dripexistyesLL.setVisibility(View.VISIBLE);
                        dripexist.setSelection(1);
                        dripexist.setEnabled(false);
                    } else if ("391".equals(selectedRecId)) {
                        dripexistLL.setVisibility(View.VISIBLE);
                        dripexistnoLL.setVisibility(View.VISIBLE);
                        dripexist.setSelection(2);
                        dripexist.setEnabled(false);
                    } else {
                        dripexistLL.setVisibility(View.GONE);
                        dripexistyesLL.setVisibility(View.GONE);
                        dripexist.setEnabled(true);
                        dripexist.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // RECOMMENDED IRRIGATION SPINNER
        irrigationRecSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position2, long id) {
                String selectedType2 = irrigationRecSpn.getSelectedItem().toString().trim();
                dripcmneySpin.setSelection(0);
                watwrpumpSpin.setSelection(0);
                capacity.setText("");
                selectedPriDripCompanyId = null;
                selectedsecDripCompanyId = null;
                dripcmney2Spin.setSelection(0);
                dripcmneySpin.setSelection(0);
                selectePumpid = null;
                String selectedType1 = typeofirrigationSpin.getSelectedItem() != null ? typeofirrigationSpin.getSelectedItem().toString().trim() : "";

                String selectedRecId = CommonUtils.getKeyFromValue(typeofirrigationMap, selectedType2);
                String selectedTypeIrrigationId = CommonUtils.getKeyFromValue(typeofirrigationMap, selectedType1);

                String dripError = CommonUiUtils.DripValidationError(getActivity());
                boolean isDripMandatory = !(dripError == null || dripError.isEmpty());
                Log.e("====selectedType1",selectedType1+"==selectedType2"+selectedType2 +"isDripMandatory" +isDripMandatory);
                // 🚫 Disallow same selection only if Drip is mandatory or selection is not both "None"
                if (position2 > 0 && selectedType2.equals(selectedType1)) {
                    if (!isDripMandatory && "None".equalsIgnoreCase(selectedType2)) {
                        // Allow same "None" selection if drip is not mandatory
                    } else {
                        UiUtils.showCustomToastMessage("Same selection not allowed in both fields", getContext(), 1);
                        irrigationRecSpn.setSelection(0);
                        return;
                    }
                }

                // 💧 Drip layout handling
                 if (position2 > 0) {
                    if ("391".equals(selectedTypeIrrigationId)) {
                        dripexistLL.setVisibility(View.VISIBLE);
                        dripexistyesLL.setVisibility(View.VISIBLE);
                        dripexist.setSelection(1);
                        dripexist.setEnabled(false);
                    } else if ("391".equals(selectedRecId)) {
                        dripexistLL.setVisibility(View.VISIBLE);
                        dripexistnoLL.setVisibility(View.VISIBLE);
                        dripexist.setSelection(2);
                        dripexist.setEnabled(false);
                    } else {
                        dripexistLL.setVisibility(View.GONE);
                        dripexistyesLL.setVisibility(View.GONE);
                        dripexistnoLL.setVisibility(View.GONE);
                        dripexist.setSelection(2);
                        dripexist.setEnabled(true);
                    }
                }
                 else{
                     dripexistLL.setVisibility(View.GONE);
                     dripexistyesLL.setVisibility(View.GONE);
                     dripexistnoLL.setVisibility(View.GONE);
                     dripexist.setSelection(2);
                     dripexist.setEnabled(true);
                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        soilClassificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                String selectedValue = parent.getItemAtPosition(position1).toString().trim();
                String selectedType2 = irrigationRecSpn.getSelectedItem() != null
                        ? irrigationRecSpn.getSelectedItem().toString().trim()
                        : "";

                // 🔍 Reverse lookup: find the key for selectedValue
                for (Map.Entry<String, String> entry : soilClassification.entrySet()) {
                    if (entry.getValue().equalsIgnoreCase(selectedValue)) {
                        selectedSoilClassificationId = Integer.parseInt(entry.getKey());
                        break;
                    }
                }

                Log.d("xxx", selectedValue + " | ID: " + selectedSoilClassificationId);

                if (position1 > 0 && selectedValue.equalsIgnoreCase(selectedType2)) {
                    UiUtils.showCustomToastMessage("Same selection not allowed in both fields", getContext(), 1);
                    soilClassificationSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dripcmneySpin.setOnTouchListener((v, event) -> {
            isUserInteracted[0] = true;
            return false;
        });

        dripexist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = dripexist.getSelectedItem().toString();
                updateDripVisibility(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dripexistyesLL.setVisibility(View.GONE);
                dripexistnoLL.setVisibility(View.GONE);
            }
        });
        primaryDripCompanyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
        PumpTypeMap = dataAccessHandler.getGenericData(Queries.getInstance().getPumpType());
        msoilTypeIrrigationModelList = (ArrayList<PlotIrrigationTypeXref>)
                DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

        Log.d("IrrigationCheck", "msoilTypeIrrigationModelList size: " +
                (msoilTypeIrrigationModelList != null ? msoilTypeIrrigationModelList.size() : "null"));
        if (msoilTypeIrrigationModelList != null && !msoilTypeIrrigationModelList.isEmpty() && msoilTypeIrrigationModelList.size() != 0) {
            mSoilTypeAdapter = new SoilTypeAdapter(
                    getActivity(),
                    msoilTypeIrrigationModelList,
                    typeofirrigationMap,
                    primaryDripCompanyMap,
                    PumpTypeMap,true);
            mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false));
            mRecyclerView2.setAdapter(mSoilTypeAdapter);
            mSoilTypeAdapter.setOnCartChangedListener(this);
        }

        // Get data from DB
        Irrigationdata = (ArrayList<PlotIrrigationTypeXref>)
                dataAccessHandler.getPlotIrrigationXRefData(
                        Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);

        Log.d("IrrigationCheck", "Irrigationdata size from DB: " +
                (Irrigationdata != null ? Irrigationdata.size() : "null"));

        if (Irrigationdata != null && !Irrigationdata.isEmpty() && Irrigationdata.size() != 0) {
            mSoilTypeAdapter1 = new SoilTypeAdapter(
                    getActivity(),
                    Irrigationdata,
                    typeofirrigationMap,
                    primaryDripCompanyMap,
                    PumpTypeMap,false);
            mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
            );
            mRecyclerView.setAdapter(mSoilTypeAdapter1);
            mSoilTypeAdapter1.setOnCartChangedListener(this);
        }

        else {
            Log.e("IrrigationCheck", "No irrigation data found in DataManager or DB!");
        }

        msoilTypeModel = (SoilResource) DataManager.getInstance().getDataFromManager(DataManager.SoilType);
        if (msoilTypeModel == null && (isFromFollowUp() || isFromCropMaintenance() || isFromConversion())) {
            updateFromDb = true;
            msoilTypeModel = (SoilResource) dataAccessHandler.getSoilResourceData(Queries.getInstance().getSoilResourceBinding(CommonConstants.PLOT_CODE), 0);
        }

        if (msoilTypeModel != null) {
            soiltype.setSelection(CommonUtilsNavigation.getvalueFromHashMap(soilTypeMap, msoilTypeModel.getSoiltypeid()));
            powerAvailSpin.setSelection((null != msoilTypeModel.getIspoweravailable() && msoilTypeModel.getIspoweravailable() == 1) ? 1 : 2);
            double hours = msoilTypeModel.getAvailablepowerhours();
            String displayValue = (hours % 1 == 0)
                    ? String.valueOf((int) hours)   // show "3" instead of "3.0"
                    : String.valueOf(hours);        // keep "3.5" as it is
            noofhourspowerEdit.setText(displayValue);

            plotprioritizationSpin.setSelection(msoilTypeModel.getPrioritizationtypeid() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(plotPrioritizationMap, msoilTypeModel.getPrioritizationtypeid()));
            water_commentsEdit.setText(msoilTypeModel.getComments());
            irrigatedArea.setText("" + msoilTypeModel.getIrrigatedArea());
            soilNatureType.setSelection(CommonUtilsNavigation.getvalueFromHashMap(soilNatureTypeMap, msoilTypeModel.getSoilNatureId()));
            soilClassificationSpinner.setSelection(CommonUtilsNavigation.getvalueFromHashMap(soilClassification, msoilTypeModel.getSoilClassificationId()));
        }
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        DripInstalledDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        });

        irigationSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SAVE_CLICK", "Save button clicked");
                try {
                    String dripError = CommonUiUtils.DripValidationError(getActivity());
                    boolean isDripMandatory = !(dripError == null || dripError.isEmpty());
                    // Validate selections
                    if (typeofirrigationSpin.getSelectedItemPosition() == 0 ) {
                        UiUtils.showCustomToastMessage("Please Select Irrigation Type", getContext(), 1);
                        return;
                    }

                    if (
                            irrigationRecSpn.getSelectedItemPosition() == 0) {
                        UiUtils.showCustomToastMessage("Please Select Recommended Irrigation", getContext(), 1);
                        return;
                    }


                    String selectedIrrigationType = typeofirrigationSpin.getSelectedItem().toString().trim();
                    String selectedRecommendedIrrigationType = irrigationRecSpn.getSelectedItem().toString().trim();
                    // Prevent same selection in both spinners
                    if (selectedIrrigationType.equalsIgnoreCase(selectedRecommendedIrrigationType)) {
                        if (!isDripMandatory && selectedIrrigationType.equalsIgnoreCase("None")) {
                            // Allow if both are "None" and drip is not mandatory
                        } else {
                            UiUtils.showCustomToastMessage("Same selection is not allowed in both fields", getContext(), 1);
                            return;
                        }
                    }

                    String dripIrrigationId = CommonUtilsNavigation.getKey(typeofirrigationMap, selectedIrrigationType);
                    String recIrrigationId = CommonUtilsNavigation.getKey(typeofirrigationMap, selectedRecommendedIrrigationType);

                    // Validate keys
                    if (TextUtils.isEmpty(dripIrrigationId) || TextUtils.isEmpty(recIrrigationId)) {
                        UiUtils.showCustomToastMessage("Invalid irrigation type selection", getContext(), 1);
                        return;
                    }
                    List<PlotIrrigationTypeXref> savedIrrigationList =
                            (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

                    if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
                        savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>)
                                dataAccessHandler.getPlotIrrigationXRefData(
                                        Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);
                    }

                    if (savedIrrigationList != null) {
                        for (PlotIrrigationTypeXref model : savedIrrigationList) {

                            Log.d("IrrigationCheck", "Model IrrType=" + model.getIrrigationtypeid() +
                                    ", Model RecIrr=" + model.getRecmIrrgId() +
                                    ", Selected Rec=" + recIrrigationId +
                                    ", Selected Drip=" + dripIrrigationId);

                            // Check if selected recommended type already used as irrigation type (except "None" i.e. 500)
                            if (!recIrrigationId.equals("500") &&
                                    model.getIrrigationtypeid() == Integer.parseInt(recIrrigationId)) {
                                UiUtils.showCustomToastMessage("Selected recommended irrigation type is already used as irrigation type", getContext(), 1);
                                return;
                            }
                            if (!dripIrrigationId.equals("500") && model.getIrrigationtypeid() == Integer.parseInt(dripIrrigationId)) {
                                UiUtils.showCustomToastMessage("Selected recommended irrigation type is already used as irrigation type", getContext(), 1);
                                return;
                            }
                            if (!recIrrigationId.equals("500") &&
                                    model.getRecmIrrgId() != null &&
                                    model.getRecmIrrgId().equals(Integer.parseInt(recIrrigationId))) {
                                UiUtils.showCustomToastMessage("Selected irrigation type is already used as recommended irrigation", getContext(), 1);
                                return;
                            }
                            // Check if selected irrigation type already used as recommended type (except "None" i.e. 500)
                            if (!recIrrigationId.equals("500") &&
                                    model.getRecmIrrgId() != null &&
                                    model.getRecmIrrgId().equals(Integer.parseInt(dripIrrigationId))) {
                                UiUtils.showCustomToastMessage("Selected irrigation type is already used as recommended irrigation", getContext(), 1);
                                return;
                            }
                        }
                    }

                    // Prepare values from spinners
                    selectedDripCompany = existdripcmneySpin.getSelectedItem().toString().trim();
                    selecteDripCompanyId = CommonUtils.getKeyFromValue(dripcompanyMap, selectedDripCompany);

                    selectedPriDripCompany = dripcmneySpin.getSelectedItem().toString().trim();
                    selectedPriDripCompanyId = CommonUtils.getKeyFromValue(primaryDripCompanyMap, selectedPriDripCompany);

                    selectedsecDripCompany = dripcmney2Spin.getSelectedItem().toString().trim();
                    selectedsecDripCompanyId = CommonUtils.getKeyFromValue(SecondaryDripCompanyMap, selectedsecDripCompany);

                    selectePumptype = watwrpumpSpin.getSelectedItem().toString().trim();
                    selectePumpid = CommonUtils.getKeyFromValue(PumpTypeMap, selectePumptype);

                    String dripInstalledStatus = dripexist.getSelectedItem() != null ?
                            dripexist.getSelectedItem().toString().trim() : "";

                    // ✅ Prepare the model
                    PlotIrrigationTypeXref msoilTypeIrrigationModel = new PlotIrrigationTypeXref();
                    msoilTypeIrrigationModel.setIrrigationtypeid(Integer.parseInt(dripIrrigationId));
                    msoilTypeIrrigationModel.setName(selectedIrrigationType);
                    msoilTypeIrrigationModel.setRecmIrrgId(Integer.parseInt(recIrrigationId));
                    Log.e("===>dripInstalledStatus",dripInstalledStatus);
                    if ("391".equals(recIrrigationId) || "391".equals(dripIrrigationId)) {
                        // ✅ Drip-related logic


                        if (dripInstalledStatus.equalsIgnoreCase("Yes")) {
                            msoilTypeIrrigationModel.setIsDripInstalled(1);

                            if (existdripcmneySpin.getSelectedItemPosition() == 0) {
                                UiUtils.showCustomToastMessage("Please select Existing Drip Company", getContext(), 1);
                                return;
                            }
                            if (TextUtils.isEmpty(DripInstalledDate.getText().toString().trim())) {
                                UiUtils.showCustomToastMessage("Please select Drip Installed Date", getContext(), 1);
                                return;
                            }

                            msoilTypeIrrigationModel.setHPIdNumber(HPIDnum.getText().toString().trim());
                            msoilTypeIrrigationModel.setDripInstalledDate(DripInstalledDate.getText().toString().trim());

                            if (!TextUtils.isEmpty(selecteDripCompanyId)) {
                                msoilTypeIrrigationModel.setPrimaryCompanyId(Integer.parseInt(selecteDripCompanyId));
                            }

                        } else if (dripInstalledStatus.equalsIgnoreCase("No")) {
                            // ✅ No case validations
                            if (dripcmneySpin.getSelectedItemPosition() == 0) {
                                UiUtils.showCustomToastMessage("Please select Primary Drip Company", getContext(), 1);
                                return;
                            }
                            if (watwrpumpSpin.getSelectedItemPosition() == 0) {
                                UiUtils.showCustomToastMessage("Please select Water Pump", getContext(), 1);
                                return;
                            }
                            if (TextUtils.isEmpty(capacity.getText().toString())) {
                                UiUtils.showCustomToastMessage("Please enter Capacity", getContext(), 1);
                                return;
                            }
                            if (TextUtils.isEmpty(capacity.getText().toString().trim())) {
                                UiUtils.showCustomToastMessage("Please enter Capacity", getContext(), 1);
                                return;
                            }

                            String capacityStr = capacity.getText().toString().trim();
                            double capacityValue = Double.parseDouble(capacityStr);

                            if (capacityValue <= 0){
                                UiUtils.showCustomToastMessage("Capacity should be greater than 0", getActivity(), 1);
                                return;
                            }

                            // ✅ Set values for No
                            msoilTypeIrrigationModel.setIsDripInstalled(0);

                            if (!TextUtils.isEmpty(selectedPriDripCompanyId)) {
                                msoilTypeIrrigationModel.setPrimaryCompanyId(Integer.parseInt(selectedPriDripCompanyId));
                            }
                            if (!TextUtils.isEmpty(selectedsecDripCompanyId)) {
                                msoilTypeIrrigationModel.setSecondaryCompanyId(Integer.parseInt(selectedsecDripCompanyId));
                            }
                            if (!TextUtils.isEmpty(selectePumpid)) {
                                msoilTypeIrrigationModel.setWaterPumpTypeId(Integer.parseInt(selectePumpid));
                            }

                            msoilTypeIrrigationModel.setCapacity(
                                    Double.parseDouble(capacity.getText().toString().trim()));
                        }
                    } else {
                        // ✅ Not drip
                        msoilTypeIrrigationModel.setIsDripInstalled(null);
                        msoilTypeIrrigationModel.setPrimaryCompanyId(null);
                        msoilTypeIrrigationModel.setSecondaryCompanyId(null);
                        msoilTypeIrrigationModel.setWaterPumpTypeId(null);
                    }
                    if (msoilTypeIrrigationModelList == null) {
                        msoilTypeIrrigationModelList = new ArrayList<>();
                    }

                    // Avoid duplicate add in same click
                    if (!msoilTypeIrrigationModelList.contains(msoilTypeIrrigationModel)) {
                        msoilTypeIrrigationModelList.add(msoilTypeIrrigationModel);
                    }

                    DataManager.getInstance().addData(DataManager.TypeOfIrrigation, msoilTypeIrrigationModelList);
                    mSoilTypeAdapter = new SoilTypeAdapter(getActivity(), msoilTypeIrrigationModelList,typeofirrigationMap,primaryDripCompanyMap,PumpTypeMap,true);
                    mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    mRecyclerView2.setAdapter(mSoilTypeAdapter);
                    mSoilTypeAdapter.setOnCartChangedListener(SoilTypeFragment.this);
                    mSoilTypeAdapter.notifyDataSetChanged();


                    // ✅ Reset fields
                    typeofirrigationSpin.setSelection(0);
                    irrigationRecSpn.setSelection(0);
                    dripexist.setSelection(0);
                    capacity.setText("");
                    HPIDnum.setText("");
                    DripInstalledDate.setText("");
                    dripexistLL.setVisibility(View.GONE);
                    dripexistyesLL.setVisibility(View.GONE);
                    dripexistnoLL.setVisibility(View.GONE);
                    // ✅ Filter out already used recommended irrigation types
                    List<PlotIrrigationTypeXref> savedList = (List<PlotIrrigationTypeXref>)
                            DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

                    Set<String> usedRecommendedIds = new HashSet<>();
                    if (savedList != null) {
                        for (PlotIrrigationTypeXref model : savedList) {
                            usedRecommendedIds.add(String.valueOf(model.getRecmIrrgId()));
                        }
                    }

                    List<String> filteredList = new ArrayList<>();
                    filteredList.add("--Select Type--");
                    for (Map.Entry<String, String> entry : typeofirrigationMap.entrySet()) {
                        if (!usedRecommendedIds.contains(entry.getKey())) {
                            filteredList.add(entry.getValue());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, filteredList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    irrigationRecSpn.setAdapter(adapter);

                } catch (Exception e) {
                    Log.e("IrrigationSaveError", "Exception: " + e.getMessage(), e);
                    UiUtils.showCustomToastMessage("Something went wrong: " + e.getMessage(), getContext(), 1);
                }
            }
        });

        PumpTypeMap = dataAccessHandler.getGenericData(Queries.getInstance().getPumpType());
        String[] pumpTypes = CommonUtils.fromMap(PumpTypeMap, "Water Pump");
        ArrayAdapter<String> pumpArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, pumpTypes);
        pumpArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        watwrpumpSpin.setAdapter(pumpArrayAdapter);

        selectePumptype = watwrpumpSpin.getSelectedItem().toString().trim();
        Log.e("selectePumptype", selectePumptype);
        selectedpump_type= CommonUtils.getKeyFromValue(PumpTypeMap, selectePumptype);
        Log.e("selectedpump_type", selectedpump_type);
        // 1. Existing Drip Company Spinner (when Drip is installed - YES)
        dripcompanyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
        ArrayAdapter<String> existCompanyAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(dripcompanyMap, "Company Name")
        );
        existCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        existdripcmneySpin.setAdapter(existCompanyAdapter);

        // Primary Drip Company Spinner
        primaryDripCompanyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
        ArrayAdapter<String> primaryCompanyAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(primaryDripCompanyMap, "Company Name")
        );
        primaryCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dripcmneySpin.setAdapter(primaryCompanyAdapter);
        // Detect user interaction for primary spinner

        // Primary Drip Company Validation
        dripcmneySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteracted[0]) {
                    String primaryCompany = dripcmneySpin.getSelectedItem().toString();
                    String secondaryCompany = dripcmney2Spin.getSelectedItem() != null ? dripcmney2Spin.getSelectedItem().toString() : "";

                    if (position > 0 && primaryCompany.equals(secondaryCompany)) {
                        UiUtils.showCustomToastMessage("Primary and Secondary Drip Companies must be different", getActivity(), 1);
                        dripcmneySpin.setSelection(0); // Reset to default
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
        // Secondary Drip Company Spinner
        SecondaryDripCompanyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
        ArrayAdapter<String> secondaryCompanyAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(SecondaryDripCompanyMap, "Company Name")
        );
        secondaryCompanyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dripcmney2Spin.setAdapter(secondaryCompanyAdapter);

        // Detect user interaction
        dripcmney2Spin.setOnTouchListener((v, event) -> {
            isUserInteracted[0] = true;
            return false;
        });

        dripcmney2Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteracted[0]) {
                    String primaryCompany = dripcmneySpin.getSelectedItem().toString();
                    String secondaryCompany = dripcmney2Spin.getSelectedItem().toString();

                    if (position > 0 &&primaryCompany.equals(secondaryCompany)) {
                        UiUtils.showCustomToastMessage("Primary and Secondary Drip Companies must be different", getActivity(), 1);

                        dripcmney2Spin.setSelection(0); // Reset to default
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });


        // 4. Handle Selections Later (Recommended: On Save Button Click)
        selectedDripCompany = ""; // Will be picked on Save
        selecteDripCompanyId = ""; // Will be picked on Save

        selectedPriDripCompany = ""; // Will be picked on Save
        selectedPriDripCompanyId = ""; // Will be picked on Save

        selectedsecDripCompany = ""; // Will be picked on Save
        selectedsecDripCompanyId = ""; // Will be picked on Save
        selectePumptype = "";
        selectePumpid = "";

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dripError = CommonUiUtils.DripValidationError(getActivity());

                if (!CommonUtilsNavigation.spinnerSelect("Soil Type", soiltype.getSelectedItemPosition(), getActivity())
                        || !CommonUtilsNavigation.spinnerSelect("Soil Nature Type", soilNatureType.getSelectedItemPosition(), getActivity())
                        || !CommonUtilsNavigation.spinnerSelect("Soil Classification", soilClassificationSpinner.getSelectedItemPosition(), getActivity())
                        || !CommonUtilsNavigation.spinnerSelect("Power Availability", powerAvailSpin.getSelectedItemPosition(), getActivity())
                        || !CommonUtilsNavigation.edittextSelect(getActivity(), irrigatedArea, "Irrigated area")) {
                    return;
                }

                try {

                    if (!TextUtils.isEmpty(noofhourspowerEdit.getText().toString()) && Double.parseDouble(noofhourspowerEdit.getText().toString()) > 24){
                        CommonUtils.showToast(getString(R.string.error_exceed24), getActivity());
                        return;
                    }
                    if ( Double.parseDouble(noofhourspowerEdit.getText().toString()) <= 0){
                        CommonUtils.showToast(getString(R.string.error_hour0), getActivity());
                        return;
                    }

                }catch (Exception e){

                }

                List<PlotIrrigationTypeXref> savedIrrigationList =
                        (List<PlotIrrigationTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.TypeOfIrrigation);

                Log.d("IrrigationCheck", "Step 1: From DataManager → " + (savedIrrigationList == null ? "null" : "size=" + savedIrrigationList.size()));

                if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
                    Log.d("IrrigationCheck", "Step 2: DataManager returned null/empty. Fetching from DB...");

                    savedIrrigationList = (ArrayList<PlotIrrigationTypeXref>)
                            dataAccessHandler.getPlotIrrigationXRefData(
                                    Queries.getInstance().getPlotIrrigationTypeXrefBinding(CommonConstants.PLOT_CODE), 1);

                    Log.d("IrrigationCheck", "Step 3: After DB fetch → " + (savedIrrigationList == null ? "null" : "size=" + savedIrrigationList.size()));
                }

                if (savedIrrigationList == null || savedIrrigationList.isEmpty()) {
                    Log.d("IrrigationCheck", "Step 4: Final check → No irrigation details found. Showing toast...");

                    UiUtils.showCustomToastMessage(
                            "Please Add Irrigation Details",
                            getActivity(),
                            1
                    );

                    // CommonUtilsNavigation.listEmpty(null, "Irrigation Details", getActivity());
                    return;
                } else {
                    Log.d("IrrigationCheck", "Step 5: Irrigation details found. Proceeding → size=" + savedIrrigationList.size());
                }

                if (dripError != null && !dripError.isEmpty()) {
                    UiUtils.showCustomToastMessage(dripError,getActivity(),1);
                    return;
                }

                msoilTypeModel = new SoilResource();
                msoilTypeModel.setSoiltypeid(Integer.parseInt(CommonUtilsNavigation.getKey(soilTypeMap, soiltype.getSelectedItem().toString())));
                msoilTypeModel.setIspoweravailable(powerAvailSpin.getSelectedItemPosition() == 1 ? 1 : 0);
                msoilTypeModel.setAvailablepowerhours(noofhourspowerEdit.getText().toString().length() > 0 ? Double.parseDouble(noofhourspowerEdit.getText().toString()) : 0.0);

                msoilTypeModel.setPrioritizationtypeid(plotprioritizationSpin.getSelectedItemPosition() == 0 ? null : Integer.parseInt(CommonUtilsNavigation.getKey(plotPrioritizationMap, plotprioritizationSpin.getSelectedItem().toString())));
                msoilTypeModel.setComments(water_commentsEdit.getText().toString());
                msoilTypeModel.setSoilNatureId(Integer.parseInt(CommonUtilsNavigation.getKey(soilNatureTypeMap, soilNatureType.getSelectedItem().toString())));
                msoilTypeModel.setSoilClassificationId(selectedSoilClassificationId);
                msoilTypeModel.setIrrigatedArea(Float.parseFloat(irrigatedArea.getText().toString()));
                DataManager.getInstance().addData(DataManager.SoilType, msoilTypeModel);
                soiltype.setEnabled(false);
                powerAvailSpin.setEnabled(false);
                noofhourspowerEdit.setEnabled(false);
                plotprioritizationSpin.setEnabled(false);
                water_commentsEdit.setEnabled(false);

                if (updateFromDb) {
                    DataManager.getInstance().addData(DataManager.IS_WOP_DATA_UPDATED, true);
                }
                CommonConstants.Flags.isWOPDataUpdated = true;
                updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();

            }
        });


        soilhistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getContext());
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        DripInstalledDate.setText(sdf.format(myCalendar.getTime()));

    }
    private void changeVisibilityOfdripexistnoLL(boolean isVisible) {}

    private void updateDripVisibility(String selectedValue) {
        if (selectedValue.equalsIgnoreCase("Yes")) {
            // Show "Yes" layout
            dripexistyesLL.setVisibility(View.VISIBLE);
            dripexistnoLL.setVisibility(View.GONE);

            // Clear "No" fields
            dripcmneySpin.setSelection(0);
            watwrpumpSpin.setSelection(0);
            capacity.setText("");
            selectedPriDripCompanyId = null;
            selectedsecDripCompanyId = null;
            selectePumpid = null;

        } else if (selectedValue.equalsIgnoreCase("No")) {
            // Show "No" layout
            dripexistyesLL.setVisibility(View.GONE);
            dripexistnoLL.setVisibility(View.VISIBLE);

            // Clear "Yes" fields
            HPIDnum.setText("");
            DripInstalledDate.setText("");
            existdripcmneySpin.setSelection(0);
            selecteDripCompanyId = null;

        } else {
            // Hide both layouts for default selection
            dripexistyesLL.setVisibility(View.GONE);
            dripexistnoLL.setVisibility(View.GONE);

            // Clear all fields
            HPIDnum.setText("");
            DripInstalledDate.setText("");
            existdripcmneySpin.setSelection(0);
            dripcmneySpin.setSelection(0);
            watwrpumpSpin.setSelection(0);
            capacity.setText("");

            selectedPriDripCompanyId = null;
            selectedsecDripCompanyId = null;
            selectePumpid = null;
            selecteDripCompanyId = null;
        }
    }

    public void showDialog(Context activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.soillastvisteddata);

        Toolbar titleToolbar = dialog.findViewById(R.id.titleToolbar);
        if (titleToolbar != null) {
            titleToolbar.setTitle("Soil & Power Details History");
            titleToolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));
        }

        RecyclerView irrigationRecyclerView = dialog.findViewById(R.id.irrigationRecyclerView);
        TextView soilnaturetype = dialog.findViewById(R.id.soilnaturetype);
        TextView soiltype = dialog.findViewById(R.id.soiltype);
        TextView availablehours = dialog.findViewById(R.id.availablehours);
        TextView poweravilable = dialog.findViewById(R.id.poweravilable);
        TextView plotpriortization = dialog.findViewById(R.id.plotpriortization);

        TextView irrigatedArea = dialog.findViewById(R.id.irrigatedArea);
        TextView Comments = dialog.findViewById(R.id.Comments);

        TextView norecords = dialog.findViewById(R.id.intercropnorecord_tv);
        LinearLayout mainLL = dialog.findViewById(R.id.intercropmainlyt);

        String lastVisitCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getLatestCropMaintanaceHistoryCode(CommonConstants.PLOT_CODE)
        );
        android.util.Log.e("lastVisitCode", lastVisitCode + "");
        SoilResourcelastvisitdatamap = (ArrayList<SoilResource>) dataAccessHandler.getSoilResourceData(Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_SOILRESOURCE), 1);
        android.util.Log.e("lastVisitSoilResource", Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_SOILRESOURCE)+ "");

        msoilTypeIrrigationModelList = (ArrayList<PlotIrrigationTypeXref>) dataAccessHandler.getPlotIrrigationXRefData(Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_PLOTIRRIGATIONTYPEXREF), 1);
        android.util.Log.e("lastVisitIrrigation", Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_PLOTIRRIGATIONTYPEXREF)+ "");
        msoilTypeModel = (SoilResource) dataAccessHandler.getSoilResourceData(
                Queries.getInstance().getSoilResourceBinding(CommonConstants.PLOT_CODE), 0
        );

        if (SoilResourcelastvisitdatamap != null && !SoilResourcelastvisitdatamap.isEmpty()) {
            SoilResource soilResource = SoilResourcelastvisitdatamap.get(0);

            String soil_type = soilResource != null
                    ? dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().gettypcdmtdata(soilResource.getSoiltypeid()))
                    : null;
            if (soiltype != null && soil_type != null) soiltype.setText(soil_type);

            String soilnature_type = soilResource != null
                    ? dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().gettypcdmtdata(soilResource.getSoilNatureId()))
                    : null;
            if (soilnaturetype != null && soilnature_type != null) soilnaturetype.setText(soilnature_type);

            String priortization = soilResource.getPrioritizationtypeid() != null ? dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().gettypcdmtdata(soilResource.getPrioritizationtypeid())) : null;
            if (plotpriortization != null && priortization != null) plotpriortization.setText(priortization);

            if (availablehours != null && soilResource != null) {
                availablehours.setText(String.valueOf(soilResource.getAvailablepowerhours()));
            }
            Log.e("Ispoweravailable=========>", soilResource.getIspoweravailable() + "");
            if (soilResource.getIspoweravailable() != null && soilResource != null) {
                Log.e("Ispoweravailable=========>", soilResource.getIspoweravailable() + "");
                poweravilable.setText(soilResource.getIspoweravailable() == 1 ? "Yes" : "No");
            }
            else{
                poweravilable.setText("No");
            }

            if (irrigatedArea != null && soilResource != null) {
                irrigatedArea.setText(String.valueOf(soilResource.getIrrigatedArea()));
            }

            if (Comments != null && soilResource != null && soilResource.getComments() != null) {
                Comments.setText(soilResource.getComments());
            }
        }

        if (msoilTypeIrrigationModelList != null && !msoilTypeIrrigationModelList.isEmpty()) {
            if (norecords != null) norecords.setVisibility(View.GONE);
            if (mainLL != null) mainLL.setVisibility(View.VISIBLE);

            SoilTypeAdapter = new SoilIrrigationAdapter(activity, msoilTypeIrrigationModelList, typeofirrigationMap);
            if (irrigationRecyclerView != null) {
                irrigationRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                irrigationRecyclerView.setAdapter(SoilTypeAdapter);
            }
        } else {
            if (mainLL != null) mainLL.setVisibility(View.GONE);
            if (norecords != null) norecords.setVisibility(View.VISIBLE);
        }
        //   }

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Any delayed operations
            }
        }, 500);
    }

    @Override
    public void setCartClickListener(String clickItem, final int selectPos) {
        if (clickItem.equalsIgnoreCase("edit")) {

            EditEntryDialogFragment editEntryDialogFragment = new EditEntryDialogFragment();
            editEntryDialogFragment.setOnDataEditChangeListener(this);
            Bundle inputBundle = new Bundle();
            selectedPosition = selectPos;
            inputBundle.putString("title", "Irrigation Type");

            inputBundle.putInt("typeDialog", EditEntryDialogFragment.TYPE_SPINNER_IRIGATION_TYPE);
            inputBundle.putString("prevData", msoilTypeIrrigationModelList.get(selectedPosition).getName() + "-" + getString(R.string.typeofirrigation) + (selectedPosition + 1));

            editEntryDialogFragment.setArguments(inputBundle);
            FragmentManager mFragmentManager = getChildFragmentManager();
            editEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
        } else if (clickItem.equalsIgnoreCase("delete")) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (msoilTypeIrrigationModelList != null && selectPos >= 0 && selectPos < msoilTypeIrrigationModelList.size()) {

                            // ✅ Log the item before removing
                            Log.d("msoilTypeIrrigationModelList", "Deleting item at position " + selectPos + ": " + msoilTypeIrrigationModelList.get(selectPos).getName());

                            // 1. Remove the item
                            msoilTypeIrrigationModelList.remove(selectPos);

                            // 2. Notify adapter about dataset change
                            mSoilTypeAdapter.notifyItemRemoved(selectPos);

                            // 3. Save updated list back to DataManager
                            DataManager.getInstance().addData(DataManager.TypeOfIrrigation, msoilTypeIrrigationModelList);

                            //4.Update UI
                            updateUi();

                            // 5. Show confirmation toast
                            UiUtils.showCustomToastMessage("Item Deleted Successfully", getContext(), 0);

                        } else {
                            Log.e("Delete", "Invalid position or empty list. selectPos=" + selectPos);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }
    }
    private void updateUi() {
        if (mSoilTypeAdapter != null) {
            mSoilTypeAdapter.notifyDataSetChanged();
        }
    }

    private void bindFormFieldsForEdit(PlotIrrigationTypeXref model) {
        typeofirrigationMap2 = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("36"));

        // Set type of irrigation spinner
        int typeIndex = getIndexFromMap(typeofirrigationSpin, typeofirrigationMap2, model.getIrrigationtypeid());
        typeofirrigationSpin.setSelection(typeIndex);

        // Delay setting recommended irrigation spinner to allow spinner population
        typeofirrigationSpin.postDelayed(() -> {
            int recIndex = getIndexFromMap(irrigationRecSpn, typeofirrigationMap2, model.getRecmIrrgId());
            irrigationRecSpn.setSelection(recIndex);
        }, 200);

        // Handle Drip Logic if Recommended Irrigation ID is 391 (Drip)
        if (model.getRecmIrrgId() == 391) {
            if (model.getIsDripInstalled() != null && model.getIsDripInstalled() == 1) {
                // Drip Installed = Yes
                dripexist.setSelection(1);
                HPIDnum.setText(model.getHPIdNumber());
                DripInstalledDate.setText(model.getDripInstalledDate());
                existdripcmneySpin.setSelection(getIndexFromMap(existdripcmneySpin, primaryDripCompanyMap, model.getPrimaryCompanyId()));
            } else {
                // Drip Installed = No
                dripexist.setSelection(2);
                dripcmneySpin.setSelection(getIndexFromMap(dripcmneySpin, primaryDripCompanyMap, model.getPrimaryCompanyId()));
                dripcmney2Spin.setSelection(getIndexFromMap(dripcmney2Spin, primaryDripCompanyMap, model.getSecondaryCompanyId()));
                watwrpumpSpin.setSelection(getIndexFromMap(watwrpumpSpin, PumpTypeMap, model.getWaterPumpTypeId()));
                capacity.setText(String.valueOf(model.getCapacity()));
            }
        } else {
            // If not drip, reset drip-related fields just in case
            dripexist.setSelection(0);
            HPIDnum.setText("");
            DripInstalledDate.setText("");
            capacity.setText("");
            dripcmneySpin.setSelection(0);
            dripcmney2Spin.setSelection(0);
            existdripcmneySpin.setSelection(0);
            watwrpumpSpin.setSelection(0);
        }
    }


    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
    private int getIndexFromMap(Spinner spinner, LinkedHashMap<String, String> map, int id) {
        String value = CommonUtils.getKeyFromValue2(map, id);
        if (value == null) {
            Log.e("SpinnerHelper", "Value not found for ID: " + id);
            return 0;
        }

        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString().trim();
            if (item.equalsIgnoreCase(value.trim())) {
                return i;
            }
        }

        Log.e("SpinnerHelper", "Spinner item not matched: " + value);
        return 0;
    }

    @Override
    public void onDataEdited(Bundle dataBundle) {
        msoilTypeIrrigationModel = new PlotIrrigationTypeXref();
        msoilTypeIrrigationModel.setName("" + dataBundle.getString("inputValue"));
        msoilTypeIrrigationModelList.set(selectedPosition, msoilTypeIrrigationModel);

        mSoilTypeAdapter.notifyDataSetChanged();

    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }
}
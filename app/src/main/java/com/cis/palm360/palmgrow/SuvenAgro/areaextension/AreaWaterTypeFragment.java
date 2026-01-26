package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.app.AlertDialog;
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

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CommonUtilsNavigation;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.WaterResource;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager.SOURCE_OF_WATER;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//To Enter/Save Water Details
public class AreaWaterTypeFragment extends Fragment implements AreaWaterTypeAdapter.OnCartChangedListener, EditEntryDialogFragment.OnDataEditChangeListener {
    private Spinner sourceOfWaterSpin;
    private DataAccessHandler dataAccessHandler;
    private EditText numberEdit, waterdischargecapacityEdit, wateravailabilityofcanalEdit;
    private RecyclerView mRecyclerView,waterRecyclerView;
    private LinearLayout numberLay, waterdischargecapacityLL, wateravailabilityofcanalLL,butonsLay;
    private Button submit, saveBtn,finishBtn;
    private View rootView;
    private ActionBar actionBar;
    private AreaWaterTypeAdapter waterAdapter;
    private WaterTypeAdapter water_Adapter;
    private int selectedPosition = 0;
    private LinkedHashMap sourceOfWaterMap, sourceOfWaterMap_for_edit;
    private ArrayList<WaterResource> mWaterTypeModelList = new ArrayList<>();
    private CoordinatorLayout parentLay;
    private UpdateUiListener updateUiListener;
    private boolean updateFromDb = false;
    private Button waterhistoryBtn;
    private ArrayList<WaterResource> mWaterTypedatamap;
    public AreaWaterTypeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water, container, false);

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
        sourceOfWaterSpin = (Spinner) rootView.findViewById(R.id.sourceOfWaterSpin);
        numberEdit = (EditText) rootView.findViewById(R.id.numberEdit);
        waterdischargecapacityEdit = (EditText) rootView.findViewById(R.id.waterdischargecapacityEdit);
        wateravailabilityofcanalEdit = (EditText) rootView.findViewById(R.id.wateravailabilityofcanalEdit);
        submit = (Button) rootView.findViewById(R.id.submit);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mRecyclerView);
        numberLay = (LinearLayout) rootView.findViewById(R.id.numberLay);
        waterdischargecapacityLL = (LinearLayout) rootView.findViewById(R.id.waterdischargecapacityLL);
        wateravailabilityofcanalLL = (LinearLayout) rootView.findViewById(R.id.wateravailabilityofcanalLL);
        butonsLay = (LinearLayout) rootView.findViewById(R.id.butonsLay);
        parentLay = (CoordinatorLayout) rootView.findViewById(R.id.parentLay);
        saveBtn = (Button) rootView.findViewById(R.id.saveBtn);
        finishBtn = (Button) rootView.findViewById(R.id.finishBtn);
        waterhistoryBtn =(Button) rootView.findViewById(R.id.waterhistoryBtn);
       waterhistoryBtn.setVisibility(CommonUtils.isFromCropMaintenance() ? View.VISIBLE : View.GONE);
     
    }

    private void setViews() {
        mWaterTypeModelList = (ArrayList<WaterResource>) DataManager.getInstance().getDataFromManager(SOURCE_OF_WATER);
//        if (mWaterTypeModelList == null && (isFromFollowUp() || isFromCropMaintenance() || isFromConversion())) {
//            mWaterTypeModelList = (ArrayList<WaterResource>) dataAccessHandler.getWaterResourceData(Queries.getInstance().getWaterResourceBinding(CommonConstants.PLOT_CODE), 1);
//        }
        bindSourceofwaterAdapter();

        sourceOfWaterMap = dataAccessHandler.getGenericData(Queries.getInstance().getSourceOfWaterInfo());
        sourceOfWaterMap_for_edit = dataAccessHandler.getGenericData(Queries.getInstance().getSourceOfWaterInfo());

        if (null != mWaterTypeModelList && !mWaterTypeModelList.isEmpty()) {
            butonsLay.setVisibility(View.VISIBLE);
         //   updateFromDb = false;
            for (WaterResource waterTypeModel : mWaterTypeModelList) {
                sourceOfWaterMap.remove(String.valueOf(waterTypeModel.getSourceofwaterid()));
            }
        } else
            mWaterTypeModelList = new ArrayList<WaterResource>();

        mRecyclerView.setHasFixedSize(true);
        waterAdapter = new AreaWaterTypeAdapter(getActivity(), mWaterTypeModelList, sourceOfWaterMap_for_edit);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(waterAdapter);
       waterAdapter.setOnCartChangedListener(this);

        sourceOfWaterSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (sourceOfWaterSpin.getSelectedItemPosition() != 0) {

                    if (sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Bore well") || sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Open well")|| sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Openwell")
                   || sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Borewell")) {
                        numberLay.setVisibility(View.VISIBLE);
                        waterdischargecapacityLL.setVisibility(View.VISIBLE);
                        wateravailabilityofcanalLL.setVisibility(View.GONE);
                    } else if (sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Canal")) {
                        numberLay.setVisibility(View.GONE);
                        waterdischargecapacityLL.setVisibility(View.GONE);
                        wateravailabilityofcanalLL.setVisibility(View.VISIBLE);
                    }
                  else if(sourceOfWaterSpin.getSelectedItem().toString().equalsIgnoreCase("Others")){
                     numberLay.setVisibility(View.GONE);
                       waterdischargecapacityLL.setVisibility(View.GONE);
                      wateravailabilityofcanalLL.setVisibility(View.GONE);                   }

                } else {
                    numberLay.setVisibility(View.GONE);
                    waterdischargecapacityLL.setVisibility(View.GONE);
                    wateravailabilityofcanalLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceOfWaterSpin.getSelectedItemPosition() != 0) {
                    if (validateUi()) {
                        WaterResource mWaterTypeModel = new WaterResource();
                        String key = CommonUtilsNavigation.getKey(sourceOfWaterMap, sourceOfWaterSpin.getSelectedItem().toString());
                        mWaterTypeModel.setSourceofwaterid(Integer.parseInt(key));
                        mWaterTypeModel.setBorewellnumber(numberEdit.isShown() ? CommonUtils.convertToBigNumber(numberEdit.getText().toString()) : 0);
                        mWaterTypeModel.setWaterdischargecapacity(waterdischargecapacityEdit.isShown() ? Double.valueOf(waterdischargecapacityEdit.getText().toString()) : 0.0);
//                        mWaterTypeModel.setCanalwater(wateravailabilityofcanalEdit.isShown() ? Double.valueOf(wateravailabilityofcanalEdit.getText().toString()) : 0.0);
                        mWaterTypeModel.setCanalwater(wateravailabilityofcanalEdit.isShown() ?CommonUtils.convertToBigNumber(wateravailabilityofcanalEdit.getText().toString()) : 0);
                        mWaterTypeModelList.add(mWaterTypeModel);
                        DataManager.getInstance().addData(SOURCE_OF_WATER, mWaterTypeModelList);

                        sourceOfWaterMap.remove(key);
                        ArrayAdapter<String> sourceOfWaterSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(sourceOfWaterMap, "Source Of Water"));
                        sourceOfWaterSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sourceOfWaterSpin.setAdapter(sourceOfWaterSpinnerArrayAdapter);
                        sourceOfWaterSpin.setSelection(0);

                        numberEdit.setText("");
                        waterdischargecapacityEdit.setText("");
                        wateravailabilityofcanalEdit.setText("");

                        waterAdapter.notifyDataSetChanged();
                        butonsLay.setVisibility(View.VISIBLE);
                    }
                } else{
                    //CommonUtils.showToast("Please Select Source of Water", getActivity());
                UiUtils.showCustomToastMessage("Please Select Source of Water", getContext(), 1);}

            }
        });

        parentLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             DataManager.getInstance().addData(SOURCE_OF_WATER, mWaterTypeModelList);
//                if (updateFromDb) {
//                    DataManager.getInstance().addData(DataManager.IS_WOP_DATA_UPDATED, true);
//                }
          //      CommonConstants.Flags.isWOPDataUpdated = true;
               updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DataManager.getInstance().addData(SOURCE_OF_WATER, mWaterTypeModelList);
//                if (updateFromDb) {
//                    DataManager.getInstance().addData(DataManager.IS_WOP_DATA_UPDATED, true);
//                }
             //   CommonConstants.Flags.isWOPDataUpdated = true;
               updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();
            }
        });
        waterhistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getContext());
            }
        });
    }

    public void showDialog(Context activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.waterlastvisteddata);

        Toolbar titleToolbar = (Toolbar) dialog.findViewById(R.id.titleToolbar);
        titleToolbar.setTitle("Water Details History");
        titleToolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));

        // Initialize waterRecyclerView from dialog's layout
        RecyclerView waterRecyclerView = (RecyclerView) dialog.findViewById(R.id.waterRecyclerView);

        TextView norecords = (TextView) dialog.findViewById(R.id.intercropnorecord_tv);
        LinearLayout mainLL = (LinearLayout) dialog.findViewById(R.id.intercropmainlyt);

        String lastVisitCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getLatestCropMaintanaceHistoryCode(CommonConstants.PLOT_CODE));
        android.util.Log.e("lastVisitCode", lastVisitCode + "");
        mWaterTypedatamap = (ArrayList<WaterResource>) dataAccessHandler.getWaterResourceData(Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_WATERESOURCE), 1);
        android.util.Log.e("lastVisitCode query", Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_WATERESOURCE) + "");

        if (mWaterTypedatamap != null && mWaterTypedatamap.size() > 0) {
            norecords.setVisibility(View.GONE);
            mainLL.setVisibility(View.VISIBLE);

            String cropgrown = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(mWaterTypedatamap.get(0).getSourceofwaterid()));
            String recommendedcrop = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(mWaterTypedatamap.get(0).getSourceofwaterid()));

            water_Adapter = new WaterTypeAdapter(activity, mWaterTypedatamap, sourceOfWaterMap_for_edit);
            waterRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            waterRecyclerView.setAdapter(water_Adapter);

        } else {
            mainLL.setVisibility(View.GONE);
            norecords.setVisibility(View.VISIBLE);
        }

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Any delayed operations
            }
        }, 500);
    }


    public void setCartClickListener(String clickItem, final int selectPos) {

        if (clickItem.equalsIgnoreCase("edit")) {


            EditEntryDialogFragment editEntryDialogFragment = new EditEntryDialogFragment();
            editEntryDialogFragment.setOnDataEditChangeListener(this);
            Bundle inputBundle = new Bundle();
            selectedPosition = selectPos;
            String title = "" + sourceOfWaterMap_for_edit.get(String.valueOf(mWaterTypeModelList.get(selectedPosition).getSourceofwaterid()));
            inputBundle.putString("title", "" + sourceOfWaterMap_for_edit.get(String.valueOf(mWaterTypeModelList.get(selectedPosition).getSourceofwaterid())));
            int sourCeofWaterId = mWaterTypeModelList.get(selectedPosition).getSourceofwaterid();
//            if (sourCeofWaterId == CommonConstants.BoreWellID || sourCeofWaterId == CommonConstants.OpenWellID) {
            if (title.equalsIgnoreCase("Bore well") || title.equalsIgnoreCase("Open well")) {
                inputBundle.putInt("typeDialog", EditEntryDialogFragment.TYPE_MULTI_EDIT_BOX2);
                inputBundle.putString("prevData", "Number *-" + mWaterTypeModelList.get(selectedPosition).getBorewellnumber());
                inputBundle.putString("prevData2", getString(R.string.waterdischargecapacity) + "-" + String.format("%.2f", mWaterTypeModelList.get(selectedPosition).getWaterdischargecapacity()));
            }
            else {
                inputBundle.putInt("typeDialog", EditEntryDialogFragment.TYPE_EDIT_BOX2);
                inputBundle.putString("prevData", mWaterTypeModelList.get(selectedPosition).getCanalwater() + "-Canal");
            }

            editEntryDialogFragment.setArguments(inputBundle);
            FragmentManager mFragmentManager = getChildFragmentManager();
            editEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");

        }
//        else if (clickItem.equalsIgnoreCase("delete")) {
//            mWaterTypeModelList.remove(selectPos);
//
//            if (mWaterTypeModelList.isEmpty())
//            {
//                butonsLay.setVisibility(View.GONE);
//            }
//           bindSourceofwaterAdapter();
//            waterAdapter.notifyDataSetChanged();
//        }
        else if (clickItem.equalsIgnoreCase("delete")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete this Water Details?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                       mWaterTypeModelList.remove(selectPos);
                       DataManager.getInstance().addData(SOURCE_OF_WATER, mWaterTypeModelList);
                        updateUiListener.updateUserInterface(0);
                        //getFragmentManager().popBackStack();
            if (mWaterTypeModelList.isEmpty())
            {
                butonsLay.setVisibility(View.GONE);
            }
           bindSourceofwaterAdapter();
            waterAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onDataEdited(Bundle dataBundle) {
        WaterResource mWaterTypeModel = new WaterResource();
        mWaterTypeModel.setSourceofwaterid(mWaterTypeModelList.get(selectedPosition).getSourceofwaterid());
        mWaterTypeModel.setBorewellnumber((null != mWaterTypeModelList.get(selectedPosition).getBorewellnumber() && mWaterTypeModelList.get(selectedPosition).getBorewellnumber() != 0) ? CommonUtils.convertToBigNumber(dataBundle.getString("inputValue")) : 0);
        mWaterTypeModel.setWaterdischargecapacity(mWaterTypeModelList.get(selectedPosition).getWaterdischargecapacity() != 0.0 ? Double.parseDouble(dataBundle.getString("inputValue2")) : 0.0);
//        mWaterTypeModel.setCanalwater(mWaterTypeModelList.get(selectedPosition).getCanalwater() != 0.0 ? Double.parseDouble(dataBundle.getString("inputValue")) : 0.0);
        mWaterTypeModel.setCanalwater((null != mWaterTypeModelList.get(selectedPosition).getCanalwater() && mWaterTypeModelList.get(selectedPosition).getCanalwater() != 0) ? CommonUtils.convertToBigNumber(dataBundle.getString("inputValue")) : 0);
        mWaterTypeModelList.set(selectedPosition, mWaterTypeModel);

        waterAdapter.notifyDataSetChanged();

    }

    private void bindSourceofwaterAdapter() {
        sourceOfWaterMap = dataAccessHandler.getGenericData(Queries.getInstance().getSourceOfWaterInfo());
        if (null != mWaterTypeModelList && !mWaterTypeModelList.isEmpty()) {
            for (WaterResource waterTypeModel : mWaterTypeModelList) {
                sourceOfWaterMap.remove(String.valueOf(waterTypeModel.getSourceofwaterid()));
            }
        } else
            mWaterTypeModelList = new ArrayList<WaterResource>();

        ArrayAdapter<String> sourceOfWaterSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(sourceOfWaterMap, "Source Of Water"));
        sourceOfWaterSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceOfWaterSpin.setAdapter(sourceOfWaterSpinnerArrayAdapter);

        mRecyclerView.setHasFixedSize(true);
        waterAdapter = new AreaWaterTypeAdapter(getActivity(), mWaterTypeModelList, sourceOfWaterMap_for_edit);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(waterAdapter);
   waterAdapter.setOnCartChangedListener(this);
    }


    private boolean validateUi() {

        if (numberEdit.isShown() && TextUtils.isEmpty(numberEdit.getText().toString())) {
            UiUtils.showCustomToastMessage(getResources().getString(R.string.error_waternumber), getActivity(), 1);
            numberEdit.requestFocus();
            return false;
        }
/*        if (numberEdit.isShown() && numberEdit.getText().toString().equals("0")) {
            UiUtils.showCustomToastMessage("Please Enter Valid Number", getActivity(), 1);

            numberEdit.requestFocus();
            return false;
        }*/
        if (numberEdit.isShown()) {
            String value = numberEdit.getText().toString().trim();
            try {
                double number = Double.parseDouble(value);
                if (number <= 0) {
                    UiUtils.showCustomToastMessage("Please enter a valid number", getActivity(), 1);
                    numberEdit.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Please enter a valid number", getActivity(), 1);
                numberEdit.requestFocus();
                return false;
            }
        }
        if (waterdischargecapacityEdit.isShown() && TextUtils.isEmpty(waterdischargecapacityEdit.getText().toString())) {
            UiUtils.showCustomToastMessage(getResources().getString(R.string.error_waterdischargecapacity), getActivity(), 1);
            waterdischargecapacityEdit.requestFocus();
            return false;
        }

        if (waterdischargecapacityEdit.isShown()) {
            try {
                double dischargeValue = Double.parseDouble(waterdischargecapacityEdit.getText().toString().trim());
                if (dischargeValue <= 0) {
                    UiUtils.showCustomToastMessage("Please Enter Valid Capacity (Lt/Hr)", getActivity(), 1);
                    waterdischargecapacityEdit.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Invalid number format", getActivity(), 1);
                waterdischargecapacityEdit.requestFocus();
                return false;
            }
        }


        if (wateravailabilityofcanalEdit.isShown() && TextUtils.isEmpty(wateravailabilityofcanalEdit.getText().toString())) {
            UiUtils.showCustomToastMessage("Please Enter " + getResources().getString(R.string.wateravailabilityofcanall), getActivity(), 1);
            wateravailabilityofcanalEdit.requestFocus();
            return false;
        }

        if (wateravailabilityofcanalEdit.isShown()) {
            int months = Integer.parseInt(wateravailabilityofcanalEdit.getText().toString());
            if (months <= 0 || months > 12) {
                UiUtils.showCustomToastMessage("Please Enter Months Between 1 and 12", getActivity(), 1);
                wateravailabilityofcanalEdit.requestFocus();
                return false;
            }
        }


        return true;
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

}

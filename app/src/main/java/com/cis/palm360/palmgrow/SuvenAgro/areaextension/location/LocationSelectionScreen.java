package com.cis.palm360.palmgrow.SuvenAgro.areaextension.location;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.RegistrationFlowScreen;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//For Selecting State/District/Mandal/Village
public class LocationSelectionScreen extends AppCompatActivity {

    private static final String LOG_TAG = LocationSelectionScreen.class.getName();

    private Button submitBtn;
    private RelativeLayout parentPanel;

    private Spinner statespin, districtSpin, mandalSpin, villageSpinner;
    private ActionBar actionBar;
    LinkedHashMap<String, Pair> stateDataMap = new LinkedHashMap<>();
    LinkedHashMap<String, Pair> districtDataMap = new LinkedHashMap<>();
    LinkedHashMap<String, Pair> mandalDataMap = new LinkedHashMap<>();
    LinkedHashMap<String, Pair> villagesDataMap = new LinkedHashMap<>();

  //  private LinkedHashMap<String, Pair> stateDataMap = null, districtDataMap, mandalDataMap, villagesDataMap;
    private DataAccessHandler dataAccessHandler;
    private String villageCodeStr;
    public TextView ClusterNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Grower Residential Location");


        CommonUtils.currentActivity = LocationSelectionScreen.this;
        dataAccessHandler = new DataAccessHandler(LocationSelectionScreen.this);


        statespin = (Spinner) findViewById(R.id.statespin);
        villageSpinner = (Spinner) findViewById(R.id.villageSpin);
        ClusterNameTv = (TextView) findViewById(R.id.Cluster_Name);
        districtSpin = (Spinner) findViewById(R.id.districtSpin);
        mandalSpin = (Spinner) findViewById(R.id.mandalSpin);

        parentPanel = (RelativeLayout) findViewById(R.id.parentPanel);
        parentPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stateDataMap == null || stateDataMap.size() == 0 ||
                        statespin.getSelectedItem() == null || statespin.getSelectedItemPosition() == 0) {
//                    Toast.makeText(LocationSelectionScreen.this, "Please Select State", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Please Select State", LocationSelectionScreen.this, 1);

                } else  if (districtSpin.getSelectedItemPosition() == 0) {
//                    Toast.makeText(LocationSelectionScreen.this, "Please Select a District", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Please Select a District", LocationSelectionScreen.this, 1);

                }
                else if (mandalDataMap == null || mandalDataMap.size() == 0 ||
                        mandalSpin.getSelectedItem() == null || mandalSpin.getSelectedItemPosition() == 0) {
//                    Toast.makeText(LocationSelectionScreen.this, "Please Select Mandal", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Please Select Mandal", LocationSelectionScreen.this, 1);
                } else if (villagesDataMap == null || villagesDataMap.size() == 0 ||
                        villageSpinner.getSelectedItem() == null || villageSpinner.getSelectedItemPosition() == 0) {
//                    Toast.makeText(LocationSelectionScreen.this, "Please Select Village", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Please Select Village", LocationSelectionScreen.this, 1);
                } else {
                    // All selections are valid, proceed
                    startActivity(new Intent(LocationSelectionScreen.this, RegistrationFlowScreen.class));
                    finish();
                }
            }
        });



/*        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isEmptySpinner(statespin)) {
                    Toast.makeText(LocationSelectionScreen.this, "Please Select State", Toast.LENGTH_SHORT).show();
                } else if (CommonUtils.isEmptySpinner(districtSpin)) {
                    Toast.makeText(LocationSelectionScreen.this, "Please Select District", Toast.LENGTH_SHORT).show();
                } else if (CommonUtils.isEmptySpinner(mandalSpin)) {
                    Toast.makeText(LocationSelectionScreen.this, "Please Select Mandal", Toast.LENGTH_SHORT).show();
                } else if (CommonUtils.isEmptySpinner(villageSpinner)) {
                    Toast.makeText(LocationSelectionScreen.this, "Please Select Village", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(LocationSelectionScreen.this, RegistrationFlowScreen.class));
                    finish();
                }

            }
        });*/
        stateDataMap = dataAccessHandler.getPairData(Queries.getInstance().getStatesMasterQuery());

        if (stateDataMap != null && stateDataMap.size() == 1) {
            // Auto-select state
            String key = new ArrayList<>(stateDataMap.keySet()).get(0);
            Pair statePair = stateDataMap.get(key);
            CommonConstants.stateId = key;
            CommonConstants.stateCode = statePair.first.toString();
            CommonConstants.stateName = statePair.second.toString();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(stateDataMap, "State"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statespin.setAdapter(adapter);
            // Find the correct index in districtList
            int position = adapter.getPosition(CommonConstants.stateName);

            if (position != -1) {
                statespin.setSelection(position); // auto-binds value
            }
            // Auto-select state spinner
          //  statespin.setAdapter(null);
            loadDistricts(CommonConstants.stateId);
        } else {
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(stateDataMap, "State"));
            stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statespin.setAdapter(stateAdapter);

            statespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        // Clear all dependent spinners
                        districtSpin.setAdapter(null);
                        mandalSpin.setAdapter(null);
                        villageSpinner.setAdapter(null);
                        ClusterNameTv.setText("");
                        CommonConstants.stateId = null;
                        CommonConstants.stateName = "";
                        CommonConstants.stateCode = "";
                        return;
                    }
                    if (stateDataMap != null && stateDataMap.size() > 0 && statespin.getSelectedItemPosition() != 0) {
                        CommonConstants.stateId = new ArrayList<>(stateDataMap.keySet()).get(position - 1);
                        Pair pair = stateDataMap.get(CommonConstants.stateId);
                        CommonConstants.stateCode = pair.first.toString();
                        CommonConstants.stateName = pair.second.toString();

                        loadDistricts(CommonConstants.stateId);
                    }
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }


//        statespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    // Clear all dependent spinners
//                    districtSpin.setAdapter(null);
//                    mandalSpin.setAdapter(null);
//                    villageSpinner.setAdapter(null);
//                    ClusterNameTv.setText("");
//                    CommonConstants.stateId = null;
//                    CommonConstants.stateName = "";
//                    CommonConstants.stateCode = "";
//                    return;
//                }
//                if (stateDataMap != null && stateDataMap.size() > 0 && statespin.getSelectedItemPosition() != 0) {
//                    CommonConstants.stateId = stateDataMap.keySet().toArray(new String[stateDataMap.size()])[i - 1];
//                    Log.v(LOG_TAG, "@@@ Selected State " + CommonConstants.stateId);
//                    districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getDistrictQuery(CommonConstants.stateId));
//                    ArrayAdapter<String> spinnerDistrictArrayAdapter = new ArrayAdapter<>(LocationSelectionScreen.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
//                    spinnerDistrictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    districtSpin.setAdapter(spinnerDistrictArrayAdapter);
//                    CommonConstants.stateName = statespin.getSelectedItem().toString();
//                    Pair statePair = stateDataMap.get(CommonConstants.stateId);
//                    CommonConstants.stateCode = statePair.first.toString();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        districtSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    mandalSpin.setAdapter(null);
//                    villageSpinner.setAdapter(null);
//                    ClusterNameTv.setText("");
//                    CommonConstants.districtId = null;
//                    return;
//                }

//                if (districtDataMap != null && districtDataMap.size() > 0 && districtSpin.getSelectedItemPosition() != 0) {
//                    CommonConstants.districtId = districtDataMap.keySet().toArray(new String[districtDataMap.size()])[i - 1];
//                    Log.v(LOG_TAG, "@@@ Selected State " + CommonConstants.districtId);
//                    mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getMandalsQuery(CommonConstants.districtId));
//                    ArrayAdapter<String> spinnerMandalArrayAdapter = new ArrayAdapter<>(LocationSelectionScreen.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
//                    spinnerMandalArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    mandalSpin.setAdapter(spinnerMandalArrayAdapter);
//                    CommonConstants.districtName = districtSpin.getSelectedItem().toString();
//                    Pair districtPair = districtDataMap.get(CommonConstants.districtId);
//                    CommonConstants.districtCode = districtPair.first.toString();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        mandalSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    villageSpinner.setAdapter(null);
//                    ClusterNameTv.setText("");
//                    CommonConstants.mandalId = null;
//                    return;
//                }
//
//                if (mandalDataMap != null && mandalDataMap.size() > 0 && mandalSpin.getSelectedItemPosition() != 0) {
//                    CommonConstants.mandalId = mandalDataMap.keySet().toArray(new String[mandalDataMap.size()])[i - 1];
//                    Log.v(LOG_TAG, "@@@ Selected State " + CommonConstants.mandalId);
//                    CommonConstants.mandalName = mandalSpin.getSelectedItem().toString();
//
//                    Pair mandalPair = mandalDataMap.get(CommonConstants.mandalId);
//                    CommonConstants.mandalCode = mandalPair.first.toString();
//                    CommonConstants.prevMandalPos = i;
//
//                    villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getVillagesQuery(CommonConstants.mandalId));
//                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(LocationSelectionScreen.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
//                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    villageSpinner.setAdapter(spinnerArrayAdapter);
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                if (villagesDataMap != null && villagesDataMap.size() > 0 && villageSpinner.getSelectedItemPosition() != 0) {
//                    CommonConstants.villageId = villagesDataMap.keySet().toArray(new String[villagesDataMap.size()])[i - 1];
//                    villageCodeStr = villageSpinner.getSelectedItem().toString();
//                    Pair villagePair = villagesDataMap.get(CommonConstants.villageId);
//                    CommonConstants.villageCode = villagePair.first.toString();
////                  CommonConstants.FARMER_CODE = dataAccessHandler.getGeneratedFarmerCode(Queries.getInstance().getMaxNumberQuery(CommonConstants.villageId, CommonConstants.villageCode));
//                    CommonConstants.villageName = villageSpinner.getSelectedItem().toString();
//                    CommonConstants.prevVillagePos = i;
//                    String ClusterName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getClusterName(CommonConstants.villageId));
//
//                    if (ClusterName != null && !ClusterName.trim().isEmpty()) {
//                        ClusterNameTv.setText(" " + ClusterName);
//                    } else {
//                        ClusterNameTv.setText(""); // Set to empty if null or empty
//                    }
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void loadDistricts(String stateId) {
        Log.d("DEBUG", "Loading Districts for StateId: " + stateId);

        // Reset dependent data/maps/spinners first
        mandalDataMap = null;
        villagesDataMap = null;
        mandalSpin.setAdapter(null);
        villageSpinner.setAdapter(null);
        ClusterNameTv.setText("");

        districtDataMap = dataAccessHandler.getPairData(Queries.getInstance().getDistrictQuery(stateId));
        Log.d("DEBUG", "District Map Size: " + (districtDataMap != null ? districtDataMap.size() : "null"));


        if (districtDataMap != null && districtDataMap.size() == 1) {


                String districtId = new ArrayList<>(districtDataMap.keySet()).get(0);
                Pair pair = districtDataMap.get(districtId);

                CommonConstants.districtId = districtId;
                CommonConstants.districtCode = pair.first.toString();
                CommonConstants.districtName = pair.second.toString();

                Log.d("DEBUG", "Auto-select district: " + CommonConstants.districtName);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpin.setAdapter(adapter);
                // Find the correct index in districtList
                int position = adapter.getPosition(CommonConstants.districtName);

                if (position != -1) {
                    districtSpin.setSelection(position); // auto-binds value
                }

                // Optionally disable spinner
                // districtSpin.setEnabled(false);

                // Load mandals
                loadMandals(districtId);
            }

            // Only one district – auto-select
//            String key = new ArrayList<>(districtDataMap.keySet()).get(0);
//            Pair pair = districtDataMap.get(key);
//
//            CommonConstants.districtId = key;
//            CommonConstants.districtCode = pair.first.toString();
//            CommonConstants.districtName = pair.second.toString();
//
//            Log.d("DEBUG", "Auto-selected district: " + CommonConstants.districtId + " | " + CommonConstants.districtName);
//
//            // Set adapter with the single item so it displays in spinner
//            List<String> singleItemList = new ArrayList<>();
//            singleItemList.add(CommonConstants.districtName);  // This ensures it shows in the spinner
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, singleItemList);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            districtSpin.setAdapter(adapter);
//            districtSpin.setSelection(0);  // Force selection
           // districtSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(districtDataMap,  CommonConstants.districtCode));

            // Optional: disable spinner if you don't want user to change
            // districtSpin.setEnabled(false);

            // Proceed to mandals
//            loadMandals(CommonConstants.districtId);
//
//        }



        else if (districtDataMap != null && districtDataMap.size() > 1) {
            // Multiple districts – allow selection
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(districtDataMap, "District"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpin.setAdapter(adapter);
            Log.d("DEBUG", "District spinner set with multiple items.");

            districtSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0) {
                        mandalSpin.setAdapter(null);
                        villageSpinner.setAdapter(null);
                        ClusterNameTv.setText("");
                        CommonConstants.districtId = null;
                        return;
                    }
                    CommonConstants.districtId = new ArrayList<>(districtDataMap.keySet()).get(position - 1);
                    Pair pair = districtDataMap.get(CommonConstants.districtId);
                    CommonConstants.districtCode = pair.first.toString();
                    CommonConstants.districtName = pair.second.toString();

                    Log.d("DEBUG", "Selected district: " + CommonConstants.districtId + " | " + CommonConstants.districtName);
                    loadMandals(CommonConstants.districtId);
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else {
            Log.d("DEBUG", "No districts found for stateId: " + stateId);
            districtSpin.setAdapter(null);  // Clear spinner
        }
    }


    private void loadMandals(String districtId) {
        villagesDataMap = null;
        villageSpinner.setAdapter(null);
        ClusterNameTv.setText("");
        mandalDataMap = dataAccessHandler.getPairData(Queries.getInstance().getMandalsQuery(districtId));

        if (mandalDataMap != null && mandalDataMap.size() == 1) {
            String key = new ArrayList<>(mandalDataMap.keySet()).get(0);
            Pair pair = mandalDataMap.get(key);

            CommonConstants.mandalId = key;
            CommonConstants.mandalCode = pair.first.toString();
            CommonConstants.mandalName = pair.second.toString();

            Log.d("DEBUG", "Auto-selected mandal: " + CommonConstants.mandalId + " | " + CommonConstants.mandalName);

            // Bind adapter with single item so it shows in spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mandalSpin.setAdapter(adapter);

            int position = adapter.getPosition(CommonConstants.mandalName);

            if (position != -1) {
                mandalSpin.setSelection(position); // auto-binds value
            }
            loadVillages(key);
        } else if (mandalDataMap != null && mandalDataMap.size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(mandalDataMap, "Mandal"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mandalSpin.setAdapter(adapter);

            mandalSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        villageSpinner.setAdapter(null);
                        ClusterNameTv.setText("");
                        CommonConstants.mandalId = null;
                        return;
                    }

                    CommonConstants.mandalId = new ArrayList<>(mandalDataMap.keySet()).get(position - 1);
                    Pair pair = mandalDataMap.get(CommonConstants.mandalId);
                    CommonConstants.mandalCode = pair.first.toString();
                    CommonConstants.mandalName = pair.second.toString();

                    loadVillages(CommonConstants.mandalId);
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        } else {
            mandalSpin.setAdapter(null);
            Log.d("DEBUG", "No mandals found for districtId: " + districtId);
        }
    }

    private void loadVillages(String mandalId) {
        villagesDataMap = dataAccessHandler.getPairData(Queries.getInstance().getVillagesQuery(mandalId));

        if (villagesDataMap != null && villagesDataMap.size() == 1) {
            String key = new ArrayList<>(villagesDataMap.keySet()).get(0);
            Pair pair = villagesDataMap.get(key);

            CommonConstants.villageId = key;
            CommonConstants.villageCode = pair.first.toString();
            CommonConstants.villageName = pair.second.toString();

            Log.d("DEBUG", "Auto-selected village: " + CommonConstants.villageId + " | " + CommonConstants.villageName);

            List<String> singleItemList = new ArrayList<>();
            singleItemList.add(CommonConstants.villageName);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            villageSpinner.setAdapter(adapter);


            int position = adapter.getPosition(CommonConstants.villageName);

            if (position != -1) {
                villageSpinner.setSelection(position); // auto-binds value
            }
            showClusterName(key);
        } else if (villagesDataMap != null && villagesDataMap.size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(villagesDataMap, "Village"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            villageSpinner.setAdapter(adapter);

            villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String key = new ArrayList<>(villagesDataMap.keySet()).get(position - 1);
                        Pair pair = villagesDataMap.get(key);

                        CommonConstants.villageId = key;
                        CommonConstants.villageCode = pair.first.toString();
                        CommonConstants.villageName = pair.second.toString();
                        showClusterName(CommonConstants.villageId);
                    }
                    else{

                            ClusterNameTv.setText("");
                        CommonConstants.villageId = null;
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

    private void showClusterName(String villageId) {
        String clusterName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getClusterName(villageId));
        if (clusterName != null && !clusterName.trim().isEmpty()) {
            ClusterNameTv.setText(" " + clusterName);
        } else {
            ClusterNameTv.setText("");
        }
    }

}

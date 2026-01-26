package com.cis.palm360.palmgrow.SuvenAgro.activitylogdetails;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.location.LocationManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areacalculator.LatLongListener;
import com.cis.palm360.palmgrow.SuvenAgro.areacalculator.LocationProvider;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.PalmOilDatabase;
import com.cis.palm360.palmgrow.SuvenAgro.ui.OilPalmBaseActivity;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

public class LogBookScreenActivity extends OilPalmBaseActivity {
    EditText clientname, mobileNumber, location, details;
    public Context context;
    Button save;
    DataAccessHandler dataAccessHandler;
    String clientname_str, location_str, details_str, mobileNumber_str, createdDate, serverStatus;
    private PalmOilDatabase palmOilDatabase;
    private static final String LOG_TAG = "MyService";
    float latitude, longitude;
    int createdByUser;
    private static LocationProvider mLocationProvider;
    private double currentLatitude, currentLongitude;
    private static String latLong = "";
    LocationManager lm;

    //GetLocation
    public static LocationProvider getLocationProvider(Context context, boolean showDialog) {
        if (mLocationProvider == null) {
            mLocationProvider = new LocationProvider(context, new LatLongListener() {

                public void getLatLong(String mLatLong) {
                    latLong = mLatLong;
                }
            });

        }
        if (mLocationProvider.getLocation(showDialog)) {
            return mLocationProvider;
        } else {
            return null;
        }

    }



    public String getLatLong(Context context, boolean showDialog) {

        mLocationProvider = getLocationProvider(context, showDialog);

        if (mLocationProvider != null) {
            latLong = mLocationProvider.getLatitudeLongitude();


        }
        return latLong;
    }

    //Initializing the UI
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.activity_client_details, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile("Log Book");
        dataAccessHandler = new DataAccessHandler(this);
        palmOilDatabase = new PalmOilDatabase(this);
        clientname = parentView.findViewById(R.id.clientname);
        mobileNumber = parentView.findViewById(R.id.clientmobilenum);
        location = parentView.findViewById(R.id.clientlocation);
        details = parentView.findViewById(R.id.clientdetails);
        save = parentView.findViewById(R.id.save);


        if (android.os.Build.VERSION.SDK_INT >= 29) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLatitude_Longitude();

            }else {
                UiUtils.showCustomToastMessage("Please Turn On GPS", this, 1);
            }


        } else {
            if (CommonUtils.isLocationPermissionGranted(this)) {
                getLatitude_Longitude();
            } else {
                UiUtils.showCustomToastMessage("Please Turn On GPS", this, 1);
            }
        }



//Save Button On Click Listener
        save.setOnClickListener(v -> {
            clientname_str = clientname.getText().toString();
            location_str = location.getText().toString();
            mobileNumber_str = mobileNumber.getText().toString();
            details_str = details.getText().toString();
            if (Validations()) {

                latitude = Float.parseFloat(String.valueOf(currentLatitude));
                longitude = Float.parseFloat(String.valueOf(currentLongitude));
                boolean isInserted = palmOilDatabase.insertLogDetails(clientname_str, mobileNumber_str, location_str, details_str, latitude, longitude, serverStatus);
                if (isInserted) {
                    UiUtils.showCustomToastMessage("Data Inserted Successfully", this, 0);
                    finish();
                    Clear();
                } else {
                    UiUtils.showCustomToastMessage("Data Insertion Failed", this, 1);

                }
            }
        });
        mobileNumber.addTextChangedListener(new TextWatcher() {
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
                    mobileNumber.setText("");
                    UiUtils.showCustomToastMessage("Enter a Valid Mobile Number", LogBookScreenActivity.this , 1);
                    return;
                }
            }
        });

    }

    //Getting Latitude and Longitude
    private void getLatitude_Longitude() {
        String latlong[] = getLatLong(this, false).split("@");
        currentLatitude = Double.parseDouble(latlong[0]);
        currentLongitude = Double.parseDouble(latlong[1]);
    }

    //Validations
    public boolean Validations() {
        if (!TextUtils.isEmpty(mobileNumber_str ) ) {
            if (mobileNumber_str.length() < 10) {
                UiUtils.showCustomToastMessage("Please Enter Proper Mobile Number", this, 1);
                mobileNumber.requestFocus();
                return false;

            }
        }
        if (TextUtils.isEmpty(clientname_str)) {
            UiUtils.showCustomToastMessage("Please Enter Client Name", this, 1);
            clientname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(location_str)) {
            UiUtils.showCustomToastMessage("Please Enter you Meeting Location", this, 1);
            location.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(details_str)) {
            UiUtils.showCustomToastMessage("Please Enter Details", this, 1);
            details.requestFocus();
            return false;
        }
        return true;
    }

    //Clearing the Fields
    protected void Clear() {
        clientname.setText("");
        location.setText("");
        mobileNumber.setText("");
        details.setText("");
        clientname.requestFocus();
    }
}

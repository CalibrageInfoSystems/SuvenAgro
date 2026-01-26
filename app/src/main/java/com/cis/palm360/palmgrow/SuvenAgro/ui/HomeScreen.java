package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cis.palm360.palmgrow.SuvenAgro.BuildConfig;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.EmpPunchDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;

import com.cis.palm360.palmgrow.SuvenAgro.palmcare.CloseHarvestingList;
import com.cis.palm360.palmgrow.SuvenAgro.palmcare.ClosecropMaintenanceList;
import com.cis.palm360.palmgrow.SuvenAgro.palmcare.NoVisitPlotslistScreen;
import com.cis.palm360.palmgrow.SuvenAgro.utils.ImageUtility;
import com.github.pavlospt.CircleView;
import com.cis.palm360.palmgrow.SuvenAgro.FiltermapsActivity;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.activitylogdetails.LogBookScreenActivity;
import com.cis.palm360.palmgrow.SuvenAgro.alerts.AlertType;
import com.cis.palm360.palmgrow.SuvenAgro.alerts.AlertsDisplayScreen;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.location.LocationSelectionScreen;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.ui.RefreshSyncActivity;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.SearchFarmerScreen;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils.resetPrevRegData;
import static com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.ComplaintDetailsFragment.CAMERA_REQUEST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


//Home Screen
public class HomeScreen extends AppCompatActivity {

    private android.widget.LinearLayout areaExtensionRel;
    private android.widget.LinearLayout prospectiveFarmersRel;
    private android.widget.LinearLayout conversionRel;
    private android.widget.LinearLayout cropMaintenanceRel;
    private android.widget.LinearLayout harvestingRel, imagesRel, planationAuditRel;
    private LinearLayout visitDetails, mapsLayout;
    private RelativeLayout transportServiceLayout;
    private LinearLayout refreshRel;
    private CircleView circleView;
    private DataAccessHandler dataAccessHandler;
    LocationManager lm;
    ConstraintLayout layout1, layout2, layout3, layout4, layout5, layout6, testdialog;
    public static final int TYPE_PLOT_FOLLOWUP = 1;
    public static final int TYPE_PLOT_VISITS = 2;
    public static final int TYPE_PLOT_MISSING_TREES = 3;
    public static final int TYPE_PLOT_NOT_VISIT = 4;
    public static final String ALERT_TYPE = "alert_type";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoURI;
    private File photoFile;
    private String mCurrentPhotoPath;
    private FileRepository savedPictureData = null;
    private double latitude, longitude;
    private File finalFile;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView iv_punchIcon;
    List<String> activityRightsScreens;
    private AlertDialog dialog;
    private boolean isPunchInFlag = false;
    private int currentIsPunchIn = 1; // default

    // int TracktypeID;
    //Initializing the UI and On Click Listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        this.refreshRel = (LinearLayout) findViewById(R.id.refreshRel1);
        this.cropMaintenanceRel = (LinearLayout) findViewById(R.id.cropMaintenanceRel);
        // this.harvestingRel = (RelativeLayout) findViewById(R.id.harvestingRel);
        imagesRel = (LinearLayout) findViewById(R.id.imagesRel);
        this.conversionRel = (LinearLayout) findViewById(R.id.conversionRel);
        this.prospectiveFarmersRel = (LinearLayout) findViewById(R.id.prospectiveFarmersRel);
        this.areaExtensionRel = (LinearLayout) findViewById(R.id.areaExtensionRel);
        this.layout1 = (ConstraintLayout) findViewById(R.id.card1);
        this.layout2 = (ConstraintLayout) findViewById(R.id.card2);//ConstraintLayout
        this.layout3 = (ConstraintLayout) findViewById(R.id.card3);
        this.layout4 = (ConstraintLayout) findViewById(R.id.card4);
        this.layout5 = (ConstraintLayout) findViewById(R.id.card5);
        this.layout6 = (ConstraintLayout) findViewById(R.id.card6);
        LinearLayout complaintshRel = (LinearLayout) findViewById(R.id.complaintshRel);
        LinearLayout krasRel = (LinearLayout) findViewById(R.id.krasRel);
        LinearLayout alertsRel = (LinearLayout) findViewById(R.id.alertsRel1);
        LinearLayout plotSplitRel = (LinearLayout) findViewById(R.id.plotSplitRel);
        visitDetails = findViewById(R.id.visitDetails);
        circleView = (CircleView) findViewById(R.id.countTxt);
        ImageView extension_logbook = (ImageView) findViewById(R.id.extensionlogbook);
        iv_punchIcon = (ImageView) findViewById(R.id.iv_punchIcon);
        transportServiceLayout = (RelativeLayout) findViewById(R.id.transportServiceLayout);

        planationAuditRel = (LinearLayout) findViewById(R.id.planationAuditRel);

        mapsLayout = findViewById(R.id.mapsLayout);

        dataAccessHandler = new DataAccessHandler(this);

        ImageView notificationicon = (ImageView) findViewById(R.id.notficationicon);

        notificationicon.setOnClickListener(v -> startActivity(new Intent(HomeScreen.this, NotificationsScreen.class)));
        SharedPreferences sharedPreferences = getSharedPreferences(CommonConstants.punchSharedPreferences, Context.MODE_PRIVATE);
        boolean punchStatus = sharedPreferences.getBoolean(CommonConstants.punchStatus, false);

        checkPunchStatusAndPerformAction();
        List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);

        for (Map<String, String> right : activityRights) {
            String name = right.get("Name");
            String desc = right.get("Desc");
            String screenName = right.get("ScreenName");

            Log.d("ActivityRight", "Name: " + name + ", Desc: " + desc + ", Screen: " + screenName);
        }
//        TracktypeID = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().gettrcketype(CommonConstants.USER_ID));
//        Log.d("TracktypeID", TracktypeID+"");
//        if (!punchStatus) {
//            showCheckInDialog(false);
//        }
//        else if (shouldShowCheckOutDialog()) {
//            showCheckInDialog(true);
//        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchCurrentLocation();

        areaExtensionRel.setOnClickListener(view -> {
            FragmentManager fm = getSupportFragmentManager();
            RegistrationTypeChooser registrationTypeChooser = new RegistrationTypeChooser();
            registrationTypeChooser.show(fm, "fragment_edit_name");
        });
        layout1.setOnClickListener(view -> showAreaExtensionDialog());
//        testdialog.setOnClickListener(view -> showCheckInDialog());

        layout2.setOnClickListener(v -> {
            showConversionDialog();
           /* resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_CONVERSION;
            Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
            startActivity(intent);*/
        });
        layout3.setOnClickListener(view -> showpalmcareDialog());

//        layout4.setOnClickListener(view -> {
//            // create new activity  here shows list button add one button after then will call fragment complaintsDetailsFragment
//              CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_COMPLAINT;
//            Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
//            startActivity(intent);
//            //  Intent in_compalints = new Intent(HomeScreen.this,ComplaintsScreenActivity.class).putExtra("plot",false);
////            Intent in_compalints = new Intent(HomeScreen.this, ComplaintsScreenActivity.class);
////            startActivity(in_compalints);
//        });

        prospectiveFarmersRel.setOnClickListener(view -> {
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VPF;
            Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
            startActivity(intent);

        });


        iv_punchIcon.setOnClickListener(view -> {
            showCheckInDialog(true);
        });

/*        layout2.setOnClickListener(v -> {   //Field Conversion todo
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_CONVERSION;
            Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
            startActivity(intent);
        });*/
//
//        cropMaintenanceRel.setOnClickListener(view -> {
//            resetPrevRegData();
//            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_CP_MAINTENANCE;
//            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
//        });



   /*     layout3.setOnClickListener(view -> {
            resetPrevRegData();
            startActivity(new Intent(HomeScreen.this, palmcareScreen.class));
        });*/

//        harvestingRel.setOnClickListener(view -> {
//            resetPrevRegData();
//            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_HARVESTING;
//            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
//        });

        planationAuditRel.setOnClickListener(view -> {
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_PLANTATION_AUDIT;
            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
        });

        imagesRel.setOnClickListener(view -> {
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_IMAGESUPLOADING;
            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
        });

        visitDetails.setOnClickListener(view -> {
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VISIT_REQUESTS;
            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
        });


        refreshRel.setOnClickListener(view -> {
            resetPrevRegData();
            startActivity(new Intent(HomeScreen.this, RefreshSyncActivity.class));
        });

//        mapsLayout.setOnClickListener(view -> {
//            resetPrevRegData();
//            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_Viewonmaps;
//            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
//        });
        mapsLayout.setOnClickListener(view -> {
            resetPrevRegData();
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_Viewonmaps;
            startActivity(new Intent(HomeScreen.this, FiltermapsActivity.class));
        });
//        transportServiceLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetPrevRegData();
//                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VISIT_REQUESTS;
//                startActivity(new Intent(HomeScreen.this, TransportActivity.class));
//            }
//        });
        //  List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(262);
        List<String> activityRightsScreens = new ArrayList<>();
        for (Map<String, String> right : activityRights) {
            activityRightsScreens.add(right.get("Name")); // assuming "Name" is the screen permission key
        }

// Layout4: Complaints (e.g., CanRegisterComplaints)
        layout4.setOnClickListener(view -> {
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_COMPLAINT;
            Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
            startActivity(intent);
        });
//        if (activityRightsScreens.contains("CanViewGrowerComplaints")) {
//            layout4.setOnClickListener(view -> {
//                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_COMPLAINT;
//                Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
//                startActivity(intent);
//            });
//        } else {
//            layout4.setVisibility(View.GONE);
//        }

// Layout5: KRAs (e.g., CanViewKRAs)
        if (activityRightsScreens.contains("CanViewUserKRA")) {
            layout5.setOnClickListener(v -> {
                startActivity(new Intent(HomeScreen.this, KrasDisplayScreen.class));
            });
        } else {
            layout5.setVisibility(View.GONE);
        }

//        layout5.setOnClickListener(v -> startActivity(new Intent(HomeScreen.this, KrasDisplayScreen.class)));

        alertsRel.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            AlertsChooser alertsTypeChooser = new AlertsChooser();
            alertsTypeChooser.show(fm, "fragment_edit_name");
//                startActivity(new Intent(HomeScreen.this, NotificationsScreen.class));

        });
        plotSplitRel.setOnClickListener(view -> {
            CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_PLOT_SPLIT;
            startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));

        });
        layout6.setOnClickListener(v -> {

            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(new Intent(HomeScreen.this, LogBookScreenActivity.class));

                } else {
                    UiUtils.showCustomToastMessage("Please Turn On GPS", getApplicationContext(), 1);

                }

            } else {
                if (CommonUtils.isLocationPermissionGranted(getApplicationContext())) {
                    startActivity(new Intent(HomeScreen.this, LogBookScreenActivity.class));
                } else {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.v("@@@LLL", "Hello1");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                100);

                        if (CommonUtils.isLocationPermissionGranted(getApplicationContext())) {
                            startActivity(new Intent(HomeScreen.this, LogBookScreenActivity.class));
                        } else {
                            UiUtils.showCustomToastMessage("Please Turn On GPS", getApplicationContext(), 1);

                        }
                    }
                }
            }
        });
    }

    private void checkPunchStatusAndPerformAction() {
        try {
            DataAccessHandler dataAccessHandler = new DataAccessHandler(getApplicationContext());
            Cursor cursor = dataAccessHandler.getTodayLatestPunchDetails();

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("Id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("UserId"));
                String logDate = cursor.getString(cursor.getColumnIndexOrThrow("LogDate"));
                int isPunchIn = cursor.getInt(cursor.getColumnIndexOrThrow("IsPunchIn"));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("Longitude"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("Address"));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow("FileName"));
                String fileLocation = cursor.getString(cursor.getColumnIndexOrThrow("FileLocation"));
                String fileExtension = cursor.getString(cursor.getColumnIndexOrThrow("FileExtension"));
                int serverUpdatedStatus = cursor.getInt(cursor.getColumnIndexOrThrow("ServerUpdatedStatus"));
                if(isPunchIn == 0){
                    iv_punchIcon.setVisibility(View.GONE);
//                    showCheckInDialog(false);
                } else {
                    iv_punchIcon.setVisibility(View.VISIBLE);
                }
                Log.d("EMP_PUNCH", "Id: " + id);
                Log.d("EMP_PUNCH", "UserId: " + userId);
                Log.d("EMP_PUNCH", "LogDate: " + logDate);
                Log.d("EMP_PUNCH", "IsPunchIn: " + isPunchIn);
                Log.d("EMP_PUNCH", "Latitude: " + latitude);
                Log.d("EMP_PUNCH", "Longitude: " + longitude);
                Log.d("EMP_PUNCH", "Address: " + address);
                Log.d("EMP_PUNCH", "FileName: " + fileName);
                Log.d("EMP_PUNCH", "FileLocation: " + fileLocation);
                Log.d("EMP_PUNCH", "FileExtension: " + fileExtension);
                Log.d("EMP_PUNCH", "ServerUpdatedStatus: " + serverUpdatedStatus);
            } else {
                Log.d("EMP_PUNCH", "No punch record found for today.");
                showCheckInDialog(false);
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean shouldShowCheckInDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 9 && hour < 18;
    }


    private boolean shouldShowCheckOutDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 18 && hour < 24;
    }

    public void showCheckInDialog(boolean isPunchIn) {

        isPunchInFlag = isPunchIn; // save flag
        Log.d("isPunchInFlag=========",isPunchInFlag +"");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(!isPunchIn){
            builder.setCancelable(false);
        }
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.punchin_dialog, null);
        builder.setView(dialogView);

//        AlertDialog dialog = builder.create();
        dialog = builder.create();

        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        TextView dialog_content = dialogView.findViewById(R.id.dialog_content);
        TextView timeText = dialogView.findViewById(R.id.current_time);
        Button captureBtn = dialogView.findViewById(R.id.btn_capture);
        MapView mapView = dialogView.findViewById(R.id.map_view);

        dialog_title.setText(isPunchIn ? "Punch Out" : "Punch In");
        dialog_content.setText(isPunchIn ? "Time to go home!" : "It's time for another great day!");
        captureBtn.setText(isPunchIn ? "Punch Out" : "Take Picture");

        // Set current time
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        timeText.setText(currentTime);

        // Initialize map view
        mapView.onCreate(dialog.onSaveInstanceState());
        mapView.onResume();

        // Get user's current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Display user's current location on map
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            });
        }

        captureBtn.setOnClickListener(v -> {
            dialog.dismiss();
            dispatchTakePictureIntent(CAMERA_REQUEST, isPunchIn ? 0 : 1);
            Log.d("======>isPunchIninsert", isPunchInFlag+"");

        });

        dialog.show();
    }

    public void insertPunchDetails(int isPunchIn) {
        Log.d("======>isPunchIninsert", isPunchIn+"");
        try {
            List<LinkedHashMap> data = new ArrayList<>();

            String address = fetchAddress();

            LinkedHashMap map = new LinkedHashMap();
            map.put("UserId", CommonConstants.USER_ID);
            map.put("LogDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            map.put("IsPunchIn", isPunchIn);
            map.put("Latitude", latitude);
            map.put("Longitude", longitude);
            map.put("Address", address);
            map.put("FileName", "FileName");
            map.put("FileLocation", finalFile.getAbsolutePath());
            map.put("FileExtension", CommonConstants.JPEG_FILE_SUFFIX);
            map.put("ServerUpdatedStatus", 0);

            data.add(map);

            dataAccessHandler.saveData(DatabaseKeys.EmpPunchDetails, data, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        // show success message
                        UiUtils.showCustomToastMessage("Employee Punch Details Saved Successfully", getApplicationContext(), 0);

                        if (isPunchIn == 1){
                            iv_punchIcon.setVisibility(View.VISIBLE);
                        } else {
                            iv_punchIcon.setVisibility(View.GONE);
                        }
                    } else {
                        // show error message
                        UiUtils.showCustomToastMessage("Failed to Save Employee Punch Details: "+ result, getApplicationContext(), 1);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Failed to Save Employee Punch Details", getApplicationContext(), 1);
        }

    }

    private String getAddressByLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


 /*   private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpPhotoFile(isPunchInFlag);
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }*/

    private void dispatchTakePictureIntent(int actionCode, int isPunchIn) {
        currentIsPunchIn = isPunchIn;
        Log.d("======>isPunchIninsert", currentIsPunchIn+"");// store the value
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;
        mCurrentPhotoPath = null;
        try {
            f = setUpPhotoFile(isPunchIn);
            mCurrentPhotoPath = f.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }




    private File setUpPhotoFile(int isPunchIn) throws IOException {

        File f = createImageFile(isPunchIn);
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

//    private File createImageFile() {
//        File pictureDirectory = new File(CommonUtils.get3FFileRootPath() + "EmpPunchDetails");
//        if (!pictureDirectory.exists()) {
//            pictureDirectory.mkdirs();
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());
//        finalFile = new File(pictureDirectory, "PunchDetails_" + timeStamp + CommonConstants.JPEG_FILE_SUFFIX);
//        return finalFile;
//    }
    private File createImageFile(int isPunchIn) {
        File pictureDirectory = new File(CommonUtils.get3FFileRootPath() + "EmpPunchDetails");
        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (isPunchIn == 1) {
            finalFile = new File(pictureDirectory, "PunchIn_" + timeStamp + CommonConstants.JPEG_FILE_SUFFIX);
        } else {
            finalFile = new File(pictureDirectory, "PunchOut_" + timeStamp + CommonConstants.JPEG_FILE_SUFFIX);
        }
        return finalFile;
    }


/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        handleBigCameraPhoto(isPunchInFlag);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mCurrentPhotoPath = null;
                    showCheckInDialog(isPunchInFlag);
                }
                break;
            }


        } // switch
    }*/
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAMERA_REQUEST) {
        if (resultCode == RESULT_OK) {
            try {
                handleBigCameraPhoto(currentIsPunchIn); // use stored value
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mCurrentPhotoPath = null;
            showCheckInDialog(isPunchInFlag);
        }
    }

//    if (requestCode == CAMERA_REQUEST) {
//        if (resultCode == RESULT_OK) {
//            int isPunchIn = 1; // Default
//            if (data != null && data.hasExtra("IsPunchIn")) {
//                isPunchIn = data.getIntExtra("IsPunchIn", 1);
//            } else if (!isPunchInFlag) {
//                isPunchIn = 0;
//            }
//
//            try {
//                handleBigCameraPhoto(isPunchIn);
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            mCurrentPhotoPath = null;
//            showCheckInDialog(isPunchInFlag);
//        }
//    }
}


    private void handleBigCameraPhoto(int isPunchInFlag) {
        Log.d("======>709", isPunchInFlag+"");

        if (mCurrentPhotoPath != null) {
            setPic();
            addTimestampToImage(isPunchInFlag);
            galleryAddPic();
        }

    }

    private void setPic() {
        try {
            EmpPunchDetails punchDetails = new EmpPunchDetails();
            // Decode the image efficiently
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Optional: scale image to reduce memory
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = Math.max(photoW / 1024, photoH / 1024); // scale to max 1024px
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            // Rotate according to EXIF if required
            bitmap = rotateImageIfRequired(bitmap, mCurrentPhotoPath);
            // = rotateImageIfRequired(bm, imgFile.getAbsolutePath());

            // Convert to Base64 and send to server
            String base64string = ImageUtility.convertBitmapToString(bitmap);
            punchDetails.setByteImage(base64string);

            // ✅ No ImageView involved, so image will not display anywhere

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void addTimestampToImage(int isPunchIn) {
        try {
            File imageFile = new File(mCurrentPhotoPath);
            Uri photoUri = Uri.fromFile(imageFile);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

            // ✅ Correct rotation
            bitmap = rotateImageIfRequired(bitmap, mCurrentPhotoPath);

            // Convert to mutable bitmap
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            Paint paint = new Paint();
            Paint backgroundPaint = new Paint();

            // ✅ Text Settings
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextSize(60);
            paint.setAntiAlias(true);
            paint.setShadowLayer(5.0f, 2.0f, 2.0f, Color.BLACK);
            paint.setTextAlign(Paint.Align.LEFT);

            // ✅ Background Settings
            backgroundPaint.setColor(Color.BLACK);
            backgroundPaint.setAlpha(180);

            // ✅ Prepare Text
            String timeStamp = "Date: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(new Date());
            String locationText = "Lat: " + latitude + "  Long: " + longitude;
            String addressText = "Address:  " + fetchAddress();

            // ✅ Wrap Long Address into Multiple Lines
            List<String> wrappedAddress = wrapText(addressText, paint, canvas.getWidth() * 0.8f);

            // ✅ Calculate Text Positions
            float xPos = canvas.getWidth() * 0.01f;
            float yPos = canvas.getHeight() - 350;
            float padding = 20;
            float rectHeight = (paint.getTextSize() * (3 + wrappedAddress.size())) + (padding * 2);

            // ✅ Draw Background Rectangle
            RectF backgroundRect = new RectF(
                    xPos - padding,
                    yPos - (paint.getTextSize() + padding),
                    canvas.getWidth() - 40,
                    yPos + rectHeight
            );
            canvas.drawRoundRect(backgroundRect, 15, 15, backgroundPaint);

            // ✅ Draw Text
            canvas.drawText(timeStamp, xPos, yPos, paint);
            canvas.drawText(locationText, xPos, yPos + paint.getTextSize() * 1.5f, paint);

            float addressYPos = yPos + paint.getTextSize() * 3f;
            int maxLines = 2;
            for (int i = 0; i < Math.min(wrappedAddress.size(), maxLines); i++) {
                canvas.drawText(wrappedAddress.get(i), xPos, addressYPos, paint);
                addressYPos += paint.getTextSize() * 1.3f;
            }

            // ✅ Save the final image
            saveImage(mutableBitmap);

            // ✅ Insert punch details according to flag
            Log.d("======>isPunchIninsert818", isPunchIn+"");

            insertPunchDetails(isPunchIn);
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Failed to load image", getApplicationContext(), 1);
        }
    }


    // Function to wrap text into multiple lines based on canvas width
    private List<String> wrapText(String text, Paint paint, float maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (paint.measureText(currentLine + " " + word) > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void saveImage(Bitmap finalBitmap) {
        File file = new File(mCurrentPhotoPath);
        try (FileOutputStream out = new FileOutputStream(file)) {
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            Toast.makeText(this, "Image Saved with Timestamp!", Toast.LENGTH_SHORT).show();
            UiUtils.showCustomToastMessage("Image Saved with Timestamp!", getApplicationContext(), 0);
        } catch (IOException e) {
            android.util.Log.e("Save Image", "Error saving image", e);
        }
    }


//    private void fetchCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                        Log.d("punchDetails", "Lat: " + latitude + ", Lng: " + longitude);
//                    } else {
//                        Log.d("punchDetails", "Location is null");
//                    }
//                });
//    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.d("fetchCurrentLocation", "Latitude: " + latitude + ", Longitude: " + longitude);
                        } else {
                            Log.d("fetchCurrentLocation", "Location is null");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Log.d("LOCATION", "Permission denied");
            }
        }
    }

    private String fetchAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            } else {
                return "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to fetch address";
        }
    }


    /*    private Bitmap rotateImageIfRequired(Bitmap img, String photoPath) throws IOException {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return img; // No rotation needed
            }

            return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        }*/
    private Bitmap rotateImageIfRequired(Bitmap img, String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return img; // No rotation needed
            }

            return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

//    private void openCamera() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                return;
//            }
//
//            photoURI = FileProvider.getUriForFile(this,
//                    getPackageName() + ".provider",
//                    photoFile);
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName, ".jpg", storageDir);
//    }

//    private void getCurrentLocation() {
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("punchDetails", "Permission not granted");
//            return;
//        }
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//                        Log.d("punchDetails", "Lat: " + latitude + ", Lng: " + longitude);
//                        Log.d("punchDetails", "Captured image path: " + photoFile.getAbsolutePath());
//                        SharedPreferences prefs = getSharedPreferences(CommonConstants.punchSharedPreferences, Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean(CommonConstants.punchStatus, true);
//                        editor.apply();
//                    } else {
//                        Log.d("punchDetails", "Location is null");
//                    }
//                });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        try {
//            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//                if (photoFile != null) {
//                    getCurrentLocation();
    ////                     SharedPreferences prefs = getSharedPreferences(CommonConstants.punchSharedPreferences, Context.MODE_PRIVATE);
    ////                     SharedPreferences.Editor editor = prefs.edit();
    ////                     editor.putBoolean(CommonConstants.punchStatus, true);
    ////                     editor.apply();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("punchDetails", "Catch Exception: " + e.getMessage());
//        }
//    }

    private void showpalmcareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_palmcare, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get Activity Rights
        List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);
        List<Integer> activityRightIds = new ArrayList<>();
        for (Map<String, String> right : activityRights) {
            try {
                activityRightIds.add(Integer.parseInt(right.get("Id")));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Palm Care Option: Do Crop Maintenance
        View docrop = dialogView.findViewById(R.id.docrop);
        if (activityRightIds.contains(266)){
            docrop.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_CP_MAINTENANCE;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            docrop.setVisibility(View.GONE);
        }

        // Close Crop Maintenance
        View closecrop = dialogView.findViewById(R.id.closecrop);
        if (activityRightIds.contains(267)){
            closecrop.setOnClickListener(v -> {
                resetPrevRegData();
                startActivity(new Intent(this, ClosecropMaintenanceList.class));
                dialog.dismiss();
            });
        } else {
            closecrop.setVisibility(View.GONE);
        }

        // Do Harvesting
        View doharvesting = dialogView.findViewById(R.id.doharvesting);
        if (activityRightIds.contains(268)){
            doharvesting.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_HARVESTING;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            doharvesting.setVisibility(View.GONE);
        }

        // Close Harvesting
        View closeharvesting = dialogView.findViewById(R.id.closeharvesting);
        if (activityRightIds.contains(269)){

            closeharvesting.setOnClickListener(v -> {
                resetPrevRegData();
                startActivity(new Intent(this, CloseHarvestingList.class));
                dialog.dismiss();
            });
        } else {
            closeharvesting.setVisibility(View.GONE);
        }

        // Not Visited Plots
        View notvisitedplots = dialogView.findViewById(R.id.notvisitedplots);
        if (activityRightIds.contains(270)){
            notvisitedplots.setOnClickListener(v -> {
                resetPrevRegData();
                startActivity(new Intent(this, NoVisitPlotslistScreen.class));
                dialog.dismiss();
            });
        } else {
            notvisitedplots.setVisibility(View.GONE);
        }

        // Plot Visits
        View visit = dialogView.findViewById(R.id.visit);
        if (activityRightIds.contains(271)){
            visit.setOnClickListener(v -> {
                resetPrevRegData();
                Intent alertsIntent = new Intent(this, AlertsDisplayScreen.class);
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_VISITS);
                startActivity(alertsIntent);
                dialog.dismiss();
            });
        } else {
            visit.setVisibility(View.GONE);
        }

        // Missing Trees
        View missingtrees = dialogView.findViewById(R.id.missingtrees);
        if (activityRightIds.contains(271)){
            missingtrees.setOnClickListener(v -> {
                Intent alertsIntent = new Intent(this, AlertsDisplayScreen.class);
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_MISSING_TREES);
                startActivity(alertsIntent);
                dialog.dismiss();
            });
        } else {
            missingtrees.setVisibility(View.GONE);
        }

        // View on Map
//        View viewonmap = dialogView.findViewById(R.id.viewonmap);
//        if (activityRightsScreens.contains("CanViewPlotsOnMap")) {
//            viewonmap.setOnClickListener(v -> {
//                resetPrevRegData();
//                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_Viewonmaps;
//                startActivity(new Intent(this, FiltermapsActivity.class));
//                dialog.dismiss();
//            });
//        } else {
//            viewonmap.setVisibility(View.GONE);
//        }

        // Visit Request
        View visitrequest = dialogView.findViewById(R.id.visitrequest);
        if (activityRightIds.contains(272)){
            visitrequest.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VISIT_REQUESTS;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            visitrequest.setVisibility(View.GONE);
        }
    }

    private void showConversionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_conversion, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get Activity Rights
        List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);
        List<Integer> activityRightIds = new ArrayList<>();
        for (Map<String, String> right : activityRights) {
            try {
                activityRightIds.add(Integer.parseInt(right.get("Id")));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Helper function to configure view
        View option;

        // 1. Retake Boundaries
        option = dialogView.findViewById(R.id.optionretakeboundaries);
        if (activityRightIds.contains(39)){
            //  if (activityRightsScreens.contains("CanRetakeGeoBoundariesOrGeoTag")) {
            option.setOnClickListener(v -> {
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_PLOT_SPLIT;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }

        // 2. Raise Dispatch Saplings Request
        option = dialogView.findViewById(R.id.raiseDispatchSaplingsRequest);
        if (activityRightIds.contains(257)){

            option.setOnClickListener(v -> {
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_raiseDispatchSaplingsRequest;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }

        // 3. Manage Advance Details
        option = dialogView.findViewById(R.id.manageAdvanceDetails);
        if (activityRightIds.contains(217)){
            //if (activityRightsScreens.contains("CanManageApprovedPlotAdvanceDetails")) {
            option.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_MANAGE_ADVANCE_DETAILS;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }

        // 4. Add Dispatch Saplings
        option = dialogView.findViewById(R.id.addDispatchSaplings);
        if (activityRightIds.contains(257)){
            //  if (activityRightsScreens.contains("CanManageSaplingDispatchRequest")) {
            option.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_addDispatchSaplings;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }

        // 5. View Dispatch Sapling Details
        option = dialogView.findViewById(R.id.viewDispatchSaplingDetails);
        if (activityRightIds.contains(256)){
            //  if (activityRightsScreens.contains("CanViewSaplingDispatchRequest")) {
            option.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.VIEW_DISPATCH_SAPLING_DETAILS;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }

        // 6. Conversion
        option = dialogView.findViewById(R.id.optionConversion);
        if (activityRightIds.contains(265)){
            // if (activityRightsScreens.contains("CanDoFieldConversion")) {
            option.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_CONVERSION;
                startActivity(new Intent(HomeScreen.this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            option.setVisibility(View.GONE);
        }
    }

    private void showAreaExtensionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_area_extension, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Example: List already filled with ActivityRight screen names
        // List<String> activityRightsScreens = ...

        // Handle visibility and clicks based on rights
        List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);

        List<Integer> activityRightIds = new ArrayList<>();
        for (Map<String, String> right : activityRights) {
            try {
                activityRightIds.add(Integer.parseInt(right.get("Id")));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
//        List<String> activityRightsScreens = new ArrayList<>();
//        for (Map<String, String> right : activityRights) {
//            String name = right.get("Name");
//            String desc = right.get("Desc");
//            String screenName = right.get("ScreenName");
//
//            Log.d("ActivityRight", "Name: " + name + ", Desc: " + desc + ", Screen: " + screenName);
//            activityRightsScreens.add(name); // Important: Add screenName, not name
//        }

        Log.d("ActivityRight", "Ids: " + activityRightIds.toString());
        View optionNewGrower = dialogView.findViewById(R.id.optionNewGrower);
        if (activityRightIds.contains(66)){
            //   if (activityRightsScreens.contains("CanRegisterGrowerandField")) {
            optionNewGrower.setOnClickListener(v -> {
                resetPrevRegData();
                startActivity(new Intent(this, LocationSelectionScreen.class));
                dialog.dismiss();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_FARMER;
            });
        } else {
            optionNewGrower.setVisibility(View.GONE);
        }

        View optionDripFollowup = dialogView.findViewById(R.id.optiondripfollowup);
        if (activityRightIds.contains(215)){
            //    if (activityRightsScreens.contains("CanManageDripIrrigation")) {
            optionDripFollowup.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_DripFOLLOWUP;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            optionDripFollowup.setVisibility(View.GONE);
        }

        View optionExistingGrower = dialogView.findViewById(R.id.optionExistingGrower);
        if (activityRightIds.contains(66)){
//             if (activityRightsScreens.contains("CanRegisterGrowerandField")) {
            optionExistingGrower.setOnClickListener(v -> {
                // Toast.makeText(this, "New Field Existing Grower", Toast.LENGTH_SHORT).show();
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_PLOT;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            optionExistingGrower.setVisibility(View.GONE);
        }

        View optionProspective = dialogView.findViewById(R.id.optionProspective);
        if (activityRightIds.contains(213)){
            //if (activityRightsScreens.contains("CanViewProspectiveFields")) {
            optionProspective.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VPF;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            optionProspective.setVisibility(View.GONE);
        }

        View optionFollowUp = dialogView.findViewById(R.id.optionFollowUp);
        if (activityRightIds.contains(264)){
            // if (activityRightsScreens.contains("CanAddGrowerFollowupData")) {
            optionFollowUp.setOnClickListener(v -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_FOLLOWUP;
                startActivity(new Intent(this, SearchFarmerScreen.class));
                dialog.dismiss();
            });
        } else {
            optionFollowUp.setVisibility(View.GONE);
        }

        View fieldFollowUp = dialogView.findViewById(R.id.fieldFollowUp);
        if (activityRightIds.contains(263)){
            //    if (activityRightsScreens.contains("CanviewGrowerFollowupData")) {
            fieldFollowUp.setOnClickListener(v -> {
                Intent alertsIntent = new Intent(this, AlertsDisplayScreen.class);
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_FOLLOWUP);
                startActivity(alertsIntent);
                dialog.dismiss();
            });
        } else {
            fieldFollowUp.setVisibility(View.GONE);
        }
    }

/*
 private void showAreaExtensionDialog() {
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     LayoutInflater inflater = getLayoutInflater();
     View dialogView = inflater.inflate(R.layout.dialog_area_extension, null);
     builder.setView(dialogView);
     AlertDialog dialog = builder.create();
     dialog.show();
     dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

     // Handle clicks
     dialogView.findViewById(R.id.optionNewGrower).setOnClickListener(v -> {
                 resetPrevRegData();
                 startActivity(new Intent(this, LocationSelectionScreen.class));
                 dialog.dismiss();
                 CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_FARMER;
          */
/*  Toast.makeText(this, "New Field New Grower", Toast.LENGTH_SHORT).show();
             dialog.dismiss();*//*

             }
     );
     dialogView.findViewById(R.id.optiondripfollowup).setOnClickListener(v -> {
                 resetPrevRegData();

                 CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_DripFOLLOWUP;
                 startActivity(new Intent(this, SearchFarmerScreen.class));
                 dialog.dismiss();
          */
/*  Toast.makeText(this, "New Field New Grower", Toast.LENGTH_SHORT).show();
             dialog.dismiss();*//*

             }
     );


     dialogView.findViewById(R.id.optionExistingGrower).setOnClickListener(v -> {
         Toast.makeText(this, "New Field Existing Grower", Toast.LENGTH_SHORT).show();
         resetPrevRegData();
         CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_PLOT;
         startActivity(new Intent(this, SearchFarmerScreen.class));
         dialog.dismiss();
     });

     dialogView.findViewById(R.id.optionProspective).setOnClickListener(v -> {
         resetPrevRegData();
         CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_VPF;
         Intent intent = new Intent(HomeScreen.this, SearchFarmerScreen.class);
         startActivity(intent);
         dialog.dismiss();
     });

     dialogView.findViewById(R.id.optionFollowUp).setOnClickListener(v -> {
         resetPrevRegData();
         CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_FOLLOWUP;
         startActivity(new Intent(this, SearchFarmerScreen.class));
         dialog.dismiss();
     });
     final Intent alertsIntent = new Intent(this, AlertsDisplayScreen.class);

     dialogView.findViewById(R.id.fieldFollowUp).setOnClickListener(v -> {
         alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_FOLLOWUP);
         startActivity(alertsIntent);
         dialog.dismiss();
     });
 }

*/

    //on Resume method
    @Override
    public void onResume() {
        super.onResume();
        List<String> userActivities = (List<String>) DataManager.getInstance().getDataFromManager(DataManager.USER_ACTIVITY_RIGHTS);
        if (null != userActivities && userActivities.contains(CommonConstants.CanManageFarmers)) {
            handleViewVisibilities(View.VISIBLE);
        } else if (null != userActivities && userActivities.contains(CommonConstants.CanViewFarmers)) {
            handleViewVisibilities(View.INVISIBLE);
            prospectiveFarmersRel.setVisibility(View.VISIBLE);
        } else {
            handleViewVisibilities(View.INVISIBLE);
        }

        String unreadNotificationsCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getUnreadNotificationsCountQuery());
        circleView.setTitleText(unreadNotificationsCount);

    }

    //Dialog for choosing type of registration
    public static class RegistrationTypeChooser extends DialogFragment {

        public RegistrationTypeChooser() {
            // Required empty public constructor
        }

        public static RegistrationTypeChooser newInstance(String type) {
            RegistrationTypeChooser fragment = new RegistrationTypeChooser();
            Bundle args = new Bundle();
            args.putString("type", "" + type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.area_extension_choose_dialog, container);
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

            Button newRegRel = (Button) view.findViewById(R.id.firstRel);
            newRegRel.setOnClickListener(view1 -> {
                resetPrevRegData();
                startActivity(new Intent(getActivity(), LocationSelectionScreen.class));
                getDialog().dismiss();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_FARMER;
            });

            Button newPlotRegRel = (Button) view.findViewById(R.id.secondRel);
            newPlotRegRel.setOnClickListener(view12 -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_NEW_PLOT;
                startActivity(new Intent(getActivity(), SearchFarmerScreen.class));
                getDialog().dismiss();
            });


            Button followUpRegRel = (Button) view.findViewById(R.id.thirdRel);
            followUpRegRel.setOnClickListener(view13 -> {
                resetPrevRegData();
                CommonConstants.REGISTRATION_SCREEN_FROM = CommonConstants.REGISTRATION_SCREEN_FROM_FOLLOWUP;
                startActivity(new Intent(getActivity(), SearchFarmerScreen.class));
                getDialog().dismiss();
            });

            return view;
        }
    }

    //Dialog for choosing
    public static class AlertsChooser extends DialogFragment {
        public static final int TYPE_PLOT_FOLLOWUP = 1;
        public static final int TYPE_PLOT_VISITS = 2;
        public static final int TYPE_PLOT_MISSING_TREES = 3;
        public static final int TYPE_PLOT_NOT_VISIT = 4;
        public static final String ALERT_TYPE = "alert_type";
        public CircleView plotFollowUpCountTxt, visitsCountTxt, missingTreesCountTxt;

        public AlertsChooser() {
            // Required empty public constructor
        }

        public static AlertsChooser newInstance(String type) {
            AlertsChooser fragment = new AlertsChooser();
            Bundle args = new Bundle();
            args.putString("type", "" + type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.alerts_layout, container);
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

            DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
            String plotFollowupCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getAlertsCount(AlertType.ALERT_PLOT_FOLLOWUP.getValue()));

//            plotFollowUpCountTxt = (CircleView) view.findViewById(R.id.countPlotTxt);
//            visitsCountTxt = (CircleView) view.findViewById(R.id.visitsCountPlotTxt);
//            missingTreesCountTxt = (CircleView) view.findViewById(R.id.missingTreesCountPlotTxt);

            //   plotFollowUpCountTxt.setTitleText(plotFollowupCount);
            Button pfuLayout = (Button) view.findViewById(R.id.pfuLayout);

            final Intent alertsIntent = new Intent(getActivity(), AlertsDisplayScreen.class);
            pfuLayout.setOnClickListener(view1 -> {
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_FOLLOWUP);
                startActivity(alertsIntent);
                dismiss();
            });

            Button visitsRel = (Button) view.findViewById(R.id.visitsRel);
            visitsRel.setOnClickListener(view12 -> {
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_VISITS);
                startActivity(alertsIntent);
                dismiss();
            });


            Button missingTreesRel = (Button) view.findViewById(R.id.missingTreesRel);
            missingTreesRel.setOnClickListener(view13 -> {
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_MISSING_TREES);
                startActivity(alertsIntent);
                dismiss();
            });

            Button notVisitedPlots = view.findViewById(R.id.notVisitedPlots);
            notVisitedPlots.setOnClickListener(v -> {
                alertsIntent.putExtra(ALERT_TYPE, TYPE_PLOT_NOT_VISIT);
                startActivity(alertsIntent);
                dismiss();
            });

            return view;
        }
    }

    //Show/hide functionality
    public void handleViewVisibilities(int visibility) {
        //  refreshRel.setVisibility(visibility);
        cropMaintenanceRel.setVisibility(visibility);
        areaExtensionRel.setVisibility(visibility);
        conversionRel.setVisibility(visibility);
        prospectiveFarmersRel.setVisibility(visibility);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.plots_display_screen, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.english){
            CommonUtils.changeLanguage(this, "en");
            // UiUtils.showCustomToastMessage("You Selected English",CollectionCenterHomeScreen.this,1);
        }

        if (item.getItemId() == R.id.hindi){
            CommonUtils.changeLanguage(this,"hi");

        }
        switch (item.getItemId()){

            case R.id.english:
                CommonUtils.changeLanguage(this, "en");
                break;
            case R.id.hindi:
                CommonUtils.changeLanguage(this, "hi");
                break;



        }
        Intent refreshIntent=new Intent(HomeScreen.this,HomeScreen.class);
        finish();
        startActivity(refreshIntent);

       *//* Intent refresh = new Intent(this, CollectionCenterHomeScreen.class);
        startActivity(refresh);//Start the same Activity*//*
        return super.onOptionsItemSelected(item);
    }*/

}



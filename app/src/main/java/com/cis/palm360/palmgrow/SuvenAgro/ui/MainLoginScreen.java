package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cis.palm360.palmgrow.SuvenAgro.FaLogTracking.FalogService;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.PalmOilDatabase;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataSyncHelper;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.UserDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.UserSync;
import com.cis.palm360.palmgrow.SuvenAgro.helper.PrefUtil;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager.USER_DETAILS;
import static com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager.USER_VILLAGES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


//Login Screen
public class MainLoginScreen extends AppCompatActivity {

    public static final String LOG_TAG = MainLoginScreen.class.getName();

    private TextView imeiNumberTxt;
    private TextView versionnumbertxt, dbVersionTxt;
    private EditText userID;
    private EditText passwordEdit;
    private Button signInBtn;
    private String userId;
    private String password;
    DataAccessHandler dataAccessHandler;
    FloatingActionButton sync;
    LocationManager lm;
    List<String> userModules = new ArrayList<>();


    //Initializing the Class and on Click Listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.sync=(FloatingActionButton) findViewById(R.id.sync);
//        toolbar.setTitle(R.string.login_screen);
//        setSupportActionBar(toolbar);
         dataAccessHandler = new DataAccessHandler(MainLoginScreen.this);


        initView();

        imeiNumberTxt.setText(CommonUtils.getIMEInumber(this));
        versionnumbertxt.setText(CommonUtils.getAppVersion(this));
        dbVersionTxt.setText(""+ PalmOilDatabase.DATA_VERSION);

        String query = Queries.getInstance().getUserDetailsNewQuery(CommonUtils.getIMEInumber(this));


        final UserDetails userDetails = (UserDetails) dataAccessHandler.getUserDetails(query, 0);

        if (null != userDetails ) {
//            if (CommonUtils.isLocationPermissionGranted(MainLoginScreen.this) ) {
//                startService(new Intent(this, FalogService.class));
//            }
            // Updated Services For Android Q ###  CIS ## 21/05/21\\

            if (CommonUtils.isLocationPermissionGranted(MainLoginScreen.this)) {
                Log.d("PermissionCheck", "Location permission granted");

                Intent serviceIntent = new Intent(MainLoginScreen.this, FalogService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        Log.d("ServiceStart", "Attempting to start service as foreground (SDK >= O)");
                        getApplicationContext().startForegroundService(serviceIntent);
                        Log.d("ServiceStart", "Foreground service started successfully");
                    } catch (Exception e) {
                        Log.e("ServiceStartError", "Failed to start foreground service: " + e.getMessage(), e);
                    }
                } else {
                    try {
                        Log.d("ServiceStart", "Attempting to start service as normal (SDK < O)");
                        getApplicationContext().startService(serviceIntent);
                        Log.d("ServiceStart", "Service started successfully");
                    } catch (Exception e) {
                        Log.e("ServiceStartError", "Failed to start service: " + e.getMessage(), e);
                    }
                }
            } else {
                Log.e("PermissionCheck", "Location permission not granted");
            }

            userID.setText(" "+userDetails.getUserName());
            passwordEdit.setText(" "+userDetails.getPassword());

            List userVillages = dataAccessHandler.getSingleListData(Queries.getInstance().getUserVillages(userDetails.getId()));
            DataManager.getInstance().addData(USER_DETAILS, userDetails);
            if (!userVillages.isEmpty()) {
                DataManager.getInstance().addData(USER_VILLAGES, userVillages);
            }
            CommonConstants.USER_ID = userDetails.getId();
            CommonConstants.TAB_ID = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumber(MainLoginScreen.this)));

            CommonConstants.TAB_ID = CommonConstants.TAB_ID.replace("Tab", "T");
            CommonConstants.USER_CODE = userDetails.getUserCode();
            imeiNumberTxt.setText(CommonUtils.getIMEInumber(this)+" ("+CommonConstants.TAB_ID+")");
            List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(userDetails.getRoleId()));
            userModules  = dataAccessHandler.getSingleListData(Queries.getInstance().getuserappaccess(userDetails.getId()));
            Log.e("=======>",userModules.toString());
//            List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(1));
            DataManager.getInstance().addData(DataManager.USER_ACTIVITY_RIGHTS, userActivityRights);
            Log.v(LOG_TAG, "@@@@ activity rights ");
        } else {
            UiUtils.showCustomToastMessage("User not existed", MainLoginScreen.this, 1);
        }

//        DataSyncHelper.getAlertsData(MainLoginScreen.this, new ApplicationThread.OnComplete<String>() {
//            @Override
//            public void execute(boolean success, String result, String msg) {
//                if (success) {
//                } else {
//                   // UiUtils.showCustomToastMessage("Error while getting alerts Data", MainLoginScreen.this, 1);
//                }
//            }
//        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainLoginScreen.this, CropMaintenanceHomeScreen.class));
                userId = userID.getText().toString();
                password = passwordEdit.getText().toString();

                if (validateField()) {

                    if (userModules.contains("Palm Grow")) {
                        CommonUtils.hideKeyPad(MainLoginScreen.this, passwordEdit);
                        startActivity(new Intent(MainLoginScreen.this, HomeScreen.class));
                        finish();
                        System.out.println("Access granted to Palm Grow");
                    } else {
                        UiUtils.showCustomToastMessage("User does not have access to Palm Grow", MainLoginScreen.this, 1);
                      //  System.out.println("Access denied to Palm Grow");
                    }

                }
            }
        });
         sync.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 startMasterSync();

             }
         });



    }

    //add usersync details when master sync is performed
    public void addUserMasSyncDetails(){
        UserSync userSync;

        SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = simpledatefrmt.format(new Date());

        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp(""+"Palm Grow");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(1);
        userSync.setTransactionSync(0);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);
        long resul=  dataAccessHandler.addUserSync(userSync);
        if(resul>-1){
            Log.v("@@@MM","Success");
        }

    }


    //initializing the UI
    private void initView() {
        imeiNumberTxt = (TextView) findViewById(R.id.imeiNumberTxt);
        versionnumbertxt = (TextView) findViewById(R.id.versionnumbertxt);
        dbVersionTxt = (TextView) findViewById(R.id.dbVersiontxt);
        userID = (EditText) findViewById(R.id.userID);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        signInBtn = (Button) findViewById(R.id.signInBtn);
    }

    //Validations
    private boolean validateField() {
        if (TextUtils.isEmpty(userId)) {
            Toasty.error(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            userID.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toasty.error(this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwordEdit.requestFocus();
            return false;
        }
        return true;
    }


    //Start Master Sync
    public void startMasterSync() {

            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {

                    if (success) {

                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data syncing success", MainLoginScreen.this, 0);
                                ProgressBar.hideProgressBar();

// Your existing logic
                                List<UserSync> userSyncList = (List<UserSync>) dataAccessHandler.getUserSyncData(
                                        Queries.getInstance().countOfSync(CommonConstants.USER_ID)
                                );

                                if (userSyncList.size() == 0) {
                                    if (Integer.parseInt(CommonConstants.USER_ID) != 12345) {
                                        Log.v("@@@MM", "mas");
                                        addUserMasSyncDetails();
                                    }
                                } else {
                                    dataAccessHandler.updateMasterSync();
                                }

// Refresh screen after a small delay to show toast
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    finish();
                                    startActivity(getIntent());
                                }, 100);

                            }
                        });

                    } else {
                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data Syncing Failed", MainLoginScreen.this, 1);
                                ProgressBar.hideProgressBar();
                            }
                        });
                    }
                }
            });

    }
}

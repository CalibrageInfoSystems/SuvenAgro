package com.cis.palm360.palmgrow.SuvenAgro.ui;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Config;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Palm3FoilDatabase;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataSyncHelper;

import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropMaintenanceDocs;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DigitalContract;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = SplashScreen.class.getName();
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private Palm3FoilDatabase palm3FoilDatabase;
    private DataAccessHandler dataAccessHandler;
    private SharedPreferences sharedPreferences;

    private ArrayList<DigitalContract> digitalList = new ArrayList<>();
    private ArrayList<CropMaintenanceDocs> cmdocsList = new ArrayList<>();
    private DigitalContract digitalContract;
    private CropMaintenanceDocs cropMaintenanceDocs;

    private ActivityResultLauncher<Intent> mGetPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);

        if (!CommonUtils.isNetworkAvailable(this)) {
            UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
        }

        mGetPermission = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            UiUtils.showCustomToastMessage("Permission granted", SplashScreen.this, 0);
                        }
                    }
                }
        );

        takePermission();
    }

    private void takePermission() {
        if (isPermissionGranted()) {
            initializeApp();
        } else {
            requestPermission();
        }
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
                    && (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                mGetPermission.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mGetPermission.launch(intent);
            }
        }

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        permissions.add(Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
        }

        ActivityCompat.requestPermissions(
                this,
                permissions.toArray(new String[0]),
                REQUEST_CODE_PERMISSIONS
        );
    }



    private void initializeApp() {
        ensureDatabaseDirectory();
        try {
            palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
            palm3FoilDatabase.createDataBase();
            dbUpgradeCall();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Database init failed: " + e.getMessage());
        }

        dataAccessHandler = new DataAccessHandler(this);

        if (!CommonUtils.isNetworkAvailable(this)) {
            goToLogin();
            UiUtils.showCustomToastMessage("Please check your network connection", this, 1);
        } else {
            startMasterSync();
        }
    }

    private void ensureDatabaseDirectory() {
        File dbDir = new File(Environment.getExternalStorageDirectory(), "EcoPalm_Files/PalmGrow_Database");
        if (!dbDir.exists()) {
            boolean isCreated = dbDir.mkdirs();
            if (!isCreated) {
                Log.e(LOG_TAG, "Failed to create database directory");
            }
        }
    }


    private void dbUpgradeCall() {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(this, false);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, true).apply();
        }
    }

    private void startMasterSync() {
        if (CommonUtils.isNetworkAvailable(this) && !sharedPreferences.getBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, false)) {
            DataSyncHelper.performMasterSync(this, false, new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    ProgressBar.hideProgressBar();
                    if (success) {
                        sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
                        digitalpdfsave();
                    } else {
                        Log.v(LOG_TAG, "Master sync failed: " + msg);
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", () -> {
                            UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
                            goToLogin();
                        });
                    }
                }
            });
        } else {
            goToLogin();
        }
    }

    private void digitalpdfsave() {
        dataAccessHandler = new DataAccessHandler(this);
        digitalList = (ArrayList<DigitalContract>) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContract(), 1);

        for (DigitalContract contract : digitalList) {
            int stateId = contract.getStateId();
            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(
                    Queries.getInstance().getDigitalContractbystatecode(stateId), 0);
            String url = Config.image_url + "/" + digitalContract.getFileLocation() + "/" +
                    digitalContract.getFILENAME() + digitalContract.getFileExtension();

            new Downloadpdf_FileFromURL(digitalContract.getFILENAME(), digitalContract.getFileExtension()).execute(url);
        }

        cmdocssave();
    }

    private void cmdocssave() {
        dataAccessHandler = new DataAccessHandler(this);
        cmdocsList = (ArrayList<CropMaintenanceDocs>) dataAccessHandler.getCMDocsData(Queries.getInstance().getAllCMDocs(), 1);

        for (CropMaintenanceDocs doc : cmdocsList) {
            int sectionId = doc.getCMSectionId();
            cropMaintenanceDocs = (CropMaintenanceDocs) dataAccessHandler.getCMDocsData(
                    Queries.getInstance().getCMDocsbysectionId(sectionId), 0);
            String url = Config.image_url + "/" + cropMaintenanceDocs.getFileLocation() + "/" +
                    cropMaintenanceDocs.getFileName() + cropMaintenanceDocs.getFileExtension();

            new DownloadCMDocsFromURL(cropMaintenanceDocs.getFileName(), cropMaintenanceDocs.getFileExtension()).execute(url);
        }

        goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, MainLoginScreen.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isPermissionGranted()) {
                initializeApp();
            } else {
                UiUtils.showCustomToastMessage("Required permissions not granted", SplashScreen.this, 1);

                finish();
            }
        }
    }


    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int res : grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public class Downloadpdf_FileFromURL extends AsyncTask<String, Void, String> {

        private boolean downloadSuccess = false;
        private String filename;
        private String fileExtension;

        public Downloadpdf_FileFromURL(String filename, String fileExtension) {
            this.filename = filename;
            this.fileExtension = fileExtension;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String rootDirectory = CommonUtils.get3FFileRootPath() + "PalmGrow_DigitalContract/";
                File directory = new File(rootDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                downloadSuccess = true;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (downloadSuccess) {
                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "PalmGrow_DigitalContract/" + filename + fileExtension);
                Log.d("File Path:", fileToDownload.getAbsolutePath());
                if (fileToDownload.exists()) {
                    Log.d("File Path:", fileToDownload.getAbsolutePath());
                } else {
                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
                }
            }
        }
    }


    public class DownloadCMDocsFromURL extends AsyncTask<String, Void, String> {

        private boolean downloadSuccess = false;
        private String filename;
        private String fileExtension;

        public DownloadCMDocsFromURL(String filename, String fileExtension) {
            this.filename = filename;
            this.fileExtension = fileExtension;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String rootDirectory = CommonUtils.get3FFileRootPath() + "PalmGrow_CMDocs/";
                File directory = new File(rootDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                downloadSuccess = true;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (downloadSuccess) {
                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "PalmGrow_CMDocs/" + filename + fileExtension);
                Log.d("File Path:", fileToDownload.getAbsolutePath());
                if (fileToDownload.exists()) {
                    Log.d("File Path:", fileToDownload.getAbsolutePath());
                } else {
                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
                }
            }
        }
    }

    // Async tasks: implement your DownloadpdfFileFromURL and DownloadCMDocsFromURL separately
}


/*public class SplashScreen extends AppCompatActivity {
    public static final String LOG_TAG = SplashScreen.class.getName();

    private Palm3FoilDatabase palm3FoilDatabase;


    private ArrayList<DigitalContract> digitalList = new ArrayList<>();
    private ArrayList<CropMaintenanceDocs> cmdocsList = new ArrayList<>(); ;
    private DataAccessHandler dataAccessHandler;
    private SharedPreferences sharedPreferences;

    private static final int SPLASH_TIME_OUT = 3000;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String[] PERMISSIONS_REQUIRED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE

    };

    private DigitalContract digitalContract;
    private CropMaintenanceDocs cropMaintenanceDocs;
    private ActivityResultLauncher<Intent> mGetPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
        if (!CommonUtils.isNetworkAvailable(this)) {
            UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
        }
        mGetPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(SplashScreen.this, "Android 11 permission ok", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        if (Utils.requestingLocationUpdates(this)) {
//            if (!checkPermissions()) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions();
//                }
//            }
//        }
        android.util.Log.d(LOG_TAG, "onCreate: Checking permissions...");
        takePermission();

        android.util.Log.d(LOG_TAG, "onCreate: Permissions requested");
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            android.util.Log.i(LOG_TAG, "Displaying permission rationale to provide additional context.");
//            Snackbar.make(
//                            findViewById(R.id.activity_main),
//                            R.string.permission_rationale,
//                            Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    })
//                    .show();
        } else {
            android.util.Log.i(LOG_TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void takePermission() {
        android.util.Log.e("  ",   isPermissionGranted()+"");
        if (isPermissionGranted()) {
            try {
                palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                palm3FoilDatabase.createDataBase(); // Assuming this method creates the database
                dbUpgradeCall(); // This seems to upgrade the database if needed
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while initializing the database: " + e.getMessage());
            }

            dataAccessHandler = new DataAccessHandler(SplashScreen.this);
            if (!CommonUtils.isNetworkAvailable(this)) {
                UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
            }
            else{
            startMasterSync();}
//            startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
//            finish();
        } else {
            requestPermission();
        }

    }

    private boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        else {
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return readExternalStoragePermission == PackageManager.PERMISSION_DENIED;
        }

    }


    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                mGetPermission.launch(intent);
                checkAllPermissions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            checkAllPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (areAllPermissionsGranted(grantResults)) {
                android.util.Log.d(LOG_TAG, "onRequestPermissionsResult: All permissions granted. Initializing app...");
                initializeApp();
            } else {
                // Handle denied permissions
            }
        }
    }

    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initializeApp() {

            try {
                palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                palm3FoilDatabase.createDataBase(); // Assuming this method creates the database
                dbUpgradeCall(); // This seems to upgrade the database if needed
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while initializing the database: " + e.getMessage());
            }

            dataAccessHandler = new DataAccessHandler(SplashScreen.this);
            startMasterSync();

    }

    public void startMasterSync() {

        if (CommonUtils.isNetworkAvailable(this) && !sharedPreferences.getBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS,false)) {
            // if (CommonUtils.isNetworkAvailable(this) && !PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS)){
            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    ProgressBar.hideProgressBar();
                    if (success) {

                        // sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();

                        sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
                        // digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContract(stateid), 0);
                        digitalpdfsave();

//                        startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
//                        finish();
                    } else {
                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
                                startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
                                finish();
                            }
                        });
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
            finish();
        }
    }

    private void digitalpdfsave() {
        dataAccessHandler = new DataAccessHandler(SplashScreen.this);
        digitalList = (ArrayList<DigitalContract>) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContract(),1);
        Log.e("========>datasize", digitalList.size() + "");
        for (int i = 0; i < digitalList.size(); i++) {
            int stateid = digitalList.get(i).getStateId();
            Log.e("========>156", stateid + "");
            // fileExtention =digitalContract.getFileExtension();
            // rootDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/");
            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContractbystatecode(stateid), 0);
            String url = Config.image_url + "/" + digitalContract.getFileLocation() + "/" + digitalContract.getFILENAME() + digitalContract.getFileExtension();
            Log.e("========>171url", url + "");

            new DownloadpdfFileFromURL(digitalContract.getFILENAME(), digitalContract.getFileExtension()).execute(url);}
      cmdocssave();
//                                new DownloadpdfFileFromURL().execute(url);

    }

    private void cmdocssave() {
        dataAccessHandler = new DataAccessHandler(SplashScreen.this);
        cmdocsList = (ArrayList<CropMaintenanceDocs>) dataAccessHandler.getCMDocsData(Queries.getInstance().getAllCMDocs(),1);
        Log.e("cmdocsListsize", cmdocsList.size() + "");
        for (int i = 0; i < cmdocsList.size(); i++) {
            int sectionId = cmdocsList.get(i).getCMSectionId();
            Log.e("sectionId", sectionId + "");
            // fileExtention =digitalContract.getFileExtension();
            // rootDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/");
            cropMaintenanceDocs = (CropMaintenanceDocs) dataAccessHandler.getCMDocsData(Queries.getInstance().getCMDocsbysectionId(sectionId), 0);
            String cmdocurl = Config.image_url + "/" + cropMaintenanceDocs.getFileLocation() + "/" + cropMaintenanceDocs.getFileName() + cropMaintenanceDocs.getFileExtension();
            Log.e("cmdocurlurl", cmdocurl + "");

            new DownloadCMDocsFromURL(cropMaintenanceDocs.getFileName(), cropMaintenanceDocs.getFileExtension()).execute(cmdocurl);}
        startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
        finish();
    }


    public void dbUpgradeCall() {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(SplashScreen.this, false);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, true).apply();
        }
    }
    public class DownloadpdfFileFromURL extends AsyncTask<String, Void, String> {

        private boolean downloadSuccess = false;
        private String filename;
        private String fileExtension;

        public DownloadpdfFileFromURL(String filename, String fileExtension) {
            this.filename = filename;
            this.fileExtension = fileExtension;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String rootDirectory = CommonUtils.get3FFileRootPath() + "3F_DigitalContract/";
                File directory = new File(rootDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                downloadSuccess = true;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (downloadSuccess) {
                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/" + filename + fileExtension);
                Log.d("File Path:", fileToDownload.getAbsolutePath());
                if (fileToDownload.exists()) {
                    Log.d("File Path:", fileToDownload.getAbsolutePath());
                } else {
                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
                }
            }
        }
    }


    public class DownloadCMDocsFromURL extends AsyncTask<String, Void, String> {

        private boolean downloadSuccess = false;
        private String filename;
        private String fileExtension;

        public DownloadCMDocsFromURL(String filename, String fileExtension) {
            this.filename = filename;
            this.fileExtension = fileExtension;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String rootDirectory = CommonUtils.get3FFileRootPath() + "3F_CMDocs/";
                File directory = new File(rootDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                downloadSuccess = true;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (downloadSuccess) {
                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "3F_CMDocs/" + filename + fileExtension);
                Log.d("File Path:", fileToDownload.getAbsolutePath());
                if (fileToDownload.exists()) {
                    Log.d("File Path:", fileToDownload.getAbsolutePath());
                } else {
                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
                }
            }
        }
    }





    private boolean checkAllPermissions() {
        try {
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String permission : SplashScreen.PERMISSIONS_REQUIRED) {
                if (ContextCompat.checkSelfPermission(SplashScreen.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    android.util.Log.d(LOG_TAG, "checkAllPermissions:159 " + permission);
                    listPermissionsNeeded.add(permission);
                }
            }
            android.util.Log.d(LOG_TAG, "listPermissionsNeeded " + listPermissionsNeeded.size());

            if (listPermissionsNeeded.isEmpty()) {
                android.util.Log.d(LOG_TAG, "checkAllPermissions: All permissions granted. Initializing app...");
                initializeApp();
                return true;
            } else {
                Log.d(LOG_TAG, "checkAllPermissions: Requesting permissions...");
                ActivityCompat.requestPermissions(SplashScreen.this, listPermissionsNeeded.toArray(new String[0]), SplashScreen.REQUEST_CODE_PERMISSIONS);
                // Call initializeApp even if some permissions are granted
                initializeApp();
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }




}*/
//public class SplashScreen extends AppCompatActivity {
//
//    public static final String LOG_TAG = SplashScreen.class.getName();
//
//    private Palm3FoilDatabase palm3FoilDatabase;
//
//
//    private ArrayList<DigitalContract> digitalList = new ArrayList<>();
//    private ArrayList<CropMaintenanceDocs> cmdocsList = new ArrayList<>(); ;
//    private DataAccessHandler dataAccessHandler;
//    private SharedPreferences sharedPreferences;
//
//    private static final String[] PERMISSIONS_REQUIRED = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.CAMERA,
//            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//            Manifest.permission.FOREGROUND_SERVICE,
//    };
//
//
//
//    private DigitalContract digitalContract;
//    private CropMaintenanceDocs cropMaintenanceDocs;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);
//
//        sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
//
//        // Check network availability
//        if (!CommonUtils.isNetworkAvailable(this)) {
//            UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestNecessaryPermissions(); // <--- Call this
//        } else {
//            initializeDatabaseAndSync(); // For older versions
//        }
//        // Check and request permissions if needed
///*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !CommonUtils.areAllPermissionsAllowedNew(this, PERMISSIONS_REQUIRED)) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, CommonUtils.PERMISSION_CODE);
//        } else {
//            initializeDatabaseAndSync();
//        }*/
//    }
//
//    private void requestNecessaryPermissions() {
//        List<String> permissionsToRequest = new ArrayList<>();
//        boolean shouldShowRationale = false;
//
//        for (String permission : PERMISSIONS_REQUIRED) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionsToRequest.add(permission);
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                    shouldShowRationale = true;
//                }
//            }
//        }
//
//        if (!permissionsToRequest.isEmpty()) {
//            if (shouldShowRationale) {
//                // Show explanation dialog
//                new AlertDialog.Builder(this)
//                        .setTitle("Permissions Required")
//                        .setMessage("This app needs storage, location, and camera permissions to function.")
//                        .setPositiveButton("Grant", (dialog, which) ->
//                                ActivityCompat.requestPermissions(this,
//                                        permissionsToRequest.toArray(new String[0]),
//                                        CommonUtils.PERMISSION_CODE))
//                        .setNegativeButton("Cancel", null)
//                        .show();
//            } else {
//                // Directly request
//                ActivityCompat.requestPermissions(this,
//                        permissionsToRequest.toArray(new String[0]),
//                        CommonUtils.PERMISSION_CODE);
//            }
//        } else {
//            initializeDatabaseAndSync(); // All permissions already granted
//        }
//    }
//
//    private void initializeDatabaseAndSync() {
//        try {
//            palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
//            palm3FoilDatabase.createDataBase(); // Assuming this method creates the database
//            dbUpgradeCall(); // This seems to upgrade the database if needed
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Error while initializing the database: " + e.getMessage());
//        }
//
//        dataAccessHandler = new DataAccessHandler(SplashScreen.this);
//        startMasterSync();
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == CommonUtils.PERMISSION_CODE) {
//            boolean allGranted = true;
//            boolean permanentlyDenied = false;
//
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                        permanentlyDenied = true;
//                    }
//                }
//            }
//
//            if (allGranted) {
//                initializeDatabaseAndSync();
//            } else if (permanentlyDenied) {
//                // User has permanently denied at least one permission
//                new AlertDialog.Builder(this)
//                        .setTitle("Permissions Denied")
//                        .setMessage("You have denied some permissions permanently. Please enable them from App Settings.")
//                        .setPositiveButton("Go to Settings", (dialog, which) -> {
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
//                            finish(); // Optional: close splash screen
//                        })
//                        .setNegativeButton("Cancel", (dialog, which) -> finish())
//                        .show();
//            } else {
//                Toast.makeText(this, "Permissions denied. App cannot proceed.", Toast.LENGTH_LONG).show();
//                finish(); // Optional: close the app or splash screen
//            }
//        }
//    }
//
//  /*  @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == CommonUtils.PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.v(LOG_TAG, "Permission granted");
//                initializeDatabaseAndSync();
//            } else {
//                Log.e(LOG_TAG, "Permissions denied");
//                // Notify the user that permissions are required to proceed
//                UiUtils.showCustomToastMessage("Required permissions are not granted. The app cannot proceed.", SplashScreen.this, 1);
//                // You can also add a dialog or redirect the user to settings to enable permissions
//            }
//        }
//
//    }*/
//
//    public void startMasterSync() {
//
//       if (CommonUtils.isNetworkAvailable(this) && !sharedPreferences.getBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS,false)) {
//     // if (CommonUtils.isNetworkAvailable(this) && !PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS)){
//            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
//                @Override
//                public void execute(boolean success, Object result, String msg) {
//                    ProgressBar.hideProgressBar();
//                    if (success) {
//
//                        // sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
//
//                        sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
//                        // digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContract(stateid), 0);
//                        digitalpdfsave();
//
////                        startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
////                        finish();
//                    } else {
//                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
//                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
//                            @Override
//                            public void run() {
//                                UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
//                                startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
//                                finish();
//                            }
//                        });
//                    }
//                }
//            });
//        } else {
//            startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
//            finish();
//        }
//    }
//
//    private void digitalpdfsave() {
//        dataAccessHandler = new DataAccessHandler(SplashScreen.this);
//        digitalList = (ArrayList<DigitalContract>) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContract(),1);
//        Log.e("========>datasize", digitalList.size() + "");
//        for (int i = 0; i < digitalList.size(); i++) {
//            int stateid = digitalList.get(i).getStateId();
//            Log.e("========>156", stateid + "");
//            // fileExtention =digitalContract.getFileExtension();
//            // rootDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/");
//            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getDigitalContractbystatecode(stateid), 0);
//            String url = Config.image_url + "/" + digitalContract.getFileLocation() + "/" + digitalContract.getFILENAME() + digitalContract.getFileExtension();
//            Log.e("========>171url", url + "");
//
//            new DownloadpdfFileFromURL(digitalContract.getFILENAME(), digitalContract.getFileExtension()).execute(url);}
//        cmdocssave();
////                                new DownloadpdfFileFromURL().execute(url);
//
//    }
//    private void cmdocssave() {
//        dataAccessHandler = new DataAccessHandler(SplashScreen.this);
//        cmdocsList = (ArrayList<CropMaintenanceDocs>) dataAccessHandler.getCMDocsData(Queries.getInstance().getAllCMDocs(),1);
//        Log.e("cmdocsListsize", cmdocsList.size() + "");
//        for (int i = 0; i < cmdocsList.size(); i++) {
//            int sectionId = cmdocsList.get(i).getCMSectionId();
//            Log.e("sectionId", sectionId + "");
//            // fileExtention =digitalContract.getFileExtension();
//            // rootDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/");
//            cropMaintenanceDocs = (CropMaintenanceDocs) dataAccessHandler.getCMDocsData(Queries.getInstance().getCMDocsbysectionId(sectionId), 0);
//            String cmdocurl = Config.image_url + "/" + cropMaintenanceDocs.getFileLocation() + "/" + cropMaintenanceDocs.getFileName() + cropMaintenanceDocs.getFileExtension();
//            Log.e("cmdocurlurl", cmdocurl + "");
//
//            new DownloadCMDocsFromURL(cropMaintenanceDocs.getFileName(), cropMaintenanceDocs.getFileExtension()).execute(cmdocurl);}
//        startActivity(new Intent(SplashScreen.this, MainLoginScreen.class));
//        finish();
//    }
//
//
//    public void dbUpgradeCall() {
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(SplashScreen.this, false);
//        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
//        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
//            SharedPreferences sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
//            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, true).apply();
//        }
//    }
//    public class DownloadpdfFileFromURL extends AsyncTask<String, Void, String> {
//
//        private boolean downloadSuccess = false;
//        private String filename;
//        private String fileExtension;
//
//        public DownloadpdfFileFromURL(String filename, String fileExtension) {
//            this.filename = filename;
//            this.fileExtension = fileExtension;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            try {
//                URL url = new URL(f_url[0]);
//                URLConnection connection = url.openConnection();
//                connection.connect();
//
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                String rootDirectory = CommonUtils.get3FFileRootPath() + "3F_DigitalContract/";
//                File directory = new File(rootDirectory);
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);
//
//                byte data[] = new byte[1024];
//
//                while ((count = input.read(data)) != -1) {
//                    output.write(data, 0, count);
//                }
//
//                output.flush();
//                output.close();
//                input.close();
//                downloadSuccess = true;
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//                downloadSuccess = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String file_url) {
//            if (downloadSuccess) {
//                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/" + filename + fileExtension);
//                Log.d("File Path:", fileToDownload.getAbsolutePath());
//                if (fileToDownload.exists()) {
//                    Log.d("File Path:", fileToDownload.getAbsolutePath());
//                } else {
//                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
//                }
//            }
//        }
//    }
//
//
//    public class DownloadCMDocsFromURL extends AsyncTask<String, Void, String> {
//
//        private boolean downloadSuccess = false;
//        private String filename;
//        private String fileExtension;
//
//        public DownloadCMDocsFromURL(String filename, String fileExtension) {
//            this.filename = filename;
//            this.fileExtension = fileExtension;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            try {
//                URL url = new URL(f_url[0]);
//                URLConnection connection = url.openConnection();
//                connection.connect();
//
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                String rootDirectory = CommonUtils.get3FFileRootPath() + "3F_CMDocs/";
//                File directory = new File(rootDirectory);
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                OutputStream output = new FileOutputStream(rootDirectory + filename + fileExtension);
//
//                byte data[] = new byte[1024];
//
//                while ((count = input.read(data)) != -1) {
//                    output.write(data, 0, count);
//                }
//
//                output.flush();
//                output.close();
//                input.close();
//                downloadSuccess = true;
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//                downloadSuccess = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String file_url) {
//            if (downloadSuccess) {
//                File fileToDownload = new File(CommonUtils.get3FFileRootPath() + "3F_CMDocs/" + filename + fileExtension);
//                Log.d("File Path:", fileToDownload.getAbsolutePath());
//                if (fileToDownload.exists()) {
//                    Log.d("File Path:", fileToDownload.getAbsolutePath());
//                } else {
//                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
//                }
//            }
//        }
//    }

//
//}
//

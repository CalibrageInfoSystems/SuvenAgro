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
import com.cis.palm360.palmgrow.SuvenAgro.database.PalmOilDatabase;
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

    private PalmOilDatabase palmOilDatabase;
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
            palmOilDatabase = PalmOilDatabase.getpalmOilDatabase(this);
            palmOilDatabase.createDataBase();
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
        File dbDir = new File(Environment.getExternalStorageDirectory(), "Suven_Files/PalmGrow_Database");
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

                String rootDirectory = CommonUtils.getFileRootPath() + "PalmGrow_DigitalContract/";
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
                File fileToDownload = new File(CommonUtils.getFileRootPath() + "PalmGrow_DigitalContract/" + filename + fileExtension);
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

                String rootDirectory = CommonUtils.getFileRootPath() + "PalmGrow_CMDocs/";
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
                File fileToDownload = new File(CommonUtils.getFileRootPath() + "PalmGrow_CMDocs/" + filename + fileExtension);
                Log.d("File Path:", fileToDownload.getAbsolutePath());
                if (fileToDownload.exists()) {
                    Log.d("File Path:", fileToDownload.getAbsolutePath());
                } else {
                    UiUtils.showCustomToastMessage("File does not exist", SplashScreen.this, 1);
                }
            }
        }
    }

}
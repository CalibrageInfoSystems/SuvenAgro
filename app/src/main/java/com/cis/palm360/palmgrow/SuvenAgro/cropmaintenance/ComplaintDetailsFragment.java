package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cis.palm360.palmgrow.SuvenAgro.BuildConfig;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataSavingHelper;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintStatusHistory;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.ComplaintTypeXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Complaints;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.DisplayPlotsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;
import com.cis.palm360.palmgrow.SuvenAgro.utils.ImageUtility;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CommonUtilsNavigation.getKey;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//This class used for raising Complaints
public class ComplaintDetailsFragment extends Fragment implements View.OnClickListener, ComplaintsAudioFragment.RecordingFinishedListener {
    public static final int REQUEST_CAM_PERMISSIONS = 2;
    public static final int CAMERA_REQUEST = 1888;
    public static final int CAMERA_REQUEST_2 = 1889;
    public static final int REQUEST_RECORD_AUDIO = 100;
    public static final int RequestPermissionCode = 1;
    private static final String LOG_TAG = ComplaintDetailsFragment.class.getName();
    public RelativeLayout sec_rel, secondImageRel;
    private Spinner complaint_typeSpinner, criticalitySpin;
    private EditText commentsEdt;
    private Button saveBtn;
    private LinkedHashMap<String, String> complaint_typeDataMap, complaintStatusMap, criticalityDataMap;
    private LinkedHashMap<String, String> complaintStatusMapDump;
    private DataAccessHandler dataAccessHandler;
    private View rootView;
    private Context mContext;
    private UpdateUiListener updateUiListener;
    private ImageView profile_pic, farmer_audio1, secondImageRelImage, farmer_icon, secondImageRelIcon;
    private TextView audioFileNameTxt;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private String mCurrentPhotoPath, mSecondPhotoPath;
    private boolean secondImage;
    private Spinner complaintStatusSpin;
    private LinearLayout complaintStatusLL;
    private boolean newComplaint;
    private ArrayList<ComplaintRepository> complaintRepository;
    private Complaints complaintsData;
    private ArrayList<ComplaintTypeXref> complaintsTypeXref;
    private ArrayList<ComplaintStatusHistory> complaintsStatusHistoryList;
    private String audioFilePath;
    private boolean modePlay;
    private ComplaintRepository audioFileRepo = null;
    private ComplaintStatusHistory complaintStatusHistory;
    private Bundle dataBundle;
    private RecyclerView statusCommentsList;
    private GenericTypeAdapter statusCommentsDataAdapter;
    private LinearLayout commentsLL;
    private int dbStatusId;
    private TextView recordTxtMsg;
    private String audioFileName = "";
    private Toolbar toolbar;
    private ActionBar actionBar;

    public ComplaintDetailsFragment() {

    }

    //Initializing the Class
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_complaint_details,container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getString(R.string.complaint_details));

        mContext = getActivity();
        initViews();
        setViews();
        complaintRepository = new ArrayList<>();
        bindData();
        dataAccessHandler = new DataAccessHandler(mContext);
        complaintsTypeXref = new ArrayList<>();
        complaintsStatusHistoryList = new ArrayList<>();
        return rootView;
    }



    //Binding the Data
    private void bindData() {
        dataBundle = getArguments();
        if (!dataBundle.getBoolean(CommonConstants.KEY_NEW_COMPLAINT)) {
            complaintStatusLL.setVisibility(View.VISIBLE);

            newComplaint = false;

            complaintsData = (Complaints) DataManager.getInstance().getDataFromManager(DataManager.COMPLAINT_DETAILS);
            complaintsStatusHistoryList = (ArrayList<ComplaintStatusHistory>) DataManager.getInstance().getDataFromManager(DataManager.COMPLAINT_STATUS_HISTORY);
            complaintsTypeXref = (ArrayList<ComplaintTypeXref>) DataManager.getInstance().getDataFromManager(DataManager.COMPLAINT_TYPE);
            complaintRepository = (ArrayList<ComplaintRepository>) DataManager.getInstance().getDataFromManager(DataManager.COMPLAINT_REPOSITORY);
            complaintStatusHistory = complaintsStatusHistoryList.get(0);
            complaint_typeSpinner.setSelection(CommonUtilsNavigation.getvalueFromHashMap(complaint_typeDataMap, complaintsTypeXref.get(0).getComplaintTypeId()));
            complaint_typeSpinner.setEnabled(false);
            criticalitySpin.setEnabled(false);
            dbStatusId = Integer.parseInt(complaintsStatusHistoryList.get(complaintsStatusHistoryList.size() - 1).getStatusTypeId());
            if (dbStatusId == 201) {
                complaintStatusMap = dataAccessHandler.getGenericData(Queries.getInstance().getCloseDoneStatus());
                complaintStatusSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Status", complaintStatusMap));
                complaintStatusSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(complaintStatusMap, Integer.parseInt(complaintsStatusHistoryList.get(complaintsStatusHistoryList.size() - 1).getStatusTypeId())));
                commentsLL.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                // commentsEdt.setText(complaintsStatusHistoryList.get(complaintsStatusHistoryList.size() - 1).getComments());
            } else {
                complaintStatusSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(complaintStatusMap, Integer.parseInt(complaintsStatusHistoryList.get(complaintsStatusHistoryList.size() - 1).getStatusTypeId())));
                complaintStatusSpin.setEnabled(false);
                commentsLL.setVisibility(View.GONE);
                saveBtn.setVisibility(View.GONE);
            }

            if (dbStatusId == 202 || dbStatusId == 201) {
                recordTxtMsg.setText("Solution");
            }
            bindStatusListData();
//            farmer_audio1.setEnabled(false);
            audioFileRepo = (ComplaintRepository) dataAccessHandler.getComplaintRepository(Queries.getInstance().getComplaintRepositoryByCodeForAudio(complaintsData.getCode()), 0);
            if (audioFileRepo != null) {
                audioFilePath = audioFileRepo.getFileLocation();
                onRecordingFinished(audioFilePath);
            }

            sec_rel.setEnabled(false);
            secondImageRel.setEnabled(false);

            String path = complaintRepository.get(0).getFileLocation();
            Log.d("ImagePath", "Path: " + path);

            loadImageFromStorage(complaintRepository.get(0).getFileLocation(), profile_pic, complaintRepository.get(0));

            ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                @Override
                public void run() {
                    loadImageFromStorage(complaintRepository.get(1).getFileLocation(), secondImageRelImage, complaintRepository.get(1));
                }
            }, 1000);
        } else {
            complaintStatusLL.setVisibility(View.GONE);
            newComplaint = true;
            String ccQuery = Queries.getInstance().getMaxComplaintsHistoryCode(CommonConstants.TAB_ID +"APYG0011000011", CommonConstants.USER_ID);
            Log.e("========", "ccQuery: " + ccQuery);
            String complaintHistoryCode = dataAccessHandler.getOnlyOneValueFromDb(ccQuery);
            Log.e("========", "complaintHistoryCode: " + complaintHistoryCode);
            CommonConstants.COMPLAINT_CODE = DataSavingHelper.generateComplaintsCode(complaintHistoryCode);
            Log.e("========", "CommonConstants.COMPLAINT_CODE: " + complaintHistoryCode);
        }

    }

    //Initializing the UI
    private void initViews() {
        dataAccessHandler = new DataAccessHandler(getActivity());
        complaint_typeSpinner = (Spinner) rootView.findViewById(R.id.complaint_typeSpinner);
        criticalitySpin = (Spinner) rootView.findViewById(R.id.criticalitySpin);
        commentsLL = (LinearLayout) rootView.findViewById(R.id.commentsLL);
        commentsEdt = (EditText) rootView.findViewById(R.id.complaintCommentsEdt);
        sec_rel = (RelativeLayout) rootView.findViewById(R.id.sec_rel);
        secondImageRel = (RelativeLayout) rootView.findViewById(R.id.secondImageRel);
        saveBtn = (Button) rootView.findViewById(R.id.complaintssaveBtn);
        profile_pic = (ImageView) rootView.findViewById(R.id.farmer_image);
        secondImageRelImage = (ImageView) rootView.findViewById(R.id.secondImageRelImage);
        farmer_audio1 = (ImageView) rootView.findViewById(R.id.farmer_audio1);
        farmer_icon = (ImageView) rootView.findViewById(R.id.farmer_icon);
        secondImageRelIcon = (ImageView) rootView.findViewById(R.id.secondImageRelIcon);
        audioFileNameTxt = (TextView) rootView.findViewById(R.id.fileNameTxt);
        statusCommentsList = (RecyclerView) rootView.findViewById(R.id.statuscommentsList);
        farmer_audio1.setVisibility(View.VISIBLE);
        recordTxtMsg = (TextView) rootView.findViewById(R.id.recordTxtMsg);

        complaintStatusLL = (LinearLayout) rootView.findViewById(R.id.complaintStatusLL);
        complaintStatusSpin = (Spinner) rootView.findViewById(R.id.complaintStatusSpin);

    }

    //Setting the Screen with data & on click listeners
    private void setViews() {
        saveBtn.setOnClickListener(this);
        sec_rel.setOnClickListener(this);
        secondImageRel.setOnClickListener(this);
        farmer_audio1.setOnClickListener(this);

        complaint_typeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtComplaintsTypeData("15"));
        criticalityDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtComplaintsTypeData("16"));
        complaintStatusMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtComplaintsTypeData("40"));
        complaintStatusMapDump=complaintStatusMap;
        complaint_typeSpinner.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Complaint Type", complaint_typeDataMap));
        criticalitySpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Criticality level", criticalityDataMap));
        complaintStatusSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Status", complaintStatusMap));

    }

    private void bindStatusListData() {
        ArrayList<ComplaintStatusHistory> complaintsStatusHistory = (ArrayList<ComplaintStatusHistory>) dataAccessHandler.getComplaintStatusHistory
                (Queries.getInstance().getComplaintStatusHistoryAll(CommonConstants.COMPLAINT_CODE), 1);
        statusCommentsDataAdapter = new GenericTypeAdapter(getActivity(), complaintsStatusHistory, complaintStatusMapDump, null, GenericTypeAdapter.TYPE_COMPLAINTSSTATUSCOMMENTS);
        statusCommentsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        statusCommentsList.setAdapter(statusCommentsDataAdapter);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.d("OnClick", "Clicked view ID: " + id);

        if (id == R.id.complaintssaveBtn) {
            Log.d("saveBtn", "Save button clicked");

            CommonUtilsNavigation.hideKeyBoard(getActivity());

            if (validateFields()) {
                Log.d("saveBtn", "Field validation passed");

                if (newComplaint) {
                    Log.d("saveBtn", "New complaint mode");

                    try {
                        ComplaintTypeXref complaintTypeXref = new ComplaintTypeXref();
                        complaintTypeXref.setComplaintCode("");
                        String selectedComplaintType = complaint_typeSpinner.getSelectedItem().toString();
                        String complaintTypeIdStr = CommonUtilsNavigation.getKey(complaint_typeDataMap, selectedComplaintType);

                        Log.d("saveBtn", "Selected complaint type: " + selectedComplaintType + ", ID: " + complaintTypeIdStr);

                        complaintTypeXref.setComplaintTypeId(Integer.parseInt(complaintTypeIdStr));
                        DataManager.getInstance().addData(DataManager.NEW_COMPLAINT_TYPE, complaintTypeXref);

                        saveComplaintStatusHistory(CommonConstants.COMPLAINT_CODE);
                        Log.d("saveBtn", "Selected 299 complaint Code: " + CommonConstants.COMPLAINT_CODE );
                        saveComplaints();
                        saveComplaintRepository();

                    } catch (Exception e) {
                        Log.e("saveBtn", "Error in new complaint block", e);
                    }
                    DataSavingHelper.saveComplaintHistoryData(getActivity(), new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                ProgressBar.hideProgressBar();
                                DisplayPlotsFragment.plotCode = "";
                                UiUtils.showCustomToastMessage("Complaints Details Data Updated Successfully", getActivity(), 0);

                            } else {
                                DisplayPlotsFragment.plotCode = "";
                                ProgressBar.hideProgressBar();
                                // Log.pushExceptionToCrashlytics(new OilPalmException("Data saving failed in conversion "+msg+"-"+result));
                                UiUtils.showCustomToastMessage("Data saving failed " + msg + "-" + result, getActivity(), 1);
                            }
                        }
                    });
                    updateUiListener.updateUserInterface(0);
                    getFragmentManager().popBackStack();

                } else {
                    Log.d("saveBtn", "Edit complaint mode");

                    try {
                        saveComplaintStatusHistoryFromEdit(complaintStatusHistory.getComplaintCode());

                        DataSavingHelper.saveComplaintHistoryDataEditMode(getActivity(), new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                Log.d("saveBtn", "Edit mode save complete: success = " + success + ", msg = " + msg);

                                if (success) {
                                    UiUtils.showCustomToastMessage(CommonUtils.isNewRegistration() ?
                                            "Data inserted successfully" : "Data updated successfully", getActivity(), 0);

                                    updateUiListener.updateUserInterface(0);
                                    getFragmentManager().popBackStack();

                                } else {
                                    UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);
                                }

                                CommonUtilsNavigation.hideKeyBoard(getActivity());
                            }
                        });
                    } catch (Exception e) {
                        Log.e("saveBtn", "Error in edit complaint block", e);
                    }
                }

            } else {
                Log.d("saveBtn", "Field validation failed");
            }
        }

        // Handle section click for image 1
        if (id == R.id.sec_rel) {
            Log.d("ImageSection", "Image 1 clicked");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    !CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA)) {
                Log.v("Permissions", "Camera permission not granted");
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_CAM_PERMISSIONS);
            } else {
                dispatchTakePictureIntent(CAMERA_REQUEST);
            }
        }

        // Handle section click for image 2
        if (id == R.id.secondImageRel) {
            Log.d("ImageSection", "Image 2 clicked");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    !CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA)) {
                Log.v("Permissions", "Camera permission not granted");
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_CAM_PERMISSIONS);
            } else {
                dispatchTakePictureIntent(CAMERA_REQUEST_2);
            }
        }

        // Handle audio recording/playback
        if (id == R.id.farmer_audio1) {
            Log.d("Audio", "Audio section clicked");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    !CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                Log.v("Permissions", "Audio recording permission not granted");
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_EXTERNAL_STORAGE, RequestPermissionCode);
            } else {
                if (newComplaint && !modePlay) {
                    Log.d("Audio", "New complaint - recording mode");
                    try {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString("filePath", createFile(1).getAbsolutePath());

                        ComplaintsAudioFragment complaintsAudioFragment = new ComplaintsAudioFragment();
                        complaintsAudioFragment.setRecordingFinishedListener(this);
                        complaintsAudioFragment.setArguments(bundle);
                        complaintsAudioFragment.show(fm, "recording screen");

                    } catch (IOException e) {
                        Log.e("Audio", "Error while creating file for recording", e);
                    }
                } else {
                    Log.d("Audio", "Playback mode");

                    if (audioFileRepo == null) {
                        audioFileName = CommonConstants.COMPLAINT_CODE;
                    } else {
                        audioFileName = audioFileRepo.getFileName();
                    }

                    if (CommonUtils.isFileExisted(CommonUtils.getAudioFilePath(audioFileName + ".mp3"))) {
                        try {
                            RecordingItem recordingItem = new RecordingItem();
                            recordingItem.setFilePath(audioFilePath);
                            recordingItem.setName(audioFileName);

                            PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(recordingItem);
                            FragmentTransaction transaction = ((FragmentActivity) mContext)
                                    .getSupportFragmentManager()
                                    .beginTransaction();
                            playbackFragment.show(transaction, "dialog_playback");

                        } catch (Exception e) {
                            Log.e("Audio", "Exception during playback", e);
                        }
                    } else {
                        Log.d("Audio", "Audio file not found, downloading...");
                        new DownAudioloadFile().execute();
                    }
                }
            }
        }
    }

    //on click listeners
/*
    @Override
    public void onClick(View v) {

        int id = v.getId();


        if (id == R.id.saveBtn){
Log.e("saveBtn","saveBtn");
        CommonUtilsNavigation.hideKeyBoard(getActivity());
        if (validateFields()) {

            if (newComplaint) {
                ComplaintTypeXref complaintTypeXref = new ComplaintTypeXref();
                complaintTypeXref.setComplaintCode("");
                complaintTypeXref.setComplaintTypeId(Integer.parseInt(CommonUtilsNavigation.getKey(complaint_typeDataMap, complaint_typeSpinner.getSelectedItem().toString())));
                DataManager.getInstance().addData(DataManager.NEW_COMPLAINT_TYPE, complaintTypeXref);
                saveComplaintStatusHistory(CommonConstants.COMPLAINT_CODE);
                saveComplaints();
                saveComplaintRepository();
            }

            if (!newComplaint) {
                saveComplaintStatusHistoryFromEdit(complaintStatusHistory.getComplaintCode());
                DataSavingHelper.saveComplaintHistoryDataEditMode(getActivity(), new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            UiUtils.showCustomToastMessage((CommonUtils.isNewRegistration()) ? "Data inserted successfully" : "Data updated successfully", getActivity(), 0);
                            updateUiListener.updateUserInterface(0);
                            getFragmentManager().popBackStack();
                        } else {
                            UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 0);
                        }
                        CommonUtilsNavigation.hideKeyBoard(getActivity());
                        getFragmentManager().popBackStack();
                    }
                });
            } else {
                updateUiListener.updateUserInterface(0);
                CommonUtilsNavigation.hideKeyBoard(getActivity());
                getFragmentManager().popBackStack();
            }
        }
    }
        if (id == R.id.sec_rel){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                    android.util.Log.v("LOG_TAG", "Camera Permissions Not Granted");
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {
                    dispatchTakePictureIntent(CAMERA_REQUEST);
                }
        }
        if (id == R.id.secondImageRel) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                android.util.Log.v("LOG_TAG", "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                dispatchTakePictureIntent(CAMERA_REQUEST_2);
            }
        }
        if (id == R.id.farmer_audio1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.RECORD_AUDIO))) {
                android.util.Log.v("LOG_TAG", "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_EXTERNAL_STORAGE, RequestPermissionCode
                );
            } else {
                if (newComplaint && !modePlay) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("filePath", createFile(1).getAbsolutePath());
                        ComplaintsAudioFragment complaintsAudioFragment = new ComplaintsAudioFragment();
                        complaintsAudioFragment.setRecordingFinishedListener(this);
                        complaintsAudioFragment.setArguments(bundle);
                        complaintsAudioFragment.show(fm, "recording screen");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    if (audioFileRepo == null) {
                        audioFileName = CommonConstants.COMPLAINT_CODE;
                    } else {
                        audioFileName = audioFileRepo.getFileName();
                    }
                    if (CommonUtils.isFileExisted(CommonUtils.getAudioFilePath(audioFileName + ".mp3"))) {
                        RecordingItem recordingItem = new RecordingItem();
                        recordingItem.setFilePath(audioFilePath);
                        recordingItem.setName(audioFileName);
                        try {
                            PlaybackFragment playbackFragment =
                                    new PlaybackFragment().newInstance(recordingItem);

                            FragmentTransaction transaction = ((FragmentActivity) mContext)
                                    .getSupportFragmentManager()
                                    .beginTransaction();

                            playbackFragment.show(transaction, "dialog_playback");

                        } catch (Exception e) {
                            Log.e(LOG_TAG, "exception", e);
                        }
                    } else {
                        new DownAudioloadFile().execute();
                    }
                }
            }
        }

    }
*/

    //save data into complaint repo
    private void saveComplaintRepository() {
        Log.e("saveBtn", "saveComplaintRepository " + newComplaint);
        if (null == complaintRepository) {
            complaintRepository = new ArrayList<>();
        }
        ComplaintRepository complaintRepositoryImage = new ComplaintRepository();
        complaintRepositoryImage.setFileExtension(CommonConstants.JPEG_FILE_SUFFIX);
        complaintRepositoryImage.setFileName(CommonConstants.COMPLAINT_CODE);
        complaintRepositoryImage.setFileLocation(mCurrentPhotoPath);
        complaintRepositoryImage.setModuleTypeId(205);
        complaintRepositoryImage.setIsVideoRecording(0);

        ComplaintRepository complaintRepositoryImage2 = new ComplaintRepository();
        complaintRepositoryImage2.setComplaintCode("");
        complaintRepositoryImage2.setFileExtension(CommonConstants.JPEG_FILE_SUFFIX);
        complaintRepositoryImage2.setFileName(CommonConstants.COMPLAINT_CODE);
        complaintRepositoryImage2.setFileLocation(mSecondPhotoPath);
        complaintRepositoryImage2.setModuleTypeId(205);
        complaintRepositoryImage.setIsVideoRecording(0);


        ComplaintRepository complaintsAudioFile = new ComplaintRepository();
        complaintsAudioFile.setFileName(CommonConstants.COMPLAINT_CODE);
        complaintsAudioFile.setFileLocation(audioFilePath);
        complaintsAudioFile.setFileExtension(CommonConstants.MP4_FILE_SUFFIX);
        complaintsAudioFile.setIsVideoRecording(1);
        complaintsAudioFile.setModuleTypeId(205);

        complaintRepository.add(complaintRepositoryImage);
        complaintRepository.add(complaintRepositoryImage2);
        complaintRepository.add(complaintsAudioFile);
        DataManager.getInstance().addData(DataManager.NEW_COMPLAINT_REPOSITORY, complaintRepository);
    }

    //save complaints
    private void saveComplaints() {
        Log.e("saveBtn", "saveComplaints " + newComplaint);
        if (complaintsData == null) {
            complaintsData = new Complaints();
        }
//        complaintsData.setCriticalityByTypeId(criticalitySpin.getSelectedItemPosition() == 0 ? null : Integer.parseInt(getKey(criticalityDataMap, criticalitySpin.getSelectedItem().toString())));
        complaint_typeSpinner.setSelection(0);
        complaintsData.setCriticalityByTypeId(null);
        complaintsData.setCode(CommonConstants.COMPLAINT_CODE);
        criticalitySpin.setSelection(0);
        Log.d("saveBtn", "Selected 299 complaint Code: " +complaintsData );
        DataManager.getInstance().addData(DataManager.NEW_COMPLAINT_DETAILS, complaintsData);
    }

    //save complaint status history
    private void saveComplaintStatusHistory(String ComplaintCode) {
        Log.d("saveBtn", "Selected complaint Code: " + ComplaintCode );
        if (complaintStatusHistory == null) {
            complaintStatusHistory = new ComplaintStatusHistory();
        }
        complaintStatusHistory.setComplaintCode(ComplaintCode);
        complaintStatusHistory.setStatusTypeId(newComplaint ? "254" : (complaintStatusSpin.getSelectedItemPosition() == 0 ? null : getKey(complaintStatusMap, complaintStatusSpin.getSelectedItem().toString())));
        complaintStatusHistory.setComments(commentsEdt.getText().toString());
        Log.d("saveBtn", "Selected 299 complaintStatusHistory Code: " +complaintStatusHistory );
        DataManager.getInstance().addData(DataManager.NEW_COMPLAINT_STATUS_HISTORY, complaintStatusHistory);

    }

    //save complaint status history after edit
    private void saveComplaintStatusHistoryFromEdit(String ComplaintCode) {
        if (complaintStatusHistory == null) {
            complaintStatusHistory = new ComplaintStatusHistory();
        }
        complaintStatusHistory.setComplaintCode(ComplaintCode);
        complaintStatusHistory.setStatusTypeId(newComplaint ? "254" : (complaintStatusSpin.getSelectedItemPosition() == 0 ? null : getKey(complaintStatusMap, complaintStatusSpin.getSelectedItem().toString())));
        complaintStatusHistory.setComments(commentsEdt.getText().toString());
        DataManager.getInstance().addData(DataManager.COMPLAINT_STATUS_HISTORY, complaintStatusHistory);
    }

    //on permissions granted result
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (StoragePermission && RecordPermission) {
//                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_LONG).show();
                        UiUtils.showCustomToastMessage("Permission Granted", getActivity(), 0);
                    } else {
//                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                        UiUtils.showCustomToastMessage("Permission Denied", getActivity(), 1);
                    }
                }
                break;
            case CommonUtils.PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 1);
                } else {

                }
                return;
            }
        }
    }

    //Handling Image from Camera methods
    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                secondImage = false;
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpFile(0);
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;
            case CAMERA_REQUEST_2:
                File f2 = null;
                mSecondPhotoPath = null;
                secondImage = true;
                try {
                    f2 = setUpFile(0);
                    mSecondPhotoPath = f2.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f2);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f2 = null;
                    mSecondPhotoPath = null;
                }
                break;
            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpFile(int fileType) throws IOException {
        File f = createFile(fileType);
        if (fileType == 0)
            if (secondImage) {
                mSecondPhotoPath = f.getAbsolutePath();
            } else {
                mCurrentPhotoPath = f.getAbsolutePath();
            }
        return f;
    }

    private File createFile(int fileType) throws IOException {
        File pictureDirectory;
        if (fileType == 0) {
            pictureDirectory = new File(CommonUtils.getFileRootPath() + "/Palm_Pictures/" + "ComplaintsPhotos");
            if (!pictureDirectory.exists()) {
                pictureDirectory.mkdirs();
            }
            File finalFile = new File(pictureDirectory, (secondImage) ? CommonConstants.COMPLAINT_CODE + "_1" + CommonConstants.JPEG_FILE_SUFFIX : CommonConstants.COMPLAINT_CODE + CommonConstants.JPEG_FILE_SUFFIX);
            return finalFile;
        } else {
            pictureDirectory = new File(CommonUtils.getFileRootPath() + "/Palm_Audio/" + "ComplaintsAudios");
            if (!pictureDirectory.exists()) {
                pictureDirectory.mkdirs();

            }
            File finalFile = new File(pictureDirectory, CommonConstants.COMPLAINT_CODE + ".mp3");
            return finalFile;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto(profile_pic);
                    farmer_icon.setVisibility(View.GONE);
                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case CAMERA_REQUEST_2: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto(secondImageRelImage);
                    secondImageRelIcon.setVisibility(View.GONE);
                }
                break;
            }
            case REQUEST_RECORD_AUDIO: {
                if (resultCode == RESULT_OK) {
//                    Toast.makeText(getActivity(), "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Audio recorded successfully!", getActivity(), 0);
                    farmer_audio1.setImageResource(R.mipmap.play_button_2);
                    audioFileNameTxt.setVisibility(View.VISIBLE);
                    audioFileNameTxt.setText(CommonConstants.COMPLAINT_CODE + ".wav");
                } else if (resultCode == RESULT_CANCELED) {
//                    Toast.makeText(getActivity(), "Audio was not recorded", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Audio was not Recorded", getActivity(), 1);
                }
                break;
            }

        } // switch
    }

    private void handleBigCameraPhoto(ImageView type) {
//        if (mCurrentPhotoPath != null) {
//
////            mCurrentPhotoPath = null;
//        }
        setPic(type);
        galleryAddPic();
    }

    private void setPic(ImageView imageView) {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */
        /* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (secondImage) {
            BitmapFactory.decodeFile(mSecondPhotoPath, bmOptions);
        } else {
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap;
        if (secondImage) {
            bitmap = BitmapFactory.decodeFile(mSecondPhotoPath, bmOptions);
        } else {
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        }
        bitmap = ImageUtility.rotatePicture(90, bitmap);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
        imageView.invalidate();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f;
        if (secondImage) {
            f = new File(mSecondPhotoPath);
        } else {
            f = new File(mCurrentPhotoPath);
        }
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    //Validations
    public boolean validateFields() {

        if (dataBundle.getBoolean(CommonConstants.KEY_NEW_COMPLAINT)) {
            if (!CommonUtilsNavigation.selectAnySpinner(complaint_typeSpinner)) {
                UiUtils.showCustomToastMessage("Please select complaint type", getActivity(), 1);
                return false;
            }

            if (TextUtils.isEmpty(commentsEdt.getText().toString())) {
                UiUtils.showCustomToastMessage("Please enter comments", getActivity(), 1);
                return false;
            }

            if (TextUtils.isEmpty(mCurrentPhotoPath) || TextUtils.isEmpty(mSecondPhotoPath)) {
                UiUtils.showCustomToastMessage("Please take pictures", getActivity(), 1);
                return false;
            }
        } else {
            if (!CommonUtilsNavigation.selectAnySpinner(complaintStatusSpin)) {
                UiUtils.showCustomToastMessage("Please select Status", getActivity(), 1);
                return false;
            }
            if (TextUtils.isEmpty(commentsEdt.getText().toString()) && dbStatusId == 201) {
                UiUtils.showCustomToastMessage("Please enter comments", getActivity(), 1);
                return false;
            }
        }

        return true;
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    //on Audio recording finished
    @Override
    public void onRecordingFinished(String filePath) {
        audioFilePath = filePath;
        audioFileNameTxt.setText("" + CommonUtils.getFilename(filePath));
        audioFileNameTxt.setVisibility(View.VISIBLE);
        farmer_audio1.setImageResource(R.mipmap.play_button);
        modePlay = true;
        //new DownAudioloadFile().execute();
    }

    private void loadImageFromStorage(final String path, final ImageView imageViewToUpdate, final ComplaintRepository complaintRepository) {
        if (complaintRepository != null) {
            final String imagePath = CommonUtils.getImageUrl(complaintRepository);
            Log.d("ImagePath", "Loading image from: " + imagePath);

            Picasso.get()
                    .load(new File(imagePath)) // 👈 Important: Use File
                    .into(imageViewToUpdate, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ImageLoadError", "Picasso failed", e);
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                Glide.with(getActivity())
                                        .load(new File(path)) // 👈 Use File
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(imageViewToUpdate);
                            }
                        }
                    });
        } else {
            if (getActivity() != null && !getActivity().isFinishing()) {
                Glide.with(getActivity())
                        .load(new File(path)) // 👈 Use File
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageViewToUpdate);
            }
        }
    }


    //load image from storage onResume
//    private void loadImageFromStorage(final String path, final ImageView imageViewToUpdate, final ComplaintRepository complaintRepository) {
//        if (null != complaintRepository) {
//            final String imageUrl = CommonUtils.getImageUrl(complaintRepository);
//
//            Picasso.get()
//                    .load(imageUrl)
//                    .into(imageViewToUpdate, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//
//                        }
//
//
//                        public void onError() {
//                            if (getActivity() != null && !getActivity().isFinishing()) {
//                                Glide.with(getActivity())
//                                        .load(path)
//                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                        .skipMemoryCache(true)
//                                        .into(imageViewToUpdate);
//
//                            }
//                        }
//                    });
//        } else {
//            if (getActivity() != null && !getActivity().isFinishing()) {
//                Glide.with(getActivity())
//                        .load(path)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .into(imageViewToUpdate);
//            }
//        }
//    }

    //Downloading Audio file
    private class DownAudioloadFile extends AsyncTask<String, Integer, String> {
        public boolean isDownLoadSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar.showProgressBar(getActivity(), "Downloading audio file...");
        }

        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                final String audioURL = CommonUtils.getImageUrl(audioFileRepo);
                URL url = new URL(audioURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Check for a successful response
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Download", "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return null;
                }

                int lengthOfFile = connection.getContentLength();

                File audioDirectory = new File(CommonUtils.getFileRootPath() + "/Palm_Audio/ComplaintsAudios");
                if (!audioDirectory.exists()) {
                    audioDirectory.mkdirs();
                }

                audioFilePath = audioDirectory + "/" + audioFileRepo.getFileName() + ".mp3";

                input = new BufferedInputStream(connection.getInputStream());
                output = new FileOutputStream(audioFilePath);

                byte data[] = new byte[4096];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (lengthOfFile > 0) {
                        publishProgress((int) (total * 100 / lengthOfFile));
                    }
                    output.write(data, 0, count);
                }

                isDownLoadSuccess = true;
            } catch (Exception e) {
                isDownLoadSuccess = false;
                Log.e("DownloadError", "Exception while downloading audio: " + e.getMessage(), e);
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException ignored) {
                }
            }

            return null;
        }

     /*   @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar.hideProgressBar();

            if (isDownLoadSuccess) {
                DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
                dataAccessHandler.updateComplaintFilePath(CommonConstants.COMPLAINT_CODE, audioFilePath);

                RecordingItem recordingItem = new RecordingItem();
                recordingItem.setFilePath(audioFilePath);
                recordingItem.setName(audioFileName);
                audioFileNameTxt.setText(audioFileName);

                try {
                    PlaybackFragment playbackFragment = PlaybackFragment.newInstance(recordingItem);
                    FragmentTransaction transaction = ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction();
                    playbackFragment.show(transaction, "dialog_playback");
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Playback fragment error", e);
                }
            } else {
//                Toast.makeText(mContext, "Error while downloading file", Toast.LENGTH_SHORT).show();
                UiUtils.showCustomToastMessage("Error while downloading file", mContext, 1);
            }
        }*/
     @Override
     protected void onPostExecute(String s) {
         super.onPostExecute(s);

         ProgressBar.hideProgressBar();

         if (!TextUtils.isEmpty(audioFilePath)) {  // audioUrl is your remote link
             try {
                 MediaPlayer mediaPlayer = new MediaPlayer();
                 mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                 mediaPlayer.setDataSource(audioFilePath); // play directly from URL
                 mediaPlayer.prepareAsync();          // async prepare
                 mediaPlayer.setOnPreparedListener(mp -> {
                     mp.start(); // start playback when ready
                 });

                 // Optional: show info on UI
                 audioFileNameTxt.setText(audioFileName);

             } catch (IOException e) {
                 Log.e(LOG_TAG, "Error playing audio", e);
                 UiUtils.showCustomToastMessage("Error playing audio", mContext, 1);
             }
         } else {
             UiUtils.showCustomToastMessage("Audio URL is empty", mContext, 1);
         }
     }


    }

}
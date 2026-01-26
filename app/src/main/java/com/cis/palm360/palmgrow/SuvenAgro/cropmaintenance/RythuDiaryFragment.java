package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.PalmDetailsEditListener;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RythuDiaryFragment extends Fragment implements View.OnClickListener, PalmDetailsEditListener, UpdateUiListener {

    private static final int REQUEST_RYTHU_DIARY = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 102;
    private FragmentActivity myContext;
    private UpdateUiListener updateUiListener;
    private ImageView rythuDiaryBtn;
    private View rootView;
    private Button saveBtn;
    Toolbar toolbar;
    ActionBar actionBar;
    private String savedRythuDiaryImage;
    private FileRepository savedPictureData = null;
    String selectedfarmercode;
    private DataAccessHandler dataAccessHandler;
    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rythu_diary, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Rythu Diary");
        initView();
        setView();
        return rootView;
    }

    private void initView() {
        dataAccessHandler = new DataAccessHandler(getContext());
        rythuDiaryBtn = (ImageView) rootView.findViewById(R.id.rythuDiaryBtn);
        saveBtn = rootView.findViewById(R.id.saveBtn);
    }
    private void setImageFromFilePath(String filePath, ImageView imageView) {
        if (filePath != null && imageView != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                android.util.Log.e("ImageLoadError", "Failed to decode image from path: " + filePath);
            }
        } else {
            android.util.Log.e("ImageLoadError", "filePath or imageView is null");
        }
    }
    private void setView() {
//        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.RYTHU_DIARY);
//        if (fileRepo != null) {
//            savedRythuDiaryImage = fileRepo.getPicturelocation(); // or getFileName() depending on your model
//            setImageFromFilePath(savedRythuDiaryImage, rythuDiaryBtn);
//        }
//        rythuDiaryBtn.setOnClickListener(v -> {
//            checkCameraPermissionAndOpenCamera(REQUEST_RYTHU_DIARY);
//        });
        selectedfarmercode = CommonConstants.FARMER_CODE;
        FileRepository fileRepo = (FileRepository) DataManager.getInstance().getDataFromManager(DataManager.RYTHU_DIARY);
        if (fileRepo == null) {
            fileRepo = dataAccessHandler.getSelectedFileRepository(Queries.getInstance().getSelectedFarmerImageQuery(selectedfarmercode, 845));
        }
        if (fileRepo != null) {
            String localPath = fileRepo.getPicturelocation(); // local file path
            String imageUrl = CommonUtils.getImageUrl(fileRepo);  // server URL

            if (localPath != null && new File(localPath).exists()) {
                // ✅ Local image exists
                setImageFromFilePath(localPath, rythuDiaryBtn);
                Log.d("RythuDiary", "Loaded image from local storage: " + localPath);
            } else if (imageUrl != null && !imageUrl.isEmpty() && CommonUtils.isNetworkAvailable(getContext())) {
                // ✅ Load from server
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.mipmap.image)
                        .into(rythuDiaryBtn);
                Log.d("RythuDiary", "Loaded image from server URL: " + imageUrl);
            } else {
                // ❌ No image available
                rythuDiaryBtn.setImageResource(R.mipmap.image);
                Log.d("RythuDiary", "No image available, showing placeholder");
            }
        } else {
            // ❌ No FileRepository exists yet
            rythuDiaryBtn.setImageResource(R.mipmap.image);
            Log.d("RythuDiary", "No FileRepository found, showing placeholder");
        }

        rythuDiaryBtn.setOnClickListener(v -> checkCameraPermissionAndOpenCamera(REQUEST_RYTHU_DIARY));

        saveBtn.setOnClickListener(v -> {
            try {
                Drawable drawable = rythuDiaryBtn.getDrawable();

                if (drawable != null && drawable instanceof BitmapDrawable) {
                    // ✅ Image uploaded
                    Bitmap uploadedImage = ((BitmapDrawable) drawable).getBitmap();
                    Log.d("SaveBtn", "Image is uploaded");

                    // Save bitmap to file and get path
                    String currentImgPath = saveBitmapAndReturnPath(uploadedImage);
                    Log.d("SaveBtn", "Image saved at path: " + currentImgPath);

                    // Create FileRepository object
                    FileRepository repo = new FileRepository();
                    repo.setFarmercode(CommonConstants.FARMER_CODE);
                    repo.setPlotcode(CommonConstants.PLOT_CODE);
                    repo.setModuletypeid(CommonConstants.rythuDiaryModuleTypeId);
                    repo.setFilename(CommonConstants.PLOT_CODE);
                    repo.setPicturelocation(currentImgPath);
                    repo.setFileextension(CommonConstants.JPEG_FILE_SUFFIX);
                    repo.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                    repo.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    repo.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
                    repo.setServerUpdatedStatus(0);
                    repo.setIsActive(1);
                    repo.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

                    // Save repo in DataManager
                    DataManager.getInstance().addData(DataManager.RYTHU_DIARY, repo);
                    Log.d("SaveBtn", "FileRepository object saved successfully");

                    // Hide keyboard & navigate back
                    CommonUtilsNavigation.hideKeyBoard(getActivity());
                    getFragmentManager().popBackStack();
                    updateUiListener.updateUserInterface(0);

                } else {
                    // ❌ No image uploaded
                    Log.d("SaveBtn", "No image uploaded");
                    UiUtils.showCustomToastMessage("Please upload an image before saving.", getContext(), 1);
                }

            } catch (Exception e) {
                Log.e("SaveBtn", "Error while saving: " + e.getMessage(), e);
                UiUtils.showCustomToastMessage("Something went wrong while saving.", getContext(), 1);
            }

        });


    }

    private String saveBitmapAndReturnPath(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {


            File pictureDirectory = new File(CommonUtils.get3FFileRootPath() + "/Palm_Pictures/" + "RythuDiaryImages");
            if (!pictureDirectory.exists()) {
                pictureDirectory.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            // Create the file with the timestamp
            File file = new File(pictureDirectory, timeStamp + "_" + CommonConstants.PLOT_CODE  + CommonConstants.JPEG_FILE_SUFFIX);
//        File finalFile = new File(pictureDirectory, CommonConstants.PLOT_CODE + CommonConstants.JPEG_FILE_SUFFIX);
//            return finalFile;
//            // Create a unique filename
//            String fileName = "rythu_diary" + System.currentTimeMillis() + ".jpg";
//
//            // Save to app's cache directory
//            File file = new File(requireContext().getCacheDir(), fileName);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RYTHU_DIARY && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                rythuDiaryBtn.setImageBitmap(imageBitmap);
                String currentImgPath = saveBitmapAndReturnPath(imageBitmap);
                setImageFromFilePath(currentImgPath, rythuDiaryBtn);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void updateUserInterface(int refreshPosition) {

    }

    @Override
    public void onEditClicked(int position) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    private void savePictureData(String imageLocation) {
        savedPictureData=new FileRepository();
        savedPictureData.setFarmercode(CommonConstants.FARMER_CODE);
        savedPictureData.setPlotcode(CommonConstants.PLOT_CODE);
        savedPictureData.setModuletypeid(CommonConstants.rythuDiaryModuleTypeId); // 845
        savedPictureData.setFilename(CommonConstants.PLOT_CODE);
        savedPictureData.setPicturelocation(imageLocation);
        savedPictureData.setFileextension(CommonConstants.JPEG_FILE_SUFFIX);
        savedPictureData.setCreatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        savedPictureData.setUpdatedbyuserid(Integer.parseInt(CommonConstants.USER_ID));
        savedPictureData.setServerUpdatedStatus(0);
        savedPictureData.setIsActive(1);
        savedPictureData.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//        DataManager.getInstance().addData(DataManager.FILE_REPOSITORY, savedPictureData);

        List<FileRepository> imageList = (List<FileRepository>) DataManager.getInstance().getDataFromManager(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        imageList.add(savedPictureData);
        DataManager.getInstance().addData(DataManager.MISSING_PLANTATION_IMAGES_BY_FINISH, imageList);
    }

}
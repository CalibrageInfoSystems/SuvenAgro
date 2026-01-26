package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.cis.palm360.palmgrow.SuvenAgro.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.DripIrrigationModel;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.HorticultureConfig;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of getActivity() fragment.
 */


public class DripIrrigationFragment extends Fragment {
    private static final int PICK_PDF_REQUEST = 3000;
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESULT_LOAD_IMAGE = 2000;

    private Button currentPdfButton = null;
    private ImageButton currentDeletePdf = null;
    private ImageView currentImageView = null;
    private ImageView currentImageView2 = null;
    private DataAccessHandler dataAccessHandler;
    private LinearLayout formContainer;
    private final List<FormItem> formItems = new ArrayList<>();
    private final List<View> allCards = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private ActionBar actionBar;
    private DripIrrigationModel dripModel = new DripIrrigationModel();
    private List<DripIrrigationModel> dripList = new ArrayList<>();
    private UpdateUiListener updateUiListener;
    Button submit;
    LinearLayout layoutPdfPreview;
    TextView tvPdfFileName;
    ImageButton btnCancelPdf;
    private LinkedHashMap trenchMap, paymentmodeMap, CompneyMap;
    Bitmap currentPhotoBitmap = null;
    Bitmap currentPhotoBitmap2 = null;
    String currentPdfPath = null;
    String currentPdffilename = null;
    List<DripIrrigationModel> dripsavedList;
    ImageView btnUploadDdImage, btnUploadAckImage;
    private int selectedImageType = 0;
    FrameLayout layoutDdImage, layoutAckImage;
    ImageView previewImageDd, previewImageAck;
    //    private LinkedHashMap<String, String> trenchMap = new LinkedHashMap<>();
//    private LinkedHashMap<String, String> paymentmodeMap = new LinkedHashMap<>();
    private static final int IMAGE_TYPE_DD = 1;
    private static final int IMAGE_TYPE_ACK = 2;
    private static final int IMAGE_TYPE_851 = 3;
    private static final int IMAGE_TYPE_832 = 4;

    private ImageView btn_delete_image_ack;
    private ImageView btn_delete_image_dd;
    Bitmap currentPhotoBitmap851 = null;
    Bitmap currentPhotoBitmap832 = null;


    ImageView btnAddImage851, btnAddImage832;
    TextView tvImage851, tvImage832;
    TextView tv_ack_image, tv_dd_image;
    FrameLayout layoutImage851, layoutImage832;
    ImageView previewImage851, deleteImage851, previewImage832, deleteImage832;
    private ImageView currentImageView851 = null;
    private ImageView currentImageView832 = null;
    private static final int IMAGE_REQUEST_MODE_OF_PAYMENT = 101;
    private static final int IMAGE_REQUEST_ACKNOWLEDGEMENT = 102;
    private int currentStepId = -1; // default value
    Integer DDcompanyId = 0;
    private String MandalId;
    LinearLayout horticulture_honamell;
    TextView horticulture_honame;
    Integer horticulture_honameid;
    String horticulturehoname;
   // TextView tv_selectdate, tv_selectcompany, tv_selectpaymentmode, tv_amount, tv_bank_name, tv_account_number, tv_checknumber, tv_trenching, tv_plantcount, tv_comments;
    private boolean isPaymentModeInitialized = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drip_irrigation, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Drip irrigation");


        dataAccessHandler = new DataAccessHandler(getContext());

// Load saved data from DataManager
// Step 1: Get both lists
        List<DripIrrigationModel> listFromManager = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        List<DripIrrigationModel> listFromDB = (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE), 1);

// Step 2: Merge them into dripsavedList
        dripsavedList = new ArrayList<>();

        if (listFromManager != null && !listFromManager.isEmpty()) {
            dripsavedList.addAll(listFromManager);
        }

        if (listFromDB != null && !listFromDB.isEmpty()) {
            dripsavedList.addAll(listFromDB);
        }

// Step 3: Check if we have any data
        if (!dripsavedList.isEmpty()) {
            Log.d("savedDripList", "Merged Drip List Size: " + dripsavedList.size());
            Log.d("savedDripList", "Drip List: " + dripsavedList.toString());

            // Continue with next steps
            // prepopulateAnsweredDripQuestions(dripsavedList);
        } else {
            Log.d("savedDripList", "No Drip Data Found in Either Source");
        }


        submit = view.findViewById(R.id.submit_btn);
        submit.setOnClickListener(v -> {
            // Fetch existing saved data (from DataManager in-memory cache)
            dripsavedList = (List<DripIrrigationModel>) DataManager.getInstance()
                    .getDataFromManager(DataManager.DripIrrigation);
            if (dripsavedList == null) {
                dripsavedList = new ArrayList<>();
            }

            // Fetch from DB also
            List<DripIrrigationModel> datainDbDB =
                    (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(
                            Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE), 1);

            Log.d("savedDripList", "Saved Drip List: " + dripsavedList.size());
            Log.d("dbDripList", "DB Drip List: " + (datainDbDB != null ? datainDbDB.size() : 0));

            boolean alreadySaved = false;
            String plotCode = CommonConstants.PLOT_CODE;

            // ✅ Check in-memory list
            for (DripIrrigationModel model : dripsavedList) {
                if (model.getStatusTypeId() == 826 && model.getDripStatusDone() == 1) {
                    alreadySaved = true;
                    break;
                }
            }

            // ✅ Also check in DB list
            if (!alreadySaved && datainDbDB != null) {
                for (DripIrrigationModel model : datainDbDB) {
                    if (model.getStatusTypeId() == 826 && model.getDripStatusDone() == 1) {
                        alreadySaved = true;
                        break;
                    }
                }
            }

            // ✅ If not saved anywhere, insert default NO (848, dripStatusDone = 0)
            if (!alreadySaved) {
                DripIrrigationModel model = new DripIrrigationModel();
                model.setPlotCode(plotCode);
                model.setDate(null);
                model.setStatusTypeId(848);
                model.setDripStatusDone(0); // NO
                model.setCreatedDate(dateFormat.format(new Date()));
                model.setUpdatedDate(dateFormat.format(new Date()));
                model.setServerUpdatedStatus(0);
                model.setIsActive(1);

                dripsavedList.add(model);
                DataManager.getInstance().addData(DataManager.DripIrrigation, dripsavedList);

                Log.d("Submit", "✅ Default NO saved for statusTypeId 826");
            }

            getFragmentManager().popBackStack();
            updateUiListener.updateUserInterface(0);
            UiUtils.showCustomToastMessage("Drip Irrigation data added successfully", getActivity(), 0);
        });

     /*   submit.setOnClickListener(v -> {
            // Fetch existing saved data
            dripsavedList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
            if (dripsavedList == null) {
                dripsavedList = new ArrayList<>();
            }
            List<DripIrrigationModel> datainDbDB = (List<DripIrrigationModel>) dataAccessHandler.getDripIrrigationDetails(Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE), 1);
            Log.d("savedDripList", "Saved Drip List: " + dripsavedList.size());

            boolean alreadySaved = false;
            String plotCode = CommonConstants.PLOT_CODE;

            for (DripIrrigationModel model : dripsavedList) {
                if (model.getStatusTypeId() == 826 && model.getDripStatusDone() == 1) {
                    alreadySaved = true;
                    break;
                }
            }

            // ✅ If not saved, save NO response for statusTypeId 826
            if (!alreadySaved) {
                DripIrrigationModel model = new DripIrrigationModel();
                model.setPlotCode(plotCode);
                model.setDate(null);
                model.setStatusTypeId(848);
                model.setDripStatusDone(0); // NO
                model.setCreatedDate(dateFormat.format(new Date()));
                model.setUpdatedDate(dateFormat.format(new Date()));
                model.setServerUpdatedStatus(0);
                model.setIsActive(1); // ✅ Important

                dripsavedList.add(model); // ✅ Using the correct list
                DataManager.getInstance().addData(DataManager.DripIrrigation, dripsavedList);
                Log.d("Submit", "✅ Default NO saved for statusTypeId 826");
            }

            getFragmentManager().popBackStack();
            updateUiListener.updateUserInterface(0);
            UiUtils.showCustomToastMessage("Drip Irrigation data added successfully", getActivity(), 0);
//            DataSavingHelper.savedripirrigation(getActivity(), new ApplicationThread.OnComplete<String>() {
//                @Override
//                public void execute(boolean success, String result, String msg) {
//                    //   Log.e("Drip i rrigation", (result != null ? result : "null") + " " + (msg != null ? msg : "null"));
//                    Toast.makeText(getActivity(), "onComplete called: " + success, Toast.LENGTH_SHORT).show();
//
//                    ProgressBar.hideProgressBar();
//                    if (success) {
//                        Log.e("Drip irrigation", "@@@  saved successfully");
//
//                        String toastMessage = CommonUtils.isNewRegistration()
//                                ? "Data saved successfully"
//                                : "Data updated successfully";
//
//                        UiUtils.showCustomToastMessage("Drip Irrigation data added successfully", getActivity(), 0);
//                        CommonUtilsNavigation.hideKeyBoard(getActivity());
//                        getActivity().finish(); // ✅ Finish only after success
//                    } else {
//                        getActivity().finish();
//                        UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);
//                    }
//                }
//            });
//            UiUtils.showCustomToastMessage("Drip Irrigation data added successfully", getActivity(), 0);
//            getActivity().finish();
        });
*/

        formContainer = view.findViewById(R.id.form_container);


//Sample questions
        // Prepare form questions
        // Prepare form questions
        formItems.add(new FormItem("Survey Done?", "Survey Done", true, true, false, false, false, true, 826, true, false));
        formItems.add(new FormItem("BOQ Done?", "BOQ Done", true, true, true, false, false, true, 827, true, false));
        formItems.add(new FormItem("DD Paid?", "DD Paid", true, true, false, true, true, true, 828, true, true)); // ✅ 2 images
        formItems.add(new FormItem("Trench Marking Done?", "Trench Marking Done", true, true, false, false, true, true, 829, true, false));
        formItems.add(new FormItem("Horticulture Team Verified?", "Horticulture Team Verified", true, true, false, false, true, true, 830, true, false));
        formItems.add(new FormItem("Administration Sanctioned?", "Administration Sanctioned", true, true, false, false, false, true, 831, false, false));
        formItems.add(new FormItem("Drip Material Received ?", "Drip Material Received", true, true, false, true, false, true, 851, false, false)); // ✅ 1 image
        formItems.add(new FormItem("Drip Installation Done?", "Drip Installation Done", true, true, false, true, false, true, 832, true, false));    // ✅ 1 image

        for (int i = 0; i < formItems.size(); i++) {
            FormItem item = formItems.get(i);
            DripIrrigationModel matchedModel = null;

            if (dripsavedList != null && !dripsavedList.isEmpty()) {
                for (DripIrrigationModel model : dripsavedList) {
                    if (model.getStatusTypeId() == item.statusTypeId) {
                        matchedModel = model;
                        break;
                    }
                }
            }

            // Step 6 (831) - Only enable if already saved
            if (item.statusTypeId == 831) {  // Todo ROJA
                item.isEnabled = false;
                if (matchedModel != null && matchedModel.getDripStatusDone() == 1) {
                    item.isEnabled = true;
                }
            }

            // Step 7 (851) - Only enable if Step 6 is saved
            if (item.statusTypeId == 851) {
                item.isEnabled = false;

                boolean sixthStepDone = false;
                if (dripsavedList != null) {
                    for (DripIrrigationModel model : dripsavedList) {
                        if (model.getStatusTypeId() == 831 && model.getDripStatusDone() == 1) {
                            sixthStepDone = true;
                            break;
                        }
                    }
                }

                if (sixthStepDone) {
                    item.isEnabled = true;
                }
            }

            // Step 8 (832) - Only enable if Step 7 is saved
            if (item.statusTypeId == 832) {
                item.isEnabled = false;

                boolean seventhStepDone = false;
                if (dripsavedList != null) {
                    for (DripIrrigationModel model : dripsavedList) {
                        if (model.getStatusTypeId() == 851 && model.getDripStatusDone() == 1) {
                            seventhStepDone = true;
                            break;
                        }
                    }
                }

                if (seventhStepDone) {
                    item.isEnabled = true;
                }
            }

            // Add form card to UI
            View card = addFormCard(item, i, matchedModel);
            allCards.add(card);
        }


        return view;
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private View addFormCard(FormItem item, int index, DripIrrigationModel savedModel) {

        View card = LayoutInflater.from(getActivity()).inflate(R.layout.question_item, formContainer, false);
        LinearLayout layoutPdfPreview;
        TextView tvPdfFileName;


        TextView tvQuestion = card.findViewById(R.id.question_text);
        RadioGroup rgAnswer = card.findViewById(R.id.answer_group);
        RadioButton btnYes = card.findViewById(R.id.yes_button);
        RadioButton btnNo = card.findViewById(R.id.no_button);
        TextView tvDate = card.findViewById(R.id.date_text);
        EditText etComments = card.findViewById(R.id.editTextComments);
        Button btnUploadPdf = card.findViewById(R.id.btn_upload_pdf);
        layoutPdfPreview = card.findViewById(R.id.layout_pdf_preview);
        tvPdfFileName = card.findViewById(R.id.tv_pdf_file_name);
        btnCancelPdf = card.findViewById(R.id.btn_cancel_pdf);

        EditText PlantCount = card.findViewById(R.id.PlantCount);
        //tv_selectdate,tv_selectcompany,tv_selectpaymentmode,tv_amount,tv_bank_name,tv_checknumber,tv_trenching,tv_plantcount
//        tv_comments = card.findViewById(R.id.tv_comments);
//        tv_selectdate = card.findViewById(R.id.tv_selectdate);
//        tv_selectcompany = card.findViewById(R.id.tv_selectcompany);
//        tv_selectpaymentmode = card.findViewById(R.id.tv_selectpaymentmode);
//        tv_amount = card.findViewById(R.id.tv_amount);
//        tv_bank_name = card.findViewById(R.id.tv_bank_name);
//        tv_account_number = card.findViewById(R.id.tv_account_number);
//        tv_checknumber = card.findViewById(R.id.tv_checknumber);
//        tv_trenching = card.findViewById(R.id.tv_trenching);
//        tv_plantcount = card.findViewById(R.id.tv_plantcount);
        tv_ack_image = card.findViewById(R.id.tv_ack_image);
        tv_dd_image = card.findViewById(R.id.tv_dd_image);
        btnUploadDdImage = card.findViewById(R.id.btn_upload_dd_image);
        btnUploadAckImage = card.findViewById(R.id.btn_upload_ack_image);
        layoutDdImage = card.findViewById(R.id.layout_dd_image);
        layoutAckImage = card.findViewById(R.id.layout_ack_image);
        previewImageDd = card.findViewById(R.id.preview_image_dd);
        previewImageAck = card.findViewById(R.id.preview_image_ack);
        btn_delete_image_dd = card.findViewById(R.id.btn_delete_image_dd);
        btn_delete_image_ack = card.findViewById(R.id.btn_delete_image_ack);
        TextView  tv_comments = card.findViewById(R.id.tv_comments);
        TextView  tv_selectdate = card.findViewById(R.id.tv_selectdate);
        TextView tv_selectcompany = card.findViewById(R.id.tv_selectcompany);
        TextView tv_selectpaymentmode = card.findViewById(R.id.tv_selectpaymentmode);
        TextView  tv_amount = card.findViewById(R.id.tv_amount);
        TextView tv_bank_name = card.findViewById(R.id.tv_bank_name);
        TextView tv_account_number = card.findViewById(R.id.tv_account_number);
        TextView tv_checknumber = card.findViewById(R.id.tv_checknumber);
        TextView  tv_trenching = card.findViewById(R.id.tv_trenching);
        TextView  tv_plantcount = card.findViewById(R.id.tv_plantcount);
        Button btnOk = card.findViewById(R.id.btn_ok);
        Spinner spinnerPaymentMode = card.findViewById(R.id.spinner_payment_mode);
        Spinner spinner_Compney = card.findViewById(R.id.spinner_Compney);
        Spinner spinnerTrenching = card.findViewById(R.id.spinner_trenching);
        EditText etAmount = card.findViewById(R.id.et_amount);
        EditText etBankName = card.findViewById(R.id.et_bank_name);
        EditText etchecknumber = card.findViewById(R.id.etchecknumber);
        EditText Accountnum = card.findViewById(R.id.Accountnum);
        TextView tvApiBoundText = card.findViewById(R.id.tv_api_bound_text);
        EditText editDateselection = card.findViewById(R.id.editDateselection);
        btnAddImage851 = card.findViewById(R.id.btn_add_image_851);
        btnAddImage832 = card.findViewById(R.id.btn_add_image_832);
        tvImage851 = card.findViewById(R.id.tvImage851);
        tvImage832 = card.findViewById(R.id.tvImage832);
        layoutImage851 = card.findViewById(R.id.layout_image_851);
        previewImage851 = card.findViewById(R.id.preview_image_851);
        deleteImage851 = card.findViewById(R.id.delete_image_851);

        layoutImage832 = card.findViewById(R.id.layout_image_832);
        previewImage832 = card.findViewById(R.id.preview_image_832);
        deleteImage832 = card.findViewById(R.id.delete_image_832);
        horticulture_honamell = card.findViewById(R.id.horticulture_honamell);
        horticulture_honame = card.findViewById(R.id.horticulture_honame);


        tvQuestion.setText(item.label);


        if (index != 0) {
            tvQuestion.setAlpha(0.5f);
            btnYes.setEnabled(false);
            btnYes.setAlpha(0.5f);
            btnNo.setEnabled(false); // disabled by default
            btnNo.setAlpha(0.5f);
            rgAnswer.setEnabled(false);
        }


        // Hide fields initially
        tvDate.setVisibility(View.GONE);
        editDateselection.setVisibility(View.GONE);

        etComments.setVisibility(View.GONE);
        btnUploadPdf.setVisibility(View.GONE);

        PlantCount.setVisibility(View.GONE);
        layoutDdImage.setVisibility(View.GONE);
        layoutAckImage.setVisibility(View.GONE);
        previewImageDd.setVisibility(View.GONE);
        previewImageAck.setVisibility(View.GONE);
        btnUploadDdImage.setVisibility(View.GONE);
        btnUploadAckImage.setVisibility(View.GONE);
        tv_ack_image.setVisibility(View.GONE);
        tv_dd_image.setVisibility(View.GONE);
//        btn_delete_image_dd.setVisibility(View.GONE);
//        btn_delete_image_ack.setVisibility(View.GONE);
        btnAddImage851.setVisibility(View.GONE);
        btnAddImage832.setVisibility(View.GONE);
        tvImage851.setVisibility(View.GONE);
        tvImage832.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        etAmount.setVisibility(View.GONE);
        etBankName.setVisibility(View.GONE);
        etchecknumber.setVisibility(View.GONE);
        Accountnum.setVisibility(View.GONE);
        tvApiBoundText.setVisibility(View.GONE);
        spinnerPaymentMode.setVisibility(View.GONE);
        spinner_Compney.setVisibility(View.GONE);
        spinnerTrenching.setVisibility(View.GONE);

        editDateselection.setOnClickListener(v -> openDatePicker(editDateselection));

        btnUploadDdImage.setOnClickListener(v -> {
            selectedImageType = IMAGE_TYPE_DD;
            showImagePickDialog();
        });

        btnUploadAckImage.setOnClickListener(v -> {
            selectedImageType = IMAGE_TYPE_ACK;
            showImagePickDialog();
        });

        btnAddImage851.setOnClickListener(v -> {
            selectedImageType = IMAGE_TYPE_851;
            showImagePickDialog();
        });

        btnAddImage832.setOnClickListener(v -> {
            selectedImageType = IMAGE_TYPE_832;
            showImagePickDialog();
        });


        DripIrrigationModel finalSavedModel = savedModel;
        btn_delete_image_dd.setOnClickListener(v -> {


            DripIrrigationModel modelToDelete = finalSavedModel;

            if (modelToDelete == null) {
                Log.d("DripDelete", "finalSavedModel is null, trying to fetch from DB...");
                modelToDelete = (DripIrrigationModel) dataAccessHandler.getDripIrrigationDetails(
                        Queries.getInstance().getDripIrrigationstatuswise(CommonConstants.PLOT_CODE, 828), 0); // Use actual statusTypeId
            }

            if (modelToDelete != null && modelToDelete.getFileLocation() != null) {
                File file = new File(modelToDelete.getFileLocation());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    Log.d("DripDelete", "File deletion status: " + deleted);
                } else {
                    Log.d("DripDelete", "File not found: " + modelToDelete.getFileLocation());
                }

                modelToDelete.setFileLocation(null);
                modelToDelete.setFileName(null);
                modelToDelete.setFileExtension(null);
                previewImageDd.setImageDrawable(null);
                previewImageDd.setVisibility(View.GONE);
                currentPhotoBitmap = null;
                layoutDdImage.setVisibility(View.GONE);
                btnUploadDdImage.setVisibility(View.VISIBLE);
                Log.d("DripDelete", "Image metadata cleared in model.");
            } else {
                Log.d("DripDelete", "No model found to delete image.");
            }
        });


        btn_delete_image_ack.setOnClickListener(v -> {
            previewImageAck.setImageDrawable(null);
            previewImageAck.setVisibility(View.GONE);
            currentPhotoBitmap2 = null;
            layoutAckImage.setVisibility(View.GONE);
            btnUploadAckImage.setVisibility(View.VISIBLE);
            if (finalSavedModel != null) {

                File file = new File(finalSavedModel.getAckFileLocation());
                if (file.exists()) {
                    file.delete();
                }
                finalSavedModel.setAckFileExtension(null);
                finalSavedModel.setAckFileLocation(null);
                finalSavedModel.setAckFileExtension(null);
            }

        });
        deleteImage851.setOnClickListener(v -> {
            previewImage851.setImageDrawable(null);
            previewImage851.setVisibility(View.GONE);
            currentPhotoBitmap851 = null;
            layoutImage851.setVisibility(View.GONE);
            btnAddImage851.setVisibility(View.VISIBLE);

            DripIrrigationModel modelToDelete = finalSavedModel;
            if (modelToDelete == null) {
                Log.d("DripDelete", "finalSavedModel is null for 851, fetching from DB...");
                modelToDelete = (DripIrrigationModel) dataAccessHandler.getDripIrrigationDetails(
                        Queries.getInstance().getDripIrrigationstatuswise(CommonConstants.PLOT_CODE, 851), 0);
            }

            if (modelToDelete != null && modelToDelete.getFileLocation() != null) {
                File file = new File(modelToDelete.getFileLocation());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    Log.d("DripDelete", "851 File deleted: " + deleted);
                } else {
                    Log.d("DripDelete", "851 File not found: " + modelToDelete.getFileLocation());
                }

                // ✅ Clear metadata so image doesn't reappear later
                modelToDelete.setFileLocation(null);
                modelToDelete.setFileName(null);
                modelToDelete.setFileExtension(null);

                // ✅ Optionally update DataManager model too if using in-memory list
                List<DripIrrigationModel> dripList =
                        (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
                if (dripList != null) {
                    for (DripIrrigationModel model : dripList) {
                        if (model.getPlotCode().equals(CommonConstants.PLOT_CODE)
                                && model.getStatusTypeId() == 851) {
                            model.setFileLocation(null);
                            model.setFileName(null);
                            model.setFileExtension(null);
                            break;
                        }
                    }
                    DataManager.getInstance().addData(DataManager.DripIrrigation, dripList);
                    Log.d("DripDelete", "Drip list updated in memory.");
                }
            } else {
                Log.d("DripDelete", "No model found for 851 to delete image.");
            }
        });

        deleteImage832.setOnClickListener(v -> {
            previewImage832.setImageDrawable(null);
            previewImage832.setVisibility(View.GONE);
            currentPhotoBitmap832 = null;
            layoutImage832.setVisibility(View.GONE);
            btnAddImage832.setVisibility(View.VISIBLE);

            DripIrrigationModel modelToDelete = finalSavedModel;
            if (modelToDelete == null) {
                Log.d("DripDelete", "finalSavedModel is null for 832, fetching from DB...");
                modelToDelete = (DripIrrigationModel) dataAccessHandler.getDripIrrigationDetails(
                        Queries.getInstance().getDripIrrigationstatuswise(CommonConstants.PLOT_CODE, 832), 0);
            }

            if (modelToDelete != null && modelToDelete.getFileLocation() != null) {
                File file = new File(modelToDelete.getFileLocation());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    Log.d("DripDelete", "832 File deleted: " + deleted);
                } else {
                    Log.d("DripDelete", "832 File not found: " + modelToDelete.getFileLocation());
                }

                modelToDelete.setFileLocation(null);
                modelToDelete.setFileName(null);
                modelToDelete.setFileExtension(null);
                Log.d("DripDelete", "832 image metadata cleared.");
            } else {
                Log.d("DripDelete", "No model found for 832 to delete image.");
            }
        });


        btnUploadPdf.setOnClickListener(v -> {
            currentPdfButton = btnUploadPdf;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
        });


        btnCancelPdf.setOnClickListener(v -> {
            // Optionally delete file from storage
            if (currentPdfPath != null) {
                File file = new File(currentPdfPath);
                if (file.exists()) file.delete();
                currentPdfPath = null;
            }

            layoutPdfPreview.setVisibility(View.GONE);
            btnUploadPdf.setVisibility(View.VISIBLE);
        });


        String plotCode = CommonConstants.PLOT_CODE;

        MandalId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getmandalId(plotCode));


        ArrayList<HorticultureConfig> configList =
                dataAccessHandler.getHorticultureConfigdata(Queries.getInstance().gethonameid(MandalId));

        if (configList != null && !configList.isEmpty()) {
            HorticultureConfig config = configList.get(0); // First (and only) record

            horticulture_honameid = config.getId();
            horticulturehoname = config.getHOName();

            Log.d("Horticulture", "Id: " + horticulture_honameid + ", HOName: " + horticulturehoname);
            horticulture_honame.setText(" : " + horticulturehoname + "");
            // Use as needed
        } else {
            Log.d("Horticulture", "No data found for MandalId: " + MandalId);
        }
        //  DripIrrigationModel savedModel = null;
        if (dripsavedList != null) {
            for (DripIrrigationModel m : dripsavedList) {
                if (m.getStatusTypeId() == item.statusTypeId && m.getIsActive() == 1) {
                    savedModel = m;
                    break;
                }
            }
        }

        if (index == 0 && savedModel == null) {
            btnNo.setChecked(true); // Default selection is No
            rgAnswer.check(R.id.no_button);
            btnOk.setVisibility(View.GONE);
        }

        if (savedModel != null) {
            Log.e("===========>", savedModel.getDripStatusDone() + "");


            // Restore YES/NO selection
            int checkedId = (savedModel.getDripStatusDone() == 1) ? R.id.yes_button : R.id.no_button;
            rgAnswer.check(checkedId);
            btnOk.setVisibility(View.VISIBLE);
            btn_delete_image_dd.setVisibility(View.GONE);
            btn_delete_image_ack.setVisibility(View.GONE);
            deleteImage851.setVisibility(View.GONE);
            deleteImage832.setVisibility(View.GONE);
            btnCancelPdf.setVisibility(View.GONE);
            if (savedModel.getDripStatusDone() == 1) {
                btnNo.setEnabled(false);
                btnNo.setAlpha(0.5f);
                btnYes.setEnabled(false);
                btnYes.setAlpha(0.5f);
                rgAnswer.setEnabled(false);
            }

            if (item.statusTypeId == 826 && savedModel.getDripStatusDone() == 0) {
                rgAnswer.check(R.id.no_button);
                btnOk.setVisibility(View.VISIBLE);
            }


            Log.d("DripIrrigation", "getDate: " + savedModel.getDate());

            // Restore date & comment
            // editDateselection.setText(savedModel.getDate() != null ? savedModel.getDate() : "");
            editDateselection.setText(savedModel.getDate() != null ? convertToDisplayDate(savedModel.getDate()) : "");

            editDateselection.setVisibility(View.VISIBLE);
            tv_selectdate.setVisibility(View.VISIBLE);
            etComments.setVisibility(View.VISIBLE);
            tv_comments.setVisibility(View.VISIBLE);
            Log.d("DripIrrigation", "Comments: " + savedModel.getComments());
            String comment = savedModel.getComments();
            Log.d("DripDebug", "Raw comment: " + comment);

            if (comment == null || comment.equalsIgnoreCase("null")) {
                comment = "";
            }
            etComments.setText(comment);

// If statusTypeId is 831, disable the card
            if (item.statusTypeId == 831) {
                rgAnswer.setEnabled(false);
                btnYes.setEnabled(false);
                btnYes.setAlpha(0.5f);
                btnNo.setEnabled(false);
                btnNo.setAlpha(0.5f);

                tvQuestion.setAlpha(0.5f);
                editDateselection.setEnabled(false);
                etComments.setEnabled(false);
                card.setAlpha(0.5f); // Optional: visually dim the card
            }


            // Handle statusType-specific fields
            switch (item.statusTypeId) {
                case 826:
                    // Load Company Map
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(CompneyMap, "Company Name")
                    );
                    companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(companyAdapter);

                    // Set selected company based on saved model
                    String selectedCompany = CommonUtils.getKeyFromValue2(CompneyMap, savedModel.getCompanyId());
                    int selectedPosition = companyAdapter.getPosition(selectedCompany);
                    if (selectedPosition >= 0) {
                        spinner_Compney.setSelection(selectedPosition);
                    }

                    spinner_Compney.setVisibility(View.VISIBLE);
                    tv_selectcompany.setVisibility(View.VISIBLE);

                    etAmount.setVisibility(View.GONE);


                    break;
                case 827:
                    // Load Company Map
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> companyAdapter1 = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(CompneyMap, "Company Name")
                    );
                    companyAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(companyAdapter1);

                    // Set selected company based on saved model
                    String selectedCompany1 = CommonUtils.getKeyFromValue2(CompneyMap, savedModel.getCompanyId());
                    int selectedPosition1 = companyAdapter1.getPosition(selectedCompany1);
                    if (selectedPosition1 >= 0) {
                        spinner_Compney.setSelection(selectedPosition1);
                    }
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    tv_amount.setVisibility(View.VISIBLE);
                    spinner_Compney.setVisibility(View.VISIBLE);
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText(String.valueOf(savedModel.getAmount()));
                    // Only for 827, handle PDF
                    if (item.statusTypeId == 827 && savedModel.getFileName() != null) {
                        if (savedModel.getFileLocation() != null) {
                            File file = new File(savedModel.getFileLocation());
                            if (file.exists()) {
                                layoutPdfPreview.setVisibility(View.VISIBLE);
                                btnUploadPdf.setVisibility(View.GONE);
                                tvPdfFileName.setText(file.getName());
                                currentPdfPath = file.getAbsolutePath();
                            }
                        }
                    }
                    break;



                case 828:
                    Log.d("PAYMENT_MODE", "Case 828 triggered");

                    // Load DD Image
                    if (savedModel.getFileLocation() != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(savedModel.getFileLocation());
                        if (bitmap != null) {
                            layoutDdImage.setVisibility(View.VISIBLE);
                            previewImageDd.setVisibility(View.VISIBLE);
                            previewImageDd.setImageBitmap(bitmap);
                            previewImageDd.setOnClickListener(v -> showZoomedImage(bitmap));
                            Log.d("PAYMENT_MODE", "DD Image loaded and click listener set");
                        } else {
                            Log.d("PAYMENT_MODE", "DD image bitmap is null");
                        }
                    }

                    // Load Ack Image
                    if (savedModel.getAckFileLocation() != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(savedModel.getAckFileLocation());
                        if (bitmap != null) {
                            layoutAckImage.setVisibility(View.VISIBLE);
                            previewImageAck.setVisibility(View.VISIBLE);
                            previewImageAck.setImageBitmap(bitmap);
                            previewImageAck.setOnClickListener(v -> showZoomedImage(bitmap));
                            Log.d("PAYMENT_MODE", "Ack Image loaded and click listener set");
                        } else {
                            Log.d("PAYMENT_MODE", "Ack image bitmap is null");
                        }
                    }

                    // Load Company Spinner
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> adapterCompany = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(CompneyMap, "Company Name"));
                    adapterCompany.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(adapterCompany);

                    String selectedCompany2 = CommonUtils.getKeyFromValue2(CompneyMap, savedModel.getCompanyId());
                    int compIndex = adapterCompany.getPosition(selectedCompany2);
                    if (compIndex >= 0) spinner_Compney.setSelection(compIndex);
                    spinner_Compney.setVisibility(View.VISIBLE);

                    tv_selectcompany.setVisibility(View.VISIBLE);
                    tv_selectpaymentmode.setVisibility(View.VISIBLE);

                    // Load Payment Mode Spinner
                    paymentmodeMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("139"));
                    if (paymentmodeMap != null) {
                        ArrayAdapter<String> adapterPayment = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_item,
                                CommonUtils.fromMap(paymentmodeMap, "Mode Of Payments"));
                        adapterPayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPaymentMode.setAdapter(adapterPayment);

                        String selectedMode = CommonUtils.getKeyFromValue2(paymentmodeMap, savedModel.getModeOfPayment());
                        int modeIndex = adapterPayment.getPosition(selectedMode);
                        if (modeIndex >= 0) spinnerPaymentMode.setSelection(modeIndex);

                        Log.d("PAYMENT_MODE", "Payment Mode spinner set with selection index: " + modeIndex);
                        spinnerPaymentMode.setVisibility(View.VISIBLE);
                    }

                    // Set saved data into fields
                    etAmount.setText(String.valueOf(savedModel.getAmount()));
                    etchecknumber.setText(savedModel.getDdChequeNumber());
                    etBankName.setText(savedModel.getDdBank());
                    Accountnum.setText(savedModel.getDdBankAccountNumber());

                    // Hide all fields initially
                    etAmount.setVisibility(View.GONE);
                    etchecknumber.setVisibility(View.GONE);
                    etBankName.setVisibility(View.GONE);
                    Accountnum.setVisibility(View.GONE);
                    tv_checknumber.setVisibility(View.GONE);
                    tv_bank_name.setVisibility(View.GONE);
                    tv_account_number.setVisibility(View.GONE);
                    tv_amount.setVisibility(View.GONE);

                    // 🔄 Update UI for saved mode only initially
                    int savedModeId = savedModel.getModeOfPayment();
                    Log.d("PAYMENT_MODE", "Saved Mode ID: " + savedModeId);
                    switch (savedModeId) {
                        case 834: // Cheque
                        case 847: // UPI
                            tv_amount.setVisibility(View.VISIBLE);
                            tv_checknumber.setVisibility(View.VISIBLE);
                            etAmount.setVisibility(View.VISIBLE);
                            etchecknumber.setHint(savedModeId == 847 ? "Enter UPI Number" : "Enter Cheque Number");
                            etchecknumber.setVisibility(View.VISIBLE);
                            break;

                        case 835: // Cash
                            tv_amount.setVisibility(View.VISIBLE);
                            etAmount.setVisibility(View.VISIBLE);
                            break;

                        case 836: // Bank Transfer
                            tv_amount.setVisibility(View.VISIBLE);
                            tv_bank_name.setVisibility(View.VISIBLE);
                            tv_account_number.setVisibility(View.VISIBLE);
                            etAmount.setVisibility(View.VISIBLE);
                            etBankName.setVisibility(View.VISIBLE);
                            Accountnum.setVisibility(View.VISIBLE);
                            break;
                    }

                    // 🧠 Add listener AFTER UI is initialized
                    spinnerPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        boolean isFirstSelection = true;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (isFirstSelection) {
                                Log.d("PAYMENT_MODE", "Skipping first spinner trigger");
                                isFirstSelection = false;
                                return;
                            }
                            tv_checknumber.setVisibility(View.GONE);
                            etchecknumber.setVisibility(View.GONE);
                            tv_bank_name.setVisibility(View.GONE);
                            etBankName.setVisibility(View.GONE);
                            tv_account_number.setVisibility(View.GONE);
                            Accountnum.setVisibility(View.GONE);
                            tv_amount.setVisibility(View.GONE);
                            etAmount.setVisibility(View.GONE);

                            // Clear data
                            tv_checknumber.setText("");
                            tv_bank_name.setText("");
                            tv_account_number.setText("");
                            tv_amount.setText("");

                            etchecknumber.setText("");
                            etBankName.setText("");
                            Accountnum.setText("");
                            etAmount.setText("");
                            String selectedMode = spinnerPaymentMode.getSelectedItem().toString();
                            String selectedPaymentModeId = CommonUtils.getKeyFromValue(paymentmodeMap, selectedMode);
                            Log.d("PAYMENT_MODE", "User changed selection to: " + selectedMode + " (ID: " + selectedPaymentModeId + ")");

                            // Clear old fields

                            // Show fields based on new selection
                            switch (selectedPaymentModeId) {
                                case "834": // Cheque
                                    Log.d("PAYMENT_MODE", "Selected: Cheque");

                                    tv_checknumber.setVisibility(View.VISIBLE);
                                    tv_checknumber.setText("Cheque Number *");
                                    etchecknumber.setHint("Enter Cheque Number");
                                    etchecknumber.setVisibility(View.VISIBLE);
                                    tv_amount.setVisibility(View.VISIBLE);
                                    tv_amount.setText("Amount *");
                                    etAmount.setVisibility(View.VISIBLE);

                                    break;

                                case "835": // Cash
                                    Log.d("PAYMENT_MODE", "Selected: Cash");
                                    tv_amount.setText("Amount *");
                                    tv_amount.setVisibility(View.VISIBLE);
                                    etAmount.setVisibility(View.VISIBLE);
                                    break;

                                case "836": // Bank Transfer
                                    Log.d("PAYMENT_MODE", "Selected: Bank Transfer");
                                    tv_bank_name.setVisibility(View.VISIBLE);
                                    etBankName.setVisibility(View.VISIBLE);
                                    tv_bank_name.setText("Bank Name *");
                                    tv_account_number.setVisibility(View.VISIBLE);
                                    tv_account_number.setText("Bank Account Number *");
                                    Accountnum.setVisibility(View.VISIBLE);
                                    tv_amount.setText("Amount *");
                                    tv_amount.setVisibility(View.VISIBLE);
                                    etAmount.setVisibility(View.VISIBLE);
                                    break;

                                case "847": // UPI
                                    Log.d("PAYMENT_MODE", "Selected: UPI");
                                    tv_checknumber.setVisibility(View.VISIBLE);
                                    tv_checknumber.setText("UPI Number *");
                                    etchecknumber.setHint("Enter UPI Number");
                                    etchecknumber.setVisibility(View.VISIBLE);
                                    tv_amount.setVisibility(View.VISIBLE);
                                    tv_amount.setText("Amount *");
                                    etAmount.setVisibility(View.VISIBLE);
                                    break;

                                default:
                                    Log.d("PAYMENT_MODE", "Unknown payment mode selected: " + selectedPaymentModeId);
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Log.d("PAYMENT_MODE", "Nothing selected in payment mode spinner.");
                        }
                    });

                    break;


                case 829: // Trench Marking
                    trenchMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("140"));
                    ArrayAdapter<String> adapterTrench = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(trenchMap, "Trench Marking"));
                    adapterTrench.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTrenching.setAdapter(adapterTrench);

                    String selectedTrench = CommonUtils.getKeyFromValue2(trenchMap, savedModel.getTrenchMarkingTypeId());
                    int trenchIndex = adapterTrench.getPosition(selectedTrench);
                    if (trenchIndex >= 0) spinnerTrenching.setSelection(trenchIndex);
                    spinnerTrenching.setVisibility(View.VISIBLE);
                    tv_trenching.setVisibility(View.VISIBLE);
                    break;

                case 830: // Horticulture
                    PlantCount.setText(String.valueOf(savedModel.getHorticultureRecommendedPlants()));
                    PlantCount.setVisibility(View.VISIBLE);
                    horticulture_honamell.setVisibility(View.VISIBLE);
                    tv_plantcount.setVisibility(View.VISIBLE);
                    // PlantCount.setEnabled(false);
                    break;
                case 851:
                    if (savedModel.getFileLocation() != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(savedModel.getFileLocation());
                        if (bitmap != null) {
                            layoutImage851.setVisibility(View.VISIBLE);
                            previewImage851.setVisibility(View.VISIBLE);
                            previewImage851.setImageBitmap(bitmap);
                            previewImage851.setOnClickListener(v -> showZoomedImage(bitmap)); // 👈 Zoom on click
                        }
                    }
                    etComments.setVisibility(View.VISIBLE);
                    etComments.setText(savedModel.getComments() != null ? savedModel.getComments() : "");
                    break;

                case 832:
                    if (savedModel.getFileLocation() != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(savedModel.getFileLocation());
                        if (bitmap != null) {
                            layoutImage832.setVisibility(View.VISIBLE);
                            previewImage832.setVisibility(View.VISIBLE);
                            previewImage832.setImageBitmap(bitmap);
                            previewImage832.setOnClickListener(v -> showZoomedImage(bitmap)); // 👈 Zoom on click
                        }
                    }
                    etComments.setVisibility(View.VISIBLE);
                    etComments.setText(savedModel.getComments() != null ? savedModel.getComments() : "");
                    break;

            }

            // Unlock next card if available
            card.post(() -> {
                if (index + 1 < allCards.size()) {
                    View nextCard = allCards.get(index + 1);
                    RadioButton nextYes = nextCard.findViewById(R.id.yes_button);
                    RadioButton nextNo = nextCard.findViewById(R.id.no_button);
                    RadioGroup nextRg = nextCard.findViewById(R.id.answer_group);

                    FormItem nextItem = formItems.get(index + 1);

                    if (nextItem.statusTypeId != 831) {
                        nextYes.setEnabled(true);
                        nextYes.setAlpha(1f);
                        nextNo.setEnabled(true);
                        nextNo.setAlpha(1f);

                        DripIrrigationModel nextModel = null;
                        for (DripIrrigationModel m : dripsavedList) {
                            if (m.getStatusTypeId() == nextItem.statusTypeId) {
                                nextModel = m;
                                break;
                            }
                        }

                        if (nextModel == null) {
                            nextRg.check(R.id.no_button);
                        } else if (nextModel.getDripStatusDone() == 1) {
                            nextNo.setEnabled(false);
                            nextNo.setAlpha(0.5f);
                        }
                    }
                }
            });

            tvQuestion.setAlpha(1f);
        }


        int currentIndex = index;
        rgAnswer.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isYes = checkedId == R.id.yes_button;
//            if (isYes && item.statusTypeId == 826) {
//                btnNo.setEnabled(false);
//                btnNo.setAlpha(0.5f);
//            }
            handleAnswerSelection(isYes, item, card, currentIndex);
        });

        btnOk.setOnClickListener(v -> {
            boolean isYes = btnYes.isChecked();
            boolean isNo = btnNo.isChecked();

            if (!isYes && !isNo) {
//                Toast.makeText(getActivity(), "Please select Yes or No", Toast.LENGTH_SHORT).show();
                UiUtils.showCustomToastMessage("Please Select Yes or No", getContext(), 1);
                return;
            }

            // ❌ "No" is allowed only for the first question (statusTypeId == 826)
            if (isNo && item.statusTypeId != 826) {
                UiUtils.showCustomToastMessage("Data Won't be Saved with No As An Answer", getActivity(), 1);

                // Toast.makeText(this, "Data Won't be Saved with No As An Answer.", Toast.LENGTH_SHORT).show();
                return;
            }
//Data Won't be Saved with No As An Answer
            // ✅ Save or update data
            // ✅ Save or update data
            if (isYes) {
                Log.e("DripFlow", "isAlreadySaved = " + isAlreadySaved(item.statusTypeId));


                if (isAlreadySaved(item.statusTypeId)) {
                    Log.e("DripFlow", "Calling updateYesModel for " + item.statusTypeId + " at index " + DDcompanyId);
                    updateYesModel(
                            item,
                            currentIndex,
                            editDateselection,
                            etComments,
                            spinnerPaymentMode,
                            spinner_Compney,
                            spinnerTrenching,
                            etBankName,
                            etAmount,
                            PlantCount,
                            etchecknumber,
                            Accountnum,
                            previewImageDd, previewImageAck
                    );
                } else {
                    Log.e("DripFlow", "Calling saveYesModel for " + item.statusTypeId + " at index " + DDcompanyId);
                    btn_delete_image_dd.setVisibility(View.GONE);
                    btn_delete_image_ack.setVisibility(View.GONE);
                    deleteImage851.setVisibility(View.GONE);
                    deleteImage832.setVisibility(View.GONE);
                    btnCancelPdf.setVisibility(View.GONE);
                    saveYesModel(
                            item,
                            currentIndex,
                            editDateselection,
                            etComments,
                            spinnerPaymentMode,
                            spinner_Compney,
                            spinnerTrenching,
                            etBankName,
                            etAmount,
                            PlantCount,
                            etchecknumber,
                            Accountnum,
                            previewImageDd,
                            previewImageAck, previewImage851, previewImage832
                    );
                }
            }


            // ✅ Disable "No" RadioButton in CURRENT card (answered question)
            btnNo.setEnabled(false);
            btnNo.setAlpha(0.5f);


        });


        formContainer.addView(card);
        // allCards.add(card); // <--- this line is needed
        return card;
    }

    public static String convertToDisplayDate(String originalDate) {
        if (originalDate == null || originalDate.trim().isEmpty()) return "";

        String[] possibleFormats = {"yyyy-MM-dd", "dd/MM/yyyy"};
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (String format : possibleFormats) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, Locale.getDefault());
                parser.setLenient(false);
                Date date = parser.parse(originalDate.split(" ")[0]); // Strip time part if present
                return displayFormat.format(date);
            } catch (ParseException e) {
                // Try next format
            }
        }

        // If all parsing fails, return empty or original
        return "";
    }


    private void showZoomedImage(Bitmap bitmap) {
        Dialog dialog = new Dialog(getActivity()); // use getActivity() for Fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        PhotoView photoView = new PhotoView(getActivity());
        photoView.setImageBitmap(bitmap);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setOnClickListener(v -> dialog.dismiss());

        // Wrap in a layout for margin/padding if needed
        FrameLayout layout = new FrameLayout(getActivity());
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);
        layout.addView(photoView);

        dialog.setContentView(layout);

        // Set dialog window size (e.g., 80% of screen width/height)
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Optional: make background cleaner
        }

        dialog.show();
    }


    private void updateYesModel(
            FormItem item,
            int currentIndex,
            EditText tvDate,
            EditText etComments,
            Spinner spinnerPaymentMode,
            Spinner spinner_Compney,
            Spinner spinnerTrenching,
            EditText etBankName,
            EditText etAmount,
            EditText plantCount,
            EditText editCheckNumber,
            EditText accountnum,
            ImageView previewImage,
            ImageView previewImage2
    ) {

        String selectedDate = tvDate.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        String selectedPaymentMode = spinnerPaymentMode.getSelectedItem() != null
                ? spinnerPaymentMode.getSelectedItem().toString().trim()
                : "";

        String selectedTrenching = spinnerTrenching.getSelectedItem() != null
                ? spinnerTrenching.getSelectedItem().toString().trim()
                : "";

        String selectedCompney = spinner_Compney.getSelectedItem() != null
                ? spinner_Compney.getSelectedItem().toString().trim()
                : "";

        // Basic validations
        if (selectedDate.isEmpty()) {
            UiUtils.showCustomToastMessage("Please Select Date.", getActivity(), 1);


            return;
        }


        // Status-wise validation
        int statusId = item.statusTypeId;
        switch (statusId) {
            case 826:
                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);

                    return;
                }
                break;

            case 827:
//                if (item.uploadPdf && (currentPdfPath == null || currentPdfPath.isEmpty())) {
//                    UiUtils.showCustomToastMessage("Please upload a document.", this, 0);
//
//                    return;
//                }
                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);

                    return;
                }
                if (etAmount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Amount.", getActivity(), 1);

                    return;
                }

                break;

            case 828:

                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);
                    return;
                }

                if (selectedPaymentMode.equalsIgnoreCase("-- Select Mode Of Payments --") || selectedPaymentMode.isEmpty()) {
                    // Toast.makeText(this, "Please Select Mode Of Payments.", Toast.LENGTH_SHORT).show();

                    UiUtils.showCustomToastMessage("Please Select Mode Of Payments.", getActivity(), 1);
                    return;
                }
                if (etAmount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Amount.", getActivity(), 1);
                    return;
                }
                String selectedPaymentModeId = CommonUtils.getKeyFromValue(paymentmodeMap, selectedPaymentMode);
                switch (selectedPaymentModeId) {
                    case "834":
                    case "847":
                        if (editCheckNumber.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Cheque or UPI number.", getActivity(), 1);

                            return;
                        }
                        break;
                    case "836":
                        if (etBankName.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Bank Name.", getActivity(), 1);

                            return;
                        }
                        if (accountnum.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Account Number.", getActivity(), 1);

                            return;
                        }

                        if (accountnum.getText().toString().trim().length() < 9 || accountnum.getText().toString().trim().length() > 18) {
                            UiUtils.showCustomToastMessage("Please Enter Valid Account Number", getActivity(), 1);

                            return ;
                        }
                        break;
                }

//                if (item.uploadImage && (previewImage.getDrawable() == null)) {
//                    UiUtils.showCustomToastMessage("Please upload a DD image.", this, 0);
//
//                    return;
//                }
//                if (item.uploadImage && (previewImage2.getDrawable() == null)) {
//                    UiUtils.showCustomToastMessage("Please upload An Acknowledgement Image.", this, 0);
//
//                    return;
//                }

                break;

            case 829:
                if (selectedTrenching.equalsIgnoreCase("-- Select Trench Marking --") || selectedTrenching.isEmpty()) {

                    UiUtils.showCustomToastMessage("Please Select Trench Marking.", getActivity(), 1);
                    return;
                }
                break;

            case 830:
                if (plantCount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Horticulture Recommended Plants Count.", getActivity(), 1);

                    return;
                }
                break;
            case 831:
                UiUtils.showCustomToastMessage("Horticulture Team Verification Saved Successfully. ", getActivity(), 0);

                return; // Do not unlock next

            case 851:
//                if (item.uploadImage && currentPhotoBitmap851 == null) {
//                    UiUtils.showCustomToastMessage("Please upload a Material Received image.", this, 0);
//
//                    return;
//                }
                break;

            case 832:
//                if (item.uploadImage && currentPhotoBitmap832 == null) {
//                    UiUtils.showCustomToastMessage("Please upload a Drip Installation image.", this, 0);
//                    //    Toast.makeText(this, "Please upload a Drip Installation image.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                break;

            // ✅ Success toast (this will show only if all validation passed and no return)


        }
        String plotCode = CommonConstants.PLOT_CODE;
        Log.d("DripUpdate", "Start updateYesModel - plotCode: " + plotCode + ", statusTypeId: " + item.statusTypeId);

        MandalId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getmandalId(plotCode));


        ArrayList<HorticultureConfig> configList =
                dataAccessHandler.getHorticultureConfigdata(Queries.getInstance().gethonameid(MandalId));

        if (configList != null && !configList.isEmpty()) {
            HorticultureConfig config = configList.get(0); // First (and only) record

            horticulture_honameid = config.getId();
            horticulturehoname = config.getHOName();

            Log.d("Horticulture", "Id: " + horticulture_honameid + ", HOName: " + horticulturehoname);
            horticulture_honame.setText(" : " + horticulturehoname + "");
            // Use as needed
        } else {
            Log.d("Horticulture", "No data found for MandalId: " + MandalId);
        }
        List<DripIrrigationModel> dripList = (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);
        if (dripList == null) {
            dripList = new ArrayList<>();
            Log.d("DripUpdate", "Drip list was null, initialized new list.");
        }

        DripIrrigationModel matchedModel = null;
        DripIrrigationModel oldModel = null;

        // ✅ Try to find existing model
        for (DripIrrigationModel model : dripList) {
            if (model.getStatusTypeId() == item.statusTypeId && plotCode.equals(model.getPlotCode())) {
                matchedModel = model;
                Log.d("DripUpdate", "Matched model found in memory.");
                break;
            }
        }

        // ✅ Create new model if not matched, and preserve old image data
        if (matchedModel == null) {
            Log.d("DripUpdate", "No matched model found. Creating new model.");
            matchedModel = new DripIrrigationModel();
            matchedModel.setPlotCode(plotCode);
            matchedModel.setStatusTypeId(item.statusTypeId);

            // 🔍 Try to fetch from DB
            oldModel = (DripIrrigationModel) dataAccessHandler.getDripIrrigationDetails(
                    Queries.getInstance().getDripIrrigationstatuswise(plotCode, item.statusTypeId), 0);
            if (oldModel != null) {
                Log.d("DripUpdate", "Old model fetched from DB. Preserving image data.");
                matchedModel.setFileName(oldModel.getFileName());
                matchedModel.setFileLocation(oldModel.getFileLocation());
                matchedModel.setFileExtension(oldModel.getFileExtension());
                matchedModel.setAckFileName(oldModel.getAckFileName());
                matchedModel.setAckFileLocation(oldModel.getAckFileLocation());
                matchedModel.setAckFileExtension(oldModel.getAckFileExtension());
            } else {
                Log.d("DripUpdate", "No old model found in DB.");
            }

            dripList.add(matchedModel);
        }

        // ✅ Common field updates
        matchedModel.setDripStatusDone(1);
        matchedModel.setDate(tvDate.getText().toString().trim());
        matchedModel.setComments(etComments.getText().toString().trim());
        matchedModel.setUpdatedDate(dateFormat.format(new Date()));

        Log.d("DripUpdate", "Updated common fields. Date: " + matchedModel.getDate() + ", Comments: " + matchedModel.getComments());

        String selectedCompany = spinner_Compney.getSelectedItem() != null
                ? spinner_Compney.getSelectedItem().toString().trim() : "";

        Log.d("DripUpdate", "Selected company: " + selectedCompany);

        switch (item.statusTypeId) {
            case 826:
                matchedModel.setCompanyId(CommonUtils.getKey_FromValue(CompneyMap, selectedCompany));
                Log.d("DripUpdate", "Status 826: Set Company ID.");
                break;

            case 827:
                matchedModel.setCompanyId(CommonUtils.getKey_FromValue(CompneyMap, selectedCompany));
                matchedModel.setFileName(currentPdffilename);
                matchedModel.setFileLocation(currentPdfPath);
                matchedModel.setFileExtension(".pdf");
                matchedModel.setAmount(Double.parseDouble(etAmount.getText().toString()));
                Log.d("DripUpdate", "Status 827: PDF set - " + currentPdfPath);
                break;

            case 828:
                matchedModel.setCompanyId(CommonUtils.getKey_FromValue(CompneyMap, selectedCompany));
                matchedModel.setModeOfPayment(CommonUtils.getKey_FromValue(paymentmodeMap,
                        spinnerPaymentMode.getSelectedItem().toString().trim()));

                if (currentPhotoBitmap != null) {
                    matchedModel.setFileName("dd_paid");
                    String path = saveBitmapToFile(currentPhotoBitmap, "dd_paid");
                    matchedModel.setFileLocation(path);
                    matchedModel.setFileExtension(".jpg");
                    Log.d("DripUpdate", "DD Photo saved at: " + path);
                }

                if (currentPhotoBitmap2 != null) {
                    matchedModel.setAckFileName("Ack_paid");
                    String path2 = saveBitmapToFile(currentPhotoBitmap2, "Ack_paid");
                    matchedModel.setAckFileLocation(path2);
                    matchedModel.setAckFileExtension(".jpg");
                    Log.d("DripUpdate", "Acknowledgement Photo saved at: " + path2);
                }

                matchedModel.setDdBank(etBankName.getText().toString().trim());
                matchedModel.setAmount(etAmount.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(etAmount.getText().toString()));
                matchedModel.setDdChequeNumber(editCheckNumber.getText().toString().trim());
                matchedModel.setDdBankAccountNumber(accountnum.getText().toString().trim());

                Log.d("DripUpdate", "Status 828: DD details saved.");
                break;

            case 829:
                Integer trenchId = CommonUtils.getKey_FromValue(trenchMap,
                        spinnerTrenching.getSelectedItem().toString().trim());
                matchedModel.setTrenchMarkingTypeId(trenchId != null && trenchId != 0 ? trenchId : null);
                matchedModel.setCompanyId(DDcompanyId);
                Log.d("DripUpdate", "Status 829: Trench ID - " + trenchId);
                break;

            case 830:
                if (!plantCount.getText().toString().isEmpty()) {
                    matchedModel.setHorticultureRecommendedPlants(
                            Integer.parseInt(plantCount.getText().toString()));
                    Log.d("DripUpdate", "Status 830: Recommended Plants - " + plantCount.getText().toString());
                }
                matchedModel.setHOId(horticulture_honameid);
                matchedModel.setCompanyId(DDcompanyId);
                break;

            case 831:
                matchedModel.setDripStatusDone(1);
                Log.d("DripUpdate", "Status 831: DripStatusDone marked.");
                break;

            case 851:
                if (currentPhotoBitmap851 != null) {
                    matchedModel.setFileName("material_receive");
                    String matPath = saveBitmapToFile(currentPhotoBitmap851, "material_receive");
                    matchedModel.setFileLocation(matPath);
                    matchedModel.setFileExtension(".jpg");
                    Log.d("DripUpdate", "Status 851: Material photo saved at - " + matPath);
                }
                matchedModel.setCompanyId(DDcompanyId);
                break;

            case 832:
                matchedModel.setCompanyId(DDcompanyId);
                Log.d("DripUpdate", "Status 832: No photo being handled currently.");
                break;
        }

        // ✅ Deactivate other entries with same plot and statusTypeId
        for (DripIrrigationModel drip : dripList) {
            if (plotCode.equals(drip.getPlotCode()) &&
                    drip.getStatusTypeId() == item.statusTypeId &&
                    drip != matchedModel) {
                drip.setIsActive(0);
                Log.d("DripUpdate", "Deactivated older model with same plot and statusTypeId.");
            }
        }

        // ✅ Save to DataManager
        DataManager.getInstance().addData(DataManager.DripIrrigation, dripList);
        Log.d("DripUpdate", "Saved updated list to DataManager. List size: " + dripList.size());
        UiUtils.showCustomToastMessage(item.value + " Updated Successfully.", getActivity(), 0);
        // Toast.makeText(this, "Step updated successfully.", Toast.LENGTH_SHORT).show();
    }


    private void handleAnswerSelection(
            boolean isYes,
            FormItem item,
            View card,
            int currentIndex
    ) {
        // Basic UI references
        TextView tvDate = card.findViewById(R.id.date_text);
        EditText etComments = card.findViewById(R.id.editTextComments);
        EditText editDateselection = card.findViewById(R.id.editDateselection);
        EditText PlantCount = card.findViewById(R.id.PlantCount);
        horticulture_honamell = card.findViewById(R.id.horticulture_honamell);
        Button btnOk = card.findViewById(R.id.btn_ok);

        // PDF handling
        Button btnUploadPdf = card.findViewById(R.id.btn_upload_pdf);
        layoutPdfPreview = card.findViewById(R.id.layout_pdf_preview);
        tvPdfFileName = card.findViewById(R.id.tv_pdf_file_name);
        btnCancelPdf = card.findViewById(R.id.btn_cancel_pdf);

        // DD & Ack image
        tv_ack_image = card.findViewById(R.id.tv_ack_image);
        tv_dd_image = card.findViewById(R.id.tv_dd_image);
        btnUploadDdImage = card.findViewById(R.id.btn_upload_dd_image);
        btnUploadAckImage = card.findViewById(R.id.btn_upload_ack_image);
        layoutDdImage = card.findViewById(R.id.layout_dd_image);
        layoutAckImage = card.findViewById(R.id.layout_ack_image);
        previewImageDd = card.findViewById(R.id.preview_image_dd);
        previewImageAck = card.findViewById(R.id.preview_image_ack);

        // Step 7 & 8 image
        btnAddImage851 = card.findViewById(R.id.btn_add_image_851);
        layoutImage851 = card.findViewById(R.id.layout_image_851);
        previewImage851 = card.findViewById(R.id.preview_image_851);
        deleteImage851 = card.findViewById(R.id.delete_image_851);
        tvImage851 = card.findViewById(R.id.tvImage851);
        tvImage832 = card.findViewById(R.id.tvImage832);
        btnAddImage832 = card.findViewById(R.id.btn_add_image_832);
        layoutImage832 = card.findViewById(R.id.layout_image_832);
        previewImage832 = card.findViewById(R.id.preview_image_832);
        deleteImage832 = card.findViewById(R.id.delete_image_832);
        btn_delete_image_dd = card.findViewById(R.id.btn_delete_image_dd);
        btn_delete_image_ack = card.findViewById(R.id.btn_delete_image_ack);
        // Payment
        Spinner spinnerPaymentMode = card.findViewById(R.id.spinner_payment_mode);
        Spinner spinner_Compney = card.findViewById(R.id.spinner_Compney);
        Spinner spinnerTrenching = card.findViewById(R.id.spinner_trenching);
        EditText etAmount = card.findViewById(R.id.et_amount);
        EditText etBankName = card.findViewById(R.id.et_bank_name);
        EditText etchecknumber = card.findViewById(R.id.etchecknumber);
        EditText Accountnum = card.findViewById(R.id.Accountnum);
        TextView tvApiBoundText = card.findViewById(R.id.tv_api_bound_text);
        horticulture_honamell = card.findViewById(R.id.horticulture_honamell);
        horticulture_honame = card.findViewById(R.id.horticulture_honame);
        TextView  tv_comments = card.findViewById(R.id.tv_comments);
        TextView  tv_selectdate = card.findViewById(R.id.tv_selectdate);
        TextView tv_selectcompany = card.findViewById(R.id.tv_selectcompany);
        TextView tv_selectpaymentmode = card.findViewById(R.id.tv_selectpaymentmode);
        TextView  tv_amount = card.findViewById(R.id.tv_amount);
        TextView tv_bank_name = card.findViewById(R.id.tv_bank_name);
        TextView tv_account_number = card.findViewById(R.id.tv_account_number);
        TextView tv_checknumber = card.findViewById(R.id.tv_checknumber);
        TextView  tv_trenching = card.findViewById(R.id.tv_trenching);
        TextView  tv_plantcount = card.findViewById(R.id.tv_plantcount);
        // Reset all fields
        editDateselection.setText("");
        etComments.setText("");
        PlantCount.setText("");
        etAmount.setText("");
        etBankName.setText("");
        etchecknumber.setText("");
        Accountnum.setText("");

        // Default visibilities
        editDateselection.setVisibility(isYes && item.showDate ? View.VISIBLE : View.GONE);
        etComments.setVisibility(isYes && item.showComments ? View.VISIBLE : View.GONE);
        btnUploadPdf.setVisibility(isYes && item.uploadPdf ? View.VISIBLE : View.GONE);
//        btnUploadDdImage.setVisibility(isYes && item.uploadImage ? View.VISIBLE : View.GONE);
//        btnUploadAckImage.setVisibility(isYes && item.uploadImage ? View.VISIBLE : View.GONE);
        btnOk.setVisibility(View.VISIBLE);
        spinner_Compney.setVisibility(View.GONE);
        layoutDdImage.setVisibility(View.GONE);
        layoutAckImage.setVisibility(View.GONE);
        layoutImage851.setVisibility(View.GONE);
        layoutImage832.setVisibility(View.GONE);
        btnAddImage851.setVisibility(View.GONE);
        btnAddImage832.setVisibility(View.GONE);
        tvImage832.setVisibility(View.GONE);
        horticulture_honamell.setVisibility(View.GONE);

        // On date click
        editDateselection.setOnClickListener(v -> openDatePicker((EditText) v));

        // Status specific logic
        if (isYes) {
            tv_comments.setVisibility(View.VISIBLE);
            switch (item.statusTypeId) {

                case 826:
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    tv_selectdate.setVisibility(View.VISIBLE);
                    spinner_Compney.setVisibility(View.VISIBLE);
                    dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> adapterc = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(CompneyMap, "Company Name"));
                    adapterc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(adapterc);
                    break;

                case 827:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    tv_amount.setVisibility(View.VISIBLE);
                    etAmount.setVisibility(View.VISIBLE);
                    btnUploadPdf.setVisibility(View.VISIBLE);
                    spinner_Compney.setVisibility(View.VISIBLE);
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(CompneyMap, "Company Name"));
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(adapter1);
                    break;

                case 828:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    tv_selectpaymentmode.setVisibility(View.VISIBLE);
                    spinnerPaymentMode.setVisibility(item.additionalText ? View.VISIBLE : View.GONE);
                    etAmount.setVisibility(item.additionalText ? View.VISIBLE : View.GONE);
                    etBankName.setVisibility(View.VISIBLE);
                    etchecknumber.setVisibility(item.additionalText ? View.VISIBLE : View.GONE);
                    Accountnum.setVisibility(View.VISIBLE);
                    btnUploadDdImage.setVisibility(View.VISIBLE);
                    btnUploadAckImage.setVisibility(View.VISIBLE);
                    tv_ack_image.setVisibility(View.VISIBLE);
                    tv_dd_image.setVisibility(View.VISIBLE);
                    spinner_Compney.setVisibility(View.VISIBLE);
                    tv_selectcompany.setVisibility(View.VISIBLE);
                    // Load company
                    CompneyMap = dataAccessHandler.getGenericData(Queries.getInstance().getcompneyinfo());
                    ArrayAdapter<String> adapterc2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(CompneyMap, "Company Name"));
                    adapterc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_Compney.setAdapter(adapterc2);

                    // Load payment modes
                    paymentmodeMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("139"));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(paymentmodeMap, "Mode Of Payments"));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPaymentMode.setAdapter(adapter);

                    spinnerPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedMode = spinnerPaymentMode.getSelectedItem().toString();
                            // Skip processing if default item is selected
                            if (selectedMode.contains("Select Mode Of Payments")) {
                                // Optionally clear the form if needed
                                etAmount.setVisibility(View.GONE);
                                etchecknumber.setVisibility(View.GONE);
                                etBankName.setVisibility(View.GONE);
                                Accountnum.setVisibility(View.GONE);
                                tv_amount.setVisibility(View.GONE);
                                tv_checknumber.setVisibility(View.GONE);
                                tv_bank_name.setVisibility(View.GONE);
                                tv_account_number.setVisibility(View.GONE);
                                return;
                            }

                            String selectedPaymentModeId = CommonUtils.getKeyFromValue(paymentmodeMap, selectedMode);

                            if (selectedPaymentModeId == null) {
                                Log.e("SpinnerError", "No match for selectedMode: [" + selectedMode + "]");
                                return;
                            }
                            etAmount.setVisibility(View.GONE);
                            etchecknumber.setVisibility(View.GONE);
                            etBankName.setVisibility(View.GONE);
                            Accountnum.setVisibility(View.GONE);
                            etchecknumber.setHint("");
                            tv_checknumber.setVisibility(View.GONE);
                            tv_bank_name.setVisibility(View.GONE);
                            tv_account_number.setVisibility(View.GONE);
                            tv_amount.setVisibility(View.GONE);

                            switch (selectedPaymentModeId) {
                                case "834":
                                    tv_amount.setVisibility(View.VISIBLE);
                                    tv_checknumber.setVisibility(View.VISIBLE);
                                    tv_checknumber.setText(" Cheque Number");
                                    etAmount.setVisibility(View.VISIBLE);
                                    etchecknumber.setVisibility(View.VISIBLE);
                                    etchecknumber.setHint("Enter  Cheque Number");
                                    break;
                                case "835":
                                    tv_amount.setVisibility(View.VISIBLE);
                                    etAmount.setVisibility(View.VISIBLE);
                                    break;
                                case "836":
                                    tv_amount.setVisibility(View.VISIBLE);
                                    tv_bank_name.setVisibility(View.VISIBLE);
                                    tv_account_number.setVisibility(View.VISIBLE);
                                    etAmount.setVisibility(View.VISIBLE);
                                    etBankName.setVisibility(View.VISIBLE);
                                    Accountnum.setVisibility(View.VISIBLE);
                                    break;
                                case "847":
                                    tv_checknumber.setVisibility(View.VISIBLE);
                                    tv_checknumber.setText("UPI Number");
                                    tv_amount.setVisibility(View.VISIBLE);
                                    etAmount.setVisibility(View.VISIBLE);
                                    etchecknumber.setVisibility(View.VISIBLE);
                                    etchecknumber.setHint("Enter UPI Number");
                                    break;
                                default:
                                    Log.e("UnknownMode", "Unhandled payment mode id: " + selectedPaymentModeId);
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            etAmount.setVisibility(View.GONE);
                            etchecknumber.setVisibility(View.GONE);
                            etBankName.setVisibility(View.GONE);
                            Accountnum.setVisibility(View.GONE);
                            etchecknumber.setHint("");
                        }
                    });
                    break;

                case 829:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    tv_trenching.setVisibility(View.VISIBLE);
                    spinnerTrenching.setVisibility(View.VISIBLE);
                    trenchMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("140"));
                    ArrayAdapter<String> trenchAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(trenchMap, "Trench Marking"));
                    trenchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTrenching.setAdapter(trenchAdapter);
                    break;

                case 830:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    tv_plantcount.setVisibility(View.VISIBLE);
                    PlantCount.setVisibility(View.VISIBLE);
                    horticulture_honamell.setVisibility(View.VISIBLE);
                    break;

                case 851:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    etComments.setVisibility(View.VISIBLE);
                    btnAddImage851.setVisibility(View.VISIBLE);
                    tvImage851.setVisibility(View.VISIBLE);

                    //   layoutImage851.setVisibility(View.VISIBLE);
                    break;

                case 832:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    etComments.setVisibility(View.VISIBLE);
                    btnAddImage832.setVisibility(View.VISIBLE);
                    tvImage832.setVisibility(View.VISIBLE);
                    //layoutImage832.setVisibility(View.VISIBLE);
                    break;

                case 831:
                    tv_selectdate.setVisibility(View.VISIBLE);
                    card.findViewById(R.id.answer_group).setEnabled(false);
                    tvApiBoundText.setVisibility(View.VISIBLE);
                    tvApiBoundText.setText("Approved by Admin: " + item.statusTypeId);
                    break;
            }
        }

        else {
            spinnerPaymentMode.setVisibility(View.GONE);
            spinner_Compney.setVisibility(View.GONE);
            spinnerTrenching.setVisibility(View.GONE);
            PlantCount.setVisibility(View.GONE);
            etAmount.setVisibility(View.GONE);
            etchecknumber.setVisibility(View.GONE);
            etBankName.setVisibility(View.GONE);
            Accountnum.setVisibility(View.GONE);
            btnUploadDdImage.setVisibility(View.GONE);
            tv_ack_image.setVisibility(View.GONE);
            tv_dd_image.setVisibility(View.GONE);
            btnUploadAckImage.setVisibility(View.GONE);
            layoutDdImage.setVisibility(View.GONE);
            layoutAckImage.setVisibility(View.GONE);
            btnOk.setVisibility(View.GONE);
            tv_selectdate.setVisibility(View.GONE);
            tv_trenching.setVisibility(View.GONE);
            tv_comments.setVisibility(View.GONE);
            tv_selectcompany.setVisibility(View.GONE);
            tv_selectpaymentmode.setVisibility(View.GONE);
            tv_amount.setVisibility(View.GONE);
            tv_bank_name.setVisibility(View.GONE);
            tv_account_number.setVisibility(View.GONE);
            tv_checknumber.setVisibility(View.GONE);
            tv_trenching.setVisibility(View.GONE);
            PlantCount.setVisibility(View.GONE);
            etComments.setVisibility(View.GONE);
            btnAddImage851.setVisibility(View.GONE);
            tvImage851.setVisibility(View.GONE);
            btnAddImage832.setVisibility(View.GONE);
            tvImage832.setVisibility(View.GONE);
            tvApiBoundText.setVisibility(View.GONE);
            tv_plantcount.setVisibility(View.GONE);
            tv_comments.setVisibility(View.GONE);
            //  tv_selectdate,tv_selectcompany,tv_selectpaymentmode,tv_amount,tv_bank_name,tv_account_number,tv_checknumber,tv_trenching,tv_plantcount,tv_comments

        }
    }

    private boolean isAlreadySaved(int statusTypeId) {
        // 1. Check from saved in-memory list (DataManager)
        List<DripIrrigationModel> savedList =
                (List<DripIrrigationModel>) DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);

        if (savedList != null) {
            for (DripIrrigationModel model : savedList) {
                Log.e("CheckSaved", "From DataManager -> statusTypeId: " + model.getStatusTypeId() +
                        ", plotCode: " + model.getPlotCode() +
                        ", isActive: " + model.getIsActive() +
                        ", dripStatusDone: " + model.getDripStatusDone());

                // Save companyId if statusTypeId is 828
                if (model.getStatusTypeId() == 828) {
                    DDcompanyId = model.getCompanyId(); // Assuming getCompanyId() exists
                    Log.e("CheckSaved", "Captured CompanyId for statusTypeId 828: " + DDcompanyId);
                    // You can save it to a variable, file, or DB here if needed
                }

                if (model.getStatusTypeId() == statusTypeId &&
                        model.getPlotCode().equals(CommonConstants.PLOT_CODE) &&
                        model.getDripStatusDone() == 1) {
                    return true;
                }
            }
        }

        // 2. Check directly from database list (fresh query)
        List<DripIrrigationModel> dbList = (List<DripIrrigationModel>)
                dataAccessHandler.getDripIrrigationDetails(
                        Queries.getInstance().getDripIrrigation(CommonConstants.PLOT_CODE), 1);

        Log.e("CheckSaved", "DB Records count: " + (dbList != null ? dbList.size() : 0));

        if (dbList != null) {
            for (DripIrrigationModel model : dbList) {
                Log.e("CheckSaved", "From DB -> statusTypeId: " + model.getStatusTypeId() +
                        ", plotCode: " + model.getPlotCode() +
                        ", isActive: " + model.getIsActive() +
                        ", dripStatusDone: " + model.getDripStatusDone());

                // Save companyId if statusTypeId is 828
                if (model.getStatusTypeId() == 828) {
                    DDcompanyId = model.getCompanyId(); // Assuming getCompanyId() exists
                    Log.e("CheckSaved", "Captured CompanyId for statusTypeId 828 (DB): " + DDcompanyId);
                    // Save logic can go here
                }

                if (model.getStatusTypeId() == statusTypeId &&
                        model.getPlotCode().equals(CommonConstants.PLOT_CODE) &&
                        model.getDripStatusDone() == 1) {
                    return true;
                }
            }
        }

        return false;
    }


    private void disableAllInputs(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                disableAllInputs(group.getChildAt(i));
            }

            // Special handling for RadioGroup
            if (view instanceof RadioGroup) {
                group.setEnabled(false); // disables the RadioGroup container
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof RadioButton) {
                        child.setEnabled(false);  // disable each RadioButton
                        child.setAlpha(0.5f);     // visually dim
                    }
                }
            }

        } else {
            view.setEnabled(false);
            view.setFocusable(false);
            view.setClickable(false);
            view.setAlpha(0.5f); // optional: dim the element
        }
    }


    private void saveYesModel(
            FormItem item,
            int currentIndex,
            EditText tvDate,
            EditText etComments,
            Spinner spinnerPaymentMode,
            Spinner spinner_Compney,
            Spinner spinnerTrenching,
            EditText etBankName,
            EditText etAmount,
            EditText plantCount,
            EditText editCheckNumber,
            EditText accountnum,
            ImageView previewImage,
            ImageView previewImage2,
            ImageView previewImage3,
            ImageView previewImage4
    ) {
        String selectedDate = tvDate.getText().toString().trim();
        String comments = etComments.getText().toString().trim();

        String selectedPaymentMode = spinnerPaymentMode.getSelectedItem() != null
                ? spinnerPaymentMode.getSelectedItem().toString().trim()
                : "";

        String selectedTrenching = spinnerTrenching.getSelectedItem() != null
                ? spinnerTrenching.getSelectedItem().toString().trim()
                : "";

        String selectedCompney = spinner_Compney.getSelectedItem() != null
                ? spinner_Compney.getSelectedItem().toString().trim()
                : "";
        Log.e("CompanyIdCheck", " companyId is null. selectedCompney = " + selectedCompney);
        // Basic validations
        if (selectedDate.isEmpty()) {
            UiUtils.showCustomToastMessage("Please Select Date.", getActivity(), 1);
            //  Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Status-wise validation
        int statusId = item.statusTypeId;
        switch (statusId) {
            case 826:
                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);

                    return;
                }
                break;

            case 827:
                if (item.uploadPdf && (currentPdfPath == null || currentPdfPath.isEmpty())) {
                    UiUtils.showCustomToastMessage("Please Upload Document.", getActivity(), 1);

                    return;
                }
                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);

                    return;
                }
                if (etAmount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Amount.", getActivity(), 1);

                    return;
                }

                break;

            case 828:

                if (selectedCompney.equalsIgnoreCase("-- Select Company Name --") || selectedCompney.isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Select Company Name.", getActivity(), 1);
                    return;
                }

                if (selectedPaymentMode.equalsIgnoreCase("-- Select Mode Of Payments --") || selectedPaymentMode.isEmpty()) {
                    // Toast.makeText(this, "Please Select Mode Of Payments.", Toast.LENGTH_SHORT).show();

                    UiUtils.showCustomToastMessage("Please Select Mode Of Payments.", getActivity(), 1);
                    return;
                }
                if (etAmount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Amount.", getActivity(), 1);
                    return;
                }
                String selectedPaymentModeId = CommonUtils.getKeyFromValue(paymentmodeMap, selectedPaymentMode);
                switch (selectedPaymentModeId) {
                    case "834":
                    case "847":
                        if (editCheckNumber.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Cheque or UPI Number.", getActivity(), 1);

                            return;
                        }
                        break;
                    case "836":
                        if (etBankName.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Bank name.", getActivity(), 1);

                            return;
                        }
                        if (accountnum.getText().toString().trim().isEmpty()) {
                            UiUtils.showCustomToastMessage("Please Enter Account number.", getActivity(), 1);

                            return;
                        }
                        if (accountnum.getText().toString().trim().length() < 9 || accountnum.getText().toString().trim().length() > 18) {
                            UiUtils.showCustomToastMessage("Please Enter Valid Account Number", getActivity(), 1);

                            return ;
                        }
                        break;
                }

                if (item.uploadImage && (previewImage.getDrawable() == null)) {
                    UiUtils.showCustomToastMessage("Please Upload  DD image.", getActivity(), 1);

                    return;
                }
                if (item.uploadImage && (previewImage2.getDrawable() == null)) {
                    UiUtils.showCustomToastMessage("Please Upload  Acknowledgement Image.", getActivity(), 1);

                    return;
                }

                break;

            case 829:
                if (selectedTrenching.equalsIgnoreCase("-- Select Trench Marking --") || selectedTrenching.isEmpty()) {

                    UiUtils.showCustomToastMessage("Please Select Trench Marking.", getActivity(), 1);
                    return;
                }
                break;

            case 830:
                if (plantCount.getText().toString().trim().isEmpty()) {
                    UiUtils.showCustomToastMessage("Please Enter Horticulture Recommended Plants Count.", getActivity(), 1);

                    return;
                }
                break;
            case 831:
                UiUtils.showCustomToastMessage("Horticulture Team Verification Saved Successfully. ", getActivity(), 0);

                return; // Do not unlock next

            case 851:
                if (item.uploadImage && currentPhotoBitmap851 == null) {
                    UiUtils.showCustomToastMessage("Please Upload  Material Received Image.", getActivity(), 1);

                    return;
                }
                break;

            case 832:
                if (item.uploadImage && currentPhotoBitmap832 == null) {
                    UiUtils.showCustomToastMessage("Please Upload Drip Installation Image.", getActivity(), 1);
                    //    Toast.makeText(this, "Please upload a Drip Installation image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            // ✅ Success toast (this will show only if all validation passed and no return)


        }
//        Toast.makeText(this, "Please continue to next.", Toast.LENGTH_SHORT).show();
        // Create model
        DripIrrigationModel model = new DripIrrigationModel();
        String plotCode = CommonConstants.PLOT_CODE;

        MandalId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getmandalId(plotCode));


        ArrayList<HorticultureConfig> configList =
                dataAccessHandler.getHorticultureConfigdata(Queries.getInstance().gethonameid(MandalId));

        if (configList != null && !configList.isEmpty()) {
            HorticultureConfig config = configList.get(0); // First (and only) record

            horticulture_honameid = config.getId();
            horticulturehoname = config.getHOName();

            Log.d("Horticulture", "Id: " + horticulture_honameid + ", HOName: " + horticulturehoname);

            // Use as needed
        } else {
            Log.d("Horticulture", "No data found for MandalId: " + MandalId);
        }


        model.setPlotCode(plotCode);
        model.setStatusTypeId(statusId);
        model.setDripStatusDone(1);
        model.setDate(selectedDate);
        model.setComments(comments.isEmpty() ? null : comments);
        model.setCreatedDate(dateFormat.format(new Date()));
        model.setUpdatedDate(dateFormat.format(new Date()));
        model.setServerUpdatedStatus(0);
        model.setIsActive(1);

        // Reset extra fields
        model.setFileName(null);
        model.setFileLocation(null);
        model.setFileExtension(null);
        model.setDdBank(null);
        model.setAmount(null);
        model.setDdBankAccountNumber(null);
        model.setDdChequeNumber(null);
        model.setTrenchMarkingTypeId(null);
        model.setHorticultureRecommendedPlants(null);

        // Set conditional data
        switch (statusId) {
            case 826: // Servey done (only Company ID)
                Integer companyId826 = CommonUtils.getKey_FromValue(CompneyMap, selectedCompney);
                if (companyId826 == null) {
                    Log.e("CompanyIdCheck", "Status 826: companyId is null. selectedCompney = " + selectedCompney);
                }
                model.setCompanyId(companyId826);
                break;

            case 827: // BOQ Upload with PDF
                model.setAmount(Double.parseDouble(etAmount.getText().toString()));
                Integer companyId827 = CommonUtils.getKey_FromValue(CompneyMap, selectedCompney);
                if (companyId827 == null) {
                    Log.e("CompanyIdCheck", "Status 827: companyId is null. selectedCompney = " + selectedCompney);
                }
                model.setCompanyId(companyId827);
                model.setFileName(currentPdffilename);
                model.setFileLocation(currentPdfPath);
                model.setFileExtension(".pdf");
                break;

            case 828:
                Integer payModeId = CommonUtils.getKey_FromValue(paymentmodeMap, selectedPaymentMode);
                Integer compId = CommonUtils.getKey_FromValue(CompneyMap, selectedCompney);
                if (compId == null) {
                    Log.e("CompanyIdCheck", "Status 828: companyId is null. selectedCompney = " + selectedCompney);
                }
                model.setModeOfPayment(payModeId != null && payModeId != 0 ? payModeId : null);
                model.setCompanyId(compId);
                String fileLoc = saveBitmapToFile(currentPhotoBitmap, "dd_paid");
                model.setFileName("dd_paid");
                model.setFileLocation(fileLoc);
                model.setFileExtension(".jpg");
                String fileLoc2 = saveBitmapToFile(currentPhotoBitmap2, "Ack_paid");
                model.setAckFileName("Ack_paid");
                model.setAckFileLocation(fileLoc2);
                model.setAckFileExtension(".jpg");

                model.setDdBank(etBankName.getText().toString());
                model.setAmount(Double.parseDouble(etAmount.getText().toString()));
                model.setDdChequeNumber(editCheckNumber.getText().toString());
                model.setDdBankAccountNumber(accountnum.getText().toString());
                break;

            case 829:
                Integer trenchId = CommonUtils.getKey_FromValue(trenchMap, selectedTrenching);
                model.setTrenchMarkingTypeId(trenchId != null && trenchId != 0 ? trenchId : null);
                if (DDcompanyId == 0) {
                    Log.e("CompanyIdCheck", "Status 829: DDcompanyId is null");
                }
                model.setCompanyId(DDcompanyId);
                break;

            case 830:
                model.setHorticultureRecommendedPlants(Integer.parseInt(plantCount.getText().toString()));
                model.setHOId(horticulture_honameid);
                if (DDcompanyId == 0) {
                    Log.e("CompanyIdCheck", "Status 830: DDcompanyId is null");
                }
                model.setCompanyId(DDcompanyId);
                break;

            case 831:
                model.setDripStatusDone(1); // Always yes
                break;

            case 851:
                String file851 = saveBitmapToFile(currentPhotoBitmap851, "material_receive");
                model.setFileName("material_receive");
                model.setFileLocation(file851);
                model.setFileExtension(".jpg");
                if (DDcompanyId == 0) {
                    Log.e("CompanyIdCheck", "Status 851: DDcompanyId is null");
                }
                model.setCompanyId(DDcompanyId);
                break;

            case 832:
                String file832 = saveBitmapToFile(currentPhotoBitmap832, "installation");
                model.setFileName("installation");
                model.setFileLocation(file832);
                model.setFileExtension(".jpg");
                if (DDcompanyId == 0) {
                    Log.e("CompanyIdCheck", "Status 832: DDcompanyId is null");
                }
                model.setCompanyId(DDcompanyId);
                break;
        }


        // Add to list
        List<DripIrrigationModel> dripList = (List<DripIrrigationModel>)
                DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);

        if (dripList == null) {
            dripList = new ArrayList<>();
        } else {
            for (DripIrrigationModel drip : dripList) {
                if (plotCode.equals(drip.getPlotCode()) && drip.getIsActive() == 1) {
                    drip.setIsActive(0);
                }
            }
        }

        model.setIsActive(1);
        dripList.add(model);
        DataManager.getInstance().addData(DataManager.DripIrrigation, dripList);

        Log.d("DripSave", "✅ Added statusTypeId: " + model.getStatusTypeId());

        // Unlock next card logic
        if (currentIndex + 1 < allCards.size()) {
            int nextIndex = currentIndex + 1;
            FormItem nextItem = formItems.get(nextIndex);
            // Check if next step already saved
            boolean isNextSaved = false;
            List<DripIrrigationModel> dripListu = (List<DripIrrigationModel>)
                    DataManager.getInstance().getDataFromManager(DataManager.DripIrrigation);

            if (dripListu != null) {
                for (DripIrrigationModel m : dripListu) {
                    if (m.getStatusTypeId() == nextItem.statusTypeId &&
                            m.getPlotCode().equals(CommonConstants.PLOT_CODE) &&
                            m.getDripStatusDone() == 1 &&
                            m.getIsActive() == 1) {
                        isNextSaved = true;
                        break;
                    }
                }
            }
            // Skip unlocking 6th question (statusTypeId == 831)
            if (nextItem.statusTypeId == 831) {
                UiUtils.showCustomToastMessage("Horticulture Team Verification Saved Successfully. ", getActivity(), 0);


                Log.d("FormFlow", "Skipping unlock for 6th question (disabled by design)");
                return;
            }

            if (isNextSaved) {
                Log.d("FormFlow", "Next step (" + nextItem.statusTypeId + ") already completed. Not unlocking.");
//                Toast.makeText(getActivity(), "Next step (" + nextItem.statusTypeId + ") already completed. Not unlocking.", Toast.LENGTH_SHORT).show();
                UiUtils.showCustomToastMessage("Next step (" + nextItem.statusTypeId + ") already completed. Not unlocking.", getActivity(), 0);
                return;
            }
            // ✅ Show toast for every step unlocked
            // Toast.makeText(this, "Next Step is unlocked. Please continue.", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this,  item.value + " Saved Successfully.", Toast.LENGTH_SHORT).show();
            UiUtils.showCustomToastMessage(item.value + " Saved Successfully.", getActivity(), 0);

            // Unlock next question normally (except 6th)
            View nextCard = allCards.get(nextIndex);
            RadioGroup nextRgAnswer = nextCard.findViewById(R.id.answer_group);
            RadioButton nextBtnYes = nextCard.findViewById(R.id.yes_button);
            RadioButton nextBtnNo = nextCard.findViewById(R.id.no_button);
            Button nextBtnOk = nextCard.findViewById(R.id.btn_ok);

            nextBtnYes.setChecked(true);// Default to "No" (do not trigger any field visibility)
            nextRgAnswer.check(R.id.no_button);
            nextBtnNo.setChecked(true);

// Hide radio buttons and OK initially
//            nextBtnYes.setVisibility(View.GONE);
//            nextBtnNo.setVisibility(View.GONE);
            nextBtnOk.setVisibility(View.GONE);

// Only show Yes (enabled), and wait for manual tap
            nextBtnYes.setVisibility(View.VISIBLE);
            nextBtnYes.setEnabled(true);
            nextBtnYes.setAlpha(1f);

// Enable logic only on YES selection
            nextBtnYes.setOnClickListener(v -> {
                nextRgAnswer.check(R.id.yes_button);
                nextBtnNo.setEnabled(false); // lock "No"
                nextBtnNo.setAlpha(0.5f);
                nextBtnOk.setVisibility(View.VISIBLE);
                handleAnswerSelection(true, nextItem, nextCard, nextIndex);
                // Toast.makeText(this, "Step \"" + nextItem.label + "\" saved successfully.", Toast.LENGTH_SHORT).show();
            });


            // Special Case: if current was 5th, try to unlock 7th (statusTypeId 832) based on DB value of 6th
            if (currentIndex == 4 && nextItem.statusTypeId == 832) { //TODO ROJA
                // Search 6th model from dripsavedList
                DripIrrigationModel sixthModel = null;
                for (DripIrrigationModel modell : dripsavedList) {
                    if (modell.getStatusTypeId() == 831) {
                        sixthModel = model;
                        break;
                    }
                }

                if (sixthModel != null && sixthModel.getDripStatusDone() == 1) {
                    int seventhIndex = currentIndex + 2;
                    if (seventhIndex < allCards.size()) {
                        View seventhCard = allCards.get(seventhIndex);
                        FormItem seventhItem = formItems.get(seventhIndex);

                        RadioGroup rg = seventhCard.findViewById(R.id.answer_group);
                        RadioButton yesBtn = seventhCard.findViewById(R.id.yes_button);
                        RadioButton noBtn = seventhCard.findViewById(R.id.no_button);
                        Button okBtn = seventhCard.findViewById(R.id.btn_ok);

                        yesBtn.setEnabled(true);
                        yesBtn.setAlpha(1f);
                        noBtn.setEnabled(false);
                        noBtn.setAlpha(0.5f);
                        yesBtn.setChecked(true);
                        rg.check(R.id.yes_button);
                        okBtn.setVisibility(View.VISIBLE);

                        handleAnswerSelection(true, seventhItem, seventhCard, seventhIndex);
                    }
                } else {
                    Log.d("FormFlow", "6th question not completed on web. 7th remains locked.");
                }
            }


        } else {
            UiUtils.showCustomToastMessage(item.value + " Saved Successfully.", getActivity(), 0);

            //       Toast.makeText(this, "All steps saved!", Toast.LENGTH_SHORT).show();
        }
    }


    private void openDatePicker(EditText tvDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth);
                    String selectedDate = dateFormat.format(selectedCal.getTime());
                    tvDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Image from");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;

            // ✅ From Camera
            if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            }

            // ✅ From Gallery
            else if (requestCode == RESULT_LOAD_IMAGE && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), selectedImageUri);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Image selection failed", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Image selection failed", getActivity(), 1);

                }
            }
            if (bitmap != null) {
                switch (selectedImageType) {
                    case IMAGE_TYPE_DD:
                        previewImageDd.setImageBitmap(bitmap);
                        previewImageDd.setVisibility(View.VISIBLE);
                        layoutDdImage.setVisibility(View.VISIBLE);
                        currentPhotoBitmap = bitmap;
                        btnUploadDdImage.setVisibility(View.GONE);
                        break;

                    case IMAGE_TYPE_ACK:
                        previewImageAck.setImageBitmap(bitmap);
                        previewImageAck.setVisibility(View.VISIBLE);
                        layoutAckImage.setVisibility(View.VISIBLE);
                        currentPhotoBitmap2 = bitmap;
                        btnUploadAckImage.setVisibility(View.GONE);
                        break;

                    case IMAGE_TYPE_851:
                        previewImage851.setImageBitmap(bitmap);
                        previewImage851.setVisibility(View.VISIBLE);
                        layoutImage851.setVisibility(View.VISIBLE);
                        currentPhotoBitmap851 = bitmap;
                        btnAddImage851.setVisibility(View.GONE);
                        break;

                    case IMAGE_TYPE_832:
                        previewImage832.setImageBitmap(bitmap);
                        previewImage832.setVisibility(View.VISIBLE);
                        layoutImage832.setVisibility(View.VISIBLE);
                        currentPhotoBitmap832 = bitmap;
                        btnAddImage832.setVisibility(View.GONE);
                        break;

                    default:
//                        Toast.makeText(getActivity(), "No image view target specified", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("No image view target specified", getActivity(), 1);
                }
            }

            // ✅ Assign Bitmap to the correct ImageView
//            if (bitmap != null) {
//                if (currentImageView != null) {
//                    currentImageView.setImageBitmap(bitmap);
//                    currentImageView.setVisibility(View.VISIBLE);
//                    layoutDdImage.setVisibility(View.VISIBLE);
//                    currentPhotoBitmap = bitmap;
//                    btnUploadDdImage.setVisibility(View.GONE);
//                } else if (currentImageView2 != null) {
//                    currentImageView2.setImageBitmap(bitmap);
//                    currentImageView2.setVisibility(View.VISIBLE);
//                    layoutAckImage.setVisibility(View.VISIBLE);
//                    currentPhotoBitmap2 = bitmap;
//                    btnUploadAckImage.setVisibility(View.GONE);
//                } else if (currentImageView851 != null) {
//                    currentImageView851.setImageBitmap(bitmap);
//                    currentImageView851.setVisibility(View.VISIBLE);
//                    layoutImage851.setVisibility(View.VISIBLE);
//                    currentPhotoBitmap851 = bitmap;
//                    btnAddImage851.setVisibility(View.GONE);
//                } else if (currentImageView832 != null) {
//                    currentImageView832.setImageBitmap(bitmap);
//                    currentImageView832.setVisibility(View.VISIBLE);
//                    layoutImage832.setVisibility(View.VISIBLE);
//                    currentPhotoBitmap832 = bitmap;
//                    btnAddImage832.setVisibility(View.GONE);
//                } else {
//                    Toast.makeText(this, "No image view set", Toast.LENGTH_SHORT).show();
//                }
//            }


            else if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri selectedPdfUri = data.getData();

                // Get the PDF file name
                currentPdffilename = getFileNameFromUri(selectedPdfUri);

                // Copy PDF to app directory
                String filePath = copyPdfToAppStorage(selectedPdfUri);

                if (filePath != null) {
                    // ✅ Save and update UI
                    currentPdfPath = filePath;
                    if (currentPdfButton != null) {
                        //  currentPdfButton.setText(currentPdffilename);


                        currentPdfButton.setVisibility(View.GONE);
                        layoutPdfPreview.setVisibility(View.VISIBLE);
                        tvPdfFileName.setText(currentPdffilename);
                        //   currentPdfPath = file.getAbsolutePath();

                    }

                    if (currentDeletePdf != null) {
                        currentDeletePdf.setVisibility(View.VISIBLE);
                    }

                    Log.e("PDF_UPLOAD", "Saved PDF path: " + filePath);
                } else {
//                    Toast.makeText(getActivity(), "Failed to save PDF file", Toast.LENGTH_SHORT).show();
                    UiUtils.showCustomToastMessage("Failed to save PDF file", getActivity(), 1);
                    Log.e("PDF_UPLOAD", "File path is null. PDF not saved.");
                }
            }

        }

        // ✅ Reset all pointers (always do this after handling result)
        currentImageView = null;
        currentImageView2 = null;
        currentImageView851 = null;
        currentImageView832 = null;
        selectedImageType = 0;
    }


    public String copyPdfToAppStorage(Uri pdfUri) {
        File directory = new File(CommonUtils.get3FFileRootPath() + "UploadedPDFs");
        if (!directory.exists()) directory.mkdirs();

        String fileName = getFileNameFromUri(pdfUri);
        File file = new File(directory, fileName);

        try (InputStream in = getActivity().getContentResolver().openInputStream(pdfUri);
             OutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public static String saveBitmapToFile(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            Log.e("SaveBitmap", "Bitmap is null, not saving");
            return null;
        }
        File dir = new File(CommonUtils.get3FFileRootPath() + "PalmGrow/Drip_Images");

       // File dir = new File(Environment.getExternalStorageDirectory(), "PalmGrow/Drip_Images");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            Log.d("SaveBitmap", "Saved at: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public class FormItem {
        public String label;
        public String value;
        public boolean showDate, showComments, uploadPdf, uploadImage, additionalText, isEnabled;
        public int statusTypeId;
        public boolean isRequired;
        public boolean allowMultipleImages; // ✅ NEW FIELD

        public FormItem(String label, String value, boolean showDate, boolean showComments, boolean uploadPdf,
                        boolean uploadImage, boolean additionalText, boolean isEnabled,
                        int statusTypeId, boolean isRequired, boolean allowMultipleImages) {
            this.label = label;
            this.value = value;
            this.showDate = showDate;
            this.showComments = showComments;
            this.uploadPdf = uploadPdf;
            this.uploadImage = uploadImage;
            this.additionalText = additionalText;
            this.isEnabled = isEnabled;
            this.statusTypeId = statusTypeId;
            this.isRequired = isRequired;
            this.allowMultipleImages = allowMultipleImages; // ✅ NEW FIELD
        }
    }


}

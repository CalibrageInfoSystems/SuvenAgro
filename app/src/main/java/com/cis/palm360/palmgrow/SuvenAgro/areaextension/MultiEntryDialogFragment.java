package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Config;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.LinkedHashMap;


//Used for Multiple Entry
public class MultiEntryDialogFragment extends DialogFragment {

    public static final int INTER_CROP_TYPE = 2;
    private Spinner currentCropSpinner;
    private EditText areaEdit;
    private Spinner anualIncomeSpinner;
    private Button saveBtn;
    private Button cancelBtn;
    private View promptView = null;

    private TextView neighbourPlotTxt;
    private Spinner neighbouringCropSpinner,recommendationCropSpn;
    private RadioGroup yesOrNoGroup;

    private Spinner varietySpnr;
    private EditText areaallocatedEdt, treesCountEdt;
    private TextView mutltiareaallocated_tv;
    private LinkedHashMap cropsMap;
    private DataAccessHandler dataAccessHandler;
    private int type;
    private Bundle dataBundle;
    private int neighbourPlotCount;

    public void setOnDataSelectedListener(MultiEntryDialogFragment.onDataSelectedListener onDataSelectedListener) {
        this.onDataSelectedListener = onDataSelectedListener;
    }

    public onDataSelectedListener onDataSelectedListener;


    public MultiEntryDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle inputBundle = getArguments();
        getDialog().setCanceledOnTouchOutside(false);
        if (null == inputBundle)
            return null;

        dataAccessHandler = new DataAccessHandler(getActivity());
        dataBundle = new Bundle();

        type = inputBundle.getInt("type", 0);
        if (type == 0) {
            promptView = inflater.inflate(R.layout.multientry_dialog, null);
            initView();
        } else if (type == 100) {
            promptView = inflater.inflate(R.layout.multiplevariety_dialog, null);
            initViewVarietyCrop();
        } else if (type == 1) {
            promptView = inflater.inflate(R.layout.neighbour_plot_dialog, null);
            neighbourPlotCount = inputBundle.getInt("neighbourPlotCount", 0);
            initViewNeighbourCrop("Neighbour Field");
        } else if (type == 2) {
            promptView = inflater.inflate(R.layout.intercrop_entry_dialog, null);
            neighbourPlotCount = inputBundle.getInt("neighbourPlotCount", 0);
            initViewInterCrop("Inter Crop");
        }

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        promptView.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        saveBtn = (Button) promptView.findViewById(R.id.saveBtn);
        cancelBtn = (Button) promptView.findViewById(R.id.cancelBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0 && validateCurrentCropInfo()) {
                    String cropId = CommonUtils.getKeyFromValue(cropsMap, currentCropSpinner.getSelectedItem().toString());
                    dataBundle.putString("cropName", currentCropSpinner.getSelectedItem().toString());
                    dataBundle.putString("areaAllocated", areaEdit.getText().toString());
                    dataBundle.putString("cropId", cropId);
                    onDataSelectedListener.onDataSelected(type, dataBundle);
                    dismiss();
                } else if (type == INTER_CROP_TYPE) {
                    if(validateInterCropInfo()) {
                        String cropId = CommonUtils.getKeyFromValue(cropsMap, neighbouringCropSpinner.getSelectedItem().toString());
                        String recId = CommonUtils.getKeyFromValue(cropsMap, recommendationCropSpn.getSelectedItem().toString());
                        dataBundle.putString("cropName", neighbouringCropSpinner.getSelectedItem().toString());
                        dataBundle.putString("recName",recommendationCropSpn.getSelectedItem().toString());

                        dataBundle.putString("cropId", cropId);
                        dataBundle.putString("recId",recId);
                        onDataSelectedListener.onDataSelected(type, dataBundle);
                        dismiss();
                    }
                }
                else if (type == PlotDetailsFragment.NEIGHBOUR_PLOT_TYPE) {
                    if(validateNeighbourPlotInfo()) {
                        String cropId = CommonUtils.getKeyFromValue(cropsMap, neighbouringCropSpinner.getSelectedItem().toString());
                        dataBundle.putString("cropName", neighbouringCropSpinner.getSelectedItem().toString());
                        dataBundle.putString("neightbourPlot", neighbourPlotTxt.getText().toString());
                        dataBundle.putString("cropId", cropId);
                        onDataSelectedListener.onDataSelected(type, dataBundle);
                        dismiss();
                    }
                }else if (validateCurrentCropInfo()) {
                    onDataSelectedListener.onDataSelected(type, dataBundle);
                    dismiss();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return promptView;
    }


    private void initView() {
        currentCropSpinner = (Spinner) promptView.findViewById(R.id.currentCropSpinner);
        cropsMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(cropsMap, "Crop"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentCropSpinner.setAdapter(spinnerArrayAdapter);
        areaEdit = (EditText) promptView.findViewById(R.id.areaEdit);
        anualIncomeSpinner = (Spinner) promptView.findViewById(R.id.anualIncomeSpinner);
    }

    private void initViewNeighbourCrop(String title) {
        neighbouringCropSpinner = promptView.findViewById(R.id.neighbouringCropSpinner);
        cropsMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(cropsMap, "Crop"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neighbouringCropSpinner.setAdapter(spinnerArrayAdapter);
        neighbourPlotTxt = (TextView) promptView.findViewById(R.id.neighbouringPlotTypeTxt);
//        neighbourPlotTxt.setText(title + " " + (neighbourPlotCount + 1));
        neighbourPlotTxt.setText(title);
    }

    private void initViewInterCrop(String title) {
        neighbouringCropSpinner = promptView.findViewById(R.id.neighbouringCropSpinner);
        recommendationCropSpn = promptView.findViewById(R.id.recommendationCropSpn);
        cropsMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CommonUtils.fromMap(cropsMap, "Crop"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neighbouringCropSpinner.setAdapter(spinnerArrayAdapter);
        recommendationCropSpn.setAdapter(spinnerArrayAdapter);
        neighbourPlotTxt = (TextView) promptView.findViewById(R.id.neighbouringPlotTypeTxt);
        neighbourPlotCount++;
        neighbourPlotTxt.setText(title + " " + neighbourPlotCount);
    }
    private void initViewVarietyCrop() {
        cropsMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
        varietySpnr = (Spinner) promptView.findViewById(R.id.varietySpinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(cropsMap, "Crop"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        varietySpnr.setAdapter(spinnerArrayAdapter);
        areaallocatedEdt = (EditText) promptView.findViewById(R.id.area_allocatedEdt);
        mutltiareaallocated_tv = (TextView) promptView.findViewById(R.id.mutltiareaallocated_tv);
        mutltiareaallocated_tv.setText(getString(R.string.area_allocated, Config.UOM));
        treesCountEdt = (EditText) promptView.findViewById(R.id.treescountEdt);
    }

    public interface onDataSelectedListener {
        void onDataSelected(int type, Bundle bundle);
    }

    public boolean validateCurrentCropInfo() {
        boolean isValid = true;
        if (CommonUtils.isEmptySpinner(currentCropSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Crop", getActivity(), 1);
            isValid = false;
        } else if (TextUtils.isEmpty(areaEdit.getText().toString())) {
            UiUtils.showCustomToastMessage("Please Enter Area Allocated for Current Crop", getActivity(), 1);
            isValid = false;
        }else {
            String areaStr = areaEdit.getText().toString().trim();
            double areaValue = Double.parseDouble(areaStr);

            // Regex: valid number format (integer or decimal, but not starting with .)
            if (areaStr.startsWith(".")) {
                UiUtils.showCustomToastMessage("Please Enter a Valid Area", getActivity(), 1);
                isValid = false;
            }else if (areaValue <= 0){
                UiUtils.showCustomToastMessage("Area should be greater than 0", getActivity(), 1);
                isValid = false;
            }
        }

        return isValid;
    }

    public boolean validateInterCropInfo() {
        boolean isValid = true;
        if (CommonUtils.isEmptySpinner(neighbouringCropSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Crop", getActivity(), 1);
            isValid = false;
        }else if (CommonUtils.isEmptySpinner(recommendationCropSpn)) {
            UiUtils.showCustomToastMessage("Please Select Recommend Crop", getActivity(), 1);
            isValid = false;
        }
        return isValid;
    }

    public boolean validateNeighbourPlotInfo() {
        boolean isValid = true;
        if (CommonUtils.isEmptySpinner(neighbouringCropSpinner)) {
            UiUtils.showCustomToastMessage("Please Select Crop", getActivity(), 1);
            isValid = false;
        }
        return isValid;
    }
}

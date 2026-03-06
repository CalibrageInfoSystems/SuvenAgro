package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.LinkedHashMap;

/**
 * Created by siva on 06/05/17.
 */


//Edit Dialog
public class EditEntryDialogFragment extends DialogFragment {

    public static final String LOG_TAG = EditEntryDialogFragment.class.getName();

    private View rootView;
    public TextView itemDisplayNameTxt, title2Txt, title1Txt;
    public EditText inputEditBox, inputBox2;
    public Spinner inputSpinner,recommendationCropSpn;
    private int typeDialog;

    public static final int TYPE_EDIT_BOX = 0;
    public static final int TYPE_SPINNER = 1;
    public static final int EDIT_INTER_CROP = 4;
    public static final int TYPE_MULTI_EDIT_BOX = 2;
    public static final int TYPE_MULTI_EDIT_BOX2 = 6;
    public static final int TYPE_EDIT_BOX2 = 7;
    public static final int TYPE_SPINNER_IRIGATION_TYPE = 3;
    private String prevData;
    private android.widget.Button saveBtn;
    private android.widget.Button cancelBtn;
    private DataAccessHandler dataAccessHandler;
    private LinkedHashMap dataMap;
    private LinearLayout inputTypeLayout1, inputTypeLayout2;


    public void setOnDataEditChangeListener(OnDataEditChangeListener onDataEditChangeListener) {
        this.onDataEditChangeListener = onDataEditChangeListener;
    }

    private OnDataEditChangeListener onDataEditChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle inputBundle = getArguments();
        typeDialog = inputBundle.getInt("typeDialog");

        if(typeDialog == EDIT_INTER_CROP)
        {
            rootView = inflater.inflate(R.layout.edit_inter_crop, null);
        }
        else {
            rootView = inflater.inflate(R.layout.edit_entry_dialog, null);
        }

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        rootView.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        dataAccessHandler = new DataAccessHandler(getActivity());

        itemDisplayNameTxt = (TextView) rootView.findViewById(R.id.selectedItemTxt);
        inputEditBox = (EditText) rootView.findViewById(R.id.inputBox);
        inputSpinner = (Spinner) rootView.findViewById(R.id.inputSpin);
        saveBtn = (Button) rootView.findViewById(R.id.saveBtn);
        cancelBtn = (Button) rootView.findViewById(R.id.cancelBtn);
        recommendationCropSpn = rootView.findViewById(R.id.recommendationCropSpn);

        inputTypeLayout1 = (LinearLayout) rootView.findViewById(R.id.inputTypeLayout1);
        inputTypeLayout2 = (LinearLayout) rootView.findViewById(R.id.inputTypeLayout2);
        title1Txt = (TextView) rootView.findViewById(R.id.title1);
        title2Txt = (TextView) rootView.findViewById(R.id.title2);
        inputBox2 = (EditText) rootView.findViewById(R.id.inputBox2);



        prevData = inputBundle.getString("prevData");

        String[] dataArr = prevData.split("-");
        itemDisplayNameTxt.setText(""+inputBundle.getString("title"));
        if (typeDialog == TYPE_EDIT_BOX) {
            inputEditBox.setVisibility(View.VISIBLE);
            inputSpinner.setVisibility(View.GONE);
            inputEditBox.setText(""+dataArr[0]);
            inputTypeLayout1.setVisibility(View.VISIBLE);
            title1Txt.setText(dataArr[1]);
        }
        if (typeDialog == TYPE_EDIT_BOX2) {
            int maxLength = 2;
            InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(maxLength)};
            inputEditBox.setFilters(filters);
            inputEditBox.setVisibility(View.VISIBLE);
            inputSpinner.setVisibility(View.GONE);
            inputEditBox.setText(""+dataArr[0]);
            inputTypeLayout1.setVisibility(View.VISIBLE);
            title1Txt.setText(dataArr[1]);}
            else if (typeDialog == EDIT_INTER_CROP) {
            itemDisplayNameTxt.setText("Inter Crop");
            dataMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    CommonUtils.fromMap(dataMap, "Crop"));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            inputEditBox.setVisibility(View.GONE);
            inputSpinner.setVisibility(View.VISIBLE);
            inputSpinner.setAdapter(spinnerArrayAdapter);
            recommendationCropSpn.setAdapter(spinnerArrayAdapter);
            int cropPos = CommonUtils.getIndex(dataMap.keySet(), CommonUtils.getKeyFromValue(dataMap, dataArr[0]));
            inputSpinner.setSelection((cropPos > -1) ? cropPos + 1 : 0);
        }else if (typeDialog == TYPE_SPINNER_IRIGATION_TYPE) {
            title1Txt.setText(dataArr[1]);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                    getResources().getStringArray(R.array.irigationType_values));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            inputEditBox.setVisibility(View.GONE);
            inputSpinner.setVisibility(View.VISIBLE);
            inputSpinner.setAdapter(spinnerArrayAdapter);
        } else if (typeDialog == TYPE_MULTI_EDIT_BOX) {

            String prevData2 = inputBundle.getString("prevData2");
            String[] dataArr2 = prevData2.split("-");
            title1Txt.setText(""+dataArr[0]);
            title2Txt.setText(""+dataArr2[0]);
            inputEditBox.setText(""+dataArr[1]);
            inputBox2.setText(""+dataArr2[1]);
            inputTypeLayout1.setVisibility(View.VISIBLE);
            inputTypeLayout2.setVisibility(View.VISIBLE);
            inputSpinner.setVisibility(View.GONE);
            inputEditBox.setVisibility(View.VISIBLE);
            inputBox2.setVisibility(View.VISIBLE);
        }
        else if (typeDialog == TYPE_MULTI_EDIT_BOX2) {
            int maxLength = 3;
            int inputBoxMaxLength = 5;
            inputEditBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            inputBox2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputBoxMaxLength)});

            String prevData2 = inputBundle.getString("prevData2");
            String[] dataArr2 = prevData2.split("-");
            title1Txt.setText(""+dataArr[0]);
            title2Txt.setText(""+dataArr2[0]);
            inputEditBox.setText(""+dataArr[1]);
            inputBox2.setText(""+dataArr2[1]);
            inputTypeLayout1.setVisibility(View.VISIBLE);
            inputTypeLayout2.setVisibility(View.VISIBLE);
            inputSpinner.setVisibility(View.GONE);
            inputEditBox.setVisibility(View.VISIBLE);
            inputBox2.setVisibility(View.VISIBLE);
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                if (!validateEditUi()) {
                    return; // Stop saving if validation fails
                }
                if (typeDialog == TYPE_EDIT_BOX) {
                    if (!TextUtils.isEmpty(inputEditBox.getText().toString())) {
                        dataBundle.putString("inputValue", inputEditBox.getText().toString());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    } else {
                        UiUtils.showCustomToastMessage("Please Enter Proper Data", getActivity(), 1);
                    }
                } else if (typeDialog == EDIT_INTER_CROP) {
                    if (!CommonUtils.isEmptySpinner(inputSpinner) && !CommonUtils.isEmptySpinner(recommendationCropSpn)) {
                        dataBundle.putString("inputValue", inputSpinner.getSelectedItem().toString());
                        dataBundle.putString("recValue", recommendationCropSpn.getSelectedItem().toString());
                        dataBundle.putInt("cropId",inputSpinner.getSelectedItemPosition());
                        dataBundle.putInt("recId",recommendationCropSpn.getSelectedItemPosition());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    } else {
                        UiUtils.showCustomToastMessage("Please Select Proper Data", getActivity(), 1);
                    }
                } else if (typeDialog == TYPE_SPINNER_IRIGATION_TYPE) {
                    if (!CommonUtils.isEmptySpinner(inputSpinner)) {
                        dataBundle.putString("inputValue", inputSpinner.getSelectedItem().toString());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    } else {
                        UiUtils.showCustomToastMessage("Please Select Proper Data", getActivity(), 1);
                    }
                }
                if (typeDialog == TYPE_MULTI_EDIT_BOX) {
                    if (!TextUtils.isEmpty(inputEditBox.getText().toString()) && !TextUtils.isEmpty(inputBox2.getText().toString())) {
                        dataBundle.putString("inputValue", inputEditBox.getText().toString());
                        dataBundle.putString("inputValue2", inputBox2.getText().toString());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    } else {
                        UiUtils.showCustomToastMessage("Please Select Proper Data", getActivity(), 1);
                    }
                }
                if (typeDialog == TYPE_MULTI_EDIT_BOX2) {
                    if (!TextUtils.isEmpty(inputEditBox.getText().toString()) && !TextUtils.isEmpty(inputBox2.getText().toString())) {
                        dataBundle.putString("inputValue", inputEditBox.getText().toString());
                        dataBundle.putString("inputValue2", inputBox2.getText().toString());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    } else {
                        UiUtils.showCustomToastMessage("Please Select Proper Data", getActivity(), 1);
                    }
                }
                if (typeDialog == TYPE_EDIT_BOX2) {
                    if (!TextUtils.isEmpty(inputEditBox.getText().toString())) {
                        dataBundle.putString("inputValue", inputEditBox.getText().toString());
                        onDataEditChangeListener.onDataEdited(dataBundle);
                        dismiss();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    private boolean validateEditUi() {
        // For Bore well / Open well validation
        if (typeDialog == TYPE_MULTI_EDIT_BOX2) {
            if (TextUtils.isEmpty(inputEditBox.getText().toString())) {
                UiUtils.showCustomToastMessage(getResources().getString(R.string.error_waternumber), getActivity(), 1);
                inputEditBox.requestFocus();
                return false;
            }
            if (inputEditBox.isShown()) {
                String value = inputEditBox.getText().toString().trim();
                try {
                    double number = Double.parseDouble(value);
                    if (number <= 0) {
                        UiUtils.showCustomToastMessage("Please enter a valid number", getActivity(), 1);
                        inputEditBox.requestFocus();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    UiUtils.showCustomToastMessage("Please enter a valid number", getActivity(), 1);
                    inputEditBox.requestFocus();
                    return false;
                }
            }

            if (TextUtils.isEmpty(inputBox2.getText().toString())) {
                UiUtils.showCustomToastMessage(getResources().getString(R.string.error_waterdischargecapacity), getActivity(), 1);
                inputBox2.requestFocus();
                return false;
            }

            String input = inputBox2.getText().toString().trim();

            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    UiUtils.showCustomToastMessage("Please Enter Valid Capacity (Lt/Hr)", getActivity(), 1);
                    inputBox2.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Please enter a valid number", getActivity(), 1);
                inputBox2.requestFocus();
                return false;
            }

        }

        // For Canal water validation
        if (typeDialog == TYPE_EDIT_BOX2) {
            if (TextUtils.isEmpty(inputEditBox.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter " + getResources().getString(R.string.wateravailabilityofcanall), getActivity(), 1);
                inputEditBox.requestFocus();
                return false;
            }
            try {
                int months = Integer.parseInt(inputEditBox.getText().toString());
                if (months <= 0 || months > 12) {
                    UiUtils.showCustomToastMessage("Please Enter Months Between 1 and 12", getActivity(), 1);
                    inputEditBox.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                UiUtils.showCustomToastMessage("Please Enter Valid Number", getActivity(), 1);
                return false;
            }
        }

        return true;
    }

    public interface OnDataEditChangeListener {
        void onDataEdited(final Bundle dataBundle);
    }
}

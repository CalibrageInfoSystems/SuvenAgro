package com.cis.palm360.palmgrow.SuvenAgro.dispatch;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.SaplingDispatchRequest;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.Calendar;

public class DispatchRequestActivity extends AppCompatActivity {
    String[]  receiptNumber ={"Select Advance Receipt Number","receiptNo1","receiptNo2"};
    EditText date_of_lifting , dispatch_sapling_count;
    Spinner receipt_number;
    SaplingDispatchRequest model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispatch_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView receiptLabel = findViewById(R.id.advance_receipt_number);
        TextView liftingDateLabel = findViewById(R.id.expected_date);
        TextView dispatchSaplingLabel = findViewById(R.id.dispatch_count);
        Button save = findViewById(R.id.save_btn);

        date_of_lifting = findViewById(R.id.expected_lifting_date);
        dispatch_sapling_count = findViewById(R.id.dispatch_sapling_count);
        receipt_number = findViewById(R.id.spinner_advance_receipt_number);

        setRedStarLabel(receiptLabel,"Advance Receipt Number ");
        setRedStarLabel(liftingDateLabel,"Expected Date of Lifting ");
        setRedStarLabel(dispatchSaplingLabel,"Dispatch Sapling Count ");

        ArrayAdapter<String> receiptAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                receiptNumber
        );
        receiptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receipt_number.setAdapter(receiptAdapter);

        model = (SaplingDispatchRequest) getIntent().getSerializableExtra(CommonConstants.dispatchModel);

        if (model != null) {
//            advance_receipt_number.setText(model.getReceiptNumber());
//            advance_receipt_number.setFocusable(false);
        }


        date_of_lifting.setOnClickListener(v->{
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(DispatchRequestActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        date_of_lifting.setText(date);
                        date_of_lifting.setError(null);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
        save.setOnClickListener(v->{
            String receipt = receipt_number.getSelectedItem().toString();
            String lifting_date = date_of_lifting.getText().toString().trim();
            String dispatch_count = dispatch_sapling_count.getText().toString().trim();

            if (receipt.equals("Select Advance Receipt Number")){
                UiUtils.showCustomToastMessage("Please select advance receipt number", this, 1);

                receipt_number.getParent().requestChildFocus(receipt_number,receipt_number);
                return;
            }
            if (lifting_date.isEmpty()){
                date_of_lifting.setError("Expected Date of Lifting is required");
                date_of_lifting.getParent().requestChildFocus(date_of_lifting,date_of_lifting);
                return;
            }
            if (dispatch_count.isEmpty()){
                dispatch_sapling_count.setError("Dispatch Sapling Count is Required");
                dispatch_sapling_count.getParent().requestChildFocus(dispatch_sapling_count,dispatch_sapling_count);
                return;
            }
        });
    }
    private void setRedStarLabel(TextView textView, String labelText) {
        SpannableStringBuilder builder = new SpannableStringBuilder(labelText);
        SpannableString redStar = new SpannableString("*");
        redStar.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(redStar);
        textView.setText(builder);
    }
}


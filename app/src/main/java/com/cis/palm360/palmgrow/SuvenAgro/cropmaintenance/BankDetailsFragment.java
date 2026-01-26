package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmerBank;


//Displaying Farmer Bank Details
public class BankDetailsFragment extends Fragment {

    private TextView bank_name_txt, branch_name_text, ifsc_code_text, account_holder_name_text, account_number_text;
    private View rootView;
    private Context mContext;
    private DataAccessHandler dataAccessHandler;
    private UpdateUiListener updateUiListener;
    private FarmerBank farmerBank = null;
    private ActionBar actionBar;
    private Toolbar toolbar;

    public BankDetailsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bank_details, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Bank Details");
        initViews();
        return rootView;
    }

    private void initViews() {
        dataAccessHandler = new DataAccessHandler(getActivity());
        bank_name_txt = (TextView) rootView.findViewById(R.id.bank_name_txt);
        branch_name_text = (TextView) rootView.findViewById(R.id.branch_name_text);
        ifsc_code_text = (TextView) rootView.findViewById(R.id.ifsc_code_text);
        account_holder_name_text = (TextView) rootView.findViewById(R.id.account_holder_name_text);
        account_number_text = (TextView) rootView.findViewById(R.id.account_number_text);

        farmerBank = (FarmerBank) DataManager.getInstance().getDataFromManager(DataManager.FARMER_BANK_DETAILS);
        if(farmerBank==null) {
            farmerBank = (FarmerBank) dataAccessHandler.getSelectedFarmerBankData(Queries.getInstance().getFarmerBankData(CommonConstants.FARMER_CODE), 0);
        }
        if (null != farmerBank) {
            bindData();
        }
    }

    private void bindData() {

        CommonConstants.bankTypeId = String.valueOf(farmerBank.getBankid());
        String banktypecddmtId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTypecdDmtIdBank(CommonConstants.bankTypeId));
        String bankName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTypecdDesc(banktypecddmtId));
        String branchName = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getBranchName(CommonConstants.bankTypeId));

        bank_name_txt.setText(bankName);
        branch_name_text.setText(branchName);
        account_holder_name_text.setText(farmerBank.getAccountholdername());
        account_number_text.setText(farmerBank.getAccountnumber());
        ifsc_code_text.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getIfscCode(String.valueOf(farmerBank.getBankid()))));
    }
}

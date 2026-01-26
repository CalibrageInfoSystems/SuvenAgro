package com.cis.palm360.palmgrow.SuvenAgro.areaextension;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Referrals;

import java.util.ArrayList;
import java.util.List;


//Add Referral Fragment
public class ReferralsFragment extends Fragment implements ReferralsDialogFragment.AddReferralInfoListener, ReferralsAdapter.OnCartChangedListener {


    private RecyclerView referralsRecyclerView;
    private RelativeLayout addreferral, addreferralBottomView;
    private View rootView;
    private ActionBar actionBar;
    private ReferralsDialogFragment referralsDialogFragment;
    private List<Referrals> referralDataModels;
    private Context mContext;
    private LinearLayout headerLL;
    private View headerView;
    private Button referralsSaveBtn;
    private UpdateUiListener updateUiListener;
    public static final int REQUEST_UPDATE_REFERRALS_DETAILS = 1;
    ReferralsAdapter referralsAdapter;

    public ReferralsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_referrals, container, false);
        mContext = getActivity();
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.referrals);
        this.addreferral = (RelativeLayout) rootView.findViewById(R.id.add_referral);
        this.referralsRecyclerView = (RecyclerView) rootView.findViewById(R.id.referralsRecyclerView);
        referralDataModels = new ArrayList<>();
        referralDataModels = (List<Referrals>) DataManager.getInstance().getDataFromManager(DataManager.REFERRALS_DATA);

        addreferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReferralDialog(null);
            }
        });
        headerLL = (LinearLayout) rootView.findViewById(R.id.headerLL);
        headerView = rootView.findViewById(R.id.divider);

        addreferralBottomView = (RelativeLayout) rootView.findViewById(R.id.add_referral_bottom);
        addreferralBottomView.setVisibility(View.GONE);

        referralsSaveBtn = (Button) rootView.findViewById(R.id.saveBtn);

        addreferralBottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReferralDialog(null);
            }
        });

        referralsSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().addData(DataManager.REFERRALS_DATA, referralDataModels);
                updateUiListener.updateUserInterface(REQUEST_UPDATE_REFERRALS_DETAILS);
                CommonConstants.Flags.isReferralsUpdated = true;
                getFragmentManager().popBackStack();
            }
        });

        referralsAdapter = new ReferralsAdapter(mContext, referralDataModels);
        referralsAdapter.setOnCartChangedListener(this);
        referralsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        referralsRecyclerView.setHasFixedSize(true);
        referralsRecyclerView.setAdapter(referralsAdapter);

        if (null != referralDataModels && !referralDataModels.isEmpty()) {
            setReferralAdapter();
        }
        return rootView;
    }

    private void openReferralDialog(Referrals dataModel) {
        referralsDialogFragment = new ReferralsDialogFragment();
        referralsDialogFragment.setAddReferralListener(ReferralsFragment.this);
        String backStateName = referralsDialogFragment.getClass().getName();
        if (dataModel != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CommonConstants.REFERRAL_DATA_MODEL, dataModel);
            referralsDialogFragment.setArguments(bundle);
        }
        FragmentManager mFragmentManager = getChildFragmentManager();
        referralsDialogFragment.show(mFragmentManager, "fragment_edit_name");
    }

    @Override
    public void addReferralInfo(Referrals referralDataModel) {
        if (null == referralDataModels) {
            referralDataModels = new ArrayList<>();
        }
        referralDataModels.add(referralDataModel);
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
        logoMoveAnimation.setFillAfter(true);
        logoMoveAnimation.setFillEnabled(true);
        addreferral.startAnimation(logoMoveAnimation);
        logoMoveAnimation.setAnimationListener(animationInListener);
        referralsRecyclerView.setVisibility(View.VISIBLE);
        headerLL.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
        referralsAdapter.updateData(referralDataModels);
        referralsSaveBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void editReferralInfo(Referrals referralDataModel) {
        for (int i = 0; i < referralDataModels.size(); i++) {
            Referrals dataModel = referralDataModels.get(i);
            if (TextUtils.equals(referralDataModel.getVillageName(), dataModel.getVillageName())) {
                referralDataModels.remove(i);
                referralDataModels.add(i, referralDataModel);
                break;
            }
        }
        setReferralAdapter();
    }

    private void setReferralAdapter() {
        referralsSaveBtn.setVisibility(View.VISIBLE);
        referralsRecyclerView.setVisibility(View.VISIBLE);
        headerLL.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
//        addreferral.setVisibility(View.GONE);
//        addreferral.setOnClickListener(null);
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
        logoMoveAnimation.setFillAfter(true);
        logoMoveAnimation.setFillEnabled(true);
        addreferral.startAnimation(logoMoveAnimation);
        logoMoveAnimation.setAnimationListener(animationInListener);
        referralsRecyclerView.setVisibility(View.VISIBLE);
        headerLL.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.VISIBLE);
        referralsAdapter.updateData(referralDataModels);
        referralsSaveBtn.setVisibility(View.VISIBLE);
    }

    Animation.AnimationListener animationInListener
            = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            addreferralBottomView.setVisibility(View.VISIBLE);
            addreferral.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    };

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    /*@Override
    public void setCartClickListener(String status, int position) {
        if (status.equalsIgnoreCase("delete")) {
            referralDataModels.remove(position);
            referralsAdapter.notifyDataSetChanged();
        } else {
            openReferralDialog(referralDataModels.get(position));
        }
    }*/
    @Override
    public void setCartClickListener(String status, int position) {
        if (status.equalsIgnoreCase("delete")) {

            if (position >= 0 && position < referralDataModels.size()) {
                referralDataModels.remove(position);
                referralsAdapter.notifyItemRemoved(position);
                referralsAdapter.notifyItemRangeChanged(position, referralDataModels.size());

                // 👇 Hide Save button if no items left
                if (referralDataModels.isEmpty()) {
                    referralsSaveBtn.setVisibility(View.GONE);
                    referralsRecyclerView.setVisibility(View.GONE);
                    headerLL.setVisibility(View.GONE);
                    headerView.setVisibility(View.GONE);
                }
                DataManager.getInstance().addData(DataManager.REFERRALS_DATA, referralDataModels);
                updateUiListener.updateUserInterface(REQUEST_UPDATE_REFERRALS_DETAILS);
                CommonConstants.Flags.isReferralsUpdated = true;

            }
        } else {
            openReferralDialog(referralDataModels.get(position));
        }
    }

}

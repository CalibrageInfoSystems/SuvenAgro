package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotFFBDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotGradingDetails;
import com.cis.palm360.palmgrow.SuvenAgro.prospectiveFarmers.ProspectivePlotsAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.prospectiveFarmers.ProspectivePlotsModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Used to display the screnshot of Cropmaintenance
public class SnapShotFragment extends Fragment {

    private TextView lastVisitedDate_text, prospectivePlots_text;
    private LinearLayout ffb_ll,grading_ll,ffbdetails_ll,gradingdetails_ll, norecrodsfound_ll, nogradingrecrodsfound_ll, gradingdetailss_ll,
            ffbdetailss_ll;
    private TextView yieldPerHectare_text, expectedYieldPerHectare_text,ripen_text,underRipe_text,unRipen_text,overRipe_text,diseased_text,emptyBunches_text;

    ImageView image_downg,image_upg;
    ImageView image_downf,image_upf;

    private View rootView;
    private RecyclerView rvplotlist;
    private Farmer selectedFarmer;
    private DataAccessHandler dataAccessHandler = null;
    private List<ProspectivePlotsModel> plotDetailsObjArrayList = new ArrayList<>();
    private com.cis.palm360.palmgrow.SuvenAgro.prospectiveFarmers.ProspectivePlotsAdapter prospectivePlotsAdapter;

    private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private ActionBar actionBar;


    private List<PlotFFBDetails> plotffbDetailsObjArrayList = new ArrayList<>();
    private List<PlotGradingDetails> plotgradingDetailsObjArrayList = new ArrayList<>();

//    public SnapShotFragment() {
//
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_snap_shot, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.snap_shot_1));

        rvplotlist = (RecyclerView) rootView.findViewById(R.id.lv_farmerplotdetails);
        initViews();

        selectedFarmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);

        bindData();

        return rootView;
    }


    private void initViews() {
        lastVisitedDate_text = (TextView) rootView.findViewById(R.id.lastVisitedDate_text);
        prospectivePlots_text = (TextView) rootView.findViewById(R.id.prospectivePlots_text);

        yieldPerHectare_text = (TextView) rootView.findViewById(R.id.yieldPerHectare_text);
        expectedYieldPerHectare_text = (TextView) rootView.findViewById(R.id.expectedYieldPerHectare_text);
        ripen_text = (TextView) rootView.findViewById(R.id.ripen_text);
        underRipe_text = (TextView) rootView.findViewById(R.id.underRipe_text);
        unRipen_text = (TextView) rootView.findViewById(R.id.unRipen_text);
        overRipe_text = (TextView) rootView.findViewById(R.id.overRipe_text);
        diseased_text = (TextView) rootView.findViewById(R.id.diseased_text);
        emptyBunches_text = (TextView) rootView.findViewById(R.id.emptyBunches_text);

        ffbdetails_ll = (LinearLayout) rootView.findViewById(R.id.ffbdetails_ll);
        gradingdetails_ll = (LinearLayout) rootView.findViewById(R.id.gradingdetails_ll);

        ffb_ll = (LinearLayout) rootView.findViewById(R.id.ffb_ll);
        grading_ll = (LinearLayout) rootView.findViewById(R.id.grading_ll);
        norecrodsfound_ll = (LinearLayout) rootView.findViewById(R.id.norecrodsfound_ll);
        nogradingrecrodsfound_ll = (LinearLayout) rootView.findViewById(R.id.nogradingrecrodsfound_ll);
        gradingdetailss_ll =  (LinearLayout) rootView.findViewById(R.id.gradingdetailss_ll);
        ffbdetailss_ll = (LinearLayout) rootView.findViewById(R.id.ffbdetailss_ll);


        image_downf = (ImageView) rootView.findViewById(R.id.image_downf);
        image_upf = (ImageView) rootView.findViewById(R.id.image_upf);

        image_downg = (ImageView) rootView.findViewById(R.id.image_downg);
        image_upg = (ImageView) rootView.findViewById(R.id.image_upg);

        image_downf.setOnClickListener(new View.OnClickListener() {    // basic Information
            @Override
            public void onClick(View view) {

                image_downf.setVisibility(View.GONE);
                image_upf.setVisibility(View.VISIBLE);
                ffbdetails_ll.setVisibility(View.VISIBLE);

            }
        });

        image_upf.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downf.setVisibility(View.VISIBLE);
                image_upf.setVisibility(View.GONE);
                ffbdetails_ll.setVisibility(View.GONE);


            }
        });

        image_upg.setOnClickListener(new View.OnClickListener() {    // basic Information
            @Override
            public void onClick(View view) {

                image_downg.setVisibility(View.VISIBLE);
                image_upg.setVisibility(View.GONE);
                gradingdetails_ll.setVisibility(View.GONE);

            }
        });

        image_downg.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downg.setVisibility(View.GONE);
                image_upg.setVisibility(View.VISIBLE);
                gradingdetails_ll.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindData() {
        dataAccessHandler = new DataAccessHandler(getActivity());
        plotDetailsObjArrayList = dataAccessHandler.getProspectivePlotDetails(selectedFarmer.getCode(), 81);
        if (plotDetailsObjArrayList != null && plotDetailsObjArrayList.size() > 0) {
            prospectivePlotsAdapter = new ProspectivePlotsAdapter(getActivity(), plotDetailsObjArrayList);
            rvplotlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(prospectivePlotsAdapter);
        }

        Log.d("PLOT_CODE",CommonConstants.PLOT_CODE + "");

        String lastVisitDate = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryCropLastVisitDate());



        if (!TextUtils.isEmpty(lastVisitDate)) {

            lastVisitDate = lastVisitDate.replace("T"," ");
            Date date = null;
            try {
                date = inputFormat.parse(lastVisitDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String outputDate = outputFormat.format(date);
            lastVisitedDate_text.setText(outputDate);
        } else {
            lastVisitedDate_text.setText(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_2));
        }

        plotffbDetailsObjArrayList = dataAccessHandler.getPlotffbDetails(Queries.getInstance().getplotffbdetails(CommonConstants.PLOT_CODE));
        plotgradingDetailsObjArrayList = dataAccessHandler.getPlotGradingDetails(Queries.getInstance().getplotgradingdetails(CommonConstants.PLOT_CODE));


        if (plotffbDetailsObjArrayList.size() >0) {

            ffbdetailss_ll.setVisibility(View.VISIBLE);
            norecrodsfound_ll.setVisibility(View.GONE);
            yieldPerHectare_text.setText(plotffbDetailsObjArrayList.get(0).getYieldPerHectare() + "");
            expectedYieldPerHectare_text.setText(plotffbDetailsObjArrayList.get(0).getExpectedYieldPerHectare() + "");
        }else{

            norecrodsfound_ll.setVisibility(View.VISIBLE);
            ffbdetailss_ll.setVisibility(View.GONE);

        }

        if (plotgradingDetailsObjArrayList.size() >0) {

            gradingdetailss_ll.setVisibility(View.VISIBLE);
            nogradingrecrodsfound_ll.setVisibility(View.GONE);
            ripen_text.setText(plotgradingDetailsObjArrayList.get(0).getRipen() + " %");
            underRipe_text.setText(plotgradingDetailsObjArrayList.get(0).getUnderRipe() + " %");
            unRipen_text.setText(plotgradingDetailsObjArrayList.get(0).getUnRipen() + " %");
            overRipe_text.setText(plotgradingDetailsObjArrayList.get(0).getOverRipe() + " %");
            diseased_text.setText(plotgradingDetailsObjArrayList.get(0).getDiseased() + " %");
            emptyBunches_text.setText(plotgradingDetailsObjArrayList.get(0).getEmptyBunches() + " %");

        }else {
            nogradingrecrodsfound_ll.setVisibility(View.VISIBLE);
            gradingdetailss_ll.setVisibility(View.GONE);

        }
    }

}

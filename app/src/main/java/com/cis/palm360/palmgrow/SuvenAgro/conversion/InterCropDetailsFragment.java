package com.cis.palm360.palmgrow.SuvenAgro.conversion;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.EditEntryDialogFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.GenericListItemClickListener;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.MultiEntryDialogFragment;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.SingleItemAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.InterCropPlantationXref;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


//Used to add Intercrop details
public class InterCropDetailsFragment extends Fragment implements View.OnClickListener, MultiEntryDialogFragment.onDataSelectedListener, GenericListItemClickListener, EditEntryDialogFragment.OnDataEditChangeListener {
    public List<CropModel> interCropList = new ArrayList<>();
    private View rootView;
    private RelativeLayout addCrop, addCropBottomView;
    Animation.AnimationListener animationInListener
            = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            addCropBottomView.setVisibility(View.VISIBLE);
            addCrop.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    };
    private RecyclerView interCropRecyclerView;
    private Button saveBtn,historyBtn;
    private String LOG_TAG = InterCropDetailsFragment.class.getName();
    private UpdateUiListener updateUiListener;
    private List<Pair> interCropPair = new ArrayList<>();
    private SingleItemAdapter interCropAdapter;
    private int selectedPosition;
    private LinearLayout headerLL;
    private LinkedHashMap<String, String> cropDataMap;
    private DataAccessHandler dataAccessHandler;
    Toolbar toolbar;
    private ActionBar actionBar;

    private ArrayList<InterCropPlantationXref> intercroplastvisitdatamap;

    public InterCropDetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inter_crop_details, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getActivity().getResources().getString(R.string.interCropDetailss));
        dataAccessHandler = new DataAccessHandler(getActivity());

        cropDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getCropsMasterInfo());
//        DataManager.getInstance().deleteData(DataManager.PLOT_INTER_CROP_DATA_PAIR);
//        DataManager.getInstance().deleteData(DataManager.PLOT_INTER_CROP_DATA); // TODO ROJA
        initViews();

        addCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiEntryDialogFragment multiEntryDialogFragment = new MultiEntryDialogFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(InterCropDetailsFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", MultiEntryDialogFragment.INTER_CROP_TYPE);
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
//                addCrop.setClickable(false);
//                addCrop.setEnabled(false);
            }
        });

        interCropAdapter = new SingleItemAdapter();
        interCropRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        interCropAdapter.setEditClickListener(this);
        interCropRecyclerView.setAdapter(interCropAdapter);
        saveBtn.setOnClickListener(this);
        bindData();

        return rootView;
    }

    private void bindData() {

        interCropPair = (List<Pair>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA_PAIR);
       interCropList = (List<CropModel>) DataManager.getInstance().getDataFromManager(DataManager.PLOT_INTER_CROP_DATA);
//        if (interCropList == null || interCropList.isEmpty()) {
//            List<InterCropPlantationXref> selectedInterCropData = (List<InterCropPlantationXref>) dataAccessHandler.getInterCropPlantationXrefData(Queries.getInstance().getInterCropPlantationXref(CommonConstants.PLOT_CODE), 1);
//            interCropList = convertToCurrentCropModel(selectedInterCropData);
//        }
        if (interCropPair != null && interCropPair.size() > 0) {
           // interCropList = convertToCurrentCropModel(InterCropPlantationXref);
            interCropAdapter.updateAdapter(interCropPair);
            saveBtn.setVisibility(View.VISIBLE);
            interCropRecyclerView.setVisibility(View.VISIBLE);
            headerLL.setVisibility(View.VISIBLE);
            addCrop.setClickable(false);
            addCrop.setEnabled(false);

            startAnimation();
        } else {
            interCropPair = new ArrayList<>();
        }
    }

    private void initViews() {
        interCropRecyclerView = (RecyclerView) rootView.findViewById(R.id.interCropRecyclerView);
        saveBtn = (Button) rootView.findViewById(R.id.saveBtn);
        historyBtn = (Button) rootView.findViewById(R.id.historyBtn);
        historyBtn.setVisibility(CommonUtils.isFromConversion() ? View.GONE : View.VISIBLE);
        historyBtn.setOnClickListener(this);
        this.addCrop = (RelativeLayout) rootView.findViewById(R.id.add_crop);
        addCropBottomView = (RelativeLayout) rootView.findViewById(R.id.add_intercrop_bottom);
        addCropBottomView.setVisibility(View.GONE);

        addCropBottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiEntryDialogFragment multiEntryDialogFragment = new MultiEntryDialogFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(InterCropDetailsFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", MultiEntryDialogFragment.INTER_CROP_TYPE);
                inpuptBundle.putInt("neighbourPlotCount", interCropPair.size());
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(getContext());
            }
        });

        headerLL = (LinearLayout) rootView.findViewById(R.id.headerLL);
    }
    public void showDialog(Context activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.intercroplastvisteddata);

        Toolbar titleToolbar = (Toolbar) dialog.findViewById(R.id.titleToolbar);
        titleToolbar.setTitle("InterCrop Details History");
        titleToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        TextView norecords = (TextView) dialog.findViewById(R.id.intercropnorecord_tv);
        LinearLayout mainLL = (LinearLayout) dialog.findViewById(R.id.intercropmainlyt);
        RecyclerView recyclerView = dialog.findViewById(R.id.intercropRecyclerView); // RecyclerView

        String lastVisitCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getLatestCropMaintanaceHistoryCode(CommonConstants.PLOT_CODE));
        android.util.Log.e("lastVisitCode", lastVisitCode + "");
        intercroplastvisitdatamap = (ArrayList<InterCropPlantationXref>) dataAccessHandler.getInterCropPlantationXrefData(Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF), 1);
        android.util.Log.e("lastVisitCode query", Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF) + "");

        if (intercroplastvisitdatamap.size() > 0) {
            norecords.setVisibility(View.GONE);
            mainLL.setVisibility(View.VISIBLE);

            // Prepare list for RecyclerView
            List<InterCropDetails> interCropDetailsList = new ArrayList<>();
            for (InterCropPlantationXref xref : intercroplastvisitdatamap) {
                String cropgrown = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(xref.getCropId()));
                String recommendedcrop = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(xref.getRecmCropId()));
                interCropDetailsList.add(new InterCropDetails(cropgrown, recommendedcrop));
            }

            // Set RecyclerView Adapter
            InterhistoryCropAdapter adapter = new InterhistoryCropAdapter(activity,interCropDetailsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
        } else {
            mainLL.setVisibility(View.GONE);
            norecords.setVisibility(View.VISIBLE);
        }

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Handle any delayed actions if necessary
            }
        }, 500);
    }

//    public void showDialog(Context activity) {
//        final Dialog dialog = new Dialog(activity);
//        dialog.setCancelable(true);
//        dialog.setContentView(R.layout.intercroplastvisteddata);
//
//        Toolbar titleToolbar;
//        titleToolbar = (Toolbar) dialog.findViewById(R.id.titleToolbar);
//        titleToolbar.setTitle("InterCrop Details History");
//        titleToolbar.setTitleTextColor(getResources().getColor(R.color.white));
//
//        TextView intercropcropgrown = (TextView) dialog.findViewById(R.id.intercropcropgrown);
//        TextView intercroprecommended = (TextView) dialog.findViewById(R.id.intercroprecommended);
//        TextView norecords = (TextView) dialog.findViewById(R.id.intercropnorecord_tv);
//        LinearLayout mainLL = (LinearLayout) dialog.findViewById(R.id.intercropmainlyt);
//
//        String lastVisitCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getLatestCropMaintanaceHistoryCode(CommonConstants.PLOT_CODE));
//        android.util.Log.e("lastVisitCode",lastVisitCode+ "");
//        intercroplastvisitdatamap = (ArrayList<InterCropPlantationXref>) dataAccessHandler.getInterCropPlantationXrefData(Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF), 1);
//        android.util.Log.e("lastVisitCode query",Queries.getInstance().getRecommndCropMaintenanceHistoryData(lastVisitCode, DatabaseKeys.TABLE_INTERCROPPLANTATIONXREF)+ "");
//        if (intercroplastvisitdatamap.size() > 0){
//            norecords.setVisibility(View.GONE);
//            mainLL.setVisibility(View.VISIBLE);
//
//            String cropgrown = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(intercroplastvisitdatamap.get(0).getCropId()));
//            String recommendedcrop = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getlookupdata(intercroplastvisitdatamap.get(0).getRecmCropId()));
//
//            intercropcropgrown.setText(cropgrown);
//            intercroprecommended.setText(recommendedcrop);
//
//        }else{
//            mainLL.setVisibility(View.GONE);
//            norecords.setVisibility(View.VISIBLE);
//        }
//
//
//
//        dialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 500);
//    }
//

    @Override
    public void onClick(View v) {

            if (v.getId() == R.id.saveBtn){
                if(interCropPair!=null )
                DataManager.getInstance().addData(DataManager.PLOT_INTER_CROP_DATA_PAIR, interCropPair);
                if(interCropList!=null )
                DataManager.getInstance().addData(DataManager.PLOT_INTER_CROP_DATA, interCropList);
                updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();
//            case R.id.historyBtn:
//                CropMaintainanceHistoryFragment newFragment = new CropMaintainanceHistoryFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("screen", 0);
//                newFragment.setArguments(bundle);
//                newFragment.show(getActivity().getFragmentManager(), "history");
//
//                break;
        }
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    @Override
    public void onDataSelected(int type, Bundle bundle) {
        if (interCropList == null || interCropList.isEmpty()) {
            interCropList = new ArrayList<>();
            startAnimation();
        }
        interCropList.add(new CropModel(bundle.getString("cropName"), Integer.parseInt(bundle.getString("cropId")),Integer.parseInt(bundle.getString("recId")),bundle.getString("recName")));
        Log.v("@@@saveInter",""+bundle.getString("recId"));
        interCropPair.add(Pair.create(bundle.getString("cropName"),bundle.getString("recName")));
        interCropAdapter.updateAdapter(interCropPair);
        saveBtn.setVisibility(View.VISIBLE);
        interCropRecyclerView.setVisibility(View.VISIBLE);
        headerLL.setVisibility(View.VISIBLE);

    }

    public void startAnimation() {
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
        logoMoveAnimation.setFillAfter(true);
        logoMoveAnimation.setFillEnabled(true);
        addCrop.startAnimation(logoMoveAnimation);
        logoMoveAnimation.setAnimationListener(animationInListener);
        headerLL.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditClicked(int position, int tag) {
        EditEntryDialogFragment editEntryDialogFragment = new EditEntryDialogFragment();
        editEntryDialogFragment.setOnDataEditChangeListener(this);
        Bundle inputBundle = new Bundle();
        selectedPosition = position;
        inputBundle.putInt("typeDialog", EditEntryDialogFragment.EDIT_INTER_CROP);
        inputBundle.putString("title", interCropPair.get(position).first.toString());
        inputBundle.putString("prevData", interCropPair.get(position).second.toString());
        editEntryDialogFragment.setArguments(inputBundle);
        FragmentManager mFragmentManager = getChildFragmentManager();
        editEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
    }

    @Override
    public void onDeleteClicked(int position, int tag) {
        Log.v(LOG_TAG, "@@@ delete clicked " + position);
        interCropList.remove(position);
        interCropPair.remove(position);
        interCropAdapter.notifyDataSetChanged();
        if (interCropPair.isEmpty()) {
            saveBtn.setVisibility(View.GONE); // hide save button
            interCropRecyclerView.setVisibility(View.GONE);
            headerLL.setVisibility(View.GONE);

            // Re-enable addCrop button so user can add again
            addCrop.setClickable(true);
            addCrop.setEnabled(true);
            addCrop.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDataEdited(Bundle dataBundle) {

        int cropId = Integer.parseInt(cropDataMap.keySet().toArray(new String[cropDataMap.size()])[dataBundle.getInt("cropId") - 1]);
        int recId = Integer.parseInt(cropDataMap.keySet().toArray(new String[cropDataMap.size()])[dataBundle.getInt("recId") - 1]);
        Log.v("@@@editInter",""+recId);

        interCropList.set(selectedPosition, new CropModel(dataBundle.getString("inputValue"),cropId, recId,dataBundle.getString("recValue")));
        interCropPair.set(selectedPosition, Pair.create(dataBundle.getString("inputValue"),dataBundle.getString("recValue")));
        interCropAdapter.notifyDataSetChanged();

    }

    public List<CropModel> convertToCurrentCropModel(List<InterCropPlantationXref> data) {
        List<CropModel> currentCropList = new ArrayList<>();
        interCropPair = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            currentCropList.add(new CropModel(cropDataMap.get(String.valueOf(data.get(i).getCropId())), data.get(i).getCropId(),data.get(i).getRecmCropId(),cropDataMap.get(String.valueOf(data.get(i).getRecmCropId()))));
            interCropPair.add(Pair.create(cropDataMap.get(String.valueOf(data.get(i).getCropId())),""+cropDataMap.get(String.valueOf(data.get(i).getRecmCropId()))));
        }
        return currentCropList;
    }

}

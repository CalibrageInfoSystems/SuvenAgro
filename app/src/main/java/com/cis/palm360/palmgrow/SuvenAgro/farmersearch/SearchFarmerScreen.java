package com.cis.palm360.palmgrow.SuvenAgro.farmersearch;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.UploadImagesNew;
import com.cis.palm360.palmgrow.SuvenAgro.prospectiveFarmers.NewProspectiveFarmers;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.RegistrationFlowScreen;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Address;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.BasicFarmerDetails;
import com.cis.palm360.palmgrow.SuvenAgro.ui.ComplaintsScreenActivity;
import com.cis.palm360.palmgrow.SuvenAgro.ui.OilPalmBaseActivity;
import com.cis.palm360.palmgrow.SuvenAgro.ui.RecyclerItemClickListener;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//Search functionality in farmer screen
public class  SearchFarmerScreen extends OilPalmBaseActivity implements RecyclerItemClickListener {

    public static final int LIMIT = 30;
    private static final String LOG_TAG = SearchFarmerScreen.class.getSimpleName();
    String searchKey = "";
    int offset;
    private RelativeLayout viewallcomplaints;
    private DataAccessHandler dataAccessHandler;
    private RecyclerView farmlandLVMembers;
    private EditText mEtSearch;
    private ImageView mIVClear;
    private Button view_all_complaint;
    private TextView tvNorecords;
    private TextView no_text;
    private ProgressBar progress;
    private List<BasicFarmerDetails> mFarmersList = new ArrayList<>();
    private FarmerDetailsRecyclerAdapter farmerDetailsRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean hasMoreItems = true;
    public static  boolean FarmerImage = false ;
    private boolean isSearch = false;
    List<BasicFarmerDetails> farmerDetails;
    String selectedVillageIds;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.d("WhatisinSearch", "is :"+ s);
            //
            offset = 0;
            ApplicationThread.uiPost(LOG_TAG, "search", new Runnable() {
                @Override
                public void run() {
                    doSearch(s.toString().trim());
                    if (s.toString().length() > 0) {
                        mIVClear.setVisibility(View.VISIBLE);
                    } else {
                        mIVClear.setVisibility(View.GONE);
                    }
                }
            }, 100);
        }

        @Override
        public void afterTextChanged(final Editable s) {

        }
    };

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    if (!hasMoreItems) {
                        UiUtils.showCustomToastMessage("No more items", SearchFarmerScreen.this, 0);

                    } else if (isSearch){
                        isLoading = true;
                        offset = 0;
                        getAllUsers();
                    }else {
                        isLoading = true;
                        offset = offset + LIMIT;
                        getAllUsers();
                        //recyclerView.getLayoutManager().scrollToPosition(0);

                    }

                }
            }
        }
    };


    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.activity_todolist, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dataAccessHandler = new DataAccessHandler(this);

        String farmerfCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryFarmersCount());
        setTile(getString(R.string.farmer_list) + "("+farmerfCount+")");

        progress = (ProgressBar) findViewById(R.id.progress);
        farmerDetailsRecyclerAdapter = new FarmerDetailsRecyclerAdapter(SearchFarmerScreen.this, mFarmersList);

        initUI();
        getAllUsers();

        CommonUtils.currentActivity = this;
        Log.e("Farmerlist Size , " ,mFarmersList.size()+"");
    }
    private void initUI() {


        farmlandLVMembers = (RecyclerView) findViewById(R.id.lv_farmerlanddetails);

        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIVClear = (ImageView) findViewById(R.id.iv_clear);
        tvNorecords = (TextView) findViewById(R.id.no_records);
        no_text = (TextView) findViewById(R.id.no_text);
        viewallcomplaints = findViewById(R.id.viewallcomplaints);
        view_all_complaint = findViewById(R.id.view_all_complaint);


        if (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_COMPLAINT)) {
            viewallcomplaints.setVisibility(View.VISIBLE);
            view_all_complaint.setVisibility(View.VISIBLE);

            List<Map<String, String>> activityRights = dataAccessHandler.getActivityRightsForUser(CommonConstants.USER_ID);
            List<Integer> activityRightIds = new ArrayList<>();
            for (Map<String, String> right : activityRights) {
                try {
                    activityRightIds.add(Integer.parseInt(right.get("Id")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            Log.d("ActivityRightIds", activityRightIds.toString());
            if (activityRightIds.contains(16)) {
                view_all_complaint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in_compalints = new Intent(SearchFarmerScreen.this, ComplaintsScreenActivity.class);
                        in_compalints.putExtra("fromsearchfarmer", "fromsearchfarmer");
                        startActivity(in_compalints);
                    }
                });
            }
            else{
                view_all_complaint.setVisibility(View.GONE);
            }

        }else{
            viewallcomplaints.setVisibility(View.GONE);
            view_all_complaint.setVisibility(View.GONE);
        }

        mIVClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = false;
                mFarmersList.clear();
                mEtSearch.setText("");
            }
        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        farmlandLVMembers.setLayoutManager(layoutManager);
        farmlandLVMembers.setAdapter(farmerDetailsRecyclerAdapter);
        farmerDetailsRecyclerAdapter.setRecyclerItemClickListener(this);
        mEtSearch.addTextChangedListener(mTextWatcher);
        farmlandLVMembers.addOnScrollListener(recyclerViewOnScrollListener);
        Intent intent = getIntent();
        CommonConstants.SelectedvillageIds = intent.getStringExtra("selectedVillageIds");


        // Log or print the selectedVillageIds to verify
        Log.v("SearchFarmerScreen", "Selected Village IDs: " + CommonConstants.SelectedvillageIds);
        //  Toast.makeText(this, "Selected Village IDs: " + selectedVillageIds, Toast.LENGTH_LONG).show();

    }

    public void doSearch(String searchQuery) {
        Log.d("DoSearchQuery", "is :" +  searchQuery);
        offset = 0;
        hasMoreItems = true;
        if (searchQuery !=null &  !TextUtils.isEmpty(searchQuery)  & searchQuery.length()  > 0) {

            offset = 0;
            isSearch = true;
            searchKey = searchQuery.trim();
            getAllUsers();
        } else {
            searchKey = "";
            isSearch = false;
            getAllUsers();
        }
    }

    public void getAllUsers() {

        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
//        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", () ->
                dataAccessHandler. getFarmerDetailsForSearch(searchKey, offset, LIMIT,
                        new ApplicationThread.OnComplete<List<BasicFarmerDetails>>() {
                            @Override
                            public void execute(boolean success, final List<BasicFarmerDetails> farmerDetails, String msg) {
//                        ProgressBar.hideProgressBar();
                                isLoading = false;
                                if (farmerDetails.isEmpty()) {
                                    hasMoreItems = false;
                                }

                                if (offset == 0 && isSearch) {
                                    mFarmersList.clear();
                                    mFarmersList.addAll(farmerDetails);

                                } else {

                                    if(farmerDetails != null  & farmerDetails.size()  > 0)
                                        mFarmersList.clear();

                                    mFarmersList.addAll(farmerDetails);
                                    //farmlandLVMembers.getLayoutManager().scrollToPosition(0);
                                }
                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);
                                        int farmersSize = farmerDetails.size();
                                        Log.v(LOG_TAG, "data size " + farmersSize);
                                        farmerDetailsRecyclerAdapter.addItems(mFarmersList);
                                        if (farmerDetailsRecyclerAdapter.getItemCount() == 0) {
                                            no_text.setVisibility(View.VISIBLE);
                                            tvNorecords.setVisibility(View.VISIBLE);
                                            setTile(getString(R.string.farmer_list));
                                        } else {
                                            setTile(getString(R.string.farmer_list) + "("+mFarmersList.size()+")");
                                            no_text.setVisibility(View.GONE);
                                            tvNorecords.setVisibility(View.GONE);
                                            farmlandLVMembers.getLayoutManager().scrollToPosition(0);

                                        }
                                    }
                                });
                            }

                        }));

    }



    @Override
    public void onItemSelected(int position) {
        moveToNextFlow(position);
    }

    public void moveToNextFlow(final int position) {
        Log.e("Farmerlist Size , " ,mFarmersList.size()+"");
        DataManager.getInstance().addData(DataManager.IS_FARMER_DATA_UPDATED, false);
        DataManager.getInstance().addData(DataManager.IS_PLOTS_DATA_UPDATED, false);
        CommonUiUtils.resetPrevRegData();
        Farmer selectedFarmer = (Farmer) dataAccessHandler.getSelecteddFarmerData(Queries.getInstance().getSelectedFarmer(mFarmersList.get(position).getFarmerCode()), 0);
        Address selectedFarmerAddress = (Address) dataAccessHandler.getSelectedFarmerAddress(Queries.getInstance().getSelectedFarmerAddress(selectedFarmer.getAddresscode()), 0);
        FileRepository selectedFileRepository = dataAccessHandler.getSelectedFileRepository(Queries.getInstance().getSelectedFileRepositoryQuery(selectedFarmer.getCode(), 193));
        FileRepository selectedFileRepository_FarmerImage = dataAccessHandler.getSelectedFileRepository(Queries.getInstance().getSelectedFileRepositoryQuery(selectedFarmer.getCode(), 193));


        if (null != selectedFarmer && selectedFarmerAddress != null) {
            CommonUiUtils.setGeoGraphicalData(selectedFarmer, this);
            CommonConstants.FARMER_CODE = selectedFarmer.getCode();
            DataManager.getInstance().addData(DataManager.FARMER_PERSONAL_DETAILS, selectedFarmer);
            DataManager.getInstance().addData(DataManager.FARMER_ADDRESS_DETAILS, selectedFarmerAddress);
            if(selectedFileRepository!=null) {
                DataManager.getInstance().addData(DataManager.FILE_REPOSITORY, selectedFileRepository);
            }
            if (selectedFileRepository_FarmerImage!=null){
                FarmerImage = true;
            }
        }

        if (CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_VPF)) {
            Intent intent = new Intent(SearchFarmerScreen.this, NewProspectiveFarmers.class);
            startActivity(intent);
        }else if(CommonConstants.REGISTRATION_SCREEN_FROM.equalsIgnoreCase(CommonConstants.REGISTRATION_SCREEN_FROM_IMAGESUPLOADING))
        {
            Intent intent = new Intent(SearchFarmerScreen.this, UploadImagesNew.class);
            startActivity(intent);
        }

        else if (CommonUtils.isFromFollowUp() || CommonUtils.isFromConversion() || CommonUtils.isFromCropMaintenance()||
                CommonUtils.isPlotSplitFarmerPlots() || CommonUtils.isVisitRequests() || CommonUtils.isFromHarvesting()
                || CommonUtils.isFromPlantationAudit() || CommonUtils.isFromviewonmaps() || CommonUtils.isComplaint() ||
                CommonUtils.isFromDripFollowUp() || CommonUtils.isManageAdvanceDetails() ||
                CommonUtils.isaddDispatchSaplings() || CommonUtils.isViewDispatchSaplingsDetails() || CommonUtils.israiseDispatchSaplingsRequest()) {
            Log.e("========>282","Re Take Geo");
            FragmentManager fm = getSupportFragmentManager();
            DisplayPlotsFragment displayPlotsFragment = DisplayPlotsFragment.newInstance(mFarmersList.get(position));
            displayPlotsFragment.show(fm, "displayPlotsFragment");
        }

        else {
            startActivity(new Intent(this, RegistrationFlowScreen.class));
        }
    }

}

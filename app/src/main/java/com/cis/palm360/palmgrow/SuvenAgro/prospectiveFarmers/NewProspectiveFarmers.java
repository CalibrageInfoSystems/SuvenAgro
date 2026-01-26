package com.cis.palm360.palmgrow.SuvenAgro.prospectiveFarmers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.database.SqlString;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Farmer;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FileRepository;
import com.cis.palm360.palmgrow.SuvenAgro.ui.OilPalmBaseActivity;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.CircleImageView;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHENCHAIAH on 5/27/2017.
 */

//Displaying the prospective Farmers Data
public class NewProspectiveFarmers extends OilPalmBaseActivity {

    private static final String LOG_TAG = NewProspectiveFarmers.class.getName();

    private TextView farmerNameTxt;
    private TextView tvfathername, tvvillagename, tvcontactnum, tvaddress, selectedPlotsTxt,tvgvtfarmercode;
    private CircleImageView userImage;
    private RecyclerView rvplotlist;
    private Farmer selectedFarmer;
    private DataAccessHandler dataAccessHandler = null;
    private List<ProspectivePlotsModel> plotDetailsObjArrayList = new ArrayList<>();
    private ProspectivePlotsAdapter prospectivePlotsAdapter;
    private FileRepository savedPictureData;
    LinearLayout pdRLgvtfarmercode;

    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.activity_prospectivefarmers, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.prospective_details));

        selectedFarmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);
        dataAccessHandler = new DataAccessHandler(this);
        savedPictureData= dataAccessHandler.getSelectedFileRepository(Queries.getInstance().getSelectedFileRepositoryQuery(selectedFarmer.getCode(), 193));

        if (selectedFarmer == null) {
            UiUtils.showCustomToastMessage("Proper grower data not found", this, 1);
            return;
        }

        initViews();

        final String imageUrl = CommonUtils.getImageUrl(savedPictureData);
        if(imageUrl.isEmpty())
        {
            userImage.setImageResource(R.drawable.no_image);
        }
        else {

//        Glide.with(this)
//                .load(Uri.parse("file://" + savedPictureData.getPicturelocation()))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .error(R.mipmap.ic_launcher)
//                .into(userImage);
            Picasso.get()
                    .load(imageUrl)
                    .error(R.mipmap.ic_launcher)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            if (!isFinishing()) {
                                ApplicationThread.uiPost(LOG_TAG, "loading image", new Runnable() {
                                    @Override
                                    public void run() {
                                        if (savedPictureData != null && !savedPictureData.getPicturelocation().isEmpty() && savedPictureData.getPicturelocation() != null) {
                                            loadImageFromStorage(savedPictureData.getPicturelocation(), userImage);
                                        }
                                    }
                                }, 50);
                            }
                        }
                    });
        }
        Log.d("xxx", "firstname: " + selectedFarmer.getFirstname() + " middlename: " + selectedFarmer.getMiddlename() + " lastname: " + selectedFarmer.getLastname() + "code: " + selectedFarmer.getCode());
        String farmerName = selectedFarmer.getMiddlename().equals("null") ? selectedFarmer.getFirstname() + " " + selectedFarmer.getLastname() : selectedFarmer.getFirstname() + " " + selectedFarmer.getMiddlename() + " " + selectedFarmer.getLastname();
        farmerNameTxt.setText(farmerName + " (" + selectedFarmer.getCode() + ")");
    }

    private void loadImageFromStorage(String path, ImageView imageView) {
        Glide.with(this)
                .load(path)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    private void initViews() {
        farmerNameTxt = (TextView) findViewById(R.id.farmerNameTxt);
        tvfathername = (TextView) findViewById(R.id.tvfathername);
        tvvillagename = (TextView) findViewById(R.id.tvvillagename);
        tvcontactnum = (TextView) findViewById(R.id.tvcontactnumber);
        tvaddress = (TextView) findViewById(R.id.tvaddress);
        userImage = (CircleImageView) findViewById(R.id.profile_pic);
        selectedPlotsTxt = (TextView) findViewById(R.id.selectedPlotsTxt);
        rvplotlist = (RecyclerView) findViewById(R.id.lv_farmerplotdetails);
        tvgvtfarmercode = (TextView) findViewById(R.id.tvgvtfarmercode);
        pdRLgvtfarmercode = (LinearLayout) findViewById(R.id.pdRLgvtfarmercode);
        bindData();
        bindSelectedFarmerData();
    }

    private void bindData() {
        dataAccessHandler = new DataAccessHandler(this);
        plotDetailsObjArrayList = dataAccessHandler.getProspectivePlotDetails(selectedFarmer.getCode(), 81);
        if (plotDetailsObjArrayList != null && plotDetailsObjArrayList.size() > 0) {
            prospectivePlotsAdapter = new ProspectivePlotsAdapter(this, plotDetailsObjArrayList);
            rvplotlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(prospectivePlotsAdapter);
        }
    }

    private void bindSelectedFarmerData() {
        tvfathername.setText(": "+selectedFarmer.getGuardianname());
        Log.d("xxx", "selectedFarmer.getGovtFarmerCode(): " + selectedFarmer.getGovtFarmerCode());
        if (selectedFarmer != null) {
            String code = selectedFarmer.getGovtFarmerCode();

            if (code != null && !code.trim().isEmpty() && !code.equalsIgnoreCase("null")) {
                tvgvtfarmercode.setText(": "+code.trim());
                pdRLgvtfarmercode.setVisibility(View.VISIBLE);
            } else {
                pdRLgvtfarmercode.setVisibility(View.GONE);
                tvgvtfarmercode.setText(""); // Optional: clear text
            }
        } else {
            pdRLgvtfarmercode.setVisibility(View.GONE);
            tvgvtfarmercode.setText(""); // Optional: clear text
        }

        tvvillagename.setText(": "+dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().onlyValueFromDb("Village", "Name", " Id = "+ SqlString.Int(selectedFarmer.getVillageid()))));
        tvcontactnum.setText(selectedFarmer.getContactnumber() != null ? ": "+selectedFarmer.getContactnumber().trim() : "");
        //  tvgvtfarmercode
    }
}

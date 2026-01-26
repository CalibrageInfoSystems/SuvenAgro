package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by latitude on 04-05-2017.
 */

//Soil/Power details display adapter
public class SoilTypeAdapter extends RecyclerView.Adapter<SoilTypeAdapter.MyHolder> {
    private Context mContext;
    boolean isdelete;
    private ArrayList<PlotIrrigationTypeXref> msoilTypeIrrigationModelList;
    private OnCartChangedListener onCartChangedListener;
    LinkedHashMap<String, String> irrigationMap;
    private DataAccessHandler dataAccessHandler;
     LinkedHashMap<String, String>  primaryDripCompanyMap, SecondaryDripCompanyMap,dripcompanyMap,PumpTypeMap;

    public SoilTypeAdapter(Context mContext, ArrayList<PlotIrrigationTypeXref> irrigationModelList, LinkedHashMap<String, String> irrigationMap, LinkedHashMap<String, String> primaryDripCompanyMap, LinkedHashMap<String, String> pumpMap, boolean delete) {
        this.mContext = mContext;
        this.msoilTypeIrrigationModelList = irrigationModelList;
        this.irrigationMap = irrigationMap;
        this.primaryDripCompanyMap = primaryDripCompanyMap;
        this.PumpTypeMap = pumpMap;
        this.isdelete= delete;
    }


    @Override
    public SoilTypeAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View bookingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.soil_adapter, null);
        SoilTypeAdapter.MyHolder myHolder = new SoilTypeAdapter.MyHolder(bookingView);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(SoilTypeAdapter.MyHolder holder, final int position) {
        dataAccessHandler = new DataAccessHandler(mContext);

Log.e("======delete value",isdelete +"");
        PlotIrrigationTypeXref model = msoilTypeIrrigationModelList.get(position);
        holder.typeOfIrrigation.setText("" + model.getName());
        holder.irrigationRecommendation.setText("" + irrigationMap.get("" + model.getRecmIrrgId()));
        if (model.getRecmIrrgId() == 391 || model.getIrrigationtypeid() == 391) {

            holder.dripll.setVisibility(View.VISIBLE);
            holder.dripInstalled.setText(model.getIsDripInstalled() == 1 ? "Yes" : "No");

            if (model.getIsDripInstalled() == 1) {
                holder.pumpTypell.setVisibility(View.GONE);
                holder.capacityll.setVisibility(View.GONE);
                holder.companyName2ll.setVisibility(View.GONE);


                holder.dripDatell.setVisibility(View.VISIBLE);

                holder.primarycompany.setText("Company Name");
                holder.dripDate.setText(model.getDripInstalledDate() != null ? model.getDripInstalledDate() : "");
Log.d("======drip date",model.getHPIdNumber() + "");
                String hpId = model.getHPIdNumber();

                if (hpId != null && !hpId.trim().isEmpty()) {
                    holder.hlNumberll.setVisibility(View.VISIBLE);
                    holder.hlNumber.setText(hpId);
                } else {
                    holder.hlNumberll.setVisibility(View.GONE);
                }

                String primaryCompany = primaryDripCompanyMap.get(String.valueOf(model.getPrimaryCompanyId()));
                holder.companyNames.setText(primaryCompany != null ? primaryCompany : "");

            } else {
                holder.pumpTypell.setVisibility(View.VISIBLE);
                holder.capacityll.setVisibility(View.VISIBLE);
                holder.companyName2ll.setVisibility(View.VISIBLE);

                holder.hlNumberll.setVisibility(View.GONE);
                holder.dripDatell.setVisibility(View.GONE);

                holder.primarycompany.setText("Primary Company Name");

                String primaryCompany = primaryDripCompanyMap.get(String.valueOf(model.getPrimaryCompanyId()));
                holder.companyNames.setText(primaryCompany != null ? primaryCompany : "");

                String secondaryCompany = primaryDripCompanyMap.get(String.valueOf(model.getSecondaryCompanyId()));
                holder.companyName2.setText(secondaryCompany != null ? secondaryCompany : "");
                if (secondaryCompany != null) {
                    holder.companyName2ll.setVisibility(View.VISIBLE);
                    holder.companyName2.setText(secondaryCompany != null ? secondaryCompany : "");
                }
                else {
                holder.companyName2ll.setVisibility(View.GONE);}
                String pumpType = PumpTypeMap.get(String.valueOf(model.getWaterPumpTypeId()));
                holder.pumpType.setText(pumpType != null ? pumpType : "");

                holder.capacity.setText( model.getCapacity() + "");
            }

        } else {
            holder.dripll.setVisibility(View.GONE);
        }
        if(isdelete ){
            holder.deleteIcon.setVisibility(View.VISIBLE);
        }else{
            holder.deleteIcon.setVisibility(View.GONE);
        }

        // ✅ Delete icon logic
        holder.deleteIcon.setOnClickListener(v -> {
            if (onCartChangedListener != null) {
                onCartChangedListener.setCartClickListener("delete", position);
            }
        });

        holder.editIcon.setOnClickListener(v -> {
            if (onCartChangedListener != null) {
                onCartChangedListener.setCartClickListener("edit", position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return msoilTypeIrrigationModelList != null ? msoilTypeIrrigationModelList.size() : 0;
    }

//    public void setOnCartChangedListener(View.OnClickListener onClickListener) {
//        this.onCartChangedListener = (OnCartChangedListener) onClickListener;
//    }

    class MyHolder extends RecyclerView.ViewHolder {

LinearLayout capacityll,pumpTypell,dripDatell,hlNumberll,companyName2ll,dripll;
        TextView typeOfIrrigation, irrigationRecommendation, dripInstalled, hlNumber, dripDate, companyNames, pumpType,capacity,companyName2,primarycompany;
        ImageView deleteIcon;
        ImageView editIcon; // Add this at the top

        private View convertView;
        //
        public MyHolder(View view) {
            super(view);
            convertView = view;
            typeOfIrrigation = view.findViewById(R.id.typeOfIrigationCount);
            irrigationRecommendation = view.findViewById(R.id.typeOfIrigationResult);
            dripInstalled = view.findViewById(R.id.dripInstalled);
            companyName2ll = view.findViewById(R.id.companyName2ll);
            dripll = view.findViewById(R.id.dripll);
            dripDatell = view.findViewById(R.id.dripDatell);
            hlNumber = view.findViewById(R.id.hlNumber);
            hlNumberll = view.findViewById(R.id.hlNumberll);
            dripDate = view.findViewById(R.id.dripDate);
            companyNames = view.findViewById(R.id.companyNames);
            companyName2 = view.findViewById(R.id.companyName2);
            pumpType = view.findViewById(R.id.pumpType);
            primarycompany = view.findViewById(R.id.primarycompany);
            deleteIcon = view.findViewById(R.id.trashIcon);
            capacity = view.findViewById(R.id.capacity);
            capacityll = view.findViewById(R.id.capacityll);
            pumpTypell= view.findViewById(R.id.pumpTypell);
            // Inside constructor
            editIcon = view.findViewById(R.id.editIcon);


        }
    }

    public void setOnCartChangedListener(OnCartChangedListener onCartChangedListener) {
        this.onCartChangedListener = onCartChangedListener;
    }


    public interface OnCartChangedListener {
        void setCartClickListener(String status, int position);
    }

}

package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotIrrigationTypeXref;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class SoilIrrigationAdapter extends RecyclerView.Adapter<SoilIrrigationAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<PlotIrrigationTypeXref> msoilTypeIrrigationModelList;
    private OnCartChangedListener onCartChangedListener;
    LinkedHashMap<String, String> irrigationMap;


    public SoilIrrigationAdapter(Context mContext, ArrayList<PlotIrrigationTypeXref> irrigationModelList, LinkedHashMap<String, String> irrigationMap) {
        this.mContext = mContext;
        this.msoilTypeIrrigationModelList = irrigationModelList;
        this.irrigationMap = irrigationMap;
    }


    @Override
    public SoilIrrigationAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View bookingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.irrigation_adapter, null);
        SoilIrrigationAdapter.MyHolder myHolder = new SoilIrrigationAdapter.MyHolder(bookingView);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(SoilIrrigationAdapter.MyHolder holder, final int position) {
        holder.typeOfIrrigation.setText(msoilTypeIrrigationModelList.get(position).getName());
        holder.irrigationRecommendation.setText(irrigationMap.get(""+msoilTypeIrrigationModelList.get(position).getRecmIrrgId()));
        Log.v("@@@id",""+msoilTypeIrrigationModelList.get(position).getRecmIrrgId());
        Log.v("@@@nameid",""+irrigationMap.get(""+msoilTypeIrrigationModelList.get(position).getRecmIrrgId()));


    }


    @Override
    public int getItemCount() {
        return msoilTypeIrrigationModelList != null ? msoilTypeIrrigationModelList.size() : 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView typeOfIrrigation, irrigationRecommendation;
        private ImageView deleteIcon;
        private View convertView;

        public MyHolder(View view) {
            super(view);
            convertView = view;
            typeOfIrrigation = (TextView) itemView.findViewById(R.id.typeOfIrigationCount);
            irrigationRecommendation = (TextView) itemView.findViewById(R.id.typeOfIrigationResult);
            //  deleteIcon = (ImageView) itemView.findViewById(R.id.trashIcon);


        }
    }

    public void setOnCartChangedListener(OnCartChangedListener onCartChangedListener) {
        this.onCartChangedListener = onCartChangedListener;
    }

    public interface OnCartChangedListener {
        void setCartClickListener(String status,int position);
    }
}

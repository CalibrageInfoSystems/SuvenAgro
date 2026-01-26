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
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.WaterResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class WaterTypeAdapter extends RecyclerView.Adapter<WaterTypeAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<WaterResource> mWaterTypeModelList;
    private LinkedHashMap sourceOfWaterMap;
    private OnCartChangedListener onCartChangedListener;


    public WaterTypeAdapter(Context mContext, ArrayList<WaterResource> mWaterTypeModelList,LinkedHashMap sourceOfWaterMap) {
        this.mContext = mContext;
        this.mWaterTypeModelList = mWaterTypeModelList;
        this.sourceOfWaterMap = sourceOfWaterMap;
    }


    @Override
    public WaterTypeAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bookingView = inflater.inflate(R.layout.water_adapter, null);
        WaterTypeAdapter.MyHolder myHolder = new WaterTypeAdapter.MyHolder(bookingView);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(WaterTypeAdapter.MyHolder holder, final int position) {
        holder.typeTxt.setText(""+sourceOfWaterMap.get(String.valueOf(mWaterTypeModelList.get(position).getSourceofwaterid())));
        holder.numberTxt.setText(""+mWaterTypeModelList.get(position).getBorewellnumber());
        holder.dischargeTxt.setText(""+mWaterTypeModelList.get(position).getWaterdischargecapacity());
        holder.canalTxt.setText(""+mWaterTypeModelList.get(position).getCanalwater());

        if (null != mWaterTypeModelList.get(position)) {
            holder.numberTxtLay.setVisibility((null != mWaterTypeModelList.get(position).getBorewellnumber() && mWaterTypeModelList.get(position).getBorewellnumber() != 0) ? View.VISIBLE : View.GONE);
            holder.dischargeTxtLay.setVisibility(mWaterTypeModelList.get(position).getWaterdischargecapacity() != 0.0 ? View.VISIBLE : View.GONE);
            holder.canalTxtLay.setVisibility(mWaterTypeModelList.get(position).getCanalwater() != 0 ? View.VISIBLE : View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return mWaterTypeModelList != null ? mWaterTypeModelList.size() : 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView numberTxt, dischargeTxt, canalTxt, typeTxt;
        private LinearLayout numberTxtLay, dischargeTxtLay,canalTxtLay;
        private ImageView editIcon,deleteIcon;

        public MyHolder(View view) {
            super(view);
            numberTxt = (TextView) itemView.findViewById(R.id.numberTxt);
            dischargeTxt = (TextView) itemView.findViewById(R.id.dischargeTxt);
            canalTxt = (TextView) itemView.findViewById(R.id.canalTxt);
            typeTxt = (TextView) itemView.findViewById(R.id.typeTxt);
            numberTxtLay = (LinearLayout) itemView.findViewById(R.id.numberTxtLay);
            dischargeTxtLay = (LinearLayout) itemView.findViewById(R.id.dischargeTxtLay);
            canalTxtLay = (LinearLayout) itemView.findViewById(R.id.canalTxtLay);
            editIcon = (ImageView) itemView.findViewById(R.id.editIcon);
            deleteIcon = (ImageView) itemView.findViewById(R.id.trashIcon);

        }
    }
    public void setOnCartChangedListener(OnCartChangedListener onCartChangedListener) {
        this.onCartChangedListener = onCartChangedListener;
    }

    public interface OnCartChangedListener {
        void setCartClickListener(String status,int position);
    }
}


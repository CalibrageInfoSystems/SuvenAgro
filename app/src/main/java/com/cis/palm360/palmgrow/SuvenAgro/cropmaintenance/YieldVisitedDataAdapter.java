package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.YieldAssessment;

import java.util.ArrayList;

public class YieldVisitedDataAdapter extends RecyclerView.Adapter<YieldVisitedDataAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<YieldAssessment> itemsList;
    private DataAccessHandler dataAccessHandler;

    public YieldVisitedDataAdapter(Context context, ArrayList itemsList,DataAccessHandler dataAccessHandler) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.dataAccessHandler = dataAccessHandler;
    }

//    @NonNull
//    @Override
//    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
////        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_yieldassesment, null);
////        MyHolder mh = new MyHolder(v);
////        return mh;
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        View listItem = layoutInflater.inflate(R.layout.adapter_yieldassesment, parent, false);
//        MyHolder viewHolder = new MyHolder(listItem);
//        return viewHolder;
//
//
//    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_yieldassesment, parent, false);
        MyHolder viewHolder = new MyHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(YieldVisitedDataAdapter.MyHolder myHolder, int i) {

        myHolder.yieldquestion.setText(itemsList.get(i).getQuestion());
        myHolder.yieldvalue.setText(itemsList.get(i).getValue() + "");

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        TextView yieldquestion, yieldvalue;

        public MyHolder( View itemView) {
            super(itemView);

            yieldquestion = itemView.findViewById(R.id.yieldquestion);
            yieldvalue = itemView.findViewById(R.id.yieldvalue);
        }
    }
}

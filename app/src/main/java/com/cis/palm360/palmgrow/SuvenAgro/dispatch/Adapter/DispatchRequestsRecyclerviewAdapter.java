package com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;

import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.FarmersDataforImageUploading;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotAuditDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.DispatchRequestsModel;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.FarmerDetailsRecyclerAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DispatchRequestsRecyclerviewAdapter extends RecyclerView.Adapter<DispatchRequestsRecyclerviewAdapter.DispatchRequestsDetailsViewHolder>{

    private static final String LOG_TAG = FarmerDetailsRecyclerAdapter.class.getName();
    private List<DispatchRequestsModel> mList;
    private Context context;
    private DispatchRequestsModel item;
    private RecyclerItemClickListener recyclerItemClickListener;
    private DataAccessHandler dataAccessHandler;
    private ClickListener clickListener;

    public DispatchRequestsRecyclerviewAdapter(Context context, List<DispatchRequestsModel> mList, DataAccessHandler dataAccessHandler, ClickListener clickListener) {
        this.context = context;
        this.mList = mList;
        this.dataAccessHandler = dataAccessHandler;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DispatchRequestsDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dispatchrequest_item, null);
        DispatchRequestsDetailsViewHolder myHolder = new DispatchRequestsDetailsViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DispatchRequestsDetailsViewHolder holder, int position) {


        item = mList.get(position);
        holder.ReceiptNumber.setText( " : " + item.getReceiptNumber());
        holder.NoOfImportedSaplingsToDispatch.setText(" : " + item.getNoOfImportedSaplingsToDispatch() + "");
        holder.NoOfIndigenousSaplingsToDispatch.setText(" : " + item.getNoOfIndigenousSaplingsToDispatch() + "");
        holder.NoOfSaplingsToDispatch.setText(" : " + item.getNoOfSaplingsToDispatch() + "");
        holder.Status.setText(" : " + item.getDesc());
//        holder.Comments.setText(" : " + item.getComments());
        if (item.getComments() != null && !item.getComments().trim().isEmpty() && !item.getComments().equalsIgnoreCase("null")) {
            holder.commnets_layout.setVisibility(View.VISIBLE);
            holder.Comments.setText(" : "+item.getComments());
        }else {
            holder.commnets_layout.setVisibility(View.GONE);
        }
        holder.PlotCode.setText(" : " + item.getPlotCode());

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(item.getExpDateOfPickup());
            String formattedDate = outputFormat.format(date);
            holder.ExpDateOfPickup.setText(" : " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.pickiup_date_layout.setVisibility(View.GONE);
        }

//        holder.ExpDateOfPickup.setText(" : " + item.getExpDateOfPickup());
        holder.CreatedBy.setText(" : " + item.getCreatedByUserId() + "");
        holder.CreatedDate.setText(" : " + item.getCreatedDate());
        holder.UpdateBy.setText(" : " + item.getUpdateByUserId() + "");
        holder.UpdatedDate.setText(" : " + item.getUpdatedDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClicked(item);
            }
        });

        holder.ivDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemDeleteClicked(item);
            }
        });



        setViews(holder);

    }
    public void setViews(DispatchRequestsDetailsViewHolder holder){
        String fullname = "", middleName = "";

        ArrayList<FarmersDataforImageUploading> farmersdata;
        ArrayList<PlotAuditDetails> plotAuditDetailsData;
        farmersdata = dataAccessHandler.getFarmerDetailsforImageUploading(Queries.getInstance().getfarmerdetailsforimageuploading(CommonConstants.FARMER_CODE));
        plotAuditDetailsData = dataAccessHandler.getPlotDetailsforAudit(Queries.getInstance().getPlotDetailsforAudit(CommonConstants.PLOT_CODE));

        if (!TextUtils.isEmpty(farmersdata.get(0).getMiddleName()) && !
                farmersdata.get(0).getMiddleName().equalsIgnoreCase("null")) {
            middleName = farmersdata.get(0).getMiddleName();
        }
        fullname = farmersdata.get(0).getFirstName().trim() + " " + middleName + " " + farmersdata.get(0).getLastName().trim();

        Log.d("Grower Name", fullname + "");
        Log.d("Grower Code", CommonConstants.FARMER_CODE);
        Log.d("Grower Field Village", plotAuditDetailsData.get(0).getVillageName());

        holder.grower_name.setText(" : " + fullname);
        holder.grower_code.setText(" : " + CommonConstants.FARMER_CODE);
        holder.field_village.setText(" : " + plotAuditDetailsData.get(0).getVillageName());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<DispatchRequestsModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(DispatchRequestsModel model);
        void onItemDeleteClicked(DispatchRequestsModel model);
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public class DispatchRequestsDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView ReceiptNumber;
        private TextView NoOfImportedSaplingsToDispatch;
        private TextView NoOfIndigenousSaplingsToDispatch;
        private TextView NoOfSaplingsToDispatch;
        private TextView Status;
        private TextView PlotCode;
        private TextView ExpDateOfPickup;
        private TextView Comments;

        private TextView CreatedBy;
        private TextView CreatedDate;
        private TextView UpdateBy;
        private TextView UpdatedDate;
        private ImageView ivDeleteRequest;

        private View convertView;
        private TextView grower_code,grower_name,field_village ;
        LinearLayout pickiup_date_layout,commnets_layout;


        public DispatchRequestsDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            convertView = itemView;
            ReceiptNumber = (TextView) convertView.findViewById(R.id.receiptId);
            NoOfImportedSaplingsToDispatch = (TextView) convertView.findViewById(R.id.NoofImportedSaplings_text);
            NoOfIndigenousSaplingsToDispatch = (TextView) convertView.findViewById(R.id.noofIndegenoussapling_txt);
            NoOfSaplingsToDispatch = (TextView) convertView.findViewById(R.id.noofsaplingstodispatch_text);
            Status = (TextView) convertView.findViewById(R.id.status_txt);
            PlotCode = (TextView) convertView.findViewById(R.id.plotcode_txt);
            ExpDateOfPickup = (TextView) convertView.findViewById(R.id.expdateofpick_text);
            Comments = (TextView) convertView.findViewById(R.id.comments_txt);
            CreatedBy = (TextView) convertView.findViewById(R.id.createdby_txt);
            CreatedDate = (TextView) convertView.findViewById(R.id.createddate_txt);
            UpdateBy = (TextView) convertView.findViewById(R.id.updatedBy_txt);
            UpdatedDate = (TextView) convertView.findViewById(R.id.Updateddate_txt);
            grower_code = itemView.findViewById(R.id.tv_grower_code);
            grower_name = itemView.findViewById(R.id.tv_grower_name);
            field_village = itemView.findViewById(R.id.tv_field_village);
            ivDeleteRequest = itemView.findViewById(R.id.ivDeleteRequest);
            pickiup_date_layout = itemView.findViewById(R.id.pickiup_date_layout);
            commnets_layout = itemView.findViewById(R.id.commnets_layout);
        }
    }
}

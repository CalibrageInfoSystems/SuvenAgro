package com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter;

import static org.apache.http.impl.cookie.DateUtils.formatDate;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewDispatchDetailsAdapter extends RecyclerView.Adapter<NewDispatchDetailsAdapter.NewDispatchDetailsViewHolder>{

    private List<DispatchRequestsModel> mList;
    private Context context;
    private DispatchRequestsModel item;
    private DataAccessHandler dataAccessHandler;


    public NewDispatchDetailsAdapter(List<DispatchRequestsModel> mList, Context context, DataAccessHandler dataAccessHandler) {
        this.mList = mList;
        this.context = context;
        this.dataAccessHandler = dataAccessHandler;

    }

    @NonNull
    @Override
    public NewDispatchDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_dispatch_details_item, null);
        NewDispatchDetailsViewHolder myHolder = new NewDispatchDetailsViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewDispatchDetailsViewHolder holder, int position) {

        setViews(holder);

        item = mList.get(position);

        String username=" ";
        username= dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getusernamequery(item.getCreatedByUserId() + " "));

        holder.ReceiptNumber.setText( " : " + item.getReceiptNumber());
        holder.NoOfImportedSaplingsToDispatch.setText(" : " + item.getNoOfImportedSaplingsToDispatch() + "");
        holder.NoOfIndigenousSaplingsToDispatch.setText(" : " + item.getNoOfIndigenousSaplingsToDispatch() + "");
        holder.NoOfSaplingsToDispatch.setText(" : " + item.getNoOfSaplingsToDispatch() + "");
        holder.Status.setText(" : " + item.getDesc());
//        holder.Comments.setText(" : " + item.getComments());
        holder.PlotCode.setText(" : " + item.getPlotCode());
//        holder.ExpDateOfPickup.setText(" : " + item.getExpDateOfPickup());
        holder.CreatedBy.setText(" : " + username + "");
        holder.CreatedDate.setText(" : " + item.getCreatedDate());
        holder.UpdateBy.setText(" : " + item.getUpdateByUserId() + "");
        holder.UpdatedDate.setText(" : " + item.getUpdatedDate());

        String comment = item.getComments();

        if (comment != null && !comment.trim().isEmpty() && !comment.equalsIgnoreCase("null")) {
            holder.commentsLayout.setVisibility(View.VISIBLE);
            holder.Comments.setText(" : " + comment.trim());
        } else {
            holder.commentsLayout.setVisibility(View.GONE);
            holder.Comments.setText("");
        }

        String expDateOfPickup = item.getExpDateOfPickup();
        if (expDateOfPickup != null && !expDateOfPickup.trim().isEmpty() && !expDateOfPickup.equalsIgnoreCase("null")) {
            String formattedDate = formatDate(expDateOfPickup); // Convert to dd/MM/yyyy format
            holder.exp_date_of_pick_layout.setVisibility(View.VISIBLE);
            holder.ExpDateOfPickup.setText(" : " + formattedDate);
            Log.d("Adapter","Purchased date:"+formattedDate);
        } else {
            holder.exp_date_of_pick_layout.setVisibility(View.GONE);
        }
    }
    private String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return inputDate; // fallback to original if parsing fails
        }
    }

    public void setViews(NewDispatchDetailsViewHolder holder){
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

    public class NewDispatchDetailsViewHolder extends RecyclerView.ViewHolder{

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

        private View convertView;
        private TextView grower_code,grower_name,field_village ;
        LinearLayout commentsLayout,exp_date_of_pick_layout;
        public NewDispatchDetailsViewHolder(@NonNull View itemView) {
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
            commentsLayout = itemView.findViewById(R.id.commentsLayout);
            exp_date_of_pick_layout = itemView.findViewById(R.id.exp_date_of_pick_layout);
        }
    }
}

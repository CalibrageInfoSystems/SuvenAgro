package com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.NurserySaplingDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NurserySaplingDetailsAdapter extends RecyclerView.Adapter<NurserySaplingDetailsAdapter.ViewHolder> {
    private List<NurserySaplingDetails> nurserySaplingDetailsList;
    private DataAccessHandler dataAccessHandler;

    public NurserySaplingDetailsAdapter(List<NurserySaplingDetails> nurserySaplingDetailsList, DataAccessHandler dataAccessHandler) {
        this.nurserySaplingDetailsList = nurserySaplingDetailsList;
        this.dataAccessHandler = dataAccessHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nursey_sapling_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NurserySaplingDetails model = nurserySaplingDetailsList.get(position);

        String username = "";

        username = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getusernamequery(model.getCreatedByUserId()+ " "));

        holder.plot_code.setText(": "+ model.getPlotCode());
      //  holder.pick_up_date.setText(": "+ model.getSaplingPickUpDate());
        holder.saplings_dispatched.setText(": "+ model.getNoOfSaplingsDispatched());
        holder.imported_saplings.setText(": "+ model.getNoOfImportedSaplingsDispatched());
        holder.indigenous_saplings.setText(": "+ model.getNoOfIndigenousSaplingsDispatched());
        holder.receipt_number.setText(": "+ model.getReceiptNumber());
        holder.created_userid.setText(": "+ username);
//        holder.created_date.setText(": "+ model.getCreatedDate());
//        holder.updated_by_userid.setText(": "+ model.getUpdatedByUserId());
//        holder.updated_date.setText(": "+ model.getUpdatedDate());
        holder.advance_id.setText(": "+model.getAdvanceReceiptNumber());
//        holder.nursery_id.setText(": "+ model.getNurseryId());
//        holder.sapling_source_id.setText(": "+model.getSaplingSourceId());
//        holder.sapling_vendor_id.setText(": "+model.getSaplingVendorId());
//        holder.crop_variety_id.setText(": "+model.getCropVarietyId());
//        holder.purchased_date.setText(": "+model.getPurchaseDate());
//        holder.batch_no.setText(": "+ model.getBatchNo());
//        holder.comments.setText(": "+model.getComments());
//        holder.server_status.setText(": "+model.getServerUpdatedStatus());

        //nursery id
        if(model.getNurseryId() != null && model.getNurseryId() !=0 ){
            holder.nursery_id_layout.setVisibility(View.VISIBLE);
            holder.nursery_id.setText(": "+model.getNurseryId());
        } else {
            holder.nursery_id_layout.setVisibility(View.GONE);
        }
        String advReceipt = model.getAdvanceReceiptNumber();
        if (advReceipt != null && !advReceipt.trim().isEmpty() && !advReceipt.equalsIgnoreCase("null")) {
            holder.advance_id.setText(": " + advReceipt);
            holder.advance_layout.setVisibility(View.VISIBLE);
        } else {
            holder.advance_layout.setVisibility(View.GONE);
        }
        String batch = model.getBatchNo();
        if (batch != null && !batch.trim().isEmpty() && !batch.equalsIgnoreCase("null")) {
            holder.layout_batch_no.setVisibility(View.VISIBLE);
            holder.batch_no.setText(": " + batch);
        } else {
            holder.layout_batch_no.setVisibility(View.GONE);
        }

        // Purchase Date
        String purchaseDate = model.getPurchaseDate();
        if (purchaseDate != null && !purchaseDate.trim().isEmpty() && !purchaseDate.equalsIgnoreCase("null")) {
            String formattedDate = formatDate(purchaseDate); // Convert to dd/MM/yyyy format
            holder.layout_purchase_date.setVisibility(View.VISIBLE);
            holder.purchased_date.setText(": " + formattedDate);
            Log.d("Adapter","Purchased date:"+formattedDate);
        } else {
            holder.layout_purchase_date.setVisibility(View.GONE);
        }

        // Crop Variety Id
        if (model.getCropVarietyId() != null && model.getCropVarietyId() != 0) {
            holder.layout_crop_variety_id.setVisibility(View.VISIBLE);
            holder.crop_variety_id.setText(": " + model.getCropVarietyId());
        } else {
            holder.layout_crop_variety_id.setVisibility(View.GONE);
        }

        // Sapling Vendor Id
        if (model.getSaplingVendorId() != null && model.getSaplingVendorId() != 0) {
            holder.layout_sapling_vendor_id.setVisibility(View.VISIBLE);
            holder.sapling_vendor_id.setText(": " + model.getSaplingVendorId());
        } else {
            holder.layout_sapling_vendor_id.setVisibility(View.GONE);
        }
        if (model.getSaplingSourceId() != null && model.getSaplingSourceId() != 0) {
            holder.layout_sapling_source_id.setVisibility(View.VISIBLE);
            holder.sapling_source_id.setText(": " + model.getSaplingSourceId());
        } else {
            holder.layout_sapling_source_id.setVisibility(View.GONE);
        }
        //comments
        if (model.getComments() != null && !model.getComments().trim().isEmpty() && !model.getComments().equalsIgnoreCase("null")) {
            holder.layout_comments.setVisibility(View.VISIBLE);
            holder.comments.setText(": "+model.getComments());
        }else {
            holder.layout_comments.setVisibility(View.GONE);
        }
        String saplingPickUp = model.getSaplingPickUpDate();
        if (saplingPickUp != null && !saplingPickUp.trim().isEmpty() && !saplingPickUp.equalsIgnoreCase("null")) {
            String formattedDate = formatDate(saplingPickUp); // Convert to dd/MM/yyyy format
            holder.layout_sapling_pick_date.setVisibility(View.VISIBLE);
            holder.pick_up_date.setText(": " + formattedDate);
            Log.d("Adapter","Purchased date:"+formattedDate);
        } else {
            holder.layout_sapling_pick_date.setVisibility(View.GONE);
        }
    }
   /* private String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return inputDate; // fallback to original if parsing fails
        }
    }*/
   private String formatDate(String inputDate) {
       if (inputDate == null || inputDate.trim().isEmpty() || inputDate.equalsIgnoreCase("null")) {
           return "";
       }

       String[] patterns = {
               "yyyy-MM-dd'T'HH:mm:ss", // with time
               "yyyy-MM-dd"             // only date
       };

       for (String pattern : patterns) {
           try {
               SimpleDateFormat inputFormat = new SimpleDateFormat(pattern, Locale.getDefault());
               Date date = inputFormat.parse(inputDate);
               if (date != null) {
                   SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                   return outputFormat.format(date);
               }
           } catch (Exception ignored) {
               // try next pattern
           }
       }

       // fallback if nothing matches
       return inputDate;
   }



    @Override
    public int getItemCount() {
        return nurserySaplingDetailsList != null ? nurserySaplingDetailsList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plot_code, pick_up_date,saplings_dispatched,imported_saplings,indigenous_saplings,receipt_number,
                created_userid,created_date,updated_by_userid,updated_date, nursery_id, sapling_source_id,
                sapling_vendor_id, crop_variety_id,purchased_date ,batch_no , advance_id, comments , server_status;
        LinearLayout layout_comments,layout_batch_no,layout_purchase_date,layout_crop_variety_id,layout_sapling_vendor_id,
                layout_sapling_source_id, nursery_id_layout,advance_layout,layout_sapling_pick_date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            plot_code = itemView.findViewById(R.id.tv_plot_code);
            pick_up_date = itemView.findViewById(R.id.tv_pickup_date);
            saplings_dispatched = itemView.findViewById(R.id.tv_saplings_dispatched);
            imported_saplings = itemView.findViewById(R.id.tv_imported_saplings);
            indigenous_saplings = itemView.findViewById(R.id.tv_indigenous_saplings_dispatched);
            receipt_number = itemView.findViewById(R.id.tv_receipt_number);
            created_userid = itemView.findViewById(R.id.tv_created_userid);
//            created_date = itemView.findViewById(R.id.tv_created_date);
            updated_by_userid = itemView.findViewById(R.id.tv_updated_by_userid);
//            updated_date = itemView.findViewById(R.id.tv_updated_date);
            nursery_id = itemView.findViewById(R.id.tv_nursery_id);
            sapling_source_id = itemView.findViewById(R.id.tv_sapling_source_id);
            sapling_vendor_id = itemView.findViewById(R.id.tv_sapling_vendor_id);
            crop_variety_id = itemView.findViewById(R.id.tv_crop_variety_id);
            purchased_date = itemView.findViewById(R.id.purchased_date);
            batch_no = itemView.findViewById(R.id.batch_no);
            advance_id = itemView.findViewById(R.id.tv_advance_id);
            comments = itemView.findViewById(R.id.comments);
//            server_status = itemView.findViewById(R.id.server_status);
            layout_comments = itemView.findViewById(R.id.layout_comments);
            layout_batch_no = itemView.findViewById(R.id.layout_batch_no);
            layout_purchase_date = itemView.findViewById(R.id.layout_purchase_date);
            layout_crop_variety_id = itemView.findViewById(R.id.layout_crop_variety_id);
            layout_sapling_vendor_id = itemView.findViewById(R.id.layout_sapling_vendor_id);
            layout_sapling_source_id = itemView.findViewById(R.id.layout_sapling_source_id);
            nursery_id_layout = itemView.findViewById(R.id.nursery_id_layout);
            advance_layout = itemView.findViewById(R.id.advance_layout);
            layout_sapling_pick_date = itemView.findViewById(R.id.layout_sapling_pick_date);


        }
    }
}

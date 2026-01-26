package com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter;

// AdvanceDetailsAdapter.java

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
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdvanceDetailsAdapter extends RecyclerView.Adapter<AdvanceDetailsAdapter.ViewHolder> {

    private List<AdvancedDetails> advanceList;
    private final ClickListener clickListener;
    LinkedHashMap<Integer, String> paymentmodeMap;
    private DataAccessHandler dataAccessHandler;
    private LinkedHashMap<String, String> PlantaionMethodMap;
    public interface ClickListener {
        void onItemClicked(AdvancedDetails model);
    }

    public AdvanceDetailsAdapter(List<AdvancedDetails> advanceList, ClickListener listener, LinkedHashMap<Integer, String> paymentmodeMap, DataAccessHandler dataAccessHandler) {
        this.advanceList = advanceList;
        this.clickListener = listener;
        this.paymentmodeMap = paymentmodeMap;
        this.dataAccessHandler = dataAccessHandler;
    }

    public void updateList(List<AdvancedDetails> filteredList) {
        this.advanceList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advance_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // dataAccessHandler = new DataAccessHandler();
        PlantaionMethodMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("106"));
        AdvancedDetails model = advanceList.get(position);

        String username = "";

        username = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getusernamequery(model.getCreatedByUserId()+ " "));

        //   holder.plantation_type.setText(": " + PlantaionMethodMap.get((int) model.getPlantationTypeId()));

        holder.plotCode.setText(": "+model.getPlotCode());
        holder.amountReceived.setText(": ₹" + model.getFarmerContributionReceived());

        holder.area.setText(": "+model.getAdvanceReceivedArea() + " acres");
        holder.importedSaplingsIssued.setText(": "+model.getNoOfImportedSaplingsToBeIssued());
        if(model.getNoOfIndigenousSaplingsToBeIssued() == 0) {
            holder.ll_indigenous_saplings_issued.setVisibility(View.GONE);
        }
        else {
            holder.indigenousSaplingsIssued.setText(": " + model.getNoOfIndigenousSaplingsToBeIssued());
        }
        holder.tv_survey_number.setText(": "+ model.getSurveyNumber());
        holder.created_by.setText(": "+ username);
        String comment = model.getComments();
        holder.commentLayout.setVisibility(View.GONE);
        holder.comments.setText("");

        if (comment != null && !comment.trim().isEmpty() && !comment.trim().equalsIgnoreCase("null")) {
            holder.commentLayout.setVisibility(View.VISIBLE);
            holder.comments.setText(": " + comment);
        }
        String getSurveyNumber = model.getSurveyNumber();
        if (getSurveyNumber != null && !getSurveyNumber.trim().isEmpty() && !getSurveyNumber.trim().equalsIgnoreCase("null")) {
            holder.ll_survey_number.setVisibility(View.VISIBLE);
            holder.tv_survey_number.setText(": "+ model.getSurveyNumber());
        }

        //Date
        //holder.dateReceived.setText(": "+model.getDateOfAdvanceReceived());

        String date = model.getDateOfAdvanceReceived();
        Log.d("Date Debug", "Original date: " + date);
        date = date != null ? date.trim() : null;

        if (date != null && !date.isEmpty()) {

            try {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat inputFormat3 = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                SimpleDateFormat inputFormat4 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


                Date parsedDate = null;

                if (date.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")) {

                    Log.d("Date Debug", "Matched format: YYYY-MM-DDTHH:MM:SS");
                    parsedDate = inputFormat1.parse(date);

                } else if (date.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {

                    Log.d("Date Debug", "Matched format: YYYY-MM-DD HH:MM:SS");
                    parsedDate = inputFormat2.parse(date);

                }
                else if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {

                    Log.d("Date Debug", "Matched format: YYYY-MM-DD");
                    parsedDate = inputFormat4.parse(date);

                }else if (date.matches("^\\d{2}/\\d{1,2}/\\d{4}$")) {

                    Log.d("Date Debug", "Matched format: DD/MM/YYYY");

                    parsedDate = inputFormat3.parse(date);

                    String formattedDate = outputFormat.format(parsedDate);

                    holder.dateLayout.setVisibility(View.VISIBLE);
                    holder.dateReceived.setText(": " + formattedDate);

                    return;
                } else {
                    Log.d("Date Debug", "No matching date format found.");
                }

                if (parsedDate != null) {

                    String formattedDate = outputFormat.format(parsedDate);
                    Log.d("Date Debug", "Formatted date: " + formattedDate);
                    holder.dateLayout.setVisibility(View.VISIBLE);
                    holder.dateReceived.setText(": " + formattedDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Date Debug", "Error parsing date: " + e.getMessage());
                holder.dateLayout.setVisibility(View.GONE);
            }

        } else {

            Log.d("Date Debug", "Date is null or empty.");
            holder.dateLayout.setVisibility(View.GONE);
        }


        String inputDate = model.getExpectedMonthOfPlanting(); // e.g., "2025-04-30" or "2023-08-24T11:44:39.13"

        if (inputDate != null && !inputDate.equalsIgnoreCase("null") && !inputDate.trim().isEmpty()) {
            Date parsedDate = null;

            // Possible input formats
            String[] possibleFormats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SS",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd"
            };

            for (String format : possibleFormats) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                    parsedDate = inputFormat.parse(inputDate);
                    if (parsedDate != null) break;
                } catch (ParseException ignored) {
                }
            }

            if (parsedDate != null) {
                try {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH);
                    String MonthformattedDate = outputFormat.format(parsedDate); // e.g., "April-2025"
                    holder.tv_exp_month_of_Planting.setText(": " + MonthformattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.tv_exp_month_of_Planting.setText("");
                }
            } else {
                holder.tv_exp_month_of_Planting.setText("");
                holder.ll_exp_month_of_Planting.setVisibility(View.GONE);
            }

        } else {
            holder.tv_exp_month_of_Planting.setText("");
            holder.ll_exp_month_of_Planting.setVisibility(View.GONE);
        }
        holder.receipt_no.setText(": " + model.getReceiptNumber());
//       holder.payment_mode.setText(": "+ model.getModeOfPayment());
//        holder.payment_mode.setText(": "+ paymentmodeMap.get(model.getModeOfPayment()));
        // Log raw PlantationTypeId


// Ensure PlantationTypeId is formatted properly
        String plantationTypeKey = String.valueOf((int) Double.parseDouble(String.valueOf(model.getPlantationTypeId())));
        Log.d("DEBUG", "Formatted PlantationTypeId key: " + plantationTypeKey);

// Retrieve map and value
        Map<String, String> PlantaionMethodMap = dataAccessHandler.getGenericData(Queries.getTypeCdDmtData("106"));
        Log.d("DEBUG", "PlantaionMethodMap: " + new Gson().toJson(PlantaionMethodMap));

        String plantationTypeValue = PlantaionMethodMap.get(plantationTypeKey);

// Set value with fallback
        if (plantationTypeValue != null) {
            holder.plantation_type.setText(": " + plantationTypeValue);
        } else {
            holder.plantation_type.setText(": Not Found (" + plantationTypeKey + ")");
            Log.e("ERROR", "Plantation type not found for key: " + plantationTypeKey);
        }

        holder.saplings_advance.setText(": "+ model.getNoOfSaplingsAdvancePaidFor());
/// Log raw ModeOfPayment
        Log.d("DEBUG", "Raw ModeOfPayment: " + model.getModeOfPayment());

        Map<String, String> paymentmodeMap =dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
        Log.d("DEBUG", "Payment Mode Map: " + new Gson().toJson(paymentmodeMap));

// Format key to strip any decimal
        String paymentKey = String.valueOf((int) Double.parseDouble(String.valueOf(model.getModeOfPayment())));
        Log.d("DEBUG", "Formatted payment key: " + paymentKey);

        String paymentModeValue = paymentmodeMap.get(paymentKey);

// Set text safely
        if (paymentModeValue != null) {
            holder.payment_mode.setText(": " + paymentModeValue);
        } else {
            holder.payment_mode.setText(": Not Found (" + paymentKey + ")");
            Log.e("ERROR", "Payment mode not found for key: " + paymentKey);
        }
// Set text safely
        if (paymentModeValue != null) {
            holder.payment_mode.setText(": " + paymentModeValue);
        } else {
            holder.payment_mode.setText(": Not Found (" + model.getModeOfPayment() + ")");
            Log.e("ERROR", "Payment mode not found for key: " + model.getModeOfPayment());
        }
        setViews(holder);
        holder.itemView.setOnClickListener(v -> clickListener.onItemClicked(model));
    }

    public void setViews(ViewHolder holder){
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

        holder.grower_name.setText(": " + fullname);
        holder.grower_code.setText(": " + CommonConstants.FARMER_CODE);
        holder.field_village.setText(": " + plotAuditDetailsData.get(0).getVillageName());
    }

    @Override
    public int getItemCount() {
        return advanceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plotCode, amountReceived, dateReceived, area, importedSaplingsIssued, comments , indigenousSaplingsIssued,
                receipt_no, payment_mode, plantation_type,saplings_advance,grower_code,grower_name,field_village,tv_survey_number,tv_exp_month_of_Planting,
                created_by;

        LinearLayout commentLayout,dateLayout,ll_survey_number,ll_exp_month_of_Planting,ll_indigenous_saplings_issued;

        ViewHolder(View itemView) {
            super(itemView);
            plotCode = itemView.findViewById(R.id.tv_plot_code);
            amountReceived = itemView.findViewById(R.id.tv_amount_received);
            dateReceived = itemView.findViewById(R.id.tv_date_received);
            area = itemView.findViewById(R.id.tv_area);
            importedSaplingsIssued = itemView.findViewById(R.id.tv_imported_saplings_issued);
            indigenousSaplingsIssued = itemView.findViewById(R.id.tv_indigenous_saplings_issued);
            comments = itemView.findViewById(R.id.tv_comments);
            commentLayout = itemView.findViewById(R.id.comments);
            receipt_no = itemView.findViewById(R.id.tv_receipt_no);
            payment_mode = itemView.findViewById(R.id.tv_paymnet_mode);
            plantation_type = itemView.findViewById(R.id.tv_plantation_type);
            saplings_advance = itemView.findViewById(R.id.tv_saplings_advance);
            grower_code = itemView.findViewById(R.id.tv_grower_code);
            grower_name = itemView.findViewById(R.id.tv_grower_name);
            field_village = itemView.findViewById(R.id.tv_field_village);
            dateLayout = itemView.findViewById(R.id.date);
            tv_survey_number = itemView.findViewById(R.id.tv_survey_number);
            tv_exp_month_of_Planting = itemView.findViewById(R.id.tv_exp_month_of_Planting);
            ll_survey_number = itemView.findViewById(R.id.ll_survey_number);
            created_by =itemView.findViewById(R.id.tv_created_by);
            ll_exp_month_of_Planting = itemView.findViewById(R.id.ll_exp_month_of_Planting);
            ll_indigenous_saplings_issued = itemView.findViewById(R.id.ll_indigenous_saplings_issued);
        }
    }
}



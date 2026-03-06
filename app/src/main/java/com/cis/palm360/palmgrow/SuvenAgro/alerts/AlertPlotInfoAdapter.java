package com.cis.palm360.palmgrow.SuvenAgro.alerts;

import static com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils.getProperComplaintsDate;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;

import com.cis.palm360.palmgrow.SuvenAgro.ui.RecyclerItemClickListener;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Siva on 15-11-2017.
 */


//Display Alerts for Plots Followup
public class AlertPlotInfoAdapter extends RecyclerView.Adapter<AlertPlotInfoAdapter.AlertPlotDetailsViewHolder> {

    private static final String LOG_TAG = AlertPlotInfoAdapter.class.getName();
    private List<AlertsPlotInfo> mList;
    private Context context;
    private AlertsPlotInfo item;
    private RecyclerItemClickListener recyclerItemClickListener;

    private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public AlertPlotInfoAdapter(Context context, List<AlertsPlotInfo> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addItems(List<AlertsPlotInfo> list) {
        this.mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public AlertPlotInfoAdapter.AlertPlotDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alerts_list_item, null);
        AlertPlotInfoAdapter.AlertPlotDetailsViewHolder myHolder = new AlertPlotInfoAdapter.AlertPlotDetailsViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final AlertPlotInfoAdapter.AlertPlotDetailsViewHolder holder, final int position) {
        item = mList.get(position);
        String lastName = "", middleName = "";
        if (!TextUtils.isEmpty(item.getLastName())) {
            lastName = item.getLastName();
        }
        if (!TextUtils.isEmpty(item.getMiddleName()) && !
                item.getMiddleName().equalsIgnoreCase("null")) {
            middleName = item.getMiddleName();
        }
        holder.tvfirstName.setText(item.getFirstName().trim() + " " + middleName + " " + lastName.trim() + "");

        if (!TextUtils.isEmpty(item.getContactNumber()) && !  item.getContactNumber().equalsIgnoreCase("null")) {
            holder.tvcontactNumber.setText(item.getContactNumber());
        } else {
            holder.tvcontactNumber.setText("");
        }
        holder.tvvillageName.setText(item.getVillageName() != null ? item.getVillageName().trim() : "");
        holder.tvmandalName.setText(item.getMandalName() != null ? item.getMandalName().trim() : "");
        holder.tvplotCode.setText(item.getPlotCode() != null ? item.getPlotCode().trim() : "");
        holder.tvtotalPlotArea.setText(item.getTotalPlotArea() != null ? item.getTotalPlotArea().trim() : "");
        holder.tvpotentialScore.setText(item.getPotentialScore() != null ? item.getPotentialScore().trim() : "");

// Field Prioritization
        if (item.getPrioritization() != null && !item.getPrioritization().trim().isEmpty() && !item.getPrioritization().trim().equalsIgnoreCase("null")) {
            holder.tvPrioritization.setText(item.getPrioritization().trim());
            holder.layout_field_prioritization.setVisibility(View.VISIBLE);
        } else {
            holder.layout_field_prioritization.setVisibility(View.GONE);
        }

// Government Grower Code
        if (item.getGovtFarmerCode() != null && !item.getGovtFarmerCode().trim().isEmpty() && !item.getGovtFarmerCode().trim().equalsIgnoreCase("null")) {
            holder.tv_gvtfarmercode.setText(item.getGovtFarmerCode().trim());
            holder.layout_govt_grower_code.setVisibility(View.VISIBLE);
        } else {
            holder.layout_govt_grower_code.setVisibility(View.GONE);
        }

// Grower Code
        if (item.getFarmerCode() != null && !item.getFarmerCode().trim().isEmpty() && !item.getFarmerCode().trim().equalsIgnoreCase("null")) {
            holder.tvfarmerCode.setText(item.getFarmerCode().trim());
            holder.layout_grower_code.setVisibility(View.VISIBLE);
        } else {
            holder.layout_grower_code.setVisibility(View.GONE);
        }

// Government Field Code
        if (item.getGovtPlotCode() != null && !item.getGovtPlotCode().trim().isEmpty() && !item.getGovtPlotCode().trim().equalsIgnoreCase("null")) {
            holder.gvttvPlotID.setText(item.getGovtPlotCode().trim());
            holder.layout_govt_field_code.setVisibility(View.VISIBLE);
        } else {
            holder.layout_govt_field_code.setVisibility(View.GONE);
        }

        if (item.getLastVistDate() != null && !(item.getLastVistDate().trim().isEmpty())) {
            String lvd = item.getLastVistDate().replace("T", " ");
            holder.tvlastVistDate.setText(getProperComplaintsDate(lvd));
        } else {
            holder.tvlastVistDate.setText("");
        }

        if (item.getHarvestDate() != null && !item.getHarvestDate().trim().isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(item.getHarvestDate().replace("T", " "));

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.tvharvestDate.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.tvharvestDate.setText("");
            }
        } else {
            holder.tvharvestDate.setText("");
        }

        holder.tvPrioritization.setText(item.getPrioritization() != null ? item.getPrioritization().trim() : "");
        holder.tvUserName.setText(item.getUserName() != null ? item.getUserName()  : "");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<AlertsPlotInfo> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class AlertPlotDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvplotCode;
        private TextView tvfarmerCode;
        private TextView tvfirstName;
        private TextView tvcontactNumber;
        private TextView tvmandalName;
        private TextView tvvillageName;
        private TextView tvtotalPlotArea;
        private TextView tvpotentialScore;
        private TextView tvcropName;
        private TextView tvlastVistDate;
        private TextView tvharvestDate;
        private TextView tvPrioritization,tvUserName;
        private View convertView;
        private TextView tv_gvtfarmercode,gvttvPlotID;
        LinearLayout layout_current_crop, layout_field_prioritization,
                layout_govt_grower_code, layout_grower_code, layout_govt_field_code;

        public AlertPlotDetailsViewHolder(View view) {
            super(view);
            convertView = view;

            tvplotCode = (TextView) view.findViewById(R.id.tvPlotID);
            tvfarmerCode = (TextView) view.findViewById(R.id.tv_farmercode);
            tvfirstName = (TextView) view.findViewById(R.id.tv_farmername);
            tvcontactNumber = (TextView) view.findViewById(R.id.tvContactNumber);
            tvmandalName = (TextView) view.findViewById(R.id.tvPlotMandal);
            tvvillageName = (TextView) view.findViewById(R.id.tvPlotVillage);
            tvtotalPlotArea = (TextView) view.findViewById(R.id.tvSize);
            tvpotentialScore = (TextView) view.findViewById(R.id.tvPlotScore);
            tvlastVistDate = (TextView) view.findViewById(R.id.tvLastVisistedDate);
            tvharvestDate = (TextView) view.findViewById(R.id.tvHarvestingDate);
            tvPrioritization = (TextView) view.findViewById(R.id.tvPlotPrioritization);
            tvUserName= (TextView) view.findViewById(R.id.tvUserName);
            tv_gvtfarmercode = (TextView) view.findViewById(R.id.tv_gvtfarmercode);
            gvttvPlotID = (TextView) view.findViewById(R.id.gvttvPlotID);
            layout_field_prioritization = view.findViewById(R.id.layout_field_prioritization);
            layout_govt_grower_code = view.findViewById(R.id.layout_govt_grower_code);
            layout_grower_code = view.findViewById(R.id.layout_grower_code);
            layout_govt_field_code = view.findViewById(R.id.layout_govt_field_code);

        }
    }
}

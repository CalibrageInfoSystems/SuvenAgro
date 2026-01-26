package com.cis.palm360.palmgrow.SuvenAgro.palmcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.alerts.AlertPlotInfoAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Config;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.ui.RecyclerItemClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NoVisitsInfoAdapter extends RecyclerView.Adapter<NoVisitsInfoAdapter.ViewHolder>{

    private static final String LOG_TAG = AlertPlotInfoAdapter.class.getName();
    private List<NotVisitedPlotsInfo> mList;
    private Context context;
    private NotVisitedPlotsInfo item;
    private RecyclerItemClickListener recyclerItemClickListener;

    private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public NoVisitsInfoAdapter(Context context, List<NotVisitedPlotsInfo> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addItems(List<NotVisitedPlotsInfo> list) {
        this.mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.no_visits_item, viewGroup, false);
        ViewHolder myHolder = new ViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        item = mList.get(position);

        holder.tvfirstName.setText(item.getFarmerName());
//
        holder.palbl.setText("Palm Area " + Config.UOM);

        holder.tvvillageName.setText(item.getVillageName() != null ? item.getVillageName().trim() : "");
        holder.tvPlotcluster.setText(item.getClusterName() != null ? item.getClusterName().trim() : "");
        holder.tvplotCode.setText(item.getPlotCode() != null ? item.getPlotCode().trim() : "");
        holder.tvContactNumber.setText(item.getContactNumber() != null ? item.getContactNumber().trim() : "");
        holder.tvfarmerCode.setText(item.getFarmerCode() != null ? item.getFarmerCode().trim() : "");
        holder.tvtotalPlotArea.setText(item.getTotalPalmArea() != null ? item.getTotalPalmArea().trim() : "");

//        holder.tvvisitedby.setText(item.getVisitedBy() != null ? item.getVisitedBy().trim() : "");
        String visitedby = item.getVisitedBy();
        if (visitedby != null && !visitedby.isEmpty()){
            holder.visited_by_layout.setVisibility(View.VISIBLE);
            holder.tvvisitedby.setText(item.getVisitedBy());
        }else {
            holder.visited_by_layout.setVisibility(View.GONE);
        }

        if(item.getLastvisiteddate() != null && !(item.getLastvisiteddate().trim().isEmpty())){
            String dop = item.getLastvisiteddate().replace("T"," ");

            Date date = null;
            try {
                date = inputFormat.parse(dop);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String DateOP = CommonUtils.getProperComplaintsDate(item.getLastvisiteddate());
            holder.tvplotvisiteddate.setText(DateOP);
        }
        else{
//            holder.tvplotvisiteddate.setText("");
            holder.last_visited_layout.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<NotVisitedPlotsInfo> list) {
        this.mList = list;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvplotCode;
        private TextView tvfarmerCode;
        private TextView tvfirstName;
        private TextView tvPlotcluster;
        private TextView tvvisitedby;
        private TextView tvContactNumber;
        private TextView tvvillageName;
        private TextView tvtotalPlotArea;
        private TextView tvdateofplanting;
        private TextView tvplotvisiteddate;
        private TextView palbl;

        private View convertView;
        LinearLayout visited_by_layout,last_visited_layout;

        public ViewHolder(View view) {
            super(view);
            convertView = view;

            tvplotCode = (TextView) view.findViewById(R.id.tvPlotID);
            tvfarmerCode = (TextView) view.findViewById(R.id.tv_farmercode);
            tvfirstName = (TextView) view.findViewById(R.id.tv_farmername);
            // tvPlotcluster = (TextView) view.findViewById(R.id.tvContactNumber);
            tvPlotcluster = (TextView) view.findViewById(R.id.tvPlotcluster);
            tvvillageName = (TextView) view.findViewById(R.id.tvPlotVillage);
            tvtotalPlotArea = (TextView) view.findViewById(R.id.tvSize);
            palbl = (TextView) view.findViewById(R.id.palbl);
            tvvisitedby = (TextView) view.findViewById(R.id.tvvisitedby);
            tvplotvisiteddate = (TextView) view.findViewById(R.id.tvplotvisiteddate);
            tvContactNumber = (TextView) view.findViewById(R.id.tv_contactNumber);
            visited_by_layout = view.findViewById(R.id.visited_by_layout);
            last_visited_layout = view.findViewById(R.id.last_visited_layout);
        }
    }
}

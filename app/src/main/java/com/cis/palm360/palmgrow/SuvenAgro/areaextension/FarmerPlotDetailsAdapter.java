package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Config;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.farmersearch.DisplayPlotsFragment;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlotDetailsObj;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.SelectableAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by skasam on 9/28/2016.
 */

//To Bind the farmer plot details
public class FarmerPlotDetailsAdapter extends SelectableAdapter<FarmerPlotDetailsAdapter.PlotDetailsViewHolder> {


    private Context context;
    private List<PlotDetailsObj> plotlist;
    private PlotDetailsObj plotdetailsObj;
    private ClickListener clickListener;
    private int layoutResourceId;
    private boolean showArrow;
    DataAccessHandler dataAccessHandler;
    private double currentLatitude, currentLongitude;


    public FarmerPlotDetailsAdapter(Context context, List<PlotDetailsObj> plotlist, int layoutResourceId, boolean showArrow) {
        this.context = context;
        this.plotlist = plotlist;
        this.layoutResourceId = layoutResourceId;
        this.showArrow = showArrow;
    }


    public PlotDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutResourceId, null);
        PlotDetailsViewHolder myHolder = new PlotDetailsViewHolder(view);
        return myHolder;
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(final PlotDetailsViewHolder holder, final int position) {

        dataAccessHandler = new DataAccessHandler(context);

        plotdetailsObj = plotlist.get(position);

        holder.tvplotId.setText(": " + plotdetailsObj.getPlotID());
        //  holder.tvplotId.setText("Field Code : " + plotdetailsObj.getPlotID());
        String govtPlotCode = plotdetailsObj.getGovtPlotCode();
        if (govtPlotCode != null && !govtPlotCode.trim().isEmpty() && !govtPlotCode.equalsIgnoreCase("null")) {
            holder.tvgvtplotidvalue.setText(": " + govtPlotCode);
            holder.govt_plot_id_layout.setVisibility(View.VISIBLE);
            Log.d("FarmerPlotDetails", govtPlotCode);
        } else {
            holder.govt_plot_id_layout.setVisibility(View.GONE);
        }

        if (CommonUtils.isFromFollowUp()){

            holder.plot_status_layout.setVisibility(View.VISIBLE);
            String plotstatus = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getplotStatus(plotdetailsObj.getPlotID()));
            holder.plot_Status.setText(": " + plotstatus);
        }else{
            holder.plot_status_layout.setVisibility(View.GONE);

        }



        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" );
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");


        String landMark = plotdetailsObj.getPlotLandMark();
        if (landMark != null &&!TextUtils.isEmpty(landMark) && !landMark.equalsIgnoreCase("null")) {
            holder.tvlandmark.setText(": " + plotdetailsObj.getPlotLandMark());
            holder.landmark_layout.setVisibility(View.VISIBLE);
        } else {
            holder.landmark_layout.setVisibility(View.GONE);
        }

        if (CommonUtils.isFromCropMaintenance() || CommonUtils.isComplaint() || CommonUtils.isFromHarvesting() || CommonUtils.isPlotSplitFarmerPlots()) {
            holder.plotDOP_layout.setVisibility(View.VISIBLE);
            String formattedDate = formatDate(plotdetailsObj.getDateofPlanting(), inputFormat1, inputFormat2, outputFormat);

            if (!TextUtils.isEmpty(formattedDate)) {
                holder.tvPlotDop.setText(": " + formattedDate);
            } else {
                holder.tvPlotDop.setText(": Not Visited");
            }


        } else {
            holder.plotDOP_layout.setVisibility(View.GONE);
        }

        if (CommonUtils.isFromCropMaintenance() || CommonUtils.isComplaint() || CommonUtils.isFromHarvesting() || CommonUtils.isPlotSplitFarmerPlots()) {

            holder.tvplotarea.setText(": " + plotdetailsObj.getTotalPalm() + " " + Config.UOM);
        } else {
            holder.tvplotarea.setText(": " + plotdetailsObj.getPlotArea() + " " + Config.UOM);
        }

        holder.tvplotvillage.setText(": " + plotdetailsObj.getVillageName());
        if (!TextUtils.isEmpty(plotdetailsObj.getSurveyNumber()) && !plotdetailsObj.getSurveyNumber().equalsIgnoreCase("null")) {
            holder.tvplotsurveynumber.setText(": " + plotdetailsObj.getSurveyNumber());
        } else {
            holder.survey_number_layout.setVisibility(View.GONE);

        }

        String visitCount = dataAccessHandler.getOnlyTwoValueFromDb(Queries.getInstance().getVisitCount(plotdetailsObj.getPlotID()));
        String harvestvisitCount = dataAccessHandler.getOnlyTwoValueFromDb(Queries.getInstance().getHarvestVisitCount(plotdetailsObj.getPlotID()));


        String count = visitCount.split("@")[0];
        String cmlastvisitdate = visitCount.split("@")[1];
        Log.d("CMdate", cmlastvisitdate + "");
        String harvestcount = harvestvisitCount.split("@")[0];
        String lastharvestdate = harvestvisitCount.split("@")[1];
        Log.d("Harvestdate", lastharvestdate + "");

        if (CommonUtils.isFromCropMaintenance() || CommonUtils.isFromHarvesting() || CommonUtils.isPlotSplitFarmerPlots()) {
            // Handling last visit date
            if (TextUtils.isEmpty(cmlastvisitdate) || cmlastvisitdate.equalsIgnoreCase("null")) {
                holder.lastest_vistDate.setText(": Not Visited");
            } else {
                String formattedDate = formatDate(cmlastvisitdate, inputFormat1, inputFormat2, outputFormat);
                Log.d("formattedCMDate", formattedDate + "");
                if (!TextUtils.isEmpty(formattedDate)) {
                    String[] dateTimeParts = formattedDate.split("T");
                    if (dateTimeParts.length == 2) {
                        holder.lastest_vistDate.setText(": " + dateTimeParts[0] + " " + dateTimeParts[1]);
                    } else {
                        holder.lastest_vistDate.setText(": " + formattedDate);
                    }
                } else {
                    holder.lastest_vistDate.setText(": Not Visited");
                }
            }

            // Handling last harvest date
            if (TextUtils.isEmpty(lastharvestdate) || lastharvestdate.equalsIgnoreCase("null")) {
                holder.lastest_harvestDate.setText(": Not Visited");
            } else {
                String formattedHarvestDate = formatDate(lastharvestdate, inputFormat1, inputFormat2, outputFormat);
                Log.d("formattedHarvestingDate", formattedHarvestDate + "");
                if (!TextUtils.isEmpty(formattedHarvestDate)) {
                    String[] dateTimeParts = formattedHarvestDate.split("T");
                    if (dateTimeParts.length == 2) {
                        holder.lastest_harvestDate.setText(": " + dateTimeParts[0] + " " + dateTimeParts[1]);
                    } else {
                        holder.lastest_harvestDate.setText(": " + formattedHarvestDate);
                    }
                } else {
                    holder.lastest_harvestDate.setText(": Not Visited");
                }
            }
        } else {
            holder.lastest_vistDate_layout.setVisibility(View.GONE);
            holder.lastest_harvestDate_layout.setVisibility(View.GONE);
        }

        holder.convertView.setOnClickListener(v -> {
            Log.v(FarmerPlotDetailsAdapter.class.getSimpleName(), "#### clicked position " + position);
            clickListener.onItemClicked(position, holder.convertView);
        });

        holder.arrowImage.setVisibility((showArrow) ? View.VISIBLE : View.INVISIBLE);
        holder.ivb_plot_location_cropcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonConstants.PLOT_CODE = plotdetailsObj.getPlotID();
                final String selectedLatLong = new DataAccessHandler(context).getLatLongs(Queries.getInstance().queryVerifyGeoTag());

                if (!selectedLatLong.isEmpty()) {
                    final String[] yieldDataArr = selectedLatLong.split("-");
                    final String latlong[] = new DisplayPlotsFragment().getLatLong(context, false).split("@");
                    String land_lattitude = yieldDataArr[0].replaceAll("[\\s\\-()]", "");
                    String land_longitude = yieldDataArr[1].replaceAll("[\\s\\-()]", "");
                    if (land_longitude.isEmpty()) {
                        try {
                            land_longitude = yieldDataArr[2].replaceAll("[\\s\\-()]", "");
                        } catch (Exception e) {
                            land_longitude = "00.000";
                        }
                    }

                    currentLatitude = Double.parseDouble(latlong[0]);
                    currentLongitude = Double.parseDouble(latlong[1]);
                    String uri = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "(" + "Village Name = " + plotdetailsObj.getVillageName() + "/" + "LandMark = " + plotdetailsObj.getPlotLandMark() + ")&daddr=" + land_lattitude + "," + land_longitude;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);

                } else {

                    UiUtils.showCustomToastMessage("This Field has not Lat_Long to show field on Google Map", context, 1);
                }


            }
        });

    }

    private String formatDate(String dateStr, SimpleDateFormat inputFormat1, SimpleDateFormat inputFormat2, SimpleDateFormat outputFormat) {
        Date date = null;
        try {
            if (dateStr.contains("T")) {
                date = inputFormat1.parse(dateStr);
            } else {
                date = inputFormat2.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return outputFormat.format(date);
        } else {
            return "";
        }
    }


    public int getItemCount() {
        return plotlist.size();
    }

    public static class PlotDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvplotId;
        private TextView tvlandmark,tvplotastatus;
        private TextView tvplotarea;
        private TextView tvplotvillage;
        private TextView tvplotsurveynumber, tvPlotDop;
        private TextView vist_count, lastest_vistDate, lastest_harvestDate, plot_Status,tvgvtplotidvalue;
        private ImageView arrowImage;
        private View convertView;
        private ImageButton ivb_plot_location_cropcollection;
        LinearLayout govt_plot_id_layout, plot_status_layout,landmark_layout,plotDOP_layout,
                survey_number_layout, lastest_vistDate_layout, lastest_harvestDate_layout;

        public PlotDetailsViewHolder(View view) {
            super(view);
            this.convertView = view;
            tvplotId = view.findViewById(R.id.tvplotidvalue);
            tvlandmark = view.findViewById(R.id.tvplotlandmarkvalue);
            tvplotarea = view.findViewById(R.id.tvplotareavalue);

            tvplotvillage = view.findViewById(R.id.tvplotvillagevalue);
            tvPlotDop = view.findViewById(R.id.tvplotDOP);
            tvplotsurveynumber = view.findViewById(R.id.tvplotsurveyvalue);
            arrowImage = view.findViewById(R.id.arrow_right);
            vist_count = view.findViewById(R.id.vistCount);
            lastest_vistDate = view.findViewById(R.id.lastest_vistDate);
            plot_Status = view.findViewById(R.id.plot_Status);
            tvgvtplotidvalue = view.findViewById(R.id.tvgvtplotidvalue);
            lastest_harvestDate = view.findViewById(R.id.lastest_harvestDate);
            ivb_plot_location_cropcollection = view.findViewById(R.id.ivb_plot_location_cropcollection);
            govt_plot_id_layout = view.findViewById(R.id.govt_plot_id_layout);
            plot_status_layout = view.findViewById(R.id.plot_status_layout);
            landmark_layout = view.findViewById(R.id.landmark_layout);
            plotDOP_layout = view.findViewById(R.id.plotDOP_layout);
            survey_number_layout = view.findViewById(R.id.survey_number_layout);
            lastest_vistDate_layout = view.findViewById(R.id.lastest_vistDate_layout);
            lastest_harvestDate_layout = view.findViewById(R.id.lastest_harvestDate_layout);
        }
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(int position, View view);
    }
}

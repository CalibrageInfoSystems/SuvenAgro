package com.cis.palm360.palmgrow.SuvenAgro.kras;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.AnnualTagetsKRA;

import java.util.List;

/**
 * Created by siva on 09/09/17.
 */

public class KrasItemHeaderViewBinder extends ViewBinder<KraItemHeader, KrasItemHeaderViewBinder.ViewHolder> {

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(StickyHeaderViewAdapter adapter, ViewHolder holder, int position, KraItemHeader entity, AppCompatActivity activity) {
        List<AnnualTagetsKRA> krasDataToDisplays = entity.getKraData();
        holder.tvPrefix.setText(krasDataToDisplays.get(0).getKraCode() + "-" + krasDataToDisplays.get(0).getKraName());
        holder.annualTargetTxt.setText(""+krasDataToDisplays.get(0).getAnnualTarget()+ " " + krasDataToDisplays.get(0).getUom());
        holder.annualAchievedTargetTxt.setText(""+krasDataToDisplays.get(0).getAchievedTarget()+ " " + krasDataToDisplays.get(0).getUom());
    }

    @Override
    public int getItemLayoutId(StickyHeaderViewAdapter adapter) {
        return R.layout.kra_header;
    }


    static class ViewHolder extends ViewBinder.ViewHolder {
        TextView tvPrefix, annualTargetTxt, annualAchievedTargetTxt;
        private LinearLayout yearLL;

        public ViewHolder(View rootView) {
            super(rootView);
            this.annualTargetTxt = (TextView) rootView.findViewById(R.id.annualTarget);
            this.annualAchievedTargetTxt = (TextView) rootView.findViewById(R.id.annualAchievedTarget);
            this.yearLL = (LinearLayout) rootView.findViewById(R.id.yearLL);
            this.tvPrefix = (TextView) rootView.findViewById(R.id.tv_prefix);

          /*  rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

           // KraItemViewBinder.visibleInvisible();
                }
            });*/
        }

    }
}


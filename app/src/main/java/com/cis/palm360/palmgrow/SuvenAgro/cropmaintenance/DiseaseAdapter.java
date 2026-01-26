package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.PalmDetailsEditListener;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.PlantProtectionModel;

import java.util.ArrayList;

/**
 * Created by Calibrage11 on 9/30/2016.
 */

//displays entered disease details
public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.SingleItemRowHolder> {
    ArrayList<PlantProtectionModel> itemsList;
    private Context mContext;
    private PalmDetailsEditListener palmDetailsEditListener;

    public DiseaseAdapter(Context context, ArrayList<PlantProtectionModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_disease_details, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int i) {
        holder.diseaseDetailsText.setText(itemsList.get(i).getDiseaseName());
        holder.nameText.setText(itemsList.get(i).getChemicalName());
        holder.chemicalText.setText(itemsList.get(i).getUOM());

        holder.editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                palmDetailsEditListener.onEditClicked(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {
        protected TextView diseaseDetailsText, nameText, chemicalText;
        private ImageView editView;
        public SingleItemRowHolder(View view) {
            super(view);
            this.diseaseDetailsText = (TextView) view.findViewById(R.id.diseaseDetailsText);
            this.nameText = (TextView) view.findViewById(R.id.nameText);
            this.chemicalText = (TextView) view.findViewById(R.id.chemicalText);
            this.editView = (ImageView) itemView.findViewById(R.id.editIcon);
        }
    }

    public void setEditClickListener(PalmDetailsEditListener palmDetailsEditListener) {
        this.palmDetailsEditListener = palmDetailsEditListener;
    }
}

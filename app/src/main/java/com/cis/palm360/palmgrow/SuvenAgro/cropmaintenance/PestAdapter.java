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

//used to display entered pests details in this Adapter
public class PestAdapter extends RecyclerView.Adapter<PestAdapter.SingleItemRowHolder> {
    ArrayList<PlantProtectionModel> itemsList;
    private Context mContext;
    public PalmDetailsEditListener palmDetailsEditListener;

    public PestAdapter(Context context, ArrayList<PlantProtectionModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_pest_details, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int i) {
        holder.codeText.setText(itemsList.get(i).getPestName());
        holder.pestnameText.setText(itemsList.get(i).getChemicalName());
        holder.pestchemicalText.setText(itemsList.get(i).getUOM());

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
        protected TextView codeText, pestnameText, pestchemicalText;
        private ImageView editView;
        public SingleItemRowHolder(View view) {
            super(view);
            this.codeText = (TextView) view.findViewById(R.id.pestcodeText);
            this.pestnameText = (TextView) view.findViewById(R.id.pestnameText);
            this.pestchemicalText = (TextView) view.findViewById(R.id.pestchemicalText);
            this.editView = (ImageView) itemView.findViewById(R.id.editIcon);
        }
    }

    public void setEditClickListener(PalmDetailsEditListener palmDetailsEditListener) {
        this.palmDetailsEditListener = palmDetailsEditListener;
    }
}

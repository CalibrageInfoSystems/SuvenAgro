package com.cis.palm360.palmgrow.SuvenAgro.conversion;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;

import java.util.List;

public class InterhistoryCropAdapter extends RecyclerView.Adapter<InterhistoryCropAdapter.ViewHolder> {
    private Context context;
    private List<InterCropDetails> interCropDetailsList;

    public InterhistoryCropAdapter(Context context, List<InterCropDetails> interCropDetailsList) {
        this.context = context;
        this.interCropDetailsList = interCropDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intercrop_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InterCropDetails currentItem = interCropDetailsList.get(position);
        holder.cropGrown.setText(currentItem.getCropGrown());
        holder.recommendedCrop.setText(currentItem.getRecommendedCrop());
        if (position % 2 == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.card_view.setCardBackgroundColor(context.getColor(R.color.white));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.card_view.setCardBackgroundColor(context.getColor(R.color.white2));
            }
        }
    }

    @Override
    public int getItemCount() {
        return interCropDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cropGrown, recommendedCrop;
        CardView card_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cropGrown = itemView.findViewById(R.id.intercropcropgrown);
            recommendedCrop = itemView.findViewById(R.id.intercroprecommended);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}

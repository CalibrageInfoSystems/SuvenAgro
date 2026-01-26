package com.cis.palm360.palmgrow.SuvenAgro.conversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.IdentityProof;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Calibrage10 on 9/26/2016.
 */

//Displaying the Id Proofs entered
public class IdProofsListAdapter extends RecyclerView.Adapter<IdProofsListAdapter.MyHolder> {
    private Context mContext;
    private List<IdentityProof> idProofsPair;

    private idProofsClickListener idProofsClickListener;
    private LinkedHashMap<String, String> idProofsData;
    private List<IdentityProof> identityProofsList;
    private DataAccessHandler dataAccessHandler;
    public IdProofsListAdapter(Context mContext, List<IdentityProof> idProofsPair, LinkedHashMap<String, String> idProofsData) {
        this.mContext = mContext;
        this.idProofsPair = idProofsPair;
        this.idProofsData = idProofsData;
        dataAccessHandler = new DataAccessHandler(mContext);
    }

    @Override
    public IdProofsListAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bookingView = inflater.inflate(R.layout.adapter_id_proof, null);
        MyHolder myHolder = new MyHolder(bookingView);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(IdProofsListAdapter.MyHolder holder, final int position) {
        holder.idProofName.setText(idProofsData.get(String.valueOf(idProofsPair.get(position).getIdprooftypeid())));
        holder.idProofNo.setText(idProofsPair.get(position).getIdproofnumber());

        holder.editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idProofsClickListener.onEditClicked(position);
            }
        });

        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete this ID Proof?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int currentPosition = holder.getAdapterPosition();
                            if (idProofsClickListener != null && currentPosition != RecyclerView.NO_POSITION) {
                                idProofsClickListener.onDeleteClicked(currentPosition);
                            } else {
                                UiUtils.showCustomToastMessage("Unable to delete this ID Proof", mContext.getApplicationContext(), 1);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return idProofsPair.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView idProofName, idProofNo;
        ImageView editView, deleteView;

        public MyHolder(View itemView) {
            super(itemView);
            idProofName = (TextView) itemView.findViewById(R.id.idproofName);
            idProofNo = (TextView) itemView.findViewById(R.id.idproofNo);
            editView = (ImageView) itemView.findViewById(R.id.editIcon);
            deleteView = (ImageView) itemView.findViewById(R.id.trashIcon);
        }
    }

    public void setIdProofsClickListener(idProofsClickListener idProofsClickListener) {
        this.idProofsClickListener = idProofsClickListener;
    }

    public interface idProofsClickListener {
        void onEditClicked(int position);
        void onDeleteClicked(int position);
    }

    public void updateData(List<IdentityProof> idProofsPair, LinkedHashMap<String, String> idProofsData) {
        this.idProofsPair = idProofsPair;
        this.idProofsData = idProofsData;
        notifyDataSetChanged();
    }

}

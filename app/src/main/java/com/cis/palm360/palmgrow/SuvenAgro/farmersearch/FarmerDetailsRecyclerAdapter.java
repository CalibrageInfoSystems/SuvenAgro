package com.cis.palm360.palmgrow.SuvenAgro.farmersearch;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.Log;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.BasicFarmerDetails;
import com.cis.palm360.palmgrow.SuvenAgro.ui.RecyclerItemClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


//Used to Bind the farmer details
public class FarmerDetailsRecyclerAdapter extends RecyclerView.Adapter<FarmerDetailsRecyclerAdapter.FarmerDetailsViewHolder> {

    private static final String LOG_TAG = FarmerDetailsRecyclerAdapter.class.getName();
    private List<BasicFarmerDetails> mList;
    private Context context;
    private BasicFarmerDetails item;
    private RecyclerItemClickListener recyclerItemClickListener;


    public FarmerDetailsRecyclerAdapter(Context context, List<BasicFarmerDetails> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addItems(List<BasicFarmerDetails> list) {
        this.mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public FarmerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.member_item, null);
        FarmerDetailsViewHolder myHolder = new FarmerDetailsViewHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(final FarmerDetailsViewHolder holder, final int position) {
        item = mList.get(position);
        String lastName = "", middleName = "";
        if (!TextUtils.isEmpty(item.getFarmerLastName())) {
            lastName = item.getFarmerLastName();
        }

        if (!TextUtils.isEmpty(item.getFarmerMiddleName()) && !
                item.getFarmerMiddleName().equalsIgnoreCase("null")) {
            middleName = item.getFarmerMiddleName();
        }

        holder.tvuserName.setText(item.getFarmerFirstName().trim() + " " + middleName + " " + lastName.trim() + " " + "(" + item.getFarmerCode().trim() + ")");

        if (!TextUtils.isEmpty(item.getFarmerFatherName()) && !
                item.getFarmerFatherName().equalsIgnoreCase("null")) {
            holder.fatherName.setText(item.getFarmerFatherName().trim());
        } else {
            holder.fatherName.setText("");
        }
       // holder.govtfarmerid.setText("Roja");
//        Log.d("getGovtFarmerCode",item.getGovtFarmerCode());
//                   holder.govtfarmerid.setText(item.getGovtFarmerCode().trim());



        if (TextUtils.isEmpty(item.getGovtFarmerCode()) || item.getGovtFarmerCode().equalsIgnoreCase("null") || item.getGovtFarmerCode().equalsIgnoreCase(null)) {
            holder.govtfarmerid.setVisibility(View.GONE);
//
        } else {

            holder.govtfarmerid.setText(item.getGovtFarmerCode().trim());
            Log.d("========>GovtFarmerCode",(item.getGovtFarmerCode()+""));
            holder.govtfarmerid.setVisibility(View.VISIBLE);
        }


        if (!TextUtils.isEmpty(item.getPrimaryContactNum()) && !
                item.getPrimaryContactNum().equalsIgnoreCase("null")) {
            holder.mobileNumber.setVisibility(View.VISIBLE);
            holder.mobileNumber.setText(item.getPrimaryContactNum());
        } else {
            holder.mobileNumber.setText("");
            holder.mobileNumber.setVisibility(View.GONE);
        }

        holder.villageName.setText(item.getFarmerVillageName() != null ? item.getFarmerVillageName().trim() : "");
        holder.tvuserStateName.setText(item.getFarmerStateName() != null ? item.getFarmerStateName().trim() : "");

        //   holder.userImage.setBorderColor(context.getResources().getColor(R.color.colorPrimary));

        final String imageUrl = CommonUtils.getImageUrl(item);
        Log.d("========> growerImage imageUrl ",imageUrl);
        final String localPath = item.getPhotoLocation();
        Log.d("========> growerImage localPath",localPath);
       // File imageFile = new File(item.getPhotoLocation()); // or model.fileLocation
        /*if (null != item.getPhotoLocation()) {
            File imageFile = new File(item.getPhotoLocation()); // or model.fileLocation
            if (imageFile.exists()) {
                Glide.with(holder.userImage.getContext())
                        .load(imageFile)
                        .placeholder(R.drawable.palm360_logo) // optional placeholder
                        .error(R.drawable.palm360_logo)         // optional error image
                        .into(holder.userImage);
            } else {
                holder.userImage.setImageResource(R.drawable.palm360_logo); // fallback
            }
        } else {
            holder.userImage.setImageResource(R.drawable.palm360_logo); // fallback
        }*/
        boolean isInternetAvailable = CommonUtils.isNetworkAvailable(context);

        File imageFile = (localPath != null) ? new File(localPath) : null;

        if (imageFile != null && imageFile.exists()) {
            // ✅ 1. Load local image first
            Glide.with(holder.userImage.getContext())
                    .load(imageFile)
                    .placeholder(R.drawable.palm360_logo)
                    .error(R.drawable.palm360_logo)
                    .into(holder.userImage);
        } else if (isInternetAvailable && imageUrl != null && !imageUrl.isEmpty()) {
            // ✅ 2. If local not available, load from the web
            Glide.with(holder.userImage.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.palm360_logo)
                    .error(R.drawable.palm360_logo)
                    .into(holder.userImage);
        } else {
            // ✅ 3. Final fallback (no local file, no internet, no URL)
            holder.userImage.setImageResource(R.drawable.palm360_logo);
        }



        // Check internet availability

//        boolean isInternetAvailable = CommonUtils.isNetworkAvailable(context); // You’ll need a helper method for this
//
//         if (localPath != null) {
//            // No internet — try local file
//            File imageFile = new File(localPath);
//            if (imageFile.exists()) {
//                Glide.with(holder.userImage.getContext())
//                        .load(imageFile)
//                        .placeholder(R.drawable.palm360_logo)
//                        .error(R.drawable.palm360_logo)
//                        .into(holder.userImage);
//            }
//        }
//   else if (isInternetAvailable && imageUrl != null && !imageUrl.isEmpty()) {
//       // Try loading from URL first
//       Glide.with(holder.userImage.getContext())
//               .load(imageUrl)
//               .placeholder(R.drawable.palm360_logo)
//               .error(R.drawable.palm360_logo)
//               .into(holder.userImage);
//   }  else {
//            // Nothing available
//            holder.userImage.setImageResource(R.drawable.palm360_logo);
//        }
//        Uri uri = null;
//        if (null != item.getPhotoLocation()) {
//            File photoFiles = new File(item.getPhotoLocation());
//            if (photoFiles != null) {
//                uri = Uri.fromFile(new File(item.getPhotoLocation()));
//                Log.d("uri========>",uri+"");
//                if (uri != null) {
//                    Picasso.get().load(imageUrl).into(holder.userImage, new Callback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError(Exception e) {
////                            holder.userImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
//
//                                    renderImage(imageUrl, holder.userImage);
//                                }
//                            });
//                } else {
//                    renderImage(imageUrl, holder.userImage);
//                }
//            } else {
//                renderImage(imageUrl, holder.userImage);
//            }
//        } else {
//            renderImage(imageUrl, holder.userImage);
//        }




        holder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "@@@ on item clicked");
                recyclerItemClickListener.onItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<BasicFarmerDetails> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public void renderImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError(Exception e) {
                            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.famer_profile));
                        }
                    });

        }
        else {
            Picasso.get()
                    .load(R.mipmap.ic_launcher)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
                        }
                    });

        }
    }
    private void loadImageFromStorage(String path, ImageView imageView) {
//        Glide.with(context)
//                .load(path)
//                .placeholder(R.mipmap.ic_launcher)
//                .into(imageView);
    }

    public class FarmerDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvuserName;
        private TextView mobileNumber;
        private TextView fatherName;
        private TextView villageName;
        private ImageView userImage;
        private TextView tvuserStateName;
        private View convertView;
        private  TextView govtfarmerid;

        public FarmerDetailsViewHolder(View view) {
            super(view);
            convertView = view;
            tvuserName = (TextView) view.findViewById(R.id.notif_title);
            userImage = (ImageView) view.findViewById(R.id.circularImageView);
            mobileNumber = (TextView) view.findViewById(R.id.mobile_number);
            fatherName = (TextView) view.findViewById(R.id.fatherName);
            villageName = (TextView) view.findViewById(R.id.villageName);
            tvuserStateName = (TextView) view.findViewById(R.id.stateName);
            govtfarmerid  = (TextView) view.findViewById(R.id.govtfarmerid);
        }
    }
}

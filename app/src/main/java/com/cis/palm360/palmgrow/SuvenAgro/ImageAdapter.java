//package com.cis.palm360;
//
//import android.content.Context;
//
//import android.util.Base64;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//
//public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
//
//    private List<ImageModel> images;
//    private Context context;
//
//    public ImageAdapter(Context context, List<ImageModel> images) {
//        this.context = context;
//        this.images = images;
//    }
//
//    public static class ImageViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//
//        public ImageViewHolder(View v) {
//            super(v);
//            imageView = v.findViewById(R.id.imageView);
//        }
//    }
//
//    @Override
//    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
//        return new ImageViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ImageViewHolder holder, int position) {
//        byte[] imageBytes = Base64.decode(images.get(position).getBase64Image(), Base64.DEFAULT);
//        Glide.with(context).load(imageBytes).into(holder.imageView);
//    }
//
//    @Override
//    public int getItemCount() {
//        return images.size();
//    }
//}
//

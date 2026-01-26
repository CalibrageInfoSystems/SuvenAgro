//package com.cis.palm360;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Base64;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class Addimages extends AppCompatActivity {
//    RecyclerView recyclerView;
//    ImageAdapter adapter;
//    List<ImageModel> imageList = new ArrayList<>();
//    DBHelper dbHelper;
//    static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_addimages);
//        dbHelper = new DBHelper(this);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        imageList = dbHelper.getAllImages();
//        adapter = new ImageAdapter(this, imageList);
//        recyclerView.setAdapter(adapter);
//
//        findViewById(R.id.btnAddImage).setOnClickListener(v -> showImagePickerDialog());
//    }
//
//    private void showImagePickerDialog() {
//        String[] options = {"Camera", "Gallery"};
//        new AlertDialog.Builder(this)
//                .setTitle("Select Image")
//                .setItems(options, (dialog, which) -> {
//                    if (which == 0)
//                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST);
//                    else {
//                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(intent, GALLERY_REQUEST);
//                    }
//                }).show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK || data == null) return;
//
//        Bitmap bitmap = null;
//
//        if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//        } else if (requestCode == GALLERY_REQUEST && data.getData() != null) {
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (bitmap != null) {
//            String base64 = bitmapToBase64(bitmap);
//            dbHelper.insertImage(base64);
//            imageList.add(new ImageModel(0, base64)); // 0, since ID is auto
//            adapter.notifyItemInserted(imageList.size() - 1);
//        }
//    }
//
//    private String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//    }
//
//}
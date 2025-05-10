package com.example.mophoneapp11.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.activities.ImgBBUploader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String IMGBB_API_KEY = "143d34c4d27ac5e5933227718dfa9bf9";

    private EditText etName, etPrice, etDescription, etQuantity;
    private ImageView ivImage;
    private Button btnSelectImage, btnUpload;
    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private String sellerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initViews();
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để tiếp tục", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSelectImage.setOnClickListener(v -> chooseImageFromGallery());
        btnUpload.setOnClickListener(v -> {
            if (imageUri != null) {
                fetchSellerNameAndUpload();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        etName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etProductPrice);
        etDescription = findViewById(R.id.etProductDescription);
        etQuantity = findViewById(R.id.etProductQuantity);
        ivImage = findViewById(R.id.ivProductImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnSaveProduct);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivImage.setImageURI(imageUri);
        }
    }

    private void fetchSellerNameAndUpload() {
        ProgressDialog progress = showProgress("Đang lấy thông tin người bán...");
        firestore.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    sellerName = doc.getString("name");
                    if (sellerName == null || sellerName.isEmpty()) {
                        sellerName = currentUser.getEmail();
                    }
                    progress.dismiss();
                    uploadImageToImgBB();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    showToast("Lỗi: " + e.getMessage());
                });
    }

    private void uploadImageToImgBB() {
        ProgressDialog progress = showProgress("Đang tải ảnh...");
        try {
            Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap resized = resizeBitmap(original, 1024);
            byte[] imageData = compressImage(resized);
            InputStream imageStream = new ByteArrayInputStream(imageData);

            ImgBBUploader.uploadImageToImgBB(imageStream, IMGBB_API_KEY, new ImgBBUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    progress.setMessage("Đang lưu dữ liệu...");
                    saveProductToFirestore(imageUrl, progress);
                }

                @Override
                public void onFailure(Exception e) {
                    progress.dismiss();
                    showToast("Lỗi upload ảnh: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            progress.dismiss();
            showToast("Không thể xử lý ảnh: " + e.getMessage());
        }
    }

    private void saveProductToFirestore(String imageUrl, ProgressDialog progress) {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            progress.dismiss();
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("description", description);
        product.put("quantity", quantity);
        product.put("imageUrl", imageUrl);
        product.put("seller", sellerName);
        product.put("sellerId", currentUser.getUid());

        firestore.collection("phones").add(product)
                .addOnSuccessListener(doc -> {
                    progress.dismiss();
                    showToast("Thêm sản phẩm thành công!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    showToast("Lỗi khi lưu dữ liệu: " + e.getMessage());
                });
    }

    private ProgressDialog showProgress(String message) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float) width / height;

        if (width > height) {
            width = maxSize;
            height = (int) (maxSize / ratio);
        } else {
            height = maxSize;
            width = (int) (maxSize * ratio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private byte[] compressImage(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        return out.toByteArray();
    }
}

package com.example.mophoneapp11.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mophoneapp11.R;
import com.example.mophoneapp11.models.Phone;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditProductActivity extends AppCompatActivity {

    private EditText editName, editPrice, editDescription, editSeller, editQuantity;
    private ImageView editImage;
    private Button btnSelectImage, btnSaveChanges;

    private Phone phone;
    private Uri selectedImageUri = null;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firestore;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).into(editImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editName = findViewById(R.id.editProductName);
        editPrice = findViewById(R.id.editProductPrice);
        editDescription = findViewById(R.id.editProductDescription);
        editSeller = findViewById(R.id.editProductSeller);
        editQuantity = findViewById(R.id.editProductQuantity);
        editImage = findViewById(R.id.editProductImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật sản phẩm...");
        progressDialog.setCancelable(false);

        phone = (Phone) getIntent().getSerializableExtra("phone");
        if (phone != null) {
            showPhoneInfo(phone);
        }

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSaveChanges.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String priceStr = editPrice.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String seller = editSeller.getText().toString().trim();
            String quantityStr = editQuantity.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()
                    || seller.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            progressDialog.show();

            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Nén ảnh
                    byte[] imageData = baos.toByteArray();
                    InputStream inputStream = new ByteArrayInputStream(imageData);

                    String apiKey = "143d34c4d27ac5e5933227718dfa9bf9"; // 🔴 Thay bằng API key thật

                    ImgBBUploader.uploadImageToImgBB(inputStream, apiKey, new ImgBBUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            updateProduct(name, price, description, seller, quantity, imageUrl);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Không thể xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            } else {
                updateProduct(name, price, description, seller, quantity, phone.getImageUrl());
            }
        });
    }

    private void showPhoneInfo(Phone phone) {
        editName.setText(phone.getName());
        editPrice.setText(String.valueOf(phone.getPrice()));
        editDescription.setText(phone.getDescription());
        editSeller.setText(phone.getSeller());
        editQuantity.setText(String.valueOf(phone.getQuantity()));
        Glide.with(this).load(phone.getImageUrl()).into(editImage);
    }

    private void updateProduct(String name, double price, String description, String seller, int quantity, String imageUrl) {
        Phone updatedPhone = new Phone(name, price, imageUrl, description, seller, quantity);

        firestore.collection("phones").document(phone.getId())
                .set(updatedPhone)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

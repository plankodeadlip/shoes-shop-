package com.example.mophoneapp11.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mophoneapp11.MainActivity;
import com.example.mophoneapp11.R;
import com.example.mophoneapp11.models.Phone;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProductActivity extends AppCompatActivity {

    private Button btnConfirmDelete, btnCancelDelete;
    private Phone phoneToDelete;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        btnConfirmDelete = findViewById(R.id.btnConfirmDelete);
        btnCancelDelete = findViewById(R.id.btnCancelDelete);
        firestore = FirebaseFirestore.getInstance();

        phoneToDelete = (Phone) getIntent().getSerializableExtra("phone");

        btnConfirmDelete.setOnClickListener(v -> {
            if (phoneToDelete != null && phoneToDelete.getId() != null) {
                deletePhoneById(phoneToDelete.getId());
            } else {
                Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelDelete.setOnClickListener(v -> finish());
    }

    private void deletePhoneById(String phoneId) {
        firestore.collection("phones")
                .document(phoneId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DeleteProductActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

package com.example.mophoneapp11.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mophoneapp11.MainActivity;
import com.example.mophoneapp11.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterName, etRegisterEmail, etRegisterPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etRegisterName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        saveUserToFirestore(uid, name, email);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserToFirestore(String uid, String name, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("role", "user"); // hoặc 'admin', tùy quyền

        firestore.collection("users")
                .document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Tạo cart mẫu
                    Map<String, Object> sampleCart = new HashMap<>();
                    sampleCart.put("productId", "sample_product");
                    sampleCart.put("productName", "Sản phẩm mẫu");
                    sampleCart.put("price", 0);
                    sampleCart.put("quantity", 0);
                    sampleCart.put("imageUrl", "");

                    // Tạo order mẫu
                    Map<String, Object> sampleOrder = new HashMap<>();
                    sampleOrder.put("orderId", "sample_order");
                    sampleOrder.put("total", 0);
                    sampleOrder.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                    sampleOrder.put("items", new ArrayList<>()); // rỗng hoặc chứa 1 item mẫu

                    // Lưu mẫu cart
                    firestore.collection("users").document(uid).collection("cart")
                            .document("sample").set(sampleCart);

                    // Lưu mẫu order
                    firestore.collection("users").document(uid).collection("order")
                            .document("sample").set(sampleOrder);

                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}

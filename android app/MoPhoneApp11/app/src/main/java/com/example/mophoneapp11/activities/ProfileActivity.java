package com.example.mophoneapp11.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mophoneapp11.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    tvName.setText("Họ tên: " + snapshot.getString("name"));
                    tvEmail.setText("Email: " + snapshot.getString("email"));
                }
            });
        }
    }
}

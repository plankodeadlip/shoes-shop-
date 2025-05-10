package com.example.mophoneapp11.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.activities.AddProductActivity;
import com.example.mophoneapp11.adapters.PhoneAdapter;
import com.example.mophoneapp11.models.Phone;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Phone> phoneList;
    private PhoneAdapter adapter;
    private Button addPhoneButton;

    private FirebaseFirestore firestore; // Firestore instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewPhones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        phoneList = new ArrayList<>();
        adapter = new PhoneAdapter(phoneList, getContext(), "home");
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        addPhoneButton = root.findViewById(R.id.addPhoneButton);
        addPhoneButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPhonesFromFirestore(); // Khi quay lại, luôn reload danh sách
    }

    private void loadPhonesFromFirestore() {
        firestore.collection("phones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    phoneList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Phone phone = document.toObject(Phone.class);
                        if (phone != null) {
                            phone.setId(document.getId());
                            phoneList.add(phone);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Lỗi khi tải dữ liệu Firestore", e);
                });
    }
}

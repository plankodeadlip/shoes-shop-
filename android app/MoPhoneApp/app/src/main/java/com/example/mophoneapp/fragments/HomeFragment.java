package com.example.mophoneapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mophoneapp.R;
import com.example.mophoneapp.adaptes.PhoneAdapter;
import com.example.mophoneapp.models.Phone;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Phone> phoneList;
    private PhoneAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewPhones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách điện thoại mẫu
        phoneList = new ArrayList<>();
        loadSamplePhones();  // Load dữ liệu mẫu

        adapter = new PhoneAdapter(phoneList, getContext());
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void loadSamplePhones() {
        // Dữ liệu mẫu
        List<Phone> samplePhones = new ArrayList<>();
        samplePhones.add(new Phone("iPhone 16 Pro MAX", 31000000, R.drawable.iphone, "Mô tả iPhone 16", "Nguyễn Văn A", 10));
        samplePhones.add(new Phone("Samsung Galaxy S25 Ultra", 34000000, R.drawable.samsung, "Mô tả Samsung S25", "Nguyễn Văn B", 7));
        samplePhones.add(new Phone("Xiaomi Mi 15 Ultra", 17000000, R.drawable.xiaomi, "Mô tả Xiaomi Mi 15", "Nguyễn Văn C", 15));

        // Đưa dữ liệu vào danh sách điện thoại
        phoneList.clear();
        phoneList.addAll(samplePhones);
        adapter.notifyDataSetChanged();
    }
}

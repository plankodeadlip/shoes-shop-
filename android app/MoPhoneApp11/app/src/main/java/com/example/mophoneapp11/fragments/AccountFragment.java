package com.example.mophoneapp11.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.activities.LoginActivity;
import com.example.mophoneapp11.activities.RegisterActivity;
import com.example.mophoneapp11.adapters.AccountAdapter;
import com.example.mophoneapp11.models.AccountOption;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AccountFragment extends Fragment {

    private TextView tvAccountName;
    private Button btnLogin, btnRegister, btnLogout;
    private RecyclerView rvAccountOptions;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvAccountName = view.findViewById(R.id.tvAccountName);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvAccountOptions = view.findViewById(R.id.rvAccountOptions);

        rvAccountOptions.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Nếu đã đăng nhập: hiển thị tên, ẩn nút login/register, hiện logout
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            tvAccountName.setText("Xin chào, " + name);
                        }
                    });
        } else {
            // Nếu chưa đăng nhập: hiển thị login/register, ẩn logout
            tvAccountName.setText("Bạn chưa đăng nhập");
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }

        // Xử lý nút
        btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), RegisterActivity.class)));
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        // Tùy chọn danh sách tài khoản
        List<AccountOption> optionList = new ArrayList<>();
        optionList.add(new AccountOption("Thông tin cá nhân", R.drawable.ic_profile));
        optionList.add(new AccountOption("Đơn hàng", R.drawable.ic_orders));
        optionList.add(new AccountOption("Cài đặt", R.drawable.ic_settings));
        optionList.add(new AccountOption("Đăng xuất", R.drawable.ic_logout)); // Xử lý trong adapter

        AccountAdapter adapter = new AccountAdapter(requireContext(), optionList);
        rvAccountOptions.setAdapter(adapter);

        return view;
    }
}

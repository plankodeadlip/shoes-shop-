package com.example.mophoneapp11.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.adapters.CartAdapter;
import com.example.mophoneapp11.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo danh sách giỏ hàng
        cartItemList = new ArrayList<>();

        // Khởi tạo CartAdapter với các tham số cần thiết
        cartAdapter = new CartAdapter(requireContext(), cartItemList, "cart", true);
        cartRecyclerView.setAdapter(cartAdapter);

        // Khởi tạo Firestore và FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Tải các sản phẩm trong giỏ hàng
        loadCartItems();

        // Xử lý sự kiện khi người dùng nhấn vào nút Thanh toán
        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dùng Navigation Component để chuyển trang
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.nav_checkout);
        });
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("cart")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Lỗi khi lắng nghe thay đổi giỏ hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cartItemList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if (!doc.getId().equals("sample")) {  // 👈 Bỏ qua document sample
                            CartItem item = doc.toObject(CartItem.class);
                            if (item != null) {
                                item.setProductId(doc.getId());
                                cartItemList.add(item);
                            }
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                });
    }

}

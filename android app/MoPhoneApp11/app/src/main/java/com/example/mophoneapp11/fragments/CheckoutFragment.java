package com.example.mophoneapp11.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.adapters.CartAdapter;
import com.example.mophoneapp11.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckoutFragment extends Fragment {

    private RecyclerView checkoutRecyclerView;
    private TextView totalAmountTextView, timestampTextView;
    private List<CartItem> checkoutItemList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CartAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        checkoutRecyclerView = view.findViewById(R.id.checkoutRecyclerView);
        totalAmountTextView = view.findViewById(R.id.totalAmountTextView);
        timestampTextView = view.findViewById(R.id.timestampTextView);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), checkoutItemList, auth.getCurrentUser().getUid(), true);
        checkoutRecyclerView.setAdapter(adapter);

        loadCartForCheckout();
    }

    private void loadCartForCheckout() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    checkoutItemList.clear();
                    double total = 0;

                    for (DocumentSnapshot doc : querySnapshots.getDocuments()) {
                        if (!doc.getId().equals("sample")) { // 👈 Bỏ qua document mẫu
                            CartItem item = doc.toObject(CartItem.class);
                            if (item != null) {
                                item.setProductId(doc.getId()); // 👈 Cần để tạo orderItem chính xác
                                checkoutItemList.add(item);
                                total += item.getPrice() * item.getQuantity();
                            }
                        }
                    }

                    if (checkoutItemList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có sản phẩm nào trong giỏ hàng!", Toast.LENGTH_LONG).show();
                    } else {
                        adapter.notifyDataSetChanged();
                        totalAmountTextView.setText("Tổng cộng: $" + total);

                        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                        timestampTextView.setText("Thời gian hóa đơn: " + currentTime);

                        // Thêm vào orders subcollection sau khi thanh toán
                        createOrderInFirestore(userId, total, currentTime);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show());
    }


    private void createOrderInFirestore(String userId, double total, String timestamp) {
        // Tạo order trong subcollection 'orders'
        CollectionReference ordersRef = db.collection("users").document(userId).collection("orders");

        // Tạo dữ liệu đơn hàng
        Map<String, Object> order = new HashMap<>();
        order.put("totalAmount", total);
        order.put("timestamp", timestamp);
        order.put("status", "pending"); // Ví dụ, trạng thái đơn hàng là "chờ xử lý"
        order.put("paymentMethod", "Offline"); // Ví dụ, thanh toán offline
        order.put("deliveryAddress", "Địa chỉ giao hàng");

        // Thêm đơn hàng vào Firestore
        ordersRef.add(order)
                .addOnSuccessListener(documentReference -> {
                    // Tạo subcollection cho các sản phẩm trong đơn hàng
                    createOrderItems(documentReference.getId(), userId);
                    Toast.makeText(getContext(), "Đơn hàng đã được tạo!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi tạo đơn hàng", Toast.LENGTH_SHORT).show());
    }

    private void createOrderItems(String orderId, String userId) {
        CollectionReference orderItemsRef = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .collection("orderItems");

        // Lặp qua các sản phẩm trong giỏ hàng và thêm vào subcollection 'orderItems'
        for (CartItem item : checkoutItemList) {
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productId", item.getProductId());
            orderItem.put("productName", item.getProductName());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("price", item.getPrice());
            orderItem.put("totalPrice", item.getPrice() * item.getQuantity());

            orderItemsRef.add(orderItem)
                    .addOnSuccessListener(documentReference -> {
                        // Đã thêm sản phẩm vào orderItems
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi tạo sản phẩm trong đơn hàng", Toast.LENGTH_SHORT).show());

            clearCart(userId);
        }
    }

    private void clearCart(String userId) {
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        cartRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (!doc.getId().equals("sample")) { // Tránh xóa document mẫu
                            cartRef.document(doc.getId()).delete();
                        }
                    }
                    Toast.makeText(getContext(), "Giỏ hàng đã được xóa sau khi đặt hàng!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa giỏ hàng", Toast.LENGTH_SHORT).show());
    }

}

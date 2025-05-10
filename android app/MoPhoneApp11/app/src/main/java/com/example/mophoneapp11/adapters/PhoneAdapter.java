package com.example.mophoneapp11.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mophoneapp11.R;
import com.example.mophoneapp11.activities.EditProductActivity;
import com.example.mophoneapp11.models.Phone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    private List<Phone> phoneList;
    private Context context;
    private final String source;

    public PhoneAdapter(List<Phone> phoneList, Context context, String source) {
        this.phoneList = phoneList;
        this.context = context;
        this.source = source;
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, seller, quantity;
        ImageView image;
        Button btnEdit, btnDelete, btnAddToCart, btnViewDetail;

        public PhoneViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.phoneName);
            price = itemView.findViewById(R.id.phonePrice);
            image = itemView.findViewById(R.id.phoneImage);
            description = itemView.findViewById(R.id.phoneDescription);
            seller = itemView.findViewById(R.id.phoneSeller);
            quantity = itemView.findViewById(R.id.phoneQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }
    }

    @Override
    public PhoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone, parent, false);
        return new PhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhoneViewHolder holder, int position) {
        Phone phone = phoneList.get(position);
        holder.name.setText(phone.getName());
        holder.price.setText(String.format("%,.0fđ", phone.getPrice()));
        holder.description.setText(phone.getDescription());
        holder.seller.setText("Người bán: " + phone.getSeller());
        holder.quantity.setText("Số lượng: " + phone.getQuantity());

        // Load ảnh
        if (phone.getImageResId() != 0) {
            holder.image.setImageResource(phone.getImageResId());
        } else if (phone.getImageUrl() != null && phone.getImageUrl().startsWith("http")) {
            Glide.with(context)
                    .load(phone.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.error);
        }

        // Hiển thị nút theo nguồn hiển thị
        if (source.equals("home")) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.btnViewDetail.setVisibility(View.VISIBLE);

            // Gắn sự kiện thêm vào giỏ hàng
            holder.btnAddToCart.setOnClickListener(v -> {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

                if (userId == null) {
                    Toast.makeText(context, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference cartRef = db.collection("users")
                        .document(userId)
                        .collection("cart")
                        .document(phone.getId());

                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("productId", phone.getId());
                cartItem.put("name", phone.getName());
                cartItem.put("price", phone.getPrice());
                cartItem.put("imageUrl", phone.getImageUrl());
                cartItem.put("quantity", 1); // mặc định là 1 khi mới thêm

                cartRef.set(cartItem)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show());
            });

            // Gắn sự kiện xem chi tiết
            holder.btnViewDetail.setOnClickListener(v -> {
                Toast.makeText(context, "Xem chi tiết: " + phone.getName(), Toast.LENGTH_SHORT).show();
                // TODO: mở activity hoặc dialog hiển thị chi tiết
            });

        } else if (source.equals("my_orders")) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.btnViewDetail.setVisibility(View.GONE);

            // Sửa sản phẩm
            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("phone", phone);
                context.startActivity(intent);
            });

            // Xóa sản phẩm
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("phones")
                                    .document(phone.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        int currentPosition = holder.getAdapterPosition();
                                        if (currentPosition != RecyclerView.NO_POSITION) {
                                            phoneList.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                            notifyItemRangeChanged(currentPosition, phoneList.size());
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("PhoneAdapter", "Xóa thất bại", e));
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public void updatePhoneList(List<Phone> newPhoneList) {
        this.phoneList = newPhoneList;
        notifyDataSetChanged();
    }
}

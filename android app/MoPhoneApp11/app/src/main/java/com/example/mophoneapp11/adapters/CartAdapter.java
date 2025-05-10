package com.example.mophoneapp11.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mophoneapp11.R;
import com.example.mophoneapp11.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private String userId;
    private boolean isCheckoutMode;

    // Constructor chỉ nhận vào context và danh sách sản phẩm
    public CartAdapter(Context context, List<CartItem> cartItemList, String userId, boolean isCheckoutMode) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.userId = userId;
        this.isCheckoutMode = isCheckoutMode;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.name.setText(item.getProductName());
        holder.price.setText(String.format("$%.2f", item.getPrice()));
        holder.quantity.setText("Số lượng: " + item.getQuantity());

        // Hiển thị ảnh nếu có
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        // Ẩn nút xóa khi ở chế độ Checkout
        holder.btnRemove.setVisibility(isCheckoutMode ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, price, quantity;
        View btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cartImage);
            name = itemView.findViewById(R.id.cartName);
            price = itemView.findViewById(R.id.cartPrice);
            quantity = itemView.findViewById(R.id.cartQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

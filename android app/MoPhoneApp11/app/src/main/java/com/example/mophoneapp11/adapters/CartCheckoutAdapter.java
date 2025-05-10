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

public class CartCheckoutAdapter extends RecyclerView.Adapter<CartCheckoutAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartCheckoutAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
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

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_product) // Đảm bảo drawable này tồn tại
                .into(holder.imageView);

        holder.btnRemove.setVisibility(View.GONE); // Không cho phép xóa ở checkout
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, price, quantity;
        TextView btnRemove;

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

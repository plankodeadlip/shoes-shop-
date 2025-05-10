package com.example.mophoneapp.adaptes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mophoneapp.R;
import com.example.mophoneapp.models.Phone;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    private List<Phone> phoneList;
    private Context context;

    public PhoneAdapter(List<Phone> phoneList, Context context) {
        this.phoneList = phoneList;
        this.context = context;
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, seller, quantity;
        ImageView image;

        public PhoneViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.phoneName);
            price = itemView.findViewById(R.id.phonePrice);
            image = itemView.findViewById(R.id.phoneImage);
            description = itemView.findViewById(R.id.phoneDescription);
            seller = itemView.findViewById(R.id.phoneSeller);
            quantity = itemView.findViewById(R.id.phoneQuantity);
        }
    }

    @Override
    public PhoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phone, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    // Optional: Method to update the phone list and notify the adapter
    public void updatePhoneList(List<Phone> newPhoneList) {
        this.phoneList = newPhoneList;
        notifyDataSetChanged();
    }
}

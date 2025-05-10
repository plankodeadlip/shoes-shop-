package com.example.mophoneapp11.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("Mã đơn: " + order.getId());
        holder.totalAmountTextView.setText("Tổng: $" + order.getTotalAmount());
        holder.timestampTextView.setText("Thời gian: " + order.getTimestamp());
        holder.statusTextView.setText("Trạng thái: " + order.getStatus());
        holder.paymentMethodTextView.setText("Thanh toán: " + order.getPaymentMethod());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdTextView, totalAmountTextView, timestampTextView, statusTextView, paymentMethodTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            paymentMethodTextView = itemView.findViewById(R.id.paymentMethodTextView);
        }
    }
}

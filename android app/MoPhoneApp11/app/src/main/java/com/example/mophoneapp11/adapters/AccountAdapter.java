package com.example.mophoneapp11.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mophoneapp11.R;
import com.example.mophoneapp11.activities.LoginActivity;
import com.example.mophoneapp11.activities.ProfileActivity;
import com.example.mophoneapp11.models.AccountOption;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private final Context context;
    private final List<AccountOption> optionList;

    public AccountAdapter(Context context, List<AccountOption> optionList) {
        this.context = context;
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account_option, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountOption option = optionList.get(position);
        holder.tvOptionName.setText(option.getTitle());
        holder.ivIcon.setImageResource(option.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            switch (option.getTitle()) {
                case "Thông tin cá nhân":
                    context.startActivity(new Intent(context, ProfileActivity.class));
                    break;
                case "Đơn hàng":
                    // TODO: Mở OrdersActivity
                    break;
                case "Cài đặt":
                    // TODO: Mở SettingsActivity
                    break;
                case "Đăng xuất":
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvOptionName;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvOptionName = itemView.findViewById(R.id.tvOptionName);
        }
    }
}

// app/src/main/java/com/example/smartspend/adapter/TransactionAdapter.java
package com.example.smartspend.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.R;
import com.example.smartspend.model.Transaction;
import com.example.smartspend.utils.CategoryUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);

        // Icône catégorie
        holder.tvIcon.setText(CategoryUtils.getIcon(t.category));

        // Catégorie + description
        holder.tvCategory.setText(t.category);
        holder.tvDescription.setText(t.description != null && !t.description.isEmpty()
                ? t.description : "—");

        // Date formatée
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(t.date)));

        // Montant coloré : rouge dépense, vert revenu
        if ("EXPENSE".equals(t.type)) {
            holder.tvAmount.setText(String.format(Locale.getDefault(), "-%.0f FCFA", t.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
        } else {
            holder.tvAmount.setText(String.format(Locale.getDefault(), "+%.0f FCFA", t.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Clic sur un item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTransactionClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvCategory, tvDescription, tvDate, tvAmount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon        = itemView.findViewById(R.id.tv_icon);
            tvCategory    = itemView.findViewById(R.id.tv_category);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate        = itemView.findViewById(R.id.tv_date);
            tvAmount      = itemView.findViewById(R.id.tv_amount);
        }
    }
}
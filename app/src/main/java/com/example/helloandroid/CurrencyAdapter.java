package com.example.helloandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private final List<Currency> currencyList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Currency currency);
    }

    public CurrencyAdapter(List<Currency> currencyList, OnItemClickListener listener) {
        this.currencyList = currencyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.currencyName.setText(currency.getName());
        holder.currencyRate.setText(currency.getRate());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currency));
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public void updateData(List<Currency> newList) {
        currencyList.clear();
        currencyList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView currencyName;
        public TextView currencyRate;

        public ViewHolder(View view) {
            super(view);
            currencyName = view.findViewById(R.id.currencyName);
            currencyRate = view.findViewById(R.id.currencyRate);
        }
    }
}
package com.dogus.otomat.icecdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SauceAdapter extends RecyclerView.Adapter<SauceAdapter.SauceViewHolder> {
    
    private List<SauceItem> sauceList;
    private OnSauceClickListener listener;
    
    public interface OnSauceClickListener {
        void onSauceClick(SauceItem sauce);
    }
    
    public SauceAdapter(List<SauceItem> sauceList, OnSauceClickListener listener) {
        this.sauceList = sauceList;
        this.listener = listener;
    }
    
    public void updateSauces(List<SauceItem> newSauces) {
        this.sauceList = newSauces;
        notifyDataSetChanged();
    }
    
    @Override
    public SauceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sauce, parent, false);
        return new SauceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(SauceViewHolder holder, int position) {
        SauceItem sauce = sauceList.get(position);
        holder.bind(sauce);
    }
    
    @Override
    public int getItemCount() {
        return sauceList.size();
    }
    
    class SauceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSauceName;
        private TextView tvPrice;
        private TextView tvStatus;
        
        public SauceViewHolder(View itemView) {
            super(itemView);
            tvSauceName = itemView.findViewById(R.id.tv_sauce_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onSauceClick(sauceList.get(position));
                    }
                }
            });
        }
        
        public void bind(SauceItem sauce) {
            tvSauceName.setText(sauce.getName());
            tvPrice.setText("₺" + String.format("%.2f", sauce.getPrice()));
            
            if (sauce.isAvailable()) {
                tvStatus.setText("Mevcut");
                tvStatus.setTextColor(0xFF4CAF50); // Yeşil
                itemView.setEnabled(true);
                itemView.setAlpha(1.0f);
            } else {
                tvStatus.setText("Yok");
                tvStatus.setTextColor(0xFFF44336); // Kırmızı
                itemView.setEnabled(false);
                itemView.setAlpha(0.5f);
            }
        }
    }
}

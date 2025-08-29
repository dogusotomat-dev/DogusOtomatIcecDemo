package com.dogus.otomat.icecdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder> {
    
    private List<ToppingItem> toppingList;
    private OnToppingClickListener listener;
    
    public interface OnToppingClickListener {
        void onToppingClick(ToppingItem topping);
    }
    
    public ToppingAdapter(List<ToppingItem> toppingList, OnToppingClickListener listener) {
        this.toppingList = toppingList;
        this.listener = listener;
    }
    
    public void updateToppings(List<ToppingItem> newToppings) {
        this.toppingList = newToppings;
        notifyDataSetChanged();
    }
    
    @Override
    public ToppingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topping, parent, false);
        return new ToppingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ToppingViewHolder holder, int position) {
        ToppingItem topping = toppingList.get(position);
        holder.bind(topping);
    }
    
    @Override
    public int getItemCount() {
        return toppingList.size();
    }
    
    class ToppingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvToppingName;
        private TextView tvPrice;
        private TextView tvStatus;
        
        public ToppingViewHolder(View itemView) {
            super(itemView);
            tvToppingName = itemView.findViewById(R.id.tv_topping_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onToppingClick(toppingList.get(position));
                    }
                }
            });
        }
        
        public void bind(ToppingItem topping) {
            tvToppingName.setText(topping.getName());
            tvPrice.setText("₺" + String.format("%.2f", topping.getPrice()));
            
            if (topping.isAvailable()) {
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

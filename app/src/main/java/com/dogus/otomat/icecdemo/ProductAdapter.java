package com.dogus.otomat.icecdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.tcn.icecboard.control.Coil_info;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Coil_info> productList;
    private OnProductClickListener listener;
    
    public interface OnProductClickListener {
        void onProductClick(Coil_info product);
    }
    
    public ProductAdapter(List<Coil_info> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }
    
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Coil_info product = productList.get(position);
        holder.bind(product);
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvPrice;
        private TextView tvStock;
        private TextView tvStatus;
        
        public ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvStatus = itemView.findViewById(R.id.tv_status);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onProductClick(productList.get(position));
                    }
                }
            });
        }
        
        public void bind(Coil_info product) {
            tvProductName.setText(product.getPar_name());
            tvPrice.setText("₺" + product.getPar_price());
            tvStock.setText("Stok: " + product.getExtant_quantity());
            
            String status;
            switch (product.getWork_status()) {
                case 0:
                    status = "Mevcut";
                    tvStatus.setTextColor(0xFF4CAF50); // Yeşil
                    break;
                case 255:
                    status = "Yok";
                    tvStatus.setTextColor(0xFFF44336); // Kırmızı
                    break;
                default:
                    status = "Hata: " + product.getWork_status();
                    tvStatus.setTextColor(0xFFFF9800); // Turuncu
                    break;
            }
            tvStatus.setText(status);
        }
    }
}

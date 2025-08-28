package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

/**
 * Reklam Listesi Adapter'ı
 * Reklam öğelerini liste halinde göstermek için
 */
public class AdvertisementAdapter extends ArrayAdapter<AdvertisementManager.AdvertisementItem> {
    private static final String TAG = "AdvertisementAdapter";
    private final Context context;
    private final List<AdvertisementManager.AdvertisementItem> advertisementList;
    private final LayoutInflater inflater;

    public AdvertisementAdapter(@NonNull Context context,
            @NonNull List<AdvertisementManager.AdvertisementItem> objects) {
        super(context, 0, objects);
        this.context = context;
        this.advertisementList = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_advertisement, parent, false);
                holder = new ViewHolder();

                holder.ivThumbnail = convertView.findViewById(R.id.ivThumbnail);
                holder.tvTitle = convertView.findViewById(R.id.tvTitle);
                holder.tvType = convertView.findViewById(R.id.tvType);
                holder.tvDuration = convertView.findViewById(R.id.tvDuration);
                holder.tvFileSize = convertView.findViewById(R.id.tvFileSize);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AdvertisementManager.AdvertisementItem item = getItem(position);
            if (item != null) {
                // Set title
                String title = item.getTitle();
                if (title == null || title.isEmpty()) {
                    title = item.getId();
                }
                holder.tvTitle.setText(title);

                // Set type
                String type = item.getType() == AdvertisementManager.AD_TYPE_PHOTO ? "📷 Fotoğraf" : "🎥 Video";
                holder.tvType.setText(type);

                // Set duration
                String duration = (item.getDuration() / 1000) + " saniye";
                holder.tvDuration.setText(duration);

                // Set file size
                String fileSize = getFileSize(item.getFilePath());
                holder.tvFileSize.setText(fileSize);

                // Load thumbnail
                loadThumbnail(holder.ivThumbnail, item);
            }

            return convertView;

        } catch (Exception e) {
            Log.e(TAG, "getView error at position " + position + ": " + e.getMessage(), e);
            // Return a simple text view in case of error
            TextView errorView = new TextView(context);
            errorView.setText("Hata: " + e.getMessage());
            return errorView;
        }
    }

    /**
     * Thumbnail yükle
     */
    private void loadThumbnail(ImageView imageView, AdvertisementManager.AdvertisementItem item) {
        try {
            if (item.getType() == AdvertisementManager.AD_TYPE_PHOTO) {
                // Fotoğraf için thumbnail yükle
                loadPhotoThumbnail(imageView, item.getFilePath());
            } else {
                // Video için varsayılan video ikonu göster
                imageView.setImageResource(R.drawable.ic_video_placeholder);
            }
        } catch (Exception e) {
            Log.e(TAG, "Thumbnail loading error: " + e.getMessage(), e);
            // Hata durumunda varsayılan ikon göster
            imageView.setImageResource(R.drawable.ic_image);
        }
    }

    /**
     * Fotoğraf thumbnail'ı yükle
     */
    private void loadPhotoThumbnail(ImageView imageView, String filePath) {
        try {
            // Background thread'de thumbnail yükle
            new Thread(() -> {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        // Bitmap'i yükle ve boyutlandır
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4; // Boyutu küçült
                        options.inJustDecodeBounds = false;

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                        if (bitmap != null) {
                            // UI thread'de image view'ı güncelle
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                imageView.setImageBitmap(bitmap);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            });
                        } else {
                            // Hata durumunda varsayılan ikon göster
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                imageView.setImageResource(R.drawable.ic_image);
                            });
                        }
                    } else {
                        // Dosya bulunamadı durumunda varsayılan ikon göster
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            imageView.setImageResource(R.drawable.ic_image);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Photo thumbnail loading error: " + e.getMessage(), e);
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        imageView.setImageResource(R.drawable.ic_image);
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Photo thumbnail setup error: " + e.getMessage(), e);
            imageView.setImageResource(R.drawable.ic_image);
        }
    }

    /**
     * Dosya boyutunu hesapla
     */
    private String getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                long sizeInBytes = file.length();
                return formatFileSize(sizeInBytes);
            } else {
                return "Bilinmiyor";
            }
        } catch (Exception e) {
            Log.e(TAG, "File size calculation error: " + e.getMessage(), e);
            return "Bilinmiyor";
        }
    }

    /**
     * Dosya boyutunu formatla
     */
    private String formatFileSize(long sizeInBytes) {
        try {
            if (sizeInBytes < 1024) {
                return sizeInBytes + " B";
            } else if (sizeInBytes < 1024 * 1024) {
                return String.format("%.1f KB", sizeInBytes / 1024.0);
            } else if (sizeInBytes < 1024 * 1024 * 1024) {
                return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
            } else {
                return String.format("%.1f GB", sizeInBytes / (1024.0 * 1024.0 * 1024.0));
            }
        } catch (Exception e) {
            Log.e(TAG, "File size formatting error: " + e.getMessage(), e);
            return "Bilinmiyor";
        }
    }

    /**
     * ViewHolder sınıfı
     */
    private static class ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvType;
        TextView tvDuration;
        TextView tvFileSize;
    }

    @Override
    public int getCount() {
        return advertisementList.size();
    }

    @Nullable
    @Override
    public AdvertisementManager.AdvertisementItem getItem(int position) {
        if (position >= 0 && position < advertisementList.size()) {
            return advertisementList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Adapter'ı temizle
     */
    public void clear() {
        advertisementList.clear();
        notifyDataSetChanged();
    }

    /**
     * Yeni öğeler ekle
     */
    public void addAll(List<AdvertisementManager.AdvertisementItem> items) {
        advertisementList.addAll(items);
        notifyDataSetChanged();
    }
}

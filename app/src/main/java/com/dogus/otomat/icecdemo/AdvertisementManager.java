package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reklam Yönetim Sistemi
 * Fotoğraf ve video reklamları için kapsamlı yönetim
 */
public class AdvertisementManager {
    private static final String TAG = "AdvertisementManager";
    private static final String PREFS_NAME = "AdvertisementSettings";

    // Reklam türleri
    public static final int AD_TYPE_PHOTO = 1;
    public static final int AD_TYPE_VIDEO = 2;

    // Varsayılan ayarlar
    private static final int DEFAULT_PHOTO_DURATION = 5000; // 5 saniye
    private static final int DEFAULT_VIDEO_DURATION = 15000; // 15 saniye
    private static final int DEFAULT_TRANSITION_DURATION = 1000; // 1 saniye
    private static final int DEFAULT_CYCLE_DELAY = 3000; // 3 saniye

    private static AdvertisementManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // Reklam listesi
    private List<AdvertisementItem> advertisementList;
    private int currentAdIndex = 0;
    private boolean isAdvertisementActive = false;
    private boolean isAutoPlayEnabled = true;

    // Zamanlama ayarları
    private int photoDuration = DEFAULT_PHOTO_DURATION;
    private int videoDuration = DEFAULT_VIDEO_DURATION;
    private int transitionDuration = DEFAULT_TRANSITION_DURATION;
    private int cycleDelay = DEFAULT_CYCLE_DELAY;

    // UI referansları
    private ImageView photoImageView;
    private VideoView videoView;
    private View advertisementContainer;

    // Callback interface
    public interface OnAdvertisementListener {
        void onAdvertisementStarted(AdvertisementItem item);

        void onAdvertisementCompleted(AdvertisementItem item);

        void onAdvertisementError(String error);

        void onTransitionStarted();

        void onTransitionCompleted();
    }

    private OnAdvertisementListener advertisementListener;

    private AdvertisementManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.advertisementList = new ArrayList<>();

        loadSettings();
        loadAdvertisements();
    }

    public static synchronized AdvertisementManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdvertisementManager(context);
        }
        return instance;
    }

    /**
     * Reklam listener'ı ayarla
     */
    public void setAdvertisementListener(OnAdvertisementListener listener) {
        this.advertisementListener = listener;
    }

    /**
     * UI referanslarını ayarla
     */
    public void setUIReferences(ImageView photoView, VideoView videoView, View container) {
        this.photoImageView = photoView;
        this.videoView = videoView;
        this.advertisementContainer = container;
    }

    /**
     * Ayarları yükle
     */
    private void loadSettings() {
        try {
            photoDuration = prefs.getInt("photo_duration", DEFAULT_PHOTO_DURATION);
            videoDuration = prefs.getInt("video_duration", DEFAULT_VIDEO_DURATION);
            transitionDuration = prefs.getInt("transition_duration", DEFAULT_TRANSITION_DURATION);
            cycleDelay = prefs.getInt("cycle_delay", DEFAULT_CYCLE_DELAY);
            isAutoPlayEnabled = prefs.getBoolean("auto_play_enabled", true);

            Log.i(TAG, "Reklam ayarları yüklendi");
        } catch (Exception e) {
            Log.e(TAG, "Ayar yükleme hatası: " + e.getMessage());
        }
    }

    /**
     * Reklamları yükle
     */
    private void loadAdvertisements() {
        try {
            advertisementList.clear();

            // Fotoğraf reklamları
            File photoDir = new File(context.getFilesDir(), "advertisements/photos");
            if (photoDir.exists()) {
                File[] photoFiles = photoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png"));

                if (photoFiles != null) {
                    for (File file : photoFiles) {
                        AdvertisementItem item = new AdvertisementItem();
                        item.setId(file.getName());
                        item.setType(AD_TYPE_PHOTO);
                        item.setFilePath(file.getAbsolutePath());
                        item.setDuration(photoDuration);
                        advertisementList.add(item);
                    }
                }
            }

            // Video reklamları
            File videoDir = new File(context.getFilesDir(), "advertisements/videos");
            if (videoDir.exists()) {
                File[] videoFiles = videoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4") ||
                        name.toLowerCase().endsWith(".avi") ||
                        name.toLowerCase().endsWith(".mov"));

                if (videoFiles != null) {
                    for (File file : videoFiles) {
                        AdvertisementItem item = new AdvertisementItem();
                        item.setId(file.getName());
                        item.setType(AD_TYPE_VIDEO);
                        item.setFilePath(file.getAbsolutePath());
                        item.setDuration(videoDuration);
                        advertisementList.add(item);
                    }
                }
            }

            Log.i(TAG, "Reklamlar yüklendi: " + advertisementList.size() + " adet");

        } catch (Exception e) {
            Log.e(TAG, "Reklam yükleme hatası: " + e.getMessage());
        }
    }

    /**
     * Reklam oynatmayı başlat
     */
    public void startAdvertisement() {
        if (advertisementList.isEmpty()) {
            Log.w(TAG, "Gösterilecek reklam bulunamadı");
            if (advertisementListener != null) {
                advertisementListener.onAdvertisementError("Gösterilecek reklam bulunamadı");
            }
            return;
        }

        if (isAdvertisementActive) {
            Log.w(TAG, "Reklam zaten aktif");
            return;
        }

        isAdvertisementActive = true;
        currentAdIndex = 0;

        mainHandler.post(() -> playCurrentAdvertisement());
    }

    /**
     * Reklam oynatmayı durdur
     */
    public void stopAdvertisement() {
        isAdvertisementActive = false;

        mainHandler.post(() -> {
            if (photoImageView != null) {
                photoImageView.setVisibility(View.GONE);
            }
            if (videoView != null) {
                videoView.stopPlayback();
                videoView.setVisibility(View.GONE);
            }
        });

        Log.i(TAG, "Reklam oynatma durduruldu");
    }

    /**
     * Mevcut reklamı oynat
     */
    private void playCurrentAdvertisement() {
        if (!isAdvertisementActive || advertisementList.isEmpty()) {
            return;
        }

        AdvertisementItem currentItem = advertisementList.get(currentAdIndex);

        if (advertisementListener != null) {
            advertisementListener.onAdvertisementStarted(currentItem);
        }

        if (currentItem.getType() == AD_TYPE_PHOTO) {
            playPhotoAdvertisement(currentItem);
        } else if (currentItem.getType() == AD_TYPE_VIDEO) {
            playVideoAdvertisement(currentItem);
        }
    }

    /**
     * Fotoğraf reklamını oynat
     */
    private void playPhotoAdvertisement(AdvertisementItem item) {
        try {
            if (photoImageView == null)
                return;

            // Video view'ı gizle
            if (videoView != null) {
                videoView.setVisibility(View.GONE);
            }

            // Fotoğrafı yükle ve göster
            executorService.execute(() -> {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(item.getFilePath());
                    if (bitmap != null) {
                        mainHandler.post(() -> {
                            photoImageView.setImageBitmap(bitmap);
                            photoImageView.setVisibility(View.VISIBLE);

                            // Belirtilen süre sonra sonraki reklama geç
                            mainHandler.postDelayed(() -> {
                                if (isAdvertisementActive) {
                                    showTransition();
                                }
                            }, item.getDuration());
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Fotoğraf yükleme hatası: " + e.getMessage());
                    mainHandler.post(() -> playNextAdvertisement());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Fotoğraf reklam oynatma hatası: " + e.getMessage());
            playNextAdvertisement();
        }
    }

    /**
     * Video reklamını oynat
     */
    private void playVideoAdvertisement(AdvertisementItem item) {
        try {
            if (videoView == null)
                return;

            // Fotoğraf view'ı gizle
            if (photoImageView != null) {
                photoImageView.setVisibility(View.GONE);
            }

            // Video'yu yükle ve oynat
            videoView.setVideoPath(item.getFilePath());
            videoView.setVisibility(View.VISIBLE);

            videoView.setOnPreparedListener(mp -> {
                mp.start();

                // Video süresi sonunda sonraki reklama geç
                mainHandler.postDelayed(() -> {
                    if (isAdvertisementActive) {
                        showTransition();
                    }
                }, item.getDuration());
            });

            videoView.setOnCompletionListener(mp -> {
                if (isAdvertisementActive) {
                    showTransition();
                }
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Video oynatma hatası: what=" + what + ", extra=" + extra);
                playNextAdvertisement();
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Video reklam oynatma hatası: " + e.getMessage());
            playNextAdvertisement();
        }
    }

    /**
     * Geçiş efekti göster
     */
    private void showTransition() {
        if (advertisementListener != null) {
            advertisementListener.onTransitionStarted();
        }

        // Geçiş süresi sonunda sonraki reklama geç
        mainHandler.postDelayed(() -> {
            if (isAdvertisementActive) {
                if (advertisementListener != null) {
                    advertisementListener.onTransitionCompleted();
                }
                playNextAdvertisement();
            }
        }, transitionDuration);
    }

    /**
     * Sonraki reklama geç
     */
    private void playNextAdvertisement() {
        currentAdIndex++;

        // Döngü tamamlandıysa başa dön
        if (currentAdIndex >= advertisementList.size()) {
            currentAdIndex = 0;

            // Döngü arası bekleme
            if (cycleDelay > 0) {
                mainHandler.postDelayed(() -> {
                    if (isAdvertisementActive) {
                        playCurrentAdvertisement();
                    }
                }, cycleDelay);
                return;
            }
        }

        playCurrentAdvertisement();
    }

    /**
     * Reklam ekle
     */
    public boolean addAdvertisement(String filePath, int type) {
        try {
            File sourceFile = new File(filePath);
            if (!sourceFile.exists()) {
                Log.e(TAG, "Kaynak dosya bulunamadı: " + filePath);
                return false;
            }

            // Hedef dizini oluştur
            String targetDir = type == AD_TYPE_PHOTO ? "photos" : "videos";
            File targetDirectory = new File(context.getFilesDir(), "advertisements/" + targetDir);
            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs();
            }

            // Dosyayı kopyala
            File targetFile = new File(targetDirectory, sourceFile.getName());
            copyFile(sourceFile, targetFile);

            // Reklam listesine ekle
            AdvertisementItem item = new AdvertisementItem();
            item.setId(targetFile.getName());
            item.setType(type);
            item.setFilePath(targetFile.getAbsolutePath());
            item.setDuration(type == AD_TYPE_PHOTO ? photoDuration : videoDuration);

            advertisementList.add(item);

            Log.i(TAG, "Reklam eklendi: " + targetFile.getName());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Reklam ekleme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reklam kaldır
     */
    public boolean removeAdvertisement(String adId) {
        try {
            for (int i = 0; i < advertisementList.size(); i++) {
                AdvertisementItem item = advertisementList.get(i);
                if (item.getId().equals(adId)) {
                    // Dosyayı sil
                    File file = new File(item.getFilePath());
                    if (file.exists()) {
                        file.delete();
                    }

                    // Listeden kaldır
                    advertisementList.remove(i);

                    Log.i(TAG, "Reklam kaldırıldı: " + adId);
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            Log.e(TAG, "Reklam kaldırma hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dosya kopyala
     */
    private void copyFile(File source, File destination) throws IOException {
        try (InputStream in = new FileInputStream(source);
                FileOutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    /**
     * Ayar güncelle
     */
    public void updateSetting(String key, Object value) {
        try {
            SharedPreferences.Editor editor = prefs.edit();

            if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            }

            editor.apply();

            // Runtime değerleri güncelle
            switch (key) {
                case "photo_duration":
                    photoDuration = (Integer) value;
                    break;
                case "video_duration":
                    videoDuration = (Integer) value;
                    break;
                case "transition_duration":
                    transitionDuration = (Integer) value;
                    break;
                case "cycle_delay":
                    cycleDelay = (Integer) value;
                    break;
                case "auto_play_enabled":
                    isAutoPlayEnabled = (Boolean) value;
                    break;
            }

            Log.i(TAG, "Ayar güncellendi: " + key + " = " + value);

        } catch (Exception e) {
            Log.e(TAG, "Ayar güncelleme hatası: " + e.getMessage());
        }
    }

    /**
     * Reklam listesini döndür
     */
    public List<AdvertisementItem> getAdvertisementList() {
        return new ArrayList<>(advertisementList);
    }

    /**
     * Mevcut ayarları döndür
     */
    public AdvertisementSettings getCurrentSettings() {
        AdvertisementSettings settings = new AdvertisementSettings();
        settings.setPhotoDuration(photoDuration);
        settings.setVideoDuration(videoDuration);
        settings.setTransitionDuration(transitionDuration);
        settings.setCycleDelay(cycleDelay);
        settings.setAutoPlayEnabled(isAutoPlayEnabled);
        return settings;
    }

    /**
     * Reklam durumunu kontrol et
     */
    public boolean isAdvertisementActive() {
        return isAdvertisementActive;
    }

    /**
     * Otomatik oynatma durumunu kontrol et
     */
    public boolean isAutoPlayEnabled() {
        return isAutoPlayEnabled;
    }

    /**
     * Reklam öğesi sınıfı
     */
    public static class AdvertisementItem {
        private String id;
        private int type;
        private String filePath;
        private int duration;
        private String title;
        private String description;

        // Getter ve Setter metodları
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Reklam ayarları sınıfı
     */
    public static class AdvertisementSettings {
        private int photoDuration;
        private int videoDuration;
        private int transitionDuration;
        private int cycleDelay;
        private boolean autoPlayEnabled;

        // Getter ve Setter metodları
        public int getPhotoDuration() {
            return photoDuration;
        }

        public void setPhotoDuration(int photoDuration) {
            this.photoDuration = photoDuration;
        }

        public int getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(int videoDuration) {
            this.videoDuration = videoDuration;
        }

        public int getTransitionDuration() {
            return transitionDuration;
        }

        public void setTransitionDuration(int transitionDuration) {
            this.transitionDuration = transitionDuration;
        }

        public int getCycleDelay() {
            return cycleDelay;
        }

        public void setCycleDelay(int cycleDelay) {
            this.cycleDelay = cycleDelay;
        }

        public boolean isAutoPlayEnabled() {
            return autoPlayEnabled;
        }

        public void setAutoPlayEnabled(boolean autoPlayEnabled) {
            this.autoPlayEnabled = autoPlayEnabled;
        }
    }
}

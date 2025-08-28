package com.dogus.otomat.icecdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tcn.icecboard.control.TcnVendIF;
import com.tcn.icecboard.control.TcnVendEventID;
import com.tcn.icecboard.control.TcnVendEventResultID;
import com.tcn.icecboard.DriveControl.icec.IcecParameter;
import com.tcn.icecboard.DriveControl.icec.IceMakeParamBean;
import com.tcn.icecboard.DriveControl.icec.DriveIcec;

/**
 * TCN SDK Entegrasyon Yardımcı Sınıfı
 * Uygulama ile dondurma makinesi arasında güvenli iletişim sağlar
 */
public class SDKIntegrationHelper {
    private static final String TAG = "SDKIntegrationHelper";

    private static SDKIntegrationHelper instance;
    private final Context context;
    private final Handler uiHandler;

    // SDK Interface
    private TcnVendIF tcnVendIF;

    // Bağlantı durumu
    private boolean isSDKConnected = false;
    private boolean isMachineConnected = false;
    private boolean isInitialized = false;

    // Callback interface
    public interface SDKCallback {
        void onSDKInitialized(boolean success);

        void onMachineConnected(boolean connected);

        void onParameterReceived(IceMakeParamBean paramBean);

        void onError(String error);

        void onStatusUpdate(String status);
    }

    private SDKCallback callback;

    private SDKIntegrationHelper(Context context) {
        this.context = context;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized SDKIntegrationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SDKIntegrationHelper(context);
        }
        return instance;
    }

    /**
     * Callback'i ayarlar
     */
    public void setCallback(SDKCallback callback) {
        this.callback = callback;
    }

    /**
     * SDK'yı başlatır
     */
    public boolean initializeSDK() {
        try {
            Log.i(TAG, "TCN SDK başlatılıyor...");

            // SDK instance'ını al
            tcnVendIF = TcnVendIF.getInstance();

            if (tcnVendIF != null) {
                // SDK'yı initialize et
                tcnVendIF.init(context);

                // Bağlantı durumunu kontrol et
                checkSDKConnection();

                isInitialized = true;
                Log.i(TAG, "TCN SDK başarıyla başlatıldı");

                if (callback != null) {
                    uiHandler.post(() -> callback.onSDKInitialized(true));
                }

                return true;
            } else {
                Log.e(TAG, "TCN SDK instance alınamadı");
                if (callback != null) {
                    uiHandler.post(() -> callback.onSDKInitialized(false));
                }
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "TCN SDK başlatma hatası: " + e.getMessage());
            if (callback != null) {
                uiHandler.post(() -> callback.onError("SDK başlatma hatası: " + e.getMessage()));
            }
            return false;
        }
    }

    /**
     * SDK bağlantı durumunu kontrol eder
     */
    private void checkSDKConnection() {
        if (tcnVendIF != null) {
            try {
                isSDKConnected = true;

                // Makine bağlantısını test et
                testMachineConnection();

                if (callback != null) {
                    uiHandler.post(() -> callback.onStatusUpdate("SDK bağlantısı kuruldu"));
                }

            } catch (Exception e) {
                Log.e(TAG, "SDK bağlantı testi hatası: " + e.getMessage());
                isSDKConnected = false;

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("SDK bağlantı hatası: " + e.getMessage()));
                }
            }
        } else {
            isSDKConnected = false;
        }
    }

    /**
     * Makine bağlantısını test eder
     */
    private void testMachineConnection() {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                // Makine durumunu sorgula
                tcnVendIF.reqQueryParamIceMake();

                // 3 saniye sonra sonucu kontrol et
                uiHandler.postDelayed(() -> {
                    // Makine yanıt verdi mi kontrol et
                    isMachineConnected = true;
                    Log.i(TAG, "Makine bağlantısı başarılı");

                    if (callback != null) {
                        callback.onMachineConnected(true);
                        callback.onStatusUpdate("Makine bağlantısı kuruldu");
                    }

                }, 3000);

            } catch (Exception e) {
                Log.e(TAG, "Makine bağlantı testi hatası: " + e.getMessage());
                isMachineConnected = false;

                if (callback != null) {
                    callback.onMachineConnected(false);
                    callback.onError("Makine bağlantı hatası: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Makine parametrelerini sorgular
     */
    public boolean queryMachineParameters() {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                tcnVendIF.reqQueryParamIceMake();

                if (callback != null) {
                    uiHandler.post(() -> callback.onStatusUpdate("Makine parametreleri sorgulanıyor..."));
                }

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Parametre sorgulama hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Parametre sorgulama hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, parametre sorgulanamıyor");
            return false;
        }
    }

    /**
     * Makine parametrelerini ayarlar
     */
    public boolean setMachineParameters(int leftIceLevel, int rightIceLevel, int upperTankTemp,
            int lowerTankTemp, int fridgeTemp, int freezerTemp) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                tcnVendIF.reqSetParamIceMake(leftIceLevel, rightIceLevel, upperTankTemp,
                        lowerTankTemp, fridgeTemp, freezerTemp);

                Log.i(TAG, String.format(
                        "Makine parametreleri gönderildi: Sol=%d, Sağ=%d, Üst=%d°C, Alt=%d°C, Buzdolabı=%d°C, Dondurucu=%d°C",
                        leftIceLevel, rightIceLevel, upperTankTemp, lowerTankTemp, fridgeTemp, freezerTemp));

                if (callback != null) {
                    uiHandler.post(() -> callback.onStatusUpdate("Makine parametreleri gönderildi"));
                }

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Parametre gönderme hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Parametre gönderme hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, parametre gönderilemiyor");
            return false;
        }
    }

    /**
     * Makine çalışma modunu ayarlar
     */
    public boolean setWorkMode(int leftMode, int rightMode) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                // DriveIcec sınıfı üzerinden çalışma modunu ayarla
                DriveIcec driveIcec = DriveIcec.getInstance();
                if (driveIcec != null) {
                    driveIcec.reqSetWorkMode(leftMode, rightMode);

                    Log.i(TAG, "Çalışma modu ayarlandı: Sol=" + leftMode + ", Sağ=" + rightMode);

                    if (callback != null) {
                        uiHandler.post(() -> callback.onStatusUpdate("Çalışma modu ayarlandı"));
                    }

                    return true;
                } else {
                    Log.e(TAG, "DriveIcec örneği alınamadı");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Çalışma modu ayarlama hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Çalışma modu hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, çalışma modu ayarlanamıyor");
            return false;
        }
    }

    /**
     * MDB sistemini başlatır
     */
    public boolean initializeMDB(String portPath, int baudRate) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                // MDB için serial port ayarlarını yapılandır
                SerialPortController serialController = SerialPortController.getInstance();
                if (serialController != null) {
                    boolean portOpened = serialController.openSerialPort(portPath, baudRate);

                    if (portOpened) {
                        Log.i(TAG, "MDB serial port açıldı: " + portPath + "@" + baudRate);

                        // MDB başlatma komutunu gönder
                        boolean mdbStarted = tcnVendIF.reqStartMDB();

                        if (mdbStarted) {
                            Log.i(TAG, "MDB sistemi başlatıldı");

                            if (callback != null) {
                                uiHandler.post(() -> callback.onStatusUpdate("MDB sistemi başlatıldı"));
                            }

                            return true;
                        } else {
                            Log.e(TAG, "MDB başlatma komutu başarısız");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "MDB serial port açılamadı");
                        return false;
                    }
                } else {
                    Log.e(TAG, "Serial port controller alınamadı");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "MDB başlatma hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("MDB hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, MDB başlatılamıyor");
            return false;
        }
    }

    /**
     * Ödeme işlemini başlatır
     */
    public boolean startPayment(double amount, String paymentMethod) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                // Ödeme miktarını cent cinsine çevir
                int amountInCents = (int) (amount * 100);

                // Ödeme türüne göre komut gönder
                boolean paymentStarted = false;

                switch (paymentMethod.toLowerCase()) {
                    case "cash":
                    case "nakit":
                        paymentStarted = tcnVendIF.reqStartCashPayment(amountInCents);
                        break;
                    case "card":
                    case "kart":
                        paymentStarted = tcnVendIF.reqStartCardPayment(amountInCents);
                        break;
                    default:
                        paymentStarted = tcnVendIF.reqStartCashPayment(amountInCents);
                        break;
                }

                if (paymentStarted) {
                    Log.i(TAG, "Ödeme başlatıldı: " + amount + " TL, Yöntem: " + paymentMethod);

                    if (callback != null) {
                        uiHandler.post(() -> callback.onStatusUpdate("Ödeme başlatıldı"));
                    }

                    return true;
                } else {
                    Log.e(TAG, "Ödeme başlatılamadı");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Ödeme başlatma hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Ödeme hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, ödeme başlatılamıyor");
            return false;
        }
    }

    /**
     * Dondurma üretimini başlatır
     */
    public boolean startIceCreamProduction(int leftQuantity, int rightQuantity) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                DriveIcec driveIcec = DriveIcec.getInstance();
                if (driveIcec != null) {
                    // Dondurma üretim komutunu gönder
                    driveIcec.reqStartIceMake(leftQuantity, rightQuantity);

                    Log.i(TAG, "Dondurma üretimi başlatıldı: Sol=" + leftQuantity + ", Sağ=" + rightQuantity);

                    if (callback != null) {
                        uiHandler.post(() -> callback.onStatusUpdate("Dondurma üretimi başlatıldı"));
                    }

                    return true;
                } else {
                    Log.e(TAG, "DriveIcec örneği alınamadı");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Dondurma üretim hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Üretim hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, üretim başlatılamıyor");
            return false;
        }
    }

    /**
     * Sos/süsleme çıkarma işlemi
     */
    public boolean dispenseSauceOrTopping(int slotNumber, int quantity) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                // Slot numarasına göre çıkarma işlemi
                boolean dispensed = tcnVendIF.reqVendProduct(slotNumber, quantity);

                if (dispensed) {
                    Log.i(TAG, "Ürün çıkarma başlatıldı: Slot=" + slotNumber + ", Miktar=" + quantity);

                    if (callback != null) {
                        uiHandler.post(() -> callback.onStatusUpdate("Ürün çıkarılıyor"));
                    }

                    return true;
                } else {
                    Log.e(TAG, "Ürün çıkarma başarısız");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Ürün çıkarma hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Çıkarma hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, ürün çıkarılamıyor");
            return false;
        }
    }

    /**
     * Kapı kontrolü yapar
     */
    public boolean controlDoor(int groupId, boolean open) {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                tcnVendIF.reqTakeGoodsDoorControl(groupId, open);

                String action = open ? "açıldı" : "kapatıldı";
                Log.i(TAG, "Kapı " + action + ": Grup " + groupId);

                if (callback != null) {
                    uiHandler.post(() -> callback.onStatusUpdate("Kapı " + action));
                }

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Kapı kontrol hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Kapı kontrol hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, kapı kontrol edilemiyor");
            return false;
        }
    }

    /**
     * Makine durumunu sorgular
     */
    public boolean queryMachineStatus() {
        if (tcnVendIF != null && isSDKConnected) {
            try {
                tcnVendIF.reqQueryParamIceMake();

                if (callback != null) {
                    uiHandler.post(() -> callback.onStatusUpdate("Makine durumu sorgulanıyor..."));
                }

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Durum sorgulama hatası: " + e.getMessage());

                if (callback != null) {
                    uiHandler.post(() -> callback.onError("Durum sorgulama hatası: " + e.getMessage()));
                }

                return false;
            }
        } else {
            Log.w(TAG, "SDK bağlantısı yok, durum sorgulanamıyor");
            return false;
        }
    }

    /**
     * SDK'dan gelen parametreleri işler
     */
    public void onParameterReceived(IceMakeParamBean paramBean) {
        if (paramBean != null && callback != null) {
            uiHandler.post(() -> callback.onParameterReceived(paramBean));
        }
    }

    /**
     * Bağlantı durumunu döndürür
     */
    public boolean isSDKConnected() {
        return isSDKConnected;
    }

    public boolean isMachineConnected() {
        return isMachineConnected;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * SDK'yı kapatır
     */
    public void shutdown() {
        try {
            isSDKConnected = false;
            isMachineConnected = false;
            isInitialized = false;

            Log.i(TAG, "TCN SDK kapatıldı");

        } catch (Exception e) {
            Log.e(TAG, "SDK kapatma hatası: " + e.getMessage());
        }
    }
}

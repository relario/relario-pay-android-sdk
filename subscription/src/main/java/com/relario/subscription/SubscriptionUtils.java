package com.relario.subscription;

import static android.os.Build.*;

import android.content.Context;
import android.telephony.SmsManager;

import com.google.gson.Gson;
import com.relario.subscription.models.NewTransaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

public class SubscriptionUtils {

    public static final String UNIQUE_WORK_NAME = "RelarioSubscription";
    public static final String SMS_COUNT_INPUT_KEY = "smsCount";
    public static final String PRODUCT_ID_INPUT_KEY = "productId";
    public static final String PRODUCT_NAME_INPUT_KEY = "productName";
    public static final String CUSTOMER_ID_INPUT_KEY = "customerId";


    public static final String SHARED_PREFS_FILE = "RelarioSubFile";
    public static final String WORKER_RESULT_KEY = "SubscriptionWorkerResult";

    public static final String API_KEY = "api_key";

    public static final OkHttpClient HttpClient = new OkHttpClient();
    public static final Gson JsonUtil = new Gson();
    public static final ExecutorService SdkExecutor = Executors.newSingleThreadExecutor();

    /**
     * Safely fetches the appropriate SmsManager instance based on the device's Android version.
     * This prevents using deprecated APIs on Android 12+ (API 31+).
     */
    public static SmsManager getSmsManager(Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            return context.getSystemService(SmsManager.class);
        } else {
            //noinspection deprecation
            return SmsManager.getDefault();
        }
    }

    public static void validateTransactionParams(int smsCount, String productId, String productName, String customerId) throws IllegalArgumentException {
        NewTransaction temporaryTx = new NewTransaction();
        temporaryTx.setSmsCount(smsCount);
        temporaryTx.setProductId(productId);
        temporaryTx.setProductName(productName);
        temporaryTx.setCustomerId(customerId);

        // Call your centralized verification logic once
        if (!temporaryTx.isValid()) {
            throw new IllegalArgumentException("Transaction initialization failed: " +
                    "Ensure smsCount > 0 and all parameters (productId, productName, customerId) are non-empty strings.");
        }
    }
}

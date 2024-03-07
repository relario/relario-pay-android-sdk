package com.relario.subscription;

import com.google.gson.Gson;

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
}

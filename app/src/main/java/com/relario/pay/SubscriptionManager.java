package com.relario.pay;

import static com.relario.pay.SubscriptionUtils.PRODUCT_ID_INPUT_KEY;
import static com.relario.pay.SubscriptionUtils.PRODUCT_NAME_INPUT_KEY;
import static com.relario.pay.SubscriptionUtils.SMS_COUNT_INPUT_KEY;
import static com.relario.pay.SubscriptionUtils.UNIQUE_WORK_NAME;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SubscriptionManager {

    public void subscribe(Context context, int interval, String timeUnit, int smsCount, String productId, String productName) {
        WorkManager workManager = WorkManager.getInstance(context);
        Data.Builder data = new Data.Builder()
                .putInt(SMS_COUNT_INPUT_KEY, smsCount)
                .putString(PRODUCT_ID_INPUT_KEY, productId)
                .putString(PRODUCT_NAME_INPUT_KEY, productName);
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(SubscriptionWorker.class, interval, TimeUnit.valueOf(timeUnit))
                .setInputData(data.build())
                .build();

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME + "-" + productId, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWorkRequest);
    }

    public void cancelSubscription(Context context, String productId) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME + "-" + productId);
    }

}

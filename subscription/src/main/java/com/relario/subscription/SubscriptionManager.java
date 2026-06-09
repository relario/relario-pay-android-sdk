package com.relario.subscription;

import static androidx.core.content.ContextCompat.RECEIVER_EXPORTED;
import static com.relario.subscription.SubscriptionUtils.CUSTOMER_ID_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.JsonUtil;
import static com.relario.subscription.SubscriptionUtils.PRODUCT_ID_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.PRODUCT_NAME_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.SHARED_PREFS_FILE;
import static com.relario.subscription.SubscriptionUtils.SMS_COUNT_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.UNIQUE_WORK_NAME;
import static com.relario.subscription.SubscriptionUtils.WORKER_RESULT_KEY;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.relario.subscription.models.Transaction;
import com.relario.subscription.models.NewTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import androidx.work.OneTimeWorkRequest;

public class SubscriptionManager {
    private static final String TAG = SubscriptionManager.class.getSimpleName();
    private final Context context;

    public SubscriptionManager(Context context) {
        this.context = context;
    }

    public SubscriptionManager(Context context, String apiKey) {
        this.context = context;
        RelarioApi.storeApiKey(context, apiKey);
    }

    private void storeSubscriptionConfiguration(int interval, String timeUnit, int smsCount, String productId, String productName, String customerId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("interval", interval);
        editor.putString("timeUnit", timeUnit);
        editor.putInt(SMS_COUNT_INPUT_KEY, smsCount);
        editor.putString(PRODUCT_ID_INPUT_KEY, productId);
        editor.putString(PRODUCT_NAME_INPUT_KEY, productName);
        editor.putString(CUSTOMER_ID_INPUT_KEY, customerId);
        editor.apply();
    }

    public void subscribe() {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        int interval = sharedPref.getInt("interval", 24);
        int smsCount = sharedPref.getInt(SMS_COUNT_INPUT_KEY, 10);
        String timeUnit = sharedPref.getString("timeUnit", null);
        String productId = sharedPref.getString(PRODUCT_ID_INPUT_KEY, null);
        String productName = sharedPref.getString(PRODUCT_NAME_INPUT_KEY, null);
        String customerId = sharedPref.getString(CUSTOMER_ID_INPUT_KEY, null);
        if (productId != null) {
            subscribe(interval, timeUnit, smsCount, productId, productName, customerId);
        }

    }

    public void subscribe(int interval, String timeUnit, int smsCount, String productId, String productName, String customerId) {
        SubscriptionUtils.validateTransactionParams(smsCount, productId, productName, customerId);

        WorkManager workManager = WorkManager.getInstance(context);
        storeSubscriptionConfiguration(interval, timeUnit, smsCount, productId, productName, customerId);
        Data data = new Data.Builder()
                .putInt(SMS_COUNT_INPUT_KEY, smsCount)
                .putString(PRODUCT_ID_INPUT_KEY, productId)
                .putString(PRODUCT_NAME_INPUT_KEY, productName)
                .putString(CUSTOMER_ID_INPUT_KEY, customerId)
                .build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(SubscriptionWorker.class, interval, TimeUnit.valueOf(timeUnit))
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME + "-" + productId, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWorkRequest);
        Log.i(TAG, "Work Manager enqueued with SMS count " + smsCount + " to run every " + interval + " Unit: " + timeUnit);
    }

    public void cancelSubscription(String productId) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME + "-" + productId);
        Log.i(TAG, "Unsubscribing to: " + UNIQUE_WORK_NAME + "-" + productId);

    }

    public void sendSms(String[] phoneNumberList, String smsBody) {
        SmsManager smsManager = SubscriptionUtils.getSmsManager(context);
        Stream.of(phoneNumberList).forEach((nbr) -> {
            smsManager.sendTextMessage("+" + nbr, null, smsBody, null, null);
        });
    }

    public void sendSms(String[] phoneNumberList, String smsBody, SmsStatusCallback callback) {
        SmsManager smsManager = SubscriptionUtils.getSmsManager(context);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> failedNumbers = Collections.synchronizedList(new ArrayList<>());
        // Define action strings for intents

        final String SENT_ACTION_PREFIX = "SMS_SENT_ACTION_";
        final String DELIVERED_ACTION_PREFIX = "SMS_DELIVERED_ACTION_";

        for (int i = 0; i < phoneNumberList.length; i++) {
            String nbr = phoneNumberList[i];

            // Unique action strings for each message
            String sentAction = SENT_ACTION_PREFIX + i;
            String deliveredAction = DELIVERED_ACTION_PREFIX + i;

            // PendingIntents with unique actions
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(sentAction), PendingIntent.FLAG_IMMUTABLE);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(deliveredAction), PendingIntent.FLAG_IMMUTABLE);

            // Register BroadcastReceiver for each message sent
            BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                            successCount.incrementAndGet();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(context, "SMS send failed", Toast.LENGTH_SHORT).show();
                            failureCount.incrementAndGet();
                            break;
                    }
                    // Unregister this BroadcastReceiver
                    context.unregisterReceiver(this);
                    // Check if all messages have been processed
                    if ((successCount.get() + failureCount.get()) == phoneNumberList.length) {
                        callback.onBatchSmsStatus(successCount.get(), failureCount.get());
                    }
                }
            };
            ContextCompat.registerReceiver(context, smsSentReceiver, new IntentFilter(sentAction), RECEIVER_EXPORTED);

            // Send the SMS
            smsManager.sendTextMessage("+" + nbr, null, smsBody, sentPI, deliveredPI);
        }
    }

    /**
     * Dispatches the call on a background thread without caring about catching the server response.
     */
    public void purchaseOneTime(int smsCount, String productId, String productName, String customerId) {
        purchaseOneTime(smsCount, productId, productName, customerId, null);
    }

    /**
     * Dispatches the call on a background thread and returns the server's response.
     */
    public void purchaseOneTime(int smsCount, String productId, String productName, String customerId, PurchaseCallback callback) {
        SubscriptionUtils.validateTransactionParams(smsCount, productId, productName, customerId);

        SubscriptionUtils.SdkExecutor.execute(() -> {
            try {
                NewTransaction newTransaction = new NewTransaction();
                newTransaction.setSmsCount(smsCount);
                newTransaction.setProductId(productId);
                newTransaction.setProductName(productName);
                newTransaction.setCustomerId(customerId);
                newTransaction.setCustomerIpAddress(RelarioApi.getCurrentIP());

                Transaction transaction = RelarioApi.createTransaction(context, newTransaction);

                if (callback != null) {
                    callback.onSuccess(transaction);
                }

                // Save the transaction log locally so "Check Status" can find it later
                storeOneTimeResult(transaction.getTransactionId());

                SmsManager smsManager = SubscriptionUtils.getSmsManager(context);
                if (transaction.getPhoneNumbersList() != null) {
                    transaction.getPhoneNumbersList().forEach((nbr) -> {
                        smsManager.sendTextMessage("+" + nbr, null, transaction.getSmsBody() + " Date: " + new Date(), null, null); //
                    });
                }

            } catch (Exception e) {
                // Forward any API/network failures to the client app
                if (callback != null) {
                    callback.onError(e);
                } else {
                    Log.e(TAG, "One-time purchase failed", e);
                }
            }
        });
    }

    /**
     * Helper method to ensure one-time logs are saved identically to the worker logs,
     * allowing retrieveTransactions() to fetch updates for one-time purchases too.
     */
    private void storeOneTimeResult(String transactionId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE); //
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()); //
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //
        String dateTime = dateFormat.format(new Date()); //

        String entry = transactionId + "_" + dateTime; //
        String existingLog = sharedPref.getString(WORKER_RESULT_KEY, ""); //
        String updatedLog = existingLog.isEmpty() ? entry : existingLog + "|" + entry; //

        sharedPref.edit().putString(WORKER_RESULT_KEY, updatedLog).apply(); //
    }

    /**
     * Public method for external developers to check the real-time status
     * of a single transaction ID from the server.
     *
     * @param transactionId The unique ID of the transaction to check.
     * @param callback      The callback to handle the transaction result or error.
     */
    public void checkTransactionStatus(String transactionId, TransactionDetailsCallback callback) {
        if (transactionId == null || transactionId.isEmpty()) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("Transaction ID cannot be null or empty"));
            }
            return;
        }

        SubscriptionUtils.SdkExecutor.execute(() -> {
            try {
                Transaction transaction = RelarioApi.getTransaction(context, transactionId);

                if (callback != null) {
                    callback.onResult(transaction);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Asynchronously retrieves full transaction execution histories.
     * Handles background execution threading natively to prevent NetworkOnMainThreadException.
     *
     * @param callback The callback to receive the compiled JSON array string or handling exceptions.
     */
    public void retrieveTransactions(TransactionHistoryCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }

        // Reuse your background executor logic to isolate network pipes safely
        new Thread(() -> {
            try {
                // Call the original heavy processing loop method
                String jsonResult = retrieveTransactions();

                // Deliver the result
                callback.onResult(jsonResult);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    private String retrieveTransactions() {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String transactionLog = sharedPref.getString(WORKER_RESULT_KEY, "");

        List<Transaction> transactions = new ArrayList<>();

        if (!transactionLog.isEmpty()) {
            String[] entries = transactionLog.split("\\|");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (String entry : entries) {
                String[] parts = entry.split("_");
                if (parts.length == 2) {
                    String transactionId = parts[0];
                    try {
                        Date transactionDate = dateFormat.parse(parts[1]);
                        Transaction transaction = RelarioApi.getTransaction(context, transactionId);
                        if (transaction != null) {
                            transactions.add(transaction);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace(); // Handle parse exception
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return JsonUtil.toJson(transactions);
    }
}

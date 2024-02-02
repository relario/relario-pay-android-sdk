package com.relario.subscription;

import static com.relario.subscription.SubscriptionUtils.CUSTOMER_ID_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.PRODUCT_ID_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.PRODUCT_NAME_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.SHARED_PREFS_FILE;
import static com.relario.subscription.SubscriptionUtils.SMS_COUNT_INPUT_KEY;
import static com.relario.subscription.SubscriptionUtils.WORKER_RESULT_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.relario.subscription.models.NewTransaction;
import com.relario.subscription.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SubscriptionWorker extends Worker {

    private static final String TAG = SubscriptionWorker.class.getSimpleName();

    public SubscriptionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i(TAG, "Doing work in the background");
        Context context = getApplicationContext();
        String currentIP = RelarioApi.getCurrentIP();
        Log.i(TAG, "Current IP is: " + currentIP);
        NewTransaction newTransaction = new NewTransaction();
        newTransaction.setCustomerIpAddress(currentIP);
        newTransaction.setSmsCount(getInputData().getInt(SMS_COUNT_INPUT_KEY, 1));
        newTransaction.setProductId(getInputData().getString(PRODUCT_ID_INPUT_KEY));
        newTransaction.setProductName(getInputData().getString(PRODUCT_NAME_INPUT_KEY));
        newTransaction.setCustomerId(getInputData().getString(CUSTOMER_ID_INPUT_KEY));
        try {
            Transaction transaction = RelarioApi.createTransaction(context, newTransaction);
            Log.i(TAG, "Transaction result: " + transaction.toString());
            Log.i(TAG, "Sending sms to " + transaction.phoneNumbersList);
            Log.i(TAG, "Message Body is [" + transaction.smsBody + "]");
            storeResult(transaction.transactionId);
            SmsManager smsManager = SmsManager.getDefault();
            transaction.phoneNumbersList.forEach((nbr) -> {
                smsManager.sendTextMessage("+" + nbr, null, transaction.smsBody + " Date: " + new Date(), null, null);
            });
            return Result.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void storeResult(String transactionId) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);

        // Get the current date and time in UTC
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateTime = dateFormat.format(new Date());

        // Format the entry
        String entry = transactionId + "_" + dateTime;

        // Retrieve the existing log and append the new entry
        String existingLog = sharedPref.getString(WORKER_RESULT_KEY, "");
        String updatedLog = existingLog.isEmpty() ? entry : existingLog + "|" + entry;

        // Store the updated log
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(WORKER_RESULT_KEY, updatedLog);
        editor.apply();
    }
}

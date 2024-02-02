package com.relario.pay;

import static com.relario.pay.SubscriptionUtils.PRODUCT_ID_INPUT_KEY;
import static com.relario.pay.SubscriptionUtils.PRODUCT_NAME_INPUT_KEY;
import static com.relario.pay.SubscriptionUtils.SMS_COUNT_INPUT_KEY;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.relario.pay.models.NewTransaction;
import com.relario.pay.models.Transaction;

public class SubscriptionWorker extends Worker {

    private static final String TAG = SubscriptionWorker.class.getSimpleName();

    public SubscriptionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        String currentIP = RelarioApi.getCurrentIP();
        NewTransaction newTransaction = new NewTransaction();
        newTransaction.setCustomerIpAddress(currentIP);
        newTransaction.setSmsCount(getInputData().getInt(SMS_COUNT_INPUT_KEY, 1));
        newTransaction.setProductId(getInputData().getString(PRODUCT_ID_INPUT_KEY));
        newTransaction.setProductName(getInputData().getString(PRODUCT_NAME_INPUT_KEY));
        Transaction transaction = RelarioApi.createTransaction(context, newTransaction);
        Log.d(TAG, "Working in the background");
        Log.i(TAG, "Sending sms to " + transaction.phoneNumbersList);
        Log.i(TAG, "Message Body is [" + transaction.smsBody + "]");

        Data.Builder data = new Data.Builder();
        data.putString("transactionId", transaction.transactionId);
        return Result.success(data.build());
    }
}

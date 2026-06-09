package com.relario.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.relario.subscription.PurchaseCallback;
import com.relario.subscription.SubscriptionManager;
import com.relario.subscription.TransactionHistoryCallback;
import com.relario.subscription.models.Transaction;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscriptionManager = new SubscriptionManager(this, "api-key");

        Button testButton = findViewById(R.id.btn_test_sdk);
        if (testButton != null) {
            testButton.setOnClickListener(v -> checkPermissionAndRunTest());
        }

        Button statusButton = findViewById(R.id.btn_check_status);
        if (statusButton != null) {
            statusButton.setOnClickListener(v -> checkServerStatuses());
        }
    }

    private void checkPermissionAndRunTest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if we don't have it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            // We have permission, trigger the SDK
            triggerSdkFunctions();
        }
    }

    private void triggerSdkFunctions() {
        Toast.makeText(this, "Triggering SDK...", Toast.LENGTH_SHORT).show();

        // UNCOMMENT WHICH TEST OPTION YOU WANT TO TRY

        // One time purchase with callback
//        subscriptionManager.purchaseOneTime(1, "prod_999", "Premium Access", "user_11", new PurchaseCallback() {
//            @Override
//            public void onSuccess(Transaction transaction) {
//                // You received the transaction back from the API!
//                String freshId = transaction.getTransactionId();
//                Log.i("DummyApp", "Successfully created transaction! ID is: " + freshId);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("DummyApp", "Purchase submission error", e);
//            }
//        });

        // One time purchase without callback
//        subscriptionManager.purchaseOneTime(
//                1,           // smsCount
//                "prod_123",  // productId
//                "Test Sub",  // productName
//                "cust_999"   // customerId
//        );

        // Subscription based approach, available to cancel via productId
//        subscriptionManager.subscribe(
//                10,          // interval
//                "MINUTES",   // timeUnit
//                1,           // smsCount
//                "prod_123",  // productId
//                "Test Sub",  // productName
//                "cust_999"   // customerId
//        );
//        subscriptionManager.cancelSubscription("prod_123");

        Log.i("DemoApp", "Sent from API! Completed: ");
    }

    private void checkServerStatuses() {
        Toast.makeText(this, "Checking server for transaction updates...", Toast.LENGTH_SHORT).show();

        subscriptionManager.retrieveTransactions(new TransactionHistoryCallback() {
            @Override
            public void onResult(String jsonResult) {
                Log.i("DemoAppStatus", "Historical Data: " + jsonResult);
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Check Logcat for 'DemoAppStatus'", Toast.LENGTH_LONG).show()
                );

            }

            @Override
            public void onError(Exception e) {
                Log.e("DemoAppStatus", "Failed to retrieve history logs", e);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            triggerSdkFunctions();
        } else {
            Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
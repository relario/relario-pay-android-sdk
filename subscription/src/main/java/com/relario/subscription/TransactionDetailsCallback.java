package com.relario.subscription;

import com.relario.subscription.models.Transaction;

public interface TransactionDetailsCallback {
    void onResult(Transaction transaction);
    void onError(Exception e);
}
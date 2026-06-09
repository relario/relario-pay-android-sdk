package com.relario.subscription;

import com.relario.subscription.models.Transaction;

public interface PurchaseCallback {
    void onSuccess(Transaction transaction);

    void onError(Exception e);
}
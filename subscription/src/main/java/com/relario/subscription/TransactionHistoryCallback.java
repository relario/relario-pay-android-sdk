package com.relario.subscription;

public interface TransactionHistoryCallback {
    void onResult(String jsonResult);
    void onError(Exception e);
}
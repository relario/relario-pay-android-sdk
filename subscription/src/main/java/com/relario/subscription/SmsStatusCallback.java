package com.relario.subscription;

import java.util.List;

public interface SmsStatusCallback {
    void onBatchSmsStatus(int successCount, int failureCount);
}
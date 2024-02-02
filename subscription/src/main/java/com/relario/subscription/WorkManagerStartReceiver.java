package com.relario.subscription;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WorkManagerStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SubscriptionManager subscriptionManager = new SubscriptionManager(context);
        subscriptionManager.subscribe();

    }
}
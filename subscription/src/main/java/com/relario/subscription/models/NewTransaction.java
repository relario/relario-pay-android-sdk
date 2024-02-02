package com.relario.subscription.models;

import androidx.annotation.NonNull;

public class NewTransaction {

    private final String paymentType = "sms";
    private int smsCount;
    private String productId;
    private String productName;
    private String customerIpAddress;
    private String customerMccMnc;
    private String customerId;


    public boolean isValid() {
        return smsCount > 0 && productId != null && productName != null && customerId != null
                && !productId.isEmpty() && !productName.isEmpty() && !customerId.isEmpty();
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerIpAddress() {
        return customerIpAddress;
    }

    public void setCustomerIpAddress(String customerIpAddress) {
        this.customerIpAddress = customerIpAddress;
    }

    public String getCustomerMccMnc() {
        return customerMccMnc;
    }

    public void setCustomerMccMnc(String customerMccMnc) {
        this.customerMccMnc = customerMccMnc;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}

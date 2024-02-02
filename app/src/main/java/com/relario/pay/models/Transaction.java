package com.relario.pay.models;

import java.util.Collections;
import java.util.List;

public class Transaction {
    public String transactionId;
    public int merchantId;
    public String productId;
    public String productName = null;
    public String customerId;
    public String paymentType = "sms";
    public List<Payment> payments = Collections.emptyList();
    public int callDuration = 0;
    public String customerIpAddress;
    public String customerMsisdn = null;
    public String customerMccmnc = null;
    public String customerCountryCode = null;
    public String customerLanguage = null;
    public String audioFileName = null;
    public String clickToCallUrl = null;
    public String androidClickToSmsUrl = null;
    public String phoneNumber = null;
    public List<String> phoneNumbersList = Collections.emptyList();
    public String smsBody = null;
    public int smsCount = 0;
    public String status;
    public boolean test;
    public long createdAt;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(int callDuration) {
        this.callDuration = callDuration;
    }

    public String getCustomerIpAddress() {
        return customerIpAddress;
    }

    public void setCustomerIpAddress(String customerIpAddress) {
        this.customerIpAddress = customerIpAddress;
    }

    public String getCustomerMsisdn() {
        return customerMsisdn;
    }

    public void setCustomerMsisdn(String customerMsisdn) {
        this.customerMsisdn = customerMsisdn;
    }

    public String getCustomerMccmnc() {
        return customerMccmnc;
    }

    public void setCustomerMccmnc(String customerMccmnc) {
        this.customerMccmnc = customerMccmnc;
    }

    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public String getCustomerLanguage() {
        return customerLanguage;
    }

    public void setCustomerLanguage(String customerLanguage) {
        this.customerLanguage = customerLanguage;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getClickToCallUrl() {
        return clickToCallUrl;
    }

    public void setClickToCallUrl(String clickToCallUrl) {
        this.clickToCallUrl = clickToCallUrl;
    }

    public String getAndroidClickToSmsUrl() {
        return androidClickToSmsUrl;
    }

    public void setAndroidClickToSmsUrl(String androidClickToSmsUrl) {
        this.androidClickToSmsUrl = androidClickToSmsUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public void setPhoneNumbersList(List<String> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

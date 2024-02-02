package com.relario.pay.models;

public class Payment {
    public int id;
    public int transactionId;
    public String cli;
    public String cliMccmnc = null;
    public String ddi;
    public String smsBody = null;
    public int callDuration = 0;
    public long initiatedAt;
    public String ipnStatus;
    public boolean billable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getCli() {
        return cli;
    }

    public void setCli(String cli) {
        this.cli = cli;
    }

    public String getCliMccmnc() {
        return cliMccmnc;
    }

    public void setCliMccmnc(String cliMccmnc) {
        this.cliMccmnc = cliMccmnc;
    }

    public String getDdi() {
        return ddi;
    }

    public void setDdi(String ddi) {
        this.ddi = ddi;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public int getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(int callDuration) {
        this.callDuration = callDuration;
    }

    public long getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(long initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public String getIpnStatus() {
        return ipnStatus;
    }

    public void setIpnStatus(String ipnStatus) {
        this.ipnStatus = ipnStatus;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }
}

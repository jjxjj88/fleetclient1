package com.headwin.fleet.pojo;

/**
 * Created by lnc on 2017/7/6.
 */

public class UpdateStatus {
    public String getLogisticalCode() {
        return logisticalCode;
    }

    public void setLogisticalCode(String logisticalCode) {
        this.logisticalCode = logisticalCode;
    }

    public String getContactPosition() {
        return contactPosition;
    }

    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    public int getLogisticalStatus() {
        return logisticalStatus;
    }

    public void setLogisticalStatus(int logisticalStatus) {
        this.logisticalStatus = logisticalStatus;
    }

    public int getCurrentLogisticalStatus() {
        return currentLogisticalStatus;
    }

    public void setCurrentLogisticalStatus(int currentLogisticalStatus) {
        this.currentLogisticalStatus = currentLogisticalStatus;
    }

    String logisticalCode;
    String contactPosition;
    int logisticalStatus;
    int currentLogisticalStatus;
}

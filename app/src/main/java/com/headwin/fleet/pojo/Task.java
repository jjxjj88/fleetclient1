package com.headwin.fleet.pojo;

/**
 * Created by lnc on 2017/6/29.
 */

public class Task {

    private String logisticalCode;
    private String accountCode;
    private String contactInfo;
    private String containerNo;
    private String sealNo;
    private String rytryh;
    private String vessel;
    private String voyage;
    private String bookingNo;
    private String containerInfo;
    private String fleetName;
    private String fleetContact;
    private String fleetMobileNo;
    private String firstTime;
    private String expectFactoryTime;
    private String factoryName;
    private String factoryAddress;
    private String factoryContact;
    private String factoryMobileNo;
    private String factoryPhone;
    private String yardCode;
    private String yardName;
    private String wharfCode;
    private String wharfName;
    private int logisticalStatus;
    private double tare;
    private String goodsDescription;
    private String remark;

    public String getLogisticalCode() {
        return logisticalCode;
    }

    public void setLogisticalCode(String logisticalCode) {
        this.logisticalCode = logisticalCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public String getRytryh() {
        return rytryh;
    }

    public void setRytryh(String rytryh) {
        this.rytryh = rytryh;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getContainerInfo() {
        return containerInfo;
    }

    public void setContainerInfo(String containerInfo) {
        this.containerInfo = containerInfo;
    }

    public String getFleetName() {
        return fleetName;
    }

    public void setFleetName(String fleetName) {
        this.fleetName = fleetName;
    }

    public String getFleetContact() {
        return fleetContact;
    }

    public void setFleetContact(String fleetContact) {
        this.fleetContact = fleetContact;
    }

    public String getFleetMobileNo() {
        return fleetMobileNo;
    }

    public void setFleetMobileNo(String fleetMobileNo) {
        this.fleetMobileNo = fleetMobileNo;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getExpectFactoryTime() {
        return expectFactoryTime;
    }

    public void setExpectFactoryTime(String expectFactoryTime) {
        this.expectFactoryTime = expectFactoryTime;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getFactoryAddress() {
        return factoryAddress;
    }

    public void setFactoryAddress(String factoryAddress) {
        this.factoryAddress = factoryAddress;
    }

    public String getFactoryContact() {
        return factoryContact;
    }

    public void setFactoryContact(String factoryContact) {
        this.factoryContact = factoryContact;
    }

    public String getFactoryMobileNo() {
        if(factoryMobileNo == null) return "";
        return factoryMobileNo;
    }

    public void setFactoryMobileNo(String factoryMobileNo) {
        this.factoryMobileNo = factoryMobileNo;
    }

    public String getFactoryPhone() {
        if(factoryPhone == null) return "";
        return factoryPhone;
    }

    public void setFactoryPhone(String factoryPhone) {
        this.factoryPhone = factoryPhone;
    }

    public String getYardName() {
        return yardName;
    }

    public void setYardName(String yardName) {
        this.yardName = yardName;
    }

    public String getWharfName() {
        return wharfName;
    }

    public void setWharfName(String wharfName) {
        this.wharfName = wharfName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getYardCode() {
        return yardCode;
    }

    public void setYardCode(String yardCode) {
        this.yardCode = yardCode;
    }

    public String getWharfCode() {
        return wharfCode;
    }

    public void setWharfCode(String wharfCode) {
        this.wharfCode = wharfCode;
    }

    public int getLogisticalStatus() {
        return logisticalStatus;
    }

    public void setLogisticalStatus(int logisticalStatus) {
        this.logisticalStatus = logisticalStatus;
    }

    public double getTare() {
        return tare;
    }

    public void setTare(double tare) {
        this.tare = tare;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }
}

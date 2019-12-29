package com.porachunki;

import java.util.Date;

public class RowData {

    private boolean justAddedFlag = false;
    private Date date;
    private float total;
    private float PaulinaPart;
    private float RobertPart;
    private String payment;
    private String description;
    private float bilansR;
    private float bilansP;
    private float saldo;

    public boolean isJustAddedFlag() {
        return justAddedFlag;
    }

    public void setJustAddedFlag(boolean justAddedFlag) {
        this.justAddedFlag = justAddedFlag;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getRobertPart() {
        return RobertPart;
    }

    public void setRobertPart(float robertPart) {
        RobertPart = robertPart;
    }

    public float getPaulinaPart() {
        return PaulinaPart;
    }

    public void setPaulinaPart(float paulinaPart) {
        PaulinaPart = paulinaPart;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getBilansP() {
        return bilansP;
    }

    public void setBilansP(float bilansP) {
        this.bilansP = bilansP;
    }

    public float getBilansR() {
        return bilansR;
    }

    public void setBilansR(float bilansR) {
        this.bilansR = bilansR;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
}
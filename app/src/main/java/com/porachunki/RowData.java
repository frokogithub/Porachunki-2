package com.porachunki;

import java.util.Date;

public class RowData {

    private boolean justAddedFlag = false;
    private Date date;
    private float bill;
    private float Person1Part;
    private float Person2Part;
    private String whoPays;
    private String description;
    private float Person2TransationBalance;
    private float person1TransationBalance;
    private float balance;

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

    public float getBill() {
        return bill;
    }

    public void setBill(float bill) {
        this.bill = bill;
    }

    public float getPerson2Part() {
        return Person2Part;
    }

    public void setPerson2Part(float person2Part) {
        Person2Part = person2Part;
    }

    public float getPerson1Part() {
        return Person1Part;
    }

    public void setPerson1Part(float person1Part) {
        Person1Part = person1Part;
    }

    public String getWhoPays() {
        return whoPays;
    }

    public void setWhoPays(String whoPays) {
        this.whoPays = whoPays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPerson1TransationBalance() {
        return person1TransationBalance;
    }

    public void setPerson1TransationBalance(float person1TransationBalance) {
        this.person1TransationBalance = person1TransationBalance;
    }

    public float getPerson2TransationBalance() {
        return Person2TransationBalance;
    }

    public void setPerson2TransationBalance(float Person2TransationBalance) {
        this.Person2TransationBalance = Person2TransationBalance;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
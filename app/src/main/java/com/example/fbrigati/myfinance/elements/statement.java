package com.example.fbrigati.myfinance.elements;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Statement {

    public long accountNumber;
    public int date;
    public String time;
    public int sequence;
    public String desc_origin;
    public String desc_user;
    public float amount;
    public String trxcode;
    public String acquirer_id;
    public String category;

    public Statement(){
        //Default constructor
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return desc_origin;
    }

    public void setDescription(String description) {
        this.desc_origin = description;
    }

    public String getDesc_user() {
        return desc_user;
    }

    public void setDesc_user(String desc_user) {
        this.desc_user = desc_user;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getTransactionCode() {
        return trxcode;
    }

    public void setTransactionCode(String transactionCode) {
        this.trxcode = transactionCode;
    }

    public String getAcquirer_id() {
        return acquirer_id;
    }

    public void setAcquirer_id(String acquirer_id) {
        this.acquirer_id = acquirer_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}

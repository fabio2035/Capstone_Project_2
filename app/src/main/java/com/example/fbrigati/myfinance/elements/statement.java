package com.example.fbrigati.myfinance.elements;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Statement {

    public int _ID;
    public String accountNumber;
    public int date;
    public String time;
    public int sequence;
    public String desc_origin;
    public String desc_user;
    public Double amount;
    public int trxcode;
    public String acquirer_id;
    public String category;

    public Statement(int id, String acctNum, int date, String time,
                     int seq, String desc_or, String desc_usr, Double amount
                    ,int trxcd, String acquir, String cat){

        this._ID = id;
        this.accountNumber = acctNum;
        this.date = date;
        this.time = time;
        this.sequence = seq;
        this.desc_origin = desc_or;
        this.desc_user = desc_usr;
        this.amount = amount;
        this.trxcode = trxcd;
        this.acquirer_id = acquir;
        this.category = cat;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getTransactionCode() {
        return trxcode;
    }

    public void setTransactionCode(int transactionCode) {
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

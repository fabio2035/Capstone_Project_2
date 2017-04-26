package com.example.fbrigati.myfinance.elements;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Budget {

    public String year;
    public String month;
    public String category;
    public float amount;


    public Budget(){

    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}

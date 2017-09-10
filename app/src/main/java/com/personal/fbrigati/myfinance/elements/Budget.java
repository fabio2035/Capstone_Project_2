package com.personal.fbrigati.myfinance.elements;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Budget implements Parcelable {

    public String year;
    public String month;
    public String category;
    public float amount;
    public int icon;
    public String description;
    public float spentAmountTemp;

    public Budget(String iYear,
                  String iMonth,
                  String iCategory,
                  Float iAmount,
                  int iIcon,
                  String iDescription,
                  Float iSpentAmountTemp){
        this.year = iYear;
        this.month = iMonth;
        this.category = iCategory;
        this.amount = iAmount;
        this.icon = iIcon;
        this.description = iDescription;
        this.spentAmountTemp = iSpentAmountTemp;

    }

    public int getProgressTemp() {
        return progressTemp;
    }

    public void setProgressTemp(int progressTemp) {
        this.progressTemp = progressTemp;
    }

    public int progressTemp;


    public float getSpentAmountTemp() {
        return spentAmountTemp;
    }

    public void setSpentAmountTemp(int spentAmountTemp) {
        this.spentAmountTemp = spentAmountTemp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    protected Budget(Parcel in){
        this.year = in.readString();
        this.month = in.readString();
        this.category = in.readString();
        this.amount = in.readFloat();
        this.icon = in.readInt();
        this.description = in.readString();
        this.spentAmountTemp = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(category);
        dest.writeFloat(amount);
        dest.writeInt(icon);
        dest.writeString(description);
        dest.writeFloat(spentAmountTemp);
    }

    public static final Creator<Budget> CREATOR = new Creator<Budget>(){

        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[0];
        }
    };



}

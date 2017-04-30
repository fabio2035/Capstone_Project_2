package com.example.fbrigati.myfinance.elements;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Category {

    public String name;
    public String acquirer_id;
    public String desc_default;
    public String desc_user;

    public Category(){
    }

    public String getDesc_default() {
        return desc_default;
    }

    public void setDesc_default(String desc_default) {
        this.desc_default = desc_default;
    }

    public String getDesc_user() {
        return desc_user;
    }

    public void setDesc_user(String desc_user) {
        this.desc_user = desc_user;
    }


    public String getAcquirer_id() {
        return acquirer_id;
    }

    public void setAcquirer_id(String acquirer_id) {
        this.acquirer_id = acquirer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.example.fbrigati.myfinance.elements;

/**
 * Created by FBrigati on 25/04/2017.
 */

public class Category {

    public String name;
    public String acquirer_id;

    public Category(){
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

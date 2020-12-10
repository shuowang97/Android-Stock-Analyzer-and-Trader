package com.example.androidapp_hw9.entity;

public class LocalStoreEntity {
    String ticker;
    String name;
    double shares;

    public LocalStoreEntity() {
    }

    public LocalStoreEntity(String ticker, String name, double shares) {
        this.ticker = ticker;
        this.name = name;
        this.shares = shares;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getShares() {
        return shares;
    }

    public void setShares(double shares) {
        this.shares = shares;
    }
}

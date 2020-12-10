package com.example.androidapp_hw9.entity;

// this obj is from local storage (shared preference)
public class LocalStockEntity {
    String ticker;
    String name;
    float shares;
    double current;
    double change;

    public LocalStockEntity() {
    }

    public LocalStockEntity(String ticker, String name, float shares, double current, double change) {
        this.ticker = ticker;
        this.name = name;
        this.shares = shares;
        this.current = current;
        this.change = change;
    }

    public String getName() { return name; }

    public String getTicker() {
        return ticker;
    }

    public float getShares() {
        return shares;
    }

    public double getCurrent() {
        return current;
    }

    public double getChange() {
        return change;
    }


    public void setName(String name) { this.name = name; }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setShares(float shares) {
        this.shares = shares;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setChange(double change) {
        this.change = change;
    }
}

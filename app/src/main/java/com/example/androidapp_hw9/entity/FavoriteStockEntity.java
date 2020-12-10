package com.example.androidapp_hw9.entity;

// this obj is for favorite stock list
public class FavoriteStockEntity {
    String ticker;
    String name;
    double current;
    double change;

    public FavoriteStockEntity() {
    }

    public FavoriteStockEntity(String ticker, String name, double current, double change) {
        this.ticker = ticker;
        this.name = name;
        this.current = current;
        this.change = change;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public double getCurrent() {
        return current;
    }

    public double getChange() {
        return change;
    }
}

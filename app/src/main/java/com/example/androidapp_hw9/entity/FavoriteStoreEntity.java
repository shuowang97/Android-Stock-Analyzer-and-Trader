package com.example.androidapp_hw9.entity;

public class FavoriteStoreEntity {

    String ticker;
    String name;

    public FavoriteStoreEntity() {
    }

    public FavoriteStoreEntity(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
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
}

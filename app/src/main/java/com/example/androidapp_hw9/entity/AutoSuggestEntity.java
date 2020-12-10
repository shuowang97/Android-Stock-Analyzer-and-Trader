package com.example.androidapp_hw9.entity;

import com.google.gson.annotations.SerializedName;

public class AutoSuggestEntity {

	@SerializedName("permaTicker")
	private String permaTicker;

	@SerializedName("ticker")
	private String ticker;

	@SerializedName("countryCode")
	private String countryCode;

	@SerializedName("name")
	private String name;

	@SerializedName("openFIGIComposite")
	private String openFIGIComposite;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("assetType")
	private String assetType;

	public String getPermaTicker(){
		return permaTicker;
	}

	public String getTicker(){
		return ticker;
	}

	public String getCountryCode(){
		return countryCode;
	}

	public String getName(){
		return name;
	}

	public String getOpenFIGIComposite(){
		return openFIGIComposite;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public String getAssetType(){
		return assetType;
	}

	@Override
	public String toString() {
		return "AutoSuggestEntity{" +
				"permaTicker='" + permaTicker + '\'' +
				", ticker='" + ticker + '\'' +
				", countryCode='" + countryCode + '\'' +
				", name='" + name + '\'' +
				", openFIGIComposite='" + openFIGIComposite + '\'' +
				", isActive=" + isActive +
				", assetType='" + assetType + '\'' +
				'}';
	}
}
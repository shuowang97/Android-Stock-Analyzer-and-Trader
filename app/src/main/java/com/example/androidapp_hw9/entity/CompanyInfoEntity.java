package com.example.androidapp_hw9.entity;

import com.google.gson.annotations.SerializedName;

public class CompanyInfoEntity {

	@SerializedName("ticker")
	private String ticker;

	@SerializedName("endDate")
	private String endDate;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("startDate")
	private String startDate;

	@SerializedName("exchangeCode")
	private String exchangeCode;

	public String getTicker(){
		return ticker;
	}

	public String getEndDate(){
		return endDate;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public String getStartDate(){
		return startDate;
	}

	public String getExchangeCode(){
		return exchangeCode;
	}
}
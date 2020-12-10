package com.example.androidapp_hw9.entity;

import com.google.gson.annotations.SerializedName;

public class LastPriceEntity {

	@SerializedName("lastSaleTimestamp")
	private String lastSaleTimestamp;

	@SerializedName("tngoLast")
	private double tngoLast;

	@SerializedName("askPrice")
	private Object askPrice;

	@SerializedName("ticker")
	private String ticker;

	@SerializedName("last")
	private double last;

	@SerializedName("bidSize")
	private Object bidSize;

	@SerializedName("mid")
	private Object mid;

	@SerializedName("bidPrice")
	private Object bidPrice;

	@SerializedName("prevClose")
	private double prevClose;

	@SerializedName("volume")
	private int volume;

	@SerializedName("high")
	private double high;

	@SerializedName("low")
	private double low;

	@SerializedName("lastSize")
	private Object lastSize;

	@SerializedName("quoteTimestamp")
	private String quoteTimestamp;

	@SerializedName("askSize")
	private Object askSize;

	@SerializedName("open")
	private double open;

	@SerializedName("timestamp")
	private String timestamp;

	public String getLastSaleTimestamp(){
		return lastSaleTimestamp;
	}

	public double getTngoLast(){
		return tngoLast;
	}

	public Object getAskPrice(){
		return askPrice;
	}

	public String getTicker(){
		return ticker;
	}

	public double getLast(){
		return last;
	}

	public Object getBidSize(){
		return bidSize;
	}

	public Object getMid(){
		return mid;
	}

	public Object getBidPrice(){
		return bidPrice;
	}

	public double getPrevClose(){
		return prevClose;
	}

	public int getVolume(){
		return volume;
	}

	public double getHigh(){
		return high;
	}

	public double getLow(){
		return low;
	}

	public Object getLastSize(){
		return lastSize;
	}

	public String getQuoteTimestamp(){
		return quoteTimestamp;
	}

	public Object getAskSize(){
		return askSize;
	}

	public double getOpen(){
		return open;
	}

	public String getTimestamp(){
		return timestamp;
	}


	@Override
	public String toString() {
		return "LastPriceEntity{" +
				"lastSaleTimestamp='" + lastSaleTimestamp + '\'' +
				", tngoLast=" + tngoLast +
				", askPrice=" + askPrice +
				", ticker='" + ticker + '\'' +
				", last=" + last +
				", bidSize=" + bidSize +
				", mid=" + mid +
				", bidPrice=" + bidPrice +
				", prevClose=" + prevClose +
				", volume=" + volume +
				", high=" + high +
				", low=" + low +
				", lastSize=" + lastSize +
				", quoteTimestamp='" + quoteTimestamp + '\'' +
				", askSize=" + askSize +
				", open=" + open +
				", timestamp='" + timestamp + '\'' +
				'}';
	}
}
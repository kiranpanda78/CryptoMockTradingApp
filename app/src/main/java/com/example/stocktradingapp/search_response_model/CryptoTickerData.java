package com.example.stocktradingapp.search_response_model;

import com.google.gson.annotations.SerializedName;

public class CryptoTickerData {

	@SerializedName("s")
	private String s;

	@SerializedName("t")
	private long t;

	@SerializedName("lp")
	private Object lp;

	@SerializedName("e")
	private String e;

	@SerializedName("ls")
	private Object ls;

	@SerializedName("type")
	private String type;

	public String getS(){
		return s;
	}

	public long getT(){
		return t;
	}

	public Object getLp(){
		return lp;
	}

	public String getE(){
		return e;
	}

	public Object getLs(){
		return ls;
	}

	public String getType(){
		return type;
	}
}
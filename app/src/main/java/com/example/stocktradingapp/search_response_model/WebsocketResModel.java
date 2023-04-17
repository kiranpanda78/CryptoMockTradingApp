package com.example.stocktradingapp.search_response_model;

import com.google.gson.annotations.SerializedName;

public class WebsocketResModel{

	@SerializedName("bs")
	private int bs;

	@SerializedName("as")
	private int as;

	@SerializedName("s")
	private String s;

	@SerializedName("t")
	private long t;

	@SerializedName("lp")
	private Object lp;

	@SerializedName("ls")
	private Object ls;

	@SerializedName("type")
	private String type;

	@SerializedName("bp")
	private Object bp;

	@SerializedName("ap")
	private Object ap;

	public int getBs(){
		return bs;
	}

	public int getAs(){
		return as;
	}

	public String getS(){
		return s;
	}

	public long getT(){
		return t;
	}

	public Object getLp(){
		return lp;
	}

	public Object getLs(){
		return ls;
	}

	public String getType(){
		return type;
	}

	public Object getBp(){
		return bp;
	}

	public Object getAp(){
		return ap;
	}
}
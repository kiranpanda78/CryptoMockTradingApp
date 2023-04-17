package com.example.stocktradingapp.search_response_model;

import com.google.gson.annotations.SerializedName;

public class SymbolSearchData{

	@SerializedName("symbol")
	public String symbol;

	@SerializedName("price")
	public Object price;

	public Object initialPrice;

	@SerializedName("name")
	public String name;

	@SerializedName("exchange")
	public String exchange;

	@SerializedName("exchangeShortName")
	public String exchangeShortName;

	@SerializedName("type")
	public String type;


	public void setInitialPrice(Object initialPrice) {
		this.initialPrice = initialPrice;
	}

	public String getSymbol(){
		return symbol;
	}

	public Object getPrice(){
		return price;
	}

	public void setPrice(Object price) {
		this.price = price;
	}

	public String getName(){
		return name;
	}

	public String getExchange(){
		return exchange;
	}

	public String getExchangeShortName(){
		return exchangeShortName;
	}

	public String getType(){
		return type;
	}
}
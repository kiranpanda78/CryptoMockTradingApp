package com.example.stocktradingapp.search_response_model;

import com.google.gson.annotations.SerializedName;

public class ChangeData {
    @SerializedName("01. symbol")
    public String symbol;

    @SerializedName("02. open")
    public String open;

    @SerializedName("03. high")
    public String high;

    @SerializedName("04. low")
    public String low;

    @SerializedName("05. price")
    public String price;

    @SerializedName("06. volume")
    public String volume;

    @SerializedName("09. change")
    public String change;

    @SerializedName("10. change percent")
    public String change_percentage;

    @SerializedName("8. currency")
    public String currency;
}

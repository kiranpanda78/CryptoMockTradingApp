package com.example.stocktradingapp.search_response_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PriceChangeResponse {

    @SerializedName("Global Quote")
    public ChangeData ChangeList;
}

package com.example.stocktradingapp;


import com.example.stocktradingapp.search_response_model.PriceChangeResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    public static String BASE_URL = "https://financialmodelingprep.com/api/v3/";

    @GET("stock/list?apikey=d8d70a0912096efb67af1a8dbb8a2138")
    Call<ResponseBody> searchSymbol();

    @GET("?function=GLOBAL_QUOTE&apikey=RGI9TE4PBRCDDXXI")
    Call<PriceChangeResponse> getSymbolData(@Query("symbol") String symbol);
}

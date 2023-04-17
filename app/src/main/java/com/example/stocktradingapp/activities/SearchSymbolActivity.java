package com.example.stocktradingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.stocktradingapp.API;
import com.example.stocktradingapp.adapters.AdapterSymbolSuggestions;
import com.example.stocktradingapp.databinding.ActivitySearchSymbolBinding;

import okhttp3.ResponseBody;

import com.example.stocktradingapp.search_response_model.SymbolSearchData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchSymbolActivity extends AppCompatActivity {

    private ActivitySearchSymbolBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchSymbolBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.rvSymbols.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        getSymbolData();
    }

    private void getSymbolData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);
        Call<ResponseBody> call = api.searchSymbol();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                Type type = new TypeToken<List<SymbolSearchData>>() {
                }.getType();
                try {
                    List<SymbolSearchData> list = new Gson().fromJson(response.body().string(), type);
                    ArrayList<SymbolSearchData> tempList = new ArrayList<SymbolSearchData>();
                    for (SymbolSearchData s : list) {
                        if (s.getExchangeShortName().equals("TSX")) {
                            tempList.add(s);
                        }
                        binding.rvSymbols.setAdapter(new AdapterSymbolSuggestions(tempList));
                    }
                } catch (Exception e) {
                    Log.wtf("Exception", e.getMessage());
                    binding.rvSymbols.setAdapter(null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                binding.rvSymbols.setAdapter(null);
            }
        });
    }

}
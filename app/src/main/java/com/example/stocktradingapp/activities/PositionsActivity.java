package com.example.stocktradingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.stocktradingapp.AppUtil;
import com.example.stocktradingapp.R;
import com.example.stocktradingapp.adapters.AdapterPositions;
import com.example.stocktradingapp.databinding.ActivityPositionsBinding;
import com.example.stocktradingapp.positions.PositionData;
import com.example.stocktradingapp.positions.PositionsModel;
import com.example.stocktradingapp.search_response_model.CryptoTickerData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tech.gusavila92.websocketclient.WebSocketClient;

public class PositionsActivity extends AppCompatActivity {

    ActivityPositionsBinding binding;

    private WebSocketClient webSocketClient;

    ArrayList<PositionData> positionsList;

    AdapterPositions adapterPositions;

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("wss://crypto.financialmodelingprep.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                System.out.println("OnOpen");
                HashMap<String, Object> loginMap = new HashMap<>();
                HashMap<String, Object> dataMap = new HashMap<>();
                loginMap.put("event", "login");
                dataMap.put("apiKey", "d8d70a0912096efb67af1a8dbb8a2138");
                loginMap.put("data", dataMap);
                webSocketClient.send(new Gson().toJson(loginMap));
            }

            @Override
            public void onTextReceived(String message) {

                if (message.contains("Authenticated")) {
                    HashMap<String, Object> loginMap = new HashMap<>();
                    HashMap<String, Object> dataMap = new HashMap<>();
                    loginMap.put("event", "subscribe");
                    dataMap.put("ticker", "btcusd");
                    loginMap.put("data", dataMap);
                    webSocketClient.send(new Gson().toJson(loginMap));
                    dataMap.put("ticker", "ethusd");
                    webSocketClient.send(new Gson().toJson(loginMap));
                }

                if (message.contains("binance")) {
                    try {
                        CryptoTickerData cryptoTickerData = new Gson().fromJson(message, CryptoTickerData.class);
                        if (!cryptoTickerData.getE().equals("binance")) {
                            return;
                        }
                        double totalPl = 0.00;
                        for (int i = 0; i < positionsList.size(); i++) {
                            if (positionsList.get(i).symbol.equals(cryptoTickerData.getS())) {
                                positionsList.get(i).ltp = Double.parseDouble("" + cryptoTickerData.getLp());
                                positionsList.get(i).exchange = cryptoTickerData.getE().toUpperCase(Locale.ROOT);
                                int finalI = i;
                                runOnUiThread(() -> adapterPositions.notifyItemChanged(finalI));
                            }
                            totalPl = totalPl + (positionsList.get(i).totalQty * (Double.valueOf(positionsList.get(i).ltp) - positionsList.get(i).buyAvg));
                        }

                        double finalTotalPl = totalPl;

                        runOnUiThread(() -> {
                            binding.tvPL.setText("" + String.format("%.2f", finalTotalPl));
                            if (finalTotalPl > 0) {
                                binding.tvPL.setTextColor(ContextCompat.getColor(PositionsActivity.this, R.color.app_green));
                            } else {
                                binding.tvPL.setTextColor(ContextCompat.getColor(PositionsActivity.this, R.color.app_red));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("onPongReceived");
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPositionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        PositionsModel list = new Gson().fromJson(getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString(AppUtil.Key_Positions, ""), PositionsModel.class);
        if (list != null && (list.getPositions() != null && list.getPositions().size() > 0)) {
            binding.rvPositions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            positionsList = list.getPositions();
            adapterPositions = new AdapterPositions(positionsList);
            binding.rvPositions.setAdapter(adapterPositions);
            createWebSocketClient();
        } else {
            Toast.makeText(this, "No Active Position", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
package com.example.stocktradingapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.stocktradingapp.adapters.AdapterWatchList;
import com.example.stocktradingapp.AppUtil;
import com.example.stocktradingapp.databinding.ActivityMainBinding;
import com.example.stocktradingapp.search_response_model.CryptoTickerData;
import com.example.stocktradingapp.search_response_model.SymbolSearchData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    private AdapterWatchList adapterWatchList;

    private ArrayList<SymbolSearchData> symbolsList;

    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;


    private WebSocketClient webSocketClient;

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
                        for (int i = 0; i < symbolsList.size(); i++) {
                            if (symbolsList.get(i).getSymbol().equals(cryptoTickerData.getS())) {
                                symbolsList.get(i).setPrice(cryptoTickerData.getLp());
                                if (symbolsList.get(i).initialPrice == null) {
                                    symbolsList.get(i).initialPrice = symbolsList.get(i).getPrice();
                                }
                                symbolsList.get(i).exchange = cryptoTickerData.getE().toUpperCase(Locale.ROOT);
                                int finalI = i;
                                runOnUiThread(() -> adapterWatchList.notifyItemChanged(finalI));
                            }
                        }
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

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == 200 && result.getData() != null && result.getData().getStringExtra("symbol") != null) {
            symbolsList.add(new Gson().fromJson(result.getData().getStringExtra("symbol"), SymbolSearchData.class));
            adapterWatchList.notifyItemInserted(adapterWatchList.getItemCount());
            sharedPreferences.edit().putString(AppUtil.Key_Watchlist, new Gson().toJson(symbolsList)).apply();
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        if (symbolsList != null && symbolsList.size() > 0) {
            createWebSocketClient();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.getValue() == null) {
                    myRef.setValue(5000);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        createWebSocketClient();

        sharedPreferences = new AppUtil().getSharedPreferences(this);

        symbolsList = new ArrayList<>();

        SymbolSearchData symbolsListItem = new SymbolSearchData();
        symbolsListItem.symbol = "btcusd";
        symbolsListItem.price = "0.00";
        symbolsListItem.exchange = "binance".toUpperCase(Locale.ROOT);
        symbolsList.add(symbolsListItem);

        SymbolSearchData symbolsListItem2 = new SymbolSearchData();
        symbolsListItem2.symbol = "ethusd";
        symbolsListItem2.price = "0.00";
        symbolsListItem2.exchange = "binance".toUpperCase(Locale.ROOT);
        symbolsList.add(symbolsListItem2);

        adapterWatchList = new AdapterWatchList(symbolsList);

        binding.rvWatchlist.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        binding.rvWatchlist.setItemViewCacheSize(symbolsList.size());
        binding.rvWatchlist.setHasFixedSize(true);

        binding.rvWatchlist.setAdapter(adapterWatchList);

        binding.btnSearchSymbol.setOnClickListener(v -> launcher.launch(new Intent(MainActivity.this, SearchSymbolActivity.class)));

        if (sharedPreferences.getString(AppUtil.Key_Watchlist, "").length() > 0) {
            Type token = new TypeToken<ArrayList<SymbolSearchData>>() {
            }.getType();
            symbolsList = new Gson().fromJson(sharedPreferences.getString(AppUtil.Key_Watchlist, ""), token);
            adapterWatchList = new AdapterWatchList(symbolsList);
            binding.rvWatchlist.setAdapter(adapterWatchList);
        }

        binding.btnWallet.setOnClickListener(v -> startActivity(new Intent(this, AddFundsActivity.class)));
        binding.btnPositions.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PositionsActivity.class)));
        binding.viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
            }
        });
    }
}

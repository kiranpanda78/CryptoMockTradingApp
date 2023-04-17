package com.example.stocktradingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.stocktradingapp.adapters.AdapterOrders;
import com.example.stocktradingapp.databinding.ActivityOrdersBinding;
import com.example.stocktradingapp.orderbook.PlaceOrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding binding;

    ArrayList<PlaceOrderModel> placeOrderModelArrayList = new ArrayList<>();
    private AdapterOrders adapterOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvOrders.setText("Orders (0)");

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        adapterOrders = new AdapterOrders(placeOrderModelArrayList);

        binding.rvOrders.setAdapter(adapterOrders);

        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    HashMap<String, Object> mapData;
                    Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
                    mapData = new Gson().fromJson(snapshot.getValue().toString(), type);
                    int count = 0;
                    for (String key: mapData.keySet()){
                        count++;
                        PlaceOrderModel placeOrderModel = new Gson().fromJson(new Gson().toJson(mapData.get(key)), PlaceOrderModel.class);
                        placeOrderModelArrayList.add(placeOrderModel);
                    }
                    adapterOrders.notifyItemRangeInserted(0, placeOrderModelArrayList.size());
                    binding.tvOrders.setText("Orders ("+count+")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
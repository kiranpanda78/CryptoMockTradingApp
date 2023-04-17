package com.example.stocktradingapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.stocktradingapp.databinding.BuySellDialogFragmentBinding;
import com.example.stocktradingapp.orderbook.PlaceOrderModel;
import com.example.stocktradingapp.positions.PositionData;
import com.example.stocktradingapp.positions.PositionsModel;
import com.example.stocktradingapp.search_response_model.SymbolSearchData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BuySellDialogFragment extends BottomSheetDialogFragment {

    private BuySellDialogFragmentBinding binding;


    int finalI;
    ArrayList<SymbolSearchData> list;

    private String b_s;

    private SharedPreferences sharedPreferences;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private double availableFunds = 0.00;

    public BuySellDialogFragment(String b, int finalI, ArrayList<SymbolSearchData> list) {
        this.finalI = finalI;
        this.list = list;
        this.b_s = b;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BuySellDialogFragmentBinding.inflate(getLayoutInflater());
        sharedPreferences = new AppUtil().getSharedPreferences(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("orders");

        database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    binding.tvAvailableFunds.setText(String.format("$%.2f", Double.valueOf(snapshot.getValue().toString())));
                    availableFunds = Double.valueOf(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        initViewAndActions();

        updateData();
    }

    private void initViewAndActions() {
        if (b_s.equals("B")) {
            binding.cvBuySell.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_green));
            binding.tvBuySell.setText("BUY");
            binding.tvUserHint.setText("Margin Required (Approx)");
        } else {
            binding.cvBuySell.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_red));
            binding.tvBuySell.setText("SELL");
            binding.tvUserHint.setText("SEll Value (Approx)");
        }
        binding.cvBuySell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etNoOfShares.getText().toString().length() > 0) {
                    int noOfShares = Integer.parseInt(binding.etNoOfShares.getText().toString());
                    if (sharedPreferences.getString(AppUtil.Key_Positions, "").length() == 0) {
                        PositionData positionData = new PositionData();
                        positionData.symbol = list.get(finalI).symbol;
                        positionData.exchange = list.get(finalI).exchange;
                        positionData.totalQty = Integer.parseInt(binding.etNoOfShares.getText().toString());
                        positionData.buyAvg = Double.parseDouble("" + list.get(finalI).price);
                        PositionsModel positionsModel = new PositionsModel();
                        ArrayList<PositionData> data = positionsModel.getPositions();
                        data.add(positionData);

                        PlaceOrderModel pom = new PlaceOrderModel();
                        pom.symbol = list.get(finalI).symbol;
                        pom.quantity = positionData.totalQty;
                        pom.side = b_s;
                        pom.executedPrice = Double.parseDouble("" + list.get(finalI).price);
                        pom.timestamp = System.currentTimeMillis();
                        if (b_s.equals("B") && availableFunds < Double.valueOf(noOfShares) * Double.valueOf(binding.tvPrice.getText().toString())) {
                            Toast.makeText(getActivity(), "Insufficient Funds!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (b_s.equals("B")) {
                            database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds - (pom.quantity * pom.executedPrice));
                        } else {
                            database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds + (pom.quantity * pom.executedPrice));
                        }
                        databaseReference.child("" + System.currentTimeMillis()).setValue(pom);
                        sharedPreferences.edit().putString(AppUtil.Key_Positions, new Gson().toJson(positionsModel)).apply();
                        Toast.makeText(getActivity(), "Order Success!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        PositionsModel positionsModel = new Gson().fromJson(sharedPreferences.getString(AppUtil.Key_Positions, ""), PositionsModel.class);
                        boolean exists = false;
                        if (positionsModel.getPositions().size() > 0) {
                            for (int i = 0; i < positionsModel.getPositions().size(); i++) {
                                if (positionsModel.getPositions().get(i).symbol.equals(list.get(finalI).symbol)) {
                                    if (b_s.equals("S") && positionsModel.getPositions().get(i).totalQty < noOfShares) {
                                        Toast.makeText(requireActivity(), "You don't have enough shares to process this order", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (positionsModel.getPositions().get(i).buyAvg > 0) {
                                        double currentTotal = Double.parseDouble("" + list.get(finalI).price) * noOfShares;
                                        double prevTotal = positionsModel.getPositions().get(i).buyAvg * positionsModel.getPositions().get(i).totalQty;
                                        positionsModel.getPositions().get(i).buyAvg = (currentTotal + prevTotal) / (noOfShares + positionsModel.getPositions().get(i).totalQty);
                                    }
                                    if (b_s.equals("S")) {
                                        positionsModel.getPositions().get(i).totalQty = positionsModel.getPositions().get(i).totalQty - noOfShares;
                                    } else {
                                        positionsModel.getPositions().get(i).totalQty = positionsModel.getPositions().get(i).totalQty + noOfShares;
                                    }
                                    PlaceOrderModel pom = new PlaceOrderModel();
                                    pom.symbol = list.get(finalI).symbol;
                                    pom.quantity = noOfShares;
                                    pom.side = b_s;
                                    pom.executedPrice = Double.parseDouble("" + list.get(finalI).price);
                                    pom.timestamp = System.currentTimeMillis();
                                    if (b_s.equals("B") && availableFunds < Double.valueOf(noOfShares) * Double.valueOf(binding.tvPrice.getText().toString())) {
                                        Toast.makeText(getActivity(), "Insufficient Funds!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (positionsModel.getPositions().get(i).totalQty == 0) {
                                        positionsModel.getPositions().remove(i);
                                    }
                                    if (b_s.equals("B")) {
                                        database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds - (pom.quantity * pom.executedPrice));
                                    } else {
                                        database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds + (pom.quantity * pom.executedPrice));
                                    }
                                    if (b_s.equals("B")) {
                                        database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds - (pom.quantity * pom.executedPrice));
                                    } else {
                                        database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds + (pom.quantity * pom.executedPrice));
                                    }
                                    databaseReference.child("" + System.currentTimeMillis()).setValue(pom);
                                    sharedPreferences.edit().putString(AppUtil.Key_Positions, new Gson().toJson(positionsModel)).apply();
                                    exists = true;
                                    Toast.makeText(getActivity(), "Order Success!", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                    break;
                                }
                            }

                            if (!exists) {
                                PositionData positionData = new PositionData();
                                positionData.symbol = list.get(finalI).symbol;
                                positionData.exchange = list.get(finalI).exchange;
                                positionData.totalQty = Integer.parseInt(binding.etNoOfShares.getText().toString());
                                double exePrc = Double.parseDouble("" + list.get(finalI).price);
                                positionData.buyAvg = exePrc;
                                if (b_s.equals("S")) {
                                    Toast.makeText(requireActivity(), "You don't have enough shares to process this order", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<PositionData> data = positionsModel.getPositions();
                                data.add(positionData);
                                PlaceOrderModel pom = new PlaceOrderModel();
                                pom.symbol = list.get(finalI).symbol;
                                pom.quantity = noOfShares;
                                pom.side = b_s;
                                pom.executedPrice = exePrc;
                                pom.timestamp = System.currentTimeMillis();
                                if (b_s.equals("B")) {
                                    database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds - (pom.quantity * pom.executedPrice));
                                } else {
                                    database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds + (pom.quantity * pom.executedPrice));
                                }
                                databaseReference.child("" + System.currentTimeMillis()).setValue(pom);
                                sharedPreferences.edit().putString(AppUtil.Key_Positions, new Gson().toJson(positionsModel)).apply();
                                Toast.makeText(getActivity(), "Order Success!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        } else {
                            PositionData positionData = new PositionData();
                            positionData.symbol = list.get(finalI).symbol;
                            positionData.exchange = list.get(finalI).exchange;
                            positionData.totalQty = Integer.parseInt(binding.etNoOfShares.getText().toString());
                            double exePrc = Double.parseDouble("" + list.get(finalI).price);
                            positionData.buyAvg = exePrc;
                            if (b_s.equals("S")) {
                                Toast.makeText(requireActivity(), "You don't have enough shares to process this order", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ArrayList<PositionData> data = positionsModel.getPositions();
                            data.add(positionData);
                            PlaceOrderModel pom = new PlaceOrderModel();
                            pom.symbol = list.get(finalI).symbol;
                            pom.quantity = noOfShares;
                            pom.side = b_s;
                            pom.executedPrice = exePrc;
                            pom.timestamp = System.currentTimeMillis();
                            if (b_s.equals("B")) {
                                database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds - (pom.quantity * pom.executedPrice));
                            } else {
                                database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet").setValue(availableFunds + (pom.quantity * pom.executedPrice));
                            }
                            databaseReference.child("" + System.currentTimeMillis()).setValue(pom);
                            sharedPreferences.edit().putString(AppUtil.Key_Positions, new Gson().toJson(positionsModel)).apply();
                            Toast.makeText(getActivity(), "Order Success!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                } else {
                    binding.etNoOfShares.setError("Please Enter No. Of Shares");
                }

                PositionsModel positionsModel = new Gson().fromJson(sharedPreferences.getString(AppUtil.Key_Positions, ""), PositionsModel.class);

                Log.wtf("Positions", new Gson().toJson(positionsModel));
            }
        });
    }

    private void updateData() {

        binding.tvSymbol.setText(list.get(finalI).symbol.toUpperCase(Locale.ROOT));

        binding.tvPrice.setText("" + list.get(finalI).price.toString());

        try {

            Double price = Double.valueOf("" + list.get(finalI).getPrice());

            binding.tvPrice.setText(String.format("%.3f", price));

            Double initialPrice = Double.valueOf("" + list.get(finalI).initialPrice);

            Double price_diff = price - initialPrice;

            String changeStr = String.format("%.3f", ((price_diff) / (initialPrice)) * 100);

            if (changeStr.startsWith("-")) {
                binding.tvPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_red));
                changeStr = MessageFormat.format("{0} ({1}%)", price_diff, changeStr);
            } else {
                changeStr = "+" + changeStr;
                changeStr = MessageFormat.format("{0} ({1}%)", price_diff, changeStr);
                binding.tvPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_green));
            }

            binding.tvPriceChange.setText(changeStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (binding.etNoOfShares.getText().toString().length() > 0) {
            int noOfShares = Integer.parseInt(binding.etNoOfShares.getText().toString());
            binding.tvRequiredMargin.setText(String.format("$%.2f", Double.valueOf(noOfShares) * Double.valueOf(binding.tvPrice.getText().toString())));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateData();
            }
        }, 500);
    }
}
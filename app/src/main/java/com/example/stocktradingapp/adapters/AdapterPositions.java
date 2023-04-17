package com.example.stocktradingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocktradingapp.R;
import com.example.stocktradingapp.positions.PositionData;

import java.util.ArrayList;
import java.util.List;

public class AdapterPositions extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<PositionData> list;

    public AdapterPositions(ArrayList<PositionData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_positions, parent, false);
        return new ViewHolderPositions(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderPositions h = (ViewHolderPositions) holder;
        PositionData item = list.get(position);
        h.tvQty.setText(item.totalQty + " Qty.");
        h.tvExchange.setText(item.exchange);
        h.tvSymbolName.setText(item.symbol.toUpperCase());
        h.tvLtp.setText(String.format("%.2f", Double.valueOf(item.ltp)));
        h.tvBuyAvg.setText(String.format("Avg. %.2f", item.buyAvg));
        try {
            if (item.ltp > 0) {
                double totalPL = item.totalQty * (Double.valueOf(item.ltp) - item.buyAvg);
                if (totalPL > 0) {
                    h.tvTotalPL.setText(String.format("%.2f", totalPL));
                    h.tvTotalPL.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_green));
                } else {
                    h.tvTotalPL.setText(String.format("%.2f", totalPL));
                    h.tvTotalPL.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_red));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderPositions extends RecyclerView.ViewHolder {

        TextView tvQty;
        TextView tvSymbolName;

        TextView tvExchange;

        TextView tvLtp;

        TextView tvTotalPL;

        TextView tvBuyAvg;

        public ViewHolderPositions(@NonNull View itemView) {
            super(itemView);
            this.tvQty = itemView.findViewById(R.id.tvQty);
            this.tvSymbolName = itemView.findViewById(R.id.tvSymbolName);
            this.tvExchange = itemView.findViewById(R.id.tvExchange);
            this.tvLtp = itemView.findViewById(R.id.tvLtp);
            this.tvTotalPL = itemView.findViewById(R.id.tvTotalPL);
            this.tvBuyAvg = itemView.findViewById(R.id.tvBuyAvg);
        }
    }
}

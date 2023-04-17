package com.example.stocktradingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocktradingapp.R;
import com.example.stocktradingapp.orderbook.PlaceOrderModel;
import com.example.stocktradingapp.positions.PositionData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<PlaceOrderModel> list;

    public AdapterOrders(ArrayList<PlaceOrderModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_orders, parent, false);
        return new ViewHolderPositions(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderPositions h = (ViewHolderPositions) holder;
        PlaceOrderModel item = list.get(position);
        h.tvPrice.setText(String.format("Avg. %.2f", item.executedPrice));
        h.tvSymbolName.setText(item.symbol.toUpperCase());
        Date date = new Date(item.timestamp);
        h.tvTime.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss").format(date));
        if (item.side.equals("B")){
            h.tvSideQty.setText("BUY " + "("+item.quantity+")");
            h.tvSideQty.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_green));
        } else {
            h.tvSideQty.setText("SELL " + "("+item.quantity+")");
            h.tvSideQty.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_red));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderPositions extends RecyclerView.ViewHolder {

        TextView tvSymbolName, tvSideQty, tvTime, tvPrice;

        public ViewHolderPositions(@NonNull View itemView) {
            super(itemView);
            this.tvSymbolName = itemView.findViewById(R.id.tvSymbolName);
            this.tvSideQty = itemView.findViewById(R.id.tvSideQty);
            this.tvTime = itemView.findViewById(R.id.tvTime);
            this.tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}

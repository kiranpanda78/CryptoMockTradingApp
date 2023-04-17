package com.example.stocktradingapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocktradingapp.R;
import com.example.stocktradingapp.activities.SearchSymbolActivity;
import com.example.stocktradingapp.search_response_model.SymbolSearchData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AdapterSymbolSuggestions extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<SymbolSearchData> list = new ArrayList<>();

    public AdapterSymbolSuggestions(ArrayList<SymbolSearchData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_symbol_suggestions, parent, false);
        return new ViewHolderSymbolSuggestions(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderSymbolSuggestions h = (ViewHolderSymbolSuggestions) holder;
        SymbolSearchData item = list.get(position);
        h.tvSymbolName.setText(item.getSymbol() + " (" + item.getExchangeShortName() + ")");
        h.tvName.setText(item.getName());

        h.btnAdd.setOnClickListener(v -> {
            SearchSymbolActivity searchSymbolActivity = (SearchSymbolActivity) h.itemView.getContext();
            Intent intent = new Intent();
            intent.putExtra("symbol", new Gson().toJson(item));
            searchSymbolActivity.setResult(200, intent);
            searchSymbolActivity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderSymbolSuggestions extends RecyclerView.ViewHolder {

        TextView tvSymbolName;
        TextView tvName;

        Button btnAdd;

        public ViewHolderSymbolSuggestions(@NonNull View itemView) {
            super(itemView);
            this.tvSymbolName = itemView.findViewById(R.id.tvSymbolName);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}

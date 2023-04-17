package com.example.stocktradingapp.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stocktradingapp.BuySellDialogFragment;
import com.example.stocktradingapp.R;
import com.example.stocktradingapp.activities.MainActivity;
import com.example.stocktradingapp.search_response_model.SymbolSearchData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterWatchList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SymbolSearchData> list;

    public AdapterWatchList(ArrayList<SymbolSearchData> list) {
        this.list = list;
    }

    public int selectedPos = -1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_watchlist, parent, false);
        return new ViewHolderWatchList(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderWatchList h = (ViewHolderWatchList) holder;
        SymbolSearchData item = list.get(position);

        h.tvSymbolName.setText(item.getSymbol().toUpperCase());

        h.tvExchange.setText(item.exchange.toUpperCase(Locale.ROOT));

        try {

            Double price = Double.valueOf("" + item.getPrice());

            h.tvPrice.setText(String.format("%.3f", price));

            Double initialPrice = Double.valueOf("" + item.initialPrice);

            Double price_diff = price - initialPrice;

            String changeStr = String.format("%.3f", ((price - initialPrice) / (price)) * 100);

            if (changeStr.startsWith("-")) {
                h.tvPrice.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_red));
                changeStr = MessageFormat.format("{0} ({1}%)", price_diff, changeStr);
            } else {
                changeStr = "+" + changeStr;
                changeStr = MessageFormat.format("{0} ({1}%)", price_diff, changeStr);
                h.tvPrice.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.app_green));
            }

            h.tvPercentageChange.setText(changeStr);

            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int finalPos = position;
                    int tmp = selectedPos;
                    if (position == selectedPos) {
                        selectedPos = -1;
                    } else {
                        selectedPos = position;
                    }
                    notifyItemChanged(position);
                    if (tmp != -1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(tmp);
                            }
                        }, 200);
                    }
                }
            });

            if (position == selectedPos) {
                h.buySellContainer.setVisibility(View.VISIBLE);
            } else {
                h.buySellContainer.setVisibility(View.GONE);
            }

            final int finalPos = position;

            h.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialogFragment bottomSheetDialogFragment = new BuySellDialogFragment("B", finalPos, list);
                    bottomSheetDialogFragment.show(((MainActivity) v.getContext()).getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
                }
            });
            h.btnSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialogFragment bottomSheetDialogFragment = new BuySellDialogFragment("S", finalPos, list);
                    bottomSheetDialogFragment.show(((MainActivity) v.getContext()).getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderWatchList extends RecyclerView.ViewHolder {

        TextView tvSymbolName;
        TextView tvExchange;
        TextView tvPrice;
        TextView tvPercentageChange;

        LinearLayout buySellContainer;

        Button btnBuy, btnSell;

        public ViewHolderWatchList(@NonNull View itemView) {
            super(itemView);
            this.tvSymbolName = itemView.findViewById(R.id.tvSymbolName);
            this.tvExchange = itemView.findViewById(R.id.tvExchange);
            this.tvPrice = itemView.findViewById(R.id.tvPrice);
            this.tvPercentageChange = itemView.findViewById(R.id.tvPercentageChange);
            this.buySellContainer = itemView.findViewById(R.id.buySellContainer);
            this.btnBuy = itemView.findViewById(R.id.btnBuy);
            this.btnSell = itemView.findViewById(R.id.btnSell);
        }
    }
}

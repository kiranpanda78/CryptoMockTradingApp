package com.example.stocktradingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.stocktradingapp.R;
import com.example.stocktradingapp.databinding.ActivityAddFundsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFundsActivity extends AppCompatActivity {

    private ActivityAddFundsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddFundsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wallet");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.getValue() == null) {
                    myRef.setValue(0.00);
                } else {
                    binding.tvFunds.setText(String.format("$%.2f", Double.valueOf(dataSnapshot.getValue().toString())));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        binding.cvAddFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvFunds.getText().toString().length() > 0 && !binding.tvFunds.getText().toString().equals("0.00")) {
                    Double funds = Double.valueOf(binding.tvFunds.getText().toString().replace("$", ""));
                    myRef.setValue(Double.valueOf(binding.etAmount.getText().toString()) + funds);
                    binding.etAmount.getText().clear();
                } else {
                    myRef.setValue(binding.etAmount.getText().toString());
                    binding.etAmount.getText().clear();
                }
            }
        });

        binding.cvWithdrawFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvFunds.getText().toString().length() > 0 && !binding.tvFunds.getText().toString().equals("0.00")) {
                    Double funds = Double.valueOf(binding.tvFunds.getText().toString().replace("$", ""));
                    if (funds - Double.valueOf(binding.etAmount.getText().toString()) < 0) {
                        Toast.makeText(AddFundsActivity.this, "Insufficient Funds!", Toast.LENGTH_SHORT).show();
                        binding.etAmount.getText().clear();
                        return;
                    }
                    myRef.setValue(funds - Double.valueOf(binding.etAmount.getText().toString()));
                    binding.etAmount.getText().clear();
                }
            }
        });
    }
}
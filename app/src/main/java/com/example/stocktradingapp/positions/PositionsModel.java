package com.example.stocktradingapp.positions;

import java.util.ArrayList;
import java.util.List;

public class PositionsModel {
    ArrayList<PositionData> positions = new ArrayList<>();

    public ArrayList<PositionData> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<PositionData> positions) {
        this.positions = positions;
    }
}

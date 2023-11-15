package com.example.collageapp.turbo_collage;

import androidx.annotation.NonNull;

public class TCCollageItem {

    TCRect ratioRect;
    String uuid;

    public TCCollageItem(String uuid, TCRect ratioRect) {
        this.uuid = uuid;
        this.ratioRect = ratioRect;
    }

    public double getRatioMaxBound(double canvasWidth, double canvasHeight) {
        double w = canvasWidth * this.ratioRect.right;
        double h = this.ratioRect.bottom * canvasHeight;
        return Math.max(w, h);
    }

    public boolean emptyUUID() {
        return uuid == null || uuid.trim().isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return "CollageItem{" +
                "ratioRect=" + ratioRect +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
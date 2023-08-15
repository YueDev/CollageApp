package com.example.collageapp.bean;

import android.graphics.Bitmap;

public class SegmentResult {
    private final Bitmap srcBitmap;
    private final Bitmap hearBitmap;
    private final Bitmap peopleBitmap;

    public SegmentResult(Bitmap srcBitmap, Bitmap hearBitmap, Bitmap peopleBitmap) {
        this.srcBitmap = srcBitmap;
        this.hearBitmap = hearBitmap;
        this.peopleBitmap = peopleBitmap;
    }

    public Bitmap getSrcBitmap() {
        return srcBitmap;
    }

    public Bitmap getHearBitmap() {
        return hearBitmap;
    }

    public Bitmap getPeopleBitmap() {
        return peopleBitmap;
    }
}

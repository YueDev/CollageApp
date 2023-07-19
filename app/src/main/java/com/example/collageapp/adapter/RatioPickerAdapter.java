package com.example.collageapp.adapter;

import cn.simonlee.widget.scrollpicker.PickAdapter;

/**
 * Created by Yue on 2022/10/28.
 */
public class RatioPickerAdapter implements PickAdapter {

    private final int size = 20;

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public String getItem(int position) {
        return size - position + "";
    }

    public int getRatio(int position) {
        return size - position;
    }

    public int getPosition(int ratio) {
        return size - ratio;
    }

}

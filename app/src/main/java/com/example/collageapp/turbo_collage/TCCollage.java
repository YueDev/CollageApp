package com.example.collageapp.turbo_collage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Yue on 2022/5/28.
 */
public class TCCollage {

    private final List<TCCollageItem> collageItems = new ArrayList<>();

    private double width = 1000;
    private double height = 1500;

    private final Map<String, TCBitmap> bitmapMap = new HashMap<>();


    //初始化，设置TCBitmaps数据，传入的每张图片的尺寸，之后调用collage()
    //图片尺寸变化时，重新调用一下这个方法即可
    public void init(List<TCBitmap> bitmaps) {

        bitmapMap.clear();
        collageItems.clear();

        collageItems.add(new TCCollageItem(null, new TCRect(0.0d, 0.0d, 1.0d, 1.0d)));

        if (bitmaps != null && !bitmaps.isEmpty()) {
            for (TCBitmap bitmap : bitmaps) {
                String uuid = bitmap.getUUID();
                TCCollageItem emptyCollageItem = getEmptyItemOrNull();
                if (emptyCollageItem != null) {
                    emptyCollageItem.uuid = uuid;
                } else {
                    int index = getMaxBoundIndex();
                    TCRect tcRect = index < 0 ? new TCRect(0.0d, 0.0d, 1.0d, 1.0d) : getTCRect(index);
                    collageItems.add(new TCCollageItem(uuid, tcRect));
                }
                bitmapMap.put(uuid, bitmap);
            }
        }
    }

    //拼图，并且返回结果  回调版本 java用这个
    public void collage(double width, double height, double padding, OnResultListener mListener) {
        try {
            this.width = width;
            this.height = height;
            reCollage();
            TCResult result = drawCollage(padding);
            mListener.onSuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(e);
        }
    }

    //同步版本 kotlin协程用这个
    public TCResult collage(double width, double height, double padding) {
        try {
            this.width = width;
            this.height = height;
            reCollage();
            return drawCollage(padding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private TCResult drawCollage(double frame) {
        TCResult result = new TCResult();
        if (collageItems.isEmpty()) return result;

        for (int i = 0; i < this.collageItems.size(); i++) {
            TCCollageItem collageItem = this.collageItems.get(i);
            TCRect tcRectF = getItemInCanvasRect(collageItem, frame, width, height);
            if (!collageItem.emptyUUID()) {
                result.add(collageItem.uuid, tcRectF.getRectF());
            }
        }
        return result;
    }


    TCRect getItemInCanvasRect(TCCollageItem collageItem, double frame, double width, double height) {
        double c = collageItem.ratioRect.left <= 0 ? 2 * frame : frame;
        double c2 = collageItem.ratioRect.left + collageItem.ratioRect.right >= 1.0 ? 2 * frame : frame;
        double c3 = collageItem.ratioRect.top <= 0 ? 2 * frame : frame;
        double c4 = frame;
        if (collageItem.ratioRect.top + collageItem.ratioRect.bottom >= 1.0) {
            c4 = frame * 2;
        }
        return new TCRect((collageItem.ratioRect.left * width) + c, (collageItem.ratioRect.top * height) + c3, (collageItem.ratioRect.right * width) - (c + c2), (collageItem.ratioRect.bottom * height) - (c3 + c4));
    }


    private void reCollage() {
        this.collageItems.clear();
        if (bitmapMap.isEmpty()) {
            collageItems.add(new TCCollageItem(null, new TCRect(0.0, 0.0, 1.0, 1.0)));
        } else {
            HashMap<String, Double> ratioMap = new HashMap<>();
            for (Map.Entry<String, TCBitmap> entry : bitmapMap.entrySet()) {
                String uuid = entry.getKey();
                TCBitmap bitmap = entry.getValue();
                ratioMap.put(uuid, bitmap.getWidth() * 1.0 / bitmap.getHeight());
            }
            TCShuffle totalShuffle = TCShuffle.getTotalShuffle(ratioMap, this.width, this.height);
            //这个返回这个结果，就是计算的质量Math.abs(totalShuffle.a() - ((this.width) / this.height)) < 0.01d;
            //一般来说 几张图计算出来 留白比较多，这个值就是false
            if (totalShuffle != null) {
                totalShuffle.a(this.collageItems, new TCRect(0.0, 0.0, 1.0, 1.0));
            }
        }
    }


    private TCCollageItem getEmptyItemOrNull() {
        List<TCCollageItem> list = getEmptyUUIDCollageItems();
        return list.isEmpty() ? null : list.get(0);
    }


    private List<TCCollageItem> getEmptyUUIDCollageItems() {
        List<TCCollageItem> list = new ArrayList<>();
        for (TCCollageItem collageItem : this.collageItems) {
            if (collageItem.emptyUUID()) {
                list.add(collageItem);
            }
        }
        return list;
    }

    //寻找collageItems的最大的max bound的index， -1无效
    private int getMaxBoundIndex() {
        if (collageItems.isEmpty()) return -1;
        int maxIndex = 0;
        double maxBound = -1000;
        for (int i = 0; i < this.collageItems.size(); i++) {
            double bound = this.collageItems.get(i).getRatioMaxBound(this.width, this.height);
            if (bound > maxBound) {
                maxBound = bound;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    //根据index获取TCRect
    private TCRect getTCRect(int i) {
        TCRect tcRect;
        TCCollageItem item = this.collageItems.get(i);
        //0.4-0.59之间的随机数
        double randomNum = 0.4 + (new Random().nextInt(20) / 100.0);
        TCRect ratioRect = item.ratioRect;
        if (item.ratioRect.right * this.width < item.ratioRect.bottom * this.height) {
            item.ratioRect = new TCRect(ratioRect.left, ratioRect.top, ratioRect.right, ratioRect.bottom * randomNum);
            tcRect = new TCRect(ratioRect.left, ratioRect.top + (ratioRect.bottom * randomNum), ratioRect.right, (1.0 - randomNum) * ratioRect.bottom);
        } else {
            item.ratioRect = new TCRect(ratioRect.left, ratioRect.top, ratioRect.right * randomNum, ratioRect.bottom);
            tcRect = new TCRect(ratioRect.left + (ratioRect.right * randomNum), ratioRect.top, (1.0 - randomNum) * ratioRect.right, ratioRect.bottom);
        }
        return tcRect;
    }


    public interface OnResultListener {
        void onSuccess(TCResult mResult);
        void onError(Exception e);
    }

}

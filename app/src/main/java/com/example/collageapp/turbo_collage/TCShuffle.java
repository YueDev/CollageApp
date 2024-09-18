package com.example.collageapp.turbo_collage;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TCShuffle {

    TCShuffle mShuffle1;
    TCShuffle mShuffle2;
    TCShuffle mShuffle3;

    String mUUIDString;
    double mRatio;
    TCJoin mTCJoin;

    private double a(double d, double d2) {
        List<TCCollageItem> arrayList = new ArrayList<>(getCount());
        refreshList(arrayList, new TCRect(0.0, 0.0, 1.0, 1.0));

        double result = 4726483295817170944.0;

        for (TCCollageItem item : arrayList) {
            double c2 = Math.min(item.ratioRect.right * d, result);
            result = Math.min(item.ratioRect.bottom * d2, c2);
        }

        return result;
    }

    // 把所有shuffleList链接起来
    private static TCShuffle linkAllShuffles(List<TCShuffle> shuffleList, TCShuffle noUse) {
        TCShuffle result;
        if (shuffleList.size() == 1) {
            result = shuffleList.get(0);
        } else {
            TCShuffle s = new TCShuffle();
            s.mTCJoin = TCJoin.TCLeftRightJoin;
            TCShuffle shuffle1 = linkAllShuffles(shuffleList.subList(0, (shuffleList.size() / 2)), s);
            TCShuffle shuffle2 = linkAllShuffles(shuffleList.subList(shuffleList.size() / 2, shuffleList.size()), s);
            s.mShuffle1 = shuffle1;
            s.mShuffle2 = shuffle2;
            result = s;
        }
        result.mShuffle3 = noUse;
        return result;
    }

    //根据ratioMap获取Shuffle
    private static TCShuffle getTotalShuffle(Map<String, Double> ratioMap) {
        TCShuffle shuffle;
        if (ratioMap.isEmpty()) {
            shuffle = null;
        } else {
            Set<String> keySet = ratioMap.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            List<String> randomKeyList = TCUtils.randomList(keyList);
            List<TCShuffle> shuffleList = new ArrayList<>(randomKeyList.size());
            for (String uuid : randomKeyList) {
                Double ratio = ratioMap.get(uuid);
                TCShuffle s = new TCShuffle();
                s.mUUIDString = uuid;
                s.setRatio(ratio == null ? 1.0 : ratio);
                shuffleList.add(s);
            }
            shuffle = linkAllShuffles(shuffleList, null);
        }
        return shuffle;
    }

    //根据ratioMap， width和height 获得Shuffle
    public static TCShuffle getTotalShuffle(Map<String, Double> ratioMap, double width, double height) {
        if (ratioMap.isEmpty()) return null;

        TCShuffle result;
        double canvasRatio = width / height;
        TCShuffle shuffle = getTotalShuffle(ratioMap);
        shuffle.refreshJoinType(canvasRatio);
        int i = 0;
        TCShuffle dVar = null;
        double r17 = 0;
        while (i < 500) {
            double z = r17;
            TCShuffle dVar2 = dVar;
            if (Math.abs(shuffle.getTotalRatio() - canvasRatio) < 0.01d) {
                double z2 = r17;
                if (dVar == null) {
                    dVar2 = shuffle;
                    z2 = shuffle.a(width, height);
                }
                double a2 = shuffle.a(width, height);
                z = z2;
                if (a2 > z2) {
                    z = a2;
                    dVar2 = shuffle;
                }
            }
            TCShuffle a3 = getTotalShuffle(ratioMap);
            a3.refreshJoinType(canvasRatio);
            TCShuffle dVar3 = shuffle;
            if (Math.abs(a3.getTotalRatio() - canvasRatio) < Math.abs(shuffle.getTotalRatio() - canvasRatio)) {
                dVar3 = a3;
            }
            i++;
            r17 = z;
            dVar = dVar2;
            shuffle = dVar3;
        }
        result = dVar;
        if (result == null) result = shuffle;
        return result;
    }

    private void setRatio(double setRatio) {
        this.mRatio = setRatio;
    }

    //获取Shuffle的count，uuid为空返回1
    //其他情况返回s1和s2的count之和
    private int getCount() {
        return this.mUUIDString != null ? 1 : this.mShuffle1.getCount() + this.mShuffle2.getCount();
    }

    //根据画布比例刷新每一个的shuffle的join type
    private void refreshJoinType(double canvasRatio) {
        if (this.mUUIDString == null) {
            List<TCShuffle> randomList = TCUtils.randomList(getShuffleList());
            for (TCShuffle shuffle : randomList) {
                //计算两种方向的ratio，看看哪个更接近canvasRatio
                double ratio1 = getTotalRatio();
                shuffle.changeJoinType();
                double ratio2 = getTotalRatio();

                if (Math.abs(ratio2 - canvasRatio) >= Math.abs(ratio1 - canvasRatio)) {
                    shuffle.changeJoinType();
                }
                //误差很小 直接返回即可
                if (Math.abs(ratio2 - canvasRatio) < 0.01) {
                    return;
                }
            }
        }
    }

    //将shuffle展平
    private List<TCShuffle> getShuffleList() {
        List<TCShuffle> result = new ArrayList<>();
        if (this.mUUIDString != null) {
            return result;
        }

        List<TCShuffle> shuffle1ShuffleList = this.mShuffle1.getShuffleList();
        List<TCShuffle> shuffle2ShuffleList = this.mShuffle2.getShuffleList();
        result.addAll(shuffle1ShuffleList);
        result.add(this);
        result.addAll(shuffle2ShuffleList);
        return result;
    }

    private void changeJoinType() {
        if (mTCJoin == TCJoin.TCLeftRightJoin) {
            mTCJoin = TCJoin.TCUpDownJoin;
        } else {
            mTCJoin = TCJoin.TCLeftRightJoin;
        }
    }

    //获取所有的ratio之和
    //如果uuid为空，返回ratio，否则返回s1和s2的ratio和
    public double getTotalRatio() {
        if (mUUIDString != null) return mRatio;
        double result;
        double ratio1 = this.mShuffle1.getTotalRatio();
        double ratio2 = this.mShuffle2.getTotalRatio();
        result = (this.mTCJoin == TCJoin.TCLeftRightJoin) ? (ratio1 + ratio2) : (1.0 / ((1.0 / ratio1) + (1.0 / ratio2)));
        return result;
    }

    //将计算的拼图结果放入list
    public void refreshList(List<TCCollageItem> list, TCRect bound) {
        if (list == null) return;
        if (this.mUUIDString != null) {
            list.add(new TCCollageItem(this.mUUIDString, bound));
            return;
        }

        TCRect s1Bound;
        TCRect s2Bound;
        double totalRatio1 = this.mShuffle1.getTotalRatio();
        double totalRatio2 = this.mShuffle2.getTotalRatio();
        if (this.mTCJoin == TCJoin.TCLeftRightJoin) {
            if (TCUtils.randomBoolean()) {
                s1Bound = new TCRect(bound.left, bound.top, bound.right * (totalRatio1 / (totalRatio2 + totalRatio1)), bound.bottom);
                s2Bound = new TCRect(bound.left + s1Bound.right, bound.top, bound.right - s1Bound.right, bound.bottom);
            } else {
                s2Bound = new TCRect(bound.left, bound.top, bound.right * (totalRatio2 / (totalRatio1 + totalRatio2)), bound.bottom);
                s1Bound = new TCRect(bound.left + s2Bound.right, bound.top, bound.right - s2Bound.right, bound.bottom);
            }
        } else {
            if (TCUtils.randomBoolean()) {
                s1Bound = new TCRect(bound.left, bound.top, bound.right, ((1.0 / totalRatio1) / ((1.0 / totalRatio1) + (1.0 / totalRatio2))) * bound.bottom);
                s2Bound = new TCRect(bound.left, bound.top + s1Bound.bottom, bound.right, bound.bottom - s1Bound.bottom);
            } else {
                s2Bound = new TCRect(bound.left, bound.top, bound.right, ((1.0 / totalRatio2) / ((1.0 / totalRatio1) + (1.0 / totalRatio2))) * bound.bottom);
                s1Bound = new TCRect(bound.left, bound.top + s2Bound.bottom, bound.right, bound.bottom - s2Bound.bottom);
            }
        }

        mShuffle1.refreshList(list, s1Bound);
        mShuffle2.refreshList(list, s2Bound);
    }
}
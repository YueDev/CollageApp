package com.example.collageapp.turbo_collage;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TCShuffle {

    //循环次数 源代码500次，改成50次效果也还行
    private static final int LOOP_NUM = 50;

    TCShuffle mShuffle1;
    TCShuffle mShuffle2;
    TCShuffle mShuffle3;

    String mUUIDString;
    double mRatio;
    TCJoin mTCJoin;

    private double getMin(double d, double d2) {
        List<TCCollageItem> arrayList = new ArrayList<>(getCount());
        refreshList(arrayList, new TCRect(0.0, 0.0, 1.0, 1.0));

        double result = 4726483295817170944.0;

        for (TCCollageItem item : arrayList) {
            double c2 = Math.min(item.getRatioRect().getWidth() * d, result);
            result = Math.min(item.getRatioRect().getHeight() * d2, c2);
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


    //gpt优化版本 说实话源码没看懂，在下边
    public static TCShuffle getTotalShuffle(Map<String, Double> ratioMap, double width, double height) {
        // 如果映射为空，直接返回null
        if (ratioMap.isEmpty()) return null;

        double canvasRatio = width / height;  // 计算画布的宽高比
        TCShuffle bestShuffle = null;  // 用于存储最优的shuffle对象
        double bestMinValue = 0;  // 最优shuffle对应的最小值
        TCShuffle currentShuffle = getTotalShuffle(ratioMap);  // 获取初始shuffle对象
        currentShuffle.refreshJoinType(canvasRatio);  // 更新当前shuffle的类型

        // 迭代500次以找到最佳shuffle
        for (int i = 0; i < LOOP_NUM; i++) {
            // 如果当前shuffle的比例非常接近画布比例
            if (Math.abs(currentShuffle.getTotalRatio() - canvasRatio) < 0.01d) {
                double currentMinValue = currentShuffle.getMin(width, height);  // 获取当前shuffle的最小值

                // 如果这是第一次找到合适的shuffle，或者找到了更优的最小值
                if (bestShuffle == null || currentMinValue > bestMinValue) {
                    bestShuffle = currentShuffle;  // 更新最优shuffle
                    bestMinValue = currentMinValue;  // 更新最优shuffle对应的最小值
                }
            }

            // 生成新的shuffle对象并比较
            TCShuffle newShuffle = getTotalShuffle(ratioMap);
            newShuffle.refreshJoinType(canvasRatio);

            // 如果新shuffle的总比例更接近画布比例，则更新当前shuffle
            if (Math.abs(newShuffle.getTotalRatio() - canvasRatio) < Math.abs(currentShuffle.getTotalRatio() - canvasRatio)) {
                currentShuffle = newShuffle;
            }
        }

        // 返回找到的最佳shuffle对象，如果没有找到则返回最后一次的shuffle
        return bestShuffle != null ? bestShuffle : currentShuffle;
    }

    //根据ratioMap， width和height 获得Shuffle
    // 源码  尽量不要删除
//    public static TCShuffle getTotalShuffle(Map<String, Double> ratioMap, double width, double height) {
//        if (ratioMap.isEmpty()) return null;
//
//        TCShuffle result;
//        double canvasRatio = width / height;
//        TCShuffle shuffle = getTotalShuffle(ratioMap);
//        shuffle.refreshJoinType(canvasRatio);
//        int i = 0;
//        TCShuffle dVar = null;
//        double r17 = 0;
//        while (i < 500) {
//            double z = r17;
//            TCShuffle dVar2 = dVar;
//            if (Math.abs(shuffle.getTotalRatio() - canvasRatio) < 0.01d) {
//                double z2 = r17;
//                if (dVar == null) {
//                    dVar2 = shuffle;
//                    z2 = shuffle.getMin(width, height);
//                }
//                double a2 = shuffle.getMin(width, height);
//                z = z2;
//                if (a2 > z2) {
//                    z = a2;
//                    dVar2 = shuffle;
//                }
//            }
//            TCShuffle a3 = getTotalShuffle(ratioMap);
//            a3.refreshJoinType(canvasRatio);
//            TCShuffle dVar3 = shuffle;
//            if (Math.abs(a3.getTotalRatio() - canvasRatio) < Math.abs(shuffle.getTotalRatio() - canvasRatio)) {
//                dVar3 = a3;
//            }
//            i++;
//            r17 = z;
//            dVar = dVar2;
//            shuffle = dVar3;
//        }
//        result = dVar;
//        if (result == null) result = shuffle;
//        return result;
//    }

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
                s1Bound = new TCRect(bound.getLeft(), bound.getTop(), bound.getWidth() * (totalRatio1 / (totalRatio2 + totalRatio1)), bound.getHeight());
                s2Bound = new TCRect(bound.getLeft() + s1Bound.getWidth(), bound.getTop(), bound.getWidth() - s1Bound.getWidth(), bound.getHeight());
            } else {
                s2Bound = new TCRect(bound.getLeft(), bound.getTop(), bound.getWidth() * (totalRatio2 / (totalRatio1 + totalRatio2)), bound.getHeight());
                s1Bound = new TCRect(bound.getLeft() + s2Bound.getWidth(), bound.getTop(), bound.getWidth() - s2Bound.getWidth(), bound.getHeight());
            }
        } else {
            if (TCUtils.randomBoolean()) {
                s1Bound = new TCRect(bound.getLeft(), bound.getTop(), bound.getWidth(), ((1.0 / totalRatio1) / ((1.0 / totalRatio1) + (1.0 / totalRatio2))) * bound.getHeight());
                s2Bound = new TCRect(bound.getLeft(), bound.getTop() + s1Bound.getHeight(), bound.getWidth(), bound.getHeight() - s1Bound.getHeight());
            } else {
                s2Bound = new TCRect(bound.getLeft(), bound.getTop(), bound.getWidth(), ((1.0 / totalRatio2) / ((1.0 / totalRatio1) + (1.0 / totalRatio2))) * bound.getHeight());
                s1Bound = new TCRect(bound.getLeft(), bound.getTop() + s2Bound.getHeight(), bound.getWidth(), bound.getHeight() - s2Bound.getHeight());
            }
        }

        mShuffle1.refreshList(list, s1Bound);
        mShuffle2.refreshList(list, s2Bound);
    }
}
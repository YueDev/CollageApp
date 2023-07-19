package com.example.collageapp.util;

import com.example.collageapp.R;
import com.example.collageapp.bean.BorderBean;
import com.example.collageapp.bean.RatioBean;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Yue on 2022/10/28.
 */
public class ResourcesUtil {

    private static List<RatioBean> sRatioBeans;
    private static List<BorderBean> sBorderBeans;


    public static List<RatioBean> getRatioBeans() {
        if (sRatioBeans == null) {
            sRatioBeans = new ArrayList<>();
            sRatioBeans.add(new RatioBean(1, 1, R.drawable.icon_ratio_custom));
            sRatioBeans.add(new RatioBean(9, 16, R.drawable.icon_ratio_9_16));
            sRatioBeans.add(new RatioBean(1, 1, R.drawable.icon_ratio_1_1));
            sRatioBeans.add(new RatioBean(4, 5, R.drawable.icon_ratio_4_5));
            sRatioBeans.add(new RatioBean(3, 4, R.drawable.icon_ratio_3_4));
            sRatioBeans.add(new RatioBean(4, 3, R.drawable.icon_ratio_4_3));
            sRatioBeans.add(new RatioBean(2, 3, R.drawable.icon_ratio_2_3));
            sRatioBeans.add(new RatioBean(3, 2, R.drawable.icon_ratio_3_2));
            sRatioBeans.add(new RatioBean(5, 7, R.drawable.icon_ratio_5_7));
            sRatioBeans.add(new RatioBean(7, 5, R.drawable.icon_ratio_7_5));
            sRatioBeans.add(new RatioBean(5, 4, R.drawable.icon_ratio_5_4));
            sRatioBeans.add(new RatioBean(16, 9, R.drawable.icon_ratio_16_9));
        }
        return sRatioBeans;
    }

    public static List<BorderBean> getBorderBeans() {
        if (sBorderBeans == null) {
            sBorderBeans = new ArrayList<>();
            sBorderBeans.add(new BorderBean(0.0f, 0.0f, R.drawable.icon_border_close));
            sBorderBeans.add(new BorderBean(2.0f, 2.0f, R.drawable.icon_border_open_1));
            sBorderBeans.add(new BorderBean(4.0f, 4.0f, R.drawable.icon_border_open_2));
        }
        return sBorderBeans;
    }
}

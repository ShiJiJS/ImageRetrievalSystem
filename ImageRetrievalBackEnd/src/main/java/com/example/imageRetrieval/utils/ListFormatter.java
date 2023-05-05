package com.example.imageRetrieval.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @description:
 * @author: ShiJi
 * @time: 2023/3/17 20:17
 */
public class ListFormatter {

    //保留两位小数
    public static void formatDoubleList(List<Double> list){
        for (int i = 0; i < list.size(); i++) {
            Double d = list.get(i);
            list.set(i, new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
    }
}

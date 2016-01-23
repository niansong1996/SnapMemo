package com.alan.sphare.presentation.tool;

import java.util.ArrayList;

/**
 * Created by Alan on 2016/1/21.
 */
public class ArrayGenerator {

    /**
     * 获得生成的start-end的整数表
     *
     * @param start
     * @param end
     * @return
     */
    public static ArrayList<Integer> getGeneratedArray(int start, int end) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = start; i <= end; i++) {
            arrayList.add(i);
        }
        return arrayList;
    }
}

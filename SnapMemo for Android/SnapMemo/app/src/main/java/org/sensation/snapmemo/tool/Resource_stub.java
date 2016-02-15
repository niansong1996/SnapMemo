package org.sensation.snapmemo.tool;

import org.sensation.snapmemo.VO.MemoVO;

import java.util.ArrayList;

/**
 * Created by Alan on 2016/2/4.
 */
public class Resource_stub {
    public ArrayList<MemoVO> getMemoVOs() {
        MemoVO memoVO1 = new MemoVO("00000120160208132435143", "寒假培训", "2016-02-04", "周四", "学校组织大家参与寒假培训，于二月四日开始，地点在四望亭东边的益达培训中心。"),
                memoVO2 = new MemoVO("00000120160208231323456", "放假通知", "2016-01-23", "周六", "请各班班长注意通知同学们去教务网填写放假返校信息，谢谢!");
        ArrayList<MemoVO> memoVOs = new ArrayList<MemoVO>();
        memoVOs.add(memoVO1);
        memoVOs.add(memoVO2);
        return memoVOs;
    }
}

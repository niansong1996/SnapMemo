package com.alan.sphare.model.logicservice;

import com.alan.sphare.presentation.widget.SlideCutListView;

/**
 * Created by Alan on 2016/1/21.
 */
public interface RemoveListener {
    /**
     * 当ListView item滑出屏幕，回调这个接口
     * 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
     *
     * @author xiaanming
     */
    public void removeItem(SlideCutListView.RemoveDirection direction, int position);
}

package com.alan.sphare.presentation.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alan.sphare.R;
import com.alan.sphare.model.VO.FreeDateTimeVO;

import java.util.List;

/**
 * Created by Alan on 2016/1/19.
 */
public class FreeTimeAdapter extends ArrayAdapter<FreeDateTimeVO> {
    int resourceID;

    public FreeTimeAdapter(Context context, int resourceID, List<FreeDateTimeVO> freeDateTimeVOList) {
        super(context, resourceID, freeDateTimeVOList);
        this.resourceID = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FreeDateTimeVO freeDateTimeVO = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID, null);
        TextView userIDTextView = (TextView) view.findViewById(R.id.userID);
        userIDTextView.setText(freeDateTimeVO.getUserID());
        TimeBar timeBar = (TimeBar) view.findViewById(R.id.timebar);
        timeBar.repaint(freeDateTimeVO.getFreeDateTime());
        return view;
    }

}
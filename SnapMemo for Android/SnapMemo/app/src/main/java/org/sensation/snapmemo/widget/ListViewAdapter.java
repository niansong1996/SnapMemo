package org.sensation.snapmemo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;

import java.util.List;

/**
 * Created by Alan on 2016/2/3.
 */
public class ListViewAdapter extends ArrayAdapter<MemoVO> {

    private int resourceID;
    private Context context;

    public ListViewAdapter(Context context, int resource, List<MemoVO> memoVOs) {
        super(context, resource, memoVOs);
        resourceID = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemoVO memoVO = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, null);
        } else {
            view = convertView;
        }

        TextView topic = (TextView) view.findViewById(R.id.listview_topic_content);
        topic.setText(memoVO.getTopic());

        TextView time = (TextView) view.findViewById(R.id.listview_time_content);
        time.setText(memoVO.getDate());

        TextView content = (TextView) view.findViewById(R.id.listview_content_text);
        content.setText(memoVO.getContent());

        return view;
    }
}

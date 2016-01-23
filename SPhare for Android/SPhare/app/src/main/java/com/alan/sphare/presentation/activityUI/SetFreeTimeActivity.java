package com.alan.sphare.presentation.activityUI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.alan.sphare.R;
import com.alan.sphare.logic.groupBl.GroupBlImpl;
import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.Tool.Time;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.TimeVO;
import com.alan.sphare.model.logicservice.GroupBlService;
import com.alan.sphare.model.logicservice.RemoveListener;
import com.alan.sphare.presentation.tool.ArrayGenerator;
import com.alan.sphare.presentation.widget.SlideCutListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetFreeTimeActivity extends AppCompatActivity implements RemoveListener, Toolbar.OnMenuItemClickListener {

    Toolbar toolbar;
    TextView headDate;
    SlideCutListView slideCutListView;
    ArrayAdapter<String> listViewAdapter;
    ArrayAdapter<Integer> spinnerAdapter;
    Spinner startHour, startMin, endHour, endMin;
    int starthour, startmin, endhour, endmin;
    Calendar calendar = Calendar.getInstance();
    Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    List<String> freeTimeList = new ArrayList<String>();

    Thread task;
    BackgroundTask backgroundTask;
    boolean gotInternetInfo;
    int internetRequest;
    String groupID, userID;
    HashMap<Date, TimeVO[]> userFreeTime;
    TimeTableVO[] timeTableList;
    GroupBlService groupBl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_free_time);

        //获得从上一个Activity传过来的数据
        groupID = getIntent().getStringExtra("groupID");
        userID = getIntent().getStringExtra("userID");

        groupBl = new GroupBlImpl();

        init();

        //开启后台网络线程
        backgroundTask = new BackgroundTask();
        internetRequest = 1;
        task = new Thread(backgroundTask);
        task.start();
    }

    private void init() {
        headDate = (TextView) findViewById(R.id.freeTimeDate);
        headDate.setText("目前选中的日期： " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());

        initToolBar();

        initSpinner();

        initFloatButton();

    }

    /**
     * 初始化Spinner
     */
    private void initSpinner() {
        startHour = (Spinner) findViewById(R.id.startHour);
        startMin = (Spinner) findViewById(R.id.startMin);
        endHour = (Spinner) findViewById(R.id.endHour);
        endMin = (Spinner) findViewById(R.id.endMin);
        spinnerAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ArrayGenerator.getGeneratedArray(8, 24));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startHour.setAdapter(spinnerAdapter);
        endHour.setAdapter(spinnerAdapter);
        spinnerAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ArrayGenerator.getGeneratedArray(0, 59));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startMin.setAdapter(spinnerAdapter);
        endMin.setAdapter(spinnerAdapter);
        Log.d("SPhare", "initSpinner: adapter");

        startHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                starthour = position + 8;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        startMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startmin = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        endHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endhour = position + 8;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        endMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endmin = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        //根据date来加载freeTimeList
        TimeVO[] timeVOList = null;
        Iterator<Map.Entry<Date, TimeVO[]>> it = userFreeTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Date, TimeVO[]> entry = it.next();
            Date tempDate = entry.getKey();
            if (tempDate.isEquals(date)) {
                timeVOList = entry.getValue();
            }
        }
        for (TimeVO timeVO : timeVOList) {
            Time startTime = timeVO.getStartTime(), endTime = timeVO.getEndTime();
            String timePeriodString = Time.amplify(startTime.hour) + ":" + Time.amplify(startTime.minute) +
                    " ~ " + Time.amplify(endTime.hour) + ":" + Time.amplify(endTime.minute);
            freeTimeList.add(timePeriodString);
        }

        listViewAdapter = new ArrayAdapter<String>(this, R.layout.setfreetime_item, R.id.listitem, freeTimeList);
        slideCutListView = (SlideCutListView) findViewById(R.id.slidecutListview);
        slideCutListView.setRemoveListener(this);
        slideCutListView.setAdapter(listViewAdapter);
    }


    private void initFloatButton() {
        Button addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开启后台线程
                internetRequest = 2;
                gotInternetInfo = false;
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.subtoolbar);
        toolbar.setTitle("设置空余时间");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.freetime_menu, menu);
        return true;
    }

    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        listViewAdapter.remove(listViewAdapter.getItem(position));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subDatePicker) {
            Log.d("SPhare", "onMenuItemClick: click gallary");
            new DatePickerDialog(SetFreeTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker dp, int year, int month, int day) {
                    date = new Date(year, month + 1, day);
                    //选择完日期后更新的显示
                    headDate.setText("目前选中的日期： " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
                    //刷新空余时间列表
                    initListView();
                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        return true;
    }

    /**
     * 后台线程处理的网络工作
     */
    class BackgroundTask implements Runnable {
        @Override
        public void run() {
            while (!gotInternetInfo) {
                if (internetRequest == 1) {//获得TimeTable
                    timeTableList = groupBl.getTimeTable(groupID);
                    //获得匹配userID的 日期-时间段 哈希表
                    for (TimeTableVO timeTable : timeTableList) {
                        if (timeTable.getUserID().equals(userID)) {
                            userFreeTime = timeTable.getUserFreeTime();
                        }
                    }
                    //初始化listView
                    initListView();

                    gotInternetInfo = true;
                }

                if (internetRequest == 2) {//设置空余时间

                    //向列表和哈希表中都要添加新设置的空余时间
                    freeTimeList.add(starthour + ":" + startmin + " ~ " + endhour + ":" + endmin);
                    //用新的TimeVO[]取代旧的
                    Time startTime = new Time(starthour, startmin), endTime = new Time(endhour, endmin);
                    TimeVO newTimeVO = new TimeVO(startTime, endTime);
                    TimeVO[] oldTimeVOList = userFreeTime.get(date), newTimeVOList = new TimeVO[oldTimeVOList.length + 1];
                    for (int i = 0; i < oldTimeVOList.length; i++) {
                        newTimeVOList[i] = oldTimeVOList[i];
                    }
                    newTimeVOList[newTimeVOList.length - 1] = newTimeVO;
                    userFreeTime.remove(date);
                    userFreeTime.put(date, newTimeVOList);


                    TimeVO[] timeVO = {new TimeVO(startTime, endTime)};
                    FreeDateTimeVO freeDateTimeVO = new FreeDateTimeVO(userID, date, timeVO);
                    groupBl.setFreeTime(freeDateTimeVO, groupID);
                    listViewAdapter.notifyDataSetChanged();

                    gotInternetInfo = true;
                }
            }
        }
    }
}

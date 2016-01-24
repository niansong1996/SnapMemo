package com.alan.sphare.presentation.activityUI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * Handler标识
     */
    final int INITSUCCESS = 1, INITFAIL = 2, UPDATESUCCESS = 3, UPDATEFAIL = 4, OPERATIONERR = 5;

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
    HashMap<Date, TimeVO[]> userFreeTime = new HashMap<Date, TimeVO[]>();
    TimeTableVO[] timeTableList;
    GroupBlService groupBl;
    Time deletedStartTime, deletedEndTime;

    /**
     * UIHandler处理网络线程中的界面反馈
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INITSUCCESS) {
                //刷新成功反馈
                Toast.makeText(SetFreeTimeActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            } else if (msg.what == INITFAIL) {
                //处理网络传输失败或者不存在分组的情况
                Toast.makeText(SetFreeTimeActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            } else if (msg.what == UPDATESUCCESS) {
                //更新列表显示
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(SetFreeTimeActivity.this, "添加成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == UPDATEFAIL) {
                //更新列表显示
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(SetFreeTimeActivity.this, "更新失败，请检查网络", Toast.LENGTH_LONG).show();
            } else if (msg.what == OPERATIONERR) {
                //操作过快的反馈
                Toast.makeText(SetFreeTimeActivity.this, "操作过快，请重新选择日期重新进行操作", Toast.LENGTH_SHORT).show();
            }
        }
    };

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
        if (timeVOList != null) {
            for (TimeVO timeVO : timeVOList) {
                Time startTime = timeVO.getStartTime(), endTime = timeVO.getEndTime();
                String timePeriodString = Time.amplify(startTime.hour) + ":" + Time.amplify(startTime.minute) +
                        " ~ " + Time.amplify(endTime.hour) + ":" + Time.amplify(endTime.minute);
                freeTimeList.add(timePeriodString);
            }
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
                //开启添加线程
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
        getMenuInflater().inflate(R.menu.freetime_menu, menu);
        return true;
    }

    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        String deletingTimePeriod = listViewAdapter.getItem(position);
        String deletingStartTime = deletingTimePeriod.split(" ~ ")[0], deletingEndTime = deletingTimePeriod.split(" ~ ")[1];
        int deletingStartHour = Integer.parseInt(deletingStartTime.split(":")[0]),
                deletingStartMin = Integer.parseInt(deletingStartTime.split(":")[1]),
                deletingEndHour = Integer.parseInt(deletingEndTime.split(":")[0]),
                deletingEndMin = Integer.parseInt(deletingEndTime.split(":")[1]);
        //设置被删除的时间以便更新
        deletedStartTime = new Time(deletingStartHour, deletingStartMin);
        deletedEndTime = new Time(deletingEndHour, deletingEndMin);
        listViewAdapter.remove(deletingTimePeriod);

        //打开删除线程
        internetRequest = 3;
        gotInternetInfo = false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subDatePicker) {
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
            while (true) {
                if (!gotInternetInfo) {
                    if (internetRequest == 1) {//获得TimeTable
                        timeTableList = groupBl.getTimeTable(groupID);
                        if (timeTableList != null) {
                            //获得匹配userID的 日期-时间段 哈希表
                            for (TimeTableVO timeTable : timeTableList) {
                                if (timeTable.getUserID().equals(userID)) {
                                    userFreeTime = timeTable.getUserFreeTime();
                                }
                            }
                            //初始化listView
                            initListView();

                            //反馈
                            Message message = new Message();
                            message.what = INITSUCCESS;
                            handler.sendMessage(message);
                        } else {
                            //初始化listView
                            initListView();

                            //反馈
                            Message message = new Message();
                            message.what = INITFAIL;
                            handler.sendMessage(message);
                        }


                        gotInternetInfo = true;
                    }

                    if (internetRequest == 2) {//添加空余时间

                        //向列表和哈希表中都要添加新设置的空余时间
                        freeTimeList.add(Time.amplify(starthour) + ":" + Time.amplify(startmin) + " ~ " + Time.amplify(endhour) + ":" + Time.amplify(endmin));
                        //用新的TimeVO[]取代旧的
                        Time startTime = new Time(starthour, startmin), endTime = new Time(endhour, endmin);
                        TimeVO newTimeVO = new TimeVO(startTime, endTime);
                        TimeVO[] oldTimeVOList = userFreeTime.get(date);
                        TimeVO[] newTimeVOList;
                        if (oldTimeVOList != null) {
                            newTimeVOList = new TimeVO[oldTimeVOList.length + 1];
                            for (int i = 0; i < oldTimeVOList.length; i++) {
                                newTimeVOList[i] = oldTimeVOList[i];
                            }
                            newTimeVOList[newTimeVOList.length - 1] = newTimeVO;
                        } else {
                            newTimeVOList = new TimeVO[1];
                            newTimeVOList[newTimeVOList.length - 1] = newTimeVO;
                        }
                        userFreeTime.remove(date);
                        userFreeTime.put(date, newTimeVOList);


                        //向网络发送添加请求
                        TimeVO[] timeVO = {new TimeVO(startTime, endTime)};
                        FreeDateTimeVO freeDateTimeVO = new FreeDateTimeVO(userID, date, timeVO);
                        boolean result = groupBl.addFreeTime(freeDateTimeVO, groupID);


                        //更新反馈
                        if (result) {
                            Message message = new Message();
                            message.what = UPDATESUCCESS;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = UPDATEFAIL;
                            handler.sendMessage(message);
                        }

                        gotInternetInfo = true;
                    }

                    if (internetRequest == 3) {//删除空余时间
                        //删除过快可能导致的理论错误
                        if ((userFreeTime.get(date).length - 1) != freeTimeList.size()) {
                            Message message = new Message();
                            message.what = OPERATIONERR;
                            handler.sendMessage(message);

                        } else {
                            //用新的键值对取代旧的
                            TimeVO[] newTimeVOList = new TimeVO[userFreeTime.get(date).length - 1];
                            //遍历界面的freeTimeList
                            for (int i = 0; i < freeTimeList.size(); i++) {
                                //将时间分割成hh:mm型
                                String[] timePeriod = freeTimeList.get(i).split(" ~ ");
                                //将时间分割成hh和mm
                                String[] startTimeSection = timePeriod[0].split(":"), endTimeSection = timePeriod[1].split(":");
                                int startHour = Integer.parseInt(startTimeSection[0]), startMin = Integer.parseInt(startTimeSection[1]),
                                        endHour = Integer.parseInt(endTimeSection[0]), endMin = Integer.parseInt(endTimeSection[1]);
                                Time startTime = new Time(startHour, startMin), endTime = new Time(endHour, endMin);
                                newTimeVOList[i] = new TimeVO(startTime, endTime);
                            }

                            //在哈希表里替换
                            userFreeTime.remove(date);
                            userFreeTime.put(date, newTimeVOList);

                            //向网络发送添加请求
                            TimeVO[] timeVOList = {new TimeVO(deletedStartTime, deletedEndTime)};
                            FreeDateTimeVO freeDateTimeVO = new FreeDateTimeVO(userID, date, timeVOList);
                            boolean result = groupBl.deleteFreeTime(freeDateTimeVO, groupID);

                            //反馈
                            if (result) {
                                Message message = new Message();
                                message.what = UPDATESUCCESS;
                                handler.sendMessage(message);
                            } else {
                                Message message = new Message();
                                message.what = UPDATEFAIL;
                                handler.sendMessage(message);
                            }

                            gotInternetInfo = true;
                        }
                    }
                }
            }
        }
    }
}

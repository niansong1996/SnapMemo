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
import com.alan.sphare.model.tool.Date;
import com.alan.sphare.model.tool.Time;
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
    //--------------------常量设置--------------------

    /**
     * 日志标识
     */
    private String TAG;

    /**
     * Handler标识
     */
    private final int INITSUCCESS = 1, INITFAIL = 2, UPDATESUCCESS = 3, UPDATEFAIL = 4, OPERATIONERR = 5;

    private final int GET = 1, ADD = 2, DELETE = 3;


    //--------------------主界面部件及布局--------------------

    /**
     * 工具栏
     */
    Toolbar toolbar;

    /**
     * 显示日期
     */
    TextView headDate;

    /**
     * 列表
     */
    SlideCutListView slideCutListView;

    /**
     * listView适配器
     */
    ArrayAdapter<String> listViewAdapter;

    /**
     * 显示在listView中的内容
     */
    List<String> freeTimeList = new ArrayList<String>();

    /**
     * 日起选择Spinner
     */
    Spinner startHour, startMin, endHour, endMin;

    /**
     * Spinner适配器
     */
    ArrayAdapter<Integer> spinnerAdapter;

    /**
     * 开始结束时间
     */
    int starthour, startmin, endhour, endmin;

    Calendar calendar = Calendar.getInstance();
    /**
     * 日期
     */
    Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

    //--------------------数据及接口--------------------
    /**
     * 从主活动传递的信息
     */
    String groupID, userID;

    /**
     * 用户本地的自由时间表
     */
    HashMap<Date, TimeVO[]> userFreeTime = new HashMap<Date, TimeVO[]>();

    /**
     * 小组的时间表
     */
    TimeTableVO[] timeTableList;

    /**
     * 依赖的小组逻辑接口
     */
    GroupBlService groupBl;

    /**
     * 检测到的被删除的时间段
     */
    Time deletedStartTime, deletedEndTime;

    //--------------------后台--------------------

    /**
     * 后台处理网络工作的线程
     */
    Thread task;
    BackgroundTask backgroundTask;

    /**
     * 网络获取状况标志位
     */
    private boolean gotInternetInfo;

    /**
     * 网络请求类型标识
     */
    int internetRequest;

    /**
     * UIHandler处理网络线程中的界面反馈
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INITSUCCESS) {
                //初始化listView
                initListView();
                //刷新成功反馈
                Toast.makeText(SetFreeTimeActivity.this, getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == INITFAIL) {
                //初始化listView
                initListView();
                //处理网络传输失败或者不存在分组的情况
                Toast.makeText(SetFreeTimeActivity.this, getString(R.string.internet_fail), Toast.LENGTH_SHORT).show();
            } else if (msg.what == UPDATESUCCESS) {
                //更新列表显示
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(SetFreeTimeActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == UPDATEFAIL) {
                //更新列表显示
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(SetFreeTimeActivity.this, getString(R.string.update_fail), Toast.LENGTH_SHORT).show();
            } else if (msg.what == OPERATIONERR) {
                //操作过快的反馈
                Toast.makeText(SetFreeTimeActivity.this, getString(R.string.operation_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_free_time);
        TAG = getString(R.string.app_name);

        //获得从上一个Activity传过来的数据
        groupID = getIntent().getStringExtra("groupID");
        userID = getIntent().getStringExtra("userID");

        groupBl = new GroupBlImpl();

        init();

        //开启后台网络线程
        backgroundTask = new BackgroundTask();
        openThread(GET);
        task = new Thread(backgroundTask);
        task.start();
    }

    private void init() {

        headDate = (TextView) findViewById(R.id.freeTimeDate);
        updateDate();

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
                openThread(ADD);
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.subtoolbar);
        toolbar.setTitle(getString(R.string.set_free_time_title));
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
        openThread(DELETE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subDatePicker) {
            new DatePickerDialog(SetFreeTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker dp, int year, int month, int day) {
                    date = new Date(year, month + 1, day);
                    updateDate();
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
     * 更新日期标题显示
     */
    private void updateDate() {
        headDate.setText("日期:  " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
    }

    /**
     * 打开网络线程
     *
     * @param request 请求类型
     */
    private void openThread(int request) {
        internetRequest = request;
        gotInternetInfo = false;
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

                            sendMessage(INITSUCCESS);
                        } else {
                            sendMessage(INITFAIL);
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
                            System.arraycopy(oldTimeVOList, 0, newTimeVOList, 0, oldTimeVOList.length);
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
                            sendMessage(UPDATESUCCESS);
                        } else {
                            sendMessage(UPDATEFAIL);
                        }

                        gotInternetInfo = true;
                    }

                    if (internetRequest == 3) {//删除空余时间
                        //删除过快可能导致的理论错误
                        if ((userFreeTime.get(date).length - 1) != freeTimeList.size()) {
                            sendMessage(OPERATIONERR);

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
                                sendMessage(UPDATESUCCESS);
                            } else {
                                sendMessage(UPDATEFAIL);
                            }

                            gotInternetInfo = true;
                        }
                    }
                }
            }
        }

        /**
         * 给UI发送信息，进行UI反馈
         *
         * @param info 反馈信息类型
         */
        private void sendMessage(int info) {
            Message message = new Message();
            message.what = info;
            handler.sendMessage(message);
        }
    }
}

package com.alan.sphare.presentation.activityUI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.sphare.R;
import com.alan.sphare.logic.groupBl.stub.GroupBlImpl_stub;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.TimeVO;
import com.alan.sphare.model.VO.UserVO;
import com.alan.sphare.model.logicservice.GroupBlService;
import com.alan.sphare.model.tool.Date;
import com.alan.sphare.presentation.widget.FreeTimeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Toolbar.OnMenuItemClickListener, View.OnTouchListener, GestureDetector.OnGestureListener {

    //--------------------常量设置--------------------

    /**
     * 日志标识
     */
    private final String TAG = getString(R.string.app_name);

    /**
     * Handler标识
     */
    private final int SUCCESS = 1, FAIL = 2;


    //--------------------主界面部件及布局--------------------

    /**
     * 工具栏
     */
    Toolbar toolbar;

    /**
     * 左滑菜单
     */
    DrawerLayout drawer;

    /**
     * 列表
     */
    ListView listView;

    /**
     * 显示在listView中的内容
     */
    List<FreeDateTimeVO> freeDateTimeVOList;

    /**
     * listView适配器
     */
    FreeTimeAdapter freeTimeAdapter;

    /**
     * 显示日期
     */
    TextView dateText;

    //--------------------数据及接口--------------------

    /**
     * 用户数据
     */
    UserVO userVO;

    Calendar calendar = Calendar.getInstance();
    /**
     * 列表显示的空余时间日期，默认为当天日期
     */
    Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

    /**
     * 依赖的小组逻辑接口
     */
    GroupBlService groupBl;

    //--------------------后台--------------------

    /**
     * 后台处理网络工作的线程
     */
    Thread task;
    BackgroundTask backgroundTask;

    /**
     * 阻塞线程标志位
     */
    private boolean gotInternetInfo = false;

    /**
     * UIHandler处理网络线程中的界面反馈
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                //刷新listView
                freeTimeAdapter.notifyDataSetChanged();
                //刷新成功反馈
                Toast.makeText(MainActivity.this, getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();
            } else if (msg.what == FAIL) {
                //处理网络传输失败或者不存在分组的情况
                Toast.makeText(MainActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 手势检测器
     */
    GestureDetector detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View contentView = findViewById(R.id.content_main);
        contentView.setOnTouchListener(this);
        contentView.setLongClickable(true);

        detector = new GestureDetector(MainActivity.this, this);
        detector.setIsLongpressEnabled(true);

        groupBl = new GroupBlImpl_stub();
        backgroundTask = new BackgroundTask();

        init();
    }

    /**
     * 初始化部件
     */
    public void init() {

        //检查登录前置条件
        postLogin();

        //设置默认日期
        dateText = (TextView) findViewById(R.id.dateText);
        updateDate();

        //初始化工具栏
        initToolBar();

        //初始化侧滑菜单
        initDrawer();

        //初始化主界面列表
        initListView();
    }

    /**
     * 前置登录，检查是否本地有已经登录的用户，如果有就读出用户信息<br>
     * 第一迭代期间弱化登录功能
     *
     * @return true
     */
    private boolean postLogin() {
        String userID, groupID;
        Properties properties = new Properties();
        try {
            properties.load(getAssets().open("userInfo.properties"));
            userID = properties.getProperty("userID");
            groupID = properties.getProperty("groupID");
            userVO = new UserVO(userID, "", groupID, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 初始化工具栏
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.app_designer_ad));
        toolbar.setOnMenuItemClickListener(this);
    }

    /**
     * 初始化侧滑菜单
     */
    private void initDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View drawerHeadView = navigationView.getHeaderView(0);

        TextView userLoginID = (TextView) drawerHeadView.findViewById(R.id.userLoginID);
        userLoginID.setText(userVO.getUserID());

        ImageView userLogo = (ImageView) drawerHeadView.findViewById(R.id.logo);
        userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getString(R.string.todo), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化listView的显示
     */
    private void initListView() {

        listView = (ListView) findViewById(R.id.listView);
        freeDateTimeVOList = new ArrayList<FreeDateTimeVO>();

        //设置listView的适配器
        freeTimeAdapter = new FreeTimeAdapter(MainActivity.this, R.layout.freetime, freeDateTimeVOList);
        listView.setAdapter(freeTimeAdapter);

        //开启后台网络获得数据
        task = new Thread(backgroundTask);
        task.start();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.group) {
            Toast.makeText(MainActivity.this, getString(R.string.todo), Toast.LENGTH_SHORT).show();
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.webviewicon) {
            Toast.makeText(MainActivity.this, getString(R.string.open_websource), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.datePicker) {
            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker dp, int year, int month, int day) {
                    date = new Date(year, month + 1, day);
                    updateDate();

                    //选择完日期后更新列表中的显示
                    gotInternetInfo = false;
                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();

        } else if (item.getItemId() == R.id.setFreeTime) {
            Intent intent = new Intent(MainActivity.this, SetFreeTimeActivity.class);
            intent.putExtra("groupID", userVO.getGroupID());
            intent.putExtra("userID", userVO.getUserID());
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityX > 0) {
            drawer.openDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * 更新日期标题显示
     */
    private void updateDate() {
        dateText.setText("日期:  " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
    }

    /**
     * 后台线程处理的网络工作
     */
    class BackgroundTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (!gotInternetInfo) {
                    TimeTableVO[] timeTableList = groupBl.getTimeTable(userVO.getGroupID());
                    if (timeTableList != null) {
                        //先清空再将新内容放入
                        if (freeDateTimeVOList != null) {
                            freeDateTimeVOList.clear();
                        }
                        //遍历获得的组内TimeTable，将TimeTable转化为选定日期的FreeDateTime添加入listView的数组中
                        for (TimeTableVO timeTable : timeTableList) {
                            //组内用户的ID
                            String userID = timeTable.getUserID();
                            //用户的空余时间段
                            TimeVO[] timeVOList = null;

                            //遍历匹配的空余时间段
                            HashMap<Date, TimeVO[]> hashMap = timeTable.getUserFreeTime();
                            Iterator<Map.Entry<Date, TimeVO[]>> it = hashMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<Date, TimeVO[]> entry = it.next();
                                Date tempDate = entry.getKey();
                                if (tempDate.isEquals(date)) {
                                    timeVOList = entry.getValue();
                                }
                            }
                            //生成FreeDateTime添加入listView的数组中
                            FreeDateTimeVO freeDateTimeVO = new FreeDateTimeVO(userID, date, timeVOList);
                            freeDateTimeVOList.add(freeDateTimeVO);
                        }

                        sendMessage(SUCCESS);

                    } else {
                        sendMessage(FAIL);
                    }

                    //修改标志位使线程阻塞
                    gotInternetInfo = true;
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



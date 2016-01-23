package com.alan.sphare.presentation.activityUI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.alan.sphare.R;
import com.alan.sphare.logic.groupBl.GroupBlImpl;
import com.alan.sphare.logic.groupBl.stub.GroupBlImpl_stub;
import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.TimeVO;
import com.alan.sphare.model.VO.UserVO;
import com.alan.sphare.model.logicservice.GroupBlService;
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
        implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

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
     * 列表显示日期
     */
    TextView dateText;

    /**
     * 用户数据
     */
    UserVO userVO;

    /**
     * 依赖的小组逻辑接口
     */
    GroupBlService groupBl;

    Calendar calendar = Calendar.getInstance();
    /**
     * 列表显示的空余时间日期，默认为当天日期
     */
    Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupBl = new GroupBlImpl();

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
        dateText.setText("日期  " + date.getYear() + " " + date.getMonth() + " " + date.getDay());

        //初始化工具栏
        initToolBar();

        //初始化侧滑菜单
        initDrawer();

        //初始化主界面列表
        initListView();
    }

    /**
     * 前置登录，检查是否本地有已经登录的用户，如果有就读出用户信息
     *
     * @return
     */
    private boolean postLogin() {
        String userID, groupID;
        Properties properties = new Properties();
        try {
            properties.load(getAssets().open("app/src/main/res/userInfo.properties"));
            userID = properties.getProperty("userID");
            groupID = properties.getProperty("groupID");
            Log.d("SPhare", "postLogin: "+userID+" "+groupID);
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
        toolbar.setTitle("SPhare");
        toolbar.setSubtitle("Sensation Design.");
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
    }

    /**
     * 初始化listView的显示
     */
    private void initListView() {

        listView = (ListView) findViewById(R.id.listView);
//        TimeTableVO[] timeTableList = groupBl.getTimeTable(userVO.getGroupID());

        //stub
        GroupBlService groupBl = new GroupBlImpl_stub();
        TimeTableVO[] timeTableList = groupBl.getTimeTable("0000");
        List<FreeDateTimeVO> freeDateTimeVOList = new ArrayList<FreeDateTimeVO>();

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
                if (tempDate.getYear() == date.getYear() && tempDate.getMonth() == date.getMonth() && tempDate.getDay() == date.getDay()) {
                    timeVOList = entry.getValue();
                }
            }
            //生成FreeDateTime添加入listView的数组中
            FreeDateTimeVO freeDateTimeVO = new FreeDateTimeVO(userID, date, timeVOList);
            freeDateTimeVOList.add(freeDateTimeVO);
        }

        //设置listView的适配器
        FreeTimeAdapter freeTimeAdapter = new FreeTimeAdapter(MainActivity.this, R.layout.freetime, freeDateTimeVOList);
        listView.setAdapter(freeTimeAdapter);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.group) {
            // Handle the camera action
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.datePicker) {
            new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker dp, int year, int month, int day) {
                    date = new Date(year, month + 1, day);
                    //选择完日期后更新列表中的显示
                    initListView();
                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();

        } else if (item.getItemId() == R.id.setFreeTime) {
            Intent intent = new Intent(MainActivity.this, SetFreeTimeActivity.class);
            startActivity(intent);
        }
        return true;
    }
}

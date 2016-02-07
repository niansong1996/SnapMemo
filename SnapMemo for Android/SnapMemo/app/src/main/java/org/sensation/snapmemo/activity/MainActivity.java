package org.sensation.snapmemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.Resource_stub;
import org.sensation.snapmemo.widget.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "SnapMemo";

    /**
     * 工具栏
     */
    Toolbar toolbar;

    /**
     * 列表
     */
    ListView listView;

    /**
     * 列表适配器
     */
    ListViewAdapter listViewAdapter;

    /**
     * 列表内容
     */
    List<MemoVO> memoVOList = new ArrayList<MemoVO>();

    /**
     * 左滑菜单
     */
    DrawerLayout drawer;

    /**
     * 用户本地数据
     */
    ClientData clientData = ClientData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //在此获得截得的Action，如果不为空就启动StartActivity并将图片Uri传入
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = null;
                if (Intent.ACTION_VIEW.equals(action)) {
                    imageUri = intent.getData();
                } else if (Intent.ACTION_SEND.equals(action)) {
                    imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                }
                if (imageUri != null) {
                    StartActivity.actionStart(this, imageUri);
                }
            }
        }
    }

    /**
     * 初始化部件和数据加载
     */
    private void init() {

        initToolBar();

        initDrawer();

        initListView();

    }

    /**
     * 初始化列表显示，在网络连通后才进行更新
     */
    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);

        //stub
        memoVOList = new Resource_stub().getMemoVOs();

        listViewAdapter = new ListViewAdapter(this, R.layout.listview_item, memoVOList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoVO clickedMemoVO = (MemoVO) parent.getItemAtPosition(position);
                clientData.setPosition(position);
                //设置非添加模式
                clientData.setAdd(false);

                ContentActivity.actionStart(MainActivity.this, clickedMemoVO);
            }
        });
        clientData.setPosition(memoVOList.size());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化ToolBar，设置监听
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化左滑菜单，在ToolBar上建立与toggle图标连接
     */
    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        MemoVO newMemoVO = clientData.getNewMemoVO();

        if (newMemoVO != null && clientData.isAdd()) {//新增状态，添加listView内容
            memoVOList.add(newMemoVO);
        } else if (newMemoVO != null && !clientData.isAdd()) {//修改状态，修改listView内容
            memoVOList.set(clientData.getPosition(), newMemoVO);
        }
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

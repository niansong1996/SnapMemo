package org.sensation.snapmemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.Resource_stub;
import org.sensation.snapmemo.widget.ListViewAdapter;
import org.sensation.snapmemo.widget.RoundImageView;
import org.sensation.snapmemo.widget.SwipeDismissListView;

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
    SwipeDismissListView listView;

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
    ClientData clientData;

    /**
     * 本地用户信息
     */
    UserVO userVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientData = ClientData.getInstance();
        userVO = clientData.getUserVO();

        init();

        interceptIntent(getIntent());
    }

    /**
     * 初始化部件和数据加载
     */
    private void init() {
        initToolBar();
        initDrawer();
        initNavigation();
        initListView();
    }

    /**
     * 在此获得截得的Action，如果不为空就启动StartActivity并将图片Uri传入
     *
     * @param intent
     */
    private void interceptIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        String type = intent.getType();
        //将传入的intent设为null以免onRestart时错误检测
        setIntent(null);

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
     * 初始化列表显示，在网络连通后才进行更新
     */
    private void initListView() {
        listView = (SwipeDismissListView) findViewById(R.id.listView);
        listView.setOnDismissCallback(new SwipeDismissListView.OnDismissCallback() {
            @Override
            public void onDismiss(int dismissPosition) {
                listViewAdapter.remove(listViewAdapter.getItem(dismissPosition));
            }
        });

        //TODO 从服务器获得MemoList
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

    /**
     * 初始化ToolBar，设置监听
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
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

    /**
     * 初始化左滑菜单内容
     */
    private void initNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        RoundImageView userLogo = (RoundImageView) headerView.findViewById(R.id.userLogo);
        userLogo.setImageBitmap(userVO.getUserLogo());
        userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        TextView userName = (TextView) headerView.findViewById(R.id.userNameText);
        userName.setText(userVO.getUserName());

        TextView condition = (TextView) headerView.findViewById(R.id.condition);
        condition.setText(userVO.getCondition());

        //Drawer头部背景
        ImageView userLogoBackground = (ImageView) headerView.findViewById(R.id.userLogoBackground);
        Bitmap originalUserLogo = userVO.getUserLogo(), newUserLogo;
        Point p = new Point();
        double scaleOfContainer = 0.6,
                scaleOfImage = (originalUserLogo.getHeight() + 0.0) / originalUserLogo.getWidth();

        if (scaleOfContainer < scaleOfImage) {
            newUserLogo = Bitmap.createBitmap(originalUserLogo, 0, 0,
                    originalUserLogo.getWidth(),
                    (int) (originalUserLogo.getWidth() * scaleOfContainer));
        } else {
            newUserLogo = Bitmap.createBitmap(originalUserLogo, 0, 0,
                    (int) (originalUserLogo.getHeight() / scaleOfContainer),
                    originalUserLogo.getHeight());
        }

        userLogoBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        userLogoBackground.setImageBitmap(newUserLogo);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        MemoVO newMemoVO = clientData.getNewMemoVO();

        //有新添加的Memo
        if (newMemoVO != null) {
            if (clientData.isAdd()) {//新增状态，添加listView内容
                memoVOList.add(newMemoVO);
            } else if (!clientData.isAdd()) {//修改状态，修改listView内容
                memoVOList.set(clientData.getPosition(), newMemoVO);
            }
            listViewAdapter.notifyDataSetChanged();
        }
        //修改过用户信息
        if (clientData.isUserInfoChanged()) {
            //刷新左滑菜单头部显示
            clientData.setUserInfoChanged(false);
            userVO = clientData.getUserVO();
            initNavigation();
        }

        //后台情况下再次启动，需要再截取一次intent
        interceptIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //重新设置新的intent
        setIntent(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //stub
            Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

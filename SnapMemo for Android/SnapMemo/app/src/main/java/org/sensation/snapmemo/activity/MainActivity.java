package org.sensation.snapmemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;
import org.sensation.snapmemo.VO.UserVOLite;
import org.sensation.snapmemo.dao.MemoListDao;
import org.sensation.snapmemo.dao.UserInfoDao;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.service.SnapListenerService;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.IOTool;
import org.sensation.snapmemo.widget.ListViewAdapter;
import org.sensation.snapmemo.widget.RoundImageView;
import org.sensation.snapmemo.widget.SwipeDismissListView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends RxAppCompatActivity
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
    ArrayList<MemoVO> memoVOList = new ArrayList<MemoVO>();

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

    /**
     * 自动登录进度条
     */
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获得用户本地信息
        clientData = ClientData.getInstance();
        userVO = clientData.getUserVO();

        //启动后台服务对截屏时间进行监听
        Intent snapIntent = new Intent(this, SnapListenerService.class);
        startService(snapIntent);

//        AlarmService.actionStart(this, 301123456, "3月1日的测试", 10 * 1000);
//        AlarmService.actionStart(this, 302123456, "3月2日的测试", 20 * 1000);

        handleServiceResult();

        init();
    }

    /**
     * 处理后台服务获取的事件
     */
    private void handleServiceResult() {
        //从后台服务中监听到的截屏图片地址
        if (getIntent() != null) {
            String imagePath = getIntent().getStringExtra("imagePath");
            Uri mImageUri = null;
            Uri mUri = Uri.parse("content://media/external/images/media");

            if (imagePath != null) {
                CursorLoader c = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);
                Cursor cursor = c.loadInBackground();
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor
                            .getColumnIndex(MediaStore.MediaColumns.DATA));
                    if (imagePath.equals(data)) {
                        int ringtoneID = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.MediaColumns._ID));
                        mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);

                        break;
                    }
                    cursor.moveToNext();
                }
                //启动开始活动
                StartActivity.actionStart(this, mImageUri);
            }
        }

    }

    /**
     * 初始化部件和数据加载
     */
    private void init() {

        preLogin();
        initToolBar();
        initDrawer();
    }

    /**
     * <strong>前置登录状态检查</strong><br>
     * 1. 如果已经登录过，就从文件读取用户名密码进行登录<br>
     * 1.1 如果网络连接成功就从服务器加载资源，并同步本地数据<br>
     * 1.2 如果网络连接不成功或登录失败，就从本地加载资源，但不可进行添加修改<br><br>
     * 2. 如果没有登录过，设置关键位，从使用默认文件<br>
     * 2.1 加载保存本地文件，数据不上传<br>
     * 2.2 如果以后注册新用户，就将本地文件同步至服务器<br>
     */
    private void preLogin() {
        if (clientData.isSigned()) {
            //利用已有的帐号密码进行登录
            String[] signInfo = clientData.getUserSignInfo();
            new UserLoginTask(signInfo[0], signInfo[1]).execute();
        } else {
            //设置关键位，加载本地文件
            clientData.setOnline(false);
            ArrayList<MemoVO> tempList = MemoListDao.getLocalMemoList(IOTool.DEFAULT_USER_NAME);
            if (tempList != null) {
                memoVOList = tempList;
            }
            initListView();
            initNavigation();
            interceptIntent(getIntent());
        }
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
                if (clientData.isOnline()) {
                    new DeleteTask(dismissPosition).execute(listViewAdapter.getItem(dismissPosition).getMemoID());
                } else {//用户没有登录
                    if (clientData.isSigned()) {
                        //用户曾经登录过，由于本地加载的是网络端同步过的内容，所以不允许进行添加删除修改，除非注销
                        Toast.makeText(MainActivity.this, getString(R.string.internet_failure),
                                Toast.LENGTH_SHORT).show();
                        listViewAdapter.notifyDataSetChanged();
                    } else {//用户没有登录过，也就是本地用户，只要保存数据至本地，不需要进行网络删除操作
                        listViewAdapter.remove(listViewAdapter.getItem(dismissPosition));
                        MemoListDao.saveLocalMemoList(memoVOList, userVO.getUserName());
                    }
                }
            }
        });

        listViewAdapter = new ListViewAdapter(this, R.layout.listview_item, memoVOList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoVO clickedMemoVO = (MemoVO) parent.getItemAtPosition(position);
                //设置改变的memo的位置，从content活动返回后通过onRestart对列表进行更新
                clientData.setPosition(position);
                //设置非添加模式
                clientData.setAdd(false);

                ContentActivity.actionStart(MainActivity.this, clickedMemoVO);
            }
        });
        listViewAdapter.notifyDataSetChanged();
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
        //已经登录了就跳转至用户设置界面，没有登录就跳转至登录界面
        userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clientData.isOnline()) {
                    Intent intent = new Intent(MainActivity.this, UserSettingsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                } else {
                    Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            }
        });

        TextView userName = (TextView) headerView.findViewById(R.id.userNameText);
        userName.setText(userVO.getUserName());

        TextView condition = (TextView) headerView.findViewById(R.id.signiture);
        condition.setText(userVO.getSignature());

        //Drawer头部背景
        ImageView userLogoBackground = (ImageView) headerView.findViewById(R.id.userLogoBackground);
        Bitmap originalUserLogo = userVO.getUserLogo(), newUserLogo = null;
        if (originalUserLogo != null) {

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

        //有新添加的Memo
        MemoVO newMemoVO = clientData.getNewMemoVO();
        if (newMemoVO != null) {
            if (clientData.isAdd()) {//新增状态，添加listView内容
                memoVOList.add(newMemoVO);
                //设置添加状态为false，下次restart不会重复加载
                clientData.setAdd(false);
            } else if (!clientData.isAdd()) {//修改状态，修改listView内容
                memoVOList.set(clientData.getPosition(), newMemoVO);
            }
            listViewAdapter.notifyDataSetChanged();
            //更新本地数据
            MemoListDao.saveLocalMemoList(memoVOList, userVO.getUserName());
            //删除缓存的newMemoVO
            clientData.setNewMemoVO(null);
        }

        //修改过用户信息
        if (clientData.isUserInfoChanged()) {
            //刷新左滑菜单头部显示
            userVO = clientData.getUserVO();
            initNavigation();
            //设置用户信息变化为false，下次restart不会重复加载
            clientData.setUserInfoChanged(false);
            //保存本地数据
            UserInfoDao.saveUserInfo(userVO);
        }

        //通过注册第一次登录
        if (clientData.isFirstSigned()) {
            new UserLoginTask(clientData.getFirstSignedUserID(), clientData.getFirstSignedPassword()).execute();
            clientData.setFirstSignedPassword(null);
            clientData.setFirstSignedUserID(null);
            clientData.setFirstSigned(false);
        }

        //利用clientData中newMemoList判断是否加载了memoList，如果有就加载新列表（清空缓存的newMemoVOList）
        ArrayList<MemoVO> newMemoList = clientData.getNewMemoVOList();
        if (newMemoList != null) {
            memoVOList = newMemoList;
            listViewAdapter.notifyDataSetChanged();
            clientData.setNewMemoVOList(null);
        }

        //后台情况下服务检测到截屏
        handleServiceResult();

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
            //TODO 主界面设置Stub
            Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Toast.makeText(MainActivity.this, "You're clicking on " + id + ", it's a useless item.", Toast.LENGTH_SHORT).show();
        return true;
    }

    class UserLoginTask extends AsyncTask<Void, Void, UserVO> {
        private final String mUserName;
        private final String mPassword;
        private ArrayList<MemoVO> memoList;

        UserLoginTask(String userName, String password) {
            mUserName = userName;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("SnapMemo正在登录...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected UserVO doInBackground(Void... params) {
            String userID = new HttpService().signIn(mUserName, mPassword);
            UserVO oldUserVO = clientData.getUserVO();
            if (userID != null) {
                UserVOLite userVOLite = new HttpService().getUserInfo(userID);
                Bitmap userLogo = new HttpService().getUserLogo(userID);
                memoList = new HttpService().getMemoList(userID);
                return new UserVO(userID, mUserName, userVOLite.getSignature(), userLogo);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserVO userVO) {
            progressDialog.dismiss();
            if (userVO != null) {
                //登录成功，ClientData加载用户信息，同步本地数据

                //设置显示列表数据
                memoVOList = memoList;

                //保存本地列表数据
                MemoListDao.saveLocalMemoList(memoList, userVO.getUserName());

                //设置本地用户数据
                clientData.setUserInfo(userVO);

                //保存本次登录用户数据至本地
                UserInfoDao.saveUserInfo(userVO);

                //保存本次登录用户头像至本地
                UserInfoDao.saveUserLogo(userVO.getUserLogo());

                //设置用户登录状态为已登录
                clientData.setOnline(true);

                //更新本界面中的userVO
                MainActivity.this.userVO = userVO;

                //截取Intent
                interceptIntent(getIntent());

            } else {
                //登录失效，跳转至登录界面
                clientData.setOnline(false);
                MainActivity.this.userVO = clientData.getUserVO();
                Toast.makeText(MainActivity.this, getString(R.string.signin_overtime), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }

            //初始化左滑面板，显示从网络端下载的用户信息和头像
            initNavigation();

            //初始化列表，显示从网络端下载的数据
            initListView();
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }

    class DeleteTask extends AsyncTask<String, Void, Boolean> {
        final private int dismissPosition;

        public DeleteTask(int dismissPosition) {
            this.dismissPosition = dismissPosition;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("SnapMemo正在删除...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //TODO 删除stub
            return new HttpService().deleteMemo(params[0]);
//            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {//成功删除后，更新界面显示
                listViewAdapter.remove(listViewAdapter.getItem(dismissPosition));
                MemoListDao.saveLocalMemoList(memoVOList, userVO.getUserName());
                Toast.makeText(MainActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            } else {//由于网络原因造成的删除失败，更新界面显示
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, getString(R.string.internet_failure), Toast.LENGTH_SHORT).show();
            }
        }
    }

}

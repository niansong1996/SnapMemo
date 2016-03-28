package org.sensation.snapmemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;
import org.sensation.snapmemo.VO.UserVOLite;
import org.sensation.snapmemo.dao.MemoListDao;
import org.sensation.snapmemo.dao.UserInfoDao;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.tool.ClientData;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SnapMemo";

    //UI references
    EditText mPasswordView;
    AutoCompleteTextView mUserNameView;
    ProgressDialog progressDialog;

    /**
     * 用户输入过的数据
     */
    List<String> userPreference = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        init();
    }

    private void init() {

        initClientPreference();

        initToolBar();

        initButton();

        initEditText();
    }

    private void initClientPreference() {
        userPreference = UserInfoDao.getUserPreference();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.userName);
        ArrayAdapter<String> autoTextAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userPreference);
        mUserNameView.setAdapter(autoTextAdapter);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.signIn || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }


    private void initButton() {
        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录
                new UserLoginTask(mUserNameView.getText().toString(),
                        mPasswordView.getText().toString()).execute();
            }
        });
    }

    private void attemptLogin() {
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mPasswordView, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
        if (id == R.id.action_sign_up) {
            //跳转 注册界面
            Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //注册完成后直接跳回主界面加载信息
        if (ClientData.getInstance().isUserInfoChanged()) {
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
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
            progressDialog = new ProgressDialog(SigninActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("SnapMemo正在登录...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected UserVO doInBackground(Void... params) {
            //登录
            String userID = new HttpService().signIn(mUserName, mPassword);
            ClientData.getInstance().setOnline(true);
            UserVO oldUserVO = ClientData.getInstance().getUserVO();
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
                //TODO 登录成功，ClientData加载用户信息，记录preference，记录已登录帐号密码信息，跳转至MainActivity
                //设置显示列表数据，在主界面restart时更新
                ClientData.getInstance().setNewMemoVOList(memoList);

                //保存本地列表数据
                MemoListDao.saveLocalMemoList(memoList, userVO.getUserName());

                //设置本地用户数据
                ClientData.getInstance().setUserInfo(userVO);

                //保存本次登录用户数据至本地
                UserInfoDao.saveUserInfo(userVO);

                //保存本次登录用户头像至本地
                UserInfoDao.saveUserLogo(userVO.getUserLogo());

                //设置用户信息改变状态为已改变
                ClientData.getInstance().setUserInfoChanged(true);

                //设置用户登录状态为已登录
                ClientData.getInstance().setOnline(true);

                //记录preference
                UserInfoDao.saveUserPreference(mUserName);

                //记录已登录帐号密码信息
                ClientData.getInstance().saveUserSignedInfo(new String[]{mUserName, mPassword});

                //跳转至MainActivity
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }
}

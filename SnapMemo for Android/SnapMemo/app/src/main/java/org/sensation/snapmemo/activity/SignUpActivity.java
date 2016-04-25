package org.sensation.snapmemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.tool.ClientData;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    EditText mUserNameView, mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    private void init() {
        initButton();
        initToolBar();
        initEditText();
    }

    private void initEditText() {
        mUserNameView = (EditText) findViewById(R.id.inputUserName);
        mPasswordView = (EditText) findViewById(R.id.inputPassword);
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

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initButton() {
        Button signUp = (Button) findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpTask(mUserNameView.getText().toString(), mPasswordView.getText().toString()).execute();
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

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    class SignUpTask extends AsyncTask<Void, Void, String> {

        private String userName, password;

        public SignUpTask(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("SnapMemo正在注册...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void... params) {

            return new HttpService().signUp(userName, password);
        }

        @Override
        protected void onPostExecute(String userID) {
            if (userID != null && userID.length() != 0) {
                //在原有userVO上添加userID
                ClientData.getInstance().setUserID(userID);
                //用户信息改变标识位
                ClientData.getInstance().setUserInfoChanged(true);
                //标识已登录
                ClientData.getInstance().setOnline(true);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                finish();

                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.internet_failure), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

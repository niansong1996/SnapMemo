package org.sensation.snapmemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.tool.ClientData;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContentActivity extends AppCompatActivity {
    ClientData clientData = ClientData.getInstance();
    EditText topicContent, timeContent, contentContent;
    ProgressDialog progressDialog;
    private String id, topic, date, day, content, originID, originTopic, originDate, originDay, originContent;

    /**
     * 自定义载入方式
     *
     * @param context 上下文内容
     * @param memoVO  以String形式传到第二个活动中去的MemoVO
     */
    public static void actionStart(Context context, MemoVO memoVO) {
        String id = memoVO.getMemoID(),
                topic = memoVO.getTopic(),
                date = memoVO.getDate(),
                day = memoVO.getDay(),
                content = memoVO.getContent();
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("topic", topic);
        intent.putExtra("date", date);
        intent.putExtra("day", day);
        intent.putExtra("content", content);

        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        topic = intent.getStringExtra("topic");
        date = intent.getStringExtra("date");
        day = intent.getStringExtra("day");
        content = intent.getStringExtra("content");

        //复制原始值检测是否修改
        originID = id;
        originTopic = topic;
        originDate = date;
        originDay = day;
        originContent = content;

        init();
    }

    private void init() {

        initToolBar();

        initButton();

        initEditView();
    }

    private void initEditView() {
        topicContent = (EditText) findViewById(R.id.topic_content);
        topicContent.setText(topic);

        timeContent = (EditText) findViewById(R.id.time_content);
        timeContent.setText(date + " " + day);

        contentContent = (EditText) findViewById(R.id.content_content);
        contentContent.setText(content);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initButton() {
        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                topic = topicContent.getText().toString();
                date = timeContent.getText().toString().split(" ")[0];
                day = timeContent.getText().toString().split(" ")[1];
                content = contentContent.getText().toString();
                MemoVO newMemoVO = new MemoVO(id, topic, date, day, content);
                clientData.setUpdateCondition(true);
                clientData.setNewMemoVO(newMemoVO);

                //如果用户修改了内容就发送修改请求
                if (isModified()) {
                    new ModifyTask().execute(newMemoVO.toMemoVOLite());
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clientData.isConnected()) {
                    new DeleteTask().execute(id);
                } else {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
            }
        });
    }

    /**
     * @return 是否修改过memo内容
     */
    private boolean isModified() {
        return !(id.equals(originID) && topic.equals(originTopic) && date.equals(originDate)
                && day.equals(originDay) && content.equals(originContent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (clientData.isConnected()) {
                new DeleteTask().execute(this.id);
            } else {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (clientData.isConnected()) {
            new DeleteTask().execute(id);
        } else {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    /**
     * 网络线程———修改任务
     */
    class ModifyTask extends AsyncTask<MemoVOLite, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ContentActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("正在添加...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(MemoVOLite... params) {
            return new HttpService().modifyMemo(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            } else {
                Toast.makeText(ContentActivity.this, getString(R.string.internet_failure), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 网络线程———删除任务
     */
    class DeleteTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ContentActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("正在取消..");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return new HttpService().deleteMemo(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                //删除成功，关闭网络需求标识
                clientData.setConnected(false);
            } else {
                Toast.makeText(ContentActivity.this, getString(R.string.internet_failure), Toast.LENGTH_SHORT).show();
            }
        }
    }

}

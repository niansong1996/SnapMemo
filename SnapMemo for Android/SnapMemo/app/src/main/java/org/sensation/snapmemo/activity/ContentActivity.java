package org.sensation.snapmemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.tool.ClientData;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContentActivity extends AppCompatActivity {
    ClientData clientData = ClientData.getInstance();
    EditText topicContent, timeContent, contentContent;
    private String topic, date, day, content;

    /**
     * 自定义载入方式
     *
     * @param context 上下文内容
     * @param memoVO  以String形式传到第二个活动中去的MemoVO
     */
    public static void actionStart(Context context, MemoVO memoVO) {
        String topic = memoVO.getTopic(),
                date = memoVO.getDate(),
                day = memoVO.getDay(),
                content = memoVO.getContent();
        Intent intent = new Intent(context, ContentActivity.class);
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
        topic = intent.getStringExtra("topic");
        date = intent.getStringExtra("date");
        day = intent.getStringExtra("day");
        content = intent.getStringExtra("content");

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
        toolbar.setTitle(getString(R.string.content_title));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initButton() {
        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                topic = topicContent.getText().toString();
                date = timeContent.getText().toString().split(" ")[0];
                day = timeContent.getText().toString().split(" ")[1];
                content = contentContent.getText().toString();
                MemoVO newMemoVO = new MemoVO(topic, date, day, content);
                clientData.setUpdateCondition(true);
                clientData.setNewMemoVO(newMemoVO);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
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

}

package org.sensation.snapmemo.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoDisplayVO;
import org.sensation.snapmemo.VO.MemoTransVO;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.service.AlarmService;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.TimeTool;

import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContentActivity extends AppCompatActivity {
    final String TAG = "SnapMemo";
    ClientData clientData = ClientData.getInstance();
    TextView dateContent, timeContent;
    EditText topicContent, contentContent;
    ProgressDialog progressDialog;
    MemoVO newMemoVO;
    RadioButton reminder;
    boolean reminderChecked = false;
    private String id, topic, date, time, day, content, originTopic, originDate, originTime, originDay, originContent;

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

    /**
     * 自定义载入方式
     *
     * @param context       上下文内容
     * @param memoDisplayVO 以String形式传到第二个活动中去的MemoDisplayVO
     */
    public static void actionStart(Context context, MemoDisplayVO memoDisplayVO) {
        String topic = memoDisplayVO.getTopic(),
                date = memoDisplayVO.getDate(),
                day = memoDisplayVO.getDay(),
                content = memoDisplayVO.getContent();
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("date", date);
        intent.putExtra("day", day);
        intent.putExtra("content", content);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        topic = intent.getStringExtra("topic");
        date = intent.getStringExtra("date").split(" ")[0];
        time = intent.getStringExtra("date").split(" ")[1];
        day = intent.getStringExtra("day");
        content = intent.getStringExtra("content");

        //复制原始值检测是否修改
        originTopic = topic;
        originDate = date;
        originTime = time;
        originDay = day;
        originContent = content;

        init();
    }

    private void init() {

        initToolBar();

        initEditView();

        initButton();

        initReminder();
    }

    private void initEditView() {
        topicContent = (EditText) findViewById(R.id.topic_content);
        topicContent.setText(topic);

        dateContent = (TextView) findViewById(R.id.date_content);
        dateContent.setText(date);
        dateContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePick();
            }
        });

        timeContent = (TextView) findViewById(R.id.time_content);
        timeContent.setText(time + " " + day);
        timeContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePick();
            }
        });

        contentContent = (EditText) findViewById(R.id.content_content);
        contentContent.setText(content);
    }

    private void datePick() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(ContentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                day = TimeTool.getDay(date);
                dateContent.setText(date);
                timeContent.setText(time + " " + day);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void timePick() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(ContentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = hourOfDay + ":" + TimeTool.amplify(minute);
                timeContent.setText(time + " " + day);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initButton() {
        Button confirm = (Button) findViewById(R.id.modify);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                topic = topicContent.getText().toString();
                date = dateContent.getText().toString();
                day = timeContent.getText().toString().split(" ")[1];
                time = timeContent.getText().toString().split(" ")[0];
                content = contentContent.getText().toString();

                //如果是添加行为就进行保存，否则为可能的修改行为
                if (clientData.isAdd()) {
                    MemoTransVO memoTransVO = new MemoTransVO(clientData.getUserVO().getUserID(), topic, date + " " + time, content);
                    new SaveTask(memoTransVO).execute();
                } else {
                    if (isModified()) {//如果用户修改了内容就发送修改请求
                        //对本地的memo进行修改
                        newMemoVO = new MemoVO(id, topic, date + " " + time, day, content);
                        new ModifyTask().execute(newMemoVO.toMemoVOLite());
                    } else {
                        Intent intent = new Intent(ContentActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    }
                }
                if (reminder.isChecked()) {
                    Calendar presentCalendar = Calendar.getInstance();
                    presentCalendar.setTimeInMillis(System.currentTimeMillis());
                    presentCalendar.set(Calendar.MONTH, presentCalendar.get(Calendar.MONTH) + 1);

                    int year = Integer.parseInt(date.split("-")[0]),
                            month = Integer.parseInt(date.split("-")[1]),
                            dayOfMonth = Integer.parseInt(date.split("-")[2]);
                    Calendar remindingCalendar = Calendar.getInstance();
                    remindingCalendar.set(year, month, dayOfMonth);

                    int dayBetween = TimeTool.getDaysBetween(presentCalendar, remindingCalendar);

                    String startTime = presentCalendar.get(Calendar.HOUR_OF_DAY) + ":" + presentCalendar.get(Calendar.MINUTE);
                    int timeBetween = TimeTool.getSecondsBetween(startTime, time);

                    AlarmService.actionStart(ContentActivity.this, (int) System.currentTimeMillis(), topic, (dayBetween * TimeTool.DAY_SECONDS + timeBetween) * 1000);
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
     * 初始化提醒
     */
    private void initReminder() {
        reminder = (RadioButton) findViewById(R.id.reminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reminderChecked) {
                    reminder.setChecked(true);
                    reminderChecked = true;
                } else {
                    reminder.setChecked(false);
                    reminderChecked = false;
                }
            }
        });

    }

    /**
     * @return 是否修改过memo内容
     */
    private boolean isModified() {
        return !(topic.equals(originTopic) && date.equals(originDate)
                && day.equals(originDay) && time.equals(originTime) && content.equals(originContent));
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

    class SaveTask extends AsyncTask<Void, Void, String> {
        MemoTransVO memoTransVO;

        public SaveTask(MemoTransVO memoTransVO) {
            this.memoTransVO = memoTransVO;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ContentActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("正在保存...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            return new HttpService().saveMemo(memoTransVO);
        }

        @Override
        protected void onPostExecute(String memoID) {
            progressDialog.dismiss();
            if (memoID != null) {
                //设置界面更新
                clientData.setNewMemoVO(new MemoVO(memoID, memoTransVO.getTopic(), memoTransVO.getTime() + " " + time,
                        TimeTool.getDay(memoTransVO.getTime()), memoTransVO.getContent()));

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
     * 网络线程———修改任务
     */
    class ModifyTask extends AsyncTask<MemoVOLite, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ContentActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("正在保存...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(MemoVOLite... params) {
            //修改memo
            return new HttpService().modifyMemo(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                //用于界面更新
                clientData.setNewMemoVO(newMemoVO);
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
            //TODO 删除的网络请求stub
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

package com.alan.sphare.presentation.activityUI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.alan.sphare.R;
import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.logicservice.RemoveListener;
import com.alan.sphare.presentation.tool.ArrayGenerator;
import com.alan.sphare.presentation.widget.SlideCutListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetFreeTimeActivity extends AppCompatActivity implements RemoveListener, Toolbar.OnMenuItemClickListener {

    Toolbar toolbar;
    TextView headDate;
    SlideCutListView slideCutListView;
    ArrayAdapter<String> listViewAdapter;
    ArrayAdapter<Integer> spinnerAdapter;
    Spinner startHour, startMin, endHour, endMin;
    int starthour, startmin, endhour, endmin;
    Calendar calendar = Calendar.getInstance();
    Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    List<String> freeTimeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_free_time);
        init();


    }

    private void init() {
        headDate = (TextView) findViewById(R.id.freeTimeDate);
        headDate.setText("目前选中的日期： " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());

        initToolBar();

        initSpinner();

        initListView();

        initFloatButton();

    }

    /**
     * 初始化Spinner
     */
    private void initSpinner() {
        startHour = (Spinner) findViewById(R.id.startHour);
        startMin = (Spinner) findViewById(R.id.startMin);
        endHour = (Spinner) findViewById(R.id.endHour);
        endMin = (Spinner) findViewById(R.id.endMin);
        spinnerAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ArrayGenerator.getGeneratedArray(8, 24));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startHour.setAdapter(spinnerAdapter);
        endHour.setAdapter(spinnerAdapter);
        spinnerAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, ArrayGenerator.getGeneratedArray(0, 59));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startMin.setAdapter(spinnerAdapter);
        endMin.setAdapter(spinnerAdapter);
        Log.d("SPhare", "initSpinner: adapter");

        startHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                starthour = position + 8;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        startMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startmin = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        endHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endhour = position + 8;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        endMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endmin = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        listViewAdapter = new ArrayAdapter<String>(this, R.layout.setfreetime_item, R.id.listitem, freeTimeList);
        slideCutListView = (SlideCutListView) findViewById(R.id.slidecutListview);
        slideCutListView.setRemoveListener(this);
        slideCutListView.setAdapter(listViewAdapter);
    }

    private void initFloatButton() {
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeTimeList.add(starthour + ":" + startmin + " ~ " + endhour + ":" + endmin);
                listViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.subtoolbar);
        toolbar.setTitle("设置空余时间");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.freetime_menu, menu);
        return true;
    }

    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        listViewAdapter.remove(listViewAdapter.getItem(position));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.subDatePicker) {
            Log.d("SPhare", "onMenuItemClick: click gallary");
            new DatePickerDialog(SetFreeTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker dp, int year, int month, int day) {
                    date = new Date(year, month + 1, day);
                    //选择完日期后更新的显示
                    headDate.setText("目前选中的日期： " + date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        return true;
    }
}

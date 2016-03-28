package org.sensation.snapmemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.UserVO;
import org.sensation.snapmemo.dao.UserInfoDao;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.IOTool;

import java.io.File;
import java.io.FileNotFoundException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    final int SELECT_PHOTO = 2;
    Uri imageUri;
    ClientData clientData;
    UserVO userVO;
    EditText educationInfo, condition, userName;
    ImageView userLogo;
    TextView userID;
    /**
     * 保存文件的根路径
     */
    String saveDir = IOTool.DEFAULT_SAVING_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        init();
    }

    private void init() {
        clientData = ClientData.getInstance();
        initToolBar();
        initUserInfo();
        initButton();
    }

    private void initButton() {
        //确认按钮
        findViewById(R.id.modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClientData();
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        //注销按钮
        findViewById(R.id.signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientData.getInstance().setOnline(false);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    private void initUserInfo() {
        userVO = clientData.getUserVO();

        userLogo = (ImageView) findViewById(R.id.userLogo);
        userLogo.setImageBitmap(userVO.getUserLogo());
        userLogo.setOnClickListener(this);

        userName = (EditText) findViewById(R.id.userName);
        userName.setText(userVO.getUserName());

        userID = (TextView) findViewById(R.id.userID);
        userID.setText(userVO.getUserID());

        condition = (EditText) findViewById(R.id.signiture);
        condition.setText(userVO.getSignature());
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.user_settings_title));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            saveClientData();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveClientData();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /**
     * 获得界面中修改过的用户数据，生成新的UserVO
     *
     * @return newUserVO
     */
    private UserVO getNewUserVO() {
        UserVO newUserVO = new UserVO(userVO.getUserID(),
                userName.getText().toString(),
                condition.getText().toString(),
                ((BitmapDrawable) userLogo.getDrawable()).getBitmap());
        return newUserVO;
    }

    /**
     * 保存ClientData
     */
    private void saveClientData() {
        clientData.setUserInfo(getNewUserVO());
        clientData.setUserInfoChanged(true);
        UserInfoDao.saveUserInfo(getNewUserVO());
        UserInfoDao.saveUserLogo(((BitmapDrawable) userLogo.getDrawable()).getBitmap());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap newUserLogo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    userLogo.setImageBitmap(newUserLogo);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.userLogo) {
            File outputImage = IOTool.createFile(saveDir, "output_image.jpg");
            imageUri = Uri.fromFile(outputImage);

            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            startActivityForResult(intent, SELECT_PHOTO);
        }
    }
}

package org.sensation.snapmemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.httpservice.HttpService;
import org.sensation.snapmemo.tool.ClientData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StartActivity extends AppCompatActivity {

    /**
     * 裁剪照片的标识
     */
    private final int CROP_PHOTO = 1;
    /**
     * 加载进来的图片
     */
    Bitmap bitmap = null;
    /**
     * 等待时进度框
     */
    ProgressDialog progressDialog;
    /**
     * 从外部传入的图片Uri地址和裁剪后的保存地址
     */
    private Uri imageUri, outputImageUri;

    /**
     * 自定义载入方式
     *
     * @param context 上下文内容
     * @param uri     以Parcelable形式传到第二个活动中去的图片Uri
     */
    public static void actionStart(Context context, Uri uri) {
        Intent intent = new Intent(context, StartActivity.class);
        intent.putExtra("imageUri", uri);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent intent = getIntent();
        imageUri = intent.getParcelableExtra("imageUri");

        init();
    }

    /**
     * 部件、数据初始化
     */
    private void init() {

        initButton();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.start_title));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleImage();
    }


    private void initButton() {
        Button generate = (Button) findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, os);
                new TransTask().execute(os);
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

    /**
     * 处理图片进入后的截取跳转
     */
    private void handleImage() {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputImageUri = Uri.fromFile(outputImage);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CROP_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputImageUri));
                    ImageView pic = (ImageView) findViewById(R.id.crop_image);
                    pic.setImageBitmap(bitmap);
                    pic.setAdjustViewBounds(true);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
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

    class TransTask extends AsyncTask<ByteArrayOutputStream, Void, MemoVO> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StartActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("微软牛津计划,正在识别...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected MemoVO doInBackground(ByteArrayOutputStream... params) {
//            MemoVO memoVO = new HttpService_stub().transPic(params[0]);
            MemoVO memoVO = new HttpService().transPic(params[0]);
            return memoVO;

        }

        @Override
        protected void onPostExecute(MemoVO memo) {
            progressDialog.dismiss();
            //跳转至内容界面
            if (memo != null) {
                ClientData.getInstance().setAdd(true);
                ContentActivity.actionStart(StartActivity.this, memo);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            } else {
                //网络异常处理
                Toast.makeText(StartActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

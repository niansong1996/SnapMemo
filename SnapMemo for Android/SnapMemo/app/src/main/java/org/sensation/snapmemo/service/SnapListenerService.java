package org.sensation.snapmemo.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.github.piasy.rxscreenshotdetector.RxScreenshotDetector;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.activity.MainActivity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 后台监测截屏事件的服务类
 */
public class SnapListenerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxScreenshotDetector.start(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String path) {
                        final String mPath = path;
                        //TODO 尝试用自己的View来显示Dialog
                        AlertDialog alertDialog = new AlertDialog.Builder(SnapListenerService.this)
                                .setMessage(getString(R.string.start_info))
                                .setPositiveButton(getString(R.string.start_generate),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                try {
                                                    //给后台留有保存截屏图片的时间
                                                    Thread.sleep(3000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                Intent intent = new Intent(SnapListenerService.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("imagePath", mPath);
                                                getApplication().startActivity(intent);
                                            }
                                        }).setNegativeButton(getString(R.string.cancel), null).create();
                        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
    }
}

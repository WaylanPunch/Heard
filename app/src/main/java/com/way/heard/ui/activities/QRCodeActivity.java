package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.way.heard.R;
import com.way.heard.utils.LogUtil;

import java.util.Hashtable;


public class QRCodeActivity extends BaseActivity {
    private final static String TAG = QRCodeActivity.class.getName();

    private static final String USER_OBJECT_ID = "UserObjectId";
    private static final String USER_NAME = "Username";
    private static final int QR_WIDTH = 200;
    private static final int QR_HEIGHT = 200;
    private ImageView ivQRCode;
    private String userobjectid;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        getParamData();

        initView();

        createQRImage(userobjectid, username);
    }

    private void getParamData() {
        Bundle bundle = getIntent().getExtras();
        userobjectid = bundle.getString(QRCodeActivity.USER_OBJECT_ID);
        username = bundle.getString(QRCodeActivity.USER_NAME);
        LogUtil.d(TAG, "getParamData debug, UserObjectId = " + userobjectid);
    }

    private void initView() {
        ivQRCode = (ImageView) findViewById(R.id.iv_qrcode_img);
    }

    public void createQRImage(String param1, String param2) {
        try {
            String param;
            //判断URL合法性
            if (TextUtils.isEmpty(param1) || TextUtils.isEmpty(param2)) {
                return;
            } else {
                param = param1 + ":" + param2;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(param, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            LogUtil.e(TAG, "createQRImage error", e);
            e.printStackTrace();
        }
    }

    public static void go(Context context, String userobjectid, String username) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra(QRCodeActivity.USER_OBJECT_ID, userobjectid);
        intent.putExtra(QRCodeActivity.USER_NAME, username);
        context.startActivity(intent);
    }
}

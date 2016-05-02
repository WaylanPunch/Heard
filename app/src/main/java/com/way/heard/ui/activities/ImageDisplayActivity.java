package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.way.heard.R;

public class ImageDisplayActivity extends AppCompatActivity {

    public final static String IMAGE_DETAIL = "ImageDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
    }

    public static void go(Context context, String imageUrl) {
        Intent intent = new Intent(context, ImageDisplayActivity.class);
        intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, imageUrl);
        context.startActivity(intent);
    }
}

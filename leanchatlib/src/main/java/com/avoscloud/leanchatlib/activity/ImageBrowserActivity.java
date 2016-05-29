package com.avoscloud.leanchatlib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PhotoUtils;

/**
 * Created by lzw on 14-9-21.
 */
public class ImageBrowserActivity extends Activity {

  private ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_image_brower_layout);
    imageView = (ImageView) findViewById(R.id.imageView);
    Intent intent = getIntent();
    String path = intent.getStringExtra(Constants.IMAGE_LOCAL_PATH);
    String url = intent.getStringExtra(Constants.IMAGE_URL);
    PhotoUtils.displayImageCacheElseNetwork(imageView, path, url);
  }
}

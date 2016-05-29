package com.avoscloud.leanchatlib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.event.EmptyEvent;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/7/24.
 */
public class AVBaseActivity extends AppCompatActivity {

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(this);
    onViewCreated();
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    ButterKnife.bind(this);
    onViewCreated();
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    ButterKnife.bind(this);
    onViewCreated();
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  protected void onViewCreated() {}

  protected void alwaysShowMenuItem(Menu menu) {
    if (null != menu && menu.size() > 0) {
      MenuItem item = menu.getItem(0);
      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
  }

  protected boolean filterException(Exception e) {
    if (e != null) {
      e.printStackTrace();
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  protected void toast(String str) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
  }

  protected void showToast(String content) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
  }

  protected void showToast(int resId) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
  }


  protected void startActivity(Class<?> cls) {
    Intent intent = new Intent(this, cls);
    startActivity(intent);
  }

  protected void startActivity(Class<?> cls, String... objs) {
    Intent intent = new Intent(this, cls);
    for (int i = 0; i < objs.length; i++) {
      intent.putExtra(objs[i], objs[++i]);
    }
    startActivity(intent);
  }

  public void startActivity(Class<?> cls, int requestCode) {
    startActivityForResult(new Intent(this, cls), requestCode);
  }

  public void onEvent(EmptyEvent event) {}

  protected ProgressDialog showSpinnerDialog() {
    //activity = modifyDialogContext(activity);
    ProgressDialog dialog = new ProgressDialog(this);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(true);
    dialog.setMessage(getString(R.string.chat_utils_hardLoading));
    if (!isFinishing()) {
      dialog.show();
    }
    return dialog;
  }
}

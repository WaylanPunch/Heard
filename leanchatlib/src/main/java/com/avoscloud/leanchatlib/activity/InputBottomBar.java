package com.avoscloud.leanchatlib.activity;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.adapter.ChatEmotionGridAdapter;
import com.avoscloud.leanchatlib.adapter.ChatEmotionPagerAdapter;
import com.avoscloud.leanchatlib.controller.EmotionHelper;
import com.avoscloud.leanchatlib.event.InputBottomBarEvent;
import com.avoscloud.leanchatlib.event.InputBottomBarLocationClickEvent;
import com.avoscloud.leanchatlib.event.InputBottomBarRecordEvent;
import com.avoscloud.leanchatlib.event.InputBottomBarTextEvent;
import com.avoscloud.leanchatlib.utils.SoftInputUtils;
import com.avoscloud.leanchatlib.view.EmotionEditText;
import com.avoscloud.leanchatlib.view.RecordButton;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/7/24.
 * 专门负责输入的底部操作栏，与 activity 解耦
 * 当点击相关按钮时发送 InputBottomBarEvent，需要的 View 可以自己去订阅相关消息
 */
public class InputBottomBar extends LinearLayout {

  /**
   * 加号 Button
   */
  private View actionBtn;

  /**
   * 表情 Button
   */
  private View emotionBtn;

  /**
   * 文本输入框
   */
  private EmotionEditText contentEditText;

  /**
   * 发送文本的Button
   */
  private View sendTextBtn;

  /**
   * 切换到语音输入的 Button
   */
  private View voiceBtn;

  /**
   * 切换到文本输入的 Button
   */
  private View keyboardBtn;

  /**
   * 底部的layout，包含 emotionLayout 与 actionLayout
   */
  private View moreLayout;

  /**
   * 表情 layout
   */
  private View emotionLayout;

  private ViewPager emotionPager;

  /**
   * 录音按钮
   */
  private RecordButton recordBtn;

  /**
   * action layout
   */
  private View actionLayout;
  private View cameraBtn;
  private View locationBtn;
  private View pictureBtn;

  /**
   * 最小间隔时间为 1 秒，避免多次点击
   */
  private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

  public InputBottomBar(Context context) {
    super(context);
    initView(context);
  }

  public InputBottomBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }


  /**
   * 隐藏底部的图片、emtion 等 layout
   */
  public void hideMoreLayout() {
    moreLayout.setVisibility(View.GONE);
  }


  //TODO 这一坨代码还是太丑了，因为 lib 里不能使用 butterknife 暂时如此
  private void initView(Context context) {
    View.inflate(context, R.layout.chat_input_bottom_bar_layout, this);
    actionBtn = findViewById(R.id.input_bar_btn_action);
    emotionBtn = findViewById(R.id.input_bar_btn_motion);
    contentEditText = (EmotionEditText) findViewById(R.id.input_bar_et_emotion);
    sendTextBtn = findViewById(R.id.input_bar_btn_send_text);
    voiceBtn = findViewById(R.id.input_bar_btn_voice);
    keyboardBtn = findViewById(R.id.input_bar_btn_keyboard);
    moreLayout = findViewById(R.id.input_bar_layout_more);
    emotionLayout = findViewById(R.id.input_bar_layout_emotion);
    emotionPager = (ViewPager)findViewById(R.id.input_bar_viewpager_emotin);
    recordBtn = (RecordButton) findViewById(R.id.input_bar_btn_record);

    actionLayout = findViewById(R.id.input_bar_layout_action);
    cameraBtn = findViewById(R.id.input_bar_btn_camera);
    locationBtn = findViewById(R.id.input_bar_btn_location);
    pictureBtn = findViewById(R.id.input_bar_btn_picture);

    setEditTextChangeListener();
    initEmotionPager();
    initRecordBtn();

    actionBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean showActionView =
          (GONE == moreLayout.getVisibility() || GONE == actionLayout.getVisibility());
        moreLayout.setVisibility(showActionView ? VISIBLE : GONE);
        actionLayout.setVisibility(showActionView ? VISIBLE : GONE);
        emotionLayout.setVisibility(View.GONE);
        SoftInputUtils.hideSoftInput(getContext(), contentEditText);
      }
    });

    emotionBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean showEmotionView =
          (GONE == moreLayout.getVisibility() || GONE == emotionLayout.getVisibility());
        moreLayout.setVisibility(showEmotionView ? VISIBLE : GONE);
        emotionLayout.setVisibility(showEmotionView ? VISIBLE : GONE);
        actionLayout.setVisibility(View.GONE);
        SoftInputUtils.hideSoftInput(getContext(), contentEditText);
      }
    });

    contentEditText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        moreLayout.setVisibility(View.GONE);
        SoftInputUtils.showSoftInput(getContext(), contentEditText);
      }
    });

    keyboardBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showTextLayout();
      }
    });

    voiceBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showAudioLayout();
      }
    });

    sendTextBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
          Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
          return;
        }

        contentEditText.setText("");
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            sendTextBtn.setEnabled(true);
          }
        }, MIN_INTERVAL_SEND_MESSAGE);

        EventBus.getDefault().post(
          new InputBottomBarTextEvent(InputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, getTag()));
      }
    });

    pictureBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new InputBottomBarEvent(InputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION, getTag()));
      }
    });

    cameraBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new InputBottomBarEvent(InputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION, getTag()));
      }
    });

    locationBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new InputBottomBarLocationClickEvent(InputBottomBarEvent.INPUTBOTTOMBAR_LOCATION_ACTION, getTag()));
      }
    });
  }

  /**
   * 初始化 emotionPager
   */
  private void initEmotionPager() {
    List<View> views = new ArrayList<View>();
    for (int i = 0; i < EmotionHelper.emojiGroups.size(); i++) {
      views.add(getEmotionGridView(i));
    }
    ChatEmotionPagerAdapter pagerAdapter = new ChatEmotionPagerAdapter(views);
    emotionPager.setOffscreenPageLimit(3);
    emotionPager.setAdapter(pagerAdapter);
  }

  private View getEmotionGridView(int pos) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View emotionView = inflater.inflate(R.layout.chat_emotion_gridview, null, false);
    GridView gridView = (GridView) emotionView.findViewById(R.id.gridview);
    final ChatEmotionGridAdapter chatEmotionGridAdapter = new ChatEmotionGridAdapter(getContext());
    List<String> pageEmotions = EmotionHelper.emojiGroups.get(pos);
    chatEmotionGridAdapter.setDatas(pageEmotions);
    gridView.setAdapter(chatEmotionGridAdapter);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String emotionText = (String) parent.getAdapter().getItem(position);
        int start = contentEditText.getSelectionStart();
        StringBuffer sb = new StringBuffer(contentEditText.getText());
        sb.replace(contentEditText.getSelectionStart(), contentEditText.getSelectionEnd(), emotionText);
        contentEditText.setText(sb.toString());

        CharSequence info = contentEditText.getText();
        if (info instanceof Spannable) {
          Spannable spannable = (Spannable) info;
          Selection.setSelection(spannable, start + emotionText.length());
        }
      }
    });
    return gridView;
  }

  /**
   * 初始化录音按钮
   */
  private void initRecordBtn() {
    recordBtn.setSavePath(com.avoscloud.leanchatlib.utils.PathUtils.getRecordPathByCurrentTime(getContext()));
    recordBtn.setRecordEventListener(new RecordButton.RecordEventListener() {
      @Override
      public void onFinishedRecord(final String audioPath, int secs) {
        EventBus.getDefault().post(
          new InputBottomBarRecordEvent(InputBottomBarEvent.INPUTBOTTOMBAR_SEND_AUDIO_ACTION, audioPath, secs, getTag()));
      }

      @Override
      public void onStartRecord() {}
    });
  }

  /**
   * 展示文本输入框及相关按钮，隐藏不需要的按钮及 layout
   */
  private void showTextLayout() {
    contentEditText.setVisibility(View.VISIBLE);
    recordBtn.setVisibility(View.GONE);
    voiceBtn.setVisibility(contentEditText.getText().length() > 0 ? GONE : VISIBLE);
    sendTextBtn.setVisibility(contentEditText.getText().length() > 0 ? VISIBLE : GONE);
    keyboardBtn.setVisibility(View.GONE);
    moreLayout.setVisibility(View.GONE);
    contentEditText.requestFocus();
    SoftInputUtils.showSoftInput(getContext(), contentEditText);
  }

  /**
   * 展示录音相关按钮，隐藏不需要的按钮及 layout
   */
  private void showAudioLayout() {
    contentEditText.setVisibility(View.GONE);
    recordBtn.setVisibility(View.VISIBLE);
    voiceBtn.setVisibility(GONE);
    keyboardBtn.setVisibility(VISIBLE);
    moreLayout.setVisibility(View.GONE);
    SoftInputUtils.hideSoftInput(getContext(), contentEditText);
  }

  /**
   * 设置 text change 事件，有文本时展示发送按钮，没有文本时展示切换语音的按钮
   */
  private void setEditTextChangeListener() {
    contentEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        boolean showSend = charSequence.length() > 0;
        keyboardBtn.setVisibility(!showSend ? View.VISIBLE : GONE);
        sendTextBtn.setVisibility(showSend ? View.VISIBLE : GONE);
        voiceBtn.setVisibility(View.GONE);
      }

      @Override
      public void afterTextChanged(Editable editable) {}
    });
  }
}

package com.way.heard.ui.views.swipecard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.utils.GlideImageLoader;

/**
 * Created by pc on 2016/5/9.
 */
@SuppressLint("NewApi")
public class CardItemView extends LinearLayout {

    public ImageView imageView;
    private Context mContext;
    private TextView userNameTv;
    private TextView imageNumTv;
    private TextView likeNumTv;

    public CardItemView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.item_swipe_card, this);
        imageView = (ImageView) findViewById(R.id.card_image_view);
        userNameTv = (TextView) findViewById(R.id.card_user_name);
        imageNumTv = (TextView) findViewById(R.id.card_pic_num);
        likeNumTv = (TextView) findViewById(R.id.card_like);
    }

    public void fillData(Image itemData) {
        GlideImageLoader.displayImage(mContext, itemData.getUrl(), imageView);
        userNameTv.setText(itemData.getAuthor().getUsername());
        imageNumTv.setText("0");
        likeNumTv.setText(itemData.getLikes() == null ? "0" : itemData.getLikes().size() + "");
    }

//    public void fillData(CardDataItem itemData) {
//        ImageLoader.getInstance().displayImage(itemData.imagePath, imageView);
//        userNameTv.setText(itemData.userName);
//        imageNumTv.setText(itemData.imageNum + "");
//        likeNumTv.setText(itemData.likeNum + "");
//    }
}

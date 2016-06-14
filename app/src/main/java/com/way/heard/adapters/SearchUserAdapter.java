package com.way.heard.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.utils.GlideImageLoader;

import java.util.List;

/**
 * Created by pc on 2016/6/14.
 */
public class SearchUserAdapter extends ArrayAdapter<AVUser> {
    private final static String TAG = SearchUserAdapter.class.getName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
    private Context mContext;
    private List<AVUser> mUsers;

    public SearchUserAdapter(Context context, List<AVUser> users) {
        super(context, R.layout.item_contact_normal, users);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mUsers = users;
    }

    public void setData(List<AVUser> users) {
        this.mUsers = users;
    }

    @Override
    public int getCount() {
        if (mUsers == null) {
            return 0;
        } else {
            return mUsers.size();
        }
    }

    @Override
    public AVUser getItem(int position) {
        if (mUsers == null || mUsers.size() == 0) {
            return null;
        } else {
            return mUsers.get(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_contact_normal, null);
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_item_contact_normal_avatar);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_item_contact_normal_nickname);
            holder.tvSignature = (TextView) convertView.findViewById(R.id.tv_item_contact_normal_signature);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        AVUser item = mUsers.get(position);
        String avatar = item.getString("avatar");
        if (!TextUtils.isEmpty(avatar)) {
            GlideImageLoader.displayImage(mContext, avatar, holder.ivAvatar);
        }
        String username = item.getUsername();
        if (!TextUtils.isEmpty(username)) {
            holder.tvUsername.setText(username);
        }
        String signature = item.getString("signature");
        if (!TextUtils.isEmpty(signature)) {
            holder.tvSignature.setText(signature);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvSignature;
    }
}
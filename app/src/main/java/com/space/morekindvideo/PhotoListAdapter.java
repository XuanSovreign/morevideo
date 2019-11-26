package com.space.morekindvideo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by licht on 2019/11/25.
 */

public class PhotoListAdapter extends BaseAdapter {
    private Context context;
    private List<UserBean> userBeans;

    public PhotoListAdapter(Context context, List<UserBean> userBeans) {
        this.context = context;
        this.userBeans = userBeans;
    }

    @Override
    public int getCount() {
        return userBeans == null ? 0 : userBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_user_photo, null);
             holder = new ViewHolder();
             holder.ivChoice=convertView.findViewById(R.id.iv_list_sure);
             holder.ivUser=convertView.findViewById(R.id.iv_list_user);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }

        if (userBeans.get(position).isChoice) {
            holder.ivChoice.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoice.setVisibility(View.GONE);
        }
        GlideApp.with(context).load(userBeans.get(position).fileName).into(holder.ivUser);
        return convertView;
    }

    private class ViewHolder {
        public ImageView ivChoice;
        public ImageView ivUser;
    }

    public void addList(List<UserBean> userBeanList) {
        userBeans.addAll(userBeanList);
        notifyDataSetChanged();
    }

}

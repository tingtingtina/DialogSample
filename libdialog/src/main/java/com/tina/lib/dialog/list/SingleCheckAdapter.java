package com.tina.lib.dialog.list;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tina.lib.dialog.R;

import java.util.ArrayList;

/*
 * Create by Tina
 * Date: 2018/9/5
 * Description：
 */
public class SingleCheckAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<SingleCheckItem> mListItem;
    private int mSelectIndex = -1;

    public SingleCheckAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ((this.mListItem == null) ? 0 : mListItem.size());
    }

    @Override
    public Object getItem(int position) {
        return ((this.mListItem == null) ? null : mListItem.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置列表信息
     *
     * @param list set list
     */
    public void setListItem(ArrayList<SingleCheckItem> list) {
        this.mListItem = list;
    }

    public void setSelectIndex(int index) {
        this.mSelectIndex = index;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.dialog_single_choice_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = view.findViewById(R.id.text);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SingleCheckItem item = mListItem.get(position);

        if (item.isSetColor()) {
            viewHolder.mTextView.setTextColor(item.getColor());
        }else{
            viewHolder.mTextView.setTextColor(Color.BLACK);
        }
        viewHolder.mTextView.setText(item.getContent());
        return view;
    }

    public class ViewHolder {
        TextView mTextView;
    }
}

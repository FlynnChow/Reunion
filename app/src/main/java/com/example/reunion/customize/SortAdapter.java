package com.example.reunion.customize;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.reunion.R;
import com.lljjcoder.style.citylist.sortlistview.SortModel;
import com.lljjcoder.style.citypickerview.R.id;
import com.lljjcoder.style.citypickerview.R.layout;
import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void updateListView(List<SortModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return this.list.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    @SuppressLint("WrongConstant")
    public View getView(int position, View view, ViewGroup arg2) {
        SortAdapter.ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new SortAdapter.ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.cust_list_item, (ViewGroup)null);
            viewHolder.tvTitle = (TextView)view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView)view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (SortAdapter.ViewHolder)view.getTag();
        }

        if (null != this.list && !this.list.isEmpty()) {
            SortModel mContent = (SortModel)this.list.get(position);
            int section = this.getSectionForPosition(position);
            if (position == this.getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(0);
                viewHolder.tvLetter.setText(mContent.getSortLetters());
            } else {
                viewHolder.tvLetter.setVisibility(8);
            }

            viewHolder.tvTitle.setText(((SortModel)this.list.get(position)).getName());
        }

        return view;
    }

    public int getSectionForPosition(int position) {
        return ((SortModel)this.list.get(position)).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for(int i = 0; i < this.getCount(); ++i) {
            String sortStr = ((SortModel)this.list.get(i)).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        return sortStr.matches("[A-Z]") ? sortStr : "#";
    }

    public Object[] getSections() {
        return null;
    }

    static final class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;

        ViewHolder() {
        }
    }
}


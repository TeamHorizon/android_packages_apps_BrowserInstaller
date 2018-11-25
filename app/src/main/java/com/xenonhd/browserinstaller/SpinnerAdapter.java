package com.xenonhd.browserinstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {
    Context mContext;
    int mIcons[];
    String[] mBrowsers;
    LayoutInflater inflter;

    public SpinnerAdapter(Context context, int[] icons, String[] browsers) {
        this.mContext = context;
        this.mIcons = icons;
        this.mBrowsers = browsers;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return mIcons.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflter.inflate(R.layout.spinner_item, null);

        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);
        icon.setImageResource(mIcons[i]);
        names.setText(mBrowsers[i]);
        return view;
    }
}

package com.example.androidapp_hw9.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomSearchAdapter extends ArrayAdapter {

    private List<String> dataList;
    private Context mContext;
    private int searchResultItemLayout;

    public CustomSearchAdapter(Context context, int resource, List<String> storeSourceDataLst) {
        super(context, resource, storeSourceDataLst);
        dataList = storeSourceDataLst;
        mContext = context;
        searchResultItemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(searchResultItemLayout, parent, false);
        }

        TextView resultItem = (TextView) view.findViewById(android.R.id.text1);
        resultItem.setText(getItem(position));
        return view;
    }
}
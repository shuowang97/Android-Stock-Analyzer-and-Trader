package com.example.androidapp_hw9.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.adapter.MyAdapter;
import com.example.androidapp_hw9.entity.LocalStockEntity;
import com.example.androidapp_hw9.util.BaseData;
import com.example.androidapp_hw9.util.ItemMoveCallback;
import com.example.androidapp_hw9.util.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortfolioFragment extends Fragment {

    private List<LocalStockEntity> localStockEntityList;
    private TextView mDateTv;
    private TextView mNetWorthTv;

    private MyAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
//    ListViewAdapter listViewAdapter;
//    private ListView mLocalLv;

    View view = null;
    private RecyclerView mRc;
    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_portfolio_fragment, container, false);
        initView();
        setView();
        enableSwipeToDeleteAndUndo();
        return view;
    }

    private void setView() {
        mDateTv.setText(getCurrentTime());
//        editor.putString(BaseData.NETWORTH, "19999.99");
//        editor.apply();
        mNetWorthTv.setText(sharedPreferences.getString(BaseData.NETWORTH, "value not found"));

    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MMMM dd, yyyy");
        Date date = new Date();
        return sdf.format(date);
    }

    private void initView() {

        localStockEntityList = new ArrayList<>();
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("MSFT", "", 8, 202.68, 10.57));
        localStockEntityList.add(new LocalStockEntity("GOOGL", "", 5, 1510.8, 88.08));
        localStockEntityList.add(new LocalStockEntity("GOOGL", "", 5, 1510.8, 88.08));
        localStockEntityList.add(new LocalStockEntity("GOOGL", "", 5, 1510.8, 88.08));
        localStockEntityList.add(new LocalStockEntity("GOOGL", "", 5, 1510.8, 88.08));
        localStockEntityList.add(new LocalStockEntity("GOOGL", "", 5, 1510.8, 88.08));
        localStockEntityList.add(new LocalStockEntity("SNOW", "", 1, 11.11, 88.08));

        mDateTv = view.findViewById(R.id.date_tv);
        mNetWorthTv = view.findViewById(R.id.netWorth_tv);
//        mLocalLv = view.findViewById(R.id.local_lv);
        mRc = view.findViewById(R.id.rc);

        // TODO: delete?
        mRc.setNestedScrollingEnabled(false);


        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };


        sharedPreferences = this.getActivity().getSharedPreferences(BaseData.LOCAL_STORAGE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        listViewAdapter = new ListViewAdapter();
//        mLocalLv.setAdapter(listViewAdapter);

        recyclerViewAdapter = new MyAdapter(getContext(), localStockEntityList, false);
        mRc.setLayoutManager(mLayoutManager);
        mRc.setAdapter(recyclerViewAdapter);
        mRc.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//        ViewGroup.LayoutParams layoutParams = mLocalLv.getLayoutParams();
//        layoutParams.height = localStockEntityList.size() * 200;

        mLinearLayout = view.findViewById(R.id.linearLayout);
    }

//    private class ListViewAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return localStockEntityList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return localStockEntityList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHandler viewHandler = null;
//            if (convertView == null) {
//                viewHandler = new ViewHandler();
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapt_local_list, null);
//                viewHandler.tvTicker = convertView.findViewById(R.id.local_ticker_tv);
//                viewHandler.tvShares = convertView.findViewById(R.id.local_shares_tv);
//                viewHandler.tvCurrent = convertView.findViewById(R.id.local_current_tv);
//                viewHandler.tvChange = convertView.findViewById(R.id.local_change_tv);
//                convertView.setTag(viewHandler);
//            } else {
//                viewHandler = (ViewHandler) convertView.getTag();
//            }
//
//            initializeView((LocalStockEntity) getItem(position), viewHandler);
//            return convertView;
//        }
//
//        private void initializeView(LocalStockEntity item, ViewHandler viewHandler) {
//            viewHandler.tvTicker.setText(item.getTicker());
//            viewHandler.tvShares.setText(item.getShares() + " shares");
//            viewHandler.tvCurrent.setText(item.getCurrent() + "");
//            viewHandler.tvChange.setText(item.getChange() + "");
//        }
//
//        class ViewHandler {
//            TextView tvTicker;
//            TextView tvShares;
//            TextView tvCurrent;
//            TextView tvChange;
//        }
//    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final LocalStockEntity item = recyclerViewAdapter.getData().get(position);

                recyclerViewAdapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(mLinearLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerViewAdapter.restoreItem(item, position);
                        mRc.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        // enable swipe
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRc);

        // enable drag
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback((ItemMoveCallback.ItemTouchHelperContract) recyclerViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRc);

    }




}
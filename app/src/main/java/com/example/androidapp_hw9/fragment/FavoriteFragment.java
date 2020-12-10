package com.example.androidapp_hw9.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.adapter.MyAdapter;
import com.example.androidapp_hw9.entity.LastPriceEntity;
import com.example.androidapp_hw9.entity.LocalStockEntity;
import com.example.androidapp_hw9.entity.LocalStoreEntity;
import com.example.androidapp_hw9.util.BaseData;
import com.example.androidapp_hw9.util.GsonParser;
import com.example.androidapp_hw9.util.ItemMoveCallback;
import com.example.androidapp_hw9.util.MyHttp;
import com.example.androidapp_hw9.util.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private List<LocalStockEntity> favoriteFragmentList;
    private List<LocalStoreEntity> localStoreEntityList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //    ListViewAdapter listViewAdapter;

    DecimalFormat df = new DecimalFormat("0.00");
    private MyAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Gson gson;

    View view = null;
    private RecyclerView mRc;
    private LinearLayout mLinearLayout;
//    private ListView mFavoriteLv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_favorite_fragment, container, false);
        initView();
        setView();
        enableSwipeToDeleteAndUndo();
//        new MySmallAsyncTask(this).execute();
        Log.d("aaaa", "here!!!!");
        return view;
    }


    private void setView() {

        getFavoriteList();

        Log.d("aaaa", favoriteFragmentList.size()+"");

    }

    private void setUpRecyclerView() {
        recyclerViewAdapter = new MyAdapter(getContext(), favoriteFragmentList, true);
        mRc.setLayoutManager(mLayoutManager);
        mRc.setAdapter(recyclerViewAdapter);
        mRc.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getFavoriteList() {
        String favorites = sharedPreferences.getString(BaseData.FAVORITE_STOCK_LIST, "");
        if (favorites == null) {
            favorites = "";
        }
        localStoreEntityList = new ArrayList<>();
        try {
            if (!favorites.equals("")) {
                JSONArray jsonArray = new JSONArray(favorites);
                GsonParser.parseNoHeaderJSONArray(localStoreEntityList, jsonArray, LocalStoreEntity.class);

                Log.d("aaaa", localStoreEntityList.size() + "");

                while (favoriteFragmentList.size() < localStoreEntityList.size()) {

                    for (int i = 0; i < localStoreEntityList.size(); i++) {
                        LocalStoreEntity item = localStoreEntityList.get(i);
                        MyHttp.getArray(BaseData.LAST_PRICE_URL + item.getTicker(), getContext(), new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray jsonArray) {
                                gson = new Gson();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = jsonArray.getJSONObject(0);
                                    LastPriceEntity lastPriceEntity = gson.fromJson(jsonObject.toString(), LastPriceEntity.class);
                                    double stockDiffer = (lastPriceEntity.getLast() - lastPriceEntity.getPrevClose());
                                    LocalStockEntity stockItem = new LocalStockEntity(item.getTicker(), item.getName(), 0, lastPriceEntity.getLast(), stockDiffer);
                                    favoriteFragmentList.add(stockItem);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                setUpRecyclerView();


                // TODO: add progress bar
                Log.d("aaaa", "favorite list loaded");
            }

        } catch (JSONException e) {
            Log.d("aaaa", "catch");
            e.printStackTrace();
        }
    }

    private void initView() {
//        mFavoriteLv = view.findViewById(R.id.favorite_lv);

        favoriteFragmentList = new ArrayList<>();

//        listViewAdapter = new ListViewAdapter();
//        mFavoriteLv.setAdapter(listViewAdapter);

//        ViewGroup.LayoutParams layoutParams = mFavoriteLv.getLayoutParams();
//        layoutParams.height = favoriteFragmentList.size() * 200;
        mRc = view.findViewById(R.id.rc);

        //TODO: delete?
        mRc.setNestedScrollingEnabled(false);


        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        sharedPreferences = this.getActivity().getSharedPreferences(BaseData.LOCAL_STORAGE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mLinearLayout = view.findViewById(R.id.linearLayout);

    }



    // TODO: below is for List view, which is hard to drag and drop
//    private class ListViewAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return favoriteFragmentList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return favoriteFragmentList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ListViewAdapter.ViewHandler viewHandler = null;
//            if (convertView == null) {
//                viewHandler = new ListViewAdapter.ViewHandler();
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapt_favorite_list, null);
//                viewHandler.tvTicker = convertView.findViewById(R.id.favorite_ticker_tv);
//                viewHandler.tvName = convertView.findViewById(R.id.favorite_name_tv);
//                viewHandler.tvCurrent = convertView.findViewById(R.id.favorite_current_tv);
//                viewHandler.tvChange = convertView.findViewById(R.id.favorite_change_tv);
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
//            viewHandler.tvName.setText(item.getName());
//            viewHandler.tvCurrent.setText(item.getCurrent() + "");
//            viewHandler.tvChange.setText(item.getChange() + "");
//        }
//
//        class ViewHandler {
//            TextView tvTicker;
//            TextView tvName;
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


//    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//
//        private List<LocalStockEntity> mData;
//
//        public MyAdapter(List<LocalStockEntity> data) {
//            this.mData = data;
//        }
//
//        public void updateData(List<LocalStockEntity> data) {
//            this.mData = data;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            // 实例化展示的view
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapt_favorite_list, parent, false);
//            // 实例化viewholder
//            ViewHolder viewHolder = new ViewHolder(v);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            // 绑定数据
//            holder.tvTicker.setText(mData.get(position).getTicker());
//            holder.tvName.setText(mData.get(position).getName());
//            holder.tvCurrent.setText(mData.get(position).getCurrent() + "");
//            holder.tvChange.setText(mData.get(position).getChange() + "");
//        }
//
//        @Override
//        public int getItemCount() {
//            return mData == null ? 0 : mData.size();
//        }
//
//        public static class ViewHolder extends RecyclerView.ViewHolder {
//
//            TextView tvTicker;
//            TextView tvName;
//            TextView tvCurrent;
//            TextView tvChange;
//
//            public ViewHolder(View convertView) {
//                super(convertView);
//                tvTicker = convertView.findViewById(R.id.favorite_ticker_tv);
//                tvName = convertView.findViewById(R.id.favorite_name_tv);
//                tvCurrent = convertView.findViewById(R.id.favorite_current_tv);
//                tvChange = convertView.findViewById(R.id.favorite_change_tv);
//            }
//        }
//    }


}
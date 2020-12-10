package com.example.androidapp_hw9.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.activity.SearchableActivity;
import com.example.androidapp_hw9.entity.FavoriteStoreEntity;
import com.example.androidapp_hw9.entity.LocalStockEntity;
import com.example.androidapp_hw9.entity.LocalStoreEntity;
import com.example.androidapp_hw9.util.BaseData;
import com.example.androidapp_hw9.util.GsonParser;
import com.example.androidapp_hw9.util.ItemMoveCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

///public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private List<LocalStockEntity> mData;
    private ImageView mIvToDetail;
    private Context context;
    private ImageView mTrendImg;
    private boolean favorite;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private LinearLayout mItemLayout;
    private ArrayList<FavoriteStoreEntity> favoriteStoreEntityList;
    private List<LocalStoreEntity> localStoreEntityList;
    private Gson gson;


    public MyAdapter(Context context, List<LocalStockEntity> data, boolean favorite) {
        this.favorite = favorite;
        this.mData = data;
        this.context = context;
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(BaseData.LOCAL_STORAGE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        favoriteStoreEntityList = new ArrayList<>();
        localStoreEntityList = new ArrayList<>();
    }

    public void updateData(List<LocalStockEntity> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {

        if (favorite && favoriteStoreEntityList.size() == 0) {
            getFavoriteList();
        } else if (!favorite && localStoreEntityList.size() == 0) {
            getLocalList();
        }

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
                if (favorite) {
                    Collections.swap(favoriteStoreEntityList, i, i + 1);
                } else {
                    Collections.swap(localStoreEntityList, i, i + 1);
                }
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
                if (favorite) {
                    Collections.swap(favoriteStoreEntityList, i, i - 1);
                } else {
                    Collections.swap(localStoreEntityList, i, i - 1);
                }
            }
        }

        String res;
        if (favorite) {
            res = gson.toJson(favoriteStoreEntityList);
            Log.d("cccc", res + " in favorite");
            editor.putString(BaseData.FAVORITE_STOCK_LIST, res);
        } else {
            res = gson.toJson(localStoreEntityList);
            Log.d("cccc", res + " in local");
            editor.putString(BaseData.LOCAL_STOCK_LIST, res);
        }
        editor.apply();


        notifyItemMoved(fromPosition, toPosition);
    }

    private void getLocalList() {
        localStoreEntityList = new ArrayList<>();
        String favorites = sharedPreferences.getString(BaseData.LOCAL_STOCK_LIST, "");

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(favorites);
            GsonParser.parseNoHeaderJSONArray(localStoreEntityList, jsonArray, LocalStoreEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFavoriteList() {
        favoriteStoreEntityList = new ArrayList<>();

        String favorites = sharedPreferences.getString(BaseData.FAVORITE_STOCK_LIST, "");

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(favorites);
            GsonParser.parseNoHeaderJSONArray(favoriteStoreEntityList, jsonArray, FavoriteStoreEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        Log.d("bbbb", "row selected");
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapt_local_list, parent, false);

        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);

        // enable go to detail button
        setView(v, viewHolder);

        return viewHolder;
    }

    private void setView(View view, ViewHolder viewHolder) {

        mIvToDetail = view.findViewById(R.id.iv_to_detail);
        mTrendImg = view.findViewById(R.id.trend_img);
        mItemLayout = view.findViewById(R.id.item_layout);

        mIvToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchableActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ticker", mData.get(viewHolder.getAdapterPosition()).getTicker());
                context.startActivity(intent);
            }
        });

        mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchableActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ticker", mData.get(viewHolder.getAdapterPosition()).getTicker());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        holder.tvTicker.setText(mData.get(position).getTicker());

        if (mData.get(position).getName().equals("")) {
            holder.tvShares.setText(mData.get(position).getShares() + " shares");
        } else {
            if (mData.get(position).getShares() != 0) {
                holder.tvShares.setText(mData.get(position).getShares() + " shares");
            } else {
                holder.tvShares.setText(mData.get(position).getName());
            }
        }

        holder.tvCurrent.setText(mData.get(position).getCurrent() + "");

        double change = mData.get(position).getChange();
        String changeString = String.valueOf(change);

        if (mData.get(position).getChange() > 0) {
            holder.tvChange.setTextColor(BaseData.GREEN);
            mTrendImg.setImageResource(R.drawable.ic_twotone_trending_up_24);
            holder.tvChange.setText(changeString);
        } else {
            holder.tvChange.setText(changeString.substring(1));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public List<LocalStockEntity> getData() {
        return mData;
    }

    public void removeItem(int position) {
        mData.remove(mData.get(position));
        notifyItemRemoved(position);
    }

    public void restoreItem(LocalStockEntity item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTicker;
        TextView tvShares;
        TextView tvCurrent;
        TextView tvChange;
        View rowView;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView;

            tvTicker = convertView.findViewById(R.id.local_ticker_tv);
            tvShares = convertView.findViewById(R.id.local_shares_tv);
            tvCurrent = convertView.findViewById(R.id.local_current_tv);
            tvChange = convertView.findViewById(R.id.local_change_tv);
        }
    }
}
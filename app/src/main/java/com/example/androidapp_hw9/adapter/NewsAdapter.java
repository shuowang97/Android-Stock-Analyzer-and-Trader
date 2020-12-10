package com.example.androidapp_hw9.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.activity.DialogActivity;
import com.example.androidapp_hw9.entity.NewsEntity;
import com.example.androidapp_hw9.util.DateUtil;
import com.example.androidapp_hw9.util.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsEntity> mData;
    private Context context;
    private CardView mNewsCard;

    public NewsAdapter(Context context, List<NewsEntity> data) {
        this.mData = data;
        this.context = context;
    }

    public void updateData(List<NewsEntity> data) {
        this.mData = data;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        // first row
        if (viewType == 1) {
            // 实例化展示的view
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_news_big, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_news_small, parent, false);
        }

        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);

        // enable go to detail button
        setView(v, viewHolder);

        return viewHolder;
    }

    private void setView(View view, ViewHolder viewHolder) {
//
//        mIvToDetail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SearchableActivity.class);
//                intent.putExtra("ticker", mData.get(viewHolder.getAdapterPosition()).getTicker());
//                context.startActivity(intent);
//            }
//        });

        viewHolder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.d("aaaa", (String) viewHolder.mNewsSmallTitle.getText());
                Intent openURL = new Intent(Intent.ACTION_VIEW);

                openURL.setData(Uri.parse(mData.get(position).getUrl()));
                openURL.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openURL);
            }
        });

        viewHolder.rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent(context, DialogActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("news", mData.get(position));
                intent.putExtras(bundle);

                context.startActivity(intent);


                // return true means do not treat it as single click
                return true;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        holder.mNewsSmallSource.setText(mData.get(position).getSource().getName());

        String timeString = mData.get(position).getPublishedAt();
        String res = DateUtil.dateHelper(timeString);
        holder.mNewsSmallTime.setText(res);

        holder.mNewsSmallTitle.setText(mData.get(position).getTitle());
//        holder.ivImage.setText(mData.get(position).getChange() + "");

        ViewGroup.LayoutParams layoutParams = holder.mNewsSmallImage.getLayoutParams();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        if (position != 0) {
            Picasso.with(context)
                    .load(mData.get(position).getUrlToImage())
                    .resize(100, 100)
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(holder.mNewsSmallImage);
        } else {
            Picasso.with(context)
                    .load(mData.get(position).getUrlToImage())
                    .fit()
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(holder.mNewsSmallImage);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public List<NewsEntity> getData() {
        return mData;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mNewsSmallSource;
        TextView mNewsSmallTime;
        TextView mNewsSmallTitle;
        ImageView mNewsSmallImage;
        View rowView;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView;

            mNewsSmallSource = convertView.findViewById(R.id.news_small_source);
            mNewsSmallTime = convertView.findViewById(R.id.news_small_time);
            mNewsSmallTitle = convertView.findViewById(R.id.news_small_title);
            mNewsSmallImage = convertView.findViewById(R.id.news_small_image);
        }
    }
}

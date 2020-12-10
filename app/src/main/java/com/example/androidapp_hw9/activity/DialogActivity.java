package com.example.androidapp_hw9.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.entity.NewsEntity;
import com.squareup.picasso.Picasso;

public class DialogActivity extends Activity {

    private ImageView mDialogImg;
    private ImageView mDialogChrome;
    private NewsEntity newsEntity;
    private ImageView mDialogTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        initView();
        setView();
    }

    private void setView() {
        mDialogChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openURL = new Intent(Intent.ACTION_VIEW);
                openURL.setData(Uri.parse(newsEntity.getUrl()));
                startActivity(openURL);
            }
        });

        mDialogTwitter.setOnClickListener(new View.OnClickListener() {
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + "Check out this link: " + "&url=" + newsEntity.getUrl();

            @Override
            public void onClick(View v) {
                Intent openTwitter = new Intent(Intent.ACTION_VIEW);
                openTwitter.setData(Uri.parse(tweetUrl));
                startActivity(openTwitter);
            }
        });
    }

    private void initView() {
        mDialogImg = findViewById(R.id.dialog_img);
        mDialogChrome = findViewById(R.id.dialog_chrome);
        mDialogTwitter = findViewById(R.id.dialog_twitter);

        Intent intent = getIntent();
        newsEntity = (NewsEntity) intent.getExtras().getSerializable("news");

        Picasso.with(getApplicationContext())
                .load(newsEntity.getUrlToImage())
                .fit()
                .centerCrop()
                .into(mDialogImg);

    }
}
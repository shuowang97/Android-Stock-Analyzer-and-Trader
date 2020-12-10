package com.example.androidapp_hw9.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.example.androidapp_hw9.R;
import com.example.androidapp_hw9.adapter.NewsAdapter;
import com.example.androidapp_hw9.entity.CompanyInfoEntity;
import com.example.androidapp_hw9.entity.FavoriteStoreEntity;
import com.example.androidapp_hw9.entity.LastPriceEntity;
import com.example.androidapp_hw9.entity.LocalStoreEntity;
import com.example.androidapp_hw9.entity.NewsEntity;
import com.example.androidapp_hw9.util.BaseData;
import com.example.androidapp_hw9.util.GsonParser;
import com.example.androidapp_hw9.util.MyHttp;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Gson gson;
    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat df1 = new DecimalFormat("0.0");
    DecimalFormat df_volume = new DecimalFormat("#,###.00");

    // local storage
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Menu menu;
    private int existInFavorite = -1;
    private int existInLocal = -1;
    private List<FavoriteStoreEntity> favoriteStoreEntityList;
    private List<LocalStoreEntity> localStoreEntityList;


    // dialog
    private View dialogView;
    private View successDialogView;
    private double marketValue = 0.0;
    private double shares = 0.0;
    private double networth = 0.0;
    private double marketValueToBuy = 0.0;


    private NewsAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mDetailTickerTv;
    private TextView mDetailNameTv;
    private TextView mDetailCurrentTv;
    private TextView mDetailChangeTv;
    private TextView mDetailDescTv;
    private Button mShowMoreLess;
    private boolean ableToMore = true;
    private TextView mDetailOwnTv;
    private TextView mDetailMarketTv;
    private TextView mDetailCurrent2Tv;
    private TextView mDetailLowTv;
    private TextView mDetailBidPrice;
    private TextView mDetailOpenPrice;
    private TextView mDetailMidTv;
    private TextView mDetailHighPrice;
    private TextView mDetailVolumeTv;
    private RecyclerView mRc;


    private LastPriceEntity lastPriceEntity;
    private CompanyInfoEntity companyInfoEntity;


    private Button mTradeBtn;
    private EditText mEditText;
    private Button mBuyBtn;
    private Button mSellBtn;
    private TextView mFormulaTv;
    private TextView mRemainAmount;
    private TextView mDialogContentSuccess;
    private Button mDoneBtn;
    private LinearLayout mProgressBarLayout;
    private WebView mWebView;

    private String ticker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        setView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        if (existInFavorite == -1) {
            inflater.inflate(R.menu.menu_uncollect, menu);
        } else {
            inflater.inflate(R.menu.menu_collected, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // back button does not have a title
        if (item.getTitle() == null) {
            return super.onOptionsItemSelected(item);
        }

        if (item.getTitle().equals("AddFavorite")) {
//            Log.d("aaaa", "add to");

            FavoriteStoreEntity newFavorite = new FavoriteStoreEntity(companyInfoEntity.getTicker(), companyInfoEntity.getName());
            favoriteStoreEntityList.add(newFavorite);

            String result = gson.toJson(favoriteStoreEntityList);
//            Log.d("aaaa", "f " + result);
            editor.putString(BaseData.FAVORITE_STOCK_LIST, result);
            editor.apply();

            existInFavorite = favoriteStoreEntityList.size() - 1;
            // refresh tool bar
            setMenuVisibility(menu, "AddFavorite");
            onCreateOptionsMenu(menu);

            Toast.makeText(this,  "\"" + ticker + "\"" + " was added to favorites", Toast.LENGTH_SHORT).show();

        } else {
//            Log.d("aaaa", "remove");
            favoriteStoreEntityList.remove(existInFavorite);
            String result = gson.toJson(favoriteStoreEntityList);
//            Log.d("aaaa", "remove after " + result);
            editor.putString(BaseData.FAVORITE_STOCK_LIST, result);
            editor.apply();
            existInFavorite = -1;
            // refresh tool bar
            setMenuVisibility(menu, "RemoveFavorite");
            onCreateOptionsMenu(menu);

            Toast.makeText(this, "\"" + ticker + "\"" +   " was removed from favorites", Toast.LENGTH_SHORT).show();

        }

        return true;
    }

    private void setMenuVisibility(Menu menu, String title) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle().equals(title)) {
                item.setVisible(false);
            }
        }
    }

    private void setView() {

        mRc.setNestedScrollingEnabled(false);

        Intent intent = getIntent();
        ticker = intent.getStringExtra("ticker");

        getCompanyInfo(ticker);
        getPortfolio(ticker);
        getStats(ticker);
        getGraph(ticker);
        setEllipsis();
        setNewsList(ticker);
        getFavoriteList(ticker);
        setWebView(ticker);


        mTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double current = lastPriceEntity.getLast();
                String currentString = df.format(current);
                String result = "0" + " x $" + currentString + "/shares = $" + "0.0";
                mFormulaTv.setText(result);

                String networthString = sharedPreferences.getString(BaseData.NETWORTH, "");
                networth = Double.parseDouble(networthString);

                mRemainAmount.setText("$" + networthString + " available to buy MSFT");

                showCustomizeDialog();
            }
        });

    }

    private void setWebView(String ticker) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.clearCache(true);
        //settings.setDomStorageEnabled(true);

//        Log.d("aaaa", "ticker + " + ticker);
        mWebView.loadUrl("file:///android_asset/graph.html?name=" + ticker);

    }

    private void getLocalList(String ticker) {
        String locals = sharedPreferences.getString(BaseData.LOCAL_STOCK_LIST, "");
        if (locals == null) {
            locals = "";
            existInLocal = -1;
        }
        gson = new Gson();
        localStoreEntityList = new ArrayList<>();
        try {
            if (!locals.equals("")) {
                JSONArray jsonArray = new JSONArray(locals);
                GsonParser.parseNoHeaderJSONArray(localStoreEntityList, jsonArray, LocalStoreEntity.class);
                for (int i = 0; i < localStoreEntityList.size(); i++) {
                    LocalStoreEntity item = localStoreEntityList.get(i);
                    if (item.getTicker().equals(ticker)) {
                        existInLocal = i;
                    }
                }
                // TODO: add progress bar
//                Log.d("aaaa", "local list loaded");

                if (existInLocal != -1) {
                    shares = localStoreEntityList.get(existInLocal).getShares();
                    String sharesString = df1.format(shares);
                    mDetailOwnTv.setText("Shares Owned " + sharesString);
                    marketValue = shares * lastPriceEntity.getLast();
                    String marketValueString = df.format(marketValue);
                    mDetailMarketTv.setText("Market Value: " + marketValueString);
                }

            }
            mProgressBarLayout.setVisibility(View.GONE);

        } catch (JSONException e) {
//            Log.d("aaaa", "catch");
            e.printStackTrace();
        }
    }


    private void showCustomizeDialog() {

        TextView myMsg = new TextView(this);
        myMsg.setText("Trade " + companyInfoEntity.getName() + " shares");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(20);
        myMsg.setTypeface(Typeface.DEFAULT_BOLD);
        myMsg.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        myMsg.setPadding(0, 100, 0, 0);
        myMsg.setLayoutParams(lp);


        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditText.setBackgroundTintList(ColorStateList.valueOf(BaseData.PURPLE));
                }
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && s.length() == 2 && s.charAt(1) == '0') {
                    mEditText.setText(s.subSequence(0, 1));
                    mEditText.setSelection(mEditText.getText().length());
                }
                if (start == 1 && s.length() == 2 && s.charAt(0) == '0') {
                    mEditText.setText(s.subSequence(1, 2));
                    mEditText.setSelection(mEditText.getText().length());
                }

                String inputString = mEditText.getText().toString();
                double current = lastPriceEntity.getLast();
                String currentString = df.format(current);

                if (!inputString.equals("")) {
                    marketValueToBuy = current * Double.parseDouble(inputString);
                    String marketValueString = df.format(marketValueToBuy);
                    marketValueToBuy = Double.parseDouble(marketValueString);

                    String result = inputString + " x $" + currentString + "/shares = $" + marketValueString;
                    mFormulaTv.setText(result);
                } else {

                    String result = "0" + " x $" + currentString + "/shares = $" + "0.0";
                    mFormulaTv.setText(result);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final AlertDialog customizeDialog = new AlertDialog.Builder(SearchableActivity.this).create();

        // handle cancel
        customizeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });


        // handle error
        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }


        customizeDialog.setCustomTitle(myMsg);

        customizeDialog.setView(dialogView);

        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numString = mEditText.getText().toString();
                if (numString.equals("")) {
                    Toast.makeText(SearchableActivity.this, "Please enter valid amount", Toast.LENGTH_LONG).show();
                    return;
                }

                double sharesToBuy = Double.parseDouble(numString);

                if (sharesToBuy == 0) {
                    Toast.makeText(SearchableActivity.this, "Cannot buy less than 0 shares", Toast.LENGTH_LONG).show();
                    return;
                } else {
//                    Log.d("aaaa", sharesToBuy + "");
                    if (networth < marketValueToBuy) {
                        Toast.makeText(SearchableActivity.this, "Not enough money to buy", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        networth -= marketValueToBuy;
                        marketValue += marketValueToBuy;

                        String networthString = df.format(networth);
                        editor.putString(BaseData.NETWORTH, networthString);

//                        FavoriteStoreEntity newFavorite = new FavoriteStoreEntity(companyInfoEntity.getTicker(), companyInfoEntity.getName());
//                        favoriteStoreEntityList.add(newFavorite);
//
//                        String result = gson.toJson(favoriteStoreEntityList);
//                        Log.d("aaaa", "f " + result);
//                        editor.putString(BaseData.FAVORITE_STOCK_LIST, result);
//                        editor.apply();
//
//                        existInFavorite = favoriteStoreEntityList.size() - 1;

                        if (existInLocal == -1) {
                            LocalStoreEntity newItem = new LocalStoreEntity(companyInfoEntity.getTicker(), companyInfoEntity.getName(), sharesToBuy);
                            localStoreEntityList.add(newItem);
                            existInLocal = localStoreEntityList.size() - 1;
                        } else {

                            // No need to iteration
//                            for (LocalStoreEntity item : localStoreEntityList) {
//                                if (item.getTicker().equals(companyInfoEntity.getTicker())) {
//                                    item.setShares(shares + sharesToBuy);
//                                }
//                            }
                            LocalStoreEntity item = localStoreEntityList.get(existInLocal);
                            item.setShares(shares + sharesToBuy);
                        }

                        String result = gson.toJson(localStoreEntityList);
//                        Log.d("aaaa", "local + " + result);
                        editor.putString(BaseData.LOCAL_STOCK_LIST, result);

                        editor.apply();
                        getLocalList(companyInfoEntity.getTicker());

                        String stringToShow = "You have successfully bought " + sharesToBuy + " shares of " + companyInfoEntity.getTicker();
                        mDialogContentSuccess.setText(stringToShow);

                        showSuccessDialog("BUY");
                    }

                }

                mEditText.setBackgroundTintList(ColorStateList.valueOf(BaseData.BLACK));
                mEditText.setText("0");

                customizeDialog.dismiss();

            }
        });

        mSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numString = mEditText.getText().toString();
                if (numString.equals("")) {
                    Toast.makeText(SearchableActivity.this, "Please enter valid amount", Toast.LENGTH_LONG).show();
                    return;
                }

                double sharesToSell = Double.parseDouble(numString);

                if (sharesToSell == 0) {
                    Toast.makeText(SearchableActivity.this, "Cannot sell less than 0 shares", Toast.LENGTH_LONG).show();
                    return;
                } else {
//                    Log.d("aaaa", sharesToSell + " will be sold");
                    if (existInLocal == -1 || shares < sharesToSell) {
                        Toast.makeText(SearchableActivity.this, "Not enough shares to sell", Toast.LENGTH_LONG).show();
                        return;
                    } else {

                        networth += marketValueToBuy;
                        marketValue -= marketValueToBuy;
                        String networthString = df.format(networth);
                        editor.putString(BaseData.NETWORTH, networthString);


                        LocalStoreEntity item = localStoreEntityList.get(existInLocal);
                        item.setShares(shares - sharesToSell);

                        if (item.getShares() == 0) {
                           localStoreEntityList.remove(item);
                           existInLocal = -1;
                        }

                        String result = gson.toJson(localStoreEntityList);
//                        Log.d("aaaa", "local + " + result);
                        editor.putString(BaseData.LOCAL_STOCK_LIST, result);

                        editor.apply();
                        getLocalList(companyInfoEntity.getTicker());

                        String stringToShow = "You have successfully sold " + sharesToSell + " shares of " + companyInfoEntity.getTicker();
                        mDialogContentSuccess.setText(stringToShow);

                        showSuccessDialog("SELL");

                    }
                }

                mEditText.setBackgroundTintList(ColorStateList.valueOf(BaseData.BLACK));
                mEditText.setText("0");

                customizeDialog.dismiss();

            }
        });

        customizeDialog.show();
    }

    private void showSuccessDialog(String flag) {
//        Log.d("aaaa", "success!");

        TextView myMsg = new TextView(this);
        myMsg.setText("Congratulations!");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(30);
        myMsg.setTypeface(Typeface.DEFAULT_BOLD);
        myMsg.setTextColor(Color.WHITE);
        myMsg.setBackgroundColor(BaseData.DARK_GREEN);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        myMsg.setPadding(0, 130, 0, 0);
        myMsg.setLayoutParams(lp);

        final AlertDialog customizeDialog = new AlertDialog.Builder(SearchableActivity.this).create();

        // handle error
        if (successDialogView.getParent() != null) {
            ((ViewGroup) successDialogView.getParent()).removeView(successDialogView);
        }


        customizeDialog.setCustomTitle(myMsg);

        customizeDialog.setView(successDialogView);

        customizeDialog.show();

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customizeDialog.dismiss();
            }
        });

    }


    private void getFavoriteList(String ticker) {
        String favorites = sharedPreferences.getString(BaseData.FAVORITE_STOCK_LIST, "");
        if (favorites == null) {
            favorites = "";
            existInFavorite = -1;
        }
//        Log.d("aaaa", "fav in able: " + favorites);
        gson = new Gson();
        favoriteStoreEntityList = new ArrayList<>();
        try {
            if (!favorites.equals("")) {
                JSONArray jsonArray = new JSONArray(favorites);
                GsonParser.parseNoHeaderJSONArray(favoriteStoreEntityList, jsonArray, FavoriteStoreEntity.class);
                for (int i = 0; i < favoriteStoreEntityList.size(); i++) {
                    FavoriteStoreEntity item = favoriteStoreEntityList.get(i);
                    if (item.getTicker().equals(ticker)) {
                        existInFavorite = i;
                    }
                }
                // TODO: add progress bar
//                Log.d("aaaa", "favorite list loaded");
            }

        } catch (JSONException e) {
//            Log.d("aaaa", "catch");
            e.printStackTrace();
        }
    }

    private void setNewsList(String ticker) {

        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        MyHttp.get(BaseData.NEWS_URL + ticker, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
//                    Log.d("aaaa", jsonObject.get("articles").getClass() + "");
                    JSONArray jsonArray = (JSONArray) jsonObject.get("articles");
                    List<NewsEntity> newsEntityList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        gson = new Gson();
                        NewsEntity newsEntity = gson.fromJson(json.toString(), NewsEntity.class);
                        newsEntityList.add(newsEntity);
                    }


//                    Log.d("aaaa", "news size " + newsEntityList.size());
                    // set up adapter
                    recyclerViewAdapter = new NewsAdapter(SearchableActivity.this, newsEntityList);
                    mRc.setLayoutManager(mLayoutManager);
                    mRc.setAdapter(recyclerViewAdapter);

                    // remove cause cardView has its own
//                    mRc.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getPortfolio(String ticker) {
        // get from sharePreference

        String marketValueString = df.format(marketValue);
        String shareString = df1.format(shares);

        if (shares == 0) {
            mDetailOwnTv.setText("You have 0 shares of " + ticker);
            mDetailMarketTv.setText("Start Trading!");
        } else {
            mDetailOwnTv.setText("Shares Owned " + shareString);
            mDetailMarketTv.setText("Market Value: " + marketValueString);
        }
    }

    private void setEllipsis() {

        mShowMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ableToMore) {
                    if (mShowMoreLess.getText().equals("Show less")) {
                        mDetailDescTv.setMaxLines(2);
                        mShowMoreLess.setText("Show more...");
                    } else {
                        mDetailDescTv.setMaxLines(Integer.MAX_VALUE);
                        mShowMoreLess.setText("Show less");
                    }

                }
            }
        });


    }

    private void getCompanyInfo(String ticker) {
        MyHttp.get(BaseData.COMPANY_INFO_URL + ticker, getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                gson = new Gson();
                companyInfoEntity = gson.fromJson(jsonObject.toString(), CompanyInfoEntity.class);
                mDetailTickerTv.setText(ticker);
                mDetailNameTv.setText(companyInfoEntity.getName());
                mDetailDescTv.setText(companyInfoEntity.getDescription());

                // TODO: add progress bar
//                Log.d("aaaa", "company info loaded");

                mDetailDescTv.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = mDetailDescTv.getLineCount();
                        // Use lineCount here
                        if (lineCount > 2) {
                            // add show more
//                            Log.d("aaaa", "more 2:" + lineCount);
                            mDetailDescTv.setMaxLines(2);
                            ableToMore = true;
                        } else {
                            mShowMoreLess.setVisibility(View.GONE);
                            ableToMore = false;
                        }
                    }

                });
            }

        });
    }

    private void getGraph(String ticker) {

    }

    private void getStats(String ticker) {
        MyHttp.getArray(BaseData.LAST_PRICE_URL + ticker, getApplicationContext(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                gson = new Gson();
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArray.getJSONObject(0);
                    lastPriceEntity = gson.fromJson(jsonObject.toString(), LastPriceEntity.class);

                    //TODO: add progress bar
//                    Log.d("aaaa", "last Price loaded");

                    double stockDiffer = (lastPriceEntity.getLast() - lastPriceEntity.getPrevClose());
                    String stockDifferString = df.format(stockDiffer);
                    if (stockDiffer > 0) {
                        mDetailChangeTv.setText("$" + stockDifferString);
                        mDetailChangeTv.setTextColor(BaseData.GREEN);
                    } else if(stockDifferString.charAt(0) != '0'){
                        mDetailChangeTv.setText(stockDifferString.charAt(0) + "$" + stockDifferString.substring(1));
                        mDetailChangeTv.setTextColor(BaseData.RED);
                    }
                    double current = lastPriceEntity.getLast();
                    String currentString = df.format(current);
                    mDetailCurrentTv.setText("$" + currentString);

                    // set up stats table
                    // first row
                    mDetailCurrent2Tv.setText(currentString);

                    double low = lastPriceEntity.getLow();
                    String lowString = df.format(low);
                    mDetailLowTv.setText("Low: " + lowString);

                    Object bidPrice = lastPriceEntity.getBidPrice();
                    if (bidPrice == null) {
                        mDetailBidPrice.setText("Bid Price: 0.0");
                    } else {
                        double bidPriceAmount = Double.parseDouble(bidPrice.toString());
                        String bidPriceString = df.format(bidPriceAmount);
                        mDetailBidPrice.setText("Bid Price: " + bidPriceString);
                    }

                    // second row
                    double openPrice = lastPriceEntity.getOpen();
                    String openPriceString = df.format(openPrice);
                    mDetailOpenPrice.setText("OpenPrice: " + openPriceString);

                    Object mid = lastPriceEntity.getMid();
                    if (mid == null) {
                        mDetailMidTv.setText("Mid: 0.0");
                    } else {
                        double midAmount = Double.parseDouble(mid.toString());
                        String midString = df.format(midAmount);
                        mDetailMidTv.setText("Mid: " + midString);
                    }

                    double high = lastPriceEntity.getHigh();
                    String highString = df.format(high);
                    mDetailHighPrice.setText("High: " + highString);

                    // third row
                    int volume = lastPriceEntity.getVolume();
                    String volumeString = df_volume.format(volume);
                    mDetailVolumeTv.setText(volumeString);

                    // HAVE TO SET IT HERE!!!!!
                    getLocalList(ticker);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {

        dialogView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.dialog_buy, null);

        successDialogView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.success_dialog, null);


        mDetailDescTv = findViewById(R.id.detail_desc_tv);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        // set up back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mShowMoreLess = findViewById(R.id.show_more_less);
        mDetailOwnTv = findViewById(R.id.detail_own_tv);
        mDetailMarketTv = findViewById(R.id.detail_market_tv);
        mDetailCurrent2Tv = findViewById(R.id.detail_current2_tv);
        mDetailLowTv = findViewById(R.id.detail_low_tv);
        mDetailBidPrice = findViewById(R.id.detail_bid_price);
        mDetailOpenPrice = findViewById(R.id.detail_open_price);
        mDetailMidTv = findViewById(R.id.detail_mid_tv);
        mDetailHighPrice = findViewById(R.id.detail_high_price);
        mDetailVolumeTv = findViewById(R.id.detail_volume_tv);
        mRc = findViewById(R.id.rc);
        mDetailTickerTv = findViewById(R.id.detail_ticker_tv);
        mDetailNameTv = findViewById(R.id.detail_name_tv);
        mDetailCurrentTv = findViewById(R.id.detail_current_tv);
        mDetailChangeTv = findViewById(R.id.detail_change_tv);

        sharedPreferences = getSharedPreferences(BaseData.LOCAL_STORAGE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mTradeBtn = findViewById(R.id.trade_btn);

        mEditText = dialogView.findViewById(R.id.edit_text);
        mBuyBtn = dialogView.findViewById(R.id.buy_btn);
        mSellBtn = dialogView.findViewById(R.id.sell_btn);
        mFormulaTv = dialogView.findViewById(R.id.formula_tv);
        mRemainAmount = dialogView.findViewById(R.id.remain_amount);
        mDialogContentSuccess = successDialogView.findViewById(R.id.dialog_content_success);
        mDoneBtn = successDialogView.findViewById(R.id.done_btn);
        mProgressBarLayout = findViewById(R.id.progress_bar_layout);
        mWebView = findViewById(R.id.web_view);
    }


}
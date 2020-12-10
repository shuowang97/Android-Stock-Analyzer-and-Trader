package com.example.androidapp_hw9;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.example.androidapp_hw9.activity.SearchableActivity;
import com.example.androidapp_hw9.adapter.MyAdapter;
import com.example.androidapp_hw9.entity.AutoSuggestEntity;
import com.example.androidapp_hw9.entity.FavoriteStoreEntity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private List<String> dataArr = new ArrayList<>();
    private ArrayAdapter<String> newsAdapter;
    private TextView mMainFooter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    // save favorite list
    private List<LocalStockEntity> favoriteFragmentList; // used to render
    private LocalStockEntity[] favoriteFragmentArray;
    private List<FavoriteStoreEntity> favoriteStoreEntityList;
    private int numInFavorite = 0;

    // save portfolio list
    private List<LocalStoreEntity> localStoreEntityList;
    private List<LocalStockEntity> localFragmentList;
    private LocalStockEntity[] localFragmentArray;
    private int numInLocal = 0;


    DecimalFormat df = new DecimalFormat("0.00");
    private MyAdapter recyclerViewAdapter_favorite;
    private MyAdapter recyclerViewAdapter_portfolio;
    private RecyclerView.LayoutManager mLayoutManager_favorite;
    private RecyclerView.LayoutManager mLayoutManager_portfolio;
    private Gson gson = new Gson();

    private RecyclerView mRcFavorite;
//    private LinearLayout mLinearLayoutFavorite;


    // for portfolio
    private TextView mDateTv;
    private TextView mNetWorthTv;

    private RecyclerView mRcPortfolio;
    private LinearLayout mLinearLayoutFavorite;
    private LinearLayout mLinearLayoutPortfolio;
    private LinearLayout mProgressBar;
    private NestedScrollView mScrollBar;

    private Map<String, Float> tickerToSharesMap = new ConcurrentHashMap<>();

    // 15s
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        favoriteFragmentList = new ArrayList<>();
        recyclerViewAdapter_favorite = new MyAdapter(getApplicationContext(), favoriteFragmentList, true);
        localFragmentList = new ArrayList<>();
        recyclerViewAdapter_portfolio = new MyAdapter(getApplicationContext(), localFragmentList, false);


        refreshing();
        initView();
        setView();

        // the callback can only bind once!! --> Lead to errors previously
        enableSwipeToDeleteAndUndoForFavorite();
        enableDragAndDropForPortfolio();
    }

    private void refreshing() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("15", "aaaa");
                handler.postDelayed(this, 15000);
                getLocalList();
            }
        };

        handler.post(runnable);
    }


    private void enableSwipeToDeleteAndUndoForPortfolio() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final LocalStockEntity item = recyclerViewAdapter_portfolio.getData().get(position);

                recyclerViewAdapter_portfolio.removeItem(position);


//                Snackbar snackbar = Snackbar
//                        .make(mLinearLayoutPortfolio, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        recyclerViewAdapter_portfolio.restoreItem(item, position);
//                        mRcPortfolio.scrollToPosition(position);
//                    }
//                });
//
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();

            }
        };

        // enable swipe
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRcPortfolio);

//        // enable drag
//        ItemTouchHelper.Callback callback =
//                new ItemMoveCallback(recyclerViewAdapter_portfolio);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(mRcPortfolio);

    }

    private void enableDragAndDropForPortfolio() {
        // enable drag
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(recyclerViewAdapter_portfolio);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRcPortfolio);
    }

    private void enableSwipeToDeleteAndUndoForFavorite() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final LocalStockEntity item = recyclerViewAdapter_favorite.getData().get(position);

                recyclerViewAdapter_favorite.removeItem(position);
                for (FavoriteStoreEntity entity : favoriteStoreEntityList) {
                    if (entity.getTicker().equals(item.getTicker())) {
                        favoriteStoreEntityList.remove(entity);
                        break;
                    }
                }
                String res = gson.toJson(favoriteStoreEntityList);
                editor.putString(BaseData.FAVORITE_STOCK_LIST, res);
                Log.d("aaaa", "fav in main: " + res);
                editor.apply();


                Snackbar snackbar = Snackbar
                        .make(mLinearLayoutFavorite, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerViewAdapter_favorite.restoreItem(item, position);
                        mRcFavorite.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        // TODO: ADD it for portfolio later

        // enable swipe
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRcFavorite);


        // enable drag
        Log.d("bbbb", "ini3" + recyclerViewAdapter_favorite);
        Log.d("bbbb", localFragmentList.size() + "");

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(recyclerViewAdapter_favorite);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRcFavorite);


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("finish", "on pause");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("aaaa", "on start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onResume() {

        super.onResume();

        mProgressBar.setVisibility(View.VISIBLE);
        if (mScrollBar.getVisibility() == View.VISIBLE) {
            mScrollBar.setVisibility(View.GONE);
        }
        if (mMainFooter.getVisibility() == View.VISIBLE) {
            mMainFooter.setVisibility(View.GONE);
        }

        Log.d("aaaa", "on resume");
        getLocalList();

    }


    private void setView() {
        mMainFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openURL = new Intent(Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("https://www.tiingo.com/"));
                startActivity(openURL);
            }
        });


        mDateTv.setText(getCurrentTime());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MMMM dd, yyyy");
        Date date = new Date();
        return sdf.format(date);
    }

    private void getLocalList() {
        String locals = sharedPreferences.getString(BaseData.LOCAL_STOCK_LIST, "");
        if (locals == null) {
            locals = "";
        }
        gson = new Gson();
        localStoreEntityList = new ArrayList<>();
        localFragmentList = new ArrayList<>();
        try {
            if (!locals.equals("")) {
                JSONArray jsonArray = new JSONArray(locals);
                GsonParser.parseNoHeaderJSONArray(localStoreEntityList, jsonArray, LocalStoreEntity.class);
                localFragmentArray = new LocalStockEntity[localStoreEntityList.size()];

                numInLocal = 0;

                for (int i = 0; i < localStoreEntityList.size(); i++) {
                    LocalStoreEntity item = localStoreEntityList.get(i);
                    int finalI = i;
                    MyHttp.getArray(BaseData.LAST_PRICE_URL + item.getTicker(), getApplicationContext(), new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            gson = new Gson();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonArray.getJSONObject(0);
                                LastPriceEntity lastPriceEntity = gson.fromJson(jsonObject.toString(), LastPriceEntity.class);
                                double stockDiffer = (lastPriceEntity.getLast() - lastPriceEntity.getPrevClose());
                                String stockDifferString = df.format(stockDiffer);
                                LocalStockEntity stockItem = new LocalStockEntity(item.getTicker(), item.getName(), (float) item.getShares(), lastPriceEntity.getLast(), Double.parseDouble(stockDifferString));
//                                localFragmentList.add(stockItem);
                                localFragmentArray[finalI] = stockItem;
                                numInLocal++;

                                tickerToSharesMap.put(item.getTicker(), (float) item.getShares());

                                Log.d("15s", lastPriceEntity.toString());


                                if (numInLocal == localStoreEntityList.size()) {
                                    for (LocalStockEntity item : localFragmentArray) {
                                        localFragmentList.add(item);
                                    }
                                    getNetWorth();
                                    getFavoriteList();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                // TODO: add progress bar
//                Log.d("aaaaa", "local list loaded " + numInLocal);
            }
            if (0 == localStoreEntityList.size()) {
                getNetWorth();
                getFavoriteList();
                mProgressBar.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
//            Log.d("aaaa", "catch");
            e.printStackTrace();
        }
    }

    private void getNetWorth() {

        String remainWorth = sharedPreferences.getString(BaseData.NETWORTH, "");

        Double remain = Double.parseDouble(remainWorth);
        double sum = 0;
        for (LocalStockEntity item : localFragmentList) {
            sum += item.getCurrent() * item.getShares();
        }

        double res = sum + remain;
        String resString = df.format(res);

        mNetWorthTv.setText(resString);
    }


    private void getFavoriteList() {
        String favorites = sharedPreferences.getString(BaseData.FAVORITE_STOCK_LIST, "");
        if (favorites == null) {
            favorites = "";
        }
        favoriteStoreEntityList = new ArrayList<>();
        favoriteFragmentList = new ArrayList<>();
        try {
            if (!favorites.equals("")) {
                JSONArray jsonArray = new JSONArray(favorites);
                GsonParser.parseNoHeaderJSONArray(favoriteStoreEntityList, jsonArray, FavoriteStoreEntity.class);
                favoriteFragmentArray = new LocalStockEntity[favoriteStoreEntityList.size()];

                Log.d("aaaaa", "SP favorite: " + favoriteStoreEntityList.size());


                for (int i = 0; i < favoriteStoreEntityList.size(); i++) {
                    FavoriteStoreEntity item = favoriteStoreEntityList.get(i);
                    int finalI = i;
                    numInFavorite = 0;
                    MyHttp.getArray(BaseData.LAST_PRICE_URL + item.getTicker(), getApplicationContext(), new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            gson = new Gson();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonArray.getJSONObject(0);
                                LastPriceEntity lastPriceEntity = gson.fromJson(jsonObject.toString(), LastPriceEntity.class);
                                double stockDiffer = (lastPriceEntity.getLast() - lastPriceEntity.getPrevClose());
                                String stockDifferString = df.format(stockDiffer);

                                // add shares
//                                Log.d("aaaa", "current size local " + localFragmentList.size());
//                                Log.d("aaaa", "current size favo " + favoriteFragmentList.size());
                                LocalStockEntity stockItem;

                                if (tickerToSharesMap.containsKey(item.getTicker())) {
                                    stockItem = new LocalStockEntity(item.getTicker(), item.getName(), tickerToSharesMap.get(item.getTicker()), lastPriceEntity.getLast(), Double.parseDouble(stockDifferString));
                                } else {
                                    stockItem = new LocalStockEntity(item.getTicker(), item.getName(), 0, lastPriceEntity.getLast(), Double.parseDouble(stockDifferString));
                                }

//                                favoriteFragmentList.add(finalI, stockItem);
                                favoriteFragmentArray[finalI] = stockItem;
                                numInFavorite++;

//                                Log.d("aaaaa", "current size local " + numInLocal);
//                                Log.d("aaaaa", "current size favo " + numInFavorite);

                                if (numInFavorite == favoriteStoreEntityList.size()) {
                                    for (LocalStockEntity item : favoriteFragmentArray) {
                                        favoriteFragmentList.add(item);
                                    }
                                    setUpRecyclerView();
                                    mProgressBar.setVisibility(View.GONE);
                                    mMainFooter.setVisibility(View.VISIBLE);
                                    mScrollBar.setVisibility(View.VISIBLE);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


                // TODO: add progress bar
//                Log.d("aaaaa", "favorite list loaded " + favoriteFragmentList.size());
            }

//            Log.d("aaaa", "set vsii");
            if (0 == favoriteStoreEntityList.size()) {
                setUpRecyclerView();
                mProgressBar.setVisibility(View.GONE);
                mMainFooter.setVisibility(View.VISIBLE);
                mScrollBar.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
//            Log.d("aaaa", "catch");
            e.printStackTrace();
        }
    }

    private void setUpRecyclerView() {

//        Log.d("aaaa", "loadedup try to set up recycler");

        //recyclerViewAdapter_favorite = new MyAdapter(getApplicationContext(), favoriteFragmentList, true);
        recyclerViewAdapter_favorite.updateData(favoriteFragmentList);
        mRcFavorite.setAdapter(recyclerViewAdapter_favorite);


        recyclerViewAdapter_portfolio.updateData(localFragmentList);
        mRcPortfolio.setAdapter(recyclerViewAdapter_portfolio);

//        enableSwipeToDeleteAndUndoForFavorite();
//        enableDragAndDropForPortfolio();
    }


    private void initView() {

        mDateTv = findViewById(R.id.date_tv);
        mNetWorthTv = findViewById(R.id.netWorth_tv);
        mRcPortfolio = findViewById(R.id.rc_portfolio);
//        mMainLayout = findViewById(R.id.main_layout);
        mLinearLayoutFavorite = findViewById(R.id.linearLayout_favorite);
        mRcPortfolio = findViewById(R.id.rc_portfolio);
        mLinearLayoutPortfolio = findViewById(R.id.linearLayout_portfolio);
        mProgressBar = findViewById(R.id.progress_bar);
        mScrollBar = findViewById(R.id.scroll_bar);
        mToolbar = findViewById(R.id.toolbar);
        mMainFooter = findViewById(R.id.main_footer);


        mScrollBar.setVisibility(View.GONE);
        mMainFooter.setVisibility(View.GONE);


        favoriteFragmentList = new ArrayList<>();
        localFragmentList = new ArrayList<>();

        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);


        // TODO: IMPORTANT -> HAVE TO CALL THIS METHOD TO INVOKE onCreateOptionsMenu() AUTO
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");


        sharedPreferences = getSharedPreferences(BaseData.LOCAL_STORAGE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mRcFavorite = findViewById(R.id.rc_favorite);
        mRcFavorite.setNestedScrollingEnabled(false);

        mLayoutManager_favorite = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRcFavorite.setLayoutManager(mLayoutManager_favorite);
        // add cutting line
        mRcFavorite.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


        mLayoutManager_portfolio = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRcPortfolio.setLayoutManager(mLayoutManager_portfolio);
        // add cutting line
        mRcPortfolio.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


        // TODO: enable it
        clearSharedPreference();

    }

    private void clearSharedPreference() {
//        editor.remove(BaseData.NETWORTH);
//        editor.remove(BaseData.LOCAL_STOCK_LIST);
//        editor.remove(BaseData.FAVORITE_STOCK_LIST);
//        editor.clear();

        editor.putString(BaseData.NETWORTH, "19163.42");
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        // Create a new ArrayAdapter and add data to search auto complete object.
        // String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};

        searchAutoComplete.setAdapter(newsAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
//                Toast.makeText(getApplicationContext(), "you clicked " + queryString, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("ticker", queryString.split(" - ")[0]);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());
                }
            }

            private void showInputMethod(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Log.d("finish", "on submitting");
                startActivity(new Intent(getApplicationContext(), SearchableActivity.class));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() >= BaseData.SEARCH_QUERY_THRESHOLD) {
//                    Log.d("finish", query);
                    autoSuggest(query, searchAutoComplete, searchView);
                    // TODO： 不能在这里执行notify adapter

                } else {
//                    Log.d("finish", "cleared");
                    dataArr.clear();
                    newsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, dataArr);
                    searchAutoComplete.setAdapter(newsAdapter);
//                    newsAdapter.notifyDataSetChanged();
                }


                return true;
            }

        });


        // 设置处理搜索的activity
//        ComponentName componentName = getComponentName(); // 当前Activity
        ComponentName componentName = new ComponentName(this, SearchableActivity.class); // 指定Activity
//        Log.d("finish", componentName.toString());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    private void autoSuggest(String searchQuery, SearchView.SearchAutoComplete searchAutoComplete, SearchView searchView) {
        dataArr.clear();
        // 自己封装 -- myHttp
        String url = "https://sw-571-hw8.azurewebsites.net/routes/utility/" + searchQuery;
//        String url = "https://v1.hitokoto.cn/";
//        Log.d("finish", url);
        List<AutoSuggestEntity> autoSuggestEntityList = new ArrayList<>();

        MyHttp.getArray(url, getApplicationContext(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
//                Log.d("finish", jsonArray.toString());
                GsonParser.parseNoHeaderJSONArray(autoSuggestEntityList, jsonArray, AutoSuggestEntity.class);

//                for(AutoSuggestEntity item : autoSuggestEntityList) {
//                    Log.d("finish", item.getName());
//                }
                if (autoSuggestEntityList.size() != 0) {
                    for (int i = 0; i < autoSuggestEntityList.size(); i++) {
                        AutoSuggestEntity item = autoSuggestEntityList.get(i);
                        dataArr.add(item.getTicker() + " - " + item.getName());
//                        Log.d("finish", dataArr.toString());
                    }

                    // 因为volley是异步的 不能在autoSuggest调用的地方执行这段 （查看TODO位置）
//                    Log.d("finish", dataArr.toString() + "-----------------------------");
//                    // update adapter
//                    newsAdapter.notifyDataSetChanged();
                    newsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, dataArr);
                    searchAutoComplete.setAdapter(newsAdapter);
                    searchView.clearFocus();
                    searchView.requestFocus();
                } else {
                    dataArr.clear();
                }

            }
        });

    }


}


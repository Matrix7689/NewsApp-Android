package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarksFragment extends Fragment {

    private ArrayList<String> mtitles = new ArrayList<>();
    private ArrayList<String> mimageURLs = new ArrayList<>();
    private ArrayList<String> mdates = new ArrayList<>();
    private ArrayList<String> msections = new ArrayList<>();
    private ArrayList<String> mids = new ArrayList<>();
    private ArrayList<String> mURLS = new ArrayList<>();
    private BookmarkCardAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Boolean fetched_data;
    private static View viewz;
    private static Context ct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        viewz = v;
        ct = getContext();
        fetched_data = false;
        mRecyclerView = v.findViewById(R.id.bookmark_cards);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bmark_parse_JSON();
        final Handler handler = new Handler();
        Runnable procRun = new Runnable() {
            @Override
            public void run() {
                if(fetched_data){
                    mAdapter = new BookmarkCardAdapter(mURLS, mids, mtitles, mimageURLs, mdates, msections, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setNestedScrollingEnabled(false);
                    //mAdapter.setOnItemClickListener(HomeFragment.this);
                } else{
                    handler.postDelayed(this,100);
                }
            }
        };
        handler.post(procRun);
        return v;
    }

    static void final_delete_article()
    {
        SharedPreferences sharedPreferences = ct.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gsonObj = new Gson();
        String jsonObj = sharedPreferences.getString("bookmarks_array", null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> bookmark_full = null;
        bookmark_full = gsonObj.fromJson(jsonObj,type);

        if(bookmark_full == null || bookmark_full.size() == 0)
        {
            viewz.findViewById(R.id.disp_no_bmark).setVisibility(View.VISIBLE);
            return;
        }
    }

    private void bmark_parse_JSON() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gsonObj = new Gson();
        String jsonObj = sharedPreferences.getString("bookmarks_array", null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> bookmark_full = null;
        bookmark_full = gsonObj.fromJson(jsonObj,type);
        fetched_data = false;

        if(bookmark_full == null || bookmark_full.size() == 0)
        {
            viewz.findViewById(R.id.disp_no_bmark).setVisibility(View.VISIBLE);
            return;
        }

        for(int i=0; i<bookmark_full.size(); i++)
        {
            JSONObject single_book_art = bookmark_full.get(i);
            try {
                mtitles.add(single_book_art.getString("title"));
                mimageURLs.add(single_book_art.getString("imageURL"));
                mdates.add(single_book_art.getString("date"));
                msections.add(single_book_art.getString("section"));
                mids.add(single_book_art.getString("id"));
                mURLS.add(single_book_art.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        fetched_data = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mAdapter != null){
            mtitles.clear();
            mimageURLs.clear();
            mdates.clear();
            msections.clear();
            mids.clear();
            mURLS.clear();
            bmark_parse_JSON();
            mAdapter.notifyDataSetChanged();
        }
    }

}

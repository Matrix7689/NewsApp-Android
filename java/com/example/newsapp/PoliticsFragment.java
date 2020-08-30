package com.example.newsapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class PoliticsFragment extends Fragment {

    private ArrayList<String> mtitles = new ArrayList<>();
    private ArrayList<String> mimageURLs = new ArrayList<>();
    private ArrayList<String> mdates = new ArrayList<>();
    private ArrayList<String> msections = new ArrayList<>();
    private ArrayList<String> mids = new ArrayList<>();
    private ArrayList<String> mURLS = new ArrayList<>();
    private HomeCardAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RequestQueue mRequestQueue;
    private Boolean fetched_data;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_politics, container, false);
        fetched_data = false;
        mRecyclerView = v.findViewById(R.id.politics_cards);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRequestQueue = Volley.newRequestQueue(getContext());
        mURLS.clear();
        mids.clear();
        mtitles.clear();
        mimageURLs.clear();
        mdates.clear();
        msections.clear();
        politics_parse_JSON();
        final Handler handler = new Handler();
        Runnable procRun = new Runnable() {
            @Override
            public void run() {
                if(fetched_data){
                    mAdapter = new HomeCardAdapter(mURLS, mids, mtitles, mimageURLs, mdates, msections, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    v.findViewById(R.id.progressBar_politics).setVisibility(View.GONE);
                    v.findViewById(R.id.progressText_politics).setVisibility(View.GONE);
                } else{
                    handler.postDelayed(this,100);
                }
            }
        };
        handler.post(procRun);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_politics);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                // Your code to make your refresh action
                fetched_data = false;
                mURLS.clear();
                mids.clear();
                mtitles.clear();
                mimageURLs.clear();
                mdates.clear();
                msections.clear();
                politics_parse_JSON();
                final Handler handler = new Handler();
                Runnable procRun = new Runnable() {
                    @Override
                    public void run() {
                        if(fetched_data){
                            mAdapter = new HomeCardAdapter(mURLS, mids, mtitles, mimageURLs, mdates, msections, getContext());
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setNestedScrollingEnabled(false);
                        } else{
                            handler.postDelayed(this,100);
                        }
                    }
                };
                handler.post(procRun);

                final Handler refreshhandler = new Handler();
                refreshhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        return v;
    }

    private void politics_parse_JSON(){
        //String url = "http://jmparekhhw8.us-east-1.elasticbeanstalk.com/guardian/politics";
        String url = "https://hw9backend-275518.ue.r.appspot.com/guardian/politics";

        fetched_data = false;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = response.getJSONObject("response");
                    JSONArray jresults_arr = jObj.getJSONArray("results");
                    for(int i=0; i< jresults_arr.length(); i++)
                    {
                        JSONObject one_result = jresults_arr.getJSONObject(i);
                        String one_imageURL = "", one_title = "", one_date = "", one_section = "", one_id = "";
                        try {
                            one_title = one_result.getString("webTitle");
                        } catch(JSONException myerr)
                        {
                            one_title = "No title";
                        }
                        try {
                            one_id = one_result.getString("id");
                        }catch(JSONException myerr){
                            one_id = "noid";
                        }
                        try {
                            one_date = one_result.getString("webPublicationDate");
                        }catch(JSONException myerr){
                            one_date ="No date";
                        }
                        try {
                            one_section = one_result.getString("sectionName");
                        }catch(JSONException myerr){
                            one_section = "No section";
                        }

                        try {
                            JSONObject blocks_obj = one_result.getJSONObject("blocks");
                            JSONObject main_obj = blocks_obj.getJSONObject("main");
                            JSONArray elements_arr = main_obj.getJSONArray("elements");
                            JSONObject first_ele = elements_arr.getJSONObject(0);
                            JSONArray assets_arr = first_ele.getJSONArray("assets");
                            JSONObject assets_first = assets_arr.getJSONObject(0);

                            one_imageURL = assets_first.getString("file");
                        } catch(JSONException myerr)
                        {
                            one_imageURL = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }

                        ZonedDateTime one_de = ZonedDateTime.parse(one_date).withZoneSameLocal(ZoneId.of("GMT"));
                        one_date = one_de.withZoneSameInstant(ZoneId.of("America/Los_Angeles")).toString();

                        mids.add(one_id);
                        mURLS.add(one_result.getString("webUrl"));
                        mtitles.add(one_title);
                        mimageURLs.add(one_imageURL);
                        mdates.add(one_date);
                        msections.add(one_section);
                        fetched_data = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
    }

    public void onResume()
    {
        super.onResume();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

}

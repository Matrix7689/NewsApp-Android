package com.example.newsapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class HomeFragment extends Fragment {

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
    private Boolean fetched_weather_data;
    private String weather_temperature_value = "14 \u00B0C";
    private String weather_type_value = "Clouds";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    String city_weather;
    String state_weather;
    public static Fragment newInstance(String city, String state) {
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        bundle.putString("state", state);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            city_weather = bundle.getString("city");
            state_weather = bundle.getString("state");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        readBundle(getArguments());
        //initTitles();
        mRequestQueue = Volley.newRequestQueue(getContext());
        fetched_weather_data = false;
        final TextView city =  v.findViewById(R.id.city_weather);
        final TextView we_state = v.findViewById(R.id.state_weather);
        final TextView we_temp =  v.findViewById(R.id.temperature_weather);
        final TextView we_type = v.findViewById(R.id.type_weather);
        final RelativeLayout weather_background = v.findViewById(R.id.rel_weather);
        city.setText(city_weather);
        we_state.setText(state_weather);
        weather_parse_JSON();
        final Handler whandler = new Handler();
        Runnable wprocRun = new Runnable() {
            @Override
            public void run() {
                if(fetched_weather_data){

                    switch(weather_type_value){
                        case "Clouds": weather_background.setBackgroundResource(R.drawable.cloudy_weather); break;
                        case "Clear": weather_background.setBackgroundResource(R.drawable.clear_weather); break;
                        case "Snow": weather_background.setBackgroundResource(R.drawable.snowy_weather); break;
                        case "Rain":
                        case "Drizzle":
                            weather_background.setBackgroundResource(R.drawable.rainy_weather); break;
                        case "Thunderstorm": weather_background.setBackgroundResource(R.drawable.thunder_weather); break;
                        default: weather_background.setBackgroundResource(R.drawable.sunny_weather); break;
                    }
                    we_temp.setText(weather_temperature_value);
                    we_type.setText(weather_type_value);
                } else{
                    whandler.postDelayed(this,100);
                }
            }
        };
        whandler.post(wprocRun);

        fetched_data = false;
        mRecyclerView = v.findViewById(R.id.home_cards);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        home_parse_JSON();
        final Handler handler = new Handler();
        Runnable procRun = new Runnable() {
            @Override
            public void run() {
                if(fetched_data){
                    mAdapter = new HomeCardAdapter(mURLS, mids, mtitles, mimageURLs, mdates, msections, getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setNestedScrollingEnabled(false);
                    v.findViewById(R.id.progressBar_home).setVisibility(View.GONE);
                    v.findViewById(R.id.progressText_home).setVisibility(View.GONE);
                    //mAdapter.setOnItemClickListener(HomeFragment.this);
                } else{
                    handler.postDelayed(this,100);
                }
            }
        };
        handler.post(procRun);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh_home);
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
                home_parse_JSON();
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

    private void weather_parse_JSON(){
        String weather_url = "https://api.openweathermap.org/data/2.5/weather?q="+city_weather+"&units=metric&appid=<INSERT_APIKEY>";

        fetched_weather_data = false;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, weather_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_Obj = response.getJSONObject("main");
                    double temp_value = main_Obj.getDouble("temp");

                    int temp_intvalue = (int) Math.round(temp_value);
                    weather_temperature_value = temp_intvalue + " \u00B0C";
                    //System.out.println("got temp "+weather_temperature_value);
                    JSONArray weather_arr = response.getJSONArray("weather");
                    JSONObject first_weather = weather_arr.getJSONObject(0);
                    weather_type_value = first_weather.getString("main");
                    fetched_weather_data = true;
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

    private void home_parse_JSON(){
        String url = "https://hw9backend-275518.ue.r.appspot.com/guardian/home";

        fetched_data = false;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("im here");
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
                            one_id = "No id";
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
                            JSONObject fields_obj = one_result.getJSONObject("fields");
                            one_imageURL = fields_obj.getString("thumbnail");
                        } catch(JSONException myerr)
                        {
                            one_imageURL = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }

                        ZonedDateTime one_de = ZonedDateTime.parse(one_date).withZoneSameLocal(ZoneId.of("GMT"));
                        one_date = one_de.withZoneSameInstant(ZoneId.of("America/Los_Angeles")).toString();

                        mtitles.add(one_title);
                        mimageURLs.add(one_imageURL);
                        mdates.add(one_date);
                        msections.add(one_section);
                        mids.add(one_id);
                        mURLS.add(one_result.getString("webUrl"));
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
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
}

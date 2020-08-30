package com.example.newsapp;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {
    public static LineChart lineChart;
    public static LineData data;
    public static Description description;
    public static ArrayList<Entry> finalData = new ArrayList<Entry>();
    public static Context trend_context;
    String keyword = "CoronaVirus";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trending, container, false);
        TrendingFragment.lineChart = v.findViewById(R.id.trend_chart);
        trend_context = getContext();
        final EditText inputtext = v.findViewById(R.id.trend_keyword);
        inputtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    TrendingFetchData process = new TrendingFetchData(trend_context,inputtext.getText().toString());
                    process.execute();
                    return true;
                }
                return false;
            }
        });

        TrendingFetchData process = new TrendingFetchData(getContext(), keyword);
        process.execute();

        return v;
    }
}

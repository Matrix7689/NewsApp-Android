package com.example.newsapp;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TrendingFetchData extends AsyncTask<Void,Void,ArrayList<Entry>> {

    String data = "", keyword;
    Context tf_cont;
    ArrayList<Entry> datavals = new ArrayList<Entry>();

    public TrendingFetchData(Context c, String k) {
        tf_cont = c;
        keyword = k;
    }

    @Override
    protected ArrayList<Entry> doInBackground(Void... voids) {
        try {
            String fetchline = "";
            String api_url = "https://hw9backend-275518.ue.r.appspot.com/guardian/trending?keyword=" + keyword;
            URL url = new URL(api_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while(fetchline != null)
            {
                fetchline = bufferedReader.readLine();
                data += fetchline;
            }

            JSONObject jObj = new JSONObject(data);
            JSONObject defObj = new JSONObject(jObj.get("default").toString());
            JSONArray timelineData = new JSONArray(defObj.get("timelineData").toString());
            for (int i = 0; i<timelineData.length(); i++)
            {
                JSONObject one_valueObj = (JSONObject) timelineData.get(i);
                JSONArray datavalue = (JSONArray) one_valueObj.get("value");
                datavals.add(new Entry(i, Integer.parseInt(datavalue.get(0).toString())));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datavals;
    }

    @Override
    protected void onPostExecute(ArrayList<Entry> datavals) {
        super.onPostExecute(datavals);

        TrendingFragment.finalData = datavals;
        TrendingFragment.lineChart = TrendingFragment.lineChart.findViewById(R.id.trend_chart);
        Legend legend = TrendingFragment.lineChart.getLegend();
        LineDataSet lineDataSet = new LineDataSet(datavals, "Trending Chart for " + keyword);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        TrendingFragment.data = new LineData(dataSets);
        TrendingFragment.description = new Description();
        TrendingFragment.description.setText("Description Label");
        TrendingFragment.lineChart.setDescription(TrendingFragment.description);
        lineDataSet.setDrawCircleHole(false);
        TrendingFragment.lineChart.setTouchEnabled(false);
        TrendingFragment.lineChart.getXAxis().setDrawGridLines(false);
        TrendingFragment.lineChart.getAxisLeft().setDrawGridLines(false);
        TrendingFragment.lineChart.getAxisRight().setDrawGridLines(false);
        lineDataSet.setColor(Color.parseColor("#6200EE"));
        lineDataSet.setCircleColor(Color.parseColor("#6200EE"));
        lineDataSet.setValueTextColor(Color.parseColor("#6200EE"));
        TrendingFragment.lineChart.setBackgroundColor(Color.WHITE);
        legend.setTextColor(Color.BLACK);
        TrendingFragment.lineChart.setDrawGridBackground(false);
        legend.setTextSize(15);
        TrendingFragment.lineChart.setData(TrendingFragment.data);
        TrendingFragment.lineChart.invalidate();
    }
}
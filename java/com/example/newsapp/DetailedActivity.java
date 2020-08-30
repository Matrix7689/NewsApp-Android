package com.example.newsapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.newsapp.HomeCardAdapter.Extra_ID;

public class DetailedActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private RequestQueue mRequestQueue;
    private String article_id = "";
    private String article_title = "";
    private String article_date = "";
    private String article_section = "";
    private String article_desc = "";
    private String article_url = "";
    private String article_imgurl = "";
    private Boolean fetched_data;
    private Context ct;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Intent intent = getIntent();

        myToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ct = this;
        mRequestQueue = Volley.newRequestQueue(this);
        article_id = intent.getStringExtra(Extra_ID);
        final TextView art_title = findViewById(R.id.detail_title);
        final TextView art_date = findViewById(R.id.detail_date);
        final TextView art_desc = findViewById(R.id.detail_desc);
        final TextView art_sec = findViewById(R.id.detail_section);
        final ImageView art_img = findViewById(R.id.detail_image);
        final ImageView twit_click = findViewById(R.id.detail_twitter);
        final ImageView bmark_icon = findViewById(R.id.detail_bmark);
        final TextView view_full_tit = findViewById(R.id.detail_view_full_art);
        fetched_data = false;
        detail_parse_JSON();
        final Handler handler = new Handler();
        Runnable procRun = new Runnable() {
            private Object Context;

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                if(fetched_data){
                    Picasso.with((android.content.Context) Context).load(article_imgurl).fit().centerInside().into(art_img);
                    art_title.setText(article_title);
                    art_date.setText(converted_detail_date(article_date));
                    art_desc.setText(Html.fromHtml(article_desc, Html.FROM_HTML_MODE_LEGACY));
                    art_sec.setText(article_section);
                    twit_click.setImageResource(R.drawable.bluetwitter);
                    bmark_icon.setImageResource(R.drawable.ic_bookmark_border_24px);
                    getSupportActionBar().setTitle(article_title);

                    if(isArticleBookmarked()) {
                        bmark_icon.setImageResource(R.drawable.ic_bookmark_24px);
                    }
                    else{
                        bmark_icon.setImageResource(R.drawable.ic_bookmark_border_24px);
                    }
                    
                    bmark_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isArticleBookmarked() != true) {
                                bmark_icon.setImageResource(R.drawable.ic_bookmark_24px);
                                saveBmarkdata();
                            }
                            else{
                                bmark_icon.setImageResource(R.drawable.ic_bookmark_border_24px);
                                String toast_msg_str = "\"" + article_title + "\"" + " was removed from bookmarks";
                                Toast.makeText(ct,toast_msg_str, Toast.LENGTH_SHORT).show();
                                removeBmarkdata(article_id);
                            }
                        }
                    });

                    view_full_tit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(article_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });

                    twit_click.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String full_url = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:%20" + article_url + "&hashtags=CSCI571NewsSearch";
                            Uri uri = Uri.parse(full_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });

                    findViewById(R.id.progressBar_detail).setVisibility(View.GONE);
                    findViewById(R.id.progressText_detail).setVisibility(View.GONE);
                    findViewById(R.id.detail_view_full_art).setVisibility(View.VISIBLE);
                    findViewById(R.id.detailed_card).setVisibility(View.VISIBLE);

                } else{
                    handler.postDelayed(this,100);
                }
            }
        };
        handler.post(procRun);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String converted_detail_date(String inputStr){
        String res_str = inputStr.substring(8,10) + " ";
        int month_val = Integer.parseInt(inputStr.substring(5,7));
        String month = "";
        switch(month_val){
            case 1: month = "Jan"; break;
            case 2: month = "Feb"; break;
            case 3: month = "Mar"; break;
            case 4: month = "Apr"; break;
            case 5: month = "May"; break;
            case 6: month = "Jun"; break;
            case 7: month = "Jul"; break;
            case 8: month = "Aug"; break;
            case 9: month = "Sep"; break;
            case 10: month = "Oct"; break;
            case 11: month = "Nov"; break;
            case 12: month = "Dec"; break;
            default: month = "May"; break;
        }
        res_str += month + " " + inputStr.substring(0,4);
        return res_str;
    }

    private void detail_parse_JSON(){
        String url = "https://hw9backend-275518.ue.r.appspot.com/guardian/article?id="+article_id;

        fetched_data = false;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = response.getJSONObject("response");
                    JSONObject contentObj = jObj.getJSONObject("content");
                    article_title = contentObj.getString("webTitle");
                    article_section = contentObj.getString("sectionName");
                    article_date = contentObj.getString("webPublicationDate");
                    article_url = contentObj.getString("webUrl");
                    ZonedDateTime one_de = ZonedDateTime.parse(article_date).withZoneSameLocal(ZoneId.of("GMT"));
                    article_date = one_de.withZoneSameInstant(ZoneId.of("America/Los_Angeles")).toString();
                    JSONObject blocksObj = contentObj.getJSONObject("blocks");
                    JSONArray bodyArr = blocksObj.getJSONArray("body");
                    for(int i=0; i<bodyArr.length(); i++)
                    {
                        JSONObject bodytemp = bodyArr.getJSONObject(i);
                        article_desc += bodytemp.getString("bodyHtml");
                    }

                    try {
                        JSONObject mainObj = blocksObj.getJSONObject("main");
                        JSONArray elemArr = mainObj.getJSONArray("elements");
                        JSONObject firstele = elemArr.getJSONObject(0);
                        JSONArray assetArr = firstele.getJSONArray("assets");
                        JSONObject firstAss = assetArr.getJSONObject(0);
                        article_imgurl = firstAss.getString("file");
                    }catch(JSONException myerr){
                        article_imgurl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                    }

                    fetched_data = true;
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

    private boolean isArticleBookmarked() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gsonObj = new Gson();
        String jsonObj = sharedPreferences.getString("bookmarks_array", null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> bookmark_full = null;
        bookmark_full = gsonObj.fromJson(jsonObj,type);
        if(bookmark_full == null)
        {
            bookmark_full = new ArrayList<>();
        }
        for(int i=0; i<bookmark_full.size(); i++)
        {
            JSONObject single_book_art = bookmark_full.get(i);
            try {
                if(single_book_art.getString("id").equals(article_id))
                {
                    bookmark_full.remove(single_book_art);
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void saveBmarkdata()
    {
        String toast_msg_str = "\"" + article_title + "\"" + " was added to bookmarks";
        Toast.makeText(this,toast_msg_str, Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gsonObj = new Gson();
        ArrayList<JSONObject> bookmark_full;

        String jsonObj = sharedPreferences.getString("bookmarks_array", null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        bookmark_full = gsonObj.fromJson(jsonObj,type);

        if(bookmark_full == null)
        {
            bookmark_full = new ArrayList<>();
        }

        JSONObject book_article = new JSONObject();
        try{
            book_article.put("url",article_url);
            book_article.put("id",article_id);
            book_article.put("section",article_section);
            book_article.put("date",article_date);
            book_article.put("imageURL",article_imgurl);
            book_article.put("title",article_title);
            bookmark_full.add(book_article);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonObj_final = gsonObj.toJson(bookmark_full);
        editor.putString("bookmarks_array",jsonObj_final);
        editor.apply();
    }

    private void removeBmarkdata(String inputID)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gsonObj = new Gson();
        String jsonObj = sharedPreferences.getString("bookmarks_array", null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> bookmark_full = null;
        bookmark_full = gsonObj.fromJson(jsonObj,type);
        for(int i=0; i<bookmark_full.size(); i++)
        {
            JSONObject single_book_art = bookmark_full.get(i);
            try {
                if(single_book_art.getString("id").equals(inputID))
                {
                    bookmark_full.remove(single_book_art);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("bookmarks_array");
        String updatedJson = gsonObj.toJson(bookmark_full);
        editor.putString("bookmarks_array", updatedJson);
        editor.apply();
    }
}

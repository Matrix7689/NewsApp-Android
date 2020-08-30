package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder>{
    private ArrayList<String> guard_titles = new ArrayList<>();
    private ArrayList<String> guard_imageURLs = new ArrayList<>();
    private ArrayList<String> guard_date = new ArrayList<>();
    private ArrayList<String> guard_section = new ArrayList<>();
    private ArrayList<String> guard_ids = new ArrayList<>();
    private ArrayList<String> guard_urls = new ArrayList<>();
    private Context guard_context;
    public static final String Extra_ID = "id";


    public HomeCardAdapter(ArrayList<String> guard_urls, ArrayList<String> guard_ids, ArrayList<String> guard_titles, ArrayList<String> guard_imageURLs, ArrayList<String> guard_date, ArrayList<String> guard_section,Context guard_context) {
        this.guard_titles = guard_titles;
        this.guard_imageURLs = guard_imageURLs;
        this.guard_date = guard_date;
        this.guard_section = guard_section;
        this.guard_context = guard_context;
        this.guard_ids = guard_ids;
        this.guard_urls = guard_urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_card, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit){
        return unit.between(d1, d2);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(guard_titles.get(position));

        String one_date = guard_date.get(position);

        ZoneId z = ZoneId.of("America/Los_Angeles");
        ZonedDateTime current_time = ZonedDateTime.now(z);
        long calc_time = zonedDateTimeDifference(ZonedDateTime.parse(one_date), current_time , ChronoUnit.SECONDS);
        if (calc_time > 86400) {
            one_date = (calc_time/86400) + "d ago";
        } else if (calc_time > 3600) {
            one_date = (calc_time/3600) + "h ago";
        } else if (calc_time > 60) {
            one_date = (calc_time/60) + "m ago";
        } else {
            one_date = (calc_time) + "s ago";
        }
        holder.date.setText(one_date);

        holder.section.setText(guard_section.get(position));
        Picasso.with(guard_context).load(guard_imageURLs.get(position)).fit().centerInside().into(holder.imageURL);
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dial = new Dialog(guard_context);
                dial.setContentView(R.layout.layout_home_card_dialog);
                TextView title_dialog = dial.findViewById(R.id.diag_title);
                title_dialog.setText(guard_titles.get(position));
                ImageView image_dialog = dial.findViewById(R.id.diag_img);

                final ImageView image_bmark = dial.findViewById(R.id.diag_bmark);
                if (isArticleBookmarked(guard_ids.get(position))) {
                    image_bmark.setImageResource(R.drawable.ic_bookmark_24px);
                }
                else{
                    image_bmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                }

                Picasso.with(guard_context).load(guard_imageURLs.get(position)).fit().centerInside().into(image_dialog);
                ImageView twit_dialog = dial.findViewById(R.id.diag_twitter);
                twit_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String guard_url = guard_urls.get(position);
                        String full_url = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:%20" + guard_url + "&hashtags=CSCI571NewsSearch";
                        Uri uri = Uri.parse(full_url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        guard_context.startActivity(intent);
                    }
                });

                image_bmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isArticleBookmarked(guard_ids.get(position)) != true) {
                            holder.bmark.setImageResource(R.drawable.ic_bookmark_24px);
                            image_bmark.setImageResource(R.drawable.ic_bookmark_24px);
                            saveBmarkdata(position);
                        }
                        else{
                            holder.bmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                            image_bmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                            String toast_msg_str = "\"" + guard_titles.get(position) + "\"" + " was removed from bookmarks";
                            Toast.makeText(guard_context,toast_msg_str, Toast.LENGTH_SHORT).show();
                            removeBmarkdata(guard_ids.get(position));
                        }
                    }
                });
                dial.show();
                return false;
            }
        });
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(guard_context, DetailedActivity.class);
                String clickedItem = guard_ids.get(position);
                detailIntent.putExtra(Extra_ID, clickedItem);
                guard_context.startActivity(detailIntent);
            }
        });

        if (isArticleBookmarked(guard_ids.get(position))) {
            holder.bmark.setImageResource(R.drawable.ic_bookmark_24px);
        }
        else{
            holder.bmark.setImageResource(R.drawable.ic_bookmark_border_24px);
        }

        holder.bmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isArticleBookmarked(guard_ids.get(position)) != true) {
                    holder.bmark.setImageResource(R.drawable.ic_bookmark_24px);
                    saveBmarkdata(position);
                }
                else{
                    holder.bmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                    String toast_msg_str = "\"" + guard_titles.get(position) + "\"" + " was removed from bookmarks";
                    Toast.makeText(guard_context,toast_msg_str, Toast.LENGTH_SHORT).show();
                    removeBmarkdata(guard_ids.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return guard_titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView section;
        ImageView imageURL;
        ImageView bmark;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            date = itemView.findViewById(R.id.card_date);
            section = itemView.findViewById(R.id.card_section);
            imageURL = itemView.findViewById(R.id.card_image);
            parentLayout = itemView.findViewById(R.id.parent_home_card);
            bmark = itemView.findViewById(R.id.card_bmark);
        }
    }

    private boolean isArticleBookmarked(String inputID) {
        SharedPreferences sharedPreferences = guard_context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
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
                if(single_book_art.getString("id").equals(inputID))
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

    private void saveBmarkdata(int position)
    {
        String toast_msg_str = "\"" + guard_titles.get(position) + "\"" + " was added to bookmarks";
        Toast.makeText(guard_context,toast_msg_str, Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = guard_context.getSharedPreferences("shared preferences",Context.MODE_PRIVATE);
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
            book_article.put("url",guard_urls.get(position));
            book_article.put("id",guard_ids.get(position));
            book_article.put("section",guard_section.get(position));
            book_article.put("date",guard_date.get(position));
            book_article.put("imageURL",guard_imageURLs.get(position));
            book_article.put("title",guard_titles.get(position));
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
        SharedPreferences sharedPreferences = guard_context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
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

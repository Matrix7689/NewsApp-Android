package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarkCardAdapter extends RecyclerView.Adapter<BookmarkCardAdapter.ViewHolder>{
    private ArrayList<String> guard_titles = new ArrayList<>();
    private ArrayList<String> guard_imageURLs = new ArrayList<>();
    private ArrayList<String> guard_date = new ArrayList<>();
    private ArrayList<String> guard_section = new ArrayList<>();
    private ArrayList<String> guard_ids = new ArrayList<>();
    private ArrayList<String> guard_urls = new ArrayList<>();
    private Context guard_context;

    public static final String Extra_ID = "id";


    public BookmarkCardAdapter(ArrayList<String> guard_urls, ArrayList<String> guard_ids, ArrayList<String> guard_titles, ArrayList<String> guard_imageURLs, ArrayList<String> guard_date, ArrayList<String> guard_section, Context guard_context) {
        this.guard_titles = guard_titles;
        this.guard_imageURLs = guard_imageURLs;
        this.guard_date = guard_date;
        this.guard_section = guard_section;
        this.guard_context = guard_context;
        this.guard_ids = guard_ids;
        this.guard_urls = guard_urls;
    }

    public String convert_bmark_date(String inputStr){
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
        res_str += month;
        return res_str;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bmark_card, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(guard_titles.get(position));
        holder.date.setText(convert_bmark_date(guard_date.get(position)));
        holder.section.setText(guard_section.get(position));
        //holder.section.setText("WOOOOOOOOOOOOOOOOW");
        Picasso.with(guard_context).load(guard_imageURLs.get(position)).fit().centerInside().into(holder.imageURL);
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dial = new Dialog(guard_context);
                dial.setContentView(R.layout.layout_home_card_dialog);
                TextView title_dialog = dial.findViewById(R.id.diag_title);
                title_dialog.setText(guard_titles.get(position));
                ImageView image_dialog = dial.findViewById(R.id.diag_img);
                Picasso.with(guard_context).load(guard_imageURLs.get(position)).fit().centerInside().into(image_dialog);
                ImageView twit_dialog = dial.findViewById(R.id.diag_twitter);
                final ImageView bmark_dialog = dial.findViewById(R.id.diag_bmark);
                bmark_dialog.setImageResource(R.drawable.ic_bookmark_24px);


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

                bmark_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            bmark_dialog.setImageResource(R.drawable.ic_bookmark_border_24px);
                            String toast_msg_str = "\"" + guard_titles.get(position) + "\"" + " was removed from bookmarks";
                            Toast.makeText(guard_context,toast_msg_str, Toast.LENGTH_SHORT).show();
                            removeBmarkdata(guard_ids.get(position));
                            guard_titles.remove(position);
                            guard_imageURLs.remove(position);
                            guard_section.remove(position);
                            guard_date.remove(position);
                            guard_ids.remove(position);
                            guard_urls.remove(position);
                            notifyDataSetChanged();
                            BookmarksFragment.final_delete_article();
                            dial.dismiss();
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
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bmark_card_title);
            date = itemView.findViewById(R.id.bmark_card_date);
            section = itemView.findViewById(R.id.bmark_card_section);
            imageURL = itemView.findViewById(R.id.bmark_card_img);
            parentLayout = itemView.findViewById(R.id.parent_bmark_card);
            ImageView bmark = itemView.findViewById(R.id.bmark_bmark);
            bmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toast_msg_str = "\"" + guard_titles.get(getAdapterPosition()) + "\"" + " was removed from bookmarks";
                    Toast.makeText(guard_context,toast_msg_str, Toast.LENGTH_SHORT).show();
                    removeBmarkdata(guard_ids.get(getAdapterPosition()));
                    guard_titles.remove(getAdapterPosition());
                    guard_imageURLs.remove(getAdapterPosition());
                    guard_section.remove(getAdapterPosition());
                    guard_date.remove(getAdapterPosition());
                    guard_ids.remove(getAdapterPosition());
                    guard_urls.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    BookmarksFragment.final_delete_article();
                }
            });

        }
    }

    private void removeBmarkdata(String inputID) {
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

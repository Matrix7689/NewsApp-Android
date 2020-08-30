package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final String Extra_query = "";
    LocationManager locationManager;
    String provider;
    String cityName = "Los Angeles";
    String stateName = "California";
    ArrayList<String> suggestedKeywords;
    JSONArray suggestionsData;
    private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bNav = findViewById(R.id.bottom_nav);
        bNav.setOnNavigationItemSelectedListener(navListener);
        requestLocationPermission();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            //System.out.println("inside if of perm denied");
            //return;
        }

        else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //System.out.println("inside if of perm accepted");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            Location location = locationManager.getLastKnownLocation(provider);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                cityName = addresses.get(0).getLocality();
                stateName = addresses.get(0).getAdminArea();
                //System.out.println("test city" + stateName);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
            //System.out.println("test city" + stateName);

            suggestedKeywords = new ArrayList<String>();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    HomeFragment.newInstance(cityName,stateName)).commit();

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId())
                    {
                        case R.id.nav_home:
                            /*selectedFragment = new HomeFragment();*/
                            selectedFragment = HomeFragment.newInstance(cityName,stateName);
                            break;
                        case R.id.nav_headlines:
                            selectedFragment = new HeadlinesFragment();
                            break;
                        case R.id.nav_trending:
                            selectedFragment = new TrendingFragment();
                            break;
                        case R.id.nav_bookmark:
                            selectedFragment = new BookmarksFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    HomeFragment.newInstance(cityName,stateName)).commit();
            //Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
        }

        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            System.out.println("inside if of onresume  perm denied");
            //return;
        }
        if(provider == null)
            return;
        //else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //if(provider!=null) {
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //if(loc_flag == true){
                locationManager.requestLocationUpdates(provider, 400, 1, this);
            //}
        //}
            //}
        //}

    }
    @Override
    protected void onPause() {
        super.onPause();
        //System.out.println("inside onPause");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            //return;
        }
        locationManager.removeUpdates(this);
    }
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchMenuItem.getActionView();
        final androidx.appcompat.widget.SearchView.SearchAutoComplete suggestions = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        //searchView.setBackgroundResource(R.drawable.ic_search_24px);
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viz, int posi, long id) {
                //Intent send_intent = new Intent(viz.getContext(), SearchActivity.class);
                String search_keyw = (String) parent.getItemAtPosition(posi);
                searchView.setQuery(search_keyw, false);
                //send_intent.putExtra(Extra_query, search_keyw);
                //startActivity(send_intent);
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent send_intent = new Intent(searchView.getContext(), SearchActivity.class);
                send_intent.putExtra(Extra_query, query);
                startActivity(send_intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() < 3) {
                    ArrayAdapter<String> newsempty = null;
                    suggestions.setAdapter(newsempty);
                    return true;
                }
                getSuggestionList(newText, suggestions);
                return true;
            }
        });
        return true;
    }


    private void getSuggestionList(String query, final androidx.appcompat.widget.SearchView.SearchAutoComplete suggestions) {
        String url = "https://<INSERT_BING_COGNITIVE_LINK>/suggestions?q="+query;
        RequestQueue bingReqQueue = Volley.newRequestQueue(getBaseContext());
        JsonObjectRequest bingJsonReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    suggestionsData = response.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
                    String[] suggested = new String[5];
                    int limit = Math.min(suggestionsData.length(), 5);
                    for (int i = 0; i<limit; i++) {
                        try {
                            suggested[i] = suggestionsData.getJSONObject(i).getString("displayText");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, suggested);
                    suggestions.setAdapter(newsAdapter);
                    suggestions.showDropDown();
                } catch (JSONException err) {
                    err.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map <String, String> bingKey = new HashMap<>();
                bingKey.put("Ocp-Apim-Subscription-Key", "<INSERT_BING_SUBSCRIPTION_KEY>");
                return bingKey;
            }
        };
        bingReqQueue.add(bingJsonReq);
    }
}

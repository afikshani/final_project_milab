package com.example.afikshani.milab_final;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.afikshani.milab_final.fragments.FirstOptionFragment;
import com.example.afikshani.milab_final.fragments.SecondOptionFragment;
import com.example.afikshani.milab_final.fragments.ThirdOptionFragment;
import com.example.afikshani.milab_final.helpers.DirectionsJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CoordinatorLayout coordinatorLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private GoogleMap mMap;
    private TabLayout tabLayout;
    private LinearLayout tabsContainer;
    private Button routeInfoContextButton;
    private Button mapContextButton;

    private String destination;
    private String destinationLat;
    private String destinationLong;
    private LatLng destinationLocation;
    private String origin;
    private String originLat;
    private String originLong;

    private Intent searchingDialog;
    private int INTENT_SIGNAL = 555;

    private PopupWindow mPopupWindow;

    private static int COLOR_GREEN = Color.parseColor("#1de9b6");
    private static int COLOR_YELLOW = Color.parseColor("#ffee58");
    private static int COLOR_RED = Color.parseColor("#f44336");
    private static int COLOR_WHITE = Color.parseColor("#fafafa");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        searchingDialog = new Intent(getApplicationContext(), PopSearchActivity.class);
        startActivityForResult(searchingDialog, INTENT_SIGNAL);

        originLat = getIntent().getStringExtra("originLat");
        originLong = getIntent().getStringExtra("originLong");
        origin = originLat + "," + originLong;
        destination = getIntent().getStringExtra("destination");
        destinationLat = getIntent().getStringExtra("destinationLat");
        destinationLong = getIntent().getStringExtra("destinationLong");

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        setToolBarButtons();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabsContainer = (LinearLayout) tabLayout.getChildAt(0);


    }

    private void setToolBarButtons() {

        mapContextButton = (Button) findViewById(R.id.toolbar_map_context);
        routeInfoContextButton = (Button) findViewById(R.id.toolbar_routeInfo_context);

        mapContextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_right_gray));
                routeInfoContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_left_white));
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });

        routeInfoContextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_right_white));
                routeInfoContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_left_gray));

                if (mPopupWindow == null) {
                    LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.custom_layout_route_info, null);
                    setAlertsRouteInformation(customView);
                    mPopupWindow = new PopupWindow(customView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
                    if (Build.VERSION.SDK_INT >= 21) {
                        mPopupWindow.setElevation(5.0f);
                    }
                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPopupWindow.dismiss();
                            mPopupWindow = null;
                            mapContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_right_gray));
                            routeInfoContextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_toolbar_items_left_white));
                        }
                    });
                    mPopupWindow.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                }

            }

            private void setAlertsRouteInformation(View customView) {
                TextView alert = (TextView) customView.findViewById(R.id.alertsText);
                String text = "<font color=#212121>Some route alerts would be shown here,</font> \n" + "<font color=#212121>RIDE SAFE PLEASE!</font>";
                alert.setText(Html.fromHtml(text));
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in location and move the camera
        LatLng sourceLocation = new LatLng(Double.parseDouble(originLat), Double.parseDouble(originLong));
        destinationLocation = new LatLng(Double.parseDouble(destinationLat), Double.parseDouble(destinationLong));

        mMap.addMarker(new MarkerOptions().position(sourceLocation).title("You Are Here"));
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title(destination));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 16.0f));

        String routeRequest = getDirectionsUrl(origin, destination);

        RouteCalculatorAsync routeCalc = new RouteCalculatorAsync();

        routeCalc.execute(routeRequest);
    }

    private String getDirectionsUrl(String origin, String destination) {

        String url = "https://finalproject-aumqcixymm.now.sh/routes/" + origin + "/" + destination;
        return url;
    }

    public class RouteCalculatorAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String routeData = "";

            try {
                routeData = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return routeData;
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask routeParserFromJsonToObject = new ParserTask();

            routeParserFromJsonToObject.execute(result);

        }


        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... routeAsJson) {

                JSONObject jFullObject;
                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jFullObject = new JSONObject(routeAsJson[0]);

                    jObject = jFullObject.getJSONObject("json");

                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {

                int numOfRoutes = result.size();

                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), numOfRoutes);

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                ArrayList points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);
                    HashMap<String, String> colorJson = path.get(0);
                    int color = getColor(colorJson.get("color"));
                    HashMap<String, String> ratingJson = path.get(1);
                    String rating = ratingJson.get("rating");
                    double ratingAsDouble = Double.valueOf(rating);
                    int ratingAsInt = (int) ratingAsDouble;
                    tabLayout.addTab(tabLayout.newTab().setText(ratingAsInt + ""));


                    for (int j = 2; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        builder.include(position);
                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(color);
                    lineOptions.geodesic(true);

                    // Drawing polyline in the Google Map for the i-th route
                    mMap.addPolyline(lineOptions);

                }

                updateTabsColors();


                Thread waitingThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(3000);
                            finishActivity(INTENT_SIGNAL);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                waitingThread.start();

                // move the camera for zoom out to see the routes
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));


            }

            private void updateTabsColors() {
                LinearLayout tab = (LinearLayout) tabsContainer.getChildAt(0);
                tab.setBackgroundColor(COLOR_GREEN);
                setTextColor(tab, COLOR_WHITE);
                LinearLayout tabTwo = (LinearLayout) tabsContainer.getChildAt(1);
                if (tabTwo != null){
                    setTextColor(tabTwo, COLOR_YELLOW);
                }
                LinearLayout tabThree = (LinearLayout) tabsContainer.getChildAt(2);
                if (tabThree != null){
                    setTextColor(tabThree, COLOR_RED);
                }

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getPosition() == 0) {
                            LinearLayout firstTab = (LinearLayout) tabsContainer.getChildAt(0);
                            firstTab.setBackgroundColor(COLOR_GREEN);
                            setTextColor(firstTab, COLOR_WHITE);
                            resetOtherTabs(0);
                        } else if (tab.getPosition() == 1) {
                            LinearLayout middleTab = (LinearLayout) tabsContainer.getChildAt(1);
                            middleTab.setBackgroundColor(COLOR_YELLOW);
                            setTextColor(middleTab, COLOR_WHITE);
                            resetOtherTabs(1);
                        } else {
                            LinearLayout lastTab = (LinearLayout) tabsContainer.getChildAt(2);
                            lastTab.setBackgroundColor(COLOR_RED);
                            setTextColor(lastTab, COLOR_WHITE);
                            resetOtherTabs(2);
                        }

                    }

                    private void resetOtherTabs(int position) {
                        switch (position) {
                            case 0:
                                LinearLayout oneToReset = (LinearLayout) tabsContainer.getChildAt(1);
                                if (oneToReset != null) {
                                    oneToReset.setBackgroundColor(COLOR_WHITE);
                                    setTextColor(oneToReset, COLOR_YELLOW);
                                }
                                LinearLayout twoToReset = (LinearLayout) tabsContainer.getChildAt(2);
                                if (twoToReset != null) {
                                    twoToReset.setBackgroundColor(COLOR_WHITE);
                                    setTextColor(twoToReset, COLOR_RED);
                                }
                                break;

                            case 1:
                                LinearLayout one = (LinearLayout) tabsContainer.getChildAt(0);
                                one.setBackgroundColor(COLOR_WHITE);
                                setTextColor(one, COLOR_GREEN);
                                LinearLayout two = (LinearLayout) tabsContainer.getChildAt(2);
                                if (two != null) {
                                    two.setBackgroundColor(COLOR_WHITE);
                                    setTextColor(two, COLOR_RED);
                                }
                                break;

                            case 2:
                                LinearLayout oneReset = (LinearLayout) tabsContainer.getChildAt(0);
                                LinearLayout twoReset = (LinearLayout) tabsContainer.getChildAt(1);
                                oneReset.setBackgroundColor(COLOR_WHITE);
                                setTextColor(oneReset, COLOR_GREEN);
                                twoReset.setBackgroundColor(COLOR_WHITE);
                                setTextColor(twoReset, COLOR_YELLOW);

                                break;

                            default:
                                break;
                        }


                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


            }

            private void setTextColor(LinearLayout tab, int color) {
                TextView tv = (TextView) tab.getChildAt(1);
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setTextColor(color);
            }


            private int getColor(String color) {
                if (("GREEN").equals(color)) {
                    return COLOR_GREEN;
                } else if (("YELLOW").equals(color)) {
                    return COLOR_YELLOW;
                } else {
                    return COLOR_RED;
                }

            }

        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int numOfTabs;

        public SectionsPagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstOptionFragment();
                case 1:
                    return new SecondOptionFragment();
                case 2:
                    return new ThirdOptionFragment();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}

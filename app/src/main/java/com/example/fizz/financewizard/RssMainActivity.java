package com.example.fizz.financewizard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RssMainActivity extends AppCompatActivity {
    static final int DIALOG_ERROR_CONNECTION = 1;
    private SwipeRefreshLayout swipeContainer;

    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected String mActivityTitle;
    protected static int position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isOnline(this)) {
            setContentView(R.layout.rss_main);
            frameLayout = (FrameLayout)findViewById(R.id.fragment_container);
           // gv = (GridView)findViewById(R.id.grid_view);
            mDrawerList = (ListView)findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
            mActivityTitle = "Eco Feed";//string

            addDrawerItems();
            setupDrawer();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            showDialog(DIALOG_ERROR_CONNECTION); //display no connection dialog
        } else {
            if (savedInstanceState == null) {
                setContentView(R.layout.rss_main);
                frameLayout = (FrameLayout)findViewById(R.id.fragment_container);
                //gv = (GridView)findViewById(R.id.grid_view);
                mDrawerList = (ListView)findViewById(R.id.navList);
                mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                mActivityTitle = "Eco Feed";//string
                addDrawerItems();
                setupDrawer();

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);

            }

            addRssFragment();
        }
    }


    //To check internet connectivity
    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }

    //No internet connection dialog

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_ERROR_CONNECTION:
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
                errorDialog.setTitle("No internet connection");
                errorDialog.setMessage("Please try again later");
                errorDialog.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog errorAlert = errorDialog.create();
                return errorAlert;

            default:
                break;
        }
        return dialog;
    }

    // Initialize RSS Feed

    public void addRssFragment() {
        rss_fragment fragment = new rss_fragment(); //rss_fragment.java
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.fragment_container, fragment); //Loads frameLayout, rss_main will serve as the host for the fragment.
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragment_added", true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!isOnline(this)) {
            getMenuInflater().inflate(R.menu.rss_sub_main_nc,menu);
        }
        else getMenuInflater().inflate(R.menu.rss_sub_main, menu);
        return true;
    }

    void SetRSS()
    {
        final CharSequence sources[] = new CharSequence[] {"Times Of India - Finance (Default)","Personal Finance - Top Stories", "Forbes Business","Financial Services","FT - Finance","CNBC Business"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a feed");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sources[which] == "Personal Finance - Top Stories") {
                    //Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://feeds.marketwatch.com/marketwatch/pf/";
                    refresh_feed();
                } else if (sources[which] == "Times Of India - Finance (Default)") {
                    //Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://timesofindia.feedsportal.com/c/33039/f/533919/index.rss";
                    refresh_feed();
                } else if (sources[which] == "Forbes Business") {
                   // Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://www.forbes.com/business/feed/";
                    refresh_feed();
                } else if (sources[which] == "Financial Services") {
                    // Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://www.ft.com/rss/companies/financial-services";
                    refresh_feed();
                } else if (sources[which] == "FT - Finance") {
                    // Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://www.ft.com/rss/companies/financials";
                    refresh_feed();
                }else if (sources[which] == "CNBC Business") {
                    // Toast.makeText(getApplicationContext(), "Showing Finance News", Toast.LENGTH_LONG).show();
                    rss_service.RSS_LINK = "http://www.cnbc.com/id/10001147/device/rss/rss.html";
                    refresh_feed();
                }
                //http://www.cnbc.com/id/10001147/device/rss/rss.html
                //http://www.ft.com/rss/companies/financial-services
                //http://www.ft.com/rss/companies/financials
            }
        });
        builder.show();

    }

    public void refresh_feed()
    {
        setContentView(R.layout.rss_main);
        //gv = (GridView)findViewById(R.id.grid_view);
        frameLayout=(FrameLayout)findViewById(R.id.fragment_container);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Eco Feed";//string

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (isOnline(this))
            addRssFragment();
        else
        showDialog(DIALOG_ERROR_CONNECTION);
    }


    // Navigation Drawer Icons
    class myAdapter extends BaseAdapter {
        private Context context;
        String NavListCategories[];
        int[] images = {R.drawable.cash_flow,R.drawable.rss,R.drawable.goals_targets,R.drawable.trends,R.drawable.cam,R.drawable.map};


        public myAdapter(Context context){
            this.context = context;
            NavListCategories = context.getResources().getStringArray(R.array.NavigationDrawerList);

        }
        @Override
        public int getCount() {
            return NavListCategories.length;
        }

        @Override
        public Object getItem(int position) {
            return NavListCategories[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.custom_row, parent, false);
            }
            else{
                row =convertView;
            }

            TextView titleTextView =(TextView) row.findViewById(R.id.textViewRow1);
            ImageView titleImageView = (ImageView) row.findViewById(R.id.imageViewRow1);
            titleTextView.setText(NavListCategories[position]);
            titleImageView.setImageResource(images[position]);
            return row;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.change_feed) {
            SetRSS();
            return true;
        }
        if(id==R.id.refresh_rss)
        {
            refresh_feed();
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, settings_main.class));
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //NavigationDrawer Code Start
    private void addDrawerItems() {

        myAdapter MyAdapter = new myAdapter(this);
        mDrawerList.setAdapter(MyAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    protected void openActivity(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        RssMainActivity.position = position; //Setting currently selected position in this field so that it will be available in our child activities.
        switch (position) {
            case 0:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, RssMainActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, Goals_MainActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, Trends_MainActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, CamMainActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, MapsMainActivity.class));
            default:
                break;
        }

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }}

//NavigationDrawer Code Ends
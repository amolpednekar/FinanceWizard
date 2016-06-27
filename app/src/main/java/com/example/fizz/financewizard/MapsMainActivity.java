package com.example.fizz.financewizard;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *  This class is used to search places using Places API using keywords like BANKS, ATM.
 */
public class MapsMainActivity extends AppCompatActivity {
	//reupload
	//android.support.v7.app.ActionBar actionBar = getSupportActionBar();
	private final String TAG = getClass().getSimpleName();
	private GoogleMap mMap;
	private String[] places;
	private LocationManager locationManager;
	private Location loc;
	UiSettings mapSettings;

	protected ListView mDrawerList;
	protected DrawerLayout mDrawerLayout;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected static int position;
	protected String mActivityTitle;


	static final int DIALOG_ERROR_CONNECTION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isOnline(this)) {
			setContentView(R.layout.maps_activity_main); //Load blank map
			// gv = (GridView)findViewById(R.id.grid_view);
			mDrawerList = (ListView)findViewById(R.id.navList);
			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
			mActivityTitle = "Nearby Banks/ATMs";//string

			addDrawerItems();
			setupDrawer();

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			showDialog(DIALOG_ERROR_CONNECTION); //display no connection dialog
		} else {

				setContentView(R.layout.maps_activity_main);

				mDrawerList = (ListView) findViewById(R.id.navList);
				mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
				mActivityTitle = "Nearby Banks/ATMs";//string

				addDrawerItems();
				setupDrawer();

				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setHomeButtonEnabled(true);
				getSupportActionBar().setTitle("ATM");
				InitCompoIfNeeded();
				places = getResources().getStringArray(R.array.places);
				currentLocation();

				if (loc != null) {
					mMap.clear();
					//		actionBar.setTitle("ATM");
					new GetPlaces(MapsMainActivity.this,
							"Finance");
				}
			}
	}

	void FilterPlaces() {
		final String sources[] = new String[]{"ATM", "Bank", "Finance"};
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a category");
		builder.setItems(sources, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (loc != null) {
					actionBar.setTitle(sources[which]);
					mMap.clear();
					new GetPlaces(MapsMainActivity.this,
							sources[which].toLowerCase().replace(
									"-", "_").replace(" ", "_")).execute();
				}
			}
		});
		builder.show();
	}

	private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

		private ProgressDialog dialog;
		private Context context;
		private String places;

		public GetPlaces(Context context, String places) {
			this.context = context;
			this.places = places;
		}

		@Override
		protected void onPostExecute(ArrayList<Place> result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			for (int i = 0; i < result.size(); i++) {
				mMap.addMarker(new MarkerOptions()
						.title(result.get(i).getName())
						.position(
								new LatLng(result.get(i).getLatitude(), result
										.get(i).getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.pin))
						.snippet(result.get(i).getVicinity()));
			}
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(result.get(0).getLatitude(), result
							.get(0).getLongitude())) // Sets the center of the map to
							// Mountain View
					.zoom(14) // Sets the zoom
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Loading Maps");
			dialog.isIndeterminate();
			dialog.show();
		}

		//Browser key AIzaSyCIIa8HuIC1duTLLVRPffQTex0Tw0fVagE
		//Server key AIzaSyCUUjovK_G1Q-ak0wV5RPTHuzyywDO5iWA
		@Override
		protected ArrayList<Place> doInBackground(Void... arg0) {
			PlacesService service = new PlacesService("AIzaSyCUUjovK_G1Q-ak0wV5RPTHuzyywDO5iWA");
			ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(), // 28.632808
					loc.getLongitude(), places); // 77.218276

			for (int i = 0; i < findPlaces.size(); i++) {

				Place placeDetail = findPlaces.get(i);
				Log.e(TAG, "places : " + placeDetail.getName());
			}
			return findPlaces;
		}

	}

	private void InitCompoIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				initCompo();
			}
		}
	}

	private void initCompo() {
		mMap.setMyLocationEnabled(true);
		mapSettings = mMap.getUiSettings();
		mapSettings.setScrollGesturesEnabled(true);
		mapSettings.setZoomControlsEnabled(true);
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);//use of location services by firstly defining location manager.
		String provider = lm.getBestProvider(new Criteria(), true);
		if (provider == null) {
			onProviderDisabled(provider);
		}
		Location loc = lm.getLastKnownLocation(provider);


		if (loc != null) {
			onLocationChanged(loc);
		}
	}

	public void onLocationChanged(Location location) {

		LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());// This methods gets the users current longitude and latitude.

		mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));//Moves the camera to users current longitude and latitude
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, (float) 14.6));//Animates camera and zooms to preferred state on the user's current location.
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.maps_mode) {
			FilterPlaces();
			return true;
		}

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onProviderDisabled(String provider) {
		Log.i("OnProviderDisabled", "OnProviderDisabled");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_maps, menu);
		return true;
	}

	private void currentLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String provider = locationManager
				.getBestProvider(new Criteria(), true);

		Location location = locationManager.getLastKnownLocation(provider);

		if (location == null) {
			locationManager.requestLocationUpdates(provider, 0, 0, listener);
		} else {
			loc = location;
			new GetPlaces(MapsMainActivity.this, places[0].toLowerCase().replace(
					"-", "_")).execute();
			Log.e(TAG, "location : " + location);
		}
	}


	private LocationListener listener = new LocationListener() {

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
		public void onLocationChanged(Location location) {
			Log.e(TAG, "location update : " + location);
			loc = location;
			locationManager.removeUpdates(listener);
		}
	};

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
	}

//NavigationDrawer Code Ends


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

}

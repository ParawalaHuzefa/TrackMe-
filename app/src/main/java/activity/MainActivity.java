package activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs656.bfls2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import database.database;
import fragment.AddFragment;
import fragment.BottomSheet3DialogFragment;
import fragment.ExistingFragment;
import fragment.HomeFragment;
import fragment.*;
import service.TransmitterService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtUsername, txtEmail;
    private Toolbar toolbar;
    private FloatingActionButton fab, fab1, fab2;
    private ActionBarDrawerToggle toggle;
    private TransmitterService transmitterService;

    private static final String TAG = "MainActivity";

    private static ProgressDialog mProgressDialog;
    private static database db;


    Intent mServiceIntent ;//= new Intent(this, transmitterService.getClass());
    Location mLastLocation;
    LocationManager locationManager;

    String mprovider;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    private BottomNavigationView bottomNavigationView;
    BottomSheetDialogFragment bottomSheetDialogFragment;

    public static final String MyPREFERENCES = "App";
    public static final String FAKE_TAG = "isFake";
    private static SharedPreferences sharedpreferences;
    MarkerOptions n;
    Marker marker1;
    private static LatLng fakeL;
    TextView userName,userEmail;

    public static LatLng getFakeL() {
        return fakeL;
    }

    public static SharedPreferences getSharedpreferences() {
        return sharedpreferences;
    }

    /*
     * This method on creation of this activity it will create three FloatingActionButton and set its visibility
     * according to the state(faking the location, transmitting original or putting the fake marker) the user is in
     * and create the navigation drawer and set its default fragment HomeFragment and also set the location listner of the device
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        db = new database();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtUsername = (TextView) navHeader.findViewById(R.id.usernameNav);
        txtEmail = (TextView) navHeader.findViewById(R.id.useremailNav);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationBottom);
        bottomSheetDialogFragment = new BottomSheet3DialogFragment();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.setVisibility(View.GONE);
                fab1.setVisibility(View.VISIBLE);
                fab2.setVisibility(View.GONE);


                Toast.makeText(MainActivity.this, "Touch on Map to select fake location", Toast.LENGTH_LONG).show();
                final GoogleMap mMap = HomeFragment.getGoogleMap();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        fakeL = latLng;
                        HomeFragment.updateMarkersMap();



                    }
                });
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fakeL != null){
                    final GoogleMap mMap = HomeFragment.getGoogleMap();
                    mMap.setOnMapClickListener(null);


                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(FAKE_TAG, true);
                    editor.commit();

                    db.onAuthSuccess(fakeL.latitude, fakeL.longitude);
                    fakeL=null;
                    fab.setVisibility(View.GONE);
                    fab1.setVisibility(View.GONE);
                    fab2.setVisibility(View.VISIBLE);

                    transmitterService = new TransmitterService();
                    mServiceIntent = new Intent(MainActivity.this, transmitterService.getClass());
                    stopService(mServiceIntent);
                }else{
                    Toast.makeText(MainActivity.this,"Please select any poin on map",Toast.LENGTH_LONG).show();
                }





            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.VISIBLE);
                fab1.setVisibility(View.GONE);
                fab2.setVisibility(View.GONE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                fakeL=null;


                transmitterService = new TransmitterService();
                mServiceIntent = new Intent(MainActivity.this, transmitterService.getClass());
                if (!isMyServiceRunning(transmitterService.getClass())) {
                    startService(mServiceIntent);
                }


            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.usernameNav);
        userEmail = (TextView) header.findViewById(R.id.useremailNav);

        userName.setText(usernameFromEmail(db.getmAuth().getCurrentUser().getEmail()));
        userEmail.setText(db.getmAuth().getCurrentUser().getEmail());

        navigationView.setNavigationItemSelectedListener(this);
        //bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                                (new AddFragment()).show(getSupportFragmentManager(), new AddFragment().getTag());
                                break;
                            case R.id.action_item2:
                                (new ExistingFragment()).show(getSupportFragmentManager(), new ExistingFragment().getTag());
                                //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                                break;
                            case R.id.action_item3:
                                (new ViewFragment()).show(getSupportFragmentManager(), new ViewFragment().getTag());
                                //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                                break;
                            case R.id.action_item4:
                                (new RecivedRequestFragment()).show(getSupportFragmentManager(), new RecivedRequestFragment().getTag());
                                //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                                break;

                        }

                        return true;
                    }
                });

        Class fragmentClass = HomeFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        db.showRelationToMe();

        double lat = (Math.random() * -60 + 1);
        double lon = (Math.random() * 60 + 1);

        if (sharedpreferences.getBoolean(FAKE_TAG, false)) {
            //fake
            fab.setVisibility(View.GONE);
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.VISIBLE);
        } else {
            fakeL=null;
            fab.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);

            if (db.getmAuth() != null) {
                transmitterService = new TransmitterService();
                mServiceIntent = new Intent(this, transmitterService.getClass());
                if (!isMyServiceRunning(transmitterService.getClass())) {
                    startService(mServiceIntent);
                }
                Log.d(TAG, " On Application Destroyed");
            }

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 10, 1, this);

            if (location != null) {
                onLocationChanged(location);
            } else {
                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
                Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_LONG).show();
            }

        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle("Home");
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
    /*
     * this method checks wather the service is running or not if its running return the boolean value
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    /*
     * When back is pressed close the navigation drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(MainActivity.this, "Log Out Section", Toast.LENGTH_LONG).show();
            logOut();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * When a navigation item is selected change the other two accordingly
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_home) {
            // Handle the camera action
            //Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_LONG).show();
            bottomNavigationView.setVisibility(View.VISIBLE);
            if (sharedpreferences.getBoolean(FAKE_TAG, false)) {
                //fake
                fab.setVisibility(View.GONE);
                fab1.setVisibility(View.GONE);
                fab2.setVisibility(View.VISIBLE);
            } else {
                fakeL = null;
                fab.setVisibility(View.VISIBLE);
                fab1.setVisibility(View.GONE);
                fab2.setVisibility(View.GONE);
            }
            /*fab.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);*/
            fragmentClass = HomeFragment.class;
        } else /*if (id == R.id.nav_request) {
            //Toast.makeText(MainActivity.this, "Request clicked", Toast.LENGTH_LONG).show();
            fragmentClass = RequestFragment.class;
        } else */if (id == R.id.nav_profile_setting) {
            //Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_LONG).show();
            bottomNavigationView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            fab1.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);
            fragmentClass = ProfileFragment.class;
        } else if (id == R.id.nav_logout) {
            //Toast.makeText(MainActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            logOut();
            return true;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
     * When logout is pressed call the service and destroy it and clean the session
     */
    public void logOut() {
        db.signOut();
        transmitterService = new TransmitterService();
        mServiceIntent = new Intent(this, transmitterService.getClass());
        stopService(mServiceIntent);
        Log.e(TAG, "stopservice");


        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

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
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
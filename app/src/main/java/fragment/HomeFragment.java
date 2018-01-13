package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cs656.bfls2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import activity.MainActivity;
import adapter.CustomAdapter;
import database.database;
import models.User;


public class HomeFragment extends Fragment  {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    static MapView mMapView;
    static private GoogleMap googleMap;
    private static final String TAG = "HomeFragment";
    private static CustomAdapter adapterNonPartner;
    static database db;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /*
    * Method to export google map object in the fragment reference
    */
    public static GoogleMap getGoogleMap() {
        return googleMap;
    }

    /*
    * Method to create view using Fragment (fragment_home)
    * Google Map Async method: To check for necessary permission is there,
    * put markers by calling updateMarkersMap method
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        db = new database();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                } else {
                    Log.e("HomeFragment", "PERMISSION GRANTED");
                }
                updateMarkersMap();
            }
        });

        return rootView;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    /*
     * Fetch partnered users and put marker on the map using it
     */
    public static void updateMarkersMap() {
        if (googleMap != null) {

            ArrayList<User> dataModelPartnerUsers = db.getDataModelPartnerUsers();
            HashMap<String, Marker> markerHashMap = db.getMarkerHashMap();
            googleMap.clear();
            markerHashMap.clear();

            for (User user : dataModelPartnerUsers) {
                LatLng temp = new LatLng(user.lat, user.lon);
                MarkerOptions n = new MarkerOptions().position(temp).title(user.username);
                Marker marker1 = googleMap.addMarker(n);
                markerHashMap.put(user.userid, marker1);
                googleMap.addMarker(n);
            }

            User mUser = db.getMyUser();
            LatLng temp = new LatLng(mUser.lat, mUser.lon);
            MarkerOptions n;
            SharedPreferences sharedpreferences=MainActivity.getSharedpreferences();

            if(sharedpreferences.getBoolean(MainActivity.FAKE_TAG,false)){
                n = new MarkerOptions().position(temp).title(mUser.username).flat(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));;
            }else{
                n = new MarkerOptions().position(temp).title(mUser.username).flat(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));;
            }

            if(MainActivity.getFakeL() != null){
                MarkerOptions n1 = new MarkerOptions().position(MainActivity.getFakeL()).title("Fake Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                googleMap.addMarker(n1);
            }

            Marker marker1 = googleMap.addMarker(n);
            markerHashMap.put(mUser.userid, marker1);
            googleMap.addMarker(n);

        }else{
            Log.d(TAG,"GOOGLE MAP OBJ NULL ");
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

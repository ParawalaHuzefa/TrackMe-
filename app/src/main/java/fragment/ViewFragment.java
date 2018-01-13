package fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cs656.bfls2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import adapter.CustomAdapterPartners;
import database.database;
import models.User;

public class ViewFragment extends BottomSheetDialogFragment {

    private static final String TAG = "``Exist Fragment``````";
    CustomAdapterPartners adapterPartner;
    View contentView;
    ListView PartnerListView;
    TextView noUsertxt;

    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    /*
    * Method to create view using Fragment (fragment_view)
    * populates the 'View' part in the application
    * Fetch the partner users with the instance of the database class
    * List View Listener:  Do focus on the marker using map object reference from the HomeFragment class
    */
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_view, null);
        PartnerListView = (ListView) contentView.findViewById(R.id.partnerUsersList);
        noUsertxt = (TextView) contentView.findViewById(R.id.noUserTxt);
        final database db=new database();
        final ArrayList<User> dataModelPartnerUsers = db.getDataModelPartnerUsers();

        if(dataModelPartnerUsers.size() == 0){
            noUsertxt.setVisibility(View.VISIBLE);
        }else{
            adapterPartner = new CustomAdapterPartners(dataModelPartnerUsers, getContext());
            PartnerListView.setAdapter(adapterPartner);
        }

        PartnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User dataModel = dataModelPartnerUsers.get(position);
                final GoogleMap mMap = HomeFragment.getGoogleMap();
                LatLng loc = new LatLng(dataModel.lat, dataModel.lon);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(0).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        dialog.setContentView(contentView);
    }
}
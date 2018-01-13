package fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cs656.bfls2.R;

import java.util.ArrayList;

import adapter.CustomAdapter;
import database.database;
import models.User;

public class RecivedRequestFragment extends BottomSheetDialogFragment {

    private static final String TAG = "Recived Req Fragment";
    CustomAdapter adapterRequest, adapterAPRequest;
    View contentView;
    ListView ReqListView,APReqListView;
    TextView noReqTxt,noAPReqTxt;

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
    * Method to create view using Fragment (fragment_recived_request)
    * Fetch request user from the instance of database class
    * Put it into adapter and bind it with list in the fragment
    */
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_recived_request, null);
        ReqListView = (ListView) contentView.findViewById(R.id.requestedUsersList);
        APReqListView = (ListView) contentView.findViewById(R.id.apRequestedUsersList);
        noReqTxt = (TextView) contentView.findViewById(R.id.noReqTxt);
        noAPReqTxt = (TextView) contentView.findViewById(R.id.noAPReqTxt);
        final database db=new database();
        final ArrayList<User> dataModelRequestUsers = db.getDataModelRequestUsers();

        if(dataModelRequestUsers.size() == 0){
            noReqTxt.setVisibility(View.VISIBLE);
        }else{
            adapterRequest = new CustomAdapter(dataModelRequestUsers, getContext());
            ReqListView.setAdapter(adapterRequest);
        }

        ReqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User dataModel = dataModelRequestUsers.get(position);
                db.approveRequest(dataModel);
                dismiss();
            }
        });

        final ArrayList<User> dataModelAPRequestUsers = db.getDataModelAPRequestUsers();

        if(dataModelAPRequestUsers.size() == 0){
            noAPReqTxt.setVisibility(View.VISIBLE);
        }else{
            adapterAPRequest = new CustomAdapter(dataModelAPRequestUsers, getContext());
            APReqListView.setAdapter(adapterAPRequest);
        }

        APReqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User dataModel = dataModelAPRequestUsers.get(position);
                db.removeAPRequest(dataModel);
                dismiss();
            }
        });

        dialog.setContentView(contentView);
    }

}
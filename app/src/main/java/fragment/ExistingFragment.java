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

import adapter.CustomAdapterPartners;
import database.database;
import models.User;

public class ExistingFragment extends BottomSheetDialogFragment {

    private static final String TAG = "``Exist Fragment``````";
    CustomAdapterPartners adapterPartner;
    View contentView;
    ListView PartnerListView;
    TextView noUsertxt;

    /*
     * To provide swioe up animation and visiblity action
     * swipe down - dismiss action
     */
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
     * Intialize the view for 'Exist' section. Fetch the partner users and display
     * it using Fragment (fragment_add).
     *
     * List Click Listner: Call remove partner to the database using database instance
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_existing, null);
        PartnerListView = (ListView) contentView.findViewById(R.id.partnerUsersList);
        noUsertxt = (TextView) contentView.findViewById(R.id.noUserTxt);
        final database db = new database();
        final ArrayList<User> dataModelPartnerUsers = db.getDataModelPartnerUsers();

        if (dataModelPartnerUsers.size() == 0) {
            noUsertxt.setVisibility(View.VISIBLE);
        } else {
            adapterPartner = new CustomAdapterPartners(dataModelPartnerUsers, getContext());
            PartnerListView.setAdapter(adapterPartner);
        }

        PartnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User dataModel = dataModelPartnerUsers.get(position);
                db.removePartner(dataModel);
                dismiss();
            }
        });
        dialog.setContentView(contentView);
    }

}
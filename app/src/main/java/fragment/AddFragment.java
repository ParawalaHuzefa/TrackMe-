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

public class AddFragment extends BottomSheetDialogFragment {

    private static final String TAG = "`````Add Fragment``````";
    CustomAdapter adapterNonPartner;
    View contentView;
    ListView nonPartnerListView;
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
     * Intialize the view for 'Add' section. Fetch the non partner users and display
     * it using Fragment (fragment_add).
     *
     * List Click Listner: Call new request to the database using database instance
     */

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_add, null);
        nonPartnerListView = (ListView) contentView.findViewById(R.id.nonPartnerUsersList);
        noUsertxt = (TextView) contentView.findViewById(R.id.noUserTxt);
        final database db=new database();
        final ArrayList<User> dataModelsNonPartnerUsers = db.getDataModelsNonPartnerUsers();

        if(dataModelsNonPartnerUsers.size() == 0){
            noUsertxt.setVisibility(View.VISIBLE);
        }else{
            adapterNonPartner = new CustomAdapter(dataModelsNonPartnerUsers, getContext());
            nonPartnerListView.setAdapter(adapterNonPartner);
        }

        nonPartnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User dataModel = dataModelsNonPartnerUsers.get(position);
                db.writeNewRequest(dataModel.userid);
                dismiss();
            }
        });
        dialog.setContentView(contentView);
    }

}
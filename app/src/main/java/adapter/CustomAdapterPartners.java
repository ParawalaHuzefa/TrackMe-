package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cs656.bfls2.R;

import java.util.ArrayList;

import models.User;



public class CustomAdapterPartners extends ArrayAdapter<User> implements View.OnClickListener {


    private ArrayList<User> dataSet;
    Context mContext;
    private static final String TAG = "CustomAdapterPartners";
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView txtUsername;
        TextView txtUserId;
    }

    public CustomAdapterPartners(ArrayList<User> data, Context context) {
        super(context, R.layout.list_item_partner, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
    }


    /*
    * Generate view from layout list_item_partner
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View result;
        User dataModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_partner, parent, false);
            viewHolder.txtUsername = (TextView) convertView.findViewById(R.id.list_item_userName);
            viewHolder.txtUserId = (TextView) convertView.findViewById(R.id.list_item_userId);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;
        viewHolder.txtUsername.setText(dataModel.username);
        viewHolder.txtUserId.setText(dataModel.userid);

        return result;
    }
}

package mooreapps.whopays20;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gmoore on 12/16/2015.
 * Adapter for selecting a perseon (AKA user)
 */
public class UserAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context context, int textViewResourceId, List<User> users) {
        super(context, textViewResourceId, users);
        this.mContext = context;
        this.mUsers = users;
    }

    @Override
    public int getCount(){
        return mUsers.size();
    }

    @Override
    public User getItem(int position){
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //This is for the first item before dropdown or default state.
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(" " + mUsers.get(position).getFirstName() + " " + mUsers.get(position).getLastName());
        label.setGravity(Gravity.LEFT | Gravity.CENTER);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        label.setPadding(10, 10, 10, 10);
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        //This is when user click the spinner and list of item display
        // beneath it
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(" " + mUsers.get(position).getFirstName() + " " + mUsers.get(position).getLastName());
        label.setGravity(Gravity.LEFT | Gravity.CENTER);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        label.setPadding(10,10,10,10);
        return label;
    }

}


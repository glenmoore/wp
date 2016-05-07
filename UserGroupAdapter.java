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
 */
public class UserGroupAdapter extends ArrayAdapter<UserGroup> {

    private Context mContext;
    private List<UserGroup> mGroups;
    private int mTextColor = Color.WHITE;

    public UserGroupAdapter(Context context, int textViewResourceId, List<UserGroup> groups) {
        super(context, textViewResourceId, groups);
        this.mContext = context;
        this.mGroups = groups;
        setTextColor(Color.BLACK);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public int getCount(){
        return mGroups.size();
    }

    @Override
    public UserGroup getItem(int position){
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //This is for the first item before dropdown or default state.
        TextView label = new TextView(mContext);
        label.setTextColor(getTextColor());
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        label.setText(" " + mGroups.get(position).getGroupName());
        label.setGravity(Gravity.LEFT | Gravity.CENTER);
        label.setPadding(10,10,10,10);
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        //This is when user click the spinner and list of item display
        // beneath it
        TextView label = new TextView(mContext);
        label.setTextColor(getTextColor());
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        label.setText(" " + mGroups.get(position).getGroupName());
        label.setPadding(10,10,10,10);
        label.setGravity(Gravity.LEFT | Gravity.CENTER );

        return label;
    }

    private int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

}


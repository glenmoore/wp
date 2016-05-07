package mooreapps.whopays20;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gmoore on 12/16/2015.
 */
public class LocationAdapter extends ArrayAdapter<Location> {

    private Context mContext;
    private List<Location> mLocations;
    private int mTextColor = Color.WHITE;
    private int mViewResourceId;

    public LocationAdapter(Context context, int viewResourceId, List<Location> locations) {
        super(context, viewResourceId, locations);

        UIUtil.logBold("LA", "CONSTRUCTOR", "1");

        this.mContext = context;
        this.mLocations = locations;
        setTextColor(Color.BLACK);
        //setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mViewResourceId =  viewResourceId;

    }

    @Override
    public int getCount(){
        UIUtil.logBold("LA", "getCount",  " mLocations.size()=" + mLocations.size());
        return mLocations.size();
    }

    @Override
    public Location getItem(int position){
        return mLocations.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //This is for the first item before dropdown or default state.
        /* BELOW CODE IS FROM DROPDOWN
        TextView label = new TextView(mContext);
        label.setTextColor(getTextColor());
        label.setText(" " + mLocations.get(position).getLocName());
        label.setGravity(Gravity.LEFT | Gravity.CENTER );
        return label;
        */

        //New code for autocomplete textbox
        UIUtil.logBold("LA","GV","1");

        View v = convertView;
        if (v == null) {
            UIUtil.logBold("LA","GV","2");
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            UIUtil.logBold("LA","GV","3");
            v = vi.inflate(mViewResourceId, null);
            UIUtil.logBold("LA","GV","4");
        }
        Location location = mLocations.get(position);
        UIUtil.logBold("LA","GV","5");
        if (location != null) {
            UIUtil.logBold("LA","GV","6");
            TextView locationNameLabel = (TextView) v.findViewById(R.id.transLocation);
            UIUtil.logBold("LA","GV","7");
            if (locationNameLabel != null) {
                UIUtil.logBold("LA","GV","8");
                locationNameLabel.setText(String.valueOf(location.getLocName()));
                UIUtil.logBold("LA", "GV", "9");
            }
        }
        UIUtil.logBold("LA","GV","10");
        return v;



    }

    /*
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        //This is when user click the spinner and list of item display
        // beneath it

        UIUtil.logBold("LA","GDDV","1");

        TextView label = new TextView(mContext);
        label.setTextColor(getTextColor());
        label.setText(" " + mLocations.get(position).getLocName());
        label.setGravity(Gravity.LEFT | Gravity.CENTER );

        return label;
    }
    */

    private int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }


}

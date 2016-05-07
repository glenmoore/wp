package mooreapps.whopays20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



/**
 * Created by gmoore on 12/16/2015.
 */
public class UserTotalAdapter extends ArrayAdapter<UserTotal> {

    private List<UserTotal> mUserTotals;

    public UserTotalAdapter(Context context, int textViewResourceId, List<UserTotal> userTotals) {
        super(context, textViewResourceId, userTotals);
        this.mUserTotals = userTotals;
    }

    @Override
    public int getCount(){
        return mUserTotals.size();
    }

    @Override
    public UserTotal getItem(int position){
        return mUserTotals.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserTotal userTotal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_totals_layout, parent, false);
        }

        TextView tvNameFirst = (TextView) convertView.findViewById(R.id.nameFirst);
        TextView tvNameLast = (TextView) convertView.findViewById(R.id.nameLast);
        TextView tvTotalPaid = (TextView) convertView.findViewById(R.id.totalPaid);
        TextView tvRowId = (TextView) convertView.findViewById(R.id.rowId);

        tvNameFirst.setText(userTotal.getFirstName());
        tvNameLast.setText(userTotal.getLastName());

        tvTotalPaid.setText(CurrencyUtil.currencyFormat(userTotal.getTotalPaid()));

        int rowId = userTotal.getUserId();
        String stRowId = Integer.valueOf(rowId).toString();
        tvRowId.setText(stRowId);

        return convertView;
    }


}

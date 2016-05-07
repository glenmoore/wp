package mooreapps.whopays20;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;



/**
 * Created by gmoore on 12/16/2015.
 */
public class PaymentAdapter extends ArrayAdapter<Payment> {

    private Context mContext;
    private List<Payment> mPayments;
    private int MAX_NAME_LENGTH = 20;

    public PaymentAdapter(Context context, int textViewResourceId, List<Payment> payments) {
        super(context, textViewResourceId, payments);
        this.mContext = context;
        this.mPayments = payments;
    }

    //HACK AROUND SCROLLING ISSUE
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    //HACK AROUND SCROLLING ISSUE
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount(){
        //UIUtil.logBold("PaymentAdapter","getCount","mPayments.size()=" +mPayments.size());
        return mPayments.size();
    }

    @Override
    public Payment getItem(int position){
        //UIUtil.logBold("PaymentAdapter","getItem","position=" +position);
        try {
            mPayments.get(position);
        } catch (Exception e){
            UIUtil.logBold("PaymentAdapter", "getItem", "Exception=" + e);
        }
        return mPayments.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        //UIUtil.logBold("PaymentAdapter", "getView", "position=" + position);

        Payment payment = null;
        try {
            payment = getItem(position);
        } catch (Exception e) {
            UIUtil.logBold("PaymentAdapter", "getView", "exception=" + e);
        }

        //UIUtil.logBold("PaymentAdapter", "getView", "payment=" + payment);

        //Hack around scroll issue THIS WAS WHAT HELPED SATURDAY
        //
        // if (convertView != null) {
        // return convertView;
        // } else {
        // convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_payment_layout, null, false);
        // }


        //SUNDAY
        // GOTAA HAVE THIS ELSE CAN'T SWAP GROUPS
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_payment_layout, null, false);
        }


        TextView tvUserName = (TextView) convertView.findViewById(R.id.userName);

        CurrencyEditText tvTotalPaid = (CurrencyEditText) convertView.findViewById(R.id.payAmount);

        TextView tvRowId = (TextView) convertView.findViewById(R.id.rowId);
        TextView tvUserId = (TextView) convertView.findViewById(R.id.userId);

        WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(mContext);

        User user = db.getOneUser(payment.mUserId);

        String firstName;
        String lastName;
        if (user == null) {
            firstName = "";
            lastName = "";
        } else {
            if ((user.getFirstName().length() + user.getLastName().length()) > MAX_NAME_LENGTH) {
                firstName = user.getFirstName().substring(0, 1);
            } else {
                firstName = user.getFirstName();
            }
            lastName = user.getLastName();
        }
        String formattedName = firstName + " " + lastName;
        tvUserName.setText(formattedName);

        tvTotalPaid.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tvTotalPaid.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tvTotalPaid.setText(CurrencyUtil.currencyFormat(payment.getPayAmount()));

        tvRowId.setText(NumberUtil.intToString(payment.mId));
        tvUserId.setText(NumberUtil.intToString(payment.mUserId));

        //UIUtil.logBold("PaymentAdapter", "getView", "end");

        return convertView;
    }


}

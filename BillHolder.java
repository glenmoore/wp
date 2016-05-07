package mooreapps.whopays20;

/**
 * Created by gmoore on 12/11/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

/**
 * Created by gmoore on 10/17/2015.
 */
public class BillHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView transDate;
    public TextView transAmount;
    public TextView transLocation;
    public TextView transGroup;
    public TextView transStatus;
    public TextView transPaid;
    public TextView rowId;
    Context mContext;

    public BillHolder(View v) {
        super(v);
        transDate = (TextView) v.findViewById(R.id.transDate);
        transAmount = (TextView) v.findViewById(R.id.transAmount);
        transLocation = (TextView) v.findViewById(R.id.transLocation);
        transGroup = (TextView) v.findViewById(R.id.transGroup);
        transStatus = (TextView) v.findViewById(R.id.transStatus);
        transPaid = (TextView) v.findViewById(R.id.transPaid);
        rowId = (TextView) v.findViewById(R.id.rowId);
        v.setOnClickListener(this);
        transDate.setOnClickListener(this);
        transAmount.setOnClickListener(this);
        transLocation.setOnClickListener(this);
        transStatus.setOnClickListener(this);
        transPaid.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), BillEditActivity.class);
        intent.putExtra(IntentParams.ADD_MODE, false);
        String stRowId = rowId.getText().toString();
        intent.putExtra(IntentParams.ROW_ID, stRowId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

}


package mooreapps.whopays20;

/**
 * Created by gmoore on 12/11/2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by gmoore on 10/14/2015.
 */
public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<Transaction> mTransactions;
    WhoPaysDbAdapter mDbAdapter;
    UserGroupModel mUserGroupModel;

    public BillAdapter(ArrayList<Transaction> transactions, Context context) {
        mTransactions = transactions;
        mContext = context;
        mDbAdapter = WhoPaysDbAdapter.getInstance(context);
        mUserGroupModel = new UserGroupModel(context.getResources());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_view_item, parent, false);
        BillHolder bh = new BillHolder(v);
        bh.setContext(mContext);
        return bh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BillHolder bh = (BillHolder) holder;
        Transaction currentTransaction = mTransactions.get(position);
        bh.transDate.setText(currentTransaction.mTransDate.toString());
        bh.transAmount.setText(CurrencyUtil.currencyFormat(currentTransaction.mTransAmount));
        bh.transStatus.setText(currentTransaction.getStatusText(mContext.getResources()));
        bh.transPaid.setText(getTransPaidMessage(currentTransaction));

        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(mContext);

        if (currentTransaction.mLocationId == null) {
            bh.transLocation.setText(R.string.vendor_not_found);
        } else {
            int locId = currentTransaction.mLocationId;
            Location location = dbAdapter.getOneLocation(locId);
            if (location == null) {
                bh.transLocation.setText(R.string.vendor_not_found);
            } else {
                bh.transLocation.setText(location.getLocName());
            }
        }

        if (currentTransaction.mGroupId == null) {
            bh.transGroup.setText(R.string.group_not_found);
        } else {
            int grpId = currentTransaction.mGroupId;
            UserGroup userGroup = dbAdapter.getOneUserGroup(grpId);
            if (userGroup == null) {
                bh.transGroup.setText(R.string.group_not_found);
            } else {
                bh.transGroup.setText(userGroup.getGroupName());
            }
        }

        bh.rowId.setText(String.valueOf(currentTransaction.mId));
    }

    private String getTransPaidMessage (Transaction transaction) {
        ArrayList<UserTotal> userTotals = mUserGroupModel.getBillUserTotals(transaction, mContext);

        if (userTotals.size() == 0) {
            return "";
        } else {
            if (userTotals.size() > 1) {
                return mContext.getResources().getString(R.string.split);
            } else {
                UserTotal userTotal = userTotals.get(0);
                String last = "";
                if (userTotal.getLastName() != null && userTotal.getLastName().length() > 0) {
                    last = String.valueOf(userTotal.getLastName().charAt(0));
                }
                return userTotal.getFirstName() + " " + last;
            }
        }
    }

    public int getItemCount() {
        return mTransactions.size();
    }

}

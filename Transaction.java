package mooreapps.whopays20;

import android.content.res.Resources;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by gmoore on 12/10/2015.
 */
public class Transaction extends ValueObject {

        Date mTransDate;
        BigDecimal mTransAmount;
        Integer mLocationId;
        Integer mGroupId;
        String mTransNote;
        BigDecimal mTransPaidAmount;
        Integer mTransStatus;

        public static final int TRANS_STATUS_NEW = 1;
        public static final int TRANS_STATUS_UNPAID = 2;
        public static final int TRANS_STATUS_PARTIAL_PAY = 3;
        public static final int TRANS_STATUS_PAID = 4;

        public Transaction(){ }

        public String getStatusText(Resources resources) {
            int status = 0;
            if (mTransStatus != null) {
                status = mTransStatus;
            } else {
                status = TRANS_STATUS_NEW;
            }

            switch (status) {
                case Transaction.TRANS_STATUS_NEW:
                    return resources.getString(R.string.bill_status_new);
                case Transaction.TRANS_STATUS_UNPAID:
                    return resources.getString(R.string.bill_status_unpaid);
                case Transaction.TRANS_STATUS_PARTIAL_PAY:
                    return resources.getString(R.string.bill_status_partial_pay);
                case Transaction.TRANS_STATUS_PAID:
                    return resources.getString(R.string.bill_status_paid);
            }
            return "";
        }

    public static String GetStatusText(int status, Resources resources) {
        switch (status) {
            case Transaction.TRANS_STATUS_NEW:
                return resources.getString(R.string.bill_status_new);
            case Transaction.TRANS_STATUS_UNPAID:
                return resources.getString(R.string.bill_status_unpaid);
            case Transaction.TRANS_STATUS_PARTIAL_PAY:
                return resources.getString(R.string.bill_status_partial_pay);
            case Transaction.TRANS_STATUS_PAID:
                return resources.getString(R.string.bill_status_paid);
        }
        return "";
    }


}

package mooreapps.whopays20;

import java.math.BigDecimal;

/**
 * Created by gmoore on 12/22/2015.
 */
public class Payment extends ValueObject {

    Integer mTransId;
    Integer mUserId;
    private BigDecimal mPayAmount;

    public Payment(){
        mTransId = 0;
        mUserId = 0;
        mPayAmount= new BigDecimal(0);
    }

    public void setPayAmount(BigDecimal payAmount) {
        if (payAmount == null) {
            mPayAmount = new BigDecimal(0);
        } else {
            mPayAmount = payAmount;
        }
    }

    public BigDecimal getPayAmount() {
        if (mPayAmount == null) {
            BigDecimal payAmount = new BigDecimal(0);
            setPayAmount(payAmount);
        }
        return mPayAmount;
    }

    @Override
    public String toString() {
        String str = "Payment: ";
        str = str + ("mTransId=" +mTransId);
        str = str + ("  mUserId=" + mUserId);
        str = str + (" mPayAmount=" + mPayAmount);
        return str;
    }



}

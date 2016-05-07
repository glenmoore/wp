package mooreapps.whopays20;


import java.math.BigDecimal;

class UserTotal {
    User mUser;
    BigDecimal mTotalPaid;
    String mWhoPaysMessage;

    public UserTotal(User user) {
        mUser = user;
        mTotalPaid = new BigDecimal(0);
    }

    public UserTotal(String whoPaysMessage) {
        mWhoPaysMessage = whoPaysMessage;
        mTotalPaid = new BigDecimal(0);
    }

    public BigDecimal getTotalPaid() {
        return mTotalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        mTotalPaid = totalPaid;
    }

    public String getLastName() {
        if (mUser == null) {
            return "";
        } else {
            return mUser.getLastName();
        }
    }

    public String getFirstName() {
        if (mUser == null) {
            return "";
        } else {
            return mUser.getFirstName();
        }
    }

    public int getUserId() {
        if (mUser == null) {
            return 0;
        } else {
            return mUser.getId();
        }
    }

    public String getWhoPaysMessage() {
        if (mWhoPaysMessage == null) {
            return "";
        } else {
            return mWhoPaysMessage;
        }
    }

    public void setWhoPaysMessage(String whoPaysMessage) {
        mWhoPaysMessage = whoPaysMessage;
    }

    @Override
    public String toString() {
        String str = "User Total: ";
        str = str + ("user=" +mUser);
        str = str + ("  mTotalPaid=" + mTotalPaid);
        return str;
    }

}
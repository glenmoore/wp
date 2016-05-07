package mooreapps.whopays20;

public class User extends ValueObject {
    String mNameFirst;
    String mNameLast;
    String mNameMiddle;
    String mLogonId;
    String mLogonCred;
    String mNum;
    String mEmailAddr;
    String mPhoneCell;
    String mPhoneOther;

	
    public User(){
        mNameFirst = "";
        mNameLast = "";
        mNameMiddle = "";
        mLogonId = "";
        mLogonCred = "";
        mNum = "";
        mEmailAddr = "";
        mPhoneCell = "";
        mPhoneOther = "";
    }

    public String getLastName() {
        return mNameLast;
    }

    public String getFirstName() {
        return mNameFirst;
    }

    public String getLogonId() {
        return mLogonId;
    }


    @Override
    public String toString() {
        String str = "User: ";
        str = str + ("mNameFirst=" +mNameFirst);
        str = str + (" mNameLast=" + mNameLast);
        str = str + ("  mId=" + mId);
        return str;
    }

}


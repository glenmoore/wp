package mooreapps.whopays20;


/**
 * Created by gmoore on 12/7/2015.
 */
public class UserGroup extends ValueObject {

    String mGroupName;

    public UserGroup(){  }

    public String getGroupName() {
        return mGroupName;
    }


    @Override
    public String toString() {
        String str = "UserGroup: ";
        str = str + ("mNGroupName=" +mGroupName);
        str = str + ("  mId=" + mId);
        return str;
    }

}

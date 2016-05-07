package mooreapps.whopays20;

/**
 * Created by gmoore on 12/22/2015.
 */
public class UserGroupXref {

    int mId;
    Integer mGroupId;
    Integer mUserId;

    public UserGroupXref(){
    }

    public UserGroupXref (Integer groupId, Integer userId) {
        this.mGroupId = groupId;
        this.mUserId = userId;
    }



    @Override
    public String toString() {
        String str = "UserGroupXref: ";
        str = str + ("  mId=" + mId);
        str = str + ("mGroupId=" +mGroupId);
        str = str + ("mUserId=" +mUserId);
        return str;
    }


}

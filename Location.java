package mooreapps.whopays20;

/**
 * Created by gmoore on 12/7/2015.
 */
public class Location extends ValueObject {

    String mLocName;

    public Location(){
    }

    public Location (String locName){
        this.mLocName = locName;
    }

    public String getLocName() {
        return mLocName;
    }

    @Override
    public String toString() {
        String str = "Location: ";
        str = str + ("mLocName=" +mLocName);
        str = str + ("  mId=" + mId);
        return str;
    }

}

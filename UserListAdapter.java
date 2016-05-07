package mooreapps.whopays20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gmoore on 4/20/2016.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private List<User> mUsers;

    public UserListAdapter(Context context, int textViewResourceId, List<User> users) {
        super(context, textViewResourceId, users);
        this.mUsers = users;
    }

    @Override
    public int getCount(){
        return mUsers.size();
    }

    @Override
    public User getItem(int position){
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_users_layout, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        TextView tvRowId = (TextView) convertView.findViewById(R.id.rowId);
        String nameFirst = user.getFirstName();
        String nameLast = user.getLastName();
        String nickName = user.getLogonId(); // ToDo rename in DB etc.
        String formattedNickName = "";
        String formattedName = "";

        if (nickName != null && nickName.trim().length() > 0 ) {
            formattedNickName = "(" + nickName + ")";
            formattedName = nameFirst + " " + formattedNickName + " " + nameLast;
        } else {
            formattedName = nameFirst + " " + nameLast;
        }

        tvName.setText(formattedName);
        int rowId = user.getId();
        String stRowId = Integer.valueOf(rowId).toString();
        tvRowId.setText(stRowId);
        return convertView;
    }


}

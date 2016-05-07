package mooreapps.whopays20;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by gmoore on 10/27/2015.
 */
public class TotalsFragment extends ListFragment implements AdapterView.OnItemSelectedListener   {

    UserTotalAdapter mAdapter;
    private Spinner transGroup;
    ListView listView;
    WhoPaysDbAdapter dbAdapter;
    ArrayList<UserTotal> userTotals;
    UserGroup selectedUserGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_totals, container, false);
        transGroup = (Spinner) mView.findViewById(R.id.transGroup);
        listView = (ListView) mView.findViewById(android.R.id.list);
        transGroup.setOnItemSelectedListener(this);
        loadGroup();
        displayTotals();
        return mView;
    }

    private void displayTotals() {
        selectedUserGroup = (UserGroup) transGroup.getSelectedItem();
        new GetTotals().execute((Object[]) null);
    }



    // Retrieve and display user totals asynchronously
    private class GetTotals extends AsyncTask<Object, Object, ArrayList<UserTotal> > {

        @Override
        protected ArrayList<UserTotal> doInBackground(Object... params) {
            if (selectedUserGroup != null) {
                return getUserTotals(selectedUserGroup.mId);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserTotal> result) {
            if (result != null && result.size() > 0) {
                userTotals = result;
                Collections.sort(userTotals, new TotalComparator());
                mAdapter = new UserTotalAdapter(getActivity().getApplicationContext(), R.layout.listview_totals_layout, userTotals);
                listView.setAdapter(mAdapter);
            } else {
                listView.setAdapter(null);
            }
        }
    }

    // Return total payments per user for selected group
    private ArrayList<UserTotal> getUserTotals(int groupId) {
        UserGroupModel ugm = new UserGroupModel(getResources());
        return ugm.getUserTotals(groupId, getActivity().getApplicationContext());
    }

    @Override
    public void onListItemClick (ListView l, View v,int pos, long id){
        super.onListItemClick(l, v, pos, id);
    }


    @Override
    public void onResume(){
        super.onResume();
        new GetTotals().execute((Object[]) null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        displayTotals();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
    }

    private void loadGroup(){
        UserGroupAdapter  userGroupAdapter;
        List<UserGroup> userGroups = getDbAdapter().getAllUserGroups();
        userGroupAdapter = new UserGroupAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item , userGroups );
        userGroupAdapter.setTextColor(Color.BLACK);
        transGroup.setAdapter(userGroupAdapter);
        userGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private WhoPaysDbAdapter getDbAdapter() {
        if (dbAdapter == null) {
            dbAdapter = WhoPaysDbAdapter.getInstance(getActivity().getApplicationContext());
        }
        return dbAdapter;
    }


    private void displayMessages(ValidationResults vr) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        alert.setTitle("");
        alert.setMessage(vr.getMessages());

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    class TotalComparator implements Comparator<UserTotal> {
        @Override
        public int compare(UserTotal o1, UserTotal o2) {

            int result = o1.getTotalPaid().compareTo(o2.getTotalPaid());

            if (result == 1) {
                return -1;
            } else if (result == -1) {
                return 1;
            }
            return 0;
        }
    }


}
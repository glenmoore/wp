package mooreapps.whopays20;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.design.widget.FloatingActionButton;

import java.util.List;


/**
 * Created by gmoore on 10/27/2015.
 */

    public class UsersFragment extends ListFragment  {

    UserListAdapter mAdapter;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getData();

        View mView = inflater.inflate(R.layout.fragment_users, container, false);

        fab = (FloatingActionButton) mView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserEditActivity.class);
                intent.putExtra(IntentParams.ADD_MODE, true);
                getActivity().startActivity(intent);
            }
        });

        return mView;
    }

    private void getData() {
        WhoPaysDbAdapter dbAdapter =  WhoPaysDbAdapter.getInstance(getContext());
        List<User> users = dbAdapter.getAllUsers();
        mAdapter = new UserListAdapter(getActivity().getApplicationContext(), R.layout.listview_users_layout, users);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        Intent intent = new Intent(getActivity(), UserEditActivity.class);
        intent.putExtra(IntentParams.ADD_MODE, false);
        TextView tvRowId = (TextView) v.findViewById(R.id.rowId);
        String stRowId = tvRowId.getText().toString();
        intent.putExtra(IntentParams.ROW_ID, stRowId);
        getActivity().startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        getData();
        mAdapter.notifyDataSetChanged();
        getListView().invalidateViews();
    }

}
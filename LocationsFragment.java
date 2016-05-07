package mooreapps.whopays20;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gmoore on 10/27/2015.
 */
public class LocationsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.listview_loc_layout,
                null,
                new String[]{WhoPaysDbAdapter.LOC_NAME, WhoPaysDbAdapter.KEY_ROWID},
                new int[]{R.id.locName, R.id.rowId}, 0);

        setListAdapter(mAdapter);

        /** Creating a loader for populating listview from sqlite database */
        /** This statement, invokes the method onCreatedLoader() */
        getActivity().getSupportLoaderManager().initLoader(WhoPaysDbAdapter.LOCATION_LOADER_ID, null, this);

        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_locations, container, false);

        fab = (FloatingActionButton) mView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationEditActivity.class);
                intent.putExtra(IntentParams.ADD_MODE, true);
                getActivity().startActivity(intent);
            }
        });

        return mView;
    }


    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        super.onListItemClick(l, v, pos, id);

        Intent intent = new Intent(getActivity(), LocationEditActivity.class);
        intent.putExtra(IntentParams.ADD_MODE, false);
        TextView tvRowId = (TextView) v.findViewById(R.id.rowId);
        String stRowId = tvRowId.getText().toString();
        intent.putExtra(IntentParams.ROW_ID, stRowId);
        getActivity().startActivity(intent);
    }


    @Override
    public void onResume(){
        super.onResume();
        getActivity().getSupportLoaderManager().restartLoader(WhoPaysDbAdapter.LOCATION_LOADER_ID, null, this);
        mAdapter.notifyDataSetChanged();
        getListView().invalidateViews();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri uri = WhoPaysContentProvider.CONTENT_URI_LOCATIONS;

        //Get all locations sorted by location name
        return new CursorLoader(getActivity(), uri, null, null, null, WhoPaysDbAdapter.LOC_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor o) {
        switch (loader.getId()) {
            case WhoPaysDbAdapter.LOCATION_LOADER_ID:
                mAdapter.swapCursor(o);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

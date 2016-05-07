package mooreapps.whopays20;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends android.support.v7.app.AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private RelativeLayout mDrawerRelativeLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    String[] mDrawerOptionLabels;
    private static final int FRAG_TRANSACTIONS = 0;
    private static final int FRAG_LOCATIONS = 1;
    private static final int FRAG_USERS = 2;
    private static final int FRAG_TOTALS = 3;
    private static final int FRAG_GROUPS = 4;
    static final String LAST_FRAG = "last_frag";
    public int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            int savedPosition = savedInstanceState.getInt(LAST_FRAG);
            setTitle(getOptionLabels()[savedPosition]);
            mPosition = savedPosition;
        }

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.left_drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, getOptionLabels());

        mDrawerListView.setAdapter(drawerAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.Fragment fragment = new android.support.v4.app.Fragment();
                switch (position) {
                    case FRAG_TRANSACTIONS:
                        fragment = new TransactionsFragment();
                        break;
                    case FRAG_LOCATIONS:
                        fragment = new LocationsFragment();
                        break;
                    case FRAG_USERS:
                        fragment = new UsersFragment();
                        break;
                    case FRAG_TOTALS:
                        fragment = new TotalsFragment();
                        break;
                    case FRAG_GROUPS:
                        fragment = new UserGroupsFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                setTitle(getOptionLabels()[position]);
                mDrawerListView.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerRelativeLayout);
                mPosition = position;
            }
        });

        if (savedInstanceState == null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.Fragment fragment = new TransactionsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            setTitle(getOptionLabels()[0]);
        }
    }

    private String[] getOptionLabels() {
        if (mDrawerOptionLabels == null) {
            Resources resources = getResources();
            mDrawerOptionLabels = resources.getStringArray(R.array.sliding_drawer_array);
        }
        return mDrawerOptionLabels;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }

        savedInstanceState.putInt(LAST_FRAG, mPosition);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getInt(LAST_FRAG);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

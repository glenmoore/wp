package mooreapps.whopays20;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class UserGroupEditActivity extends ActivityBase implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ClearableEditText userGroupName;
    private Spinner people;
    UserAdapter  userAdapter;
    ListView peopleList;
    TextView txtAddPerson;
    TextView peopleText;

    ImageButton buttonAddPerson;
    SimpleCursorAdapter mAdapter;

    private int groupRowId = 0;
    private int xrefRowId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonAddPerson = (ImageButton) findViewById(R.id.buttonAddPerson);

        txtAddPerson = (TextView) findViewById((R.id.txtAddPerson));
        peopleText = (TextView) findViewById((R.id.peopleText));
        userGroupName = (ClearableEditText) findViewById(R.id.userGroupName);
        people = (Spinner) findViewById(R.id.people);

        if (!addMode) {
            String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
            groupRowId = Integer.parseInt(stRowId);
        }

        people.setOnItemSelectedListener(this);
        loadPeople();

        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.listview_user_xref_layout,
                null,
                new String[]{WhoPaysDbAdapter.NAME_FIRST, WhoPaysDbAdapter.NAME_LAST, WhoPaysDbAdapter.KEY_ROWID},
                new int[]{R.id.nameFirst, R.id.nameLast, R.id.rowId}, WhoPaysDbAdapter.USER_GROUP_XREF_LOADER_ID);

        peopleList = (ListView) findViewById(R.id.peopleListView);

        peopleList.setAdapter(mAdapter);

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                xrefRowId = cursor.getInt(cursor.getColumnIndex(WhoPaysDbAdapter.KEY_ROWID));
                deleteUserGroupXref();
            }
        });

        if (addMode) {
            setTitle(R.string.title_activity_user_group_add);
            setVisibity(View.INVISIBLE);
        } else {
            setTitle(R.string.title_activity_user_group_edit);
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            getSupportLoaderManager().initLoader(WhoPaysDbAdapter.USER_GROUP_XREF_LOADER_ID, null, this);
            Cursor cursor = db.getUserGroup(groupRowId);
            UserGroup userGroup = WhoPaysDbAdapter.getUserGroupFromCursor(cursor);
            userGroupName.setText(userGroup.mGroupName);
        }


        buttonAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                insertUserGroupXref();
            }
        });

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_group_edit;
    }

    @Override
    protected int getDeleteMessageId() {
        return R.string.group_delete_confirm;
    }

    private void loadPeople(){
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(getApplicationContext());
        List<User> users = dbAdapter.getAllUsers();
        users = removeUsersAlreadyInGroup(users);
        userAdapter = new UserAdapter(UserGroupEditActivity.this,
                android.R.layout.simple_spinner_item , users );
        people.setAdapter(userAdapter);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAddPersonVisibility();

    }

    private void setAddPersonVisibility() {
        if (people.getCount() == 0) {
            buttonAddPerson.setVisibility(View.INVISIBLE);
        } else {
            buttonAddPerson.setVisibility(View.VISIBLE);
        }
    }

    private List<User> removeUsersAlreadyInGroup(List<User> fullUsersList) {
        List<User> returnUsersList = new ArrayList<User>();
        UserGroupModel ugm = new UserGroupModel(getResources());

        for (int i = 0; i < fullUsersList.size(); i++) {
            User user = fullUsersList.get(i);
            if (!ugm.isUserInGroup(groupRowId, user.getId(), getApplicationContext())) {
                returnUsersList.add(user);
            }
        }

        return returnUsersList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
    }

    @Override
    protected void insert() {
        ContentValues newValues = getUserGroupValues();
        try {
            Uri uri = getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_USER_GROUP, newValues);
            long id = ContentUris.parseId(uri);
            groupRowId = NumberUtil.safeLongToInt(id);
            addMode = false;
            getSupportLoaderManager().initLoader(WhoPaysDbAdapter.USER_GROUP_XREF_LOADER_ID, null, this);
            Toast.makeText(getApplicationContext(), R.string.group_added, Toast.LENGTH_SHORT).show();
            setTitle(R.string.title_activity_user_group_edit);
            setVisibity(View.VISIBLE);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.group_name_duplicate, Toast.LENGTH_LONG).show();
        };
    }

    @Override
    protected ValidationResults validate() {
        ValidationResults vr = new ValidationResults(getResources());

        String groupName = userGroupName.getText().toString().trim();

        if (groupName.length() < 1) {
            vr.addErrorMessage(R.string.group_name_required);
        } else {
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            if (! db.validateGroupName(groupName, groupRowId, addMode)) {
                vr.addErrorMessage(R.string.group_name_duplicate);
            }
        }

        return vr;
    }

    @Override
    protected void delete() {
        if (WhoPaysDbAdapter.getInstance(getApplicationContext()).isUserGroupOnTransaction(groupRowId)) {
            ValidationResults vr = new ValidationResults(getResources());
            vr.addErrorMessage(R.string.group_not_deletable);
            displayMessages(vr);
        } else {
            String selectionArgs[] = new String[]{Integer.toString(groupRowId)};;
            getContentResolver().delete(WhoPaysContentProvider.CONTENT_URI_USER_GROUPS, WhoPaysDbAdapter.SELECT_ID, selectionArgs);
            Toast.makeText(getApplicationContext(), R.string.group_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void update() {
        ContentValues saveValues = getUserGroupValues();
        String selectionArgs[] = new String[] { Integer.toString(groupRowId) };
        String selectionclause = WhoPaysDbAdapter.SELECT_ID;

        try {
            int rowsAffected = getContentResolver().update(WhoPaysContentProvider.CONTENT_URI_USER_GROUP, saveValues, selectionclause, selectionArgs);
            if (rowsAffected == 1) {
                Toast.makeText(getApplicationContext(), R.string.group_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.group_not_updated, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.group_not_updated, Toast.LENGTH_LONG).show();
        }
    }

    private ContentValues getUserGroupValues() {
        ContentValues values = new ContentValues();
        values.put(WhoPaysDbAdapter.GROUP_NAME, userGroupName.getText().toString().trim());
        return values;
    }


    private void insertUserGroupXref() {
        UserGroupModel model = new UserGroupModel(getResources());
        int userId = getSelectedUserId();
        if (model.isUserInGroup(groupRowId, userId, getApplicationContext())) {
            ValidationResults vr = new ValidationResults(getResources());
            vr.addErrorMessage(R.string.user_already_in_group);
            displayMessages(vr);
        } else {
            ContentValues newValues = getUserGroupXrefValues();
            try {
                getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_USER_GROUP_XREFS, newValues);
                refreshUserGroup();

                // Remove inserted user from UserAdapter
                loadPeople();

            } catch (SQLException e) {
                Toast.makeText(getApplicationContext(), R.string.user_not_added_to_group, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void deleteUserGroupXref() {
        // Validate that there are no payments for this person on this group
        UserGroupModel model = new UserGroupModel(getResources());
        UserGroupXref ugx = WhoPaysDbAdapter.getInstance(getApplicationContext()).getUserGroupXrefFromId(xrefRowId);

        if (model.hasUserPaidGroup(groupRowId, ugx.mUserId, getApplicationContext())) {
            ValidationResults vr = new ValidationResults(getResources());
            vr.addErrorMessage(R.string.person_cannot_remove);
            displayMessages(vr);
        } else {
            String selectionArgs[];
            selectionArgs = new String[]{String.valueOf(xrefRowId)};
            getContentResolver().delete(WhoPaysContentProvider.CONTENT_URI_USER_GROUP_XREFS, WhoPaysDbAdapter.SELECT_ID, selectionArgs);
            refreshUserGroup();
            loadPeople();
        }
    }

    private void refreshUserGroup() {
        if (mAdapter.getCursor() != null) {
            mAdapter.getCursor().requery();
            mAdapter.notifyDataSetChanged();
        }
        peopleList.invalidateViews();
        peopleList.refreshDrawableState();
    }

    private ContentValues getUserGroupXrefValues() {
        ContentValues values = new ContentValues();
        values.put(WhoPaysDbAdapter.USER_ID, getSelectedUserId());
        values.put(WhoPaysDbAdapter.GROUP_ID, groupRowId);
        return values;
    }


    private int getSelectedUserId() {
        User selectedUser = new User();
        if (people.getCount() == 0) {
            return 0;
        } else {
            selectedUser = (User) people.getSelectedItem();
            return selectedUser.mId;
        }
    }

    //Hide in add mode, show in edit mode
    private void setVisibity(int visibility) {
        txtAddPerson.setVisibility(visibility);
        buttonAddPerson.setVisibility(visibility);
        people.setVisibility(visibility);
        peopleList.setVisibility(visibility);
        peopleText.setVisibility(visibility);
}


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri uri = WhoPaysContentProvider.CONTENT_URI_USER_GROUP_XREFS_VIEW;

        //Get all xrefs for this group sorted by user ID
        String[] selectionArgs = {String.valueOf(groupRowId)};

        return new CursorLoader(this, uri, null, WhoPaysDbAdapter.GROUP_ID + "=?", selectionArgs, WhoPaysDbAdapter.USER_ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor o) {
        switch (loader.getId()) {
            case WhoPaysDbAdapter.USER_GROUP_XREF_LOADER_ID:
                mAdapter.swapCursor(o);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_group_edit, menu);

        if (addMode) {
            menu.findItem(R.id.action_delete).setVisible(false);
        } else {
            menu.findItem(R.id.action_delete).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_save:
                save();
                return true;
            case R.id.action_delete:
                deletePrompt();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean hasSaveButton() {
        return false;
    }

    protected boolean hasDeleteButton() {
        return false;
    }

    protected boolean hasCancelButton() {
        return false;
    }

}

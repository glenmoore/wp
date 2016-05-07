package mooreapps.whopays20;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

    public class UserEditActivity extends ActivityBase {

    private EditText firstName;
    private EditText lastName;
    private EditText logonId;
    //private EditText phoneCell;

    private int rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        //phoneCell = (EditText) findViewById(R.id.phoneCell);
        logonId = (EditText) findViewById(R.id.logonId);

        if (addMode) {
            setTitle(R.string.title_activity_user_add);
        } else {
            setTitle(R.string.title_activity_user_edit);
            String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            rowId = Integer.parseInt(stRowId);
            Cursor cursor = db.getUser(rowId);
            User user = db.getUserFromCursor(cursor);
            firstName.setText(user.mNameFirst);
            lastName.setText(user.mNameLast);
            logonId.setText(user.mLogonId);
            //String formattedCell = UIUtil.formatPhoneNumber(user.mPhoneCell);
            //phoneCell.setText(formattedCell);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_edit;
    }

    @Override
    protected int getDeleteMessageId() {
        return R.string.user_delete_confirm;
    }

    @Override
    protected void insert() {
        ContentValues newValues = getUserValues();

        try {
            getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_USERS, newValues);
            Toast.makeText(getApplicationContext(), R.string.user_added, Toast.LENGTH_SHORT).show();
            finish();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.user_not_added, Toast.LENGTH_LONG).show();
        };
    }

    @Override
    protected ValidationResults validate() {
        ValidationResults vr = new ValidationResults(getResources());
        String logon = logonId.getText().toString().trim();

        if (logon.length() > 1) {
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            if (! db.validateLogonId(logon, rowId, addMode)) {
                vr.addErrorMessage(R.string.nickname_already_used);
            }
        }

        if (firstName.getText().toString().trim().length() < 1) {
            vr.addErrorMessage(R.string.user_first_name_required);
        }

        if (lastName.getText().toString().trim().length() < 1) {
            vr.addErrorMessage(R.string.user_last_name_required);
        }

        return vr;
    }

    @Override
    protected boolean isDelteable() {
        UserGroupModel model = new UserGroupModel(getResources());
        if (model.isUserInGroup(rowId, getApplicationContext())) {
            ValidationResults vr = new ValidationResults(getResources());
            vr.addErrorMessage(R.string.person_cannot_delete);
            displayMessages(vr);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void delete() {
        String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
        String selectionArgs[];
        selectionArgs = new String[]{stRowId};
        String selectionclause = WhoPaysDbAdapter.SELECT_ID;
        getContentResolver().delete(WhoPaysContentProvider.CONTENT_URI_USERS, selectionclause, selectionArgs);
        Toast.makeText(getApplicationContext(), R.string.user_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void update() {
        ContentValues saveValues = getUserValues();
        String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
        String selectionArgs[];
        selectionArgs = new String[] { stRowId };
        String selectionclause = WhoPaysDbAdapter.SELECT_ID;

        try {
            int rowsAffected = getContentResolver().update(WhoPaysContentProvider.CONTENT_URI_USERS, saveValues, selectionclause, selectionArgs);
            if (rowsAffected == 1) {
                Toast.makeText(getApplicationContext(), R.string.user_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.user_not_updated, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.user_not_updated, Toast.LENGTH_LONG).show();
        }
   }

    private ContentValues getUserValues() {
        ContentValues values = new ContentValues();
        values.put(WhoPaysDbAdapter.NAME_LAST, lastName.getText().toString().trim());
        values.put(WhoPaysDbAdapter.NAME_FIRST, firstName.getText().toString().trim());
        values.put(WhoPaysDbAdapter.NAME_MIDDLE, "");
        values.put(WhoPaysDbAdapter.LOGON_ID, logonId.getText().toString().trim());
        values.put(WhoPaysDbAdapter.LOGON_CRED, "");
        values.put(WhoPaysDbAdapter.NUM, "");
        values.put(WhoPaysDbAdapter.EMAIL_ADDR, "");
        //values.put(WhoPaysDbAdapter.PHONE_CELL, phoneCell.getText().toString());
        values.put(WhoPaysDbAdapter.PHONE_CELL, "");
        values.put(WhoPaysDbAdapter.PHONE_OTHER, "");
        return values;
    }

}

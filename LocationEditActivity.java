package mooreapps.whopays20;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LocationEditActivity extends ActivityBase {

    private EditText locName;
    int rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locName = (EditText) findViewById(R.id.locName);

        if (addMode) {
            setTitle(R.string.title_activity_vendor_add);
        } else {
            setTitle(R.string.title_activity_vendor_edit);
            String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            rowId = Integer.parseInt(stRowId);
            Cursor cursor = db.getLocation(rowId);
            Location location = WhoPaysDbAdapter.getLocationFromCursor(cursor);
            locName.setText(location.mLocName);
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_location_edit;
    }

    @Override
    protected int getDeleteMessageId() {
        return R.string.vendor_delete_confirm;
    }


    @Override
    protected void insert() {
        ContentValues newValues = getLocationValues();
        try {
            getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_LOCATION, newValues);
            Toast.makeText(getApplicationContext(), R.string.vendor_added, Toast.LENGTH_SHORT).show();
            finish();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.vendor_not_added, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected ValidationResults validate() {

        ValidationResults vr = new ValidationResults(getResources());
        String locationName = locName.getText().toString().trim();

        if (locationName.length() < 1) {
            vr.addErrorMessage(R.string.vendor_name_enter);
        } else {
            WhoPaysDbAdapter db = WhoPaysDbAdapter.getInstance(getApplicationContext());
            if (! db.validateLocationName(locationName, rowId, addMode)) {
                vr.addErrorMessage(R.string.vendor_name_duplicate);
            }
        }

        return vr;
    }

    @Override
    protected void delete() {
        String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);

        Integer rowId = new Integer(stRowId);

        if (WhoPaysDbAdapter.getInstance(getApplicationContext()).isLocOnTransaction(rowId)) {
            Toast.makeText(getApplicationContext(), R.string.vendor_delete_fail, Toast.LENGTH_LONG).show();
        } else {
            String selectionArgs[];
            selectionArgs = new String[]{stRowId};
            String selectionclause = WhoPaysDbAdapter.SELECT_ID;
            getContentResolver().delete(WhoPaysContentProvider.CONTENT_URI_LOCATIONS, selectionclause, selectionArgs);
            Toast.makeText(getApplicationContext(), R.string.vendor_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void update() {
        ContentValues saveValues = getLocationValues();
        String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
        String selectionArgs[];
        selectionArgs = new String[] { stRowId };
        String selectionclause = WhoPaysDbAdapter.SELECT_ID;

        try {
            int rowsAffected = getContentResolver().update(WhoPaysContentProvider.CONTENT_URI_LOCATION, saveValues, selectionclause, selectionArgs);
            if (rowsAffected == 1) {
                Toast.makeText(getApplicationContext(), R.string.vendor_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.vendor_not_updated, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e){
            Toast.makeText(getApplicationContext(), R.string.vendor_not_updated, Toast.LENGTH_LONG).show();
        }
    }

    private ContentValues getLocationValues() {
        ContentValues values = new ContentValues();
        values.put(WhoPaysDbAdapter.LOC_NAME, locName.getText().toString().trim());
        return values;
    }

}

package mooreapps.whopays20;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.database.SQLException;
import android.util.Log;

public class WhoPaysContentProvider extends ContentProvider {
    WhoPaysDbAdapter mWhoPaysDbAdapter;
    private String className = "WhoPaysContentProvider";

    // All URIs share these parts
    public static final String AUTHORITY = "mooreapps.whopays20.provider";
    public static final String SCHEME = "content://";

    private static final int USERS = 1;
    private static final int USER_ID = 2;
    private static final int LOCATIONS = 3;
    private static final int LOCATION_ID = 4;
    private static final int TRANSACTIONS = 5;
    private static final int TRANSACTION_ID = 6;
    private static final int PAYMENTS = 7;
    private static final int PAYMENT_ID = 8;
    private static final int PAYMENTS_VIEW = 9;
    private static final int USER_GROUPS = 10;
    private static final int USER_GROUP_ID = 11;
    private static final int USER_GROUP_XREFS = 12;
    private static final int USER_GROUP_XREF_ID = 13;
    private static final int USER_GROUP_XREFS_VIEW = 14;


    // ------- Uris
    private static final String PATH_USERS = "USER_TABLE";
    private static final String PATH_LOCATIONS = "LOCATION_TABLE";
    private static final String PATH_TRANSACTIONS = "TRANSACTION_TABLE";
    private static final String PATH_PAYMENTS = "PAYMENT_TABLE";
    private static final String PATH_PAYMENTS_VIEW = "PAYMENT_VIEW";
    private static final String PATH_USER_GROUPS = "USER_GROUP_TABLE";
    private static final String PATH_USER_GROUP_XREFS = "USER_GROUP_XREF_TABLE";
    private static final String PATH_USER_GROUP_XREFS_VIEW = "USER_GROUPS_XREF_VIEW";

    public static final Uri CONTENT_URI_USERS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USERS);
    public static final Uri CONTENT_URI_LOCATIONS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_LOCATIONS);
    public static final Uri CONTENT_URI_LOCATION = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_LOCATIONS + "/#");
    public static final Uri CONTENT_URI_TRANSACTIONS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_TRANSACTIONS);
    public static final Uri CONTENT_URI_TRANSACTION = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_TRANSACTIONS + "/#");
    public static final Uri CONTENT_URI_PAYMENTS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_PAYMENTS);
    public static final Uri CONTENT_URI_PAYMENT= Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_PAYMENTS + "/#");
    public static final Uri CONTENT_URI_PAYMENTS_VIEW = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_PAYMENTS_VIEW);
    public static final Uri CONTENT_URI_USER_GROUPS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USER_GROUPS);
    public static final Uri CONTENT_URI_USER_GROUP = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USER_GROUPS + "/#");
    public static final Uri CONTENT_URI_USER_GROUP_XREFS = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USER_GROUP_XREFS);
    public static final Uri CONTENT_URI_USER_GROUP_XREF = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USER_GROUP_XREFS + "/#");
    public static final Uri CONTENT_URI_USER_GROUP_XREFS_VIEW = Uri.parse(SCHEME + AUTHORITY
            + "/" + PATH_USER_GROUP_XREFS_VIEW);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, PATH_USERS, USERS);
        sUriMatcher.addURI(AUTHORITY, PATH_USERS + "/#", USER_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_LOCATIONS, LOCATIONS);
        sUriMatcher.addURI(AUTHORITY, PATH_LOCATIONS + "/#", LOCATION_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_TRANSACTIONS, TRANSACTIONS);
        sUriMatcher.addURI(AUTHORITY, PATH_TRANSACTIONS + "/#", TRANSACTION_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_PAYMENTS, PAYMENTS);
        sUriMatcher.addURI(AUTHORITY, PATH_PAYMENTS + "/#", PAYMENT_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_PAYMENTS_VIEW, PAYMENTS_VIEW);
        sUriMatcher.addURI(AUTHORITY, PATH_USER_GROUPS, USER_GROUPS);
        sUriMatcher.addURI(AUTHORITY, PATH_USER_GROUPS + "/#", USER_GROUP_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_USER_GROUP_XREFS, USER_GROUP_XREFS);
        sUriMatcher.addURI(AUTHORITY, PATH_USER_GROUP_XREFS + "/#", USER_GROUP_XREF_ID);
        sUriMatcher.addURI(AUTHORITY, PATH_USER_GROUP_XREFS_VIEW, USER_GROUP_XREFS_VIEW);
    }

    public WhoPaysContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mWhoPaysDbAdapter = WhoPaysDbAdapter.getInstance(getContext());
        mWhoPaysDbAdapter.open();
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int result = 0;

        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case USERS:
                result = mWhoPaysDbAdapter.deleteUser(selection, selectionArgs);
                break;
            case USER_ID:
                result = mWhoPaysDbAdapter.deleteUser(selection, selectionArgs);
                break;
            case LOCATIONS:
                result = mWhoPaysDbAdapter.deleteLocation(selection, selectionArgs);
                break;
            case LOCATION_ID:
                result = mWhoPaysDbAdapter.deleteLocation(selection, selectionArgs);
                break;
            case TRANSACTIONS:
                result = mWhoPaysDbAdapter.deleteTransaction(selection, selectionArgs);
                break;
            case TRANSACTION_ID:
                result = mWhoPaysDbAdapter.deleteTransaction(selection, selectionArgs);
                break;
            case PAYMENTS:
                result = mWhoPaysDbAdapter.deletePayment(selection, selectionArgs);
                break;
            case PAYMENT_ID:
                result = mWhoPaysDbAdapter.deletePayment(selection, selectionArgs);
                break;
            case USER_GROUPS:
                result = mWhoPaysDbAdapter.deleteUserGroup(selection, selectionArgs);
                break;
            case USER_GROUP_ID:
                result = mWhoPaysDbAdapter.deleteUserGroup(selection, selectionArgs);
                break;
            case USER_GROUP_XREFS:
                result = mWhoPaysDbAdapter.deleteUserGroupXref(selection, selectionArgs);
                break;
            case USER_GROUP_XREF_ID:
                result = mWhoPaysDbAdapter.deleteUserGroupXref(selection, selectionArgs);
                break;
            default:
                break;
        }

        return result;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case USERS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.User";
            case USER_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.User";
            case LOCATIONS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.Location";
            case LOCATION_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.Location";
            case TRANSACTIONS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.Transaction";
            case TRANSACTION_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.Transaction";
            case PAYMENTS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.Payment";
            case PAYMENT_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.Payment";
            case PAYMENTS_VIEW:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.Payment";
            case USER_GROUPS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.UserGroup";
            case USER_GROUP_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.UserGroup";
            case USER_GROUP_XREFS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/mooreapps.whopays20.UserGroupXref";
            case USER_GROUP_XREF_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.UserGroupXref";
            case USER_GROUP_XREFS_VIEW:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mooreapps.whopays20.UserGroupXref";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case USERS:
                id = mWhoPaysDbAdapter.insertUser(values);
                break;
            case USER_ID:
                id = mWhoPaysDbAdapter.insertUser(values);
                break;
            case LOCATIONS:
                id = mWhoPaysDbAdapter.insertLocation(values);
                break;
            case LOCATION_ID:
                id = mWhoPaysDbAdapter.insertLocation(values);
                break;
            case TRANSACTIONS:
                id = mWhoPaysDbAdapter.insertTransaction(values);
                break;
            case TRANSACTION_ID:
                id = mWhoPaysDbAdapter.insertTransaction(values);
                break;
            case PAYMENTS:
                id = mWhoPaysDbAdapter.insertPayment(values);
                break;
            case PAYMENT_ID:
                id = mWhoPaysDbAdapter.insertPayment(values);
                break;
            case USER_GROUPS:
                id = mWhoPaysDbAdapter.insertUserGroup(values);
                break;
            case USER_GROUP_ID:
                id = mWhoPaysDbAdapter.insertUserGroup(values);
                break;
            case USER_GROUP_XREFS:
                id = mWhoPaysDbAdapter.insertUserGroupXref(values);
                break;
            case USER_GROUP_XREF_ID:
                id = mWhoPaysDbAdapter.insertUserGroupXref(values);
                break;
            default:
                break;
        }

        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, id);
            return newUri;
        }
        else {
            throw new SQLException("Failed to insert row into " + uri);
        }

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case USERS:
                cursor =  mWhoPaysDbAdapter.queryUsers(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case USER_ID:
                cursor =  mWhoPaysDbAdapter.getUser(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case LOCATIONS:
                cursor =  mWhoPaysDbAdapter.queryLocations(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case LOCATION_ID:
                cursor =  mWhoPaysDbAdapter.getLocation(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case TRANSACTIONS:
                cursor =  mWhoPaysDbAdapter.queryTransactions(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case TRANSACTION_ID:
                cursor =  mWhoPaysDbAdapter.getTransaction(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case PAYMENTS:
                cursor =  mWhoPaysDbAdapter.queryPayments(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case PAYMENT_ID:
                cursor =  mWhoPaysDbAdapter.getPayment(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case PAYMENTS_VIEW:
                cursor =  mWhoPaysDbAdapter.queryPaymentsView(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case USER_GROUPS:
                cursor =  mWhoPaysDbAdapter.queryUserGroups(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case USER_GROUP_ID:
                cursor =  mWhoPaysDbAdapter.getUserGroup(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case USER_GROUP_XREFS:
                cursor =  mWhoPaysDbAdapter.queryUserGroupXrefs(projection, selection,
                        selectionArgs, sortOrder);
                break;
            case USER_GROUP_XREF_ID:
                cursor =  mWhoPaysDbAdapter.getUserGroupXref(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case USER_GROUP_XREFS_VIEW:
                cursor =  mWhoPaysDbAdapter.queryUserGroupXrefsView(projection, selection,
                        selectionArgs, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        if (cursor == null) {
            UIUtil.logBold(className, "query", "Cursor is null");
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int  result = 0;
        int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case USERS:
                result =  mWhoPaysDbAdapter.updateUser(selection, selectionArgs, values);
                break;
            case USER_ID:
                result =  mWhoPaysDbAdapter.updateUser(selection, selectionArgs, values);
                break;
            case LOCATIONS:
                result =  mWhoPaysDbAdapter.updateLocation(selection, selectionArgs, values);
                break;
            case LOCATION_ID:
                result =  mWhoPaysDbAdapter.updateLocation(selection, selectionArgs, values);
                break;
            case TRANSACTIONS:
                result =  mWhoPaysDbAdapter.updateTransaction(selection, selectionArgs, values);
                break;
            case TRANSACTION_ID:
                result =  mWhoPaysDbAdapter.updateTransaction(selection, selectionArgs, values);
                break;
            case PAYMENTS:
                result =  mWhoPaysDbAdapter.updatePayment(selection, selectionArgs, values);
                break;
            case PAYMENT_ID:
                result =  mWhoPaysDbAdapter.updatePayment(selection, selectionArgs, values);
                break;
            case USER_GROUPS:
                result =  mWhoPaysDbAdapter.updateUserGroup(selection, selectionArgs, values);
                break;
            case USER_GROUP_ID:
                result =  mWhoPaysDbAdapter.updateUserGroup(selection, selectionArgs, values);
                break;
            case USER_GROUP_XREFS:
                result =  mWhoPaysDbAdapter.updateUserGroupXref(selection, selectionArgs, values);
                break;
            case USER_GROUP_XREF_ID:
                result =  mWhoPaysDbAdapter.updateUserGroupXref(selection, selectionArgs, values);
                break;
            default:
                UIUtil.logBold(className, "update", " NO MATCH uriType = " + uriType);
                break;
        }

        return result;
    }
}

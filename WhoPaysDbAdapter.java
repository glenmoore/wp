package mooreapps.whopays20;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//Singleton for DB access
public class WhoPaysDbAdapter extends SQLiteOpenHelper {

    private static WhoPaysDbAdapter sInstance;

    private String className = this.getClass().getName();

    public static final String SELECT_ID = "_ID = ?";
    private static final String DESCENDING = " DESC";

    public static final int USER_LOADER_ID = 1;
    public static final int LOCATION_LOADER_ID = 2;
    public static final int USER_GROUP_LOADER_ID = 3;
    public static final int USER_GROUP_XREF_LOADER_ID = 4;

    private static final String DATABASE_NAME = "WHO_PAYS.db";
    private static final String USER_TABLE = "USER_TABLE";
    public static final String LOCATION_TABLE = "LOCATION_TABLE";
    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String PAYMENT_TABLE = "PAYMENT_TABLE";
    public static final String PAYMENT_VIEW = "PAYMENT_VIEW";
    public static final String USER_GROUP_TABLE = "USER_GROUP_TABLE";
    public static final String USER_GROUP_XREF_TABLE = "USER_GROUP_XREF_TABLE";
    public static final String USER_GROUP_XREFS_VIEW = "USER_GROUP_XREFS_VIEW";
    public static final String TRANS_PAYMENT_VIEW = "TRANS_PAYMENT_VIEW";

    private static final int DATABASE_VERSION = 500;
    private final Context mCtx;
    public static String TAG = WhoPaysDbAdapter.class.getSimpleName();

    SQLiteDatabase mDb;

    //User Column Names
    public static final String KEY_ROWID = "_id";
    public static final String NAME_LAST = "name_last";
    public static final String NAME_FIRST = "name_first";
    public static final String NAME_MIDDLE = "name_middle";
    public static final String LOGON_ID = "logon_id";
    public static final String LOGON_CRED = "logon_cred";
    public static final String NUM = "num";
    public static final String EMAIL_ADDR = "email_addr";
    public static final String PHONE_CELL = "phone_cell";
    public static final String PHONE_OTHER = "phone_other";

    //Location Column Names
    public static final String LOC_NAME = "loc_name";

    //Transaction Column Names
    public static final String TRANS_DATE = "trans_date";
    public static final String TRANS_AMOUNT = "trans_amount";
    public static final String LOCATION_ID = "location_id";
    public static final String GROUP_ID = "group_id";
    public static final String TRANS_NOTE = "trans_note";
    public static final String TRANS_PAID_AMOUNT = "trans_paid_amount";
    public static final String TRANS_STATUS = "trans_status";


    //Payment Column Names
    public static final String USER_ID = "user_id";
    public static final String TRANS_ID = "trans_id";
    public static final String PAY_AMOUNT = "pay_amount";

    //User Group Column Names
    public static final String GROUP_NAME = "group_name";

    public static final String[] USER_FIELDS = new String[]{
            KEY_ROWID,
            NAME_LAST,
            NAME_FIRST,
            NAME_MIDDLE,
            LOGON_ID,
            LOGON_CRED,
            NUM,
            EMAIL_ADDR,
            PHONE_CELL,
            PHONE_OTHER
    };

    public static final String[] LOCATION_FIELDS = new String[]{
            KEY_ROWID,
            LOC_NAME
    };

    public static final String[] TRANSACTION_FIELDS = new String[]{
            KEY_ROWID,
            TRANS_DATE,
            TRANS_AMOUNT,
            LOCATION_ID,
            GROUP_ID,
            TRANS_NOTE,
            TRANS_PAID_AMOUNT,
            TRANS_STATUS
    };

    public static final String[] PAYMENT_FIELDS = new String[]{
            KEY_ROWID,
            USER_ID,
            TRANS_ID,
            PAY_AMOUNT
    };

    public static final String[] USER_GROUP_FIELDS = new String[]{
            KEY_ROWID,
            GROUP_NAME
    };

    public static final String[] USER_GROUP_XREF_FIELDS = new String[]{
            KEY_ROWID,
            GROUP_ID,
            USER_ID
    };


    private static final String CREATE_TABLE_USER =
            "create table " + USER_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NAME_LAST + " text,"
                    + NAME_FIRST + " text,"
                    + NAME_MIDDLE + " text,"
                    + LOGON_ID + " text,"
                    + LOGON_CRED + " text,"
                    + NUM + " text,"
                    + EMAIL_ADDR + " text,"
                    + PHONE_CELL + " text,"
                    + PHONE_OTHER + " text"
                    + ");";


    private static final String CREATE_TABLE_LOCATION =
            "create table " + LOCATION_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + LOC_NAME + " not null UNIQUE"
                    + ");";

    private static final String CREATE_TABLE_TRANSACTION =
            "create table " + TRANSACTION_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TRANS_DATE + " INTEGER,"
                    + TRANS_AMOUNT + " INTEGER,"
                    + LOCATION_ID + " INTEGER,"
                    + GROUP_ID + " INTEGER,"
                    + TRANS_NOTE + " text,"
                    + TRANS_PAID_AMOUNT + " INTEGER,"
                    + TRANS_STATUS + " INTEGER"
                    + ");";

    private static final String CREATE_TABLE_PAYMENT =
            "create table " + PAYMENT_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USER_ID + " INTEGER,"
                    + TRANS_ID + " INTEGER,"
                    + PAY_AMOUNT + " INTEGER"
                    + ");";


    private static String CREATE_PAYMENT_VIEW =
            "create view " + PAYMENT_VIEW + " AS SELECT "
                + PAYMENT_TABLE + "." + KEY_ROWID + " AS " + KEY_ROWID + ", "
                + PAYMENT_TABLE + "." + USER_ID + " AS " + USER_ID + ", "
                + PAYMENT_TABLE + "." + TRANS_ID + " AS " + TRANS_ID + ", "
                + PAYMENT_TABLE + "." + PAY_AMOUNT + " AS " + PAY_AMOUNT + ", "
                + USER_TABLE + "." + NAME_LAST + " AS " + NAME_LAST + ", "
                + USER_TABLE + "." + NAME_FIRST + " AS " + NAME_FIRST + ", "
                + USER_TABLE + "." + LOGON_ID + " AS " + LOGON_ID + ", "
                + USER_TABLE + "." + PHONE_CELL + " AS " + PHONE_CELL + ", "
                + USER_TABLE + "." + EMAIL_ADDR + " AS " + EMAIL_ADDR
                + " FROM " + PAYMENT_TABLE + " JOIN " + USER_TABLE
                + " ON " + PAYMENT_TABLE + "." + USER_ID + " = " + USER_TABLE + "." + KEY_ROWID + ";";


    private static final String CREATE_TABLE_USER_GROUP =
            "create table " + USER_GROUP_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GROUP_NAME + " not null UNIQUE"
                    + ");";

    private static final String CREATE_TABLE_USER_GROUP_XREF =
            "create table " + USER_GROUP_XREF_TABLE + "("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GROUP_ID + " INTEGER,"
                    + USER_ID + " INTEGER"
                    + ");";


    private static String CREATE_USER_GROUP_XREFS_VIEW =
            "create view " + USER_GROUP_XREFS_VIEW + " AS SELECT "
                    + USER_GROUP_XREF_TABLE + "." + KEY_ROWID + " AS " + KEY_ROWID + ", "
                    + USER_GROUP_XREF_TABLE + "." + GROUP_ID + " AS " + GROUP_ID + ", "
                    + USER_GROUP_XREF_TABLE + "." + USER_ID + " AS " + USER_ID + ", "
                    + USER_TABLE + "." + NAME_LAST + " AS " + NAME_LAST + ", "
                    + USER_TABLE + "." + NAME_FIRST + " AS " + NAME_FIRST + ", "
                    + USER_TABLE + "." + LOGON_ID + " AS " + LOGON_ID + ", "
                    + USER_TABLE + "." + PHONE_CELL + " AS " + PHONE_CELL + ", "
                    + USER_TABLE + "." + EMAIL_ADDR + " AS " + EMAIL_ADDR
                    + " FROM " + USER_GROUP_XREF_TABLE + " JOIN " + USER_TABLE
                    + " ON " + USER_GROUP_XREF_TABLE + "." + USER_ID + " = " + USER_TABLE + "." + KEY_ROWID + ";";


    private static String CREATE_TRANS_PAYMENT_VIEW =
            "create view " + TRANS_PAYMENT_VIEW + " AS SELECT "
                    + PAYMENT_TABLE + "." + KEY_ROWID + " AS " + KEY_ROWID + ", "
                    + PAYMENT_TABLE + "." + USER_ID + " AS " + USER_ID + ", "
                    + PAYMENT_TABLE + "." + TRANS_ID + " AS " + TRANS_ID + ", "
                    + PAYMENT_TABLE + "." + PAY_AMOUNT + " AS " + PAY_AMOUNT + ", "
                    + TRANSACTION_TABLE + "." + TRANS_DATE + " AS " + TRANS_DATE + ", "
                    + TRANSACTION_TABLE + "." + TRANS_PAID_AMOUNT + " AS " + TRANS_PAID_AMOUNT + ", "
                    + TRANSACTION_TABLE + "." + TRANS_STATUS + " AS " + TRANS_STATUS + ", "
                    + TRANSACTION_TABLE + "." + LOCATION_ID + " AS " + LOCATION_ID + ", "
                    + TRANSACTION_TABLE + "." + GROUP_ID + " AS " + GROUP_ID
                    + " FROM " + PAYMENT_TABLE + " JOIN " + TRANSACTION_TABLE
                    + " ON " + PAYMENT_TABLE + "." + TRANS_ID + " = " + TRANSACTION_TABLE + "." + KEY_ROWID + ";";

    public static synchronized WhoPaysDbAdapter getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new WhoPaysDbAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private WhoPaysDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        UIUtil.logBold("WPDBA", "onCreate", "****");

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_PAYMENT);
        db.execSQL(CREATE_PAYMENT_VIEW);
        db.execSQL(CREATE_TABLE_USER_GROUP);
        db.execSQL(CREATE_TABLE_USER_GROUP_XREF);
        db.execSQL(CREATE_USER_GROUP_XREFS_VIEW);
        db.execSQL(CREATE_TRANS_PAYMENT_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ToDo reorder based on foreign keys
        /*
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PAYMENT_TABLE);
        db.execSQL("DROP VIEW IF EXISTS " + PAYMENT_VIEW);
        db.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_XREF_TABLE);
        db.execSQL("DROP VIEW IF EXISTS " + USER_GROUP_XREFS_VIEW);
        db.execSQL("DROP VIEW IF EXISTS " + TRANS_PAYMENT_VIEW);
        onCreate(db);
        */
    }

    public WhoPaysDbAdapter open() throws SQLException {
        mDb = getWritableDatabase();


        //Remove after clear
        /*
        mDb.execSQL("DROP VIEW IF EXISTS " + PAYMENT_VIEW);
        mDb.execSQL("DROP VIEW IF EXISTS " + USER_GROUP_XREFS_VIEW);
        mDb.execSQL("DROP VIEW IF EXISTS " + TRANS_PAYMENT_VIEW);
        mDb.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + PAYMENT_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_TABLE);
        mDb.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_XREF_TABLE);
        */

        //Create tables/views if needed
        createTable(USER_TABLE, CREATE_TABLE_USER);
        createTable(LOCATION_TABLE, CREATE_TABLE_LOCATION);
        createTable(TRANSACTION_TABLE, CREATE_TABLE_TRANSACTION);
        createTable(PAYMENT_TABLE, CREATE_TABLE_PAYMENT);
        createTable(USER_GROUP_TABLE, CREATE_TABLE_USER_GROUP);
        createTable(USER_GROUP_XREF_TABLE, CREATE_TABLE_USER_GROUP_XREF);
        createTable(PAYMENT_VIEW, CREATE_PAYMENT_VIEW);
        createTable(USER_GROUP_XREFS_VIEW, CREATE_USER_GROUP_XREFS_VIEW);
        createTable(TRANS_PAYMENT_VIEW, CREATE_TRANS_PAYMENT_VIEW);

        return this;
    }


    private void createTable(String tableName, String createTableSQL) throws SQLException {
        //Create table/view if it does not exist
        if  (! isTableExists(tableName)) {
            UIUtil.log(className, "open", "CREATING " + tableName + " TABLE");
            mDb.execSQL(createTableSQL);
        }
    }


    public void close() {
        super.close();
    }

    public void upgrade() throws SQLException {
        getWritableDatabase();
    }

    //
    //USER TABLE
    //
    public long insertUser(ContentValues initialValues) {
        return mDb.insertWithOnConflict(USER_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public int updateUser(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(USER_TABLE, newValues, selection, selectionArgs);
    }

    public int deleteUser(String selection, String[] selectionArgs) {
        return mDb.delete(USER_TABLE, selection, selectionArgs);
    }

    public Cursor getUsers() {
        return mDb.query(USER_TABLE, USER_FIELDS, null, null, null, null, NAME_FIRST + "," + NAME_LAST);
    }

    public Cursor getUser(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(USER_TABLE, USER_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    private Cursor getUser(String logonId) {
        String[] selectionArgs = {logonId};
        return mDb.query(USER_TABLE, USER_FIELDS, LOGON_ID + "=?", selectionArgs, null, null, null);
    }


    public Cursor queryUsers(String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {

        return mDb.query(USER_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
    }


    public User getUserFromCursor(Cursor cursor) {
        User user = new User();
        if (cursor.moveToFirst()) {
            user = loadUser(user, cursor);
        }
        return (user);
    }

    // return true if logonID is NOT already used
    public boolean validateLogonId(String logonId, int rowId, boolean addMode) {

        User user = getUserFromCursor(getUser(logonId));

        if (addMode) {
            if (user == null) {
                return true;
            } else {
                return !(logonId.equals(user.mLogonId));
            }
        } else {
            return !(logonId.equals(user.mLogonId) && rowId != user.getId());
        }

    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        Cursor c = getUsers();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            User user = new User();
            user = loadUser(user, c);
            users.add(user);
        }
        return users;
    }

    private User loadUser(User user, Cursor cursor) {
        user.mId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
        user.mNameLast = cursor.getString(cursor.getColumnIndex(NAME_LAST));
        user.mNameFirst = cursor.getString(cursor.getColumnIndex(NAME_FIRST));
        user.mNameMiddle = cursor.getString(cursor.getColumnIndex(NAME_MIDDLE));
        user.mLogonId = cursor.getString(cursor.getColumnIndex(LOGON_ID));
        user.mLogonCred = cursor.getString(cursor.getColumnIndex(LOGON_CRED));
        user.mNum = cursor.getString(cursor.getColumnIndex(NUM));
        user.mEmailAddr = cursor.getString(cursor.getColumnIndex(EMAIL_ADDR));
        user.mPhoneCell = cursor.getString(cursor.getColumnIndex(PHONE_CELL));
        user.mPhoneOther = cursor.getString(cursor.getColumnIndex(PHONE_OTHER));
        return user;
    }

    public User getOneUser(int userId) {
        return getUserFromCursor(getUser(userId));
    }

    public long getUserCount() {
        return DatabaseUtils.queryNumEntries(mDb, USER_TABLE);
    }

    //
    //LOCATION TABLE
    //
    // return true if group name is NOT already used
    public boolean validateLocationName(String locName, int rowId, boolean addMode) {

        Location location = getLocationFromCursor(getLocation(locName));

        if (addMode) {
            if (location == null) {
                return true;
            } else {
                return !(locName.equals(location.getLocName()));
            }
        } else {
            return !(locName.equals(location.getLocName()) && rowId != location.getId());
        }

    }

    public int deleteLocation(String selection, String[] selectionArgs) {
        return mDb.delete(LOCATION_TABLE, selection, selectionArgs);
    }

    public Cursor getLocations() {
        return mDb.query(LOCATION_TABLE, LOCATION_FIELDS, null, null, null, null, null);
    }

    public Cursor getLocation(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(LOCATION_TABLE, LOCATION_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    private Cursor getLocation(String locName) {
        String[] selectionArgs = {locName};
        return mDb.query(LOCATION_TABLE, LOCATION_FIELDS, LOC_NAME + "=?", selectionArgs, null, null, null);
    }


    public Cursor queryLocations(String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        return mDb.query(LOCATION_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }


    public static Location getLocationFromCursor(Cursor cursor) {
        Location location = new Location();
        if (cursor.moveToFirst()) {
            location.mId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
            location.mLocName = cursor.getString(cursor.getColumnIndex(LOC_NAME));
        }
        return (location);
    }

    public long insertLocation(ContentValues initialValues) {
        return mDb.insertWithOnConflict(LOCATION_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int updateLocation(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(LOCATION_TABLE, newValues, selection, selectionArgs);

    }

    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<Location>();
        Cursor c = getLocations();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Location location = new Location();
            location.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            location.mLocName = c.getString(c.getColumnIndex(LOC_NAME));
            locations.add(location);
        }
        return locations;
    }

    public Location getOneLocation(int locId) {
        return getLocationFromCursor(getLocation(locId));
    }

    public long getLocationCount() {
        return DatabaseUtils.queryNumEntries(mDb, LOCATION_TABLE);
    }





    //
    //TRANSACTION TABLE
    //
    public int deleteTransaction(String selection, String[] selectionArgs) {
        return mDb.delete(TRANSACTION_TABLE, selection, selectionArgs);
    }

    public Cursor getTransactions() {
        return mDb.query(TRANSACTION_TABLE, TRANSACTION_FIELDS, null, null, null, null, TRANS_DATE + DESCENDING + "," + KEY_ROWID + DESCENDING);
    }

    public Cursor getTransaction(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(TRANSACTION_TABLE, TRANSACTION_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);
    }

    public Cursor queryTransactions(String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
       return mDb.query(TRANSACTION_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
    }


    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        Cursor c = getTransactions();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Transaction transaction = new Transaction();
            transaction = loadTransaction(transaction, c);
            transactions.add(transaction);
        }
        return transactions;
    }


    public Transaction getTransactionFromCursor(Cursor cursor){
        Transaction transaction = new Transaction();

        if (cursor.moveToFirst()) {
            transaction = loadTransaction(transaction, cursor);
        }
        return (transaction);
    }


    private Transaction loadTransaction(Transaction transaction, Cursor cursor) {
        transaction.mId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
        String strTransDate = cursor.getString(cursor.getColumnIndex(TRANS_DATE));
        transaction.mTransDate = DateUtil.parseDate(strTransDate);
        String strAmtRaw = cursor.getString(cursor.getColumnIndex(TRANS_AMOUNT));
        transaction.mTransAmount = CurrencyUtil.unformatAmountBigDecimal(strAmtRaw);
        String strLocId = cursor.getString(cursor.getColumnIndex(LOCATION_ID));
        transaction.mLocationId = Integer.valueOf(strLocId);
        String strGroupId = cursor.getString(cursor.getColumnIndex(GROUP_ID));
        transaction.mGroupId = Integer.valueOf(strGroupId);
        transaction.mTransNote = cursor.getString(cursor.getColumnIndex(TRANS_NOTE));
        String strPaidAmtRaw = cursor.getString(cursor.getColumnIndex(TRANS_PAID_AMOUNT));
        transaction.mTransPaidAmount = CurrencyUtil.unformatAmountBigDecimal(strPaidAmtRaw);
        String strStatus = cursor.getString(cursor.getColumnIndex(TRANS_STATUS));
        transaction.mTransStatus = NumberUtil.stringToInteger(strStatus);

        return transaction;
    }


    public  long insertTransaction(ContentValues initialValues) {
        return mDb.insertWithOnConflict(TRANSACTION_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int updateTransaction(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(TRANSACTION_TABLE, newValues, selection, selectionArgs);
    }

    // Return true if location ID is on a Transaction
    public boolean isLocOnTransaction (Integer locId) {

        String strLocId = locId.toString();
        String selection = LOCATION_ID + "=?";
        String[] selectionArgs = new String[]{strLocId};

        Cursor cursor = mDb.query(TRANSACTION_TABLE, null, selection,
                selectionArgs, null, null, null);

        int count = cursor.getCount();
        cursor.close();

        return ! (count < 1);

    }

    // Return true if user group ID is on a Transaction
    public boolean isUserGroupOnTransaction (Integer userGroupId) {
        String strUserGroupId = userGroupId.toString();
        String selection = GROUP_ID + "=?";
        String[] selectionArgs = new String[]{strUserGroupId};

        Cursor cursor = mDb.query(TRANSACTION_TABLE, null, selection,
                selectionArgs, null, null, null);

        int count = cursor.getCount();
        cursor.close();

        return ! (count < 1);
    }



    //
    //PAYMENT TABLE
    //
    public boolean deletePayment(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.delete(PAYMENT_TABLE, KEY_ROWID + "=?", selectionArgs) > 0;
    }

    public int deletePayment(String selection, String[] selectionArgs) {
        return mDb.delete(PAYMENT_TABLE, selection, selectionArgs);
    }

    public Cursor getPayment(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(PAYMENT_TABLE, PAYMENT_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    public Cursor queryPayments(String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
        return mDb.query(PAYMENT_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    public Cursor queryPaymentsView(String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {
        return mDb.query(PAYMENT_VIEW, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    public Cursor queryTransPaymentView(String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
        return mDb.query(TRANS_PAYMENT_VIEW, projection, selection,
                selectionArgs, null, null, sortOrder);
    }


    public ArrayList<Payment> getTransactionPayments(int transactionId) {
        ArrayList<Payment> payments = new ArrayList<Payment>();
        String[] selectionArgs = {String.valueOf(transactionId)};
        Cursor c = queryPayments(null, TRANS_ID + "=?", selectionArgs, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Payment payment = new Payment();
            payment.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            payment.setPayAmount(CurrencyUtil.unformatAmountBigDecimal(c.getString(c.getColumnIndex(PAY_AMOUNT))));
            payment.mUserId = Integer.valueOf(c.getString(c.getColumnIndex(USER_ID)));
            payment.mTransId = Integer.valueOf(c.getString(c.getColumnIndex(TRANS_ID)));
            payments.add(payment);
        }

        return payments;
    }

    // Has a user made payments to particular group?
    public int getUserGroupPaymentCount(int groupId, int userId) {
        String selection = GROUP_ID + "=? AND " + USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId), String.valueOf(userId)};
        return queryTransPaymentView(null, selection, selectionArgs, null).getCount();
    }


    public ArrayList<Payment> getGroupPayments(int userGroupId) {
        ArrayList<Payment> payments = new ArrayList<Payment>();

        String[] selectionArgs = {String.valueOf(userGroupId)};
        Cursor c = queryTransPaymentView(null, GROUP_ID + "=?", selectionArgs, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Payment payment = new Payment();
            payment.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            payment.setPayAmount(CurrencyUtil.unformatAmountBigDecimal(c.getString(c.getColumnIndex(PAY_AMOUNT))));
            payment.mUserId = Integer.valueOf(c.getString(c.getColumnIndex(USER_ID)));
            payment.mTransId = Integer.valueOf(c.getString(c.getColumnIndex(TRANS_ID)));
            payments.add(payment);
        }
        return payments;
    }

    public ArrayList<Payment> getBillPayments(int transactionId) {
        ArrayList<Payment> payments = new ArrayList<Payment>();

        String[] selectionArgs = {String.valueOf(transactionId)};
        Cursor c = queryTransPaymentView(null, TRANS_ID + "=?", selectionArgs, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Payment payment = new Payment();
            payment.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            payment.setPayAmount(CurrencyUtil.unformatAmountBigDecimal(c.getString(c.getColumnIndex(PAY_AMOUNT))));
            payment.mUserId = Integer.valueOf(c.getString(c.getColumnIndex(USER_ID)));
            payment.mTransId = Integer.valueOf(c.getString(c.getColumnIndex(TRANS_ID)));
            payments.add(payment);
        }
        return payments;
    }



    public  long insertPayment(ContentValues initialValues) {
        return mDb.insertWithOnConflict(PAYMENT_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int updatePayment(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(PAYMENT_TABLE, newValues, selection, selectionArgs);
    }


    // Get total amount paid against a bill
    public BigDecimal getTotalBillPayments(int billId) {
        String[] selectionArgs = {String.valueOf(billId)};
        Cursor c = queryPayments(null, TRANS_ID + "=?", selectionArgs, null);
        BigDecimal paidAmount = new BigDecimal(0);
        BigDecimal currentPayment = new BigDecimal(0);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                currentPayment = new BigDecimal(c.getString(c.getColumnIndex(PAY_AMOUNT)));
            } catch (NumberFormatException e) {
                currentPayment = new BigDecimal(0);
            }
            paidAmount = paidAmount.add(currentPayment);
        }

        return paidAmount;
    }


    // Maintain payments for bill
    boolean maintainBillPayments(List<Payment> payments) {

        if (payments == null || payments.get(0) == null) {
            return false;
        }

        // first delete all payments for bill
        Payment payment = payments.get(0);
        String[] selectionArgs = {payment.mTransId.toString()};
        deletePayment(TRANS_ID + "=?", selectionArgs);

        // insert payments
        for (int i=0; i<payments.size(); i++){
            payment = payments.get(i);
            if (payment != null && payment.getPayAmount() != null && payment.getPayAmount().compareTo(BigDecimal.ZERO) != 0) {
                insertPayment(getPaymentValues(payment));
            }
        }

        return true;

    }

    private ContentValues getPaymentValues(Payment payment) {
        ContentValues values = new ContentValues();
        String strAmt = payment.getPayAmount().toString();
        String strAmt2 = CurrencyUtil.unformatAmount(strAmt);
        values.put(WhoPaysDbAdapter.PAY_AMOUNT, strAmt2);
        values.put(WhoPaysDbAdapter.USER_ID, payment.mUserId);
        values.put(WhoPaysDbAdapter.TRANS_ID, payment.mTransId);
        return values;
    }



    //
    //USER GROUP TABLE
    //

    public int deleteUserGroup(String selection, String[] selectionArgs) {
        String strGroupId = selectionArgs[0];
        Integer groupId = NumberUtil.stringToInteger(strGroupId);
        deleteUserGroupXrefs(groupId);
        return mDb.delete(USER_GROUP_TABLE, selection, selectionArgs);
    }

    public Cursor getUserGroups() {
        return mDb.query(USER_GROUP_TABLE, USER_GROUP_FIELDS, null, null, null, null, GROUP_NAME);
    }

    public Cursor getUserGroup(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(USER_GROUP_TABLE, USER_GROUP_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    private Cursor getUserGroup(String groupName) {
        String[] selectionArgs = {groupName};
        return mDb.query(USER_GROUP_TABLE, USER_GROUP_FIELDS, GROUP_NAME + "=?", selectionArgs, null, null, null);
    }


    public Cursor queryUserGroups(String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        return mDb.query(USER_GROUP_TABLE, projection, selection,
                selectionArgs, null, null, GROUP_NAME); // Override. Always group name
    }


    public static UserGroup getUserGroupFromCursor(Cursor cursor) {
        UserGroup userGroup = new UserGroup();
        if (cursor.moveToFirst()) {
            userGroup.mId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
            userGroup.mGroupName = cursor.getString(cursor.getColumnIndex(GROUP_NAME));
        }
        return (userGroup);
    }

    public long insertUserGroup(ContentValues initialValues) {
        return mDb.insertWithOnConflict(USER_GROUP_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int updateUserGroup(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(USER_GROUP_TABLE, newValues, selection, selectionArgs);
    }

    // return true if group name is NOT already used
    public boolean validateGroupName(String groupName, int rowId, boolean addMode) {

        UserGroup userGroup = getUserGroupFromCursor(getUserGroup(groupName));

        if (addMode) {
            if (userGroup == null) {
                return true;
            } else {
                return !(groupName.equals(userGroup.getGroupName()));
            }
        } else {
            return !(groupName.equals(userGroup.getGroupName()) && rowId != userGroup.getId());
        }

    }


    public List<UserGroup> getAllUserGroups() {
        List<UserGroup> userGroups = new ArrayList<UserGroup>();
        Cursor c = getUserGroups();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // REDUNDANT CODE. NEED TO SHARE WITH getUserGroups()
            UserGroup userGroup = new UserGroup();
            userGroup.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            userGroup.mGroupName = c.getString(c.getColumnIndex(GROUP_NAME));
            userGroups.add(userGroup);
        }
        return userGroups;
    }

    public UserGroup getOneUserGroup(int grpId) {
        return getUserGroupFromCursor(getUserGroup(grpId));
    }

    public long getUserGroupCount() {
        return DatabaseUtils.queryNumEntries(mDb, USER_GROUP_TABLE);
    }




    //
    //USER GROUP XREF TABLE
    //
    public int deleteUserGroupXref(String selection, String[] selectionArgs) {
        return mDb.delete(USER_GROUP_XREF_TABLE, selection, selectionArgs);
    }

    public Cursor getUserGroupXrefs() {
        return mDb.query(USER_GROUP_XREF_TABLE, USER_GROUP_XREF_FIELDS, null, null, null, null, null);
    }

    public Cursor getUserGroupXref(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.query(USER_GROUP_XREF_TABLE, USER_GROUP_XREF_FIELDS, KEY_ROWID + "=?", selectionArgs, null, null, null);

    }

    public Cursor queryUserGroupXrefs(String[] projection, String selection,
                                String[] selectionArgs, String sortOrder) {

        return mDb.query(USER_GROUP_XREF_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    public  long insertUserGroupXref(ContentValues initialValues) {
        return mDb.insertWithOnConflict(USER_GROUP_XREF_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int updateUserGroupXref(String selection, String[] selectionArgs, ContentValues newValues) {
        return mDb.update(USER_GROUP_XREF_TABLE, newValues, selection, selectionArgs);
    }

    public Cursor queryUserGroupXrefsView(String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {

        return mDb.query(USER_GROUP_XREFS_VIEW, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    // How many times is a user in a particular group?
    public int getUserGroupCount(int groupId, int userId) {
        String selection = GROUP_ID + "=? AND " + USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId), String.valueOf(userId)};
        return queryUserGroupXrefs(null, selection, selectionArgs, null).getCount();
    }

    // How many times is a user in any group?
    public int getUserGroupCount(int userId) {
        String selection = USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};


        //getAllUserGroupXrefs(); // remove later (debug)


        return queryUserGroupXrefs(null, selection, selectionArgs, null).getCount();
    }

    public UserGroupXref getUserGroupXrefFromId(int id) {
        Cursor cursor = getUserGroupXref(id);
        UserGroupXref userGroupXref = new UserGroupXref();
        if (cursor.moveToFirst()) {
            userGroupXref.mId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
            userGroupXref.mGroupId = cursor.getInt(cursor.getColumnIndex(GROUP_ID));
            userGroupXref.mUserId = cursor.getInt(cursor.getColumnIndex(USER_ID));
        }
        return (userGroupXref);
    }

    //Just for debug
    public List<UserGroupXref> getAllUserGroupXrefs() {
        List<UserGroupXref> userGroupXrefs = new ArrayList<UserGroupXref>();
        Cursor c = getUserGroupXrefs();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            UserGroupXref userGroupXref = new UserGroupXref();
            userGroupXref.mId = c.getInt(c.getColumnIndex(KEY_ROWID));
            userGroupXref.mGroupId = Integer.valueOf(c.getString(c.getColumnIndex(GROUP_ID)));
            userGroupXref.mUserId = Integer.valueOf(c.getString(c.getColumnIndex(USER_ID)));
            UIUtil.logBold("WPDBA", "getAllUserGroupXrefs","userGroupXref="+userGroupXref);
        }
        return userGroupXrefs;
    }

    public void deleteUserGroupXrefs(int groupId) {
        String selection = GROUP_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId)};
        deleteUserGroupXref(selection, selectionArgs);
    }


    // How many users are in a particular group?
    public int getUserCount(int groupId) {
        String selection = GROUP_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId)};
        return queryUserGroupXrefs(null, selection, selectionArgs, null).getCount();
    }


    public List<Integer> getUsersIDsInGroup(int groupId) {
        List<Integer> userIds = new ArrayList<Integer>();
        String[] selectionArgs = {String.valueOf(groupId)};
        Cursor c = queryUserGroupXrefs(null, GROUP_ID + "=?", selectionArgs, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int userInt = c.getInt(c.getColumnIndex(USER_ID));
            User user = getOneUser(userInt);
            Integer userId = userInt;
            userIds.add(userId);
        }

        return userIds;
    }

    // Return true of table exists
      public boolean isTableExists(String tableName) {
        Cursor cursor = mDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}

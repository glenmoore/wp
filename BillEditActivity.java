package mooreapps.whopays20;


import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BillEditActivity  extends ActivityBase implements DatePickerDialog.OnDateSetListener {

    private String className = this.getClass().getName();
    private EditText transDate;
    private AutoCompleteTextView transLocation;
    private CurrencyEditText transAmount;
    private TextView statusTextView;
    private TextView transPaidAmount;
    private TextView paymentsText;
    private Spinner transGroup;
    private int billRowId = 0;
    List<Payment> mPayments; //Feed Adapter
    private PaymentAdapter mPaymentAdapter;
    private AbsListView mPaymentListView;
    WhoPaysDbAdapter dbAdapter;
    private UserGroup selectedUserGroup;
    private List<Location> mLocations;
    private int mTransStatus;
    private boolean mFinish = true;
    HashMap<Integer, BigDecimal> mPaymentMap = new HashMap<Integer,BigDecimal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbAdapter = WhoPaysDbAdapter.getInstance(getApplicationContext());

        // Dump out if reference data doesn't exist
        if (!initialValidation()) {
            return;
        }

        int rowId = 0;
        billRowId = 0;

        if (!addMode) {
            String stRowId = getIntent().getStringExtra(IntentParams.ROW_ID);
            if (stRowId != null) {
                rowId = Integer.parseInt(stRowId);
            }
            billRowId = rowId;
        }

        transDate = getEditDate(R.id.transDate);
        transLocation = (AutoCompleteTextView) findViewById(R.id.transLocation);
        transGroup = (Spinner) findViewById(R.id.transGroup);
        transAmount = (CurrencyEditText) findViewById(R.id.transAmount);
        statusTextView = (TextView) findViewById(R.id.status);
        paymentsText = (TextView) findViewById(R.id.paymentText);
        transPaidAmount = (TextView) findViewById(R.id.paidAmount);
        loadLocation();
        transLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                // Position cursor to end of bill amount
                transAmount.setFocusableInTouchMode(true);
                transAmount.requestFocus();
                transAmount.setSelection(transAmount.getText().length());
            }
        });

        loadGroup();
        mPaymentListView = (AbsListView) findViewById(android.R.id.list);

        if (addMode) {
            setTitle(R.string.title_activity_bill_add);
            transAmount.setText(CurrencyUtil.currencyFormat(new BigDecimal(0)));
            statusTextView.setText(getResources().getString(R.string.bill_status_new));
            mPaymentListView.setVisibility(View.INVISIBLE);
            paymentsText.setVisibility(View.INVISIBLE);
        } else {
            Cursor cursor = dbAdapter.getTransaction(rowId);
            Transaction transaction = dbAdapter.getTransactionFromCursor(cursor);
            if (transaction == null) {
                UIUtil.log(className, "onCreate", "transaction is null");
            } else {
                if (transaction.mTransDate == null) {
                    transDate.setText("");
                } else {
                    transDate.setText(transaction.mTransDate.toString());
                }
                List<Payment> defaultPayments = getDefaultPayments(transaction.mGroupId);
                initializePaymentsMap(defaultPayments);
                setTitle(R.string.title_activity_bill_edit);
                List<Payment> dbPayments = dbAdapter.getTransactionPayments(billRowId);
                mPayments = mergePayments(defaultPayments, dbPayments);
                transLocation.setText(getLocationName(transaction.mLocationId));
                transGroup.setSelection(UIUtil.getValueObjectIndex(transGroup, transaction.mGroupId));
                transAmount.setText(CurrencyUtil.currencyFormat(transaction.mTransAmount));
                transPaidAmount.setText(CurrencyUtil.currencyFormat(transaction.mTransPaidAmount));
                statusTextView.setText(transaction.getStatusText(getResources()));
                mTransStatus = transaction.mTransStatus;
                transGroup.setEnabled(false);
                mPaymentListView.setVisibility(View.VISIBLE);
                paymentsText.setVisibility(View.VISIBLE);
                mPaymentAdapter = new PaymentAdapter(getBaseContext(),
                        R.layout.listview_payment_layout, mPayments);
                mPaymentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    private int mLastFirstVisibleItem;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            mFinish = false;
                            save(); // save what they have
                            mFinish = true;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {

                    }
                });
                mPaymentListView.setAdapter(mPaymentAdapter);
            }
        }
    }

    private void initializePaymentsMap(List<Payment> defaultPayments) {
        for (int i = 0; i < defaultPayments.size(); i++) {
            Payment payment = defaultPayments.get(i);
            mPaymentMap.put(payment.mUserId, payment.getPayAmount());
        }
        //UIUtil.logBold("BEA", "initializePaymentsMap mPaymentMap=", mPaymentMap);
    }

    private void loadLocation(){
        mLocations = dbAdapter.getAllLocations();
        String[] locArray = new String[mLocations.size()];

        for (int i = 0; i < mLocations.size(); i++) {
            Location loc = mLocations.get(i);
            locArray[i] = loc.getLocName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,locArray);
        transLocation.setThreshold(1);
        transLocation.setAdapter(adapter);
    }

    private int getLocationId (String locationName) {
        int id = 0;
        for (int i = 0; i < mLocations.size(); i++) {
            Location loc = mLocations.get(i);
            if (loc.getLocName().equals(locationName)) {
                id = loc.getId();
                break;
            }
        }
        return id;
    }

    private String getLocationName (int locationId) {
        String name = "";
        for (int i = 0; i < mLocations.size(); i++) {
            Location loc = mLocations.get(i);
            if (loc.getId() == (locationId)) {
                name = loc.getLocName();
                break;
            }
        }
        return name;
    }

    private void loadGroup(){
        List<UserGroup> userGroups = dbAdapter.getAllUserGroups();
        UserGroupAdapter userGroupAdapter = new UserGroupAdapter(BillEditActivity.this,
                android.R.layout.simple_spinner_item , userGroups );
        transGroup.setAdapter(userGroupAdapter);
    }

    @Override
    protected void postSave() {
        if (!addMode) {
            setPayments();
            dbAdapter.maintainBillPayments(mPayments);
            transPaidAmount.setText(CurrencyUtil.currencyFormat(getTotalPaid()));
            statusTextView.setText(Transaction.GetStatusText(mTransStatus, getResources()));
        }
    }

    private void setPayments() {
        EditText etPayAmount;
        TextView tvUserId;

        for (int i = 0; i < mPaymentListView.getCount(); i++) {
            View view = mPaymentListView.getChildAt(i);
            if (view != null) {
                etPayAmount = (EditText) mPaymentListView.getChildAt(i).findViewById(R.id.payAmount);
                tvUserId = (TextView) mPaymentListView.getChildAt(i).findViewById(R.id.userId);
                Integer userId = UIUtil.getTextViewInteger(tvUserId);
                BigDecimal payAmount = UIUtil.getEditTextBigDecimal(etPayAmount);
                mPaymentMap.put(userId, payAmount);
            }
        }

        for (int i=0; i<mPayments.size(); i++){
            Payment payment = mPayments.get(i);
            if (payment != null) {
                payment.setPayAmount(mPaymentMap.get(payment.mUserId));
                if (addMode && billRowId != -1) {
                    payment.mTransId = NumberUtil.safeLongToInt(billRowId);
                }
            }
        }

    }

    @Override
    protected void insert() {
        ContentValues newValues = getBillValues();
        try {
            Uri uri = getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_TRANSACTIONS, newValues);
            billRowId = NumberUtil.safeLongToInt(ContentUris.parseId(uri));
            switchToEditMode();
        } catch (SQLException e){
            billRowId = -1;
            Toast.makeText(getApplicationContext(), R.string.bill_not_added, Toast.LENGTH_LONG).show();
        };

    }

    // ToDo Redundant with onCreate()
    private void switchToEditMode() {
        addMode = false;
        setTitle(R.string.title_activity_bill_edit);
        List<Payment> defaultPayments = getDefaultPayments(getSelectedGroupId());
        List<Payment> dbPayments = dbAdapter.getTransactionPayments(billRowId);
        initializePaymentsMap(defaultPayments);
        mPayments = mergePayments(defaultPayments, dbPayments);
        mPaymentListView.setVisibility(View.VISIBLE);
        paymentsText.setVisibility(View.VISIBLE);
        mPaymentAdapter = new PaymentAdapter(getBaseContext(),
                R.layout.listview_payment_layout, mPayments);
        mPaymentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mFinish = false;
                    save(); // save what they have
                    mFinish = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
        mPaymentListView.setAdapter(mPaymentAdapter);
        String strAmt = transAmount.getText().toString();
        String strAmt2 = CurrencyUtil.unformatAmount(strAmt);
        getStatus(strAmt2); // Sets class level status
        Cursor cursor = dbAdapter.getTransaction(billRowId);
        Transaction transaction = dbAdapter.getTransactionFromCursor(cursor);
        transGroup.setSelection(UIUtil.getValueObjectIndex(transGroup, transaction.mGroupId));
        transGroup.setEnabled(false);
        invalidateOptionsMenu();
    }

    @Override
    protected ValidationResults validate() {

        ValidationResults vr = new ValidationResults(getResources());

        if (getSelectedGroupId() == -1) {
            vr.addErrorMessage(R.string.groups_not_found);
            return vr;
        }

        String locName = transLocation.getText().toString().trim();
        if (locName.length() < 1 ) {
            vr.addErrorMessage(R.string.vendor_name_enter);
        }

        if (transDate.getText().toString().length() < 1) {
            vr.addErrorMessage(R.string.date_not_entered);
        }

        CurrencyUtil.validateMoney(transAmount.getText().toString(), CurrencyUtil.AMOUNT_REQUIRED, vr);

        if (!addMode) {
            for (int i = 0; i < mPaymentListView.getCount(); i++) {
                View view = mPaymentListView.getChildAt(i);
                if (view != null) {
                    EditText etPayAmount = (EditText) mPaymentListView.getChildAt(i).findViewById(R.id.payAmount);
                    CurrencyUtil.validateMoney(etPayAmount.getText().toString(), CurrencyUtil.AMOUNT_NOT_REQUIRED, vr);
                }
            }
            if (dbAdapter.getUserCount(getSelectedGroupId()) < 1) {
                vr.addErrorMessage(R.string.no_users);
            } else {
                setPayments();
                if (mPayments != null && mPayments.get(0) != null) {
                    BigDecimal totalPaid = new BigDecimal(0);
                    for (int i = 0; i < mPayments.size(); i++) {
                        Payment payment = mPayments.get(i);
                        if (payment != null) {
                            totalPaid = totalPaid.add(payment.getPayAmount());
                        }
                    }
                    String strAmt = transAmount.getText().toString();
                    String strAmt2 = CurrencyUtil.unformatAmount(strAmt);
                    BigDecimal billAmt = new BigDecimal(strAmt2);
                    if (billAmt.compareTo(totalPaid) == -1) {
                        vr.addErrorMessage(R.string.over_paid);
                    }
                }
            }
        }
        return vr;
    }

    @Override
    protected void delete() {
        String[] selectionArgs = new String[] { ""+billRowId };
        String selectionclause = WhoPaysDbAdapter.SELECT_ID;
        getContentResolver().delete(WhoPaysContentProvider.CONTENT_URI_TRANSACTIONS, selectionclause, selectionArgs);
        Toast.makeText(getApplicationContext(), R.string.bill_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void update() {
        ContentValues saveValues = getBillValues();
        String[] selectionArgs = new String[] { ""+billRowId };

        String selectionclause = WhoPaysDbAdapter.SELECT_ID;

        try {
            int rowsAffected = getContentResolver().update(WhoPaysContentProvider.CONTENT_URI_TRANSACTIONS, saveValues, selectionclause, selectionArgs);
            if (rowsAffected == 1) {
                if (mFinish) {
                    Toast.makeText(getApplicationContext(), R.string.bill_updated, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                UIUtil.logBold("BEA", "update", "Bill NOT updated rows affected not 1");
                Toast.makeText(getApplicationContext(), R.string.bill_not_updated, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e){
            UIUtil.logBold("BEA","update","Bill NOT updated SQLException =" + e);
            Toast.makeText(getApplicationContext(), R.string.bill_not_updated, Toast.LENGTH_LONG).show();
        }
    }

    private ContentValues getBillValues() {
        if (!addMode) {
            setPayments();
        }
        ContentValues values = new ContentValues();
        values.put(WhoPaysDbAdapter.TRANS_DATE, transDate.getText().toString());
        String strAmt = transAmount.getText().toString();
        String strAmt2 = CurrencyUtil.unformatAmount(strAmt);
        values.put(WhoPaysDbAdapter.TRANS_AMOUNT, strAmt2);
        int status = 0;
        if (!addMode) {
            values.put(WhoPaysDbAdapter.TRANS_PAID_AMOUNT, getTotalPaid());
            status = getStatus(strAmt2);
        } else {
            values.put(WhoPaysDbAdapter.TRANS_PAID_AMOUNT, 0);
            status = Transaction.TRANS_STATUS_NEW;
        }

        values.put(WhoPaysDbAdapter.TRANS_STATUS, status);
        String locName = transLocation.getText().toString().trim();
        int locId = getLocationId(locName);

        if (locId == 0) {
            // Insert "on-the-fly" location
            ContentValues locValues = new ContentValues();
            locValues.put(WhoPaysDbAdapter.LOC_NAME, locName);
            try {
                getContentResolver().insert(WhoPaysContentProvider.CONTENT_URI_LOCATION, locValues);
                mLocations = dbAdapter.getAllLocations();
                locId = getLocationId(locName);
            } catch (SQLException e) {
                UIUtil.logBold("BillEditActivity", "getBillValues", "Location insert failed");
            }
        }

        values.put(WhoPaysDbAdapter.LOCATION_ID, locId);
        values.put(WhoPaysDbAdapter.GROUP_ID, getSelectedGroupId());
        return values;
    }

    private int getStatus(String billAmount) {
        double paid = getTotalPaid();
        int ret = 0;

        if (paid == 0) {
            ret = Transaction.TRANS_STATUS_UNPAID;
        } else {
            double bill = NumberUtil.stringToDouble(billAmount);
            if (bill == paid) {
                ret = Transaction.TRANS_STATUS_PAID;
            } else {
                ret = Transaction.TRANS_STATUS_PARTIAL_PAY;
            }
        }
        mTransStatus = ret;
        return ret;
    }

    private double getTotalPaid() {
        Iterator it = mPaymentMap.entrySet().iterator();
        double totalPaid = 0;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            BigDecimal payAmount = (BigDecimal) pair.getValue();
            double paid = NumberUtil.stringToDouble(payAmount.toString());
            totalPaid = totalPaid + paid;
        }

        return totalPaid;
    }

    public int getSelectedGroupId() {
        int id = 0;
        UserGroup selectedUserGroup = null;
        if (transGroup != null) {
            selectedUserGroup = (UserGroup) transGroup.getSelectedItem();
            if (selectedUserGroup != null) {
                id = selectedUserGroup.mId;
            } else {
                return -1;
            }

        }
        return id;
    }

    List<Payment> getDefaultPayments(int groupId) {

        ArrayList<Payment> payments = new ArrayList<Payment>();

        // get user ID's of users in group
        List<Integer> userIds = dbAdapter.getUsersIDsInGroup(groupId);

        for (int i=0; i<userIds.size(); i++){
            Payment payment = new Payment();
            payment.mUserId = userIds.get(i);
            payment.mTransId = billRowId;
            payment.setPayAmount(new BigDecimal(0));
            payments.add(payment);
        }

        return payments;
    }

    // Merge the default payment list with the list from the database
    // Add the transaction ID and amount to the defaults when row exists in DB
    List<Payment> mergePayments(List<Payment> defaultPayments, List<Payment> dbPayments) {
        ArrayList<Payment> payments = new ArrayList<Payment>();

        for (int i=0; i<defaultPayments.size(); i++){
            Payment defaultPayment = defaultPayments.get(i);
            Payment dbPayment = getUserPayment(defaultPayment.mUserId, dbPayments);
            Payment mergePayment = new Payment();
            mergePayment.mUserId = defaultPayment.mUserId;
            mergePayment.mTransId = defaultPayment.mTransId;
            if (dbPayment != null) {
                mergePayment.mId = dbPayment.mId;
                mergePayment.setPayAmount(dbPayment.getPayAmount());
            }
            payments.add(mergePayment);
            mPaymentMap.put(mergePayment.mUserId, mergePayment.getPayAmount());
            //UIUtil.logBold("BEA", "mergePayments mPaymentMap=", mPaymentMap);
        }
        return payments;
    }

    Payment getUserPayment(Integer userId, List<Payment> dbPayments) {
        Payment payment = null;

        for (int i=0; i<dbPayments.size(); i++){
            Payment dbPayment = dbPayments.get(i);
            if (dbPayment.mUserId.intValue() == userId.intValue()) {
                payment = dbPayment;
            }
        }

        return payment;
    }

    private boolean initialValidation() {
        long userCount = dbAdapter.getUserCount();
        long userGroupCount = dbAdapter.getUserGroupCount();

        ValidationResults vr = new ValidationResults(getResources());

        if (userCount < 1) {
            vr.addErrorMessage(R.string.people_not_found);
        }

        if (userGroupCount < 1) {
            vr.addErrorMessage(R.string.groups_not_found);
        }

        if (vr.hasMessages()) {
            displayMessages(vr);
            return false;
        } else {
            return true;
        }

    }

    private void displayWhoPaysMessage() {
        ValidationResults vr = new ValidationResults(getResources());
        CurrencyUtil.validateMoney(transAmount.getText().toString(), CurrencyUtil.AMOUNT_REQUIRED, vr);

        if (dbAdapter.getUserCount(getSelectedGroupId()) < 1) {
            vr.addErrorMessage(R.string.no_users);
        } else {
            if ((mTransStatus != Transaction.TRANS_STATUS_UNPAID && mTransStatus != Transaction.TRANS_STATUS_NEW)) {
                vr.addErrorMessage(R.string.payments_already_entered);
            }
        }

        if (vr.hasMessages()) {
            displayMessages(vr);
        } else {
            selectedUserGroup = (UserGroup) transGroup.getSelectedItem();
            new GetTotals().execute((Object[]) null);
        }
    }

    // Retrieve user totals and display who pays asynchronously
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
                UserGroupModel ugm = new UserGroupModel(getResources());
                final UserTotal payUT = ugm.whoPays(result);
                String displayMessage = payUT.getWhoPaysMessage();
                AlertDialog.Builder alert = new AlertDialog.Builder(BillEditActivity.this, R.style.AlertDialogCustom);
                alert.setTitle("");
                alert.setMessage(displayMessage);
                alert.setPositiveButton(R.string.pay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        payTotal(payUT);
                        dialog.dismiss();
                        BigDecimal payAmt = CurrencyUtil.unformatAmountBigDecimal(transAmount.getText().toString());
                        mPaymentMap.put(payUT.getUserId(), payAmt);
                        save();
                        //finish();
                }
            });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            } else {
                ValidationResults vr = new ValidationResults(getResources());
                if (selectedUserGroup != null) {
                    vr.addErrorMessage(R.string.bill_not_found_for_group);
                } else {
                    vr.addErrorMessage(R.string.group_bills_not_found);
                }
                displayMessages(vr);
            }

        }
    }

    // Echo the bill amount into the payment field of the user
    private void payTotal(UserTotal payUT) {
        int payUserId = payUT.getUserId();
        EditText etPayAmount;
        TextView tvUserId;

        for (int i = 0; i < mPaymentListView.getCount(); i++) {
            View view = mPaymentListView.getChildAt(i);
            if (view != null) {
                tvUserId = (TextView) mPaymentListView.getChildAt(i).findViewById(R.id.userId);
                Integer userId = UIUtil.getTextViewInteger(tvUserId);
                if (userId == payUserId) {
                    etPayAmount = (EditText) mPaymentListView.getChildAt(i).findViewById(R.id.payAmount);
                    etPayAmount.setText(transAmount.getText());
                    return;
                }
            }
        }
     }


    // Return total payments per user for selected group
    private ArrayList<UserTotal> getUserTotals(int groupId) {
        UserGroupModel ugm = new UserGroupModel(getResources());
        return ugm.getUserTotals(groupId, getApplicationContext());
    }


    public void setDate(EditText et){
        Calendar calendar = DateUtil.getCalendar(et.getText().toString());
        PickerDialogs pickerDialogs=new PickerDialogs(this, calendar);
        pickerDialogs.show(getSupportFragmentManager(), "date_picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int dateSetting, int dayOfMonth) {
        transDate.setText(DateUtil.getDateString(year, dateSetting, dayOfMonth));
    }

    private EditText getEditDate(int viewId) {
        final EditText et =  (EditText) findViewById(viewId);
        et.setInputType(InputType.TYPE_NULL);
        et.requestFocus();
        et.postDelayed(new Runnable() {
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(
                        et.getWindowToken(), 0);
            }
        }, 200);

        et.setText(DateUtil.getToday());

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) v;
                setDate(et);
            }
        });
        return et;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bill_edit, menu);

        if (addMode) {
            menu.findItem(R.id.action_who_pays).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_who_pays).setVisible(false);
        } else {
            if (mTransStatus == Transaction.TRANS_STATUS_UNPAID) {
                menu.findItem(R.id.action_who_pays).setVisible(true);
            } else {
                menu.findItem(R.id.action_who_pays).setVisible(false);
            }
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
            case R.id.action_who_pays:
                displayWhoPaysMessage();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem whoPays = menu.findItem(R.id.action_who_pays);
        if (!addMode && (mTransStatus == Transaction.TRANS_STATUS_UNPAID || mTransStatus == Transaction.TRANS_STATUS_NEW )) {
            whoPays.setVisible(true);
        } else {
            whoPays.setVisible(false);
        }
        return true;
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


    @Override
    protected int getContentView() {
        return R.layout.activity_bill_edit;
    }

    @Override
    protected int getDeleteMessageId() {
        return R.string.bill_delete_confirm;
    }



}

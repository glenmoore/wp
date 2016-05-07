package mooreapps.whopays20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by gmoore on 10/27/2015.
 */
public class TransactionsFragment extends android.support.v4.app.Fragment {

    RecyclerView recyclerView;
    ArrayList<Transaction> transactions;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BillEditActivity.class);
                intent.putExtra(IntentParams.ADD_MODE, true);
                getActivity().startActivity(intent);
            }
        });

        final FragmentActivity c = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(layoutManager);
        new GetTransactions().execute((Object[]) null);

        if (!initialValidation()) {
            fab.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        new GetTransactions().execute((Object[]) null);
    }


    // Retrieve and display bills asynchronously
    private class GetTransactions extends AsyncTask<Object, Object, ArrayList<Transaction> > {
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(getActivity().getApplicationContext());

        @Override
        protected ArrayList<Transaction> doInBackground(Object... params) {
            return dbAdapter.getAllTransactions();
        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> result) {
            if (result != null) {
                transactions = result;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final BillAdapter adapter = new BillAdapter(transactions, getActivity().getApplicationContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private boolean initialValidation() {

        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(getActivity().getApplicationContext());
        long userCount = dbAdapter.getUserCount();
        long userGroupCount = dbAdapter.getUserGroupCount();
        ValidationResults vr = new ValidationResults(getResources());

        if (userCount < 1 && userGroupCount < 1) {
            vr.addErrorMessage(R.string.initial_message);
        } else {
            if (userCount < 1) {
                vr.addErrorMessage(R.string.people_not_found);
            }
            if (userGroupCount < 1) {
                vr.addErrorMessage(R.string.groups_not_found);
            }
        }

        if (vr.hasMessages()) {
            displayMessages(vr);
            return false;
        } else {
            return true;
        }

    }

    // ToDo redundant with TotalsFragment
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


}

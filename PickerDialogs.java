package mooreapps.whopays20;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import java.util.Calendar;

/**
 * Created by gmoore on 12/14/2015.
 */
    public class PickerDialogs extends DialogFragment {

        public BillEditActivity mActivity;
        public Calendar mCalendar;

        public PickerDialogs(BillEditActivity activity, Calendar calendar) {
            mActivity = activity;
            mCalendar = calendar;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year= mCalendar.get(Calendar.YEAR);
            int month=mCalendar.get(Calendar.MONTH);
            int day=mCalendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),mActivity,year,month,day);
        }
    }


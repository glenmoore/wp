package mooreapps.whopays20;

import android.content.Context;
import android.content.res.Resources;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by gmoore on 4/5/2016.
 */
public class UserGroupModel {

    Resources mResources;

    public UserGroupModel(Resources resources) {
        mResources = resources;
    }

    // Return total payments per user for selected group
    public ArrayList<UserTotal> getUserTotals(int groupId, Context context) {
        ArrayList<UserTotal> userTotals = new ArrayList<UserTotal>();
        HashMap<Integer, UserTotal> hmap = new HashMap<Integer, UserTotal>();
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(context);
        ArrayList<Payment> payments = dbAdapter.getGroupPayments(groupId);

        for (int i = 0; i < payments.size(); i++) {
            Payment payment = payments.get(i);
            int userId = payment.mUserId;

            BigDecimal payAmount = payment.getPayAmount();

            //Update if already in map, else add new item
            Integer index = userId;
            UserTotal ut = hmap.get(index);

            if (ut == null) {
                User user = dbAdapter.getOneUser(userId);
                ut = new UserTotal(user);
                ut.setTotalPaid(payAmount);
                hmap.put(index, ut);
            } else {
                BigDecimal total = ut.getTotalPaid().add(payAmount);
                ut.setTotalPaid(total);
            }

        }

        //Transfer hashMap to ArrayList
        Set keySet = hmap.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            UserTotal ut = hmap.get(key);
            userTotals.add(ut);
        }

        //Need to return an entry per user
        List<Integer> userIDs = dbAdapter.getUsersIDsInGroup(groupId);

        if (userTotals.size() < userIDs.size()) {
            for (int i = 0; i < userIDs.size(); i++) {
                int userId = userIDs.get(i);
                Integer index = userId;
                UserTotal ut = hmap.get(index);
                if (ut == null) {
                    User user = dbAdapter.getOneUser(userId);
                    ut = new UserTotal(user);
                    ut.setTotalPaid(new BigDecimal(0));
                    userTotals.add(ut);
                }
            }
        }

        return userTotals;
    }

    // One UserTotal per User that Paid Bill
    // ToDo redundant code with getUserTotals
    public  ArrayList<UserTotal> getBillUserTotals(Transaction transaction, Context context) {
        ArrayList<UserTotal> userTotals = new ArrayList<UserTotal>();
        HashMap<Integer, UserTotal> hmap = new HashMap<Integer, UserTotal>();
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(context);
        ArrayList<Payment> payments = dbAdapter.getBillPayments(transaction.getId());

        for (int i = 0; i < payments.size(); i++) {
            Payment payment = payments.get(i);
            int userId = payment.mUserId;

            BigDecimal payAmount = payment.getPayAmount();

            //Update if already in map, else add new item
            Integer index = userId;
            UserTotal ut = hmap.get(index);

            if (ut == null) {
                User user = dbAdapter.getOneUser(userId);
                ut = new UserTotal(user);
                ut.setTotalPaid(payAmount);
                hmap.put(index, ut);
            } else {
                BigDecimal total = ut.getTotalPaid().add(payAmount);
                ut.setTotalPaid(total);
            }

        }

        //Transfer hashMap to ArrayList
        Set keySet = hmap.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            UserTotal ut = hmap.get(key);
            userTotals.add(ut);
        }

        return userTotals;
    }


    // return user total with display message
    public UserTotal whoPays(ArrayList<UserTotal> totals) {
        if (totals == null || totals.size() == 0) {
            return new UserTotal(mResources.getString(R.string.group_no_users));
        }

        //Sort User Totals by Amount Paid
        Collections.sort(totals, new UserTotalComparator());

        //Is there a tie for the lowest? Who are tied?
        UserTotal lowestUT = totals.get(0);
        BigDecimal lowestAmount = lowestUT.getTotalPaid();
        ArrayList<UserTotal> lowest = new ArrayList<UserTotal>();

        for (int i = 0; i < totals.size(); i++) {
            UserTotal ut = totals.get(i);
            if (ut.getTotalPaid().compareTo(lowestAmount) == 0) {
                lowest.add(ut);
            }
        }

        UserTotal payUT = null;
        if (lowest.size() == 1) {
            payUT = lowest.get(0);
        } else {
            // More than one user tied for lowest so randomly select
            Random rand = new Random();
            int randomVal = rand.nextInt(lowest.size());
            payUT = lowest.get(randomVal);
        }

        String message = mResources.getString(R.string.who_should_pay) + "\n" + "\n" + payUT.getFirstName() + " " + payUT.getLastName();
        payUT.setWhoPaysMessage(message);
        return payUT;
    }

    // Return true if selected user is already in a particular group
    public boolean isUserInGroup(int groupRowId, int userId, Context context) {
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(context);
        int count = dbAdapter.getUserGroupCount(groupRowId, userId);
        return count > 0;
    }

    // Return true if selected user is belongs to any group
    public boolean isUserInGroup(int userId, Context context) {
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(context);
        int count = dbAdapter.getUserGroupCount(userId);
        dbAdapter.getAllUserGroups(); // remove later
        return count > 0;
    }

    // Return true if selected user has payments for a particular group
    public boolean hasUserPaidGroup(int groupId, int userId, Context context) {
        WhoPaysDbAdapter dbAdapter = WhoPaysDbAdapter.getInstance(context);
        int count = dbAdapter.getUserGroupPaymentCount(groupId, userId);
        return count > 0;
    }


    // Compare UserTotals bny Total Paid
    private class UserTotalComparator implements Comparator<UserTotal> {
        @Override
        public int compare(UserTotal o1, UserTotal o2) {
            return o1.getTotalPaid().compareTo(o2.getTotalPaid());
        }
    }

}

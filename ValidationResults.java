package mooreapps.whopays20;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmoore on 3/22/2016.
 */
public class ValidationResults {

    private List<ValidationResult> mValidationResults;
    private Resources mResources;


    public ValidationResults(Resources resources) {
        mValidationResults = new ArrayList<ValidationResult>();
        mResources = resources;
    }

    public void addInfoMessage(int messageId) {
        ValidationResult vr = new ValidationResult(ValidationResult.LEVEL_INFO, messageId);
        if (!hasMessage(messageId)) {
            mValidationResults.add(vr);
        }
    }

    public void addWarningMessage(int messageId) {
        ValidationResult vr = new ValidationResult(ValidationResult.LEVEL_WARNING, messageId);
        if (!hasMessage(messageId)) {
            mValidationResults.add(vr);
        }
    }

    public void addErrorMessage(int messageId) {
        ValidationResult vr = new ValidationResult(ValidationResult.LEVEL_ERROR, messageId);
        if (!hasMessage(messageId)) {
            mValidationResults.add(vr);
        }

    }

    // ToDo not efficient
    private boolean hasMessage(int messageId) {
        for (int i = 0; i < mValidationResults.size(); i++) {
            ValidationResult vr = mValidationResults.get(i);
            if (vr.mMessageId == messageId) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMessages() {
        return ! (mValidationResults.size() == 0);
    }

    public String getMessages() {
        String msg = "";
        for (int i = 0; i < mValidationResults.size(); i++) {
            ValidationResult vr = mValidationResults.get(i);
            msg = msg + "\n" + "\n" + vr.mMessage;
        }
        return msg;
    }


    private class ValidationResult {
        public int mLevel;
        public static final int LEVEL_INFO = 1;
        public static final int LEVEL_WARNING = 2;
        public static final int LEVEL_ERROR = 3;
        public String mMessage;
        public int mMessageId;

        public ValidationResult(int level, int messageId) {
            mLevel = level;
            mMessageId = messageId;
            mMessage = mResources.getString(messageId);
        }

    }

}

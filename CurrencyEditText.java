package mooreapps.whopays20;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Created by gmoore on 4/1/2016.
 */
public class CurrencyEditText extends android.widget.EditText {
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangeListener();

    }
    public CurrencyEditText(Context context) {
        super(context);
        addTextChangeListener();
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangeListener();
    }

    private void addTextChangeListener() {
        this.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String text = arg0.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    setText(text.substring(0, text.length() - 1));
                    setSelection(getText().length());
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) { }
        });
    }

}

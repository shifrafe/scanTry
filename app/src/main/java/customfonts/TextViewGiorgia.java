package customfonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class TextViewGiorgia extends TextView {

    public TextViewGiorgia(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewGiorgia(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewGiorgia(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Georgia.ttf");
            setTypeface(tf);
        }
    }
}


package hf.car.wifi.video.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class DrawableCenterTextView extends AppCompatTextView {

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // We want the icon and/or text grouped together and centered as a group.
        // We need to accommodate any existing padding
        final float buttonContentWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        float textWidth = 0f;
        final Layout layout = getLayout();
        if (layout != null) {
            for (int i = 0; i < layout.getLineCount(); i++) {
                textWidth = Math.max(textWidth, layout.getLineRight(i));
            }
        }

        // Compute left drawable width, if any
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];

        int drawableWidth = (drawableLeft != null) ? drawableLeft.getIntrinsicWidth() : 0;

        // We only count the drawable padding if there is both an icon and text
        int drawablePadding = ((textWidth > 0) && (drawableLeft != null)) ? getCompoundDrawablePadding() : 0;

        // Adjust contents to center
        float bodyWidth = textWidth + drawableWidth + drawablePadding;
        int translate = (int) ((buttonContentWidth - bodyWidth));
        if (translate != 0)
            setPadding(translate, 0, translate, 0);
//        canvas.translate((buttonContentWidth - bodyWidth) / 2, 0);
        super.onDraw(canvas);
    }
}

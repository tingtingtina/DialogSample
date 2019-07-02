
package com.tina.lib.dialog.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tina.lib.dialog.R;

/*
 * Create by Tina
 * Date: 2018/9/5
 * Description：
 */
public class SingleCheckLinearLayout extends LinearLayout implements Checkable {

    View mSelectImageIconView;
    Context mContext;
    TextView mTextView;

    public SingleCheckLinearLayout(Context context) {
        super(context);
    }

    public SingleCheckLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSelectImageIconView = findViewById(R.id.select_icon);
        mTextView = findViewById(R.id.text_left);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            mSelectImageIconView.setVisibility(View.VISIBLE);
        } else {
            mSelectImageIconView.setVisibility(View.INVISIBLE);
        }
        mTextView.setSelected(checked);
    }

    @Override
    public boolean isChecked() {
        return mTextView.isSelected();
    }

    @Override
    public void toggle() {

    }
}

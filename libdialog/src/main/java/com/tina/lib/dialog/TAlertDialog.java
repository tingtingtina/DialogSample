package com.tina.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;

import java.util.Timer;
import java.util.TimerTask;

/*
 * Create by Tina
 * Date: 2018/9/5
 * Description：
 */
public class TAlertDialog extends Dialog implements DialogInterface {
    private static final String TAG = TAlertDialog.class.getSimpleName();
    boolean DEBUG = true;
    private TAlertController mAlert;
    public CharSequence[] mItemTexts;
    private Context mContext;

    private DismissCallBack mDismissCallBack;

    protected TAlertDialog(Context context) {
        this(context, R.style.V5_AlertDialog);
    }

    protected TAlertDialog(Context context, int theme) {
        this(context, theme, Gravity.BOTTOM/*, 0*/);
    }

    protected TAlertDialog(Context context, int theme, int gravity/*, int startY*/) {
        super(context, theme);
        mAlert = new TAlertController(context, this, getWindow(), gravity);
        mContext = context;
    }

    public EditText getInputView() {
        return Builder.mEdit;
    }

    public View getView() {
        return mAlert.getView();
    }

    private void showSoftInput() {
        if (mAlert.getView() != null && mAlert.getView() instanceof EditText) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(mAlert.getView(),
                                InputMethodManager.RESULT_SHOWN);
                    }
                }
            }, 200);
        }
    }

    private void hideSoftInput() {
        if (mAlert.getView() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(mAlert.getView().getWindowToken(), 0);
            }
        }
    }

    /**
     * Set resId to 0 if you don't want an icon.
     *
     * @param resId the resourceId of the drawable to use as the icon or 0 if
     *              you don't want an icon.
     */
    public void setIcon(int resId) {
        mAlert.setIcon(resId);
    }

    public void setIcon(Drawable icon) {
        mAlert.setIcon(icon);
    }

    public void setPositiveButtonEnable(boolean eanble) {
        mAlert.setPositiveButtonEnable(eanble);
    }

    @Override
    public void dismiss() {
        if (mDismissCallBack != null) {
            mDismissCallBack.beforeDismissCallBack();
        }
        hideSoftInput();
        super.dismiss();
        if (mDismissCallBack != null) {
            mDismissCallBack.afterDismissCallBack();
        }
    }

    /**
     * 对话框点击按钮之后默认会自动消失 如果不希望点击按钮之后自动消失，可以使用此方法
     * @return
     */
    public void setAutoDismiss(boolean autoDismiss) {
        mAlert.setAutoDismiss(autoDismiss);
        if (autoDismiss) {
            mAlert.sendDismissMessage();
        }
    }

    /**
     * 设置dialog dismiss时的回调
     *
     * @param callBack
     */
    public void setDismissCallBack(DismissCallBack callBack) {
        this.mDismissCallBack = callBack;
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAlert.installContent();
    }


    public static class Builder {
        private final TAlertController.AlertParams P;
        private Context mContext;
        public boolean isHide = true;

        public static final int TYPE_CUSTOM_EDIT = 1;
        public static final int TYPE_EDIT_WITH_DEL = 2;
        public static final int TYPE_EDIT_WITH_HIDE = 3;

        static public EditText mEdit;

        /**
         * Constructor using a context for this builder and the
         * {@link android.app.AlertDialog} it creates.
         */
        public Builder(Context context) {
            this.mContext = context;
            P = new TAlertController.AlertParams(context);
        }

        /**
         * Set the title using the custom view {@code customTitleView}. The
         * methods {@link #setTitle(int)} and {@link #setIcon(int)} should be
         * sufficient for most titles, but this is provided if the title needs
         * more customization. Using this will replace the title and icon set
         * via the other methods.
         *
         * @param customTitleView The custom view to use as the title.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setCustomTitle(View customTitleView) {
            P.mCustomTitleView = customTitleView;
            return this;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setTitle(int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }

        /**
         * Set the resource id of the {@link Drawable}
         * to be used in the title.
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setIcon(int iconId) {
            P.mIconId = iconId;
            return this;
        }

        /**
         * Set the {@link Drawable} to be used in the
         * title.
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setIcon(Drawable icon) {
            P.mIcon = icon;
            return this;
        }

        public Builder setGravity(int gravity) {
            P.mGravity = gravity;
            return this;
        }

        public Builder setInputView(String str, boolean singleLine) {
            return setInputView(str, singleLine, TYPE_CUSTOM_EDIT);
        }

        /**
         * 该方法不能与setView()方法同时使用
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setInputView(String str, boolean singleLine, int type) {
//            View view = View.inflate(mContext, R.layout.ml_alert_dialog_input_view, null);
//            setView(view,
//                    mContext.getResources().getDimensionPixelSize(R.dimen.alert_dialog_button_panel_padding_horizontal),
//                    0,
//                    mContext.getResources().getDimensionPixelSize(R.dimen.alert_dialog_button_panel_padding_horizontal),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.alert_dialog_custom_panel_padding_bottom));
//            if (type == TYPE_CUSTOM_EDIT) {
//                mEdit = ((EditText) view.findViewById(R.id.input_text));
//                mEdit.setVisibility(View.VISIBLE);
//                mEdit.setSingleLine(singleLine);
//                if (!TextUtils.isEmpty(str))
//                    mEdit.setHint(str);
//                mEdit.requestFocus();
//            }
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog, specifying the
         * spacing to appear around that view. If the supplied view is an
         * instance of a {@link android.widget.ListView} the light background
         * will be used.
         *
         * @param view              The view to use as the contents of the Dialog.
         * @param viewSpacingLeft   Spacing between the left edge of the view and
         *                          the dialog frame
         * @param viewSpacingTop    Spacing between the top edge of the view and
         *                          the dialog frame
         * @param viewSpacingRight  Spacing between the right edge of the view
         *                          and the dialog frame
         * @param viewSpacingBottom Spacing between the bottom edge of the view
         *                          and the dialog frame
         * @return This Builder object to allow for chaining of calls to set
         * methods This is currently hidden because it seems like people
         * should just be able to put padding around the view.
         */
        public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop,
                               int viewSpacingRight, int viewSpacingBottom) {
            P.mView = view;
            P.mViewSpacingSpecified = true;
            P.mViewSpacingLeft = viewSpacingLeft;
            P.mViewSpacingTop = viewSpacingTop;
            P.mViewSpacingRight = viewSpacingRight;
            P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setMessage(int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed
         *
         * @param textId   The resource id of the text to display in the positive button.
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            P.mButtonPositiveText = P.mContext.getText(textId);
            P.mButtonPositiveListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text     The text to display in the positive button
         * @param listener The {@link OnClickListener} to  use.
         * @return This Builder object to allow for chaining of calls to set methods.
         */

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mButtonPositiveText = text;
            P.mButtonPositiveListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text     The text to display in the positive button
         * @param color    The color to display int the positive button
         * @param listener The {@link OnClickListener} to  use.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setPositiveButton(CharSequence text, ColorStateList color, final OnClickListener listener) {
            P.mButtonPositiveText = text;
            P.mButtonPositiveTextColor = color;
            P.mSetPositiveColor = true;
            P.mButtonPositiveListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed
         *
         * @param textId   The resource id of the text to display in the negative button
         * @param listener The {@link OnClickListener} to  use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            P.mButtonNegativeText = P.mContext.getText(textId);
            P.mButtonNegativeListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         *
         * @param text     The text to display in the negative button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mButtonNegativeText = text;
            P.mButtonNegativeListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         *
         * @param text     The text to display in the positive button
         * @param color    The color to display int the positive button
         * @param listener The {@link OnClickListener} to  use.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setNegativeButton(CharSequence text, ColorStateList color, final OnClickListener listener) {
            P.mButtonNegativeText = text;
            P.mButtonNegativeTextColor = color;
            P.mSetNegativeColor = true;
            P.mButtonNegativeListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed
         *
         * @param textId   The resource id of the text to display in the neutral button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            P.mButtonNeutralText = P.mContext.getText(textId);
            P.mButtonNeutralListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         *
         * @param text     The text to display in the neutral button
         * @param listener The {@link OnClickListener} to use.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            P.mButtonNeutralText = text;
            P.mButtonNeutralListener = listener;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not. Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */

        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * 对话框点击按钮之后默认会自动消失 如果不希望点击按钮之后自动消失，可以使用此方法
         *
         * @param autoDismiss default is true
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAutoDismiss(boolean autoDismiss) {
            P.mAutoDismiss = autoDismiss;
            return this;
        }

        /**
         * whether the dialog set padding or not. Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPadding(boolean isPadding) {
            P.mIsPadding = isPadding;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener. This
         * should be an array type i.e. R.array.foo
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setItems(int itemsId, final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener.
         *
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener. This
         * should be an array type i.e. R.array.foo The list will have a check
         * mark displayed to the right of the text for the checked item.
         * Clicking on an item in the list will not dismiss the dialog. Clicking
         * on a button will dismiss the dialog.
         *
         * @param itemsId     the resource id of an array i.e. R.array.foo
         * @param checkedItem specifies which item is checked. If -1 closeButton items
         *                    are checked.
         * @param listener    notified when an item on the list is clicked. The
         *                    dialog will not be dismissed when an item is clicked. It
         *                    will only be dismissed if clicked on a button, if closeButton
         *                    buttons are supplied it's up to the user to dismiss the
         *                    dialog.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setSingleChoiceItems(int itemsId, int checkedItem,
                                            final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener. The
         * list will have a check mark displayed to the right of the text for
         * the checked item. Clicking on an item in the list will not dismiss
         * the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param cursor      the cursor to retrieve the items from.
         * @param checkedItem specifies which item is checked. If -1 closeButton items
         *                    are checked.
         * @param labelColumn The column name on the cursor containing the
         *                    string to display in the label.
         * @param listener    notified when an item on the list is clicked. The
         *                    dialog will not be dismissed when an item is clicked. It
         *                    will only be dismissed if clicked on a button, if closeButton
         *                    buttons are supplied it's up to the user to dismiss the
         *                    dialog.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
                                            final OnClickListener listener) {
            P.mCursor = cursor;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mLabelColumn = labelColumn;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener. The
         * list will have a check mark displayed to the right of the text for
         * the checked item. Clicking on an item in the list will not dismiss
         * the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items       the items to be displayed.
         * @param checkedItem specifies which item is checked. If -1 closeButton items are checked.
         * @param listener    notified when an item on the list is clicked. The
         *                    dialog will not be dismissed when an item is clicked. It
         *                    will only be dismissed if clicked on a button, if closeButton
         *                    buttons are supplied it's up to the user to dismiss the
         *                    dialog.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem,
                                            final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you
         * will be notified of the selected item via the supplied listener. The
         * list will have a check mark displayed to the right of the text for
         * the checked item. Clicking on an item in the list will not dismiss
         * the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param adapter     The {@link ListAdapter} to supply the
         *                    list of items
         * @param checkedItem specifies which item is checked. If -1 closeButton items
         *                    are checked.
         * @param listener    notified when an item on the list is clicked. The
         *                    dialog will not be dismissed when an item is clicked. It
         *                    will only be dismissed if clicked on a button, if closeButton
         *                    buttons are supplied it's up to the user to dismiss the
         *                    dialog.
         * @return This Builder object to allow for chaining of calls to set
         * methods
         */
        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem,
                                            final OnClickListener listener) {
            P.mAdapter = adapter;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }


        public TAlertDialog create() {
            return this.create(R.style.V5_AlertDialog, Gravity.BOTTOM);
        }

        public TAlertDialog create(int gravity) {
            return this.create(R.style.V5_AlertDialog, gravity);
        }

        public TAlertDialog create(int theme, int gravity) {
            final TAlertDialog dialog = new TAlertDialog(P.mContext, theme, gravity);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            return dialog;
        }
    }

    public static interface DismissCallBack {

        void beforeDismissCallBack();

        void afterDismissCallBack();
    }
}

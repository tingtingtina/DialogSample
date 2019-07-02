package com.tina.lib.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/*
 * Create by Tina
 * Date: 2018/9/5
 * Description：
 */
public class TAlertController {

    private static final int BIT_BUTTON_POSITIVE = 1;
    private static final int BIT_BUTTON_NEGATIVE = 2;
    private static final int BIT_BUTTON_NEUTRAL = 4;

    private final Context mContext;
    private final DialogInterface mDialogInterface;
    private final Window mWindow;
    private TextView mTitleView;
    private ImageView mIconView;
    private TextView mMessageView;
    private int mIconId = -1;
    private Drawable mIcon;

    private TextView mButtonPositive;
    private TextView mButtonNegative;
    private TextView mButtonNeutral;
    private ColorStateList mButtonPositiveTextColor;
    private ColorStateList mButtonNegativeTextColor;
    private boolean mSetPositiveColor;
    private boolean mSetNegativeColor;
    private CharSequence mButtonPositiveText;
    private CharSequence mButtonNegativeText;
    private CharSequence mButtonNeutralText;
    private Message mButtonPositiveMessage;
    private Message mButtonNegativeMessage;
    private Message mButtonNeutralMessage;

    private View mCustomTitleView;
    private ScrollView mScrollView;
    private ListView mListView;
    private ListAdapter mAdapter;
    public CharSequence[] mItems;
    private int mCheckedItem = -1;
    private View mView;
    private int mViewSpacingLeft;
    private int mViewSpacingTop;
    private int mViewSpacingRight;
    private int mViewSpacingBottom;
    private boolean mViewSpacingSpecified = false;

    private boolean mIsPadding = true;

    private CheckBox mCheckBox;

    private CharSequence mTitle;
    private CharSequence mMessage;

    private int mAlertDialogLayout;
    private int mListLayout;
    private int mSingleChoiceItemLayout;

    private int mGravity;
    private int mContentGravity = -1;
    private Handler mHandler;

    private boolean mAutoDismiss = true; // 对话框在点击按钮之后是否自动消失


    View.OnClickListener mButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Message m = null;
            if (v == mButtonPositive && mButtonPositiveMessage != null) {
                m = Message.obtain(mButtonPositiveMessage);
            } else if (v == mButtonNegative && mButtonNegativeMessage != null) {
                m = Message.obtain(mButtonNegativeMessage);
            } else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
                m = Message.obtain(mButtonNeutralMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }

            if (mAutoDismiss) {
                // Post a message so we dismiss after the above handlers are
                // executed
                mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialogInterface)
                        .sendToTarget();
            }
        }
    };

    private static final class ButtonHandler extends Handler {
        // Button clicks have Message.what as the BUTTON{1,2,3} constant
        private static final int MSG_DISMISS_DIALOG = 1;

        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            mDialog = new WeakReference<DialogInterface>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEGATIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
                    break;
                case MSG_DISMISS_DIALOG:
                    ((DialogInterface) msg.obj).dismiss();
            }
        }
    }

    public void sendDismissMessage() {
        mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialogInterface).sendToTarget();
    }

    public TAlertController(Context context, DialogInterface di, Window window) {
        this(context, di, window, Gravity.BOTTOM);
    }

    public TAlertController(Context context, DialogInterface di, Window window, int gravity) {
        mContext = context;
        mDialogInterface = di;
        mWindow = window;
        mHandler = new ButtonHandler(di);
        mAlertDialogLayout = R.layout.dialog_alert;
        mListLayout = R.layout.dialog_single_choice;
        mSingleChoiceItemLayout = R.layout.dialog_single_choice_item_default;
        mGravity = gravity;
        mAutoDismiss = true;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        mAutoDismiss = autoDismiss;
    }


    /**
     * @see android.app.AlertDialog.Builder#setCustomTitle(View)
     */
    public void setCustomTitle(View customTitleView) {
        mCustomTitleView = customTitleView;
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    /**
     * Set resId to 0 if you don't want an icon.
     *
     * @param resId the resourceId of the drawable to use as the icon or 0 if
     *              you don't want an icon.
     */
    public void setIcon(int resId) {
        mIconId = resId;
        if (mIconView != null) {
            if (resId > 0) {
                mIconView.setImageResource(mIconId);
            } else if (resId == 0) {
                mIconView.setVisibility(View.GONE);
            }
        }
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
        if ((mIconView != null) && (mIcon != null)) {
            mIconView.setImageDrawable(icon);
        }
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
        if (mMessageView != null) {
            mMessageView.setText(message);
        }
    }

    public void setMessage(Spanned spanned) {
        if (mMessageView != null && spanned != null) {
            mMessageView.setText(spanned);
        }
    }

    /**
     * Set the view to display in the dialog.
     */
    public void setView(View view) {
        mView = view;
        mViewSpacingSpecified = false;
    }

    /**
     * Set the view to display in the dialog along with the spacing around that
     * view
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
                        int viewSpacingBottom) {
        mView = view;
        mViewSpacingSpecified = true;
        mViewSpacingLeft = viewSpacingLeft;
        mViewSpacingTop = viewSpacingTop;
        mViewSpacingRight = viewSpacingRight;
        mViewSpacingBottom = viewSpacingBottom;
    }

    /**
     * set padding
     */
    public void setPadding(boolean isPadding) {
        mIsPadding = isPadding;
    }

    /**
     * Sets a click listener or a message to be sent when the button is clicked.
     * You only need to pass one of {@code listener} or {@code msg}.
     *
     * @param whichButton Which button, can be one of
     *                    {@link DialogInterface#BUTTON_POSITIVE},
     *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
     *                    {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text        The text to display in positive button.
     * @param listener    The{@link DialogInterface.OnClickListener} to use.
     * @param msg         The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, CharSequence text,
                          DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                mButtonPositiveText = text;
                mButtonPositiveMessage = msg;
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mButtonNegativeText = text;
                mButtonNegativeMessage = msg;
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                mButtonNeutralText = text;
                mButtonNeutralMessage = msg;
                break;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setPositiveColor(ColorStateList color) {
        mSetPositiveColor = true;
        mButtonPositiveTextColor = color;
    }

    public void setPositiveButtonEnable(boolean enable) {
        if (mButtonPositive != null) {
            mButtonPositive.setEnabled(enable);
        }
    }

    public void setNegativeColor(ColorStateList color) {
        mSetNegativeColor = true;
        mButtonNegativeTextColor = color;
    }

    public ListView getListView() {
        return mListView;
    }

    public View getView() {
        return mView;
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        /* We use a custom title so never request a window title */
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setGravity(mGravity);
        if (mView == null || !canTextInput(mView)) {
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        mWindow.setContentView(mAlertDialogLayout);
        initView();
    }

    public void initView() {
        if (!mIsPadding) {
            LinearLayout parentPanel = (LinearLayout) mWindow.findViewById(R.id.parentPanel);
            parentPanel.setPadding(0, 0, 0, 0);
        }
        //title
        LinearLayout topPanel = (LinearLayout) mWindow.findViewById(R.id.topPanel);
        boolean hasTitle = initTitle(topPanel);

        //contentPanel control
        LinearLayout contentPanel = (LinearLayout) mWindow.findViewById(R.id.contentPanel);
        initContent(contentPanel);

        //buttonPanel control
        boolean hasButtons = initButtons();
        View buttonPanel = mWindow.findViewById(R.id.buttonPanel);
        if (!hasButtons) {
            buttonPanel.setVisibility(View.GONE);
        }

        //title control
        if (!hasTitle) {
            mWindow.findViewById(R.id.empty_view).setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mMessage)){
                mWindow.findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
            }else{
                mWindow.findViewById(R.id.empty_message).setVisibility(View.GONE);
            }
        }else{
            mWindow.findViewById(R.id.empty_message).setVisibility(View.GONE);
        }

        if (mGravity == Gravity.CENTER) {
            mMessageView.setGravity(Gravity.CENTER);
        }

        //customPanel view control.
        FrameLayout customPanel = (FrameLayout) mWindow.findViewById(R.id.customPanel);
        if (mView != null) {
            FrameLayout custom = (FrameLayout) mWindow.findViewById(R.id.custom);
            custom.addView(mView);
            if (mViewSpacingSpecified) {
                custom.setPadding(mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                        mViewSpacingBottom);
//                if (mCustomBgTransplant)
//                    mTransplantBg = true;
            }
            if (mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
            }
        } else {
            customPanel.setVisibility(View.GONE);
        }

        //list control.
        if (mListView != null) {
            // ListView有分割线divider，因此header和listView需要显示分割线
            mWindow.findViewById(R.id.title_divider_line).setVisibility(View.VISIBLE);
        } else {
            mWindow.findViewById(R.id.title_divider_line).setVisibility(View.GONE);
        }
        if (hasButtons) {
            mWindow.findViewById(R.id.title_divider_line_bottom).setVisibility(View.VISIBLE);
        } else {
            mWindow.findViewById(R.id.title_divider_line_bottom).setVisibility(View.GONE);
        }

        if ((mListView != null) && (mAdapter != null)) {
            mListView.setAdapter(mAdapter);
            if (mCheckedItem > -1) {
                mListView.setItemChecked(mCheckedItem, true);
                mListView.setSelection(mCheckedItem);
            }
        }
    }

    /**
     * init the title
     */
    private boolean initTitle(LinearLayout topPanel) {
        boolean hasTitle = true;

        if (mCustomTitleView != null) {
            // Add the custom title view directly to the topPanel layout
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            topPanel.addView(mCustomTitleView, 0, lp);
            // Hide the title template
            View titleTemplate = mWindow.findViewById(R.id.title_template);
            titleTemplate.setVisibility(View.GONE);
        } else {
            final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);

            mIconView = (ImageView) mWindow.findViewById(R.id.icon);
            if (hasTextTitle) {
                /* Display the title if a title is supplied, else hide it */
                mTitleView = (TextView) mWindow.findViewById(R.id.tvTitle);
                mTitleView.setText(mTitle);
                /*
                 * Do this last so that if the user has supplied any icons we
                 * use them instead of the default ones. If the user has
                 * specified 0 then make it disappear.
                 */
                if (mIconId > 0) {
                    mIconView.setImageResource(mIconId);
                } else if (mIcon != null) {
                    mIconView.setImageDrawable(mIcon);
                } else if (mIconId == 0) {
                    /*
                     * Apply the padding from the icon to ensure the title is
                     * aligned correctly.
                     */
                    mTitleView.setPadding(mIconView.getPaddingLeft(),
                            mIconView.getPaddingTop(),
                            mIconView.getPaddingRight(),
                            mIconView.getPaddingBottom());
                    mIconView.setVisibility(View.GONE);
                }
            } else {
                // Hide the title template
                View titleTemplate = mWindow.findViewById(R.id.title_template);
                titleTemplate.setVisibility(View.GONE);
                mIconView.setVisibility(View.GONE);
                topPanel.setVisibility(View.GONE);
                hasTitle = false;
            }
        }
        return hasTitle;
    }

    private void initContent(LinearLayout contentPanel) {
        mScrollView = (ScrollView) mWindow.findViewById(R.id.scrollView);
        mScrollView.setFocusable(false);
        // Special case for users that only want to display a String
        mMessageView = (TextView) mWindow.findViewById(R.id.message);
        if (mMessageView == null) {
            return;
        }
        if (mMessage != null) {
            mMessageView.setText(mMessage);
            if (mContentGravity != -1) {
                mMessageView.setGravity(mContentGravity);
            }
        } else {
            mMessageView.setVisibility(View.GONE);
            mScrollView.removeView(mMessageView);
            if (mListView != null) {
                contentPanel.removeView(mWindow.findViewById(R.id.scrollView));
                if ((mAdapter != null && mAdapter.getCount() > 7)
                        || (mItems != null && mItems.length > 7)) {
                    contentPanel.addView(mListView, new LinearLayout.LayoutParams(MATCH_PARENT, 780));
                } else {
                    contentPanel.addView(mListView, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                }
                contentPanel.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1.0f));
            } else {
                contentPanel.setVisibility(View.GONE);
            }
        }
    }

    private boolean initButtons() {
        // set the dialog buttons
        int whichButtons = 0;
        mButtonPositive = (TextView) mWindow.findViewById(R.id.btnPos);
        mButtonPositive.setOnClickListener(mButtonHandler);
        if (TextUtils.isEmpty(mButtonPositiveText)) {
            mButtonPositive.setVisibility(View.GONE);
        } else {
            if (mSetPositiveColor) {
                mButtonPositive.setTextColor(mButtonPositiveTextColor);
                mSetPositiveColor = false;
            }
            mButtonPositive.setText(mButtonPositiveText);
            mButtonPositive.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
        }

        mButtonNegative = (TextView) mWindow.findViewById(R.id.btnNeg);
        mButtonNegative.setOnClickListener(mButtonHandler);
        if (TextUtils.isEmpty(mButtonNegativeText)) {
            mButtonNegative.setVisibility(View.GONE);
        } else {
            if (mSetNegativeColor) {
                mButtonNegative.setTextColor(mButtonNegativeTextColor);
                mSetNegativeColor = false;
            }
            mButtonNegative.setText(mButtonNegativeText);
            mButtonNegative.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
        }

        mButtonNeutral = mWindow.findViewById(R.id.btnNeu);
        mButtonNeutral.setOnClickListener(mButtonHandler);
        if (TextUtils.isEmpty(mButtonNeutralText)) {
            mButtonNeutral.setVisibility(View.GONE);
        } else {
            mButtonNeutral.setText(mButtonNegativeText);
            mButtonNeutral.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
        }
        // 适配按键样式
        switch (whichButtons) {
            case BIT_BUTTON_POSITIVE:
                mButtonPositive.setBackgroundResource(R.drawable.dialog_btn_single_selector);
                break;
            case BIT_BUTTON_NEGATIVE:
                mButtonNegative.setBackgroundResource(R.drawable.dialog_btn_single_selector);
                break;
        }
        return whichButtons != 0;
    }

    public static class AlertParams {

        public Context mContext;
        public LayoutInflater mInflater;
        public View mCustomTitleView;
        public int mIconId = 0;
        public Drawable mIcon;
        public CharSequence mTitle;
        public CharSequence mTitleColor;
        public CharSequence mMessage;
        public ColorStateList mButtonPositiveTextColor;
        public ColorStateList mButtonNegativeTextColor;
        public boolean mSetPositiveColor = false;
        public boolean mSetNegativeColor = false;
        public CharSequence mButtonPositiveText;
        public CharSequence mButtonNegativeText;
        public CharSequence mButtonNeutralText;
        public DialogInterface.OnClickListener mButtonPositiveListener;
        public DialogInterface.OnClickListener mButtonNegativeListener;
        public DialogInterface.OnClickListener mButtonNeutralListener;

        public View mView;
        public int mViewSpacingLeft;
        public int mViewSpacingTop;
        public int mViewSpacingRight;
        public int mViewSpacingBottom;
        public boolean mViewSpacingSpecified = false;

        public boolean mIsPadding = true;

        public boolean mAutoDismiss;
        public boolean mCancelable;

        public CharSequence[] mItems;
        public ListAdapter mAdapter;
        public DialogInterface.OnClickListener mOnClickListener;
        public boolean mIsSingleChoice;
        public int mCheckedItem = -1;
        public Cursor mCursor;
        public String mLabelColumn;
        public int mGravity = -1;

        public AlertParams(Context context) {
            mContext = context;
            mCancelable = true;
            mAutoDismiss = true;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void apply(TAlertController dialog) {
            //set the dialog title.
            if (mCustomTitleView != null) {
                dialog.setCustomTitle(mCustomTitleView);
            } else {
                if (mTitle != null) {
                    dialog.setTitle(mTitle);
                }
                if (mIcon != null) {
                    dialog.setIcon(mIcon);
                }
                if (mIconId >= 0) {
                    dialog.setIcon(mIconId);
                }
            }
            //set the dialog message.
            if (mMessage != null) {
                dialog.setMessage(mMessage);
            }
            //set the dialog buttons.
            if (mButtonPositiveText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, mButtonPositiveText,
                        mButtonPositiveListener, null);
            }
            if (mButtonNegativeText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mButtonNegativeText,
                        mButtonNegativeListener, null);
            }
            if (mButtonNeutralText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, mButtonNeutralText,
                        mButtonNeutralListener, null);
            }

            if (mSetPositiveColor) {
                dialog.setPositiveColor(mButtonPositiveTextColor);
            }

            if (mSetNegativeColor) {
                dialog.setNegativeColor(mButtonNegativeTextColor);
            }

            dialog.mContentGravity = mGravity;

            //set the dialog list.
            if ((mItems != null) || (mCursor != null) || (mAdapter != null)) {
                createListView(dialog);
            }
            //set the other view.
            if (mView != null) {
                if (mViewSpacingSpecified) {
                    dialog.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                            mViewSpacingBottom);
                } else {
                    dialog.setView(mView);
                }
            }

            //set padding
            dialog.setPadding(mIsPadding);

            //set dismiss auto.
            dialog.setAutoDismiss(mAutoDismiss);
        }

        private void createListView(final TAlertController dialog) {
            final ListView listView = (ListView) mInflater.inflate(dialog.mListLayout, null);
            ListAdapter adapter = null;
            int layout = dialog.mSingleChoiceItemLayout;
            if (mCursor == null) {
                if (mAdapter != null) {
                    adapter = mAdapter;
                } else {
                    if (mGravity == Gravity.CENTER) {
                        adapter = new ArrayAdapter<>(mContext, layout, R.id.text_center, mItems);
                    } else {
                        adapter = new ArrayAdapter<>(mContext, layout, R.id.text_left, mItems);
                    }
                }
            } else {
                if (mGravity == Gravity.CENTER) {
                    adapter = new SimpleCursorAdapter(mContext, layout, mCursor,
                            new String[]{mLabelColumn}, new int[]{R.id.text_center});
                } else {
                    adapter = new SimpleCursorAdapter(mContext, layout, mCursor,
                            new String[]{mLabelColumn}, new int[]{R.id.text_left});
                }
            }

            dialog.mAdapter = adapter;
            dialog.mItems = mItems;
            dialog.mCheckedItem = mCheckedItem;

            if (mOnClickListener != null) {
                listView.setOnItemClickListener((parent, v, position, id) -> {
                    mOnClickListener.onClick(dialog.mDialogInterface, position);
                    if (!mIsSingleChoice) {
                        dialog.mDialogInterface.dismiss();
                    }
                });
            }
            if (mIsSingleChoice) {
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            }

            dialog.mListView = listView;
        }
    }
}

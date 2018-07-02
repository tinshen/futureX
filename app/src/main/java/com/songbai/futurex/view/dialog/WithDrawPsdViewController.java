package com.songbai.futurex.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.mine.CashPwdFragment;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.view.SmartDialog;

/**
 * Modified by john on 2018/6/1
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class WithDrawPsdViewController extends SmartDialog.CustomViewController {

    private TextView mTitle;
    private OnClickListener mOnClickListener;
    private Context mContext;
    private EditText mCashPwd;
    private TextView mConfirm;
    private EditText mGoogleAuthCode;
    private boolean showCashPwd = true;
    private boolean showGoogleAuth = false;

    public interface OnClickListener {
        void onConfirmClick(String cashPwd,String googleAuth);
    }

    public WithDrawPsdViewController(Context context, OnClickListener onClickListener) {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.view_with_draw_psd_google_auth, null);
    }

    @Override
    public void onInitView(View view, final SmartDialog dialog) {
        mTitle = view.findViewById(R.id.topTitle);
        mCashPwd = (EditText) view.findViewById(R.id.cashPwd);
        mCashPwd.setVisibility(showCashPwd ? View.VISIBLE : View.GONE);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
        mGoogleAuthCode = (EditText) view.findViewById(R.id.googleAuthCode);
        mGoogleAuthCode.setVisibility(showGoogleAuth ? View.VISIBLE : View.GONE);
        TextView forgetCashPwd = (TextView) view.findViewById(R.id.forgetCashPwd);
        forgetCashPwd.setVisibility(showCashPwd ? View.VISIBLE : View.GONE);

        mCashPwd.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean enable = checkConfirmButtonEnable();
                if (enable != mConfirm.isEnabled()) {
                    mConfirm.setEnabled(enable);
                }
            }
        });

        forgetCashPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UniqueActivity.launcher(mContext, CashPwdFragment.class)
                        .putExtra(ExtraKeys.HAS_WITH_DRAW_PASS, true).execute();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String authCode = mCashPwd.getText().toString();
                mOnClickListener.onConfirmClick(authCode,mGoogleAuthCode.getText().toString());
            }
        });
    }

    private boolean checkConfirmButtonEnable() {
        if (showCashPwd && TextUtils.isEmpty(mCashPwd.getText().toString())) {
            return false;
        }
        if (showGoogleAuth && TextUtils.isEmpty(mGoogleAuthCode.getText().toString())) {
            return false;
        }
        return true;
    }

    public void setShowCashPwd(boolean showCashPwd) {
        this.showCashPwd = showCashPwd;
    }

    public void setShowGoogleAuth(boolean showGoogleAuth) {
        this.showGoogleAuth = showGoogleAuth;
    }

    public void setTitle(int titleRes) {
        if (isViewInitialized()) {
            mTitle.setText(titleRes);
        }
    }

    public void mConfirm(int titleRes) {
        if (isViewInitialized()) {
            mConfirm.setText(titleRes);
        }
    }
}

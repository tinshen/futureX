package com.songbai.futurex.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.songbai.futurex.R;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestProgress {

    private AtomicInteger mCounter;
    private Dialog mDialog;
    private DialogInterface.OnCancelListener mOnCancelListener;

    public RequestProgress(DialogInterface.OnCancelListener cancelListener) {
        mCounter = new AtomicInteger(0);
        mOnCancelListener = cancelListener;
    }

    public void show(Activity activity, boolean cancelable) {
        if (activity.isFinishing()) return;

        if (mCounter.get() == 0) {
            if (mDialog == null) {
                mDialog = ProgressDialog.show(activity, mOnCancelListener, cancelable);
            } else {
                mDialog.show();
            }
            mCounter.incrementAndGet();
        } else {
            mCounter.incrementAndGet();
        }
    }

    public void show(Activity activity) {
        show(activity, true);
    }

    public void dismiss() {
        if (mCounter.get() == 0) {
            return;
        } else {
            mCounter.decrementAndGet();
        }

        if (mCounter.get() == 0 && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void dismissAll() {
        mCounter.set(0);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private static class ProgressDialog {

        public static Dialog show(Context context, DialogInterface.OnCancelListener cancelListener, boolean cancelable) {
            Dialog dialog = new Dialog(context, R.style.DialogTheme_NoTitle);
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(createView(context));
            dialog.setOnCancelListener(cancelListener);
            dialog.show();
            return dialog;
        }

        private static View createView(Context context) {
            RelativeLayout rootView = new RelativeLayout(context);
            ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            rootView.addView(progressBar, params);
            return rootView;
        }
    }
}

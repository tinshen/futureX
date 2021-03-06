package com.songbai.wrapres;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.songbai.wrapres.model.Training;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TrainingRuleDialog {

	private GifDrawable mGifFromResource;

	public interface OnDismissListener {
		void onDismiss();
	}

	private OnDismissListener mOnDismissListener;

	private Activity mActivity;
	private SmartDialog mSmartDialog;
	private View mView;
	private Training mTraining;

	public static TrainingRuleDialog with(Activity activity, Training training) {
		TrainingRuleDialog trainingRuleDialog = new TrainingRuleDialog();
		trainingRuleDialog.mActivity = activity;
		trainingRuleDialog.mSmartDialog = SmartDialog.single(activity);
		if (training.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
			trainingRuleDialog.mView = LayoutInflater.from(activity)
					.inflate(R.layout.dialog_training_rule_land, null);
			trainingRuleDialog.mSmartDialog.setHeightScale(0.8f);
		} else {
			trainingRuleDialog.mView = LayoutInflater.from(activity)
					.inflate(R.layout.dialog_training_rule, null);
		}
		trainingRuleDialog.mSmartDialog.setCustomView(trainingRuleDialog.mView);
		trainingRuleDialog.mTraining = training;
		trainingRuleDialog.init();
		return trainingRuleDialog;
	}

	private void init() {
		Button confirm = (Button) mView.findViewById(R.id.confirm);
		GifImageView image = (GifImageView) mView.findViewById(R.id.trainGif);
		TextView content = (TextView) mView.findViewById(R.id.content);

		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSmartDialog.dismiss();
				mGifFromResource.recycle();
			}
		});

		int gifDrawable = 0;
		int contentRes = 0;
		GradientDrawable gradientDrawable = null;
		float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
				mActivity.getResources().getDisplayMetrics());

		switch (mTraining.getPlayType()) {
			case Training.PLAY_TYPE_REMOVE:
				gifDrawable = R.drawable.ic_kline_train;
				contentRes = R.string.remove_train_rule;
				gradientDrawable = roundDrawable(R.drawable.bg_train_technology, radius);
				break;
			case Training.PLAY_TYPE_SORT:
				gifDrawable = R.drawable.ic_annual_report_train;
				contentRes = R.string.sort_train_rule;
				gradientDrawable = roundDrawable(R.drawable.bg_train_fundamentals, radius);
				break;
			case Training.PLAY_TYPE_MATCH_STAR:
				gifDrawable = R.drawable.ic_identification_train;
				contentRes = R.string.match_star_train_rule;
				gradientDrawable = roundDrawable(R.drawable.bg_train_theory, radius);
				break;
			case Training.PLAY_TYPE_JUDGEMENT:
				gifDrawable = R.drawable.ic_average_line_train;
				contentRes = R.string.judgement_train_rule;
				gradientDrawable = roundDrawable(R.drawable.bg_train_technology, radius);
				break;

		}
		if (gifDrawable != 0) {
			try {
				mGifFromResource = new GifDrawable(mActivity.getResources(),gifDrawable);
				image.setImageDrawable(mGifFromResource);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (contentRes != 0) {
			content.setText(contentRes);
		}

		if (gradientDrawable != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				confirm.setBackground(gradientDrawable);
			} else {
				confirm.setBackgroundDrawable(gradientDrawable);
			}
		}

		mSmartDialog.setOnDismissListener(new SmartDialog.OnDismissListener() {
			@Override
			public void onDismiss(Dialog dialog) {
				if (mOnDismissListener != null) {
					mOnDismissListener.onDismiss();
				}
			}
		});
	}

	private GradientDrawable roundDrawable(int drawableRes, float radius) {
		GradientDrawable oldDrawable = (GradientDrawable) ContextCompat.getDrawable(mActivity, drawableRes);
		GradientDrawable drawable = (GradientDrawable) oldDrawable.getConstantState().newDrawable().mutate();
		drawable.setCornerRadius(radius);
		return drawable;
	}

	public TrainingRuleDialog setOnDismissListener(OnDismissListener onDismissListener) {
		mOnDismissListener = onDismissListener;
		return this;
	}

	public void show() {
		mSmartDialog.show();
	}

}

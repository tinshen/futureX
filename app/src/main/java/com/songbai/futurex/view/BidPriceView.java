package com.songbai.futurex.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.utils.Display;

/**
 * Modified by john on 2018/6/21
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class BidPriceView extends LinearLayout {

    private TextView mRank;
    private TextView mVolume;
    private TextView mPrice;

    private int mMargin8;
    private int mMargin12;
    private int mHeight;

    private double mMaxValue;
    private double mValue;
    private Paint mPaint;
    private RectF mRectF;

    public BidPriceView(Context context) {
        super(context);
        init();
    }

    public BidPriceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#3F3FC87D"));
        mPaint.setStyle(Paint.Style.FILL);
        mRectF = new RectF();

        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mMargin8 = (int) Display.dp2Px(8, getResources());
        mMargin12 = (int) Display.dp2Px(12, getResources());
        mHeight = (int) Display.dp2Px(32, getResources());

        mRank = getTextView(12, ContextCompat.getColor(getContext(), R.color.text99));
        mRank.setMaxLines(1);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(mMargin12, 0, 0, 0);
        addView(mRank, params);

        mVolume = getTextView(12, ContextCompat.getColor(getContext(), R.color.text99));
        mVolume.setMaxLines(1);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.setMargins(mMargin8, 0, 0, 0);
        addView(mVolume, params);

        mPrice = getTextView(12, ContextCompat.getColor(getContext(), R.color.green));
        mPrice.setMaxLines(1);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, mMargin12, 0);
        addView(mPrice, params);

        if (isInEditMode()) {
            mRank.setText("1");
            mVolume.setText("0.0814");
            mPrice.setText("9216.30");
            mMaxValue = 10;
            mValue = 4;
        }

        setEmptyValue();
    }

    public void setMaxValue(double maxValue) {
        mMaxValue = maxValue;
        invalidate();
    }

    public void setValue(double value) {
        mValue = value;
        invalidate();
    }

    public void setValueAndMax(double value, double maxValue) {
        mValue = value;
        mMaxValue = maxValue;
        invalidate();
    }

    public void setEmptyValue() {
        mVolume.setText("--");
        mPrice.setText("--");
        mValue = 0;
        mMaxValue = 0;
        invalidate();
    }

    public boolean isEmptyValue() {
        return mPaint.equals("--") && mVolume.equals("--");
    }

    public void setRank(int rank) {
        mRank.setText(String.valueOf(rank));
    }

    public void setVolume(String volume) {
        mVolume.setText(volume);
    }

    public void setPrice(String price) {
        mPrice.setText(price);
    }

    public String getPrice() {
        return mPrice.getText().toString();
    }

    public String getVolume() {
        return mVolume.getText().toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMaxValue == 0) return;

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        float colorWidth = (float) (width * (mValue / mMaxValue));
        float colorLeft = left + width - colorWidth;
        mRectF.set(colorLeft, top, colorLeft + colorWidth, top + height);
        canvas.drawRect(mRectF, mPaint);
    }

    private TextView getTextView(float textSize, int textColor) {
        TextView textView = new TextView(getContext());

        textView.setTextSize(textSize);
        textView.setTextColor(textColor);

        return textView;
    }
}

package com.songbai.futurex.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.home.Banner;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sbai.com.glide.GlideApp;

public class HomeBanner extends FrameLayout {

    @BindView(R.id.viewPager)
    InfiniteViewPager mViewPager;
    @BindView(R.id.pageIndicator)
    PageIndicator mPageIndicator;

    private AdvertisementAdapter mAdapter;

    public interface OnBannerClickListener {
        void onBannerClick(Banner information);
    }

    public interface OnBannerTouchListener {
        void onTouch(boolean touch);
    }

    private OnBannerClickListener mOnBannerClickListener;
    private OnBannerTouchListener mOnBannerTouchListener;

    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        mOnBannerClickListener = onBannerClickListener;
    }

    public void setOnBannerTouchListener(OnBannerTouchListener onBannerTouchListener) {
        mOnBannerTouchListener = onBannerTouchListener;
    }

    public HomeBanner(Context context) {
        super(context);
        init();
    }

    public HomeBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.home_banner, this, true);
        ButterKnife.bind(this);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mPageIndicator != null) {
                mPageIndicator.move(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
            }
        }
    };

    public void nextAdvertisement() {
        if (mAdapter != null && mAdapter.getCount() > 1) {
            //ViewPager还在窗口执行这个动作
            if (!mViewPager.isDetachFromWindow()) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        }
    }

    public void setHomeAdvertisement(List<Banner> informationList) {
        if (informationList.size() == 0) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }
        filterEmptyInformation(informationList);

        if (!informationList.isEmpty()) {
            int size = informationList.size();
            if (size < 2) {
                mPageIndicator.setVisibility(INVISIBLE);
            } else {
                mPageIndicator.setVisibility(VISIBLE);
            }
            mPageIndicator.setCount(size);

            if (mAdapter == null) {
                mAdapter = new AdvertisementAdapter(getContext(), informationList, mOnBannerClickListener);
                mViewPager.addOnPageChangeListener(mOnPageChangeListener);
                mViewPager.setAdapter(mAdapter);
            } else {
                mAdapter.setNewAdvertisements(informationList);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnBannerTouchListener != null) {
                    mOnBannerTouchListener.onTouch(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (mOnBannerTouchListener != null) {
                    mOnBannerTouchListener.onTouch(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void filterEmptyInformation(List<Banner> informationList) {
        Iterator<Banner> iterator = informationList.iterator();
        while (iterator.hasNext()) {
            Banner banner = iterator.next();
            if (TextUtils.isEmpty(banner.getContent())) {
                iterator.remove();
            }
        }
    }

    private static class AdvertisementAdapter extends PagerAdapter {

        private List<Banner> mList;
        private Context mContext;
        private OnBannerClickListener mListener;

        public AdvertisementAdapter(Context context, List<Banner> informationList, OnBannerClickListener listener) {
            mContext = context;
            mList = informationList;
            mListener = listener;
        }

        public void setNewAdvertisements(List<Banner> informationList) {
            mList = informationList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position;
            ImageView imageView = new ImageView(mContext);
            final Banner information = mList.get(pos);

            container.addView(imageView);
            if (!TextUtils.isEmpty(information.getContent())) {
                GlideApp.with(mContext).load(information.getContent())
                        .centerCrop().into(imageView);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBannerClick(information);
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}

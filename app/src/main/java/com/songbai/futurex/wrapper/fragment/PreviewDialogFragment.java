package com.songbai.futurex.wrapper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PreviewDialogFragment extends DialogFragment {
    private String mUserPortrait;

    @BindView(R.id.imageview)
    ImageView mImageview;
    Unbinder unbinder;

    public static PreviewDialogFragment newInstance(String userPortrait) {
        PreviewDialogFragment fragment = new PreviewDialogFragment();
        Bundle args = new Bundle();
        args.putString(ExtraKeys.IMAGE, userPortrait);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置宽度为屏宽、靠近屏幕底部。
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        mUserPortrait = getArguments().getString(ExtraKeys.IMAGE);
        Glide.with(getActivity()).load(mUserPortrait)
                .into(mImageview);
        return view;
    }

    @OnClick(R.id.imageview)
    public void onClick(View view) {
        dismiss();
    }

    public void show(FragmentManager manager) {
        this.show(manager, PreviewDialogFragment.class.getSimpleName());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

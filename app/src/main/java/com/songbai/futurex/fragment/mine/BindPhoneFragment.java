package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class BindPhoneFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.areaCode)
    TextView mAreaCode;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.authCode)
    EditText mAuthCode;
    @BindView(R.id.getMessageAuthCode)
    TextView mGetMessageAuthCode;
    @BindView(R.id.mailAuthCode)
    EditText mMailAuthCode;
    @BindView(R.id.getMailAuthCode)
    TextView mGetMailAuthCode;
    @BindView(R.id.confirmBind)
    TextView mConfirmBind;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_phone, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.areaCode, R.id.getMessageAuthCode, R.id.getMailAuthCode, R.id.confirmBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.areaCode:
                break;
            case R.id.getMessageAuthCode:
                break;
            case R.id.getMailAuthCode:
                break;
            case R.id.confirmBind:
                break;
            default:
        }
    }
}

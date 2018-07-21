package com.songbai.futurex.fragment.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.UserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.CreateGoogleKey;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.utils.ValidationWatcher;
import com.songbai.futurex.utils.ZXingUtils;
import com.songbai.futurex.utils.image.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class GoogleAuthenticatorFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.qcCode)
    ImageView mQcCode;
    @BindView(R.id.secretKey)
    TextView mSecretKey;
    @BindView(R.id.googleAuthCode)
    EditText mGoogleAuthCode;
    @BindView(R.id.confirm)
    TextView mConfirm;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_authenticator, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mGoogleAuthCode.addTextChangedListener(new ValidationWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mConfirm.setEnabled(s.length() > 0);
            }
        });
        createGoogleKey();
    }

    private void createGoogleKey() {
        Apic.createGoogleKey().tag(TAG)
                .callback(new Callback<Resp<CreateGoogleKey>>() {
                    @Override
                    protected void onRespSuccess(Resp<CreateGoogleKey> resp) {
                        setGoogleKey(resp.getData());
                    }
                })
                .fire();
    }

    private void bindGoogleKey(String googleCode, String drawPass, String googleKey) {
        Apic.bindGoogleKey(googleCode, drawPass, googleKey).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(R.string.bind_google_authenticator_success);
                        LocalUser user = LocalUser.getUser();
                        if (user.isLogin()) {
                            UserInfo userInfo = user.getUserInfo();
                            if (userInfo.getGoogleAuth() == 0) {
                                userInfo.setGoogleAuth(1);
                                LocalUser.getUser().setUserInfo(userInfo);
                            }
                        }
                        finish();
                        UniqueActivity.launcher(getContext(), GoogleAuthenticatorSettingsFragment.class).execute();
                    }
                })
                .fire();
    }

    private void setGoogleKey(CreateGoogleKey data) {
        final Bitmap bitmap = ZXingUtils.createQRImage(data.getQrCode(), mQcCode.getMeasuredWidth(), mQcCode.getMeasuredHeight());
        mQcCode.setImageBitmap(bitmap);
        mQcCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageUtils.saveImageToGallery(getContext(), bitmap);
                ToastUtil.show(R.string.save_success);
                return true;
            }
        });
        mSecretKey.setText(data.getGoogleKey());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.copy, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mSecretKey.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            case R.id.confirm:
                bindGoogleKey(mGoogleAuthCode.getText().toString(), "", mSecretKey.getText().toString());
                break;
            default:
        }
    }
}

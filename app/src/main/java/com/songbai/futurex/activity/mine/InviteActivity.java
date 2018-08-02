package com.songbai.futurex.activity.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.fragment.mine.MyInviteFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.PromoterInfo;
import com.songbai.futurex.model.mine.PromotionInfos;
import com.songbai.futurex.utils.AnimatorUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.ToastUtil;
import com.songbai.futurex.view.TitleBar;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/31
 */
public class InviteActivity extends BaseActivity {
    @BindView(R.id.rules)
    TextView mRules;
    @BindView(R.id.inviteCode)
    TextView mInviteCode;
    @BindView(R.id.checkDetail)
    TextView mCheckDetail;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.eventPic)
    ImageView mEventPic;
    private String mPromotionGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UniqueActivity.launcher(InviteActivity.this, MyInviteFragment.class).execute();
            }
        });
        Apic.getCurrentPromoterMsg().tag(TAG)
                .callback(new Callback<Resp<PromoterInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromoterInfo> resp) {
                        PromoterInfo promoterInfo = resp.getData();
                        mInviteCode.setText(promoterInfo.getCode());
                    }
                })
                .fire();

        Apic.promotionRule().tag(TAG)
                .callback(new Callback<Resp<PromotionInfos>>() {
                    @Override
                    protected void onRespSuccess(Resp<PromotionInfos> resp) {
                        PromotionInfos promotionInfos = resp.getData();
                        GlideApp
                                .with(InviteActivity.this)
                                .load("")
                                .into(mEventPic);
                        mRules.setText(promotionInfos.getPromotionRule());
                        mPromotionGroup = promotionInfos.getPromotionGroup();
                    }
                }).fireFreely();
    }

    @OnClick({R.id.checkDetail, R.id.joinNow, R.id.copy, R.id.inviteBuddies, R.id.createPoster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkDetail:
                boolean b = mRules.getVisibility() == View.GONE;
                if (b) {
                    expand(mRules);
                } else {
                    collapse(mRules);
                }
                break;
            case R.id.joinNow:
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, "t.me" + mPromotionGroup)
                        .execute();
                break;
            case R.id.copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setPrimaryClip(ClipData.newPlainText(null, mInviteCode.getText()));
                ToastUtil.show(R.string.copy_success);
                break;
            case R.id.inviteBuddies:
                new ShareAction(getActivity())
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)//传入平台
                        .withText("hello")//分享内容
//                        .setCallback(umShareListener)//回调监听器
                        .share();
                break;
            case R.id.createPoster:
                break;
            default:
        }
    }

    private void expand(final View view) {
        AnimatorUtil.expandVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    mCheckDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_up, 0);
                }
            }
        });
    }

    private void collapse(final View view) {
        AnimatorUtil.collapseVertical(view, new AnimatorUtil.OnAnimatorFactionListener() {
            @Override
            public void onFaction(float fraction) {
                if (fraction == 1) {
                    view.setVisibility(View.GONE);
                    mCheckDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invitation_angle_down, 0);
                }
            }
        });
    }
}

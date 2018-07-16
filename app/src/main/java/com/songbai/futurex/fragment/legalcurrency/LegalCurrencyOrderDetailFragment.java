package com.songbai.futurex.fragment.legalcurrency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.CustomServiceActivity;
import com.songbai.futurex.activity.OtcTradeChatActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.OtcOrderDetail;
import com.songbai.futurex.model.WaresUserInfo;
import com.songbai.futurex.model.local.LocalUser;
import com.songbai.futurex.model.mine.BankCardBean;
import com.songbai.futurex.model.status.OTCOrderStatus;
import com.songbai.futurex.utils.FinanceUtil;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.view.CountDownView;
import com.songbai.futurex.view.EmptyRecyclerView;
import com.songbai.futurex.view.SmartDialog;
import com.songbai.futurex.view.TitleBar;
import com.songbai.futurex.view.dialog.WithDrawPsdViewController;
import com.songbai.futurex.websocket.DataParser;
import com.songbai.futurex.websocket.OnDataRecListener;
import com.songbai.futurex.websocket.Response;
import com.songbai.futurex.websocket.otc.OtcProcessor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/6/28
 */
public class LegalCurrencyOrderDetailFragment extends UniqueActivity.UniFragment {
    private static final int LEGAL_CURRENCY_ORDER_RESULT = 15432;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.turnover)
    TextView mTurnover;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.tradeAmount)
    TextView mTradeAmount;
    @BindView(R.id.orderNo)
    TextView mOrderNo;
    @BindView(R.id.orderStatus)
    TextView mOrderStatus;
    @BindView(R.id.timer)
    CountDownView mCountDownView;
    @BindView(R.id.headPortrait)
    ImageView mHeadPortrait;
    @BindView(R.id.certification)
    ImageView mCertification;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.payInfo)
    EmptyRecyclerView mPayInfo;
    @BindView(R.id.countDealRate)
    TextView mCountDealRate;
    @BindView(R.id.bankEmptyView)
    TextView mBankEmptyView;
    @BindView(R.id.appeal)
    TextView mAppeal;
    @BindView(R.id.cancelOrder)
    TextView mCancelOrder;
    @BindView(R.id.confirm)
    TextView mConfirm;
    private int mOrderId;
    private int mTradeDirection;
    private Unbinder mBind;
    private int mStatus;
    private OtcOrderDetail mOtcOrderDetail;
    private boolean mIsBuyer;
    private boolean mNeedGoogle;
    private WaresUserInfo mWaresUserInfo;
    private OtcProcessor mOtcProcessor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_legal_currency_order_detail, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        mOrderId = extras.getInt(ExtraKeys.ORDER_ID);
        mTradeDirection = extras.getInt(ExtraKeys.TRADE_DIRECTION);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mPayInfo.setEmptyView(mBankEmptyView);
        needGoogle();
        otcOrderDetail();
        otcWaresMine();
        orderPayInfo();
        initSocketListener();
        initEntrustPush();
    }

    private void initSocketListener() {
        mOtcProcessor = new OtcProcessor(new OnDataRecListener() {
            @Override
            public void onDataReceive(String data, int code, String dest) {
                new DataParser<Response<Object>>(data) {

                    @Override
                    public void onSuccess(Response<Object> resp) {
                    }
                }.parse();
            }
        });
        mOtcProcessor.resume();
    }

    private void initEntrustPush() {
        mOtcProcessor.registerEntrust();
    }

    private void otcOrderDetail() {
        Apic.otcOrderDetail(mOrderId, mTradeDirection).tag(TAG)
                .callback(new Callback<Resp<OtcOrderDetail>>() {
                    @Override
                    protected void onRespSuccess(Resp<OtcOrderDetail> resp) {
                        setView(resp.getData());
                    }
                }).fire();
    }

    private void otcWaresMine() {
        Apic.otcWaresMine("", String.valueOf(mOrderId), 1).tag(TAG)
                .callback(new Callback<Resp<WaresUserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<WaresUserInfo> resp) {
                        mWaresUserInfo = resp.getData();
                        setWaresUserInfo(mWaresUserInfo);
                    }
                }).fire();
    }

    private void orderPayInfo() {
        Apic.orderPayInfo(mOrderId).tag(TAG)
                .callback(new Callback<Resp<List<BankCardBean>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<BankCardBean>> resp) {
                        setBankList(resp.getData());
                    }
                }).fire();
    }

    private void otcOrderCancel() {
        Apic.otcOrderCancel(mOrderId).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (mOtcOrderDetail != null) {
                            mOtcOrderDetail.getOrder().setStatus(OTCOrderStatus.ORDER_CANCLED);
                            setView(mOtcOrderDetail);
                        }
                        otcOrderDetail();
                        Intent data = new Intent();
                        data.putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true);
                        setResult(LEGAL_CURRENCY_ORDER_RESULT, data);
                    }
                }).fire();
    }

    private void needGoogle() {
        Apic.needGoogle("DRAW").tag(TAG)
                .callback(new Callback<Resp<Boolean>>() {
                    @Override
                    protected void onRespSuccess(Resp<Boolean> resp) {
                        mNeedGoogle = resp.getData();
                    }
                })
                .fireFreely();
    }

    private void otcOrderConfirm(int status, String drawPass, String googleCode) {
        Apic.otcOrderConfirm(mOrderId, status, drawPass, googleCode).tag(TAG)
                .callback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (mOtcOrderDetail != null) {
                            mOtcOrderDetail.getOrder().setStatus(mIsBuyer ? OTCOrderStatus.ORDER_PAIED : OTCOrderStatus.ORDER_COMPLATED);
                            setView(mOtcOrderDetail);
                        }
                        otcOrderDetail();
                        Intent data = new Intent();
                        data.putExtra(ExtraKeys.MODIFIED_SHOULD_REFRESH, true);
                        setResult(LEGAL_CURRENCY_ORDER_RESULT, data);
                    }
                }).fire();
    }

    private void setWaresUserInfo(WaresUserInfo waresUserInfo) {
        GlideApp
                .with(getContext())
                .load(waresUserInfo.getUserPortrait())
                .circleCrop()
                .into(mHeadPortrait);
        int authStatus = waresUserInfo.getAuthStatus();
        mCertification.setVisibility(authStatus == 1 || authStatus == 2 ? View.VISIBLE : View.GONE);
        switch (authStatus) {
            case 1:
                mCertification.setImageResource(R.drawable.ic_primary_star);
                break;
            case 2:
                mCertification.setImageResource(R.drawable.ic_senior_star);
                break;
            default:
        }
        mUserName.setText(waresUserInfo.getUsername());
        mCountDealRate.setText(getString(R.string.x_done_count_done_rate_x,
                waresUserInfo.getCountDeal(),
                FinanceUtil.formatToPercentage(waresUserInfo.getDoneRate())));
    }

    private void setView(OtcOrderDetail otcOrderDetail) {
        mOtcOrderDetail = otcOrderDetail;
        OtcOrderDetail.OrderBean order = otcOrderDetail.getOrder();
        String coinSymbol = order.getCoinSymbol();
        mTitleBar.setTitle(getString(mTradeDirection == OTCOrderStatus.ORDER_DIRECT_BUY ?
                R.string.buy_x : R.string.sell_x, coinSymbol.toUpperCase()));
        setBottomButtonStatus(order);
        setOrderInfoCard(order, coinSymbol);

        GlideApp
                .with(getContext())
                .load(order.getBuyerPortrait())
                .circleCrop()
                .into(mHeadPortrait);
        mUserName.setText(order.getBuyerName());
        if (mWaresUserInfo != null) {
            setWaresUserInfo(mWaresUserInfo);
        }
    }

    private void setOrderInfoCard(OtcOrderDetail.OrderBean order, String coinSymbol) {
        mTurnover.setText(getString(R.string.x_space_x,
                FinanceUtil.formatWithScale(order.getOrderAmount()),
                order.getPayCurrency().toUpperCase()));
        mPrice.setText(getString(R.string.order_detail_price_x,
                FinanceUtil.formatWithScale(order.getOrderPrice()),
                order.getPayCurrency().toUpperCase(), coinSymbol.toUpperCase()));
        mTradeAmount.setText(getString(R.string.order_detail_amount_x,
                order.getOrderCount(),
                coinSymbol.toUpperCase()));
        mOrderNo.setText(getString(R.string.pound_sign_x, order.getOrderId()));
        switch (order.getStatus()) {
            case OTCOrderStatus.ORDER_CANCLED:
                mOrderStatus.setText(R.string.canceled);
                mCountDownView.setVisibility(View.GONE);
                break;
            case OTCOrderStatus.ORDER_COMPLATED:
                mOrderStatus.setText(R.string.completed);
                mCountDownView.setVisibility(View.GONE);
                break;
            case OTCOrderStatus.ORDER_UNPAIED:
                mCountDownView.setVisibility(View.VISIBLE);
                long endTime = System.currentTimeMillis() + order.getCountDown() * 1000;
                if (System.currentTimeMillis() < endTime) {
                    mCountDownView.setTimes(endTime);
                    mCountDownView.setVisibility(View.VISIBLE);
                    mCountDownView.beginRun();
                } else {
                    mCountDownView.setVisibility(View.GONE);
                }
                mCountDownView.setOnStateChangeListener(new CountDownView.OnStateChangeListener() {
                    @Override
                    public void onStateChange(int countDownState) {
                        if (countDownState == CountDownView.STOPPED) {
                            mCountDownView.setVisibility(View.GONE);
                            otcOrderDetail();
                        }
                    }
                });
                mOrderStatus.setText(R.string.wait_buyer_to_pay);
                break;
            case OTCOrderStatus.ORDER_PAIED:
                mCountDownView.setVisibility(View.GONE);
                mOrderStatus.setText(R.string.buyer_have_paid);
                break;
            default:
        }
    }

    private void setBottomButtonStatus(OtcOrderDetail.OrderBean order) {
        mStatus = order.getStatus();
        LocalUser user = LocalUser.getUser();
        if (user.isLogin()) {
            int id = user.getUserInfo().getId();
            mIsBuyer = id == order.getBuyerId();
        }
        switch (mStatus) {
            case OTCOrderStatus.ORDER_CANCLED:
            case OTCOrderStatus.ORDER_COMPLATED:
                mAppeal.setVisibility(View.GONE);
                mCancelOrder.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                break;
            case OTCOrderStatus.ORDER_UNPAIED:
                mAppeal.setVisibility(View.GONE);
                mCancelOrder.setVisibility(mIsBuyer ? View.VISIBLE : View.GONE);
                mConfirm.setVisibility(mIsBuyer ? View.VISIBLE : View.GONE);
                mConfirm.setText(mIsBuyer ? R.string.i_have_paid : R.string.confirm_transfer_coin);
                mCancelOrder.setVisibility(mIsBuyer ? View.VISIBLE : View.GONE);
                break;
            case OTCOrderStatus.ORDER_PAIED:
                mAppeal.setVisibility(View.VISIBLE);
                mCancelOrder.setVisibility(View.GONE);
                mConfirm.setVisibility(mIsBuyer ? View.GONE : View.VISIBLE);
                mConfirm.setText(mIsBuyer ? R.string.i_have_paid : R.string.confirm_transfer_coin);
                break;
            default:
        }
    }

    private void setBankList(List<BankCardBean> bankList) {
        mPayInfo.setLayoutManager(new LinearLayoutManager(getContext()));
        BankListAdapter adapter = new BankListAdapter();
        adapter.setList(bankList);
        mPayInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mPayInfo.hideAll(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
        mOtcProcessor.pause();
    }

    @OnClick({R.id.sellerInfo, R.id.bankEmptyView, R.id.contractEachOther, R.id.cancelOrder, R.id.appeal, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sellerInfo:
                UniqueActivity.launcher(this, OtcSellUserInfoFragment.class)
                        .putExtra(ExtraKeys.ORDER_ID, mOrderId)
                        .putExtra(ExtraKeys.TRADE_DIRECTION, mTradeDirection == OTCOrderStatus.ORDER_DIRECT_BUY ?
                                OTCOrderStatus.ORDER_DIRECT_SELL : OTCOrderStatus.ORDER_DIRECT_BUY)
                        .execute();
                break;
            case R.id.bankEmptyView:
                break;
            case R.id.contractEachOther:
                Launcher.with(this, OtcTradeChatActivity.class)
                        .putExtra(ExtraKeys.ORDER_ID, mOrderId)
                        .putExtra(ExtraKeys.TRADE_DIRECTION, mTradeDirection)
                        .execute();
                break;
            case R.id.cancelOrder:
                showCancelConfirmView();
                break;
            case R.id.appeal:
                Launcher.with(getActivity(), CustomServiceActivity.class).execute();
                break;
            case R.id.confirm:
                if (mIsBuyer) {
                    otcOrderConfirm(mStatus, "", "");
                } else {
                    showTransferConfirm();
                }
                break;
            default:
        }
    }

    private void showCancelConfirmView() {
        CancelConfirmController cancelConfirmController = new CancelConfirmController(getContext(), new CancelConfirmController.OnConfirmClick() {
            @Override
            public void onConfirm() {
                otcOrderCancel();
            }
        });
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(cancelConfirmController)
                .show();
    }

    private void showTransferConfirm() {
        WithDrawPsdViewController withDrawPsdViewController = new WithDrawPsdViewController(getActivity(),
                new WithDrawPsdViewController.OnClickListener() {
                    @Override
                    public void onConfirmClick(String cashPwd, String googleAuth) {
                        otcOrderConfirm(mStatus, md5Encrypt(cashPwd), mNeedGoogle ? googleAuth : "");
                    }
                });
        withDrawPsdViewController.setShowGoogleAuth(mNeedGoogle);
        SmartDialog smartDialog = SmartDialog.solo(getActivity());
        smartDialog.setCustomViewController(withDrawPsdViewController)
                .show();
    }

    private static class CancelConfirmController extends SmartDialog.CustomViewController {
        ImageView mClose;
        LinearLayout mConfirmUnpaid;
        TextView mConfirm;
        private Context mContext;
        private OnConfirmClick mOnConfirmClick;
        private ImageView mCheck;

        public CancelConfirmController(Context context, OnConfirmClick onConfirmClick) {
            mContext = context;
            mOnConfirmClick = onConfirmClick;
        }

        @Override
        protected View onCreateView() {
            return LayoutInflater.from(mContext).inflate(R.layout.view_otc_cancel_confirm, null, false);
        }

        interface OnConfirmClick {
            void onConfirm();
        }

        @Override
        protected void onInitView(View view, final SmartDialog dialog) {
            mClose = view.findViewById(R.id.close);
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mConfirmUnpaid = view.findViewById(R.id.confirmUnpaid);
            mCheck = view.findViewById(R.id.check);
            mConfirmUnpaid.setSelected(false);
            mConfirmUnpaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConfirmUnpaid.setSelected(!mConfirmUnpaid.isSelected());
                    mConfirm.setEnabled(mConfirmUnpaid.isSelected());
                    mCheck.setImageResource(mConfirmUnpaid.isSelected() ? R.drawable.ic_common_checkmark : 0);
                }
            });
            mConfirm = view.findViewById(R.id.confirm);
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnConfirmClick != null) {
                        mOnConfirmClick.onConfirm();
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    class BankListAdapter extends RecyclerView.Adapter {

        private List<BankCardBean> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_list, parent, false);
            return new BankListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof BankListHolder) {
                ((BankListHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(List<BankCardBean> list) {
            mList.clear();
            mList.addAll(list);
        }

        class BankListHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.payTypeIcon)
            ImageView mPayTypeIcon;
            @BindView(R.id.accountType)
            TextView mAccountType;
            @BindView(R.id.bankName)
            TextView mBankName;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.cardNum)
            TextView mCardNum;
            @BindView(R.id.bankBranch)
            TextView mBankBranch;

            public BankListHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindData(BankCardBean bankCardBean) {
                switch (bankCardBean.getPayType()) {
                    case BankCardBean.PAYTYPE_ALIPAY:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_alipay_s);
                        mAccountType.setText(R.string.alipay);
                        break;
                    case BankCardBean.PAYTYPE_WX:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_wechat_s);
                        mAccountType.setText(R.string.wechatpay);
                        break;
                    case BankCardBean.PAYTYPE_BANK:
                        mPayTypeIcon.setImageResource(R.drawable.ic_pay_unionpay_s);
                        mAccountType.setText(R.string.bank_card);
                        break;
                    default:
                }
                String bankName = bankCardBean.getBankName();
                if (!TextUtils.isEmpty(bankName)) {
                    mBankName.setText(bankName);
                    mBankName.setVisibility(View.VISIBLE);
                } else {
                    mBankName.setVisibility(View.GONE);
                }
                String realName = bankCardBean.getRealName();
                if (!TextUtils.isEmpty(realName)) {
                    mName.setText(realName);
                    mName.setVisibility(View.VISIBLE);
                } else {
                    mName.setVisibility(View.GONE);
                }
                mCardNum.setText(bankCardBean.getCardNumber());
                String bankBranch = bankCardBean.getBankBranch();
                if (!TextUtils.isEmpty(bankBranch)) {
                    mBankBranch.setText(bankBranch);
                    mBankBranch.setVisibility(View.VISIBLE);
                } else {
                    mBankBranch.setVisibility(View.GONE);
                }
            }
        }
    }
}

package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.fragment.dialog.UploadUserImageDialogFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.utils.ValidationWatcher;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import sbai.com.glide.GlideApp;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class FeedbackFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.textSize)
    TextView mTextSize;
    @BindView(R.id.imageNum)
    TextView mImageNum;
    private Unbinder mBind;
    private static final int MAX_TEXT_SIZE = 200;
    private static final int MAX_IMAGE_SIZE = 4;
    private ArrayList<String> mImages = new ArrayList<>();
    private FeedbackPicAdapter mPicAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mPicAdapter = new FeedbackPicAdapter(getContext());
        mPicAdapter.setList(mImages);
        mPicAdapter.setOnImageClickListener(new FeedbackPicAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(boolean selectImage) {
                if (selectImage) {
                    selectImage();
                }
            }
        });
        mRecyclerView.setAdapter(mPicAdapter);
        mEditText.addTextChangedListener(mValidationWatcher);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT_SIZE)});
        mTextSize.setText(getString(R.string.x_faction_x, 0, MAX_TEXT_SIZE));
        mImageNum.setText(getString(R.string.x_faction_x, mImages.size(), MAX_IMAGE_SIZE));
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mTextSize.setText(getString(R.string.x_faction_x, s.length(), MAX_TEXT_SIZE));
        }
    };

    private void selectImage() {
        UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_OPEN_CUSTOM_GALLERY,
                "", -1, "", MAX_IMAGE_SIZE - mImages.size())
                .setOnImagePathListener(new UploadUserImageDialogFragment.OnImagePathListener() {
                    @Override
                    public void onImagePath(int index, String imagePath) {
                        String[] split = imagePath.split(",");
                        for (String aSplit : split) {
                            if (!mImages.contains(aSplit)) {
                                mImages.add(aSplit);
                            }
                        }
                        mPicAdapter.notifyDataSetChanged();
                        mImageNum.setText(getString(R.string.x_faction_x, mImages.size(), MAX_IMAGE_SIZE));
                    }
                }).show(getChildFragmentManager());
    }

    // TODO: 2018/5/31 调试接口
    public void addFeedback(String content, String contactInfo, String feedbackPic) {
        Apic.addFeedback(content, contactInfo, feedbackPic)
                .callback(new Callback<Object>() {
                    @Override
                    protected void onRespSuccess(Object resp) {
                        mEditText.setText(null);
                    }
                }).fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        String content = mEditText.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            addFeedback(content, null, null);
        }
    }

    static class FeedbackPicAdapter extends RecyclerView.Adapter {
        private final Context mContext;
        private ArrayList<String> mList;
        private static OnImageClickListener mOnImageClickListener;

        public FeedbackPicAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feedback_image, parent, false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ImageHolder) {
                ((ImageHolder) holder).bindData(mContext, mList, position);
            }
        }

        @Override
        public int getItemCount() {
            if (mList.size() == 0) {
                return 1;
            }
            if (mList.size() >= MAX_IMAGE_SIZE) {
                return MAX_IMAGE_SIZE;
            }
            return mList.size() + 1;
        }

        public void setList(ArrayList<String> list) {
            mList = list;
        }

        public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
            mOnImageClickListener = onImageClickListener;
        }

        static class ImageHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.image)
            ImageView mImage;

            ImageHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            void bindData(Context context, final ArrayList<String> list, final int position) {
                if (list.size() > position) {
                    GlideApp
                            .with(context)
                            .load(list.get(position))
                            .into(mImage);
                }
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnImageClickListener != null) {
                            mOnImageClickListener.onImageClick(position >= list.size());
                        }
                    }
                });
            }
        }

        interface OnImageClickListener {
            void onImageClick(boolean selectImage);
        }
    }
}
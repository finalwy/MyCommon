package com.finalwy.basecomponent.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finalwy.basecomponent.utils.ContinuationClickUtils;
import com.finalwy.basecomponent.utils.ToastUtil;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.RxFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author wy
 * @Date 2020-02-18
 */
public abstract class BaseFragment<T extends BasePresenter> extends RxFragment implements BaseView {
    protected String TAG = getClass().getSimpleName();
    private Unbinder unbinder;
    protected Context mContext;
    private ToastUtil mToastUtil;
    protected T mPresenter;
    private CompositeDisposable mDisposables;
    protected Handler mHandler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(getViewLayout(), container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        mToastUtil = new ToastUtil(mContext);
        //注册eventbus
        Disposable disposable = RxBus.getDefault()
                .register(RxEvent.class, event -> {
                    int eventCode = event.getCode();
                    switch (eventCode) {
                        case RxEvent.EVENT_CLOSE_ALL_ACTIVITY:
                            break;
                        default:
                            onEvent(event);
                            break;
                    }
                });
        addDispose(disposable);
        initData();
        initView();
        return mRootView;
    }

    @Override
    public LifecycleTransformer bindLifecycle() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * RxJava 添加订阅
     */
    protected void addDispose(Disposable disposable) {
        if (mDisposables == null) {
            mDisposables = new CompositeDisposable();
        }
        //将所有disposable放入,集中处理
        mDisposables.add(disposable);
    }

    protected abstract int getViewLayout();


    protected void initView() {
    }

    protected void initData() {
    }

    protected void onEvent(RxEvent onEvent) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        if (mDisposables != null) {
            RxBus.getDefault().unregister(mDisposables);
            mDisposables.clear();
            mDisposables = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Fragment被移除Activity的时候，清空handler里面的所有消息
        mHandler.removeCallbacksAndMessages(null);

    }

    protected void showToast(String msg) {
        mToastUtil.showToast(msg);
    }

    protected void showLongToast(String msg) {
        mToastUtil.showToastLong(msg);
    }

    /**
     * 是否点击过快
     *
     * @return
     */
    public boolean isFastClick() {
        return ContinuationClickUtils.isFastClick();
    }
}

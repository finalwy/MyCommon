package com.finalwy.basecomponent.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finalwy.basecomponent.utils.ToastUtil;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author wy
 * @Date 2020-02-18
 */
public abstract class LazyBaseFragment<T extends BasePresenter> extends BaseFragment {
    private Unbinder unbinder;
    protected Context mContext;
    private ToastUtil mToastUtil;
    protected T mPresenter;
    //当前Fragment是否可见
    private boolean isVisible = false;
    //是否与View建立起映射关系
    private boolean isInitView = false;
    //是否是第一次加载数据
    private boolean isFirstLoad = true;

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
                .register(RxEvent.class, new Consumer<RxEvent>() {
                    @Override
                    public void accept(RxEvent event) {
                        int eventCode = event.getCode();
                        Log.e("RxBus", event.toString());
                        switch (eventCode) {
                            case RxEvent.EVENT_CLOSE_ALL_ACTIVITY:
                                break;
                            default:
                                onEvent(event);
                                break;
                        }
                    }
                });
        addDispose(disposable);
        initData();
        initView();
        isInitView = true;
        return mRootView;
    }

    @Override
    public LifecycleTransformer bindLifecycle() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lazyLoadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();
        } else {
            isVisible = false;

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 处理数据
     */
    private void lazyLoadData() {
        //可视、第一次加载、view init done
        if (isFirstLoad && isVisible && isInitView) {
            isFirstLoad = false;
            business();
        }
    }
    protected void onEvent(RxEvent onEvent) {

    }
    protected abstract void business();

    protected abstract int getViewLayout();


    protected void initView() {
    }

    protected void initData() {
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
    }

    protected void showToast(String msg) {
        mToastUtil.showToast(msg);
    }

    protected void showLongToast(String msg) {
        mToastUtil.showToastLong(msg);
    }
}

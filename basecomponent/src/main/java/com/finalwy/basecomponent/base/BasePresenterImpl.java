package com.finalwy.basecomponent.base;

/**
 * @author wy
 * @Date 2020-02-18
 */
public class BasePresenterImpl<T extends BaseView> implements BasePresenter {
    protected String TAG = getClass().getSimpleName();
    protected T mView;

    public BasePresenterImpl(T view) {
        mView = view;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

}

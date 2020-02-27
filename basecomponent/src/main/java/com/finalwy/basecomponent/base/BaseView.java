package com.finalwy.basecomponent.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author wy
 * @Date 2020-02-18
 */
public interface BaseView {
    LifecycleTransformer bindLifecycle();
}

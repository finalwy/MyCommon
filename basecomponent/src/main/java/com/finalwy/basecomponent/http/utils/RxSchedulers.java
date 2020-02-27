package com.finalwy.basecomponent.http.utils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 通用的Rx线程转换类
 *
 * @author wy
 * @Date 2020-02-18
 */
public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> applySchedulers() {

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return (ObservableSource<T>) (upstream)
                        .map(new Function<T, Object>() {
                            @Override
                            public Object apply(T t) throws Exception {
                                return t;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                            @Override
                            public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                                // 用来统一处理Http的resultCode, 并将HttpResult的Data部分剥离出来返回给subscriber
                                return Observable.error(ExceptionConverter.convertException(throwable));
                            }
                        });
            }
        };


    }

}


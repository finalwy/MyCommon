package com.finalwy.basecomponent.base;

/**
 * 事件发送包装类
 * @author wy
 * @Date 2020-02-18
 */
public class RxEvent<T> {
    public static final int EVENT_CLOSE_ALL_ACTIVITY = 10001;

    /**
     * reserved data
     */
    private T data;

    /**
     * this code distinguish between different events
     */
    private int eventCode = -1;

    public RxEvent(int eventCode) {
        this(eventCode, null);
    }

    public RxEvent(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    /**
     * get event code
     *
     * @return
     */
    public int getCode() {
        return this.eventCode;
    }

    /**
     * get event reserved data
     *
     * @return
     */
    public T getData() {
        return this.data;
    }


}

package com.bql.utils;

/**
 * ClassName: EventManager <br>
 * Description:事件管理<br>
 * Author: Cyarie <br>
 * Created: 2016/7/5 11:21 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class EventManager<T> {

    /**
     * reserved data
     */
    private T data;

    /**
     * this code distinguish between different events
     */
    private int eventCode = -1;

    public EventManager() {
    }

    public EventManager(int eventCode) {
        this(eventCode, null);
    }

    public EventManager(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    /**
     * get event code
     *
     * @return
     */
    public int getEventCode() {
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

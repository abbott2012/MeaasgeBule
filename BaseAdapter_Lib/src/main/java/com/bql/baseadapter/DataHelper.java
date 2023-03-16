package com.bql.baseadapter;

import java.util.List;

/**
 * 数据操作接口
 * Created by Cyarie on 2016/3/30.
 */
public interface DataHelper<T> {


    boolean isEnabled(int position);

    /**
     * 添加单个数据到列表头部
     *
     * @param data
     */
    void addToHead(T data);

    /**
     * 添加单个数据到列表尾部
     *
     * @param data
     */
    boolean addToLast(T data);


    /**
     * 添加数据集到列表头部
     *
     * @param list
     */
    boolean addToHead(List<T> list);

    /**
     * 添加数据集到列表尾部
     *
     * @param list
     */
    boolean addToLast(List<T> list);

    /**
     * 添加数据集合到指定位置
     *
     * @param startPos 数据添加的位置
     * @param list     数据集合
     */
    boolean add(int startPos, List<T> list);

    /**
     * 添加单个数据到指定位置
     *
     * @param startPos 数据添加的位置
     * @param data     数据
     */
    void add(int startPos, T data);

    /**
     * 获取index对应的数据
     *
     * @param index 位置
     * @return 数据对象
     */
    T getData(int index);

    /**
     * 将某一个数据修改
     *
     * @param oldData 旧的数据
     * @param newData 新的数据
     */
    void updateItem(T oldData, T newData);


    /**
     * 修改对应位置的数据
     *
     * @param index 位置
     * @param data  新的数据
     */
    void updateItem(int index, T data);

    /**
     * 删除数据
     *
     * @param data
     */
    boolean remove(T data);

    /**
     * 删除对应位置的数据
     *
     * @param index
     */
    void remove(int index);

    /**
     * 替换所有数据
     *
     * @param list
     */
    void replaceAll(List<T> list);

    /**
     * 清除所有数据
     */
    void clear();

    /**
     * 判断数据集合中是否包含这个对象
     *
     * @param data 判断对象
     * @return true|false
     */
    boolean contains(T data);
}

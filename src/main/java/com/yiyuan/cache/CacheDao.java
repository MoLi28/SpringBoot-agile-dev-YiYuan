package com.yiyuan.cache;

import java.io.Serializable;

/**
 * 缓存数据接口
 *
 * @author MoLi
 */
public interface CacheDao {

    //全局配置
    String CONSTANT = "CONSTANT";
    String SESSION = "SESSION";

    /**
     * 设置hash key值
     *
     * @param key
     * @param k
     * @param val
     */
    void hset(Serializable key, Serializable k, Object val);

    /**
     * 获取hash key值
     *
     * @param key
     * @param k
     * @return
     */
    Serializable hget(Serializable key, Serializable k);

    /**
     * 获取hash key值
     * @param key
     * @param k
     * @param klass
     * @param <T>
     * @return
     */
    <T>T hget(Serializable key, Serializable k, Class<T> klass);

    /**
     * 设置key值，超时失效
     *
     * @param key
     * @param val
     */
    void set(Serializable key, Object val);



    /**
     * 获取key值
     *
     * @param key
     * @param klass
     * @return
     */
      <T>T get(Serializable key, Class<T> klass);
      String get(Serializable key);


      void del(Serializable key);
      void hdel(Serializable key, Serializable k);
}

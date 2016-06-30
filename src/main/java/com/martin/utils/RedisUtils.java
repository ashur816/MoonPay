package com.martin.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisCommons
 * @Description: redis工具类
 * @author ZXY
 * @date 2016/6/30 16:40
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RedisUtils {
    private Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    //注入redisTemplate
    private RedisTemplate redisTemplate;


    //设置某日超时天数
    private int defaultTimeoutDay = 1;

    //设置超时时间
    private String defaultTimeoutTimes = "03:00:00";

    //缓存开关
    private volatile boolean redisSwitch = true;

    /**
     * redis set集合有过期时间
     * @param key reids key
     * @param value 需要缓存的数据
     * @param timeOut 超时时长
     * @param unit    超时时长单位
     * @throws IOException
     */
    public boolean set(String key, Object value, long timeOut, TimeUnit unit) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
                    boundValueOperations.set(value);
                    return boundValueOperations.expire(timeOut, unit);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return false;
    }

    /**
     * redis set集合有默认过期时间
     * @param key reids key
     * @param value 需要缓存的数据
     */
    public boolean set(String key, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
                    boundValueOperations.set(value);
                    return boundValueOperations.expireAt(getDefaultExpireDate());
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return false;
    }

    /**
     * set集合可设置过期时间
     * @param key
     * @param value
     * @param date
     */
    public boolean set(String key, Object value, Date date) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
                    boundValueOperations.set(value);
                    boundValueOperations.expireAt(date);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return false;
    }

    /**
     * set集合 没有过期时间
     * @param key
     * @param value
     */
    public void setNoExpire(String key, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
                    boundValueOperations.set(value);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * get
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            if (redisSwitch) {
                return (T) redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            logger.error("get error={}", e);
        }
        return null;
    }

    /**
     * getIncr 解决redis increment GUG 取值问题
     * @param key
     * @param clazz
     * @return
     */
    public <T> T getIncr(String key, Class<T> clazz) {
        try {
            if (redisSwitch) {
                redisTemplate.setValueSerializer(new StringRedisSerializer());
                String objectJson = (String) redisTemplate.opsForValue().get(key);
                redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return JSON.parseObject(objectJson, clazz);
            }
        } catch (Exception e) {
            logger.error("get error={}", e);
        }
        return null;
    }


    /**
     * 根据key删除
     * @param key
     */
    public void delete(String key) {
        if (redisSwitch) {
            try {
                redisTemplate.delete(key);
            } catch (Throwable e) {
                logger.error("delete error:", e);
            }
        }
    }


    /**
     * 根据key删除
     * @param key
     */
    public void hDelete(String key, String hKey) {
        if (redisSwitch) {
            try {
                BoundHashOperations operations = redisTemplate.boundHashOps(key);
                operations.delete(hKey);
            } catch (Exception e) {
                logger.error("delete error:", e);
            }
        }
    }

    /**
     * get  List集合
     * @param key
     * @param clazz
     * @return
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            if (redisSwitch) {
                return (List<T>) redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            logger.error("get error={}", e);
        }
        return null;
    }

    /**
     * 存放hash集合可设置过期时间
     * @param key
     * @param value
     * @param date
     */
    public void hSet(String key, String hKey, Object value, Date date) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.put(hKey, value);
                    operations.expireAt(date);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 存放hash集合可设置过期时间
     * @param key
     * @param hKey
     * @param value
     * @param timeOut
     * @param unit
     */
    public void hSet(String key, String hKey, Object value, long timeOut, TimeUnit unit) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.put(hKey, value);
                    operations.expire(timeOut, unit);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 存放hash集合使用默认失效时间
     * @param key
     * @param hKey
     * @param value
     */
    public void hSet(String key, String hKey, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.put(hKey, value);
                    operations.expireAt(getDefaultExpireDate());
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 存放hash集合
     * @param key
     * @param hKey
     * @param value
     */
    public void hSetNoExpire(String key, String hKey, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.put(hKey, value);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 获得默认失效时间
     */
    private Date getDefaultExpireDate() {
        return DateUtils.getTomorrow();
    }

    /**
     * 存放Map集合 有默认失效时间
     * @param key
     * @param map
     * @param value
     */
    public void hSetAll(String key, Map<String, String> map, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.putAll(map);
                    operations.expireAt(getDefaultExpireDate());
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 存放Map集合 有默认失效时间
     * @param key
     * @param map
     * @param value
     * @param timeout
     * @param unit
     */
    public void hSetAll(String key, Map<String, String> map, Object value, long timeout, TimeUnit unit) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.putAll(map);
                    operations.expire(timeout, unit);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }

    /**
     * 存放Map集合 有默认失效时间
     * @param key
     * @param map
     * @param value
     */
    public void hSetAllNoExpire(String key, Map<String, String> map, Object value) {
        try {
            if (redisSwitch) {
                if (ObjectUtils.isNotEmpty(value)) {
                    BoundHashOperations operations = redisTemplate.boundHashOps(key);
                    operations.putAll(map);
                }
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
    }


    /**
     * hash获取自增 getIncr 解决redis increment GUG 取值问题
     * @param key
     * @param hKey
     * @param clazz
     * @return
     */
    public <T> T hGetIncr(String key, String hKey, Class<T> clazz) {
        try {
            if (redisSwitch) {
                redisTemplate.setHashValueSerializer(new StringRedisSerializer());
                String objectJson = (String) redisTemplate.opsForHash().get(key, hKey);
                redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
                if (StringUtils.isBlank(objectJson)) {
                    return null;
                }
                return JSON.parseObject(objectJson, clazz);
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return null;
    }

    /**
     * hash存放
     * @param key
     * @param hKey
     * @param clazz
     * @return
     */
    public <T> T hGet(String key, String hKey, Class<T> clazz) {
        try {
            if (redisSwitch) {
                return (T) redisTemplate.opsForHash().get(key, hKey);
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return null;
    }

    /**
     * 获得hash全部值
     * @param key
     * @return
     */
    public Map<String, Object> hGetAll(String key) {
        try {
            if (redisSwitch) {
                return redisTemplate.opsForHash().entries(key);
            }
        } catch (Exception e) {
            logger.error("set error={}", e);
        }
        return null;
    }

    /**
     * set 集合式存放
     * @param key
     * @param value
     * @param score
     * @return
     */
    public boolean zAdd(String key, Object value, double score) {
        if (redisSwitch) {
            BoundZSetOperations<String, Object> opt = redisTemplate.boundZSetOps(key);
            return opt.add(value, score);
        }
        return false;
    }

    /**
     * 根据下标大小获取Set
     * @param key
     * @param min
     * @param max
     * @param clazz
     * @return
     */
    public <T> Set<T> zrange(String key, double min, double max, Class<T> clazz) {
        if (redisSwitch) {
            BoundZSetOperations<String, Object> opt = redisTemplate.boundZSetOps(key);
            Set<Object> set = opt.rangeByScore(min, max);
            Set<T> result = new LinkedHashSet<T>(set.size());
            for (Object obj : set) {
                result.add((T) obj);
            }
            return result;
        }
        return null;
    }

    /**
     * set 删除
     * @param key
     * @param min
     * @param max
     */
    public void zDelByScore(String key, double min, double max) {
        if (redisSwitch) {
            BoundZSetOperations<String, String> opt = redisTemplate.boundZSetOps(key);
            opt.removeRangeByScore(min, max);
        }
    }

    /**
     * 自增带有效时间的
     * @param key
     * @param value
     * @return
     */
    public int incr(String key, int value, long timeout, TimeUnit unit) {
        if (redisSwitch) {
            BoundValueOperations operations = redisTemplate.boundValueOps(key);
            Long i = operations.increment(value);
            operations.expire(timeout, unit);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }


    /**
     * 自增指定失效时间点
     * @param key
     * @param value
     * @return
     */
    public int incr(String key, int value, Date date) {
        if (redisSwitch) {
            BoundValueOperations operations = redisTemplate.boundValueOps(key);
            Long i = operations.increment(value);
            operations.expireAt(date);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }

    /**
     * 自增
     * @param key
     * @param value
     * @return
     */
    public int incr(String key, int value) {
        if (redisSwitch) {
            BoundValueOperations operations = redisTemplate.boundValueOps(key);
            Long i = operations.increment(value);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }


    /**
     * 自减
     * @param key
     * @param value
     * @return
     */
    public int derc(String key, int value) {
        if (redisSwitch) {
            BoundValueOperations operations = redisTemplate.boundValueOps(key);
            Long i = operations.increment(-value);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }

    /**
     * hash自增
     * @param key
     * @param value
     * @return
     */
    public int hIncr(String key, String hKey, int value) {
        if (redisSwitch) {
            BoundHashOperations operations = redisTemplate.boundHashOps(key);
            Long i = operations.increment(hKey, value);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }

    /**
     * hash自增指定失效时间点
     * @param key
     * @param value
     * @return
     */
    public int hIncr(String key, String hKey, int value, Date date) {
        if (redisSwitch) {
            BoundHashOperations operations = redisTemplate.boundHashOps(key);
            Long i = operations.increment(hKey, value);
            operations.expireAt(date);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }

    /**
     * hash自减
     * @param key
     * @param value
     * @return
     */
    public int hDecr(String key, String hKey, int value) {
        if (redisSwitch) {
            BoundHashOperations operations = redisTemplate.boundHashOps(key);
            Long i = operations.increment(hKey, -value);
            if (i != null) {
                return i.intValue();
            }
        }
        return 0;
    }

    /**
     * 队列顶部插入数据
     * @param key
     * @param obj
     */
    public long inPop(String key, Object obj) {
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            return operations.leftPush(obj);
        }
        return 0;
    }

    /**
     * 队列顶部插入数据
     * @param key
     * @param obj
     */
    public long inPop(String key, Object obj, Date date) {
        long result = 0;
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            result = operations.leftPush(obj);
            operations.expireAt(date);
        }
        return result;
    }

    /**
     * 队列插入list数据
     * @param key
     * @param objs
     */
    public long inPopAll(String key, Date date, Object... objs) {
        long result = 0;
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            result = operations.leftPushAll(objs);
            operations.expireAt(date);
        }
        return result;
    }

    /**
     * 队列插入list数据
     * @param key
     * @param objs
     */
    public long inPopAll(String key, Object... objs) {
        long result = 0;
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            result = operations.leftPushAll(objs);
        }
        return result;
    }

    /**
     * 队列底部取出
     * @param key
     * @param obj
     */
    public <T> T getBottomPop(String key, Object obj, Class<T> clazz) {
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            return (T) operations.rightPop();
        }
        return null;
    }

    /**
     * 队列顶部取出
     * @param key
     * @param obj
     */
    public <T> T getTopPop(String key, Object obj, Class<T> clazz) {
        if (redisSwitch) {
            BoundListOperations operations = redisTemplate.boundListOps(key);
            return (T) operations.leftPop();
        }
        return null;
    }

    public void setRedisSwitch(boolean redisSwitch) {
        this.redisSwitch = redisSwitch;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setDefaultTimeoutDay(int defaultTimeoutDay) {
        this.defaultTimeoutDay = defaultTimeoutDay;
    }

    public void setDefaultTimeoutTimes(String defaultTimeoutTimes) {
        this.defaultTimeoutTimes = defaultTimeoutTimes;
    }


}

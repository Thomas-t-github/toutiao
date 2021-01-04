package com.toutiao.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {

        pool = new JedisPool("localhost",6379);
    }

    public Jedis getJedis(){
        return pool.getResource();
    }


    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
    public void setObject(String key,Object obj){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key, JSON.toJSONString(obj));
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
    public <T> T getObject(String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String value = jedis.get(key);
            if (value != null){
                return JSON.parseObject(value,clazz);
            }
            return null;
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return false;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
    public void setex(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.setex(key,10,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key,String value){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
    public List<String> brpop(int timeout,String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("Jedis发生异常："+e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

}

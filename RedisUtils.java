package com.jec36.dfs.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jec36.dfs.utils.Constants.CacheType;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

	protected static ReentrantLock lockPool = new ReentrantLock();
	protected static ReentrantLock lockJedis = new ReentrantLock();

	protected static Logger logger = Logger.getLogger(RedisUtils.class);

	// Redis服务器IP
	private static String ADDR_ARRAY = "127.0.0.1";

	// Redis的端口号
	private static int PORT = 6379;

	// 访问密码
	private static String AUTH = "";
	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 600;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 300;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 7500;

	// 超时时间
	private static int TIMEOUT = 15000;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = false;

	private static JedisPool jedisPool = null;

	/**
	 * redis过期时间,以秒为单位
	 */
	public final static int EXRP_HOUR = 60 * 60; // 一小时
	public final static int EXRP_DAY = 60 * 60 * 24; // 一天
	public final static int EXRP_MONTH = 60 * 60 * 24 * 30; // 一个月

	/**
	 * 初始化Redis连接池
	 */
	private static void initialPool() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT);
		} catch (Exception e) {
			logger.error("First create JedisPool error : " + e);
		}
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static void poolInit() {
		lockPool.lock();
		try {
			if (jedisPool == null) {
				initialPool();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lockPool.unlock();
		}
	}

	public static Jedis getJedis() {
		lockJedis.lock();
		if (jedisPool == null) {
			poolInit();
		}
		Jedis jedis = null;
		try {
			if (jedisPool != null) {
				jedis = jedisPool.getResource();
			}
		} catch (Exception e) {
			logger.error("Get jedis error : " + e);
		} finally {
			returnResource(jedis);
			lockJedis.unlock();
		}
		return jedis;
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null && jedisPool != null) {
			jedis.close();
		}
	}

	/**
	 * 设置 String
	 * 
	 * @param key
	 * @param value
	 */
	public static void setString(String key, String value) {
		try {
			value = StringUtils.isEmpty(value) ? "" : value;
			getJedis().set(key, value);
		} catch (Exception e) {
			logger.error("Set String error : " + e);
		}
	}

	/**
	 * 获取String值
	 * 
	 * @param key
	 * @return value
	 */
	public static String getString(String key) {
		if (getJedis() == null || !getJedis().exists(key)) {
			return null;
		}
		return getJedis().get(key);
	}

	// 通过序列化存入Redis
	/*public synchronized static void JedisSet(CacheType key, Map<String, Object> hash) {

		try {
			byte[] Jkey = key.toString().getBytes();
			byte[] Jvalue = serialize(hash);
			getJedis().set(Jkey, Jvalue);
		} catch (Exception e) {
			logger.error("Set key error : " + e);
		}

		// jedis.set(Jkey, serialize(hash));

	}*/
	
	public static Object get(String key) {

		byte[] Jkey = key.toString().getBytes();
		if (getJedis() == null || !getJedis().exists(Jkey)) {
			return null;
		}
		byte[] value = getJedis().get(Jkey);
		return unserialize(value);
	}
	
	public static void set(String key, Object bean) {
		try {
			//Thread.sleep(1000);
			byte[] Jkey = key.toString().getBytes();
			byte[] value = serialize(bean);
			getJedis().set(Jkey, value);

		} catch (Exception e) {
			logger.error("Set hset error : " + e);
		}
	}

	public static void hset(CacheType key, String field, Object bean) {
		try {

			byte[] Jkey = key.toString().getBytes();

			byte[] Jfield = field.getBytes();
			byte[] value = serialize(bean);
			getJedis().hset(Jkey, Jfield, value);

		} catch (Exception e) {
			logger.error("Set hset error : " + e);
		}
	}

/*	// 通过cacheType和key，反序列化后，获取redis中存储的值
	public synchronized static Object get(Object cacheType, String key) {
		byte[] Jkey = cacheType.toString().getBytes();
		if (getJedis() == null || !getJedis().exists(Jkey)) {
			return null;
		}
		byte[] value = getJedis().get(Jkey);
		Map<String, Object> hash = (Map<String, Object>) unserialize(value);
		return hash.get(key);
	}*/

	public static Object hget(Object cacheType, String key) {

		byte[] Jkey = cacheType.toString().getBytes();
		byte[] Jfield = key.getBytes();
		if (getJedis() == null || !getJedis().exists(Jkey)) {
			return null;
		}
		byte[] value = getJedis().hget(Jkey, Jfield);
		return unserialize(value);
		
	}

	public static Map<String, Object> getMap(Object cacheType) {

		
		byte[] Jkey = cacheType.toString().getBytes();
		if (getJedis() == null || !getJedis().exists(Jkey)) {
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		Set<String> fields = getJedis().hkeys(cacheType.toString());
		for(String field : fields){
			byte[] value = getJedis().hget(cacheType.toString().getBytes(), field.getBytes());
			map.put(field, unserialize(value));
		}
		
		
		return  map;
	}

/*	public synchronized static void hput(Object cacheType, String key, Object value) {

		try {

			byte[] Jkey = cacheType.toString().getBytes();
			byte[] Jfield = key.getBytes();
			getJedis().hset(Jkey, Jfield, serialize(value));

		} catch (Exception e) {
			logger.error("Set hput error : " + e);
		}

	}*/

/*	public synchronized static void put(Object cacheType, String key, Object value) {

		try {
			
			byte[] Jkey = cacheType.toString().getBytes();
			byte[] Jfield = key.getBytes();
			
			byte[] Jvalue = getJedis().get(Jkey);
			Map<String, Object> map = (Map<String, Object>) unserialize(Jvalue);
			map.put(key, value);
			getJedis().set(Jkey, serialize(map));

		} catch (Exception e) {
			logger.error("Set put error : " + e);
		}

	}*/

	public static void remove(Object cacheType, String key) {

		try {
			byte[] Jkey = cacheType.toString().getBytes();
			byte[] Jfield = key.getBytes();
			getJedis().hdel(Jkey, Jfield);
		} catch (Exception e) {
			logger.error("Set remove error : " + e);
		}

	}
	
	public static void remove(String key) {

		try {
			byte[] Jkey = key.getBytes();
			getJedis().del(Jkey);
		} catch (Exception e) {
			logger.error("Set remove error : " + e);
		}

	}

	/**
	 * serialize Object
	 * 
	 * @param object
	 * @return byte[]
	 */
	public static byte[] serialize(final Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * unserialize byte[]
	 * 
	 * @param bytes
	 * @return Object
	 */
	public static Object unserialize(final byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}
	 /**
     * unserialize hash Map<byte[], byte[]>
     * @param hash
     * @return Map<String, Object>
     */
    public static Map<String, Object> unserializehmbb2mso(final Map<byte[], byte[]> hash) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Set<byte[]> keys = hash.keySet();
            if (keys != null && keys.size() > 0) {
                for (byte[] key : keys) {
                    result.put(unserialize(key).toString(), unserialize(hash.get(key)));
                }
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
        }
        return result;
    }
}

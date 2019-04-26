package com.owinfo.rabbit.test;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置常量
 * @author pengjunjie
 */
public class QueueConstant {

	public static final String HOST;
	public static final int PORT;
	public static final String VHOST;
	public static final String USERNAME;
	public static final String PASSWORD;

	/**
	 *  order_queue 队列名称
	 */
	public static final String ORDER_QUEUE;

	/**
	 *  order_exchange 订单交换机
	 */
	public static final String ORDER_EXCHANGE;

	/**
	 *  order_key 订单路由键
	 */
	public static final String ORDER_KEY;

	/**
	 *  inventory_queue 清单队列
	 */
	public static final String INVENTORY_QUEUE;

	/**
	 *  inventory_key 清单关键字
	 */
	public static final String INVENTORY_KEY;

	/**
	 *  inventory_exchange 清单交换机
	 */
	public static final String INVENTORY_EXCHANGE;


	/**
	 *  direct_queue 队列
	 */
	public static final String DIRECT_QUEUE;

	/**
	 *  direct 类型交换机
	 */
	public static final String DIRECT_EXCHANGE;

	/**
	 *  direct 交换机的路由键
	 */
	public static final String DIRECT_KEY;

	/**
	 *  fanout交换类型队列
	 */
	public static final String FANOUT_QUEUE1;

	/**
	 *  fanout交换类型队列
	 */
	public static final String FANOUT_QUEUE2;

	/**
	 *  fanout交换类型队列
	 */
	public static final String FANOUT_QUEUE3;


	/**
	 *  fanout类型交换机
	 */
	public static final String FANOUT_EXCHANGE;

	/**
	 *  fanout类型路由键
	 */
	public static final String FANOUT_KEY;


	/**
	 *  topic交换类型队列
	 */
	public static final String TOPIC_QUEUE;

	/**
	 *  topic类型交换机
	 */
	public static final String TOPIC_EXCHANGE;

	/**
	 *  topic类型路由键
	 */
	public static final String TOPIC_KEY;

	/**
	 *  死信队列名称
	 */
	public static final String DEAD_QUEUE;

	/**
	 *  死信队列交换机
	 */
	public static final String DEAD_EXCHANGE;

	/**
	 *  死信队列路由键
	 */
	public static final String DEAD_KEY;

	/**
	 *  业务队列
	 */
	public static final String BUSINESS_QUEUE;

	/**
	 *  业务队列交换机
	 */
	public static final String BUSINESS_EXCHANGE;

	/**
	 *  业务队列路由键
	 */
	public static final String BUSINESS_KEY;

	static {
		ClassPathResource classPathResource = new ClassPathResource("queue.properties");
		Properties queueProperty = new Properties();
		try {
			InputStream stream = classPathResource.getInputStream();
			queueProperty.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		HOST = queueProperty.get("host").toString();
		PORT = Integer.parseInt(queueProperty.get("port").toString());
		VHOST = queueProperty.get("vhost").toString();
		USERNAME = queueProperty.get("username").toString();
		PASSWORD = queueProperty.get("password").toString();

		ORDER_QUEUE = queueProperty.get("order_queue").toString();
		ORDER_EXCHANGE = queueProperty.get("order_exchange").toString();
		ORDER_KEY = queueProperty.get("order_key").toString();

		INVENTORY_QUEUE = queueProperty.get("inventory_queue").toString();
		INVENTORY_EXCHANGE = queueProperty.get("inventory_exchange").toString();
		INVENTORY_KEY = queueProperty.get("inventory_key").toString();

		DIRECT_QUEUE = queueProperty.get("direct_queue").toString();
		DIRECT_EXCHANGE = queueProperty.get("direct_exchange").toString();
		DIRECT_KEY = queueProperty.get("direct_key").toString();

		FANOUT_QUEUE1 = queueProperty.get("fanout_queue1").toString();
		FANOUT_QUEUE2 = queueProperty.get("fanout_queue2").toString();
		FANOUT_QUEUE3 = queueProperty.get("fanout_queue3").toString();
		FANOUT_EXCHANGE = queueProperty.get("fanout_exchange").toString();
		FANOUT_KEY = queueProperty.get("fanout_key").toString();

		TOPIC_QUEUE = queueProperty.get("topic_queue").toString();
		TOPIC_EXCHANGE = queueProperty.get("topic_exchange").toString();
		TOPIC_KEY = queueProperty.get("topic_key").toString();

		DEAD_QUEUE = queueProperty.get("dead_queue").toString();
		DEAD_EXCHANGE = queueProperty.get("dead_exchange").toString();
		DEAD_KEY = queueProperty.get("dead_key").toString();

		BUSINESS_QUEUE = queueProperty.get("business_queue").toString();
		BUSINESS_EXCHANGE = queueProperty.get("business_exchange").toString();
		BUSINESS_KEY = queueProperty.get("business_key").toString();
	}

}

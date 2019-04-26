package com.owinfo.rabbit.test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.owinfo.rabbit.test.QueueConstant.*;

/**
 * Hello World
 * @author pengjunjie
 */
public class Queue1Test {

	private Channel channel;
	private Connection connection;

	/**
	 * 初始化连接
	 */
	@Before
	public void connection() throws Exception {
		channel = QueueUtil.initChannel();
		connection = channel.getConnection();
	}

	/**
	 * 关闭信道和连接
	 */
	@After
	public void close() throws Exception {
		QueueUtil.close(connection, channel);
	}

	/**
	 * 生产者案例
	 */
	@Test
	public void produce() throws Exception {
		BindingDomain binding = new BindingDomain();
		binding.setChannel(channel);
		binding.setQueueNames(new String[] { ORDER_QUEUE });
		binding.setExchangeName(ORDER_EXCHANGE);
		binding.setBindingKey(ORDER_KEY);
		binding.setExchangeType(BuiltinExchangeType.DIRECT.getType());
		QueueUtil.initBinding(binding);

		int total = 10;
		for (int i = 0; i < total; i++) {
			QueueUtil.publishMessage(channel, ORDER_EXCHANGE, ORDER_KEY,
					"我是消息" + i, MessageProperties.PERSISTENT_TEXT_PLAIN);
		}
		// 可以在客户端页面查看持久化消息
		System.out.println("消息发送成功");
	}

	/**
	 *  消费者案例
	 */

	@Test
	public void consume() throws Exception {
		QueueUtil.consumeMessage(channel, ORDER_QUEUE);
	}

	/**
	 *  测试整个连接流程
	 */
	@Test
	public void isAccessable() {
		System.out.println(channel);
	}

}

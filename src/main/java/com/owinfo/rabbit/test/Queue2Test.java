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
 *  RabbitMQ的四种交换机
 *  direct 通过交换机和路由key像指定队列发送消息
 *  fanout 只需要交换机，发送消息到所有绑定到该交换机的队列，队列不存在，消息直接丢失
 *  headers 首部交换机(性能极差，不常使用，略)
 *  topic 主题交换机 路由键支持统配符形式，以 . 分割。比如 topic.key.*.# （*代表一个字符 #代表0个或者多个字符）
 *  @author pengjunjie
 */
public class Queue2Test {

	private Connection connection;
	private Channel channel;

	@Before
	public void connection() throws Exception {
		channel = QueueUtil.initChannel();
		connection = channel.getConnection();
	}

	@After
	public void close() throws Exception {
		QueueUtil.close(connection, channel);
	}

	/**
	 * direct 直连交换机，指定交换机和路由键，发送到绑定的队列(需要指定路由Key)
	 * @throws Exception
	 */
	@Test
	public void directExchangeTest() throws Exception {
		BindingDomain binding = new BindingDomain();
		binding.setChannel(channel);
		binding.setExchangeType(BuiltinExchangeType.DIRECT.getType());
		binding.setBindingKey(DIRECT_KEY);
		binding.setExchangeName(DIRECT_EXCHANGE);
		binding.setQueueNames(new String[] { DIRECT_QUEUE });
		QueueUtil.initBinding(binding);

		int total = 10;
		for (int i = 0; i < total; i++) {
			QueueUtil.publishMessage(channel, DIRECT_EXCHANGE, DIRECT_KEY,
					"direct类型交换机消息，序号为:" + i, MessageProperties.PERSISTENT_TEXT_PLAIN);
		}
		// 可以在客户端页面查看持久化消息
		System.out.println("消息发送成功");
	}

	/**
	 * topic 主题交换机 路由模式支撑通配符匹配， 并以 . 分割
	 * @throws Exception
	 */
	@Test
	public void topicExchangeTest() throws Exception {
		BindingDomain binding = new BindingDomain();
		binding.setChannel(channel);
		binding.setExchangeType(BuiltinExchangeType.TOPIC.getType());
		binding.setExchangeName(TOPIC_EXCHANGE);
		binding.setQueueNames(new String[] { TOPIC_QUEUE });
		binding.setBindingKey(TOPIC_KEY);
		QueueUtil.initBinding(binding);

		String key1 = "topic.key.1.test1";
		String key2 = "topic.key.2.test2";
		int total = 10;
		for (int i = 0; i < total; i++) {
			QueueUtil.publishMessage(channel, TOPIC_EXCHANGE, key1,
					"topic类型交换机消息key = " + key1 + "，序号为:" + i,
					MessageProperties.PERSISTENT_TEXT_PLAIN);

			QueueUtil.publishMessage(channel, TOPIC_EXCHANGE, key2,
					"topic类型交换机消息key = " + key2 + "，序号为:" + i,
					MessageProperties.PERSISTENT_TEXT_PLAIN);
		}

		// 可以在客户端页面查看持久化消息,这里20条全部成功
		System.out.println("消息发送成功");
	}

	/**
	 * fanout 扇形交换机， 将消息发送到所有绑定的队列(不需要指定路由Key)
	 * @throws Exception
	 */
	@Test
	public void fanoutExchangeTest() throws Exception {
		BindingDomain binding = new BindingDomain();
		binding.setChannel(channel);
		binding.setExchangeType(BuiltinExchangeType.FANOUT.getType());
		binding.setExchangeName(FANOUT_EXCHANGE);
		binding.setQueueNames( new String[] { FANOUT_QUEUE1, FANOUT_QUEUE2, FANOUT_QUEUE3 });
		binding.setBindingKey(FANOUT_KEY);
		QueueUtil.initBinding(binding);

		int total = 10;
		for (int i = 0; i < total; i++) {
			QueueUtil.publishMessage(channel, FANOUT_EXCHANGE , "",
					"fanout类型交换机消息，序号为:" + i, MessageProperties.PERSISTENT_TEXT_PLAIN);
		}
		// 可以在客户端页面查看持久化消息
		System.out.println("消息发送成功");
	}
}

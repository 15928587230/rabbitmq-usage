package com.owinfo.rabbit.test;

import com.rabbitmq.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.owinfo.rabbit.test.QueueConstant.*;

/**
 * 死信队列和消费者
 * @author pengjunjie
 */
public class Queue4Test {

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
	 * 初始化死信队列
	 */
	@Test
	public void initDequeue() throws Exception {

		// 死信队列也是普通的队列
		BindingDomain domain = new BindingDomain();
		domain.setChannel(channel);
		domain.setExchangeType(BuiltinExchangeType.DIRECT.getType());
		domain.setQueueNames(new String[] { DEAD_QUEUE });
		domain.setExchangeName(DEAD_EXCHANGE);
		domain.setBindingKey(DEAD_KEY);
		QueueUtil.initBinding(domain);

		/**
		 *  声明业务队列与死信队列绑定
		 *  死信队列运用：
		 *      消息过期
		 *      超过最大长度
		 *      消费失败reject，requeue=false等等
		 */
		BindingDomain business = new BindingDomain();
		business.setChannel(channel);
		business.setExchangeType(BuiltinExchangeType.DIRECT.getType());
		business.setQueueNames(new String[] { BUSINESS_QUEUE });
		business.setBindingKey(BUSINESS_KEY);
		business.setExchangeName(BUSINESS_EXCHANGE);
		Map<String, Object> queueProps = new HashMap<>(10);
		queueProps.put("x-message-ttl", 10000);
		// 指定死信路由键和死信交换机，达到与死信队列绑定的效果
		queueProps.put("x-dead-letter-exchange", DEAD_EXCHANGE);
		queueProps.put("x-dead-letter-routing-key", DEAD_KEY);
		business.setQueueProps(queueProps);
		QueueUtil.initBinding(business);

		int total = 10;
		for (int i = 0; i < total; i++) {
			QueueUtil.publishMessage(channel, BUSINESS_EXCHANGE,
					BUSINESS_KEY, "延迟消息" + i, MessageProperties.PERSISTENT_TEXT_PLAIN);
		}

	}

	/**
	 *  消费者注意事项
	 */
	@Test
	public void consume() throws Exception {
		// 每次拉取100条进行消费， false(作用范围为单个消费者)
		channel.basicQos(100, false);
		channel.basicConsume(BUSINESS_QUEUE, false, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
			                           AMQP.BasicProperties properties, byte[] body) throws IOException {
				long deliveryTag = envelope.getDeliveryTag();
				try {
					System.out.println(new String(body, "UTF-8"));
					// false 单条消息消费确认
					System.out.println(1/0);
					channel.basicAck(deliveryTag, false);
				} catch (Exception e) {
					// false 单条处理
					channel.basicReject(deliveryTag, false);
				}
			}
		});

		Thread.sleep(2000);
	}

}

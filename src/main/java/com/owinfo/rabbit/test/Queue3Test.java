package com.owinfo.rabbit.test;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息发送和消费相关
 * @author pengjunjie
 */
public class Queue3Test {

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

	@Test
	public void publishMessage() throws Exception {
		BindingDomain bindingDomain = new BindingDomain();
		bindingDomain.setChannel(channel);
		bindingDomain.setExchangeName(QueueConstant.INVENTORY_EXCHANGE);
		bindingDomain.setQueueNames(new String[] { QueueConstant.INVENTORY_QUEUE });
		bindingDomain.setBindingKey(QueueConstant.INVENTORY_KEY);
		bindingDomain.setExchangeType(BuiltinExchangeType.DIRECT.getType());

		/**
		 *  队列属性
		 *  x-message-ttl 消息过期时间(毫秒)
		 *  x-expires  队列的过期时间(毫秒)
		 *  x-max-length  队列的消息最大条数
		 *  x-max-length-bytes 单条消息的最大size
		 *  overflow  drop-head or reject-publish，当队列消息达到最大长度时，消息的接受策略。删除前面的消息或者拒绝发送
		 *  x-dead-letter-exchange 被绑定的死信交换机(死信队列也是普通的队列)
		 *  x-dead-letter-routing-key 被绑定的死信路由键
		 *  x-max-priority 优先级队列(数字), 表示队列支持的最大优先级， 消息的优先级在消息属性中设置
		 */
		Map<String, Object> queueProperties = new HashMap<>(10);
		queueProperties.put("x-message-ttl", 10000);
		queueProperties.put("x-max-priority", 10);
		bindingDomain.setQueueProps(queueProperties);
		QueueUtil.initBinding(bindingDomain);

		// 回调方法
		addReturnListener(channel);
		addConfirmListener(channel);

		/**
		 * content_type ： 消息内容的类型
		 * content_encoding： 消息内容的编码格式
		 * priority： 消息的优先级
		 * correlation_id：关联id
		 * reply_to: 用于指定回复的队列的名称（消费端手动获取该队列，向里面发消息 ）
		 * expiration： 消息的失效时间
		 * deliveryMode： 消息持久化 2持久 1不持久。设置成1，重启队列，消息丢失
		 * message_id： 消息id
		 * timestamp：消息的时间戳
		 * type： 类型
		 * user_id: 用户id（pengjunjie）
		 * app_id： 应用程序id
		 * cluster_id: 集群id
		 */
		AMQBasicProperties properties = new AMQP.BasicProperties();
		AMQP.BasicProperties build = ((AMQP.BasicProperties) properties).builder().contentType("text/plain")
				.messageId(UUID.randomUUID().toString())
				.contentEncoding("UTF-8")
				.priority(8)
				.deliveryMode(2)
				.build();

		/**
		 *  消息发送
		 *  mandatory  消息发送之后是否回应，设置为false无论发送成功或者失败，都不会回应，消息可能丢失.
		 *              设置成true, 告诉服务器调用basic.return把消息返还给生产者
		 *  immediate  是否马上消费，设置成true,将调用消费者进行消费，不会投递消息到队列。如果没有消费者绑定
		 *              调用basic.return返还消息给生产者。这里设置成false(属性不常用)
		 */
		// 管理界面查看10s后消息是否过期消失
		channel.basicPublish(QueueConstant.INVENTORY_EXCHANGE, QueueConstant.INVENTORY_KEY,
				true, false, build , "清单消息，过期时间10s".getBytes("UTF-8"));

		// 执行错误的路由键发送，看是否触发mandatory=true的应答机制
		channel.basicPublish(QueueConstant.INVENTORY_EXCHANGE, "notExistKey",
				true, false, build , "不存在的路由键，消息应答Listener触发".getBytes("UTF-8"));

		Thread.sleep(1000);
	}

	/**
	 *  发送时，通常将消息存储缓存中。发送成功之后， 异步回调，删除成功发送的消息。缓存中剩余的就是发送失败的消息
	 * 	消息是否可达, 找不到队列时调用
	 */
	public void addReturnListener(Channel channel) {
		channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
			System.out.println(replyCode + ": " + replyText);
			System.out.println(properties.getPriority());
			System.out.println(new String(body, "UTF-8"));
		});
	}


	/**
	 * 发送方消息确认模式
	 * @param channel
	 */
	private void addConfirmListener(Channel channel) throws IOException {
		channel.confirmSelect();
		channel.addConfirmListener(new ConfirmListener() {
			// deliveryTag消息序号， multiple是否是批量确认
			@Override
			public void handleAck(long deliveryTag, boolean multiple) {
				//缓存消息删除
				System.out.println("ACK消息序号为：" + deliveryTag + "  是否批量确认：" + multiple);
			}

			@Override
			public void handleNack(long deliveryTag, boolean multiple) {
				System.out.println("NACK消息序号为：" + deliveryTag + "  是否批量确认：" + multiple);
			}
		});
	}

}

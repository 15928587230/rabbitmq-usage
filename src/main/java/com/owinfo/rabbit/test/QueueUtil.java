package com.owinfo.rabbit.test;

import com.rabbitmq.client.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.concurrent.*;

import static com.owinfo.rabbit.test.QueueConstant.*;

/**
 * 工具类
 * @author pengjunjie
 */
public class QueueUtil {

	/**
	 * 自定义线程池
	 * @return
	 */
	public static ExecutorService initExecutorService() {
		return new ThreadPoolExecutor(5,
				20, 10, TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(100),
				r -> new Thread(r, "AMQP-Thread-" + r.hashCode()));

	}

	/**
	 * 初始化连接和信道
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static Channel initChannel() throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setPort(PORT);
		connectionFactory.setHost(HOST);
		connectionFactory.setVirtualHost(VHOST);
		connectionFactory.setUsername(USERNAME);
		connectionFactory.setPassword(PASSWORD);
		connectionFactory.setAutomaticRecoveryEnabled(true);
		connectionFactory.useNio();
		connectionFactory.setSharedExecutor(QueueUtil.initExecutorService());
		Connection connection = connectionFactory.newConnection();
		return connection.createChannel();
	}

	/**
	 *  声明交换机、队列、并绑定
	 * @param bindingDomain
	 */
	public static void initBinding(BindingDomain bindingDomain) throws Exception {
		Channel channel = bindingDomain.getChannel();
		Assert.notNull(channel, "Channel can not be null");

		/**
		 *  声明交换机
		 *  boolean durable 持久化, boolean autoDelete 是否自动删除
		 */
		channel.exchangeDeclare(bindingDomain.getExchangeName(),
				bindingDomain.getExchangeType(), true, false, bindingDomain.getExchangeProps());


		/**
		 *  声明队列
		 *  boolean durable 队列持久化, boolean exclusive 是否为排他队列。表示被一个消费者连上，不允许其他的连接。最好设置成false
		 *  boolean autoDelete 是否自动删除
		 */
		String[] queueNames = bindingDomain.getQueueNames();
		for (String queueName: queueNames) {
			channel.queueDeclare(queueName, true,
					false, false, bindingDomain.getQueueProps());

			// 通过路由键、交换机和队列绑定
			channel.queueBind(queueName, bindingDomain.getExchangeName(), bindingDomain.getBindingKey());

		}
	}

	/**
	 * 发送消息
	 * @param channel
	 * @param exchangeName
	 * @param bindingKey
	 * @param message
	 * @throws Exception
	 */
	public static void publishMessage(Channel channel, String exchangeName,
	                                  String bindingKey, String message, AMQP.BasicProperties properties) throws Exception {
		channel.basicPublish(exchangeName, bindingKey, true, properties, message.getBytes("UTF-8"));
	}

	/**
	 * 消费某个队列的消息
	 * @param channel
	 * @param queueName
	 * @throws Exception
	 */
	public static void consumeMessage(Channel channel, String queueName) throws Exception {
		// 消费者消费消息， 必须保证queue已经存在
		channel.basicQos(100);
		channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
			                           AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("==> 接收到消息，内容为：" + message);
				// false 单条消息确认
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		});

		// 等待消费完毕
		TimeUnit.SECONDS.sleep(5);
	}

	/**
	 * 关闭信道和连接
	 * @param connection
	 * @param channel
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void close(Connection connection, Channel channel) throws IOException, TimeoutException {
		if (channel != null) {
			channel.close();
		}

		if (connection != null) {
			connection.close();
		}
	}
}

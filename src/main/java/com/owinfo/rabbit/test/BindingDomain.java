package com.owinfo.rabbit.test;

import com.rabbitmq.client.Channel;

import java.util.Map;

/**
 * 实体对象
 * @author pengjunjie
 */
public class BindingDomain {

	private Channel channel;
	private String[] queueNames;
	private Map<String, Object> queueProps;
	private String exchangeName;
	private String exchangeType;
	private Map<String, Object> exchangeProps;
	private String bindingKey;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String[] getQueueNames() {
		return queueNames;
	}

	public void setQueueNames(String[] queueNames) {
		this.queueNames = queueNames;
	}

	public Map<String, Object> getQueueProps() {
		return queueProps;
	}

	public void setQueueProps(Map<String, Object> queueProps) {
		this.queueProps = queueProps;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(String exchangeType) {
		this.exchangeType = exchangeType;
	}

	public Map<String, Object> getExchangeProps() {
		return exchangeProps;
	}

	public void setExchangeProps(Map<String, Object> exchangeProps) {
		this.exchangeProps = exchangeProps;
	}

	public String getBindingKey() {
		return bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}
}

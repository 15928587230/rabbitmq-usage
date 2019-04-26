package com.owinfo.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * RabbitMQ
 * @date 2019-04
 * @author pengjunjie
 */
@EnableRabbit
@ComponentScan(basePackages = {"com.owinfo.rabbit.starter"})
@SpringBootApplication
public class OwinfoRabbitApplication {

	public static void main(String[] args) {
		SpringApplication.run(OwinfoRabbitApplication.class, args);
	}

}

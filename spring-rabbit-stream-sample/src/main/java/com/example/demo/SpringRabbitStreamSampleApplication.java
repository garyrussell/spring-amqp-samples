package com.example.demo;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.stereotype.Component;

import com.rabbitmq.stream.Environment;

@SpringBootApplication
public class SpringRabbitStreamSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRabbitStreamSampleApplication.class, args).close();
	}

	@Bean
	public ApplicationRunner runner(RabbitTemplate template, AmqpAdmin admin) {
		return args -> {
			template.convertAndSend("test.stream.queue", "test");
			Thread.sleep(5_000);
		};
	}

	// These 3 beans will eventually be auto-configured by Boot
	@Bean
	Environment env() {
		return Environment.builder().build();
	}

	@Bean
	DisposableBean disposer(Environment env) {
		return () -> env.close();
	}

	@Bean
	RabbitListenerContainerFactory<StreamListenerContainer> rabbitListenerContainerFactory(Environment env) {
		return new StreamRabbitListenerContainerFactory(env);
	}

}

@Component
class Listener {

	@RabbitListener(queues = "test.stream.queue")
	void listen(String in) {
		System.out.println(in);
	}

}

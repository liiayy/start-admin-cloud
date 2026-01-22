package cn.muziseo.common.mq.rabbitmq.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * RabbitMQ 配置类
 *
 * @author 木子软件
 * @Date 2026-01-19
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Configuration
@PropertySource(value = "classpath:common-rabbitmq.yml", factory = YmlPropertySourceFactory.class)
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "boot_exchange";
    public static final String QUEUE_NAME = "boot_queue";
    public static final String ROUTING_KEY = "boot.routing.key";

    /**
     * 声明交换机
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange directExchange() {
        // durable: 是否持久化 (重启后依然存在)
        // autoDelete: 没有队列绑定时是否自动删除
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * 声明队列
     *
     * @return Queue
     */
    @Bean
    public Queue createQueue() {
        // durable: 是否持久化
        // exclusive: 是否独占 (仅当前连接可用)
        // autoDelete: 没有消费者时是否自动删除
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    /**
     * 绑定队列和交换机
     *
     * @param queue    队列
     * @param exchange 交换机
     * @return Binding
     */
    @Bean
    public Binding bindQueueAndExchange(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    /**
     * 配置 JSON 序列化
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
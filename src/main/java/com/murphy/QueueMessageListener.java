package com.murphy;

import com.murphy.message.LoginMessage;
import com.murphy.message.RegistrationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 处理消息的consumer
 * @author Murphy
 */
@Component

public class QueueMessageListener {
    final Logger logger = LoggerFactory.getLogger(getClass());

    static final String QUEUE_MAIL = "q.mail";
    static final String QUEUE_SMS = "q.sms";
    static final String QUEUE_APP = "q.app";

    @RabbitListener(queues = QUEUE_MAIL)
    public void onRegistrationMessageFormMailQueue(RegistrationMessage message) throws Exception {
        logger.info("queue {} received registration message:{}", QUEUE_MAIL, message);
    }

    @RabbitListener(queues = QUEUE_SMS)
    public void onRegistrationMessageFormSmsQueue(RegistrationMessage message) {
        logger.info("queue {} received registration message:{}", QUEUE_SMS, message);
    }

    @RabbitListener(queues = QUEUE_MAIL)
    public void onLoginMessageFromMailQueue(LoginMessage message) throws Exception {
        logger.info("queue {} received message: {}", QUEUE_MAIL, message);
    }

    @RabbitListener(queues = QUEUE_SMS)
    public void onLoginMessageFromSmsQueue(LoginMessage message) throws Exception {
        logger.info("queue {} received message: {}", QUEUE_SMS, message);
    }

    @RabbitListener(queues = QUEUE_APP)
    public void onLoginMessageFromAppQueue(LoginMessage message) throws Exception {
        logger.info("queue {} received message: {}", QUEUE_APP, message);
    }
}

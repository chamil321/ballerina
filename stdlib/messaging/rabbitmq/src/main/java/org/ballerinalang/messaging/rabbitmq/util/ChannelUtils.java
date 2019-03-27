/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.ballerinalang.bre.Context;
import org.ballerinalang.messaging.rabbitmq.RabbitMQConstants;
import org.ballerinalang.messaging.rabbitmq.RabbitMQUtils;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Util class for RabbitMQ Channel handling.
 *
 * @since 0.995.0
 */
public class ChannelUtils {

    /**
     * Creates a RabbitMQ AMQ Channel.
     *
     * @param connection RabbitMQ Connection object.
     * @return RabbitMQ Channel object.
     */
    public static Channel createChannel(Connection connection) {
        try {
            return connection.createChannel();
        } catch (IOException exception) {
            String errorMessage = "An error occurred while creating the channel ";
            throw new BallerinaException(errorMessage + exception.getMessage(), exception);
        }
    }

    /**
     * Closes the channel.
     *
     * @param channel RabbitMQ Channel object.
     * @param context Context.
     */
    public static void close(Channel channel, Context context) {
        try {
            channel.close();
        } catch (TimeoutException | IOException exception) {
            RabbitMQUtils.returnError(RabbitMQConstants.CLOSE_CHANNEL_ERROR
                    + " " + exception.getMessage(), context, exception);
        }
    }

    /**
     * Declares a queue with an auto-generated queue name.
     *
     * @param channel RabbitMQ Channel object.
     * @return An auto-generated queue name.
     */
    public static String queueDeclare(Channel channel) {
        try {
            return channel.queueDeclare().getQueue();
        } catch (IOException exception) {
            String errorMessage = "An error occurred while auto-declaring the queue ";
            throw new BallerinaException(errorMessage + exception.getMessage(), exception);
        }
    }

    /**
     * Declared a queue.
     *
     * @param channel     RabbitMQ Channel object.
     * @param queueConfig Parameters related to declaring a queue.
     */
    public static void queueDeclare(Channel channel, BMap<String, BValue> queueConfig) {
        String queueName = RabbitMQUtils.getStringFromBValue(queueConfig, RabbitMQConstants.ALIAS_QUEUE_NAME);
        boolean durable = RabbitMQUtils.getBooleanFromBValue(queueConfig, RabbitMQConstants.ALIAS_QUEUE_DURABLE);
        boolean exclusive = RabbitMQUtils.getBooleanFromBValue(queueConfig, RabbitMQConstants.ALIAS_QUEUE_EXCLUSIVE);
        boolean autoDelete = RabbitMQUtils.getBooleanFromBValue(queueConfig, RabbitMQConstants.ALIAS_QUEUE_AUTODELETE);
        try {
            channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
        } catch (IOException e) {
            String errorMessage = "An error occurred while declaring the queue ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    /**
     * Declares an exchange.
     *
     * @param channel        RabbitMQ Channel object.
     * @param exchangeConfig Parameters related to declaring an exchange.
     */
    public static void exchangeDeclare(Channel channel, BMap<String, BValue> exchangeConfig) {
        String exchangeName = RabbitMQUtils.getStringFromBValue(exchangeConfig, RabbitMQConstants.ALIAS_EXCHANGE_NAME);
        String exchangeType = RabbitMQUtils.getStringFromBValue(exchangeConfig, RabbitMQConstants.ALIAS_EXCHANGE_TYPE);
        boolean durable = RabbitMQUtils.getBooleanFromBValue(exchangeConfig, RabbitMQConstants.ALIAS_EXCHANGE_DURABLE);
        try {
            channel.exchangeDeclare(exchangeName, exchangeType, durable);
        } catch (IOException e) {
            String errorMessage = "An error occurred while declaring the exchange ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    /**
     * Binds a queue to an exchange.
     *
     * @param channel      RabbitMQ Channel object.
     * @param queueName    Name of the queue.
     * @param exchangeName Name of the exchange.
     * @param bindingKey   The binding key used for the binding.
     */
    public static void queueBind(Channel channel, String queueName, String exchangeName, String bindingKey) {
        try {
            channel.queueBind(queueName, exchangeName, bindingKey);
        } catch (Exception e) {
            String errorMessage = "An error occurred while binding the queue to an exchange ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    /**
     * Publishes messages to an exchange.
     *
     * @param channel    RabbitMQ Channel object.
     * @param routingKey The routing key of the queue.
     * @param message    The message body.
     * @param exchange   The name of the exchange.
     */
    public static void basicPublish(Channel channel, String routingKey, String message, String exchange) {
        try {
            channel.basicPublish(exchange, routingKey, null, message.getBytes("UTF-8"));
        } catch (Exception e) {
            String errorMessage = "An error occurred while publishing the message to a queue ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }

    }

    /**
     * Deletes a queue.
     *
     * @param channel   RabbitMQ Channel object.
     * @param queueName Name of the queue.
     */
    public static void queueDelete(Channel channel, String queueName) {
        try {
            channel.queueDelete(queueName);
        } catch (Exception e) {
            String errorMessage = "An error occurred while deleting the queue ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    /**
     * Deletes an exchange.
     *
     * @param channel      RabbitMQ Channel object.
     * @param exchangeName Name of the exchange.
     */
    public static void exchangeDelete(Channel channel, String exchangeName) {
        try {
            channel.exchangeDelete(exchangeName);
        } catch (Exception e) {
            String errorMessage = "An error occurred while deleting the exchange ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    /**
     * Purges a queue.
     *
     * @param channel   RabbitMQ Channel object.
     * @param queueName Name of the queue.
     */
    public static void queuePurge(Channel channel, String queueName) {
        try {
            channel.queuePurge(queueName);
        } catch (Exception e) {
            String errorMessage = "An error occurred while purging the queue ";
            throw new BallerinaException(errorMessage + e.getMessage(), e);
        }
    }

    private ChannelUtils() {
    }
}

package ru.tishin.rabbitMq.itBlog;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Blog {

    private static final String EXCHANGE_NAME = "it-blog";


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
             Scanner scanner = new Scanner(System.in)) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            while (scanner.hasNext()) {
                String message = scanner.nextLine();
                String topic = getTopic(message);
                String article = message.substring(topic.length() + 1);
                channel.basicPublish(EXCHANGE_NAME, topic, null, article.getBytes());
            }
        }
    }

    private static String getTopic(String message) {
        String[] arr = message.split(" ");
        return arr[0];
    }

}

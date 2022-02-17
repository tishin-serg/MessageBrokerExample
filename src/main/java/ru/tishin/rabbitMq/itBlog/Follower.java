package ru.tishin.rabbitMq.itBlog;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Follower {
    private static final String EXCHANGE_NAME = "it-blog";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = getChannel();
        String queue = channel.queueDeclare().getQueue();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Type set_topic \"name\" for subscribe: \n" +
                "php \n" +
                "java \n" +
                "c++ \n" +
                "python. \n");

        System.out.println("Type delete_topic \"name\" for unsubscribe.");

        while (scanner.hasNext()) {
            String answer = scanner.nextLine();
            String command = getCommand(answer);
            answer = answer.substring(command.length() + 1);
            switch (command) {
                case "set_topic":
                    channel.queueBind(queue, EXCHANGE_NAME, answer);
                    System.out.println(" [x] Subscribed '" + answer + "'");
                    break;
                case "delete_topic":
                    channel.queueUnbind(queue, EXCHANGE_NAME, answer);
                    System.out.println(" [x] Unsubscribed '" + answer + "'");
                    break;
            }
            receiveMessage(channel, queue);
        }

        scanner.close();
    }

    private static void receiveMessage(Channel channel, String queue) throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received from topic " + delivery.getEnvelope().getRoutingKey() + ": " + message);
        };

        channel.basicConsume(queue, false, deliverCallback, consumerTag -> {
        });
    }

    private static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        return channel;
    }

    private static String getCommand(String message) {
        String[] arr = message.split(" ");
        return arr[0];
    }
}

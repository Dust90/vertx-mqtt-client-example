package io.mqtt;

import io.vertx.core.AbstractVerticle;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.io.UnsupportedEncodingException;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    MqttClientOptions options = new MqttClientOptions();
      // specify broker host
      options.setHost("iot.eclipse.org");
      // specify max size of message in bytes
      options.setMaxMessageSize(100_000_000);

    MqttClient client = MqttClient.create(vertx, options);

    client.publishHandler(s -> {
      try {
        String message = new String(s.payload().getBytes(), "UTF-8");
        System.out.println(String.format("Receive message with content: \"%s\" from topic \"%s\"", message, s.topicName()));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    });

    client.connect(s -> {
      // subscribe to all subtopics
      client.subscribe("#", 0);
    });
  }
}

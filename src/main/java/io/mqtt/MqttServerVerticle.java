package io.mqtt;

import io.vertx.core.AbstractVerticle;
import io.vertx.mqtt.MqttServer;

public class MqttServerVerticle  extends AbstractVerticle{
  @Override
  public void start() throws Exception {

    MqttServer mqttServer = MqttServer.create(vertx);
    mqttServer.endpointHandler(endpoint -> {

      // shows main connect info
      System.out.println("[MQTT SERVER] MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

      if (endpoint.auth() != null) {
        System.out.println("[MQTT SERVER] [username = " + endpoint.auth().userName() + ", password = " + endpoint.auth().password() + "]");
      }
      if (endpoint.will() != null) {
        System.out.println("[MQTT SERVER] [will topic = " + endpoint.will().willTopic() + " msg = " + endpoint.will().willMessage() +
          " QoS = " + endpoint.will().willQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
      }

      System.out.println("[MQTT SERVER] [keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

      // accept connection from the remote client
      endpoint.accept(false);

    })
      .listen(ar -> {

        if (ar.succeeded()) {

          System.out.println("[MQTT SERVER] MQTT server is listening on port " + ar.result().actualPort());
        } else {

          System.out.println("[MQTT SERVER] Error on starting the server");
          ar.cause().printStackTrace();
        }
      });
  }
}

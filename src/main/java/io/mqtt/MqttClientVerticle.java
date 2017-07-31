package io.mqtt;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.core.net.NetClient;
import io.vertx.mqtt.MqttClientOptions;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

public class MqttClientVerticle extends AbstractVerticle {

  private static final String PROTOCOL_NAME = "MQTT";
  private static final int PROTOCOL_VERSION = 4;

  @Override
  public void start() throws Exception {
    NetClient netClient = vertx.createNetClient();

    MqttClientOptions options = new MqttClientOptions();

    options.setKeepAliveTimeSeconds(100);

    netClient.connect(1883, "0.0.0.0", done -> {

      NetSocketInternal soi = (NetSocketInternal) done.result();
      ChannelPipeline pipeline = soi.channelHandlerContext().pipeline();
      pipeline.addBefore("handler", "mqttEncoder", MqttEncoder.INSTANCE);


      MqttMessage connect = createConnect(options);
      soi.writeMessage(connect);
      System.out.println("[MQTT CLIENT] first CONNECT sent");


      vertx.setTimer(1000, h -> {
        MqttMessage con = createConnect(options);
        soi.writeMessage(con);
        System.out.println("[MQTT CLIENT] second CONNECT sent");
      });

      vertx.setTimer(2000, h -> {
        MqttMessage con = createConnect(options);
        soi.writeMessage(con);
        System.out.println("[MQTT CLIENT] third CONNECT sent");
      });

    });
  }

  private MqttMessage createConnect(MqttClientOptions options) {
    MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT,
      false,
      AT_MOST_ONCE,
      false,
      0);

    MqttConnectVariableHeader variableHeader = new MqttConnectVariableHeader(
      PROTOCOL_NAME,
      PROTOCOL_VERSION,
      options.hasUsername(),
      options.hasPassword(),
      options.isWillRetain(),
      options.getWillQoS(),
      options.isWillFlag(),
      options.isCleanSession(),
      options.getKeepAliveTimeSeconds()
    );

    MqttConnectPayload payload = new MqttConnectPayload(
      options.getClientId() == null ? "" : options.getClientId(),
      options.getWillTopic(),
      options.getWillMessage(),
      options.hasUsername() ? options.getUsername() : null,
      options.hasPassword() ? options.getPassword() : null
    );

    return MqttMessageFactory.newMessage(fixedHeader, variableHeader, payload);
  }
}
